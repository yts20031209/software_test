package com.imooc.mall.controller;

/**
 * 订单控制器测试类
 * 对应测试类: com.imooc.mall.controller.OrderController
 * 测试内容：
 * 1. 创建订单接口
 * 2. 订单列表查询接口
 * 3. 订单详情查询接口
 * 4. 取消订单接口
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.mall.form.OrderCreateForm;
import com.imooc.mall.pojo.User;
import com.imooc.mall.service.impl.OrderServiceImpl;
import com.imooc.mall.vo.OrderVo;
import com.imooc.mall.vo.ResponseVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderServiceImpl orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private OrderCreateForm orderForm;

    @BeforeEach
    void setUp() {
        // 初始化测试用户
        user = new User();
        user.setId(1);
        user.setUsername("testuser");

        // 初始化订单创建表单
        orderForm = new OrderCreateForm();
        orderForm.setShippingId(1);
    }

    @Test
    @DisplayName("测试创建订单 - 成功场景")
    void testCreate_Success() throws Exception {
        // 准备测试数据
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(12345678L);
        orderVo.setPayment(new BigDecimal("9999.00"));

        // 模拟服务层返回
        when(orderService.create(anyInt(), anyInt())).thenReturn(ResponseVo.success(orderVo));

        // 执行测试
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderForm))
                .sessionAttr(MallConst.CURRENT_USER, user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0))
                .andExpect(jsonPath("$.data.orderNo").value(12345678));
    }

    @Test
    @DisplayName("测试查询订单列表 - 成功场景")
    void testList_Success() throws Exception {
        // 准备测试数据
        OrderVo order1 = new OrderVo();
        order1.setOrderNo(12345678L);
        OrderVo order2 = new OrderVo();
        order2.setOrderNo(87654321L);
        List<OrderVo> orderList = Arrays.asList(order1, order2);

        // 模拟服务层返回
        when(orderService.list(anyInt(), any())).thenReturn(ResponseVo.success(orderList));

        // 执行测试
        mockMvc.perform(get("/orders")
                .sessionAttr(MallConst.CURRENT_USER, user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0))
                .andExpect(jsonPath("$.data[0].orderNo").value(12345678))
                .andExpect(jsonPath("$.data[1].orderNo").value(87654321));
    }

    @Test
    @DisplayName("测试查询订单详情 - 成功场景")
    void testDetail_Success() throws Exception {
        // 准备测试数据
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(12345678L);
        orderVo.setPayment(new BigDecimal("9999.00"));

        // 模拟服务层返回
        when(orderService.detail(anyInt(), anyInt())).thenReturn(ResponseVo.success(orderVo));

        // 执行测试
        mockMvc.perform(get("/orders/1")
                .sessionAttr(MallConst.CURRENT_USER, user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0))
                .andExpect(jsonPath("$.data.orderNo").value(12345678));
    }

    @Test
    @DisplayName("测试取消订单 - 成功场景")
    void testCancel_Success() throws Exception {
        // 模拟服务层返回
        when(orderService.cancel(anyInt(), anyInt())).thenReturn(ResponseVo.success());

        // 执行测试
        mockMvc.perform(put("/orders/1/cancel")
                .sessionAttr(MallConst.CURRENT_USER, user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0));
    }

    @Test
    @DisplayName("测试创建订单 - 收货地址不存在")
    void testCreate_ShippingNotExist() throws Exception {
        // 模拟服务层返回收货地址不存在错误
        when(orderService.create(anyInt(), anyInt()))
                .thenReturn(ResponseVo.error(ResponseEnum.SHIPPING_NOT_EXIST));

        // 执行测试
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderForm))
                .sessionAttr(MallConst.CURRENT_USER, user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ResponseEnum.SHIPPING_NOT_EXIST.getCode()));
    }
}
