package com.herosoft.order.schedules;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.herosoft.commons.dto.ProductDto;
import com.herosoft.order.clients.ProductService;
import com.herosoft.order.constants.OrderConstants;
import com.herosoft.order.enums.OrderStatus;
import com.herosoft.order.po.OrderDetailPo;
import com.herosoft.order.po.OrderHeaderPo;
import com.herosoft.order.services.impl.OrderDetailServiceImpl;
import com.herosoft.order.services.impl.OrderHeaderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OrderScheduleJobs {
    @Autowired
    private ProductService productService;

    @Autowired
    private OrderDetailServiceImpl orderDetailServiceImpl;

    @Autowired
    private OrderHeaderServiceImpl orderHeaderServiceImpl;

    @Value("${server.port}")
    private int port;

    @Scheduled(cron = "0/5 * * * * ?")
    public void printProductSummary(){
        List<ProductDto> productDtoList = JSON.parseArray(JSON.toJSONString(productService.findAll().getData()), ProductDto.class);
        AtomicLong paidOrders= new AtomicLong();
        AtomicLong waitingPaymentOrders= new AtomicLong();
        AtomicLong cancelOrders = new AtomicLong();

        List<Map<String,String>> reportList = new ArrayList<>();

        productDtoList.forEach(list -> {
            QueryWrapper<OrderDetailPo> orderDetailWrapper = new QueryWrapper<>();

            orderDetailWrapper.eq("product_id",list.getProductId());

            //获取商品相关的所有订单号列表
            List<Integer> orderHeaderIds = orderDetailServiceImpl.getBaseMapper().selectList(orderDetailWrapper).stream()
                    .map(orderDetailPo -> orderDetailPo.getOrderHeaderId())
                    .distinct()
                    .collect(Collectors.toList());
            if (!orderHeaderIds.isEmpty()) {


                //创建待支付和已支付订单查询器
                QueryWrapper<OrderHeaderPo> paidOrdersWrapper = new QueryWrapper<>();
                QueryWrapper<OrderHeaderPo> waitingPaymentOrdersWrapper = new QueryWrapper<>();
                QueryWrapper<OrderHeaderPo> cancelOrdersWrapper = new QueryWrapper<>();

                paidOrdersWrapper.in("order_header_id", orderHeaderIds)
                        .eq("status", OrderStatus.PAID.getOrderStatus());

                waitingPaymentOrdersWrapper.in("order_header_id", orderHeaderIds)
                        .in("status", Arrays.asList(OrderStatus.OPEN.getOrderStatus(), OrderStatus.STOCKOUT.getOrderStatus()));

                cancelOrdersWrapper.in("order_header_id", orderHeaderIds)
                        .eq("status", OrderStatus.CANCEL.getOrderStatus());

                //获取待支付和已支付订单数量
                paidOrders.set(orderHeaderServiceImpl.getBaseMapper().selectCount(paidOrdersWrapper));
                waitingPaymentOrders.set(orderHeaderServiceImpl.getBaseMapper().selectCount(waitingPaymentOrdersWrapper));
                cancelOrders.set(orderHeaderServiceImpl.getBaseMapper().selectCount(cancelOrdersWrapper));
            }
            else {
                paidOrders.set(0);
                waitingPaymentOrders.set(0);
                cancelOrders.set(0);
            }

            HashMap map = new HashMap();
            map.put("productName",list.getProductName());
            map.put("productPrice",list.getProductPrice());
            map.put("productAmount",list.getProductAmount());
            map.put("waitOrders",waitingPaymentOrders.get());
            map.put("paidOrders",paidOrders.get());
            map.put("cancelOrders",cancelOrders.get());
            reportList.add(map);

        });
        log.info("当前商品销售情况==========================port:{}",port);
        log.info("创建订单方法调用了{}次", OrderConstants.ORDER_CREATE_REQUESTS_COUNT);
        reportList.forEach(item ->{
            log.info("商品：{}   价格：{}   库存数量：{} 待支付订单数量：{}   已支付订单数量：{}   已取消订单数量：{}",
                    item.get("productName"),
                    item.get("productPrice"),
                    item.get("productAmount"),
                    item.get("waitOrders"),
                    item.get("paidOrders"),
                    item.get("cancelOrders"));
        });

    }
}
