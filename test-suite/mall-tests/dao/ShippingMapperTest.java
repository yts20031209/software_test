package com.imooc.mall.dao;

import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.pojo.Shipping;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class ShippingMapperTest extends MallApplicationTests {

    @Autowired
    private ShippingMapper shippingMapper;

    @Test
    @DisplayName("测试添加收货地址")
    void testInsertShipping() {
        Shipping shipping = new Shipping();
        shipping.setUserId(1);
        shipping.setReceiverName("张三");
        shipping.setReceiverPhone("13800138000");
        shipping.setReceiverMobile("13800138000");
        shipping.setReceiverProvince("北京市");
        shipping.setReceiverCity("北京市");
        shipping.setReceiverDistrict("海淀区");
        shipping.setReceiverAddress("中关村大街1号");
        shipping.setReceiverZip("100080");
        shipping.setCreateTime(new Date());
        shipping.setUpdateTime(new Date());

        int result = shippingMapper.insertSelective(shipping);
        assertNotNull(shipping.getId());
        assertEquals(1, result);
    }

    @Test
    @DisplayName("测试删除收货地址")
    void testDeleteShipping() {
        // 先插入测试数据
        Shipping shipping = new Shipping();
        shipping.setUserId(1);
        shipping.setReceiverName("张三");
        shipping.setReceiverPhone("13800138000");
        shipping.setReceiverMobile("13800138000");
        shipping.setReceiverProvince("北京市");
        shipping.setReceiverCity("北京市");
        shipping.setReceiverDistrict("海淀区");
        shipping.setReceiverAddress("中关村大街1号");
        shipping.setReceiverZip("100080");
        shipping.setCreateTime(new Date());
        shipping.setUpdateTime(new Date());
        shippingMapper.insertSelective(shipping);

        // 删除收货地址
        int result = shippingMapper.deleteByPrimaryKey(shipping.getId());

        // 验证删除结果
        assertEquals(1, result);
        Shipping deleted = shippingMapper.selectByPrimaryKey(shipping.getId());
        assertNull(deleted);
    }

    @Test
    @DisplayName("测试更新收货地址")
    void testUpdateShipping() {
        // 先插入测试数据
        Shipping shipping = new Shipping();
        shipping.setUserId(1);
        shipping.setReceiverName("张三");
        shipping.setReceiverPhone("13800138000");
        shipping.setReceiverMobile("13800138000");
        shipping.setReceiverProvince("北京市");
        shipping.setReceiverCity("北京市");
        shipping.setReceiverDistrict("海淀区");
        shipping.setReceiverAddress("中关村大街1号");
        shipping.setReceiverZip("100080");
        shipping.setCreateTime(new Date());
        shipping.setUpdateTime(new Date());
        shippingMapper.insertSelective(shipping);

        // 更新收货地址
        shipping.setReceiverName("李四");
        shipping.setReceiverPhone("13900139000");
        shipping.setReceiverAddress("中关村大街2号");
        shipping.setUpdateTime(new Date());
        int result = shippingMapper.updateByPrimaryKeySelective(shipping);

        // 验证更新结果
        assertEquals(1, result);
        Shipping updated = shippingMapper.selectByPrimaryKey(shipping.getId());
        assertEquals("李四", updated.getReceiverName());
        assertEquals("13900139000", updated.getReceiverPhone());
        assertEquals("中关村大街2号", updated.getReceiverAddress());
    }

    @Test
    @DisplayName("测试查询用户的收货地址列表")
    void testSelectByUserId() {
        // 插入多个收货地址
        Shipping shipping1 = new Shipping();
        shipping1.setUserId(1);
        shipping1.setReceiverName("张三");
        shipping1.setReceiverPhone("13800138000");
        shipping1.setReceiverMobile("13800138000");
        shipping1.setReceiverProvince("北京市");
        shipping1.setReceiverCity("北京市");
        shipping1.setReceiverDistrict("海淀区");
        shipping1.setReceiverAddress("中关村大街1号");
        shipping1.setReceiverZip("100080");
        shipping1.setCreateTime(new Date());
        shipping1.setUpdateTime(new Date());
        shippingMapper.insertSelective(shipping1);

        Shipping shipping2 = new Shipping();
        shipping2.setUserId(1);
        shipping2.setReceiverName("李四");
        shipping2.setReceiverPhone("13900139000");
        shipping2.setReceiverMobile("13900139000");
        shipping2.setReceiverProvince("上海市");
        shipping2.setReceiverCity("上海市");
        shipping2.setReceiverDistrict("浦东新区");
        shipping2.setReceiverAddress("张江高科1号");
        shipping2.setReceiverZip("200120");
        shipping2.setCreateTime(new Date());
        shipping2.setUpdateTime(new Date());
        shippingMapper.insertSelective(shipping2);

        // 测试查询
        List<Shipping> shippings = shippingMapper.selectByUserId(1);
        assertNotNull(shippings);
        assertEquals(2, shippings.size());
        assertTrue(shippings.stream().anyMatch(s -> s.getReceiverName().equals("张三")));
        assertTrue(shippings.stream().anyMatch(s -> s.getReceiverName().equals("李四")));
    }

    @Test
    @DisplayName("测试根据ID查询收货地址")
    void testSelectByPrimaryKey() {
        // 先插入测试数据
        Shipping shipping = new Shipping();
        shipping.setUserId(1);
        shipping.setReceiverName("张三");
        shipping.setReceiverPhone("13800138000");
        shipping.setReceiverMobile("13800138000");
        shipping.setReceiverProvince("北京市");
        shipping.setReceiverCity("北京市");
        shipping.setReceiverDistrict("海淀区");
        shipping.setReceiverAddress("中关村大街1号");
        shipping.setReceiverZip("100080");
        shipping.setCreateTime(new Date());
        shipping.setUpdateTime(new Date());
        shippingMapper.insertSelective(shipping);

        // 测试查询
        Shipping result = shippingMapper.selectByPrimaryKey(shipping.getId());
        assertNotNull(result);
        assertEquals("张三", result.getReceiverName());
        assertEquals("13800138000", result.getReceiverPhone());
        assertEquals("中关村大街1号", result.getReceiverAddress());
    }

    @Test
    @DisplayName("测试根据用户ID和地址ID查询收货地址")
    void testSelectByUserIdAndShippingId() {
        // 先插入测试数据
        Shipping shipping = new Shipping();
        shipping.setUserId(1);
        shipping.setReceiverName("张三");
        shipping.setReceiverPhone("13800138000");
        shipping.setReceiverMobile("13800138000");
        shipping.setReceiverProvince("北京市");
        shipping.setReceiverCity("北京市");
        shipping.setReceiverDistrict("海淀区");
        shipping.setReceiverAddress("中关村大街1号");
        shipping.setReceiverZip("100080");
        shipping.setCreateTime(new Date());
        shipping.setUpdateTime(new Date());
        shippingMapper.insertSelective(shipping);

        // 测试查询
        Shipping result = shippingMapper.selectByUserIdAndShippingId(1, shipping.getId());
        assertNotNull(result);
        assertEquals("张三", result.getReceiverName());
        assertEquals("13800138000", result.getReceiverPhone());
        assertEquals("中关村大街1号", result.getReceiverAddress());

        // 测试查询不存在的用户ID
        Shipping notFound = shippingMapper.selectByUserIdAndShippingId(999, shipping.getId());
        assertNull(notFound);
    }
}
