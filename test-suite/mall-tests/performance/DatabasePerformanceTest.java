package com.imooc.mall.performance;

import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.dao.*;
import com.imooc.mall.pojo.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabasePerformanceTest extends MallApplicationTests {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    private static final int THREAD_COUNT = 10;
    private static final int BATCH_SIZE = 1000;
    private ExecutorService executorService;

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
    }

    @Test
    @Order(1)
    @DisplayName("批量插入商品性能测试")
    void testBatchInsertProducts() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            Future<Integer> future = executorService.submit(() -> {
                try {
                    List<Product> products = new ArrayList<>();
                    for (int j = 0; j < BATCH_SIZE; j++) {
                        Product product = new Product();
                        product.setCategoryId(1);
                        product.setName("性能测试商品_" + threadId + "_" + j);
                        product.setSubtitle("性能测试副标题");
                        product.setMainImage("test.jpg");
                        product.setPrice(new BigDecimal("99.99"));
                        product.setStatus(1);
                        product.setStock(100);
                        products.add(product);
                    }
                    
                    int insertCount = 0;
                    for (Product product : products) {
                        insertCount += productMapper.insertSelective(product);
                    }
                    return insertCount;
                } finally {
                    latch.countDown();
                }
            });
            futures.add(future);
        }

        latch.await();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        int totalInserted = 0;
        for (Future<Integer> future : futures) {
            try {
                totalInserted += future.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("批量插入商品性能测试结果：");
        System.out.println("总插入记录数: " + totalInserted);
        System.out.println("总耗时: " + totalTime + "ms");
        System.out.println("平均每条记录耗时: " + (double)totalTime/totalInserted + "ms");
    }

    @Test
    @Order(2)
    @DisplayName("商品查询性能测试")
    void testProductQueryPerformance() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            Future<Integer> future = executorService.submit(() -> {
                try {
                    int queryCount = 0;
                    for (int j = 0; j < 100; j++) {
                        List<Product> products = productMapper.selectAll();
                        queryCount += products.size();
                    }
                    return queryCount;
                } finally {
                    latch.countDown();
                }
            });
            futures.add(future);
        }

        latch.await();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        int totalQueried = 0;
        for (Future<Integer> future : futures) {
            try {
                totalQueried += future.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("商品查询性能测试结果：");
        System.out.println("总查询记录数: " + totalQueried);
        System.out.println("总耗时: " + totalTime + "ms");
        System.out.println("平均每条记录查询耗时: " + (double)totalTime/totalQueried + "ms");
    }

    @Test
    @Order(3)
    @DisplayName("订单创建性能测试")
    void testOrderCreatePerformance() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            Future<Integer> future = executorService.submit(() -> {
                try {
                    List<Order> orders = new ArrayList<>();
                    for (int j = 0; j < 100; j++) {
                        Order order = new Order();
                        order.setUserId(1);
                        order.setOrderNo(System.currentTimeMillis() + threadId * 10000 + j);
                        order.setPayment(new BigDecimal("100.00"));
                        order.setPaymentType(1);
                        order.setPostage(0);
                        order.setStatus(10);
                        orders.add(order);
                    }
                    
                    int insertCount = 0;
                    for (Order order : orders) {
                        insertCount += orderMapper.insertSelective(order);
                    }
                    return insertCount;
                } finally {
                    latch.countDown();
                }
            });
            futures.add(future);
        }

        latch.await();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        int totalInserted = 0;
        for (Future<Integer> future : futures) {
            try {
                totalInserted += future.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("订单创建性能测试结果：");
        System.out.println("总创建订单数: " + totalInserted);
        System.out.println("总耗时: " + totalTime + "ms");
        System.out.println("平均每个订单创建耗时: " + (double)totalTime/totalInserted + "ms");
    }

    @Test
    @Order(4)
    @DisplayName("购物车操作性能测试")
    void testCartOperationsPerformance() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            Future<Integer> future = executorService.submit(() -> {
                try {
                    int operationCount = 0;
                    for (int j = 0; j < 50; j++) {
                        // 添加购物车
                        Cart cart = new Cart();
                        cart.setUserId(1);
                        cart.setProductId(threadId * 50 + j);
                        cart.setQuantity(1);
                        cart.setSelected(true);
                        operationCount += cartMapper.insertSelective(cart);

                        // 更新购物车
                        cart.setQuantity(2);
                        operationCount += cartMapper.updateByPrimaryKeySelective(cart);

                        // 查询购物车
                        List<Cart> cartList = cartMapper.selectByUserId(1);
                        operationCount += cartList.size();
                    }
                    return operationCount;
                } finally {
                    latch.countDown();
                }
            });
            futures.add(future);
        }

        latch.await();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        int totalOperations = 0;
        for (Future<Integer> future : futures) {
            try {
                totalOperations += future.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("购物车操作性能测试结果：");
        System.out.println("总操作次数: " + totalOperations);
        System.out.println("总耗时: " + totalTime + "ms");
        System.out.println("平均每次操作耗时: " + (double)totalTime/totalOperations + "ms");
    }

    @Test
    @Order(5)
    @DisplayName("类目查询性能测试")
    void testCategoryQueryPerformance() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            Future<Integer> future = executorService.submit(() -> {
                try {
                    int queryCount = 0;
                    for (int j = 0; j < 1000; j++) {
                        // 递归查询所有子类目
                        List<Category> categories = categoryMapper.selectAll();
                        queryCount += categories.size();
                    }
                    return queryCount;
                } finally {
                    latch.countDown();
                }
            });
            futures.add(future);
        }

        latch.await();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        int totalQueried = 0;
        for (Future<Integer> future : futures) {
            try {
                totalQueried += future.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("类目查询性能测试结果：");
        System.out.println("总查询记录数: " + totalQueried);
        System.out.println("总耗时: " + totalTime + "ms");
        System.out.println("平均每条记录查询耗时: " + (double)totalTime/totalQueried + "ms");
    }

    @AfterEach
    void tearDown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
