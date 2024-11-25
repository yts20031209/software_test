package com.imooc.mall.service;

/**
 * 商品分类服务测试类
 * 对应测试类: com.imooc.mall.service.impl.CategoryServiceImpl
 * 测试内容：
 * 1. 获取分类列表
 * 2. 获取子分类
 * 3. 递归查找所有子分类
 */

import com.imooc.mall.dao.CategoryMapper;
import com.imooc.mall.pojo.Category;
import com.imooc.mall.service.impl.CategoryServiceImpl;
import com.imooc.mall.vo.CategoryVo;
import com.imooc.mall.vo.ResponseVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CategoryServiceTest {

    @Autowired
    private CategoryServiceImpl categoryService;

    @MockBean
    private CategoryMapper categoryMapper;

    private List<Category> mockCategories;

    @BeforeEach
    void setUp() {
        // 初始化模拟分类数据
        Category parent = new Category();
        parent.setId(1);
        parent.setParentId(0);
        parent.setName("电子产品");
        parent.setSortOrder(1);

        Category child1 = new Category();
        child1.setId(2);
        child1.setParentId(1);
        child1.setName("手机");
        child1.setSortOrder(1);

        Category child2 = new Category();
        child2.setId(3);
        child2.setParentId(1);
        child2.setName("电脑");
        child2.setSortOrder(2);

        mockCategories = Arrays.asList(parent, child1, child2);
    }

    @Test
    @DisplayName("测试获取所有分类")
    void testSelectAll() {
        // 模拟数据库返回所有分类
        when(categoryMapper.selectAll()).thenReturn(mockCategories);

        // 执行测试
        ResponseVo<List<CategoryVo>> response = categoryService.selectAll();

        // 验证结果
        assertNotNull(response);
        assertEquals(0, response.getStatus());
        List<CategoryVo> categoryVoList = response.getData();
        assertNotNull(categoryVoList);
        assertFalse(categoryVoList.isEmpty());

        // 验证父子关系
        CategoryVo parentVo = categoryVoList.get(0);
        assertEquals("电子产品", parentVo.getName());
        assertEquals(2, parentVo.getSubCategories().size());
    }

    @Test
    @DisplayName("测试获取指定分类的所有子分类ID")
    void testFindSubCategoryId() {
        // 模拟数据库返回所有分类
        when(categoryMapper.selectAll()).thenReturn(mockCategories);

        // 执行测试 - 获取电子产品分类的所有子分类ID
        Set<Integer> subIds = categoryService.findSubCategoryId(1);

        // 验证结果
        assertNotNull(subIds);
        assertEquals(2, subIds.size());
        assertTrue(subIds.contains(2)); // 包含手机分类ID
        assertTrue(subIds.contains(3)); // 包含电脑分类ID
    }

    @Test
    @DisplayName("测试获取空分类的子分类")
    void testFindSubCategoryId_Empty() {
        // 模拟数据库返回空列表
        when(categoryMapper.selectAll()).thenReturn(Arrays.asList());

        // 执行测试
        Set<Integer> subIds = categoryService.findSubCategoryId(1);

        // 验证结果
        assertNotNull(subIds);
        assertTrue(subIds.isEmpty());
    }

    @Test
    @DisplayName("测试分类排序")
    void testCategorySort() {
        // 创建测试数据 - 乱序的分类列表
        Category cat1 = new Category();
        cat1.setId(1);
        cat1.setSortOrder(3);

        Category cat2 = new Category();
        cat2.setId(2);
        cat2.setSortOrder(1);

        Category cat3 = new Category();
        cat3.setId(3);
        cat3.setSortOrder(2);

        List<Category> unsortedList = Arrays.asList(cat1, cat2, cat3);
        when(categoryMapper.selectAll()).thenReturn(unsortedList);

        // 执行测试
        ResponseVo<List<CategoryVo>> response = categoryService.selectAll();

        // 验证结果
        List<CategoryVo> sortedList = response.getData();
        assertNotNull(sortedList);
        assertEquals(3, sortedList.size());
        // 验证排序是否正确
        assertTrue(sortedList.get(0).getSortOrder() <= sortedList.get(1).getSortOrder());
        assertTrue(sortedList.get(1).getSortOrder() <= sortedList.get(2).getSortOrder());
    }
}
