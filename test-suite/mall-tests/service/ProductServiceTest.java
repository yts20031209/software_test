package com.imooc.mall.service;

/**
 * 商品服务测试类
 * 对应测试类: com.imooc.mall.service.impl.ProductServiceImpl
 * 测试内容：
 * 1. 商品列表查询
 * 2. 商品详情查询
 * 3. 商品分类查询
 * 4. 商品状态验证
 */

import com.github.pagehelper.PageInfo;
import com.imooc.mall.dao.ProductMapper;
import com.imooc.mall.pojo.Product;
import com.imooc.mall.service.impl.ProductServiceImpl;
import com.imooc.mall.vo.ProductDetailVo;
import com.imooc.mall.vo.ResponseVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductServiceImpl productService;

    @MockBean
    private ProductMapper productMapper;

    @MockBean
    private CategoryService categoryService;

    private Product mockProduct;
    private List<Product> mockProducts;

    @BeforeEach
    void setUp() {
        // 初始化测试商品数据
        mockProduct = new Product();
        mockProduct.setId(1);
        mockProduct.setCategoryId(100);
        mockProduct.setName("测试商品");
        mockProduct.setSubtitle("商品副标题");
        mockProduct.setMainImage("main.jpg");
        mockProduct.setPrice(new BigDecimal("999.99"));
        mockProduct.setStock(100);
        mockProduct.setStatus(1);

        Product product2 = new Product();
        product2.setId(2);
        product2.setCategoryId(100);
        product2.setName("测试商品2");
        product2.setPrice(new BigDecimal("888.88"));
        product2.setStock(50);
        product2.setStatus(1);

        mockProducts = Arrays.asList(mockProduct, product2);
    }

    @Test
    @DisplayName("测试获取商品列表 - 成功场景")
    void testList_Success() {
        // 模拟分类服务返回分类ID集合
        Set<Integer> categoryIds = Set.of(100, 101, 102);
        when(categoryService.findSubCategoryId(any())).thenReturn(categoryIds);
        
        // 模拟数据库查询返回商品列表
        when(productMapper.selectByCategoryIds(any())).thenReturn(mockProducts);

        // 执行测试
        ResponseVo<PageInfo> response = productService.list(100, 1, 10);

        // 验证结果
        assertNotNull(response);
        assertEquals(0, response.getStatus());
        PageInfo pageInfo = response.getData();
        assertNotNull(pageInfo);
        assertEquals(2, pageInfo.getList().size());
    }

    @Test
    @DisplayName("测试获取商品详情 - 成功场景")
    void testDetail_Success() {
        // 模拟数据库查询返回商品
        when(productMapper.selectByPrimaryKey(1)).thenReturn(mockProduct);

        // 执行测试
        ResponseVo<ProductDetailVo> response = productService.detail(1);

        // 验证结果
        assertNotNull(response);
        assertEquals(0, response.getStatus());
        ProductDetailVo productDetailVo = response.getData();
        assertNotNull(productDetailVo);
        assertEquals(mockProduct.getName(), productDetailVo.getName());
        assertEquals(mockProduct.getSubtitle(), productDetailVo.getSubtitle());
        assertEquals(mockProduct.getMainImage(), productDetailVo.getMainImage());
        assertEquals(mockProduct.getPrice(), productDetailVo.getPrice());
        assertEquals(mockProduct.getStock(), productDetailVo.getStock());
        assertEquals(mockProduct.getStatus(), productDetailVo.getStatus());
    }

    @Test
    @DisplayName("测试获取商品详情 - 商品不存在")
    void testDetail_ProductNotFound() {
        // 模拟数据库查询返回空
        when(productMapper.selectByPrimaryKey(any())).thenReturn(null);

        // 执行测试
        ResponseVo<ProductDetailVo> response = productService.detail(999);

        // 验证结果
        assertNotNull(response);
        assertNotEquals(0, response.getStatus());
    }

    @Test
    @DisplayName("测试获取商品详情 - 商品已下架")
    void testDetail_ProductOffShelf() {
        // 修改商品状态为下架
        mockProduct.setStatus(0);
        when(productMapper.selectByPrimaryKey(1)).thenReturn(mockProduct);

        // 执行测试
        ResponseVo<ProductDetailVo> response = productService.detail(1);

        // 验证结果
        assertNotNull(response);
        assertNotEquals(0, response.getStatus());
    }

    @Test
    @DisplayName("测试商品分页")
    void testProductPagination() {
        // 创建超过一页的商品数据
        List<Product> manyProducts = Arrays.asList(
            mockProduct,
            mockProduct,
            mockProduct,
            mockProduct,
            mockProduct
        );

        // 模拟分类服务和数据库查询
        when(categoryService.findSubCategoryId(any())).thenReturn(Set.of(100));
        when(productMapper.selectByCategoryIds(any())).thenReturn(manyProducts);

        // 测试第一页
        ResponseVo<PageInfo> response1 = productService.list(100, 1, 2);
        assertEquals(2, response1.getData().getPageSize());
        assertEquals(1, response1.getData().getPageNum());

        // 测试第二页
        ResponseVo<PageInfo> response2 = productService.list(100, 2, 2);
        assertEquals(2, response2.getData().getPageSize());
        assertEquals(2, response2.getData().getPageNum());
    }

    @Test
    @DisplayName("测试空分类商品查询")
    void testEmptyCategory() {
        // 模拟空分类
        when(categoryService.findSubCategoryId(any())).thenReturn(Set.of());

        // 执行测试
        ResponseVo<PageInfo> response = productService.list(999, 1, 10);

        // 验证结果
        assertNotNull(response);
        assertEquals(0, response.getStatus());
        assertTrue(response.getData().getList().isEmpty());
    }
}
