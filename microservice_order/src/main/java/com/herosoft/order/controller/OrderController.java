package com.herosoft.order.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.herosoft.commons.dto.message.OrderCreateMessageDto;
import com.herosoft.commons.dto.message.OrderPayMessageDto;
import com.herosoft.commons.exceptions.DefinitionException;
import com.herosoft.commons.results.Result;
import com.herosoft.order.clients.ProductService;
import com.herosoft.order.clients.UserService;
import com.herosoft.order.config.RabbitMqConfig;
import com.herosoft.order.constants.OrderConstants;
import com.herosoft.order.dto.OrderDetailDto;
import com.herosoft.order.dto.OrderInfoDto;
import com.herosoft.order.dto.OrderQueryDto;
import com.herosoft.order.dto.OrderRequestDto;
import com.herosoft.order.enums.OrderStatus;
import com.herosoft.order.po.OrderDetailPo;
import com.herosoft.order.po.OrderHeaderPo;
import com.herosoft.order.services.impl.OrderDetailServiceImpl;
import com.herosoft.order.services.impl.OrderHeaderServiceImpl;
import com.herosoft.order.services.impl.OrderServiceImpl;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
@Api(value = "订单管理接口")
@Slf4j
public class OrderController {
    @Autowired
    private OrderHeaderServiceImpl orderHeaderService;
    @Autowired
    private OrderDetailServiceImpl orderDetailService;

    @Autowired
    private OrderServiceImpl orderServiceImpl;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @RequestMapping(method = RequestMethod.GET,value = "/check1/{orderHeaderId}")
    public OrderInfoDto checkOrder1(@PathVariable int orderHeaderId){

        /////////////////////联表查询方式一
        LambdaQueryWrapper<OrderHeaderPo> wrapper = Wrappers.lambdaQuery(OrderHeaderPo.class)
                .eq(OrderHeaderPo::getOrderHeaderId,orderHeaderId);
        //先查主表信息
        OrderHeaderPo orderHeaderPo=orderHeaderService.getBaseMapper().selectOne(wrapper);

        OrderInfoDto orderInfoDTO = Optional.ofNullable(orderHeaderPo).map(OrderInfoDto::new).orElse(null);

        //再查关联表信息
        Optional.ofNullable(orderInfoDTO).ifPresent(orderInfoDto -> {
            LambdaQueryWrapper<OrderDetailPo> wrapperOrderDetail=Wrappers.lambdaQuery(OrderDetailPo.class)
                    .eq(OrderDetailPo::getOrderHeaderId,orderInfoDto.getOrderHeaderId());
            List<OrderDetailPo> orderDetailList = orderDetailService.getBaseMapper().selectList(wrapperOrderDetail);

            orderInfoDto.setOrderDetailList(
            orderDetailList.stream().map( orderDetailList1->{
                OrderDetailDto orderDetailDto = new OrderDetailDto();

                orderDetailDto.setOrderDetailId(orderDetailList1.getOrderDetailId());
                orderDetailDto.setProductId(orderDetailList1.getProductId());
                orderDetailDto.setProductQty(orderDetailList1.getProductQty());

                return orderDetailDto;
            }).collect(Collectors.toList()));

        });

        return orderInfoDTO;

    }
    @RequestMapping(method = RequestMethod.GET,value = "/check2/{orderHeaderId}")
    public  OrderInfoDto checkOrder2(@PathVariable int orderHeaderId){
        /////////////////////////联表查询方式二分割线
        OrderInfoDto orderInfoDto = new OrderInfoDto();
        QueryWrapper<OrderHeaderPo> orderHeaderWrapper =new QueryWrapper<>();
        orderHeaderWrapper.eq("order_header_id",orderHeaderId);

        OrderHeaderPo orderHeaderPo = orderHeaderService.getBaseMapper().selectOne(orderHeaderWrapper);

        orderInfoDto.setOrderHeaderId(orderHeaderId);
        orderInfoDto.setOrderStatus(orderHeaderPo.getStatus());
        orderInfoDto.setOrderStatusMessage(OrderStatus.values()[orderHeaderPo.getStatus()].getOrderStatusMessage());
        orderInfoDto.setOrderAmount(orderHeaderPo.getOrderAmount());

        QueryWrapper<OrderDetailPo> orderDetailWrapper = new QueryWrapper<>();

        orderDetailWrapper.eq("order_header_id",orderHeaderId);

        List<OrderDetailPo> orderDetailList = orderDetailService.list(orderDetailWrapper);

        List<OrderDetailDto> orderDetailDtoList = orderDetailList.stream().map(list -> {
            OrderDetailDto orderDetailDto = new OrderDetailDto();

            orderDetailDto.setOrderDetailId(list.getOrderDetailId());
            orderDetailDto.setProductId(list.getProductId());
            orderDetailDto.setProductQty(list.getProductQty());

            return orderDetailDto;
        }).collect(Collectors.toList());

        orderInfoDto.setOrderDetailList(orderDetailDtoList);

        return orderInfoDto;
    }

