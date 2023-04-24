package com.herosoft.order.services.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.herosoft.commons.exceptions.DefinitionException;
import com.herosoft.commons.results.Result;
import com.herosoft.order.clients.ProductService;
import com.herosoft.order.clients.UserService;
import com.herosoft.order.dto.OrderDetailDto;
import com.herosoft.order.dto.OrderInfoDto;
import com.herosoft.order.dto.OrderRequestDto;
import com.herosoft.order.enums.OrderStatus;
import com.herosoft.order.po.OrderDetailPo;
import com.herosoft.order.po.OrderHeaderPo;
import com.herosoft.order.services.OrderService;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
    @Qualifier("taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

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

        String username = jsonObject.getString("userName");
        double balance = jsonObject.getDouble("balance");

        if(balance < orderAmount){
            throw new DefinitionException(407,username+"的账户余额不足，当前余额："+balance+ " 订单总额："+orderRequestDto.getOrderAmount());
        }
        //生成订单主表信息
        if(!orderHeaderService.save(orderHeader)) {
            throw new DefinitionException(507,"创建订单主表出错");
        }

        int orderHeaderId = orderHeader.getOrderHeaderId();

        //生成订单明细表信息
        List<OrderDetailPo> orderDetailList =  orderRequestDto.getOrderproducts().stream().map(orderProduct ->{

            OrderDetailPo orderDetail = new OrderDetailPo();
            int orderProductId = orderProduct.getProductId();
            int orderProductQty = orderProduct.getProductQty();
            int currentProductAmount;
            double productPrice;
            String productName;

            RMap<Integer, Map<String,Object>> productRedis = redissonClient.getMap("Product_Amount_Check_For_Order_Create");

            if(productRedis.containsKey(orderProductId)){
                log.info("线程{}用Redis库存校验订购数量。。。。",Thread.currentThread().getName());
                //如果Redis有值，就用Redis中的值校验订购数量
                currentProductAmount= (int) productRedis.get(orderProductId).get("productAmount");
                productPrice= (double) productRedis.get(orderProductId).get("productPrice");
                productName= (String) productRedis.get(orderProductId).get("productName");

            }
            else {
                log.info("线程{}调用产品服务获取库存校验订购数量。。。。",Thread.currentThread().getName());
                //如果Redis没有，就调用产品服务获取当前库存校验订购数量
                String jsonStringProduct = JSON.toJSONString(productService.findById(orderProductId).getData());
                JSONObject jsonObjectProduct = JSON.parseObject(jsonStringProduct);

                currentProductAmount = jsonObjectProduct.getInteger("productAmount");
                productPrice = jsonObjectProduct.getDouble("productPrice");
                productName=jsonObjectProduct.getString("productName");
            }

            if(currentProductAmount < orderProductQty){
                throw new DefinitionException(509,"订购产品["+productName+"]库存不足,当前余额:"+currentProductAmount+",订购数量:"+orderProductQty);
            }
            //更新Redis，反映扣除订购数量后的库存
            HashMap<String,Object> map = new HashMap<>();

            map.put("productAmount",currentProductAmount-orderProductQty);
            map.put("productPrice",productPrice);
            map.put("productName",productName);

            productRedis.put(orderProductId,map);
            productRedis.expire(Duration.ofMillis(5000));

            //订购数量校验通过，组装订单明细实体类
            orderDetail.setOrderHeaderId(orderHeaderId);
            orderDetail.setProductId(orderProductId);
            orderDetail.setProductQty(orderProductQty);
            orderDetail.setProductPrice(productPrice);
            orderDetail.setCreateBy(createBy);
            orderDetail.setUpdateBy(updateBy);

            return orderDetail;
        }).collect(Collectors.toList());

        if(!orderDetailService.saveBatch(orderDetailList)){
            throw new DefinitionException(508,"创建订单明细表出错");
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
                .sheet("订单模板")
                .doWrite(getOrderInfo());
    }
    @Override
    public void generateOrderByExcel(String fileName) {

        EasyExcel.write(fileName+".xlsx", OrderInfoDto.class)
                .sheet("订单模板")
                .doWrite(getOrderInfo());
    }

    @Override
    @Transactional
    public void cancelOrderWithoutStockChange(int userId, int orderHeaderId) {
        OrderHeaderPo cancelOrder = orderHeaderService.getById(orderHeaderId);

        cancelOrder.setStatus(OrderStatus.CANCEL.getOrderStatus());
        cancelOrder.setUpdateBy(userId);

        orderHeaderService.updateById(cancelOrder);
    }

    @Override
    @GlobalTransactional
    public void cancelOrderWithStockChange(int userId, int orderHeaderId) throws RuntimeException {
        OrderHeaderPo orderHeader = orderHeaderService.getById(orderHeaderId);

        orderHeader.setStatus(OrderStatus.CANCEL.getOrderStatus());
        orderHeader.setUpdateBy(userId);
        BeanCopier beanCopier = new BeanCopier() {
            @Override
            public void copy(Object o, Object o1, Converter converter) {

            }
        };

        log.info("线程池核心线程数：{}",taskExecutor.getCorePoolSize());

        //本地更新
        log.info("开始执行本地更新订单状态事务。。。");
        orderHeaderService.updateById(orderHeader);

        List<OrderDetailPo> orderDetail = orderDetailService.list(Wrappers.lambdaQuery(OrderDetailPo.class)
                .eq(OrderDetailPo::getOrderHeaderId,orderHeaderId));

        List<OrderDetailDto> orderDetailDtos = BeanUtil.copyToList(orderDetail,OrderDetailDto.class);
        //远程库存服务
        log.info("开始调用远程库存服务进行增加库存事务。。。全局事务XID:{}", RootContext.getXID());
        Result result = productService.deduceStock(orderDetailDtos);

        log.info("结束调用远程库存服务进行增加库存事务。。。");

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
