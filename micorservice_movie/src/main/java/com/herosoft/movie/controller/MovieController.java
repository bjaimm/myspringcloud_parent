package com.herosoft.movie.controller;

import com.herosoft.movie.client.UserService;
import com.herosoft.movie.dto.UserDto;
import com.herosoft.movie.service.TicketService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@RestController
@RequestMapping(value = "/movie")
public class MovieController {

    @Autowired
    private UserService userService;

    @Autowired
    private TicketService ticketService;

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            1000,
            2000,
            1,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(50));

    @Autowired
    RedissonClient redissonClient;

    private int soldTicketNumber=0;

    @RequestMapping(value="/buyticketwithoutlock",method = RequestMethod.PUT)
    public String buyTicketWithoutLock() throws ExecutionException, InterruptedException {

        List<Future<Integer>> futureList = new ArrayList<>(1000);
        soldTicketNumber=0;

        for(int i = 0; i<1000; i++){
            futureList.add(threadPoolExecutor.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return ticketService.decreaseTicketNumberById(1);
                }
            }));
        }

        for(int j = 0; j<1000;j++){

            if(futureList.get(j).get()==1){
                soldTicketNumber++;
            };
        }

        System.out.println("成功售出"+soldTicketNumber+"张票");

        return "成功售出"+soldTicketNumber+"张票";
    }

    @RequestMapping(value="/buyticketwithlock",method = RequestMethod.PUT)
    public String buyTicketWithLock() throws ExecutionException, InterruptedException {

        List<Future<Integer>> futureList = new ArrayList<>(1000);
        RLock rLock=redissonClient.getLock("reentrantLock");

        soldTicketNumber=0;

        for(int i = 0; i<1000; i++){
            futureList.add(threadPoolExecutor.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    boolean res;

                    res = rLock.tryLock(100,10,TimeUnit.SECONDS);

                    if(res) {
                        try {
                            return ticketService.decreaseTicketNumberById(1);
                        }finally {
                            rLock.unlock();
                        }
                    }
                    else {
                        return 0;
                    }
                }
            }));
        }

        for(int j = 0; j<1000;j++){

            if(futureList.get(j).get()==1){
                soldTicketNumber++;
            };
        }
        System.out.println("成功售出"+soldTicketNumber+"张票");
        return "成功售出"+soldTicketNumber+"张票";
    }

    @RequestMapping(value="/buyticketsingle",method = RequestMethod.PUT)
    public String buyTicketSingle() {

        if(ticketService.decreaseTicketNumberById(1)==1){
            soldTicketNumber++;
            System.out.println("成功售出第"+soldTicketNumber+"张票");
            return "成功售出第"+soldTicketNumber+"张票";
        }
        else {
            return "购票失败";
        }
    }
    @RequestMapping(value="/buyticketsinglewithlock",method = RequestMethod.PUT)
    public String buyTicketSingleWithLock() throws InterruptedException {
        RLock rLock=redissonClient.getLock("reentrantLock");
        boolean res=false;
        String retResult;
        retResult="购票失败-未进排队列";

        res=rLock.tryLock(20,10,TimeUnit.SECONDS);

        if (res) {
            try {
                if (ticketService.decreaseTicketNumberById(1) == 1) {
                    soldTicketNumber++;
                    System.out.println("成功售出第" + soldTicketNumber + "张票");
                    retResult= "成功售出第" + soldTicketNumber + "张票";
                } else {
                    retResult= "购票失败-出票失败";
                }
            }
            finally {
                rLock.unlock();
            }
        }

        return retResult;
    }


    @RequestMapping(value="/ticket/{id}",method = RequestMethod.GET)
    public String showMovieTicket(@PathVariable Integer id){
        System.out.println("正在查询电影"+id);

        return ticketService.showTicket(id);
    }

    @RequestMapping(value = "/order",method = RequestMethod.POST)
    public String order(){
        //模拟当前用户购票
        Integer id=1;

        UserDto user;

        user=userService.findById(id);
        System.out.println(user+"正在购票。。。");

        return "购票成功";
    }

}
