package com.imooc.mall.integration;

import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.dao.OrderMapper;
import com.imooc.mall.dao.PayInfoMapper;
import com.imooc.mall.enums.OrderStatusEnum;
import com.imooc.mall.enums.PayPlatformEnum;
import com.imooc.mall.enums.PaymentTypeEnum;
import com.imooc.mall.pojo.Order;
import com.imooc.mall.pojo.PayInfo;
import com.imooc.mall.service.IOrderService;
import com.imooc.mall.service.IPayService;
import com.imooc.mall.vo.OrderVo;
import com.imooc.mall.vo.ResponseVo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PayIntegrationTest extends MallApplicationTests {

    @Autowired
    private IPayService payService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private OrderMapper orderMapper;

    private static Long orderNo;
    private static Integer userId = 1;

    @BeforeEach
    void setUp() {
        // 创建测试订单
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNo(System.currentTimeMillis());
        order.setPaymentType(PaymentTypeEnum.PAY_ONLINE.getCode());
        order.setPostage(0);
        order.setPayment(new BigDecimal("100.00"));
        order.setPaymentType(PaymentTypeEnum.PAY_ONLINE.getCode());
        order.setStatus(OrderStatusEnum.NO_PAY.getCode());
        orderMapper.insertSelective(order);
        orderNo = order.getOrderNo();
    }

    @Test
    @Order(1)
    @DisplayName("测试创建支付")
    void testCreatePay() {
        // 创建支付
        ResponseVo responseVo = payService.create(userId, orderNo);
        
        // 验证响应
        assertEquals(0, responseVo.getStatus());
        assertNotNull(responseVo.getData());

        // 验证数据库记录
        PayInfo payInfo = payInfoMapper.selectByOrderNo(orderNo);
        assertNotNull(payInfo);
        assertEquals(orderNo, payInfo.getOrderNo());
        assertEquals(userId, payInfo.getUserId());
    }

    @Test
    @Order(2)
    @DisplayName("测试支付状态查询")
    void testQueryPayStatus() {
        // 先创建支付
        payService.create(userId, orderNo);

        // 查询支付状态
        ResponseVo responseVo = payService.queryByOrderNo(orderNo, userId);
        
        // 验证响应
        assertEquals(0, responseVo.getStatus());
        assertNotNull(responseVo.getData());
    }

    @Test
    @Order(3)
    @DisplayName("测试支付回调处理")
    void testPayNotify() {
        // 先创建支付
        payService.create(userId, orderNo);

        // 模拟支付平台回调
        String platformNumber = "TEST_" + System.currentTimeMillis();
        PayInfo payInfo = new PayInfo();
        payInfo.setPlatformNumber(platformNumber);
        payInfo.setOrderNo(orderNo);
        payInfo.setPlatformStatus("TRADE_SUCCESS");
        
        // 处理支付通知
        ResponseVo responseVo = payService.notify(platformNumber, payInfo);
        
        // 验证响应
        assertEquals(0, responseVo.getStatus());

        // 验证订单状态
        Order order = orderMapper.selectByOrderNo(orderNo);
        assertEquals(OrderStatusEnum.PAID.getCode(), order.getStatus());

        // 验证支付信息
        PayInfo updatedPayInfo = payInfoMapper.selectByOrderNo(orderNo);
        assertEquals("TRADE_SUCCESS", updatedPayInfo.getPlatformStatus());
        assertEquals(platformNumber, updatedPayInfo.getPlatformNumber());
    }

    @Test
    @Order(4)
    @DisplayName("测试无效订单支付")
    void testInvalidOrderPay() {
        // 使用不存在的订单号
        Long invalidOrderNo = 999999L;
        
        // 尝试创建支付
        ResponseVo responseVo = payService.create(userId, invalidOrderNo);
        
        // 验证响应
        assertNotEquals(0, responseVo.getStatus());
    }

    @Test
    @Order(5)
    @DisplayName("测试重复支付")
    void testDuplicatePay() {
        // 第一次创建支付
        ResponseVo firstResponse = payService.create(userId, orderNo);
        assertEquals(0, firstResponse.getStatus());

        // 尝试重复创建支付
        ResponseVo secondResponse = payService.create(userId, orderNo);
        assertNotEquals(0, secondResponse.getStatus());
    }

    @Test
    @Order(6)
    @DisplayName("测试支付超时")
    void testPayTimeout() {
        // 创建一个过期订单
        Order expiredOrder = new Order();
        expiredOrder.setUserId(userId);
        expiredOrder.setOrderNo(System.currentTimeMillis());
        expiredOrder.setPaymentType(PaymentTypeEnum.PAY_ONLINE.getCode());
        expiredOrder.setPostage(0);
        expiredOrder.setPayment(new BigDecimal("100.00"));
        expiredOrder.setStatus(OrderStatusEnum.CANCELED.getCode());
        expiredOrder.setCreateTime(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)); // 24小时前
        orderMapper.insertSelective(expiredOrder);

        // 尝试创建支付
        ResponseVo responseVo = payService.create(userId, expiredOrder.getOrderNo());
        
        // 验证响应
        assertNotEquals(0, responseVo.getStatus());
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        orderMapper.deleteByPrimaryKey(orderNo);
        payInfoMapper.deleteByOrderNo(orderNo);
    }
}
