package com.imooc.mall.controller;

/**
 * 商品控制器测试类
 * 对应测试类: com.imooc.mall.controller.ProductController
 * 测试内容：
 * 1. 商品列表查询接口
 * 2. 商品详情查询接口
 * 3. 商品分类查询接口
 */

import com.imooc.mall.service.impl.ProductServiceImpl;
import com.imooc.mall.vo.ProductDetailVo;
import com.imooc.mall.vo.ProductVo;
import com.imooc.mall.vo.ResponseVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductServiceImpl productService;

    @Test
    @DisplayName("测试获取商品列表 - 成功场景")
    void testList_Success() throws Exception {
        // 准备测试数据
        ProductVo product1 = new ProductVo();
        product1.setId(1);
        product1.setName("iPhone");
        product1.setPrice(new BigDecimal("6999.00"));
        
        ProductVo product2 = new ProductVo();
        product2.setId(2);
        product2.setName("MacBook");
        product2.setPrice(new BigDecimal("9999.00"));

        List<ProductVo> productList = Arrays.asList(product1, product2);

        // 模拟服务层返回
        when(productService.list(anyInt(), any())).thenReturn(ResponseVo.success(productList));

        // 执行测试
        mockMvc.perform(get("/products")
                .param("categoryId", "100001")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0))
                .andExpect(jsonPath("$.data[0].name").value("iPhone"))
                .andExpect(jsonPath("$.data[1].name").value("MacBook"));
    }

    @Test
    @DisplayName("测试获取商品详情 - 成功场景")
    void testDetail_Success() throws Exception {
        // 准备测试数据
        ProductDetailVo productDetail = new ProductDetailVo();
        productDetail.setId(1);
        productDetail.setName("iPhone");
        productDetail.setPrice(new BigDecimal("6999.00"));
        productDetail.setStock(100);
        productDetail.setStatus(1);

        // 模拟服务层返回
        when(productService.detail(anyInt())).thenReturn(ResponseVo.success(productDetail));

        // 执行测试
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0))
                .andExpect(jsonPath("$.data.name").value("iPhone"))
                .andExpect(jsonPath("$.data.stock").value(100));
    }

    @Test
    @DisplayName("测试获取商品详情 - 商品不存在")
    void testDetail_ProductNotExist() throws Exception {
        // 模拟服务层返回商品不存在错误
        when(productService.detail(anyInt())).thenReturn(ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST));

        // 执行测试
        mockMvc.perform(get("/products/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ResponseEnum.PRODUCT_NOT_EXIST.getCode()));
    }

    @Test
    @DisplayName("测试获取商品列表 - 分页参数验证")
    void testList_ValidationFail() throws Exception {
        // 测试无效的分页参数
        mockMvc.perform(get("/products")
                .param("categoryId", "100001")
                .param("pageNum", "0")
                .param("pageSize", "0"))
                .andExpect(status().isBadRequest());
    }
}
