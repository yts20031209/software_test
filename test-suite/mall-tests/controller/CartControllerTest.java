package com.imooc.mall.controller;

/**
 * 购物车控制器测试类
 * 对应测试类: com.imooc.mall.controller.CartController
 * 测试内容：
 * 1. 添加商品到购物车接口
 * 2. 获取购物车列表接口
 * 3. 更新购物车商品数量接口
 * 4. 删除购物车商品接口
 * 5. 全选/取消全选购物车商品接口
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.mall.form.CartAddForm;
import com.imooc.mall.form.CartUpdateForm;
import com.imooc.mall.pojo.User;
import com.imooc.mall.service.impl.CartServiceImpl;
import com.imooc.mall.vo.CartVo;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartServiceImpl cartService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private CartAddForm cartAddForm;
    private CartUpdateForm cartUpdateForm;
    private CartVo cartVo;

    @BeforeEach
    void setUp() {
        // 初始化测试用户
        user = new User();
        user.setId(1);
        user.setUsername("testuser");

        // 初始化添加购物车表单
        cartAddForm = new CartAddForm();
        cartAddForm.setProductId(1);
        cartAddForm.setSelected(true);

        // 初始化更新购物车表单
        cartUpdateForm = new CartUpdateForm();
        cartUpdateForm.setQuantity(2);
        cartUpdateForm.setSelected(true);

        // 初始化购物车返回对象
        cartVo = new CartVo();
        cartVo.setCartTotalPrice(new BigDecimal("9999.00"));
        cartVo.setCartTotalQuantity(2);
    }

    @Test
    @DisplayName("测试添加商品到购物车 - 成功场景")
    void testAdd_Success() throws Exception {
        // 模拟服务层返回
        when(cartService.add(anyInt(), any(CartAddForm.class))).thenReturn(ResponseVo.success(cartVo));

        // 执行测试
        mockMvc.perform(post("/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartAddForm))
                .sessionAttr(MallConst.CURRENT_USER, user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0))
                .andExpect(jsonPath("$.data.cartTotalPrice").value(9999.00));
    }

    @Test
    @DisplayName("测试获取购物车列表 - 成功场景")
    void testList_Success() throws Exception {
        // 模拟服务层返回
        when(cartService.list(anyInt())).thenReturn(ResponseVo.success(cartVo));

        // 执行测试
        mockMvc.perform(get("/carts")
                .sessionAttr(MallConst.CURRENT_USER, user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0))
                .andExpect(jsonPath("$.data.cartTotalQuantity").value(2));
    }

    @Test
    @DisplayName("测试更新购物车商品数量 - 成功场景")
    void testUpdate_Success() throws Exception {
        // 模拟服务层返回
        when(cartService.update(anyInt(), anyInt(), any(CartUpdateForm.class)))
                .thenReturn(ResponseVo.success(cartVo));

        // 执行测试
        mockMvc.perform(put("/carts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartUpdateForm))
                .sessionAttr(MallConst.CURRENT_USER, user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0));
    }

    @Test
    @DisplayName("测试删除购物车商品 - 成功场景")
    void testDelete_Success() throws Exception {
        // 模拟服务层返回
        when(cartService.delete(anyInt(), anyInt())).thenReturn(ResponseVo.success(cartVo));

        // 执行测试
        mockMvc.perform(delete("/carts/1")
                .sessionAttr(MallConst.CURRENT_USER, user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0));
    }

    @Test
    @DisplayName("测试全选购物车商品 - 成功场景")
    void testSelectAll_Success() throws Exception {
        // 模拟服务层返回
        when(cartService.selectAll(anyInt())).thenReturn(ResponseVo.success(cartVo));

        // 执行测试
        mockMvc.perform(put("/carts/selectAll")
                .sessionAttr(MallConst.CURRENT_USER, user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0));
    }

    @Test
    @DisplayName("测试取消全选购物车商品 - 成功场景")
    void testUnSelectAll_Success() throws Exception {
        // 模拟服务层返回
        when(cartService.unSelectAll(anyInt())).thenReturn(ResponseVo.success(cartVo));

        // 执行测试
        mockMvc.perform(put("/carts/unSelectAll")
                .sessionAttr(MallConst.CURRENT_USER, user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0));
    }

    @Test
    @DisplayName("测试获取购物车商品数量 - 成功场景")
    void testSum_Success() throws Exception {
        // 模拟服务层返回
        when(cartService.sum(anyInt())).thenReturn(ResponseVo.success(2));

        // 执行测试
        mockMvc.perform(get("/carts/products/sum")
                .sessionAttr(MallConst.CURRENT_USER, user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0))
                .andExpect(jsonPath("$.data").value(2));
    }
}
