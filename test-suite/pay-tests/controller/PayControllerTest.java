package com.imooc.pay.controller;

/**
 * 支付控制器测试类
 * 对应测试类: com.imooc.pay.controller.PayController
 * 测试内容：
 * 1. 创建支付订单接口
 * 2. 异步通知处理接口
 * 3. 支付查询接口
 */

import com.imooc.pay.service.impl.PayServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PayServiceImpl payService;

    @Test
    @DisplayName("测试创建支付 - 成功场景")
    void testCreate() throws Exception {
        // 准备测试数据
        Long orderId = 12345L;
        Double amount = 100.00;

        // 执行测试
        mockMvc.perform(post("/pay/create")
                .param("orderId", orderId.toString())
                .param("amount", amount.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // 验证服务调用
        verify(payService, times(1)).create(eq(orderId), eq(amount));
    }

    @Test
    @DisplayName("测试支付异步通知 - 成功场景")
    void testAsyncNotify() throws Exception {
        // 准备测试数据
        String notifyData = "{\"orderId\":\"12345\",\"status\":\"SUCCESS\"}";

        // 执行测试
        mockMvc.perform(post("/pay/notify")
                .content(notifyData)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 验证服务调用
        verify(payService, times(1)).handleAsyncNotify(eq(notifyData));
    }

    @Test
    @DisplayName("测试支付查询 - 成功场景")
    void testQuery() throws Exception {
        // 准备测试数据
        Long orderId = 12345L;

        // 执行测试
        mockMvc.perform(get("/pay/query")
                .param("orderId", orderId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // 验证服务调用
        verify(payService, times(1)).query(eq(orderId));
    }

    @Test
    @DisplayName("测试创建支付 - 参数验证失败场景")
    void testCreate_ValidationFail() throws Exception {
        // 执行测试 - 缺少必要参数
        mockMvc.perform(post("/pay/create")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // 验证服务未被调用
        verify(payService, never()).create(any(), any());
    }
}
