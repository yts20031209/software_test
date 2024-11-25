package com.imooc.mall.service;

/**
 * 购物车服务测试类
 * 对应测试类: com.imooc.mall.service.impl.CartServiceImpl
 * 测试内容：
 * 1. 添加商品到购物车
 * 2. 更新购物车商品数量
 * 3. 删除购物车商品
 * 4. 购物车列表查询
 * 5. 全选/取消全选
 */

import com.imooc.mall.dao.ProductMapper;
import com.imooc.mall.pojo.Cart;
import com.imooc.mall.pojo.Product;
import com.imooc.mall.service.impl.CartServiceImpl;
import com.imooc.mall.vo.CartProductVo;
import com.imooc.mall.vo.CartVo;
import com.imooc.mall.vo.ResponseVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CartServiceTest {

    @Autowired
    private CartServiceImpl cartService;

    @MockBean
    private ProductMapper productMapper;

    @MockBean
    private StringRedisTemplate redisTemplate;

    @MockBean
    private HashOperations<String, String, String> hashOperations;

    private Integer uid = 1;
    private Product mockProduct;
    private Cart mockCart;

    @BeforeEach
    void setUp() {
        // 初始化测试商品数据
        mockProduct = new Product();
        mockProduct.setId(1);
        mockProduct.setName("测试商品");
        mockProduct.setPrice(new BigDecimal("999.99"));
        mockProduct.setStock(100);
        mockProduct.setStatus(1);

        // 初始化测试购物车数据
        mockCart = new Cart();
        mockCart.setProductId(1);
        mockCart.setQuantity(2);
        mockCart.setSelected(true);

        // 模拟Redis操作
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    @DisplayName("测试添加商品到购物车 - 成功场景")
    void testAdd_Success() {
        // 模拟商品存在
        when(productMapper.selectByPrimaryKey(mockProduct.getId())).thenReturn(mockProduct);
        
        // 模拟Redis操作
        Map<String, String> cartMap = new HashMap<>();
        when(hashOperations.entries(any())).thenReturn(cartMap);

        // 执行测试
        ResponseVo<CartVo> response = cartService.add(uid, mockCart);

        // 验证结果
        assertNotNull(response);
        assertEquals(0, response.getStatus());
        verify(hashOperations, times(1)).put(any(), any(), any());
    }

    @Test
    @DisplayName("测试更新购物车商品数量 - 成功场景")
    void testUpdate_Success() {
        // 模拟购物车中已有商品
        Map<String, String> cartMap = new HashMap<>();
        cartMap.put(mockCart.getProductId().toString(), "2,true"); // quantity=2, selected=true
        when(hashOperations.entries(any())).thenReturn(cartMap);

        // 执行测试 - 更新数量为3
        mockCart.setQuantity(3);
        ResponseVo<CartVo> response = cartService.update(uid, mockCart.getProductId(), mockCart);

        // 验证结果
        assertNotNull(response);
        assertEquals(0, response.getStatus());
        verify(hashOperations, times(1)).put(any(), any(), any());
    }

    @Test
    @DisplayName("测试删除购物车商品 - 成功场景")
    void testDelete_Success() {
        // 模拟购物车中已有商品
        Map<String, String> cartMap = new HashMap<>();
        cartMap.put(mockCart.getProductId().toString(), "2,true");
        when(hashOperations.entries(any())).thenReturn(cartMap);

        // 执行测试
        ResponseVo<CartVo> response = cartService.delete(uid, mockCart.getProductId());

        // 验证结果
        assertNotNull(response);
        assertEquals(0, response.getStatus());
        verify(hashOperations, times(1)).delete(any(), any());
    }

    @Test
    @DisplayName("测试查询购物车列表 - 成功场景")
    void testList_Success() {
        // 模拟购物车数据
        Map<String, String> cartMap = new HashMap<>();
        cartMap.put(mockCart.getProductId().toString(), "2,true");
        when(hashOperations.entries(any())).thenReturn(cartMap);

        // 模拟商品数据
        when(productMapper.selectByPrimaryKey(mockCart.getProductId())).thenReturn(mockProduct);

        // 执行测试
        ResponseVo<CartVo> response = cartService.list(uid);

        // 验证结果
        assertNotNull(response);
        assertEquals(0, response.getStatus());
        CartVo cartVo = response.getData();
        assertNotNull(cartVo);
        assertFalse(cartVo.getCartProductVoList().isEmpty());
    }

    @Test
    @DisplayName("测试全选购物车商品 - 成功场景")
    void testSelectAll_Success() {
        // 模拟购物车数据
        Map<String, String> cartMap = new HashMap<>();
        cartMap.put("1", "2,false");
        cartMap.put("2", "1,false");
        when(hashOperations.entries(any())).thenReturn(cartMap);

        // 执行测试
        ResponseVo<CartVo> response = cartService.selectAll(uid);

        // 验证结果
        assertNotNull(response);
        assertEquals(0, response.getStatus());
        verify(hashOperations, times(2)).put(any(), any(), any());
    }

    @Test
    @DisplayName("测试取消全选购物车商品 - 成功场景")
    void testUnSelectAll_Success() {
        // 模拟购物车数据
        Map<String, String> cartMap = new HashMap<>();
        cartMap.put("1", "2,true");
        cartMap.put("2", "1,true");
        when(hashOperations.entries(any())).thenReturn(cartMap);

        // 执行测试
        ResponseVo<CartVo> response = cartService.unSelectAll(uid);

        // 验证结果
        assertNotNull(response);
        assertEquals(0, response.getStatus());
        verify(hashOperations, times(2)).put(any(), any(), any());
    }

    @Test
    @DisplayName("测试获取购物车商品总数")
    void testSum_Success() {
        // 模拟购物车数据
        Map<String, String> cartMap = new HashMap<>();
        cartMap.put("1", "2,true"); // 2件商品
        cartMap.put("2", "3,true"); // 3件商品
        when(hashOperations.entries(any())).thenReturn(cartMap);

        // 执行测试
        ResponseVo<Integer> response = cartService.sum(uid);

        // 验证结果
        assertNotNull(response);
        assertEquals(0, response.getStatus());
        assertEquals(5, response.getData()); // 总共5件商品
    }

    @Test
    @DisplayName("测试添加超出库存的商品数量")
    void testAdd_ExceedStock() {
        // 模拟商品存在但库存不足
        mockProduct.setStock(1); // 库存只有1个
        when(productMapper.selectByPrimaryKey(mockProduct.getId())).thenReturn(mockProduct);

        // 尝试添加2个商品
        mockCart.setQuantity(2);
        ResponseVo<CartVo> response = cartService.add(uid, mockCart);

        // 验证结果
        assertNotNull(response);
        assertNotEquals(0, response.getStatus());
        verify(hashOperations, never()).put(any(), any(), any());
    }
}
