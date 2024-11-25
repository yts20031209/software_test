package com.imooc.mall.dao;

import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.pojo.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class ProductMapperTest extends MallApplicationTests {

    @Autowired
    private ProductMapper productMapper;

    @Test
    @DisplayName("测试插入商品")
    void testInsertProduct() {
        Product product = new Product();
        product.setName("iPhone 13");
        product.setCategoryId(100001);
        product.setPrice(new BigDecimal("6999.00"));
        product.setStock(1000);
        product.setStatus(1);
        product.setMainImage("main.jpg");
        product.setSubImages("sub1.jpg,sub2.jpg");
        product.setDetail("商品详情");
        product.setCreateTime(new Date());
        product.setUpdateTime(new Date());

        int result = productMapper.insertSelective(product);
        assertNotNull(product.getId());
        assertEquals(1, result);
    }

    @Test
    @DisplayName("测试根据ID查询商品")
    void testSelectByPrimaryKey() {
        // 先插入测试数据
        Product product = new Product();
        product.setName("iPhone 13");
        product.setCategoryId(100001);
        product.setPrice(new BigDecimal("6999.00"));
        product.setStock(1000);
        product.setStatus(1);
        product.setMainImage("main.jpg");
        product.setCreateTime(new Date());
        product.setUpdateTime(new Date());
        productMapper.insertSelective(product);

        // 测试查询
        Product result = productMapper.selectByPrimaryKey(product.getId());
        assertNotNull(result);
        assertEquals("iPhone 13", result.getName());
        assertEquals(0, result.getPrice().compareTo(new BigDecimal("6999.00")));
    }

    @Test
    @DisplayName("测试根据分类ID查询商品列表")
    void testSelectByCategoryId() {
        // 先插入测试数据
        Product product1 = new Product();
        product1.setName("iPhone 13");
        product1.setCategoryId(100001);
        product1.setPrice(new BigDecimal("6999.00"));
        product1.setStock(1000);
        product1.setStatus(1);
        product1.setCreateTime(new Date());
        product1.setUpdateTime(new Date());
        productMapper.insertSelective(product1);

        Product product2 = new Product();
        product2.setName("iPhone 14");
        product2.setCategoryId(100001);
        product2.setPrice(new BigDecimal("7999.00"));
        product2.setStock(1000);
        product2.setStatus(1);
        product2.setCreateTime(new Date());
        product2.setUpdateTime(new Date());
        productMapper.insertSelective(product2);

        // 测试查询
        List<Product> results = productMapper.selectByCategoryId(100001);
        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(p -> p.getName().equals("iPhone 13")));
        assertTrue(results.stream().anyMatch(p -> p.getName().equals("iPhone 14")));
    }

    @Test
    @DisplayName("测试更新商品信息")
    void testUpdateByPrimaryKey() {
        // 先插入测试数据
        Product product = new Product();
        product.setName("iPhone 13");
        product.setCategoryId(100001);
        product.setPrice(new BigDecimal("6999.00"));
        product.setStock(1000);
        product.setStatus(1);
        product.setCreateTime(new Date());
        product.setUpdateTime(new Date());
        productMapper.insertSelective(product);

        // 更新数据
        product.setName("iPhone 13 Pro");
        product.setPrice(new BigDecimal("7999.00"));
        product.setUpdateTime(new Date());
        int result = productMapper.updateByPrimaryKeySelective(product);

        // 验证更新结果
        assertEquals(1, result);
        Product updated = productMapper.selectByPrimaryKey(product.getId());
        assertEquals("iPhone 13 Pro", updated.getName());
        assertEquals(0, updated.getPrice().compareTo(new BigDecimal("7999.00")));
    }

    @Test
    @DisplayName("测试商品库存更新")
    void testUpdateStock() {
        // 先插入测试数据
        Product product = new Product();
        product.setName("iPhone 13");
        product.setCategoryId(100001);
        product.setPrice(new BigDecimal("6999.00"));
        product.setStock(1000);
        product.setStatus(1);
        product.setCreateTime(new Date());
        product.setUpdateTime(new Date());
        productMapper.insertSelective(product);

        // 更新库存
        product.setStock(900);
        int result = productMapper.updateStock(product.getId(), product.getStock());

        // 验证更新结果
        assertEquals(1, result);
        Product updated = productMapper.selectByPrimaryKey(product.getId());
        assertEquals(900, updated.getStock());
    }

    @Test
    @DisplayName("测试商品状态更新")
    void testUpdateStatus() {
        // 先插入测试数据
        Product product = new Product();
        product.setName("iPhone 13");
        product.setCategoryId(100001);
        product.setPrice(new BigDecimal("6999.00"));
        product.setStock(1000);
        product.setStatus(1);
        product.setCreateTime(new Date());
        product.setUpdateTime(new Date());
        productMapper.insertSelective(product);

        // 更新状态（下架）
        product.setStatus(0);
        int result = productMapper.updateStatus(product.getId(), product.getStatus());

        // 验证更新结果
        assertEquals(1, result);
        Product updated = productMapper.selectByPrimaryKey(product.getId());
        assertEquals(0, updated.getStatus());
    }

    @Test
    @DisplayName("测试批量查询商品")
    void testSelectByIds() {
        // 先插入测试数据
        Product product1 = new Product();
        product1.setName("iPhone 13");
        product1.setCategoryId(100001);
        product1.setPrice(new BigDecimal("6999.00"));
        product1.setStock(1000);
        product1.setStatus(1);
        product1.setCreateTime(new Date());
        product1.setUpdateTime(new Date());
        productMapper.insertSelective(product1);

        Product product2 = new Product();
        product2.setName("iPhone 14");
        product2.setCategoryId(100001);
        product2.setPrice(new BigDecimal("7999.00"));
        product2.setStock(1000);
        product2.setStatus(1);
        product2.setCreateTime(new Date());
        product2.setUpdateTime(new Date());
        productMapper.insertSelective(product2);

        // 测试批量查询
        List<Integer> ids = Arrays.asList(product1.getId(), product2.getId());
        List<Product> results = productMapper.selectByIds(ids);
        
        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(p -> p.getName().equals("iPhone 13")));
        assertTrue(results.stream().anyMatch(p -> p.getName().equals("iPhone 14")));
    }
}
