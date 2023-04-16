import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.util.concurrent.RateLimiter;
import com.herosoft.commons.dto.UserDto;
import com.herosoft.user.UserApplication;
import com.herosoft.user.mappers.UserMapper;
import com.herosoft.user.mongo.Student;
import com.herosoft.user.mongo.StudentRepository;
import com.herosoft.user.po.UserPo;
import com.herosoft.user.service.impl.UserServiceImpl;
import com.mongodb.client.MongoCursor;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import reactor.core.publisher.Flux;
import testcases.DelayTask;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
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
    private StudentRepository studentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserServiceImpl userServiceImpl;

    private MockMvc mockMvc;

    @Before
    public void setupTest() {
        log.info("Before方法执行。。。");
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testCheckStatus() throws Exception {

        String token = mockMvc.perform(MockMvcRequestBuilders.get("/users/checkstatus")
                        .accept(MediaType.TEXT_HTML))
                .andReturn()
                .getResponse().getContentAsString();
        log.info("获取到的Token:{}" + token);
        Assert.assertEquals("用户微服务状态正常 Port:null token secret:changedagain!!!!!!!okokok", token);
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
    public void testFlux() {
        Flux.create(fluxSink -> {
            fluxSink.next("test1");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            fluxSink.next("test2");
            fluxSink.complete();
        }).subscribe(System.out::println);

        System.out.println("主线程结束");
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
                System.out.println("ScheduleThreadPool " + Thread.currentThread().getName() + "线程正在执行...");
            }
        }, 3, TimeUnit.SECONDS);

    }

    @Test
    public void testThreadPoolExecutor() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                2,
                4,
                300,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(4));
    }

    @Test
    public void testTempCase() {
        List<String> list = new ArrayList<>(Arrays.asList("item1", "item2", "item3"));
        list.add("item4");
        Assert.assertEquals(4, list.size());
    }

    @Test
    public void testStream() {
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 8, 9, 6, 10));//Arrays.asList生成的固定size的List，通过new ArrayList封装后，List大小可变
        //1.filter demo
        list.stream().filter(x -> x <= 8 && x >= 6).forEach(System.out::println);

        //2.map demo
        list.stream().map(x -> {
            if (x >= 9) {
                return "LargeMapValue" + x;
            }
            return "SmallMapValue" + x;
        }).map(String::toUpperCase).forEach(System.out::println);

        //3.reduce demo
        Stream<Integer> stream = list.stream();

        System.out.println("利用reduce对List元素求和=" + stream.reduce(0, (accumulate, element) -> accumulate + element));

        //如果没有accumulate初始值，如果stream为空，reduce会返回Optional对象，
        //所以需要判断isPresent再获取Optional值
        Stream<Integer> stream2 = Stream.of();
        Optional<Integer> optionalInteger = stream2.reduce((accumulate, element) -> accumulate + element);
        if (optionalInteger.isPresent()) {
            System.out.println(optionalInteger.get());
        }

        //利用reduce合并对象
        List<String> props = Arrays.asList("profile=dev", "debug=true", "interval=30"); //使用Arrays.asList生成的是固定size的List

        Map<String, String> map = props.stream()
                //把k-v字符串转换为Map(k,v)
                .map(s -> {
                    String[] str = s.split("\\=", 2);
                    Map<String, String> temp = new HashMap<>();
                    temp.put(str[0], str[1]);
                    return temp;
                })
                //把Map合并
                .reduce(new HashMap<String, String>(), (accumulate, element) -> {
                    accumulate.putAll(element);
                    return accumulate;
                });
        map.forEach((k, v) -> System.out.println("k=" + k + ",v=" + v));

        //4.collect demo
        List<String> propsList = Arrays.asList("profile=dev", "debug=true", "interval=30"); //使用Arrays.asList生成的是固定size的List

        consoleLog("利用collect()生成List:");
        propsList.stream().collect(Collectors.toList()).forEach(System.out::println);
        consoleLog("利用collect()生成Set:");
        propsList.stream().collect(Collectors.toSet()).forEach(System.out::println);
        consoleLog("利用collect()生成Map:");
        propsList.stream().collect(Collectors.toMap(k -> k.substring(0, k.indexOf("=")),
                v -> v.substring(v.indexOf("=")))).forEach((k, v) -> System.out.println("k=" + k + ",v=" + v));

        consoleLog("利用collect()生成分组Map:");
        List<String> fruitsList = Arrays.asList("Apple", "Banana", "BlueChery", "PineApple", "Pear");
        Map<String, List<String>> fruitsGroup = fruitsList.stream()
                .collect(Collectors.groupingBy(s -> s.substring(0, 1),
                        Collectors.toList()));
        System.out.println(fruitsGroup);

        //5.sort demo
        consoleLog("利用sort排序stream:");
        List<Integer> list2 = Arrays.asList(18, 2, 29, 1, 32, 100);
        list2.stream().sorted().forEach(System.out::println);

        //6.distinct demo
        List<Integer> list3 = Arrays.asList(18, 18, 29, 32, 2, 29, 1, 32, 100);
        consoleLog("利用distinct去重stream:");
        list3.stream().distinct().forEach(System.out::println);
        list3 = list3.stream().distinct().collect(Collectors.toList());
        System.out.println("利用stream distinct去重后的list:" + list3);

        //7.skip limit demo
        List<Integer> list4 = Arrays.asList(18, 18, 29, 32, 2, 29, 1, 32, 100);
        consoleLog("利用skip,limit截取stream:");
        list4.stream().skip(3).limit(3).forEach(System.out::println);

        //8.concat demo
        List<Integer> list5 = Arrays.asList(18, 18, 29, 32, 2, 29, 1, 32, 100);
        List<Integer> list6 = Arrays.asList(3, 9, 8, 711, 2);
        consoleLog("利用concat合并stream:");
        Stream.concat(list5.stream(), list6.stream()).forEach(System.out::println);

        //9.flatMap demo
        List<Integer> list7 = Arrays.asList(18, 18, 29, 32, 2, 29, 1, 32, 100);
        List<List<Integer>> list8 = Arrays.asList(
                Arrays.asList(33, 22, 88),
                Arrays.asList(98, 10, 3),
                Arrays.asList(1092, 2, 99)
        );

        consoleLog("利用flatMap把每个元素都转换为stream，再合并为一个stream");
        list7.stream().flatMap(s -> Stream.of(s)).forEach(System.out::println);
        list8.stream().flatMap(listElement -> listElement.stream()).forEach(System.out::println);

        //10.count demo
        List<Integer> list9 = Arrays.asList(18, 18, 29, 32, 2, 29, 1, 32, 100);
        System.out.println("利用count求stream元素个数：" + list9.stream().count());

        //11.min demo
        System.out.println("利用min求stream最小元素:" + list9.stream().min(Integer::compareTo).get());
        System.out.println("利用min求stream最小元素:" + list9.stream().min((a, b) -> a - b).get());
        //12.max demo
        System.out.println("利用max求stream最大元素:" + list9.stream().max(Integer::compareTo).get());
        System.out.println("利用max求stream最大元素:" + list9.stream().max((a, b) -> a - b).get());

    }

    private static void consoleLog(String x) {
        System.out.println(x);
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testWrongWay1ToRemoveItemFromList() {
        List<String> list1 = new ArrayList<>(Arrays.asList("apple", "banana", "pineapple", "orange"));
        for (String item : list1) {
            if (item.contains("o")) {
                list1.remove(item);
            }
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testWrongWay2ToRemoveItemFromList() {
        List<String> list1 = new ArrayList<>(Arrays.asList("apple", "banana", "pineapple", "orange"));
        int listsize = list1.size();

        for (int i = 0; i < listsize; i++) {
            String item = list1.get(i);

            if (item.contains(B)) {
                list1.remove(i);
            }
        }
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testWrongWay3ToRemoveItemFromList() {
        List<String> list1 = new ArrayList<>(Arrays.asList("apple", "banana", "pineapple", "orange"));

        for (Iterator<String> iterator = list1.iterator(); iterator.hasNext(); ) {
            String item = iterator.next();

            if (item.contains("b")) {
                list1.remove(item);
            }
        }
    }

    @Test
    public void testWrongWay4ToRemoveItemFromList() {
        List<String> list1 = new ArrayList<>(Arrays.asList("张三", "李四", "李四", "周一", "刘四", "李强", "李白"));

        //这种方法每次都调用size，虽然不会报错，但有可能漏删，且执行性能有影响
        for (int i = 0; i < list1.size(); i++) {
            String item = list1.get(i);

            if (item.contains("李")) {
                list1.remove(i);
            }
        }
        System.out.println("清理后的List:" + list1);
        System.out.println("清理后的List size:" + list1.size());

    }

    @Test()
    public void testCorrectWay1ToRemoveItemFromList() {
        List<String> list1 = new ArrayList<>(Arrays.asList("张三", "李四", "李四", "周一", "刘四", "李强", "李白"));

        list1 = list1.stream().filter(e -> !e.contains("李")).collect(Collectors.toList());

        System.out.println("清理后的List:" + list1);
        log.info("清理后的List size:{}", list1.size());

    }

    @Test()
    public void testCorrectWay2ToRemoveItemFromList() {
        List<String> list1 = new ArrayList<>(Arrays.asList("apple", "banana", "pineapple", "orange"));

        for (Iterator<String> iterator = list1.iterator(); iterator.hasNext(); ) {
            String item = iterator.next();

            if (item.contains("b")) {
                iterator.remove();
            }
        }
        log.info("清理后的List size:{}", list1.size());

    }

    @Test
    public void testRedisSet() {
        ValueOperations ops = redisTemplate.opsForValue();
        ops.set("firstkey", "2");
    }

    @Test
    public void testRedisGet() {
        ValueOperations ops = redisTemplate.opsForValue();
        consoleLog(ops.get("firstkey").toString());
    }

    @Test
    public void testRedisHashSet() {
        HashOperations opsForHash = redisTemplate.opsForHash();
        opsForHash.put("user", "name", "Tom");
        opsForHash.put("user", "age", "12");
    }

    @Test
    public void testRedisHashGet() {
        HashOperations opsForHash = redisTemplate.opsForHash();
        consoleLog(opsForHash.get("user", "name").toString());

        consoleLog(opsForHash.keys("user").toString());
        consoleLog(opsForHash.values("user").toString());
        consoleLog(opsForHash.entries("user").toString());
    }

    @Test
    public void testRedisListSet() {
        ListOperations opsForList = redisTemplate.opsForList();
        UserDto userDto = new UserDto();
        userDto.setUserId(0);
        userDto.setUserName("Tom");
        userDto.setUserType("0");

        opsForList.leftPush("userlist", userDto);

        userDto.setUserId(1);
        userDto.setUserName("Jack");
        userDto.setUserType("1");

        opsForList.leftPush("userlist", userDto);

    }

    @Test
    public void testRedisListGet() {
        ListOperations opsForList = redisTemplate.opsForList();
        consoleLog(opsForList.leftPop("userlist").toString());
    }

    @Test
    public void testRedisSetSet() {
        SetOperations opsForSet = redisTemplate.opsForSet();
        UserDto userDto = new UserDto();
        userDto.setUserId(0);
        userDto.setUserName("Mary");
        userDto.setUserType("0");

        opsForSet.add("userset", userDto);

        userDto.setUserId(1);
        userDto.setUserName("Rose");
        userDto.setUserType("1");

        opsForSet.add("userset", userDto);
    }

    @Test
    public void testRedisSetGet() {
        SetOperations opsForSet = redisTemplate.opsForSet();

        consoleLog(opsForSet.pop("userset").toString());
    }

    @Test
    public void testRedisZsetSet() {
        ZSetOperations opsForZSet = redisTemplate.opsForZSet();

        opsForZSet.add("zset", "a", 8);
        opsForZSet.add("zset", "b", 6);
        opsForZSet.add("zset", "c", 1);
        opsForZSet.add("zset", "d", 3);


    }

    @Test
    public void testRedisZsetGet() {
        ZSetOperations opsForZSet = redisTemplate.opsForZSet();

        consoleLog(opsForZSet.range("zset", 0, 3).toString());
        opsForZSet.incrementScore("zset", "c", 6);
        consoleLog(opsForZSet.range("zset", 0, 3).toString());

    }

    @Test
    public void testSelectUserMapper() {
        List<UserPo> userList = userMapper.selectList(null);

        //Assert.assertEquals(4,userList.size());

        userList.forEach(System.out::println);
    }

    @Test
    public void testAddUsers() {
        UserPo user = new UserPo();

        user.setUsername("韩八");
        user.setPassword("123456");
        user.setBalance(4000.00);
        user.setSex("男");

        userServiceImpl.save(user);

        userServiceImpl.list().forEach(System.out::println);
    }

    @Test
    public void testAddUsersByBatch() {
        UserPo user1 = new UserPo();
        UserPo user2 = new UserPo();
        List<UserPo> userList = new ArrayList<>();

        user1.setUsername("张二");
        user1.setPassword("123456");
        user1.setBalance(3000.00);
        user1.setSex("男");

        userList.add(user1);

        user2.setUsername("张四");
        user2.setPassword("123456");
        user2.setBalance(2000.00);
        user2.setSex("女");

        userList.add(user2);

        userServiceImpl.saveBatch(userList);

        userServiceImpl.list().forEach(System.out::println);
    }

    @Test
    public void testAddUsersByBatchSql() {
        UserPo user1 = new UserPo();
        UserPo user2 = new UserPo();
        List<UserPo> userList = new ArrayList<>();

        user1.setUsername("张二");
        user1.setPassword("123456");
        user1.setBalance(5000.00);
        user1.setSex("男");

        userList.add(user1);

        user2.setUsername("张九");
        user2.setPassword("123456");
        user2.setBalance(7000.00);
        user2.setSex("女");

        userList.add(user2);

        userServiceImpl.saveBatchBySql(userList);

        userServiceImpl.list().forEach(System.out::println);
    }

    @Test
    public void testQueryWrapper() {
        QueryWrapper<UserPo> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", "李九")
                .between("balance", 1000.00, 9000.00)
                .select("username", "balance")
                .orderByDesc("balance");

        userServiceImpl.list(userQueryWrapper).forEach(System.out::println);
    }

    @Test
    public void testUpdateWrapper() {
        UpdateWrapper<UserPo> userUpdateWrapper = new UpdateWrapper<>();
        UpdateWrapper<UserPo> userUpdateWrapper2 = new UpdateWrapper<>();
        UserPo user = new UserPo();
        //方法一 这种方法不会触发POJO对象的字段的自动填充设置@TableField(fill=XXXX)
        userUpdateWrapper.eq("username", "张三")
                .set("balance", 550.00);
        userServiceImpl.update(null, userUpdateWrapper);

        //方法二 这种方法可以触发POJO对象的字段的自动填充设置@TableField(fill=XXXX)
        userUpdateWrapper2.eq("sex", "男")
                .or().eq("sex", "女");
        user.setPassword("12345678");

        userServiceImpl.update(user, userUpdateWrapper2);

        userServiceImpl.list().forEach(System.out::println);
    }

    @Test
    public void testUpdateUsers() {
        UserPo user = new UserPo();

        user = userServiceImpl.findById(3);
        user.setBalance(4000.00);

        userServiceImpl.update(user);
        userServiceImpl.list().forEach(System.out::println);
    }

    @Test
    public void testFindUsersSelective() {
        UserDto userCriteria = new UserDto();

        //userCriteria.setBalance(4000.00);
        userCriteria.setUserName("王五");

        userServiceImpl.findUsersSelective(userCriteria).forEach(System.out::println);
    }

    @Test
    public void testStrFilter() {

        String input = "abcd中ea";
        String filter1 = "ab";

        String[] array1 = input.split("");

        List<String> list1 = Arrays.asList(array1);

        List<String> result = list1.stream()
                .filter(char1 -> !filter1.contains(char1))
                .collect(Collectors.toList());

        System.out.println("Stream collectors.joining方式：" + Stream.of(input.split(""))
                .filter(str -> !filter1.contains(str))
                .collect(Collectors.joining()));

        System.out.println(String.join("", result));


    }

    public static void main(String[] args) {
        String message = "Main method 开始执行。。。";
        consoleLog(message);
    }

    @Test
    public void testHashCode() {
        Set<Person> set1 = new HashSet<>();

        set1.add(new Person("张三", 21));
        set1.add(new Person("李四", 19));
        set1.add(new Person("张三", 21));
        set1.add(new Person("王五", 31));

        System.out.println(set1);
    }

    @Test
    public void testTreeMap(){
        TreeMap<String,Integer> map1 = new TreeMap<>();

        map1.put("张一",21);
        map1.put("张三",31);
        map1.put("张六",81);
        map1.put("张二",25);
        map1.put("张九",121);

        map1.forEach((key,value)->{System.out.println("Key:"+key+",Value:"+value);});
    }

    @Test
    public void testLinkedHashMap(){
        Map<Integer,Integer> map1 = new LinkedHashMap<>();

        map1.put(2,81);
        map1.put(6,31);
        map1.put(1,21);
        map1.put(5,25);
        map1.put(4,121);

        map1.forEach((key,value)->{System.out.println("Key:"+key+",Value:"+value);});
    }

    @Test
    public void testSemaphore() throws InterruptedException {
        //控制线程并发数量
        Semaphore semaphore = new Semaphore(2);
        //控制线程开始或结束
        CountDownLatch countDownLatch = new CountDownLatch(10);

        for(int i=0;i<10;i++){
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        semaphore.acquire();
                        countDownLatch.countDown();
                        System.out.println(Thread.currentThread().getName() + "开始通过隧道。。。");
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    finally {
                        semaphore.release();
                    }
                }
            }).start();
        }
        countDownLatch.await();
        System.out.println("车辆同行完毕。。。");
    }
    @Test
    public void testAtomic(){
        AtomicInteger atomicCount = new AtomicInteger();

        System.out.println(atomicCount.getAndIncrement()); //输出0
        System.out.println(atomicCount.getAndUpdate(p->p-2));//输出1
        System.out.println(atomicCount.get());//输出-1
        System.out.println(atomicCount.getAndAccumulate(10,(p,x)->p+x));//输出-1
        System.out.println(atomicCount.get());//输出9

        System.out.println(UUID.randomUUID().toString());

    }

    @Test
    public void testRateLimiter() throws InterruptedException {
        RateLimiter rateLimiter = RateLimiter.create(2);

        CountDownLatch countDownLatch = new CountDownLatch(10);

        Thread.sleep(30000);
        for(int i=0; i<10;i++){
            new Thread(new Runnable() {

                @Override
                public void run() {
                    rateLimiter.acquire();
                    countDownLatch.countDown();
                    System.out.println(Thread.currentThread().getName()+"抢到了。。。"+ LocalDateTime.now());
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }

        countDownLatch.await();
    }

    @Test
    public void testQueue(){
        Queue queue = new LinkedBlockingQueue(Arrays.asList("test","test2"));
        queue.add("test3");

        System.out.println("peek one time:"+queue.peek());//test
        System.out.println("peek second time:"+queue.peek());//test
        System.out.println("poll one time:"+queue.poll());//test
        System.out.println("peek third time:"+queue.peek());//test2

        Queue queue2 = new PriorityBlockingQueue(10, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Person) o1).getAge()-((Person) o2).getAge();
            }
        });

        queue2.add(new Person("张三",31));
        queue2.add(new Person("张一",11));
        queue2.add(new Person("张五",41));

        System.out.println("1st peek:"+queue2.peek());
        System.out.println("1st poll:"+queue2.poll());
        System.out.println("2nd poll:"+queue2.poll());
        System.out.println("3rd poll:"+queue2.poll());
    }

    @Test
    public void testDelayQueue() throws InterruptedException {
        BlockingQueue delayQueue = new DelayQueue();

        delayQueue.add(new DelayTask<>(20000, "第二个任务"));
        delayQueue.add(new DelayTask<>(10000, "第一个任务"));

        while(!delayQueue.isEmpty()){
            DelayTask task = (DelayTask) delayQueue.take();
            System.out.println(task);
        }
    }

    @Test
    public void testAddStudent(){
        for(int count=0;count<10;count++){
            Student student = new Student();

            student.setStudentName("Student"+count);
            student.setStudentAge(20+count);
            student.setBirthday(new Date());

            studentRepository.save(student);
        }
    }

    @Test
    public void testQueryStudent(){
        Bson query = new Bson() {
            @Override
            public <TDocument> BsonDocument toBsonDocument(Class<TDocument> aClass, CodecRegistry codecRegistry) {
                return null;
            }
        } ;
        MongoCursor<Document> cursor=mongoTemplate.getCollection("student").find().iterator();
        while(cursor.hasNext()){
            Document next = cursor.next();

            System.out.println(next.getObjectId("_id")+":"+next.getString("studentName"));
        }

    }

}
