package com.imooc.mall.dao;

import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.pojo.Cart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class CartMapperTest extends MallApplicationTests {

    @Autowired
    private CartMapper cartMapper;

    @Test
    @DisplayName("测试添加购物车项")
    void testInsertCart() {
        Cart cart = new Cart();
        cart.setUserId(1);
        cart.setProductId(1);
        cart.setQuantity(2);
        cart.setSelected(true);
        cart.setCreateTime(new Date());
        cart.setUpdateTime(new Date());

        int result = cartMapper.insertSelective(cart);
        assertNotNull(cart.getId());
        assertEquals(1, result);
    }

    @Test
    @DisplayName("测试根据用户ID和产品ID查询购物车")
    void testSelectByUserIdAndProductId() {
        // 先插入测试数据
        Cart cart = new Cart();
        cart.setUserId(1);
        cart.setProductId(1);
        cart.setQuantity(2);
        cart.setSelected(true);
        cart.setCreateTime(new Date());
        cart.setUpdateTime(new Date());
        cartMapper.insertSelective(cart);

        // 测试查询
        Cart result = cartMapper.selectByUserIdAndProductId(1, 1);
        assertNotNull(result);
        assertEquals(2, result.getQuantity());
        assertTrue(result.getSelected());
    }

    @Test
    @DisplayName("测试查询用户的购物车列表")
    void testSelectByUserId() {
        // 插入多个购物车项
        Cart cart1 = new Cart();
        cart1.setUserId(1);
        cart1.setProductId(1);
        cart1.setQuantity(2);
        cart1.setSelected(true);
        cart1.setCreateTime(new Date());
        cart1.setUpdateTime(new Date());
        cartMapper.insertSelective(cart1);

        Cart cart2 = new Cart();
        cart2.setUserId(1);
        cart2.setProductId(2);
        cart2.setQuantity(1);
        cart2.setSelected(true);
        cart2.setCreateTime(new Date());
        cart2.setUpdateTime(new Date());
        cartMapper.insertSelective(cart2);

        // 测试查询
        List<Cart> carts = cartMapper.selectByUserId(1);
        assertNotNull(carts);
        assertEquals(2, carts.size());
    }

    @Test
    @DisplayName("测试查询用户选中的购物车项")
    void testSelectCheckedByUserId() {
        // 插入选中和未选中的购物车项
        Cart cart1 = new Cart();
        cart1.setUserId(1);
        cart1.setProductId(1);
        cart1.setQuantity(2);
        cart1.setSelected(true);
        cart1.setCreateTime(new Date());
        cart1.setUpdateTime(new Date());
        cartMapper.insertSelective(cart1);

        Cart cart2 = new Cart();
        cart2.setUserId(1);
        cart2.setProductId(2);
        cart2.setQuantity(1);
        cart2.setSelected(false);
        cart2.setCreateTime(new Date());
        cart2.setUpdateTime(new Date());
        cartMapper.insertSelective(cart2);

        // 测试查询选中的购物车项
        List<Cart> selectedCarts = cartMapper.selectByUserIdAndSelected(1, true);
        assertNotNull(selectedCarts);
        assertEquals(1, selectedCarts.size());
        assertTrue(selectedCarts.get(0).getSelected());
    }

    @Test
    @DisplayName("测试更新购物车商品数量")
    void testUpdateQuantity() {
        // 先插入测试数据
        Cart cart = new Cart();
        cart.setUserId(1);
        cart.setProductId(1);
        cart.setQuantity(2);
        cart.setSelected(true);
        cart.setCreateTime(new Date());
        cart.setUpdateTime(new Date());
        cartMapper.insertSelective(cart);

        // 更新数量
        cart.setQuantity(3);
        cart.setUpdateTime(new Date());
        int result = cartMapper.updateByPrimaryKeySelective(cart);

        // 验证更新结果
        assertEquals(1, result);
        Cart updated = cartMapper.selectByPrimaryKey(cart.getId());
        assertEquals(3, updated.getQuantity());
    }

    @Test
    @DisplayName("测试更新购物车选中状态")
    void testUpdateSelected() {
        // 先插入测试数据
        Cart cart = new Cart();
        cart.setUserId(1);
        cart.setProductId(1);
        cart.setQuantity(2);
        cart.setSelected(true);
        cart.setCreateTime(new Date());
        cart.setUpdateTime(new Date());
        cartMapper.insertSelective(cart);

        // 更新选中状态
        cart.setSelected(false);
        cart.setUpdateTime(new Date());
        int result = cartMapper.updateByPrimaryKeySelective(cart);

        // 验证更新结果
        assertEquals(1, result);
        Cart updated = cartMapper.selectByPrimaryKey(cart.getId());
        assertFalse(updated.getSelected());
    }

    @Test
    @DisplayName("测试删除购物车项")
    void testDeleteCart() {
        // 先插入测试数据
        Cart cart = new Cart();
        cart.setUserId(1);
        cart.setProductId(1);
        cart.setQuantity(2);
        cart.setSelected(true);
        cart.setCreateTime(new Date());
        cart.setUpdateTime(new Date());
        cartMapper.insertSelective(cart);

        // 删除购物车项
        int result = cartMapper.deleteByPrimaryKey(cart.getId());

        // 验证删除结果
        assertEquals(1, result);
        Cart deleted = cartMapper.selectByPrimaryKey(cart.getId());
        assertNull(deleted);
    }

    @Test
    @DisplayName("测试批量删除购物车项")
    void testDeleteBatch() {
        // 插入多个购物车项
        Cart cart1 = new Cart();
        cart1.setUserId(1);
        cart1.setProductId(1);
        cart1.setQuantity(2);
        cart1.setSelected(true);
        cart1.setCreateTime(new Date());
        cart1.setUpdateTime(new Date());
        cartMapper.insertSelective(cart1);

        Cart cart2 = new Cart();
        cart2.setUserId(1);
        cart2.setProductId(2);
        cart2.setQuantity(1);
        cart2.setSelected(true);
        cart2.setCreateTime(new Date());
        cart2.setUpdateTime(new Date());
        cartMapper.insertSelective(cart2);

        // 批量删除
        int result = cartMapper.deleteByUserIdAndProductIds(1, List.of(1, 2));

        // 验证删除结果
        assertEquals(2, result);
        List<Cart> remainingCarts = cartMapper.selectByUserId(1);
        assertTrue(remainingCarts.isEmpty());
    }

    @Test
    @DisplayName("测试清空用户购物车")
    void testClearCart() {
        // 插入多个购物车项
        Cart cart1 = new Cart();
        cart1.setUserId(1);
        cart1.setProductId(1);
        cart1.setQuantity(2);
        cart1.setSelected(true);
        cart1.setCreateTime(new Date());
        cart1.setUpdateTime(new Date());
        cartMapper.insertSelective(cart1);

        Cart cart2 = new Cart();
        cart2.setUserId(1);
        cart2.setProductId(2);
        cart2.setQuantity(1);
        cart2.setSelected(true);
        cart2.setCreateTime(new Date());
        cart2.setUpdateTime(new Date());
        cartMapper.insertSelective(cart2);

        // 清空购物车
        int result = cartMapper.deleteByUserId(1);

        // 验证清空结果
        assertTrue(result >= 2);
        List<Cart> remainingCarts = cartMapper.selectByUserId(1);
        assertTrue(remainingCarts.isEmpty());
    }
}
