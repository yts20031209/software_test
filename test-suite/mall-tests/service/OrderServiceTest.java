package com.imooc.mall.service;

/**
 * 订单服务测试类
 * 对应测试类: com.imooc.mall.service.impl.OrderServiceImpl
 * 测试内容：
 * 1. 创建订单
 * 2. 订单查询
 * 3. 订单取消
 * 4. 订单状态变更
 */

import com.github.pagehelper.PageInfo;
import com.imooc.mall.dao.OrderMapper;
import com.imooc.mall.dao.ProductMapper;
import com.imooc.mall.dao.ShippingMapper;
import com.imooc.mall.pojo.Order;
import com.imooc.mall.pojo.OrderItem;
import com.imooc.mall.pojo.Product;
import com.imooc.mall.pojo.Shipping;
import com.imooc.mall.service.impl.OrderServiceImpl;
import com.imooc.mall.vo.OrderVo;
import com.imooc.mall.vo.ResponseVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    private OrderServiceImpl orderService;

    @MockBean
    private OrderMapper orderMapper;

    @MockBean
    private ProductMapper productMapper;

    @MockBean
    private ShippingMapper shippingMapper;

    private Order mockOrder;
    private Product mockProduct;
    private Shipping mockShipping;
    private Integer uid = 1;

    @BeforeEach
    void setUp() {
        // 初始化测试订单数据
        mockOrder = new Order();
        mockOrder.setOrderNo(123456789L);
        mockOrder.setUserId(uid);
        mockOrder.setShippingId(1);
        mockOrder.setPayment(new BigDecimal("999.99"));
        mockOrder.setPaymentType(1);
        mockOrder.setPostage(0);
        mockOrder.setStatus(10);

        // 初始化测试商品数据
        mockProduct = new Product();
        mockProduct.setId(1);
        mockProduct.setName("测试商品");
        mockProduct.setPrice(new BigDecimal("999.99"));
        mockProduct.setStock(100);
        mockProduct.setStatus(1);

        // 初始化测试收货地址数据
        mockShipping = new Shipping();
        mockShipping.setId(1);
        mockShipping.setUserId(uid);
        mockShipping.setReceiverName("测试用户");
        mockShipping.setReceiverPhone("13800138000");
        mockShipping.setReceiverAddress("测试地址");
    }

    @Test
    @DisplayName("测试创建订单 - 成功场景")
    void testCreate_Success() {
        // 模拟商品和收货地址存在
        when(productMapper.selectByPrimaryKey(any())).thenReturn(mockProduct);
        when(shippingMapper.selectByPrimaryKey(any())).thenReturn(mockShipping);
        when(orderMapper.insertSelective(any())).thenReturn(1);

        // 执行测试
        ResponseVo<OrderVo> response = orderService.create(uid, 1);

        // 验证结果
        assertNotNull(response);
        assertEquals(0, response.getStatus());
        verify(orderMapper, times(1)).insertSelective(any());
    }

    @Test
    @DisplayName("测试订单查询 - 成功场景")
    void testList_Success() {
        // 模拟订单列表数据
        List<Order> orders = new ArrayList<>();
        orders.add(mockOrder);
        when(orderMapper.selectByUserId(uid)).thenReturn(orders);

        // 执行测试
        ResponseVo<PageInfo> response = orderService.list(uid, 1, 10);

        // 验证结果
        assertNotNull(response);
        assertEquals(0, response.getStatus());
        PageInfo pageInfo = response.getData();
        assertNotNull(pageInfo);
        assertFalse(pageInfo.getList().isEmpty());
    }

    @Test
    @DisplayName("测试订单详情查询 - 成功场景")
    void testDetail_Success() {
        // 模拟订单存在
        when(orderMapper.selectByUserIdAndOrderNo(uid, mockOrder.getOrderNo())).thenReturn(mockOrder);

        // 执行测试
        ResponseVo<OrderVo> response = orderService.detail(uid, mockOrder.getOrderNo());

        // 验证结果
        assertNotNull(response);
        assertEquals(0, response.getStatus());
        OrderVo orderVo = response.getData();
        assertNotNull(orderVo);
        assertEquals(mockOrder.getOrderNo(), orderVo.getOrderNo());
    }

    @Test
    @DisplayName("测试取消订单 - 成功场景")
    void testCancel_Success() {
        // 模拟订单存在且状态允许取消
        mockOrder.setStatus(10); // 未付款状态
        when(orderMapper.selectByUserIdAndOrderNo(uid, mockOrder.getOrderNo())).thenReturn(mockOrder);
        when(orderMapper.updateByPrimaryKeySelective(any())).thenReturn(1);

        // 执行测试
        ResponseVo response = orderService.cancel(uid, mockOrder.getOrderNo());

        // 验证结果
        assertNotNull(response);
        assertEquals(0, response.getStatus());
        verify(orderMapper, times(1)).updateByPrimaryKeySelective(any());
    }

    @Test
    @DisplayName("测试取消订单 - 订单状态不允许取消")
    void testCancel_InvalidStatus() {
        // 模拟订单已付款，不能取消
        mockOrder.setStatus(20); // 已付款状态
        when(orderMapper.selectByUserIdAndOrderNo(uid, mockOrder.getOrderNo())).thenReturn(mockOrder);

        // 执行测试
        ResponseVo response = orderService.cancel(uid, mockOrder.getOrderNo());

        // 验证结果
        assertNotNull(response);
        assertNotEquals(0, response.getStatus());
        verify(orderMapper, never()).updateByPrimaryKeySelective(any());
    }

    @Test
    @DisplayName("测试订单分页")
    void testOrderPagination() {
        // 创建多个订单数据
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Order order = new Order();
            order.setOrderNo(123456789L + i);
            order.setUserId(uid);
            orders.add(order);
        }

        when(orderMapper.selectByUserId(uid)).thenReturn(orders);

        // 测试第一页
        ResponseVo<PageInfo> response1 = orderService.list(uid, 1, 2);
        assertEquals(2, response1.getData().getPageSize());
        assertEquals(1, response1.getData().getPageNum());

        // 测试第二页
        ResponseVo<PageInfo> response2 = orderService.list(uid, 2, 2);
        assertEquals(2, response2.getData().getPageSize());
        assertEquals(2, response2.getData().getPageNum());
    }
}
