package com.imooc.mall.service;

import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.dao.OrderMapper;
import com.imooc.mall.dao.PayInfoMapper;
import com.imooc.mall.enums.PayPlatformEnum;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.pojo.Order;
import com.imooc.mall.pojo.PayInfo;
import com.imooc.mall.service.impl.PayServiceImpl;
import com.imooc.mall.vo.ResponseVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
public class PayServiceTest extends MallApplicationTests {

    @InjectMocks
    private PayServiceImpl payService;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private PayInfoMapper payInfoMapper;

    @Test
    @DisplayName("测试创建支付")
    void testCreate() {
        // 准备测试数据
        Long orderNo = 12345678L;
        Integer userId = 1;
        
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setPayment(new BigDecimal("9999.00"));
        order.setStatus(10);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());

        // 模拟订单查询
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(order);

        // 执行测试
        ResponseVo responseVo = payService.create(userId, orderNo);

        // 验证结果
        assertNotNull(responseVo);
        assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
        
        // 验证方法调用
        verify(orderMapper).selectByOrderNo(orderNo);
    }

    @Test
    @DisplayName("测试创建支付 - 订单不存在")
    void testCreateWithNonExistentOrder() {
        // 准备测试数据
        Long orderNo = 12345678L;
        Integer userId = 1;

        // 模拟订单查询 - 返回空
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(null);

        // 执行测试
        ResponseVo responseVo = payService.create(userId, orderNo);

        // 验证结果
        assertNotNull(responseVo);
        assertEquals(ResponseEnum.ORDER_NOT_EXIST.getCode(), responseVo.getStatus());
        
        // 验证方法调用
        verify(orderMapper).selectByOrderNo(orderNo);
        verify(payInfoMapper, never()).insertSelective(any());
    }

    @Test
    @DisplayName("测试创建支付 - 订单用户不匹配")
    void testCreateWithWrongUser() {
        // 准备测试数据
        Long orderNo = 12345678L;
        Integer userId = 1;
        
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(2); // 不同的用户ID
        order.setPayment(new BigDecimal("9999.00"));
        order.setStatus(10);

        // 模拟订单查询
        when(orderMapper.selectByOrderNo(orderNo)).thenReturn(order);

        // 执行测试
        ResponseVo responseVo = payService.create(userId, orderNo);

        // 验证结果
        assertNotNull(responseVo);
        assertEquals(ResponseEnum.ORDER_NOT_EXIST.getCode(), responseVo.getStatus());
        
        // 验证方法调用
        verify(orderMapper).selectByOrderNo(orderNo);
        verify(payInfoMapper, never()).insertSelective(any());
    }

    @Test
    @DisplayName("测试支付通知处理")
    void testNotify() {
        // 准备测试数据
        String platformNumber = "2023112012345678";
        PayInfo payInfo = new PayInfo();
        payInfo.setOrderNo(12345678L);
        payInfo.setPlatformStatus("WAIT_BUYER_PAY");

        Order order = new Order();
        order.setOrderNo(12345678L);
        order.setStatus(10);

        // 模拟数据查询
        when(payInfoMapper.selectByPlatformNumber(platformNumber)).thenReturn(payInfo);
        when(orderMapper.selectByOrderNo(payInfo.getOrderNo())).thenReturn(order);
        when(payInfoMapper.updateByPrimaryKeySelective(any())).thenReturn(1);
        when(orderMapper.updateByPrimaryKeySelective(any())).thenReturn(1);

        // 执行测试
        ResponseVo responseVo = payService.notify(platformNumber, PayPlatformEnum.ALIPAY);

        // 验证结果
        assertNotNull(responseVo);
        assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
        
        // 验证方法调用
        verify(payInfoMapper).selectByPlatformNumber(platformNumber);
        verify(orderMapper).selectByOrderNo(payInfo.getOrderNo());
        verify(payInfoMapper).updateByPrimaryKeySelective(any());
        verify(orderMapper).updateByPrimaryKeySelective(any());
    }

    @Test
    @DisplayName("测试支付通知处理 - 支付信息不存在")
    void testNotifyWithNonExistentPayInfo() {
        // 准备测试数据
        String platformNumber = "2023112012345678";

        // 模拟数据查询 - 返回空
        when(payInfoMapper.selectByPlatformNumber(platformNumber)).thenReturn(null);

        // 执行测试
        ResponseVo responseVo = payService.notify(platformNumber, PayPlatformEnum.ALIPAY);

        // 验证结果
        assertNotNull(responseVo);
        assertEquals(ResponseEnum.PAY_INFO_NOT_EXIST.getCode(), responseVo.getStatus());
        
        // 验证方法调用
        verify(payInfoMapper).selectByPlatformNumber(platformNumber);
        verify(orderMapper, never()).selectByOrderNo(any());
        verify(payInfoMapper, never()).updateByPrimaryKeySelective(any());
        verify(orderMapper, never()).updateByPrimaryKeySelective(any());
    }

    @Test
    @DisplayName("测试查询支付状态")
    void testQueryByOrderNo() {
        // 准备测试数据
        Long orderNo = 12345678L;
        Integer userId = 1;
        
        PayInfo payInfo = new PayInfo();
        payInfo.setOrderNo(orderNo);
        payInfo.setUserId(userId);
        payInfo.setPlatformStatus("TRADE_SUCCESS");

        // 模拟数据查询
        when(payInfoMapper.selectByOrderNo(orderNo)).thenReturn(payInfo);

        // 执行测试
        ResponseVo responseVo = payService.queryByOrderNo(orderNo, userId);

        // 验证结果
        assertNotNull(responseVo);
        assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
        
        // 验证方法调用
        verify(payInfoMapper).selectByOrderNo(orderNo);
    }

    @Test
    @DisplayName("测试查询支付状态 - 支付信息不存在")
    void testQueryByOrderNoWithNonExistentPayInfo() {
        // 准备测试数据
        Long orderNo = 12345678L;
        Integer userId = 1;

        // 模拟数据查询 - 返回空
        when(payInfoMapper.selectByOrderNo(orderNo)).thenReturn(null);

        // 执行测试
        ResponseVo responseVo = payService.queryByOrderNo(orderNo, userId);

        // 验证结果
        assertNotNull(responseVo);
        assertEquals(ResponseEnum.PAY_INFO_NOT_EXIST.getCode(), responseVo.getStatus());
        
        // 验证方法调用
        verify(payInfoMapper).selectByOrderNo(orderNo);
    }
}
