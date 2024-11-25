package com.imooc.mall.dao;

import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.pojo.PayInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class PayInfoMapperTest extends MallApplicationTests {

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Test
    @DisplayName("测试创建支付信息")
    void testInsertPayInfo() {
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(1);
        payInfo.setOrderNo(12345678L);
        payInfo.setPayPlatform(1);
        payInfo.setPlatformNumber("2023112012345678");
        payInfo.setPlatformStatus("TRADE_SUCCESS");
        payInfo.setPayAmount(new BigDecimal("9999.00"));
        payInfo.setCreateTime(new Date());
        payInfo.setUpdateTime(new Date());

        int result = payInfoMapper.insertSelective(payInfo);
        assertNotNull(payInfo.getId());
        assertEquals(1, result);
    }

    @Test
    @DisplayName("测试根据ID查询支付信息")
    void testSelectByPrimaryKey() {
        // 先插入测试数据
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(1);
        payInfo.setOrderNo(12345678L);
        payInfo.setPayPlatform(1);
        payInfo.setPlatformNumber("2023112012345678");
        payInfo.setPlatformStatus("TRADE_SUCCESS");
        payInfo.setPayAmount(new BigDecimal("9999.00"));
        payInfo.setCreateTime(new Date());
        payInfo.setUpdateTime(new Date());
        payInfoMapper.insertSelective(payInfo);

        // 测试查询
        PayInfo result = payInfoMapper.selectByPrimaryKey(payInfo.getId());
        assertNotNull(result);
        assertEquals(12345678L, result.getOrderNo().longValue());
        assertEquals("2023112012345678", result.getPlatformNumber());
        assertEquals("TRADE_SUCCESS", result.getPlatformStatus());
        assertEquals(0, result.getPayAmount().compareTo(new BigDecimal("9999.00")));
    }

    @Test
    @DisplayName("测试根据订单号查询支付信息")
    void testSelectByOrderNo() {
        // 先插入测试数据
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(1);
        payInfo.setOrderNo(12345678L);
        payInfo.setPayPlatform(1);
        payInfo.setPlatformNumber("2023112012345678");
        payInfo.setPlatformStatus("TRADE_SUCCESS");
        payInfo.setPayAmount(new BigDecimal("9999.00"));
        payInfo.setCreateTime(new Date());
        payInfo.setUpdateTime(new Date());
        payInfoMapper.insertSelective(payInfo);

        // 测试查询
        PayInfo result = payInfoMapper.selectByOrderNo(12345678L);
        assertNotNull(result);
        assertEquals("2023112012345678", result.getPlatformNumber());
        assertEquals("TRADE_SUCCESS", result.getPlatformStatus());
    }

    @Test
    @DisplayName("测试更新支付状态")
    void testUpdatePayStatus() {
        // 先插入测试数据
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(1);
        payInfo.setOrderNo(12345678L);
        payInfo.setPayPlatform(1);
        payInfo.setPlatformNumber("2023112012345678");
        payInfo.setPlatformStatus("WAIT_BUYER_PAY");
        payInfo.setPayAmount(new BigDecimal("9999.00"));
        payInfo.setCreateTime(new Date());
        payInfo.setUpdateTime(new Date());
        payInfoMapper.insertSelective(payInfo);

        // 更新支付状态
        payInfo.setPlatformStatus("TRADE_SUCCESS");
        payInfo.setUpdateTime(new Date());
        int result = payInfoMapper.updateByPrimaryKeySelective(payInfo);

        // 验证更新结果
        assertEquals(1, result);
        PayInfo updated = payInfoMapper.selectByPrimaryKey(payInfo.getId());
        assertEquals("TRADE_SUCCESS", updated.getPlatformStatus());
    }

    @Test
    @DisplayName("测试查询用户的支付记录")
    void testSelectByUserId() {
        // 插入多条支付记录
        PayInfo payInfo1 = new PayInfo();
        payInfo1.setUserId(1);
        payInfo1.setOrderNo(12345678L);
        payInfo1.setPayPlatform(1);
        payInfo1.setPlatformNumber("2023112012345678");
        payInfo1.setPlatformStatus("TRADE_SUCCESS");
        payInfo1.setPayAmount(new BigDecimal("9999.00"));
        payInfo1.setCreateTime(new Date());
        payInfo1.setUpdateTime(new Date());
        payInfoMapper.insertSelective(payInfo1);

        PayInfo payInfo2 = new PayInfo();
        payInfo2.setUserId(1);
        payInfo2.setOrderNo(87654321L);
        payInfo2.setPayPlatform(1);
        payInfo2.setPlatformNumber("2023112087654321");
        payInfo2.setPlatformStatus("TRADE_SUCCESS");
        payInfo2.setPayAmount(new BigDecimal("8888.00"));
        payInfo2.setCreateTime(new Date());
        payInfo2.setUpdateTime(new Date());
        payInfoMapper.insertSelective(payInfo2);

        // 测试查询
        List<PayInfo> payInfos = payInfoMapper.selectByUserId(1);
        assertNotNull(payInfos);
        assertEquals(2, payInfos.size());
        assertTrue(payInfos.stream().anyMatch(p -> p.getOrderNo().equals(12345678L)));
        assertTrue(payInfos.stream().anyMatch(p -> p.getOrderNo().equals(87654321L)));
    }

    @Test
    @DisplayName("测试根据支付平台流水号查询")
    void testSelectByPlatformNumber() {
        // 先插入测试数据
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(1);
        payInfo.setOrderNo(12345678L);
        payInfo.setPayPlatform(1);
        payInfo.setPlatformNumber("2023112012345678");
        payInfo.setPlatformStatus("TRADE_SUCCESS");
        payInfo.setPayAmount(new BigDecimal("9999.00"));
        payInfo.setCreateTime(new Date());
        payInfo.setUpdateTime(new Date());
        payInfoMapper.insertSelective(payInfo);

        // 测试查询
        PayInfo result = payInfoMapper.selectByPlatformNumber("2023112012345678");
        assertNotNull(result);
        assertEquals(12345678L, result.getOrderNo().longValue());
        assertEquals("TRADE_SUCCESS", result.getPlatformStatus());

        // 测试查询不存在的流水号
        PayInfo notFound = payInfoMapper.selectByPlatformNumber("nonexistent");
        assertNull(notFound);
    }

    @Test
    @DisplayName("测试查询指定时间范围内的支付记录")
    void testSelectByTimeRange() {
        // 插入测试数据
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(1);
        payInfo.setOrderNo(12345678L);
        payInfo.setPayPlatform(1);
        payInfo.setPlatformNumber("2023112012345678");
        payInfo.setPlatformStatus("TRADE_SUCCESS");
        payInfo.setPayAmount(new BigDecimal("9999.00"));
        payInfo.setCreateTime(new Date());
        payInfo.setUpdateTime(new Date());
        payInfoMapper.insertSelective(payInfo);

        // 设置查询时间范围
        Date endTime = new Date();
        Date startTime = new Date(endTime.getTime() - 24 * 60 * 60 * 1000); // 24小时前

        // 测试查询
        List<PayInfo> results = payInfoMapper.selectByTimeRange(startTime, endTime);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(p -> p.getOrderNo().equals(12345678L)));
    }
}
