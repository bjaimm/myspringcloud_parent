import com.herosoft.user.UserApplication;
import com.herosoft.user.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest(classes={UserApplication.class})
@RunWith(SpringRunner.class)
@Slf4j
public class TestCases {

    public static final String B = "b";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setupTest(){
        log.info("Before方法执行。。。");
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testCheckStatus() throws Exception {

        String token = mockMvc.perform(MockMvcRequestBuilders.get("/users/checkstatus")
                .accept(MediaType.TEXT_HTML))
                .andReturn()
                .getResponse().getContentAsString();
        log.info("获取到的Token:{}"+token);
        Assert.assertEquals("用户微服务状态正常 Port:null token secret:changedagain!!!!!!!okokok",token);
    }
    @Test
    public void testCacheThreadPool() {
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int i = 1; i <= 10; i++) {
            final int index = 1;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("CacheThreadPool " + Thread.currentThread().getName() + "线程正在执行-> index=" + index);
                }
            });
        }
    }

    @Test
    public void testFixedThreadPool() {
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        for (int i = 1; i <= 10; i++) {
            final int index = 1;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("FixedThreadPool " + Thread.currentThread().getName() + "线程正在执行-> index=" + index);
                }
            });
        }
    }

    @Test
    public void testSingleThreadPool() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        for (int i = 1; i <= 10; i++) {
            final int index = 1;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("SingleThreadPool " + Thread.currentThread().getName() + "线程正在执行-> index=" + index);
                }
            });
        }
    }

    @Test
    public void testScheduleThreadPool() {
        ExecutorService executorService = Executors.newScheduledThreadPool(2);

        for (int i = 1; i <= 10; i++) {
            final int index = 1;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("ScheduleThreadPool " + Thread.currentThread().getName() + "线程正在执行-> index=" + index);
                }
            });
        }
    }

    @Test
    public void testScheduleThreadPool2() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);

        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("ScheduleThreadPool " + Thread.currentThread().getName() + "线程正在执行..." );
            }
        }, 3, TimeUnit.SECONDS);

    }

    @Test
    public void testThreadPoolExecutor(){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                2,
                4,
                300,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(4));
    }
    @Test
    public void testTempCase(){
        List<String> list = new ArrayList<>(Arrays.asList("item1","item2","item3"));
        list.add("item4");
        Assert.assertEquals(4,list.size());
    }

    @Test
    public void testStream(){
        List<Integer> list = new ArrayList<>(Arrays.asList(1,8,9,6,10));//Arrays.asList生成的固定size的List，通过new ArrayList封装后，List大小可变
        //1.filter demo
        list.stream().filter(x -> x<=8 && x>=6).forEach(System.out::println);

        //2.map demo
        list.stream().map(x -> {
            if(x>=9) {
                return "LargeMapValue"+x;
            }
            return "SmallMapValue" + x;
        }).map(String::toUpperCase).forEach(System.out::println);

        //3.reduce demo
        Stream<Integer> stream = list.stream();

        System.out.println("利用reduce对List元素求和="+stream.reduce(0,(accumulate,element) -> accumulate + element));

        //如果没有accumulate初始值，如果stream为空，reduce会返回Optional对象，
        //所以需要判断isPresent再获取Optional值
        Stream<Integer> stream2 = Stream.of();
        Optional<Integer> optionalInteger = stream2.reduce((accumulate, element) -> accumulate + element);
        if(optionalInteger.isPresent()) {
            System.out.println(optionalInteger.get());
        }

        //利用reduce合并对象
        List<String> props = Arrays.asList("profile=dev","debug=true","interval=30"); //使用Arrays.asList生成的是固定size的List

        Map<String,String> map = props.stream()
                //把k-v字符串转换为Map(k,v)
                .map(s -> {
                    String[] str = s.split("\\=",2);
                    Map<String,String> temp = new HashMap<>();
                    temp.put(str[0],str[1]);
                    return temp;
                })
                //把Map合并
                .reduce(new HashMap<String,String>(), (accumulate,element)->{
                    accumulate.putAll(element);
                    return accumulate;
                });
        map.forEach((k,v)-> System.out.println("k="+k+",v="+v));

        //4.collect demo
        List<String> propsList = Arrays.asList("profile=dev","debug=true","interval=30"); //使用Arrays.asList生成的是固定size的List

        consoleLog("利用collect()生成List:");
        propsList.stream().collect(Collectors.toList()).forEach(System.out::println);
        consoleLog("利用collect()生成Set:");
        propsList.stream().collect(Collectors.toSet()).forEach(System.out::println);
        consoleLog("利用collect()生成Map:");
        propsList.stream().collect(Collectors.toMap(k->k.substring(0,k.indexOf("=")),
                v -> v.substring(v.indexOf("=")))).forEach((k,v)-> System.out.println("k="+k+",v="+v));

        consoleLog("利用collect()生成分组Map:");
        List<String> fruitsList = Arrays.asList("Apple","Banana","BlueChery","PineApple","Pear");
        Map<String,List<String>> fruitsGroup = fruitsList.stream()
                .collect(Collectors.groupingBy(s->s.substring(0,1),
                        Collectors.toList()));
        System.out.println(fruitsGroup);

        //5.sort demo
        consoleLog("利用sort排序stream:");
        List<Integer> list2 = Arrays.asList(18,2,29,1,32,100);
        list2.stream().sorted().forEach(System.out::println);

        //6.distinct demo
        List<Integer> list3 = Arrays.asList(18,18,29,32,2,29,1,32,100);
        consoleLog("利用distinct去重stream:");
        list3.stream().distinct().forEach(System.out::println);

        //7.skip limit demo
        List<Integer> list4 = Arrays.asList(18,18,29,32,2,29,1,32,100);
        consoleLog("利用skip,limit截取stream:");
        list4.stream().skip(3).limit(3).forEach(System.out::println);

        //8.concat demo
        List<Integer> list5 = Arrays.asList(18,18,29,32,2,29,1,32,100);
        List<Integer> list6 = Arrays.asList(3,9,8,711,2);
        consoleLog("利用concat合并stream:");
        Stream.concat(list5.stream(),list6.stream()).forEach(System.out::println);

        //9.flatMap demo
        List<Integer> list7 = Arrays.asList(18,18,29,32,2,29,1,32,100);
        List<List<Integer>> list8 = Arrays.asList(
                 Arrays.asList(33,22,88),
                 Arrays.asList(98,10,3),
                 Arrays.asList(1092,2,99)
        );

        consoleLog("利用flatMap把每个元素都转换为stream，再合并为一个stream");
        list7.stream().flatMap(s-> Stream.of(s)).forEach(System.out::println);
        list8.stream().flatMap(listElement->listElement.stream()).forEach(System.out::println);

        //10.count demo
        List<Integer> list9 = Arrays.asList(18,18,29,32,2,29,1,32,100);
        System.out.println("利用count求stream元素个数："+list9.stream().count());

        //11.min demo
        System.out.println("利用min求stream最小元素:"+list9.stream().min(Integer::compareTo).get());
        System.out.println("利用min求stream最小元素:"+list9.stream().min((a,b)->a-b).get());
        //12.max demo
        System.out.println("利用max求stream最大元素:"+list9.stream().max(Integer::compareTo).get());
        System.out.println("利用max求stream最大元素:"+list9.stream().max((a,b)->a-b).get());

    }

    private static void consoleLog(String x) {
        System.out.println(x);
    }

    @Test(expected = ConcurrentModificationException.class)
    public  void testWrongWay1ToRemoveItemFromList(){
        List<String> list1 = new ArrayList<>(Arrays.asList("apple","banana","pineapple","orange"));
        for(String item : list1){
            if(item.contains("o")){
                list1.remove(item);
            }
        }
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public  void testWrongWay2ToRemoveItemFromList(){
        List<String> list1 = new ArrayList<>(Arrays.asList("apple","banana","pineapple","orange"));
        int listsize = list1.size();

        for(int i =0;i<listsize;i++){
            String item = list1.get(i);

            if(item.contains(B)){
                list1.remove(i);
            }
        }
    }
    @Test(expected = ConcurrentModificationException.class)
    public void testWrongWay3ToRemoveItemFromList(){
        List<String> list1 = new ArrayList<>(Arrays.asList("apple","banana","pineapple","orange"));

        for(Iterator<String> iterator=list1.iterator(); iterator.hasNext();){
            String item = iterator.next();

            if(item.contains("b")){
                list1.remove(item);
            }
        }
    }
    @Test
    public void testCorrectWay1ToRemoveItemFromList(){
        List<String> list1 = new ArrayList<>(Arrays.asList("apple","banana","pineapple","orange"));

        //这种方法每次都调用size，执行性能有影响，不推荐
        for(int i =0;i<list1.size();i++){
            String item = list1.get(i);

            if(item.contains("b")){
                list1.remove(i);
            }
        }
        System.out.println("清理后的List size:"+list1.size());
    }
    @Test()
    public void testCorrectWay2ToRemoveItemFromList(){
        List<String> list1 = new ArrayList<>(Arrays.asList("apple","banana","pineapple","orange"));

        for(Iterator<String> iterator=list1.iterator(); iterator.hasNext();){
            String item = iterator.next();

            if(item.contains("b")){
                iterator.remove();
            }
        }
        log.info("清理后的List size:{}",list1.size());

    }

    @Test
    public void testRedisSet(){
        ValueOperations ops = redisTemplate.opsForValue();
        ops.set("firstkey","2");
    }

    @Test
    public void testRedisGet(){
        ValueOperations ops = redisTemplate.opsForValue();
        consoleLog(ops.get("firstkey").toString());
    }

    @Test
    public void testRedisHashSet(){
        HashOperations opsForHash = redisTemplate.opsForHash();
        opsForHash.put("user","name","Tom");
        opsForHash.put("user","age","12");
    }

    @Test
    public void testRedisHashGet(){
        HashOperations opsForHash = redisTemplate.opsForHash();
        consoleLog(opsForHash.get("user","name").toString());

        consoleLog(opsForHash.keys("user").toString());
        consoleLog(opsForHash.values("user").toString());
        consoleLog(opsForHash.entries("user").toString());
    }

    @Test
    public void testRedisListSet(){
        ListOperations opsForList = redisTemplate.opsForList();
        UserDto userDto = new UserDto();
        userDto.setId(0);
        userDto.setUserName("Tom");
        userDto.setUserType("0");

        opsForList.leftPush("userlist",userDto);

        userDto.setId(1);
        userDto.setUserName("Jack");
        userDto.setUserType("1");

        opsForList.leftPush("userlist",userDto);

    }

    @Test
    public void testRedisListGet(){
        ListOperations opsForList = redisTemplate.opsForList();
        consoleLog(opsForList.leftPop("userlist").toString());
    }

    @Test
    public void testRedisSetSet(){
        SetOperations opsForSet = redisTemplate.opsForSet();
        UserDto userDto = new UserDto();
        userDto.setId(0);
        userDto.setUserName("Mary");
        userDto.setUserType("0");

        opsForSet.add("userset",userDto);

        userDto.setId(1);
        userDto.setUserName("Rose");
        userDto.setUserType("1");

        opsForSet.add("userset",userDto);
    }

    @Test
    public void testRedisSetGet(){
        SetOperations opsForSet = redisTemplate.opsForSet();

        consoleLog(opsForSet.pop("userset").toString());
    }

    @Test
    public void testRedisZsetSet(){
        ZSetOperations opsForZSet = redisTemplate.opsForZSet();

        opsForZSet.add("zset","a",8);
        opsForZSet.add("zset","b",6);
        opsForZSet.add("zset","c",1);
        opsForZSet.add("zset","d",3);

    }

    @Test
    public void testRedisZsetGet(){
        ZSetOperations opsForZSet = redisTemplate.opsForZSet();

        consoleLog(opsForZSet.range("zset",0,3).toString());
        opsForZSet.incrementScore("zset","c",6);
        consoleLog(opsForZSet.range("zset",0,3).toString());

    }
    public static void main(String[] args) {
        String message = "Main method 开始执行。。。";
        consoleLog(message);
    }
}
