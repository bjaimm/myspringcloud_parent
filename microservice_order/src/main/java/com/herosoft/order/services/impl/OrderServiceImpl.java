package com.herosoft.order.services.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.herosoft.commons.exceptions.DefinitionException;
import com.herosoft.order.clients.ProductService;
import com.herosoft.order.clients.UserService;
import com.herosoft.order.dto.OrderInfoDto;
import com.herosoft.order.dto.OrderRequestDto;
import com.herosoft.order.enums.OrderStatus;
import com.herosoft.order.po.OrderDetailPo;
import com.herosoft.order.po.OrderHeaderPo;
import com.herosoft.order.services.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderHeaderServiceImpl orderHeaderService;

    @Autowired
    private OrderDetailServiceImpl orderDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    @Transactional
    public OrderInfoDto createOrder(OrderRequestDto orderRequestDto) {

        OrderHeaderPo orderHeader = new OrderHeaderPo();

        int userId = orderRequestDto.getUserId();
        int createBy = orderRequestDto.getCreateBy();
        int updateBy = orderRequestDto.getUpdateBy();
        double orderAmount = orderRequestDto.getOrderAmount();

        orderHeader.setUserId(userId);
        orderHeader.setOrderAmount(orderAmount);
        orderHeader.setCreateBy(createBy);
        orderHeader.setUpdateBy(updateBy);
        orderHeader.setStatus(OrderStatus.OPEN.getOrderStatus());

        String jsonString = JSON.toJSONString(userService.findById(userId).getData());
        JSONObject jsonObject = JSON.parseObject(jsonString);

        String username = jsonObject.getString("username");
        double balance = jsonObject.getDouble("balance");

        if(balance < orderAmount){
            throw new DefinitionException(407,username+"???????????????????????????????????????"+balance+ " ???????????????"+orderRequestDto.getOrderAmount());
        }
        //????????????????????????
        if(!orderHeaderService.save(orderHeader)) {
            throw new DefinitionException(507,"????????????????????????");
        }

        int orderHeaderId = orderHeader.getOrderHeaderId();

        //???????????????????????????
        List<OrderDetailPo> orderDetailList =  orderRequestDto.getOrderproducts().stream().map(orderProduct ->{

            OrderDetailPo orderDetail = new OrderDetailPo();
            int orderProductId = orderProduct.getProductId();
            int orderProductQty = orderProduct.getProductQty();
            int currentProductAmount;
            double productPrice;
            String productName;

            RMap<Integer, Map<String,Object>> productRedis = redissonClient.getMap("Product_Amount_Check_For_Order_Create");

            if(productRedis.containsKey(orderProductId)){
                log.info("??????{}???Redis????????????????????????????????????",Thread.currentThread().getName());
                //??????Redis???????????????Redis???????????????????????????
                currentProductAmount= (int) productRedis.get(orderProductId).get("productAmount");
                productPrice= (double) productRedis.get(orderProductId).get("productPrice");
                productName= (String) productRedis.get(orderProductId).get("productName");

            }
            else {
                log.info("??????{}????????????????????????????????????????????????????????????",Thread.currentThread().getName());
                //??????Redis??????????????????????????????????????????????????????????????????
                String jsonStringProduct = JSON.toJSONString(productService.findById(orderProductId).getData());
                JSONObject jsonObjectProduct = JSON.parseObject(jsonStringProduct);

                currentProductAmount = jsonObjectProduct.getInteger("productAmount");
                productPrice = jsonObjectProduct.getDouble("productPrice");
                productName=jsonObjectProduct.getString("productName");
            }

            if(currentProductAmount < orderProductQty){
                throw new DefinitionException(509,"????????????["+productName+"]????????????,????????????:"+currentProductAmount+",????????????:"+orderProductQty);
            }
            //??????Redis???????????????????????????????????????
            HashMap<String,Object> map = new HashMap<>();

            map.put("productAmount",currentProductAmount-orderProductQty);
            map.put("productPrice",productPrice);
            map.put("productName",productName);

            productRedis.put(orderProductId,map);
            productRedis.expire(Duration.ofMillis(5000));

            //??????????????????????????????????????????????????????
            orderDetail.setOrderHeaderId(orderHeaderId);
            orderDetail.setProductId(orderProductId);
            orderDetail.setProductQty(orderProductQty);
            orderDetail.setProductPrice(productPrice);
            orderDetail.setCreateBy(createBy);
            orderDetail.setUpdateBy(updateBy);

            return orderDetail;
        }).collect(Collectors.toList());

        if(!orderDetailService.saveBatch(orderDetailList)){
            throw new DefinitionException(508,"???????????????????????????");
        }

        OrderInfoDto orderInfoDto = Optional.ofNullable(orderHeaderService.findOrderInfoByOrderHeaderId(orderHeaderId))
                .orElse(new OrderInfoDto());

        return orderInfoDto;
    }

    @Override
    public void downloadOrderByExcel(String fileName) throws IOException {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("content-disposition","attachment;filename="+fileName+".xlsx");

        EasyExcel.write(response.getOutputStream(), OrderInfoDto.class)
                //.registerConverter()
                .sheet("????????????")
                .doWrite(getOrderInfo());
    }
    @Override
    public void generateOrderByExcel(String fileName) {

        EasyExcel.write(fileName+".xlsx", OrderInfoDto.class)
                .sheet("????????????")
                .doWrite(getOrderInfo());
    }

    private List<OrderInfoDto> getOrderInfo() {
        List<OrderHeaderPo> orderHeaderPoList = orderHeaderService.list();

        List<OrderInfoDto> orderInfoDtoList = orderHeaderPoList.stream().map(orderHeaderPo->{
            OrderInfoDto orderInfoDto = new OrderInfoDto();

            BeanUtils.copyProperties(orderHeaderPo,orderInfoDto);
            return orderInfoDto;
        }).collect(Collectors.toList());

        return orderInfoDtoList;
    }
}
