package com.imooc.mall.dao;

import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.pojo.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class OrderMapperTest extends MallApplicationTests {

    @Autowired
    private OrderMapper orderMapper;

    @Test
    @DisplayName("测试创建订单")
    void testInsertOrder() {
        Order order = new Order();
        order.setOrderNo(12345678L);
        order.setUserId(1);
        order.setShippingId(1);
        order.setPayment(new BigDecimal("9999.00"));
        order.setPaymentType(1);
        order.setPostage(0);
        order.setStatus(10);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());

        int result = orderMapper.insertSelective(order);
        assertNotNull(order.getId());
        assertEquals(1, result);
    }

    @Test
    @DisplayName("测试根据订单号查询订单")
    void testSelectByOrderNo() {
        // 先插入测试数据
        Order order = new Order();
        order.setOrderNo(12345678L);
        order.setUserId(1);
        order.setShippingId(1);
        order.setPayment(new BigDecimal("9999.00"));
        order.setPaymentType(1);
        order.setPostage(0);
        order.setStatus(10);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        orderMapper.insertSelective(order);

        // 测试查询
        Order result = orderMapper.selectByOrderNo(12345678L);
        assertNotNull(result);
        assertEquals(12345678L, result.getOrderNo().longValue());
        assertEquals(0, result.getPayment().compareTo(new BigDecimal("9999.00")));
    }

    @Test
    @DisplayName("测试根据用户ID查询订单列表")
    void testSelectByUserId() {
        // 先插入测试数据
        Order order1 = new Order();
        order1.setOrderNo(12345678L);
        order1.setUserId(1);
        order1.setShippingId(1);
        order1.setPayment(new BigDecimal("9999.00"));
        order1.setStatus(10);
        order1.setCreateTime(new Date());
        order1.setUpdateTime(new Date());
        orderMapper.insertSelective(order1);

        Order order2 = new Order();
        order2.setOrderNo(87654321L);
        order2.setUserId(1);
        order2.setShippingId(1);
        order2.setPayment(new BigDecimal("8888.00"));
        order2.setStatus(10);
        order2.setCreateTime(new Date());
        order2.setUpdateTime(new Date());
        orderMapper.insertSelective(order2);

        // 测试查询
        List<Order> results = orderMapper.selectByUserId(1);
        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(o -> o.getOrderNo().equals(12345678L)));
        assertTrue(results.stream().anyMatch(o -> o.getOrderNo().equals(87654321L)));
    }

    @Test
    @DisplayName("测试更新订单状态")
    void testUpdateOrderStatus() {
        // 先插入测试数据
        Order order = new Order();
        order.setOrderNo(12345678L);
        order.setUserId(1);
        order.setShippingId(1);
        order.setPayment(new BigDecimal("9999.00"));
        order.setStatus(10);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        orderMapper.insertSelective(order);

        // 更新订单状态
        order.setStatus(20);
        int result = orderMapper.updateByPrimaryKeySelective(order);

        // 验证更新结果
        assertEquals(1, result);
        Order updated = orderMapper.selectByPrimaryKey(order.getId());
        assertEquals(20, updated.getStatus());
    }

    @Test
    @DisplayName("测试删除订单")
    void testDeleteOrder() {
        // 先插入测试数据
        Order order = new Order();
        order.setOrderNo(12345678L);
        order.setUserId(1);
        order.setShippingId(1);
        order.setPayment(new BigDecimal("9999.00"));
        order.setStatus(10);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        orderMapper.insertSelective(order);

        // 删除订单
        int result = orderMapper.deleteByPrimaryKey(order.getId());

        // 验证删除结果
        assertEquals(1, result);
        Order deleted = orderMapper.selectByPrimaryKey(order.getId());
        assertNull(deleted);
    }

    @Test
    @DisplayName("测试查询用户订单数量")
    void testSelectOrderCount() {
        // 先插入测试数据
        Order order1 = new Order();
        order1.setOrderNo(12345678L);
        order1.setUserId(1);
        order1.setShippingId(1);
        order1.setPayment(new BigDecimal("9999.00"));
        order1.setStatus(10);
        order1.setCreateTime(new Date());
        order1.setUpdateTime(new Date());
        orderMapper.insertSelective(order1);

        Order order2 = new Order();
        order2.setOrderNo(87654321L);
        order2.setUserId(1);
        order2.setShippingId(1);
        order2.setPayment(new BigDecimal("8888.00"));
        order2.setStatus(10);
        order2.setCreateTime(new Date());
        order2.setUpdateTime(new Date());
        orderMapper.insertSelective(order2);

        // 测试查询订单数量
        int count = orderMapper.selectCountByUserId(1);
        assertEquals(2, count);
    }

    @Test
    @DisplayName("测试查询不同状态的订单")
    void testSelectByStatus() {
        // 先插入测试数据
        Order order1 = new Order();
        order1.setOrderNo(12345678L);
        order1.setUserId(1);
        order1.setShippingId(1);
        order1.setPayment(new BigDecimal("9999.00"));
        order1.setStatus(10);
        order1.setCreateTime(new Date());
        order1.setUpdateTime(new Date());
        orderMapper.insertSelective(order1);

        Order order2 = new Order();
        order2.setOrderNo(87654321L);
        order2.setUserId(1);
        order2.setShippingId(1);
        order2.setPayment(new BigDecimal("8888.00"));
        order2.setStatus(20);
        order2.setCreateTime(new Date());
        order2.setUpdateTime(new Date());
        orderMapper.insertSelective(order2);

        // 测试查询未支付订单
        List<Order> unpaidOrders = orderMapper.selectByUserIdAndStatus(1, 10);
        assertEquals(1, unpaidOrders.size());
        assertEquals(12345678L, unpaidOrders.get(0).getOrderNo().longValue());

        // 测试查询已支付订单
        List<Order> paidOrders = orderMapper.selectByUserIdAndStatus(1, 20);
        assertEquals(1, paidOrders.size());
        assertEquals(87654321L, paidOrders.get(0).getOrderNo().longValue());
    }
}
