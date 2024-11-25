package com.imooc.pay.service;

/**
 * 支付服务测试类
 * 对应测试类: com.imooc.pay.service.impl.PayServiceImpl
 * 测试内容：
 * 1. 支付创建功能
 * 2. 支付查询功能
 * 3. 异步通知处理功能
 */

import com.imooc.pay.PayApplication;
import com.imooc.pay.dao.PayInfoMapper;
import com.imooc.pay.enums.PayPlatformEnum;
import com.imooc.pay.pojo.PayInfo;
import com.imooc.pay.service.impl.PayServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = PayApplication.class)
public class PayServiceTest {

    @Autowired
    private PayServiceImpl payService;

    @MockBean
    private PayInfoMapper payInfoMapper;

    private PayInfo mockPayInfo;

    @BeforeEach
    void setUp() {
        // 测试数据初始化
        mockPayInfo = new PayInfo();
        mockPayInfo.setOrderNo(12345L);
        mockPayInfo.setPlatformStatus("SUCCESS");
        mockPayInfo.setPayPlatform(PayPlatformEnum.ALIPAY.getCode());
    }

    @Test
    @DisplayName("测试支付创建 - 成功场景")
    void testCreatePayment_Success() {
        // 准备测试数据
        Long orderId = 12345L;
        Double amount = 100.00;
        
        // 模拟数据库操作
        when(payInfoMapper.insertSelective(any(PayInfo.class))).thenReturn(1);
        
        // 执行测试
        assertDoesNotThrow(() -> {
            payService.create(orderId, amount);
        });
        
        // 验证调用
        verify(payInfoMapper, times(1)).insertSelective(any(PayInfo.class));
    }

    @Test
    @DisplayName("测试支付查询 - 成功场景")
    void testQueryPayment_Success() {
        // 准备测试数据
        Long orderNo = 12345L;
        
        // 模拟数据库查询返回
        when(payInfoMapper.selectByOrderNo(orderNo)).thenReturn(mockPayInfo);
        
        // 执行测试
        PayInfo result = payService.query(orderNo);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("SUCCESS", result.getPlatformStatus());
        assertEquals(PayPlatformEnum.ALIPAY.getCode(), result.getPayPlatform());
    }

    @Test
    @DisplayName("测试异步通知处理 - 成功场景")
    void testHandleAsyncNotify_Success() {
        // 模拟支付平台返回的通知数据
        String asyncNotify = "{\"orderId\":\"12345\",\"status\":\"SUCCESS\"}";
        
        // 模拟数据库更新
        when(payInfoMapper.updateByPrimaryKeySelective(any(PayInfo.class))).thenReturn(1);
        
        // 执行测试
        assertDoesNotThrow(() -> {
            payService.handleAsyncNotify(asyncNotify);
        });
        
        // 验证更新调用
        verify(payInfoMapper, times(1)).updateByPrimaryKeySelective(any(PayInfo.class));
    }
}
