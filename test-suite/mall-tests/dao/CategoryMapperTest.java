package com.imooc.mall.dao;

import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.pojo.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class CategoryMapperTest extends MallApplicationTests {

    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    @DisplayName("测试插入商品分类")
    void testInsertCategory() {
        Category category = new Category();
        category.setName("电子产品");
        category.setParentId(0);
        category.setStatus(true);
        category.setSortOrder(1);
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());

        int result = categoryMapper.insertSelective(category);
        assertNotNull(category.getId());
        assertEquals(1, result);
    }

    @Test
    @DisplayName("测试根据ID查询分类")
    void testSelectByPrimaryKey() {
        // 先插入测试数据
        Category category = new Category();
        category.setName("电子产品");
        category.setParentId(0);
        category.setStatus(true);
        category.setSortOrder(1);
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        categoryMapper.insertSelective(category);

        // 测试查询
        Category result = categoryMapper.selectByPrimaryKey(category.getId());
        assertNotNull(result);
        assertEquals("电子产品", result.getName());
        assertEquals(0, result.getParentId());
    }

    @Test
    @DisplayName("测试查询所有分类")
    void testSelectAll() {
        // 先插入测试数据
        Category category1 = new Category();
        category1.setName("电子产品");
        category1.setParentId(0);
        category1.setStatus(true);
        category1.setSortOrder(1);
        category1.setCreateTime(new Date());
        category1.setUpdateTime(new Date());
        categoryMapper.insertSelective(category1);

        Category category2 = new Category();
        category2.setName("服装");
        category2.setParentId(0);
        category2.setStatus(true);
        category2.setSortOrder(2);
        category2.setCreateTime(new Date());
        category2.setUpdateTime(new Date());
        categoryMapper.insertSelective(category2);

        // 测试查询
        List<Category> results = categoryMapper.selectAll();
        assertNotNull(results);
        assertTrue(results.size() >= 2);
        assertTrue(results.stream().anyMatch(c -> c.getName().equals("电子产品")));
        assertTrue(results.stream().anyMatch(c -> c.getName().equals("服装")));
    }

    @Test
    @DisplayName("测试查询子分类")
    void testSelectByParentId() {
        // 先插入父分类
        Category parent = new Category();
        parent.setName("电子产品");
        parent.setParentId(0);
        parent.setStatus(true);
        parent.setSortOrder(1);
        parent.setCreateTime(new Date());
        parent.setUpdateTime(new Date());
        categoryMapper.insertSelective(parent);

        // 插入子分类
        Category child1 = new Category();
        child1.setName("手机");
        child1.setParentId(parent.getId());
        child1.setStatus(true);
        child1.setSortOrder(1);
        child1.setCreateTime(new Date());
        child1.setUpdateTime(new Date());
        categoryMapper.insertSelective(child1);

        Category child2 = new Category();
        child2.setName("电脑");
        child2.setParentId(parent.getId());
        child2.setStatus(true);
        child2.setSortOrder(2);
        child2.setCreateTime(new Date());
        child2.setUpdateTime(new Date());
        categoryMapper.insertSelective(child2);

        // 测试查询
        List<Category> children = categoryMapper.selectByParentId(parent.getId());
        assertNotNull(children);
        assertEquals(2, children.size());
        assertTrue(children.stream().anyMatch(c -> c.getName().equals("手机")));
        assertTrue(children.stream().anyMatch(c -> c.getName().equals("电脑")));
    }

    @Test
    @DisplayName("测试更新分类信息")
    void testUpdateCategory() {
        // 先插入测试数据
        Category category = new Category();
        category.setName("电子产品");
        category.setParentId(0);
        category.setStatus(true);
        category.setSortOrder(1);
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        categoryMapper.insertSelective(category);

        // 更新分类
        category.setName("数码产品");
        category.setSortOrder(2);
        category.setUpdateTime(new Date());
        int result = categoryMapper.updateByPrimaryKeySelective(category);

        // 验证更新结果
        assertEquals(1, result);
        Category updated = categoryMapper.selectByPrimaryKey(category.getId());
        assertEquals("数码产品", updated.getName());
        assertEquals(2, updated.getSortOrder());
    }

    @Test
    @DisplayName("测试批量查询分类")
    void testSelectByIds() {
        // 先插入测试数据
        Category category1 = new Category();
        category1.setName("电子产品");
        category1.setParentId(0);
        category1.setStatus(true);
        category1.setSortOrder(1);
        category1.setCreateTime(new Date());
        category1.setUpdateTime(new Date());
        categoryMapper.insertSelective(category1);

        Category category2 = new Category();
        category2.setName("服装");
        category2.setParentId(0);
        category2.setStatus(true);
        category2.setSortOrder(2);
        category2.setCreateTime(new Date());
        category2.setUpdateTime(new Date());
        categoryMapper.insertSelective(category2);

        // 测试批量查询
        List<Integer> ids = Arrays.asList(category1.getId(), category2.getId());
        List<Category> results = categoryMapper.selectByIds(ids);
        
        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(c -> c.getName().equals("电子产品")));
        assertTrue(results.stream().anyMatch(c -> c.getName().equals("服装")));
    }

    @Test
    @DisplayName("测试查询分类层级")
    void testSelectCategoryLevel() {
        // 插入三级分类结构
        // 一级分类
        Category level1 = new Category();
        level1.setName("电子产品");
        level1.setParentId(0);
        level1.setStatus(true);
        level1.setSortOrder(1);
        level1.setCreateTime(new Date());
        level1.setUpdateTime(new Date());
        categoryMapper.insertSelective(level1);

        // 二级分类
        Category level2 = new Category();
        level2.setName("手机");
        level2.setParentId(level1.getId());
        level2.setStatus(true);
        level2.setSortOrder(1);
        level2.setCreateTime(new Date());
        level2.setUpdateTime(new Date());
        categoryMapper.insertSelective(level2);

        // 三级分类
        Category level3 = new Category();
        level3.setName("iPhone");
        level3.setParentId(level2.getId());
        level3.setStatus(true);
        level3.setSortOrder(1);
        level3.setCreateTime(new Date());
        level3.setUpdateTime(new Date());
        categoryMapper.insertSelective(level3);

        // 测试查询各级分类
        Category result1 = categoryMapper.selectByPrimaryKey(level1.getId());
        Category result2 = categoryMapper.selectByPrimaryKey(level2.getId());
        Category result3 = categoryMapper.selectByPrimaryKey(level3.getId());

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        assertEquals(0, result1.getParentId());
        assertEquals(level1.getId(), result2.getParentId());
        assertEquals(level2.getId(), result3.getParentId());
    }
}