    @RequestMapping(method = RequestMethod.GET,value = "/check3/{orderHeaderId}")
    public OrderInfoDto checkOrder3(@PathVariable int orderHeaderId){
        return orderHeaderService.findOrderInfoByOrderHeaderId(orderHeaderId);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/listOrderHeaderPage/{pageNum}/{pageSize}")
    public Page<OrderHeaderPo> listOrderHeaderPage(@PathVariable long pageNum,
                                                   @PathVariable long pageSize){
        QueryWrapper<OrderHeaderPo> queryWrapper = new QueryWrapper<>();
        //queryWrapper.eq("order_amount",10000.00);

        Page<OrderHeaderPo> page = new Page<>(pageNum, pageSize);
        return orderHeaderService.listPage(page,queryWrapper);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/listOrderInfoPage1/{pageNum}/{pageSize}")
    public IPage<OrderInfoDto> listOrderInfoPage1(@PathVariable long pageNum,
                                                 @PathVariable long pageSize){

        Page<OrderInfoDto> page = new Page<>(pageNum, pageSize);
        return orderHeaderService.listPageOrderInfo(page);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/listOrderInfoPage2/{pageNum}/{pageSize}")
    public IPage<OrderInfoDto> listOrderInfoPage2(@PathVariable long pageNum,
                                                  @PathVariable long pageSize, OrderQueryDto queryDto){

        log.info("queryDto:{}",queryDto);
        //生成主表的分页对象
        Page<OrderHeaderPo> orderHeaderPage = orderHeaderService.page(new Page<>(pageNum, pageSize),
                Wrappers.lambdaQuery(OrderHeaderPo.class)
                        .eq(queryDto.getOrderStatus()!=null,OrderHeaderPo::getStatus,queryDto.getOrderStatus())
                        .orderByDesc(OrderHeaderPo::getCreateTime));

        //转换为DTO的分页对象
        IPage<OrderInfoDto> orderInfoPage = orderHeaderPage.convert(source -> {
            OrderInfoDto dest = new OrderInfoDto();

            BeanUtils.copyProperties(source, dest);
            dest.setOrderStatus(source.getStatus());//因为两个Dto的字段名不一样，所以这里需要再单独复制一下
            dest.setOrderStatusMessage(OrderStatus.values()[source.getStatus()].getOrderStatusMessage());

            String jsonString = JSON.toJSONString(userService.findById(source.getUserId()).getData());
            JSONObject jsonObject = JSON.parseObject(jsonString);

            String username = jsonObject.getString("userName");

            jsonString = JSON.toJSONString(userService.findById(source.getCreateBy()).getData());
            String createUserName = jsonObject.getString("userName");
            jsonString = JSON.toJSONString(userService.findById(source.getUpdateBy()).getData());
            String updateUserName = jsonObject.getString("userName");

            dest.setOrderUserId(source.getUserId());
            dest.setOrderUserName(username);
            dest.setCreateUserName(createUserName);
            dest.setUpdateUserName(updateUserName);
            dest.setCreateUserId(source.getCreateBy());
            dest.setCreateDateTime(source.getCreateTime());
            dest.setUpdateUserId(source.getUpdateBy());
            dest.setUpdateDateTime(source.getUpdateTime());
            return dest;
        });

        Set<Integer> orderHeaderIds = orderInfoPage.getRecords().stream()
                .map(orderInfo -> orderInfo.getOrderHeaderId())
                .collect(Collectors.toSet());
        if(orderHeaderIds.size() == 0){
            return null;
        }
        //生成主表相关的明细List
        List<OrderDetailPo> orderDetailList = orderDetailService.getBaseMapper().selectList(Wrappers.lambdaQuery(OrderDetailPo.class)
                .in(OrderDetailPo::getOrderHeaderId, orderHeaderIds));

        /* List转换为List Map方法二
        Map<Integer, List<OrderDetailPo>> listMap = orderDetailList.stream()
                .collect(Collectors.groupingBy(OrderDetailPo::getOrderHeaderId, Collectors.toList()));
        */
        Map<Integer, List<OrderDetailPo>> orderDetailPoMap = orderDetailList.stream()
                .collect(Collectors.toMap(key -> key.getOrderHeaderId(),
                        value -> Lists.newArrayList(value),
                        (newValue,oldValue)->{
                            oldValue.addAll(newValue);
                            return oldValue;
                        }));
        //DTO的分页对象中注入明细List
        orderInfoPage.getRecords().forEach(orderInfoDto -> {
            orderInfoDto.setOrderDetailList(orderDetailPoMap.get(orderInfoDto.getOrderHeaderId()).stream()
                    .map(source->{
                        OrderDetailDto dest = new OrderDetailDto();
                        BeanUtils.copyProperties(source,dest);

                        String jsonStringProduct = JSON.toJSONString(productService.findById(source.getProductId()).getData());
                        JSONObject jsonObjectProduct = JSON.parseObject(jsonStringProduct);

                        dest.setProductName(jsonObjectProduct.getString("productName"));
                        dest.setProductPrice(jsonObjectProduct.getDouble("productPrice"));

                        return dest;
                    }).collect(Collectors.toList()));
        });

        return orderInfoPage;
    }

    @PostMapping
    public Result createOrder(@RequestBody @Valid OrderRequestDto orderRequestDto){

        int userId = orderRequestDto.getUserId();
        int createBy = orderRequestDto.getCreateBy();
        int updateBy = orderRequestDto.getUpdateBy();
        double orderAmount = orderRequestDto.getOrderAmount();
        boolean tryLock;
        //更新创建订单服务调用计数器
        OrderConstants.ORDER_CREATE_REQUESTS_COUNT=OrderConstants.ORDER_CREATE_REQUESTS_COUNT+1;
        Result userResult = userService.findById(userId);

        if(!userResult.getSuccess()){
            return userResult;
        }
        String jsonString = JSON.toJSONString(userResult.getData());
        JSONObject jsonObject = JSON.parseObject(jsonString);

        String username = jsonObject.getString("userName");

        RLock rLock = redissonClient.getLock("CreateOrderLock");

        if(Objects.isNull(rLock)){
            return Result.defineError(new DefinitionException(511,"没有获取到创建订单锁对象"));
        }
        try {
            //第一个参数表示如果没有获取到锁，线程会加锁等待时间，再重试
            //第二个参数表示加锁成功后，锁的过期时间
            tryLock=rLock.tryLock(50,1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return Result.defineError(new DefinitionException(512,"获取创建订单锁异常"+e.getLocalizedMessage()));
        }
        if(!tryLock){
            return Result.defineError(new DefinitionException(513,"没有获取到创建订单锁"));
        }
        try {

            int orderHeaderId = orderServiceImpl.createOrder(orderRequestDto).getOrderHeaderId();

            if (orderHeaderId ==0){
                return Result.defineError(new DefinitionException(510,"订单数据创建失败"));
            }

            //发送生成订单的消息到RabbitMQ, 产品服务消费者会根据消息分别进行库存扣减
            OrderCreateMessageDto message = new OrderCreateMessageDto();

            message.setUserId(userId);
            message.setOrderHeaderId(orderHeaderId);
            message.setOrderAmount(orderAmount);
            message.setProductList(orderRequestDto.getOrderproducts());

            rabbitTemplate.convertAndSend(RabbitMqConfig.ORDER_EXCHANGE,
                    RabbitMqConfig.ORDER_CREATE_ROUTEKEY,
                    message, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    return message;
                }
            },new CorrelationData(String.valueOf(orderHeaderId)));
        } catch (DefinitionException e) {
            throw e;
        }
        finally {
            if(rLock.isLocked()){
                rLock.unlock();
            }
        }
        return Result.success(username+"添加订单成功");
    }

    @RequestMapping(method = RequestMethod.GET,value = "/pay/{userId}/{orderHeaderId}")
    public Result payOrder(@PathVariable int userId, @PathVariable int orderHeaderId){
        OrderHeaderPo orderHeaderPo = Optional.ofNullable(orderHeaderService.getById(orderHeaderId)).orElse(new OrderHeaderPo());

        double orderAmount=orderHeaderPo.getOrderAmount();

        //发送支付订单的消息到RabbitMQ, 账户服务消费者会根据消息进行余额扣减
        OrderPayMessageDto message = new OrderPayMessageDto();

        message.setUserId(userId);
        message.setOrderHeaderId(orderHeaderId);
        message.setOrderAmount(orderAmount);

        rabbitTemplate.convertAndSend(RabbitMqConfig.ORDER_EXCHANGE,
                RabbitMqConfig.ORDER_PAY_ROUTEKEY,
                message, new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        return message;
                    }
                },new CorrelationData(String.valueOf(orderHeaderId)));

        return Result.success("支付请求发送成功");
    }

