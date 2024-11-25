package com.imooc.mall.performance;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MallPerformanceTest {

    private static StandardJMeterEngine jmeter;
    private static HashTree testPlanTree;
    private static final String BASE_URL = "http://localhost:8080";
    private static final String JMETER_HOME = System.getProperty("user.home") + "/apache-jmeter";
    private static final String REPORT_DIR = "./performance-reports";

    @BeforeAll
    static void setUp() {
        // 设置JMeter运行环境
        File jmeterHome = new File(JMETER_HOME);
        String slash = System.getProperty("file.separator");
        
        if (jmeterHome.exists()) {
            File jmeterProperties = new File(jmeterHome.getPath() + slash + "bin" + slash + "jmeter.properties");
            if (jmeterProperties.exists()) {
                JMeterUtils.setJMeterHome(jmeterHome.getPath());
                JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
                JMeterUtils.initLocale();
            }
        }

        // 创建JMeter引擎
        jmeter = new StandardJMeterEngine();

        // 创建测试计划
        TestPlan testPlan = new TestPlan("商城系统性能测试计划");
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlan.class.getName());
        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

        // 创建测试计划树
        testPlanTree = new HashTree();
        testPlanTree.add(testPlan);

        // 创建报告目录
        new File(REPORT_DIR).mkdirs();
    }

    @Test
    @Order(1)
    @DisplayName("商品列表接口性能测试")
    void testProductListPerformance() throws Exception {
        // 创建HTTP请求
        HTTPSampler productListSampler = new HTTPSampler();
        productListSampler.setDomain("localhost");
        productListSampler.setPort(8080);
        productListSampler.setPath("/products");
        productListSampler.setMethod("GET");
        
        // 设置线程组
        ThreadGroup threadGroup = createThreadGroup(100, 10, "商品列表性能测试线程组");
        
        // 添加到测试计划树
        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
        threadGroupHashTree.add(productListSampler);
        
        // 添加结果收集器
        String reportFile = REPORT_DIR + "/product-list-" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".jtl";
        addResultCollector(threadGroupHashTree, reportFile);
        
        // 执行测试
        jmeter.configure(testPlanTree);
        jmeter.run();
        
        // 验证结果
        File reportFile = new File(reportFile);
        Assertions.assertTrue(reportFile.exists());
    }

    @Test
    @Order(2)
    @DisplayName("用户登录接口性能测试")
    void testUserLoginPerformance() throws Exception {
        // 创建HTTP请求
        HTTPSampler loginSampler = new HTTPSampler();
        loginSampler.setDomain("localhost");
        loginSampler.setPort(8080);
        loginSampler.setPath("/user/login");
        loginSampler.setMethod("POST");
        
        // 添加请求参数
        Arguments arguments = new Arguments();
        arguments.addArgument("username", "test_user");
        arguments.addArgument("password", "123456");
        loginSampler.setArguments(arguments);
        
        // 设置线程组
        ThreadGroup threadGroup = createThreadGroup(50, 5, "用户登录性能测试线程组");
        
        // 添加到测试计划树
        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
        threadGroupHashTree.add(loginSampler);
        
        // 添加结果收集器
        String reportFile = REPORT_DIR + "/user-login-" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".jtl";
        addResultCollector(threadGroupHashTree, reportFile);
        
        // 执行测试
        jmeter.configure(testPlanTree);
        jmeter.run();
    }

    @Test
    @Order(3)
    @DisplayName("订单创建接口性能测试")
    void testOrderCreatePerformance() throws Exception {
        // 创建HTTP请求
        HTTPSampler orderCreateSampler = new HTTPSampler();
        orderCreateSampler.setDomain("localhost");
        orderCreateSampler.setPort(8080);
        orderCreateSampler.setPath("/orders");
        orderCreateSampler.setMethod("POST");
        
        // 添加请求头
        HeaderManager headerManager = new HeaderManager();
        headerManager.add("Content-Type", "application/json");
        
        // 设置线程组
        ThreadGroup threadGroup = createThreadGroup(20, 2, "订单创建性能测试线程组");
        
        // 添加到测试计划树
        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
        threadGroupHashTree.add(orderCreateSampler);
        threadGroupHashTree.add(headerManager);
        
        // 添加结果收集器
        String reportFile = REPORT_DIR + "/order-create-" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".jtl";
        addResultCollector(threadGroupHashTree, reportFile);
        
        // 执行测试
        jmeter.configure(testPlanTree);
        jmeter.run();
    }

    @Test
    @Order(4)
    @DisplayName("支付创建接口性能测试")
    void testPayCreatePerformance() throws Exception {
        // 创建HTTP请求
        HTTPSampler payCreateSampler = new HTTPSampler();
        payCreateSampler.setDomain("localhost");
        payCreateSampler.setPort(8080);
        payCreateSampler.setPath("/pay");
        payCreateSampler.setMethod("POST");
        
        // 添加请求头
        HeaderManager headerManager = new HeaderManager();
        headerManager.add("Content-Type", "application/json");
        
        // 设置线程组
        ThreadGroup threadGroup = createThreadGroup(10, 1, "支付创建性能测试线程组");
        
        // 添加到测试计划树
        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
        threadGroupHashTree.add(payCreateSampler);
        threadGroupHashTree.add(headerManager);
        
        // 添加结果收集器
        String reportFile = REPORT_DIR + "/pay-create-" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".jtl";
        addResultCollector(threadGroupHashTree, reportFile);
        
        // 执行测试
        jmeter.configure(testPlanTree);
        jmeter.run();
    }

    @Test
    @Order(5)
    @DisplayName("购物车操作性能测试")
    void testCartOperationsPerformance() throws Exception {
        // 创建HTTP请求 - 添加商品到购物车
        HTTPSampler addToCartSampler = new HTTPSampler();
        addToCartSampler.setDomain("localhost");
        addToCartSampler.setPort(8080);
        addToCartSampler.setPath("/cart");
        addToCartSampler.setMethod("POST");
        
        // 添加请求头
        HeaderManager headerManager = new HeaderManager();
        headerManager.add("Content-Type", "application/json");
        
        // 设置线程组
        ThreadGroup threadGroup = createThreadGroup(30, 3, "购物车操作性能测试线程组");
        
        // 添加到测试计划树
        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
        threadGroupHashTree.add(addToCartSampler);
        threadGroupHashTree.add(headerManager);
        
        // 添加结果收集器
        String reportFile = REPORT_DIR + "/cart-operations-" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".jtl";
        addResultCollector(threadGroupHashTree, reportFile);
        
        // 执行测试
        jmeter.configure(testPlanTree);
        jmeter.run();
    }

    private ThreadGroup createThreadGroup(int numThreads, int rampUpPeriod, String name) {
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName(name);
        threadGroup.setNumThreads(numThreads);
        threadGroup.setRampUp(rampUpPeriod);
        threadGroup.setSamplerController(new LoopController());
        ((LoopController) threadGroup.getSamplerController()).setLoops(1);
        ((LoopController) threadGroup.getSamplerController()).setFirst(true);
        return threadGroup;
    }

    private void addResultCollector(HashTree threadGroup, String filename) {
        ResultCollector resultCollector = new ResultCollector(new Summariser());
        resultCollector.setFilename(filename);
        threadGroup.add(resultCollector);
    }

    @AfterAll
    static void tearDown() {
        if (jmeter != null) {
            jmeter.exit();
        }
    }
}
