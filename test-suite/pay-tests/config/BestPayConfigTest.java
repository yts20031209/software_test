package com.imooc.pay.config;

/**
 * 支付配置测试类
 * 对应测试类: com.imooc.pay.config.BestPayConfig
 * 测试内容：
 * 1. 支付宝配置初始化
 * 2. 微信支付配置初始化
 * 3. 最佳支付配置整合
 */

import com.lly835.bestpay.config.AliPayConfig;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BestPayConfigTest {

    @Autowired
    private BestPayConfig bestPayConfig;

    @MockBean
    private WxAccountConfig wxAccountConfig;

    @MockBean
    private AlipayAccountConfig alipayAccountConfig;

    @BeforeEach
    void setUp() {
        // 配置微信支付mock数据
        when(wxAccountConfig.getMchId()).thenReturn("test_mch_id");
        when(wxAccountConfig.getMchKey()).thenReturn("test_mch_key");
        when(wxAccountConfig.getNotifyUrl()).thenReturn("http://test.notify.url");
        
        // 配置支付宝mock数据
        when(alipayAccountConfig.getAppId()).thenReturn("test_app_id");
        when(alipayAccountConfig.getPrivateKey()).thenReturn("test_private_key");
        when(alipayAccountConfig.getPublicKey()).thenReturn("test_public_key");
        when(alipayAccountConfig.getNotifyUrl()).thenReturn("http://test.notify.url");
    }

    @Test
    @DisplayName("测试微信支付配置初始化")
    void testWxPayConfig() {
        WxPayConfig wxPayConfig = bestPayConfig.wxPayConfig();
        
        assertNotNull(wxPayConfig);
        assertEquals("test_mch_id", wxPayConfig.getMchId());
        assertEquals("test_mch_key", wxPayConfig.getMchKey());
        assertEquals("http://test.notify.url", wxPayConfig.getNotifyUrl());
    }

    @Test
    @DisplayName("测试支付宝配置初始化")
    void testAliPayConfig() {
        AliPayConfig aliPayConfig = bestPayConfig.aliPayConfig();
        
        assertNotNull(aliPayConfig);
        assertEquals("test_app_id", aliPayConfig.getAppId());
        assertEquals("test_private_key", aliPayConfig.getPrivateKey());
        assertEquals("test_public_key", aliPayConfig.getPublicKey());
        assertEquals("http://test.notify.url", aliPayConfig.getNotifyUrl());
    }

    @Test
    @DisplayName("测试最佳支付服务配置整合")
    void testBestPayService() {
        BestPayServiceImpl bestPayService = bestPayConfig.bestPayService();
        
        assertNotNull(bestPayService);
        // 验证配置是否正确注入
        verify(wxAccountConfig, times(1)).getMchId();
        verify(alipayAccountConfig, times(1)).getAppId();
    }
}