    /**
     * 直接生成Excel文件返回浏览器下载
     *
     * @param fileName
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.GET,value = "/downloadOrder")
    public void downloadOrderInfoByExcel(@RequestParam String fileName) throws IOException {
        orderServiceImpl.downloadOrderByExcel(fileName);
    }

    /**
     * 在服务端生成Excel文件
     *
     * @param fileName 生成的Excel文件名
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.GET,value = "/generateOrder")
    public void generateOrderInfoByExcel(@RequestParam String fileName) throws IOException {
        orderServiceImpl.generateOrderByExcel(fileName);
    }
    @RequestMapping(method = RequestMethod.PUT, value = "/cancel/{userId}/{orderHeaderId}",produces = "application/json")
    public String cancelOrder(@PathVariable int userId, @PathVariable int orderHeaderId){
        OrderHeaderPo orderHeaderPo = orderHeaderService.getById(orderHeaderId);
        if(orderHeaderPo==null){
            return "无效订单";
        }
        if(OrderStatus.PAID.getOrderStatus()==orderHeaderPo.getStatus()){
            return "订单已支付，不能取消订单";
        }
        if(OrderStatus.CANCEL.getOrderStatus()==orderHeaderPo.getStatus()){
            return "订单已取消，不能再取消订单";
        }
        if(OrderStatus.OPEN.getOrderStatus()==orderHeaderPo.getStatus()){
            orderServiceImpl.cancelOrderWithoutStockChange(userId,orderHeaderId);
        }
        else
        {
            orderServiceImpl.cancelOrderWithStockChange(userId,orderHeaderId);
        }
        return "成功取消订单";
    }
}
