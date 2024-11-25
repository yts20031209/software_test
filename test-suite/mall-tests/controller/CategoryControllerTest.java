package com.imooc.mall.controller;

/**
 * 商品分类控制器测试类
 * 对应测试类: com.imooc.mall.controller.CategoryController
 * 测试内容：
 * 1. 获取所有分类接口
 * 2. 获取当前分类及其子分类接口
 */

import com.imooc.mall.service.impl.CategoryServiceImpl;
import com.imooc.mall.vo.CategoryVo;
import com.imooc.mall.vo.ResponseVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("测试获取所有分类 - 成功场景")
    void testGetAll_Success() throws Exception {
        // 准备测试数据
        CategoryVo category1 = new CategoryVo();
        category1.setId(1);
        category1.setName("电子产品");
        category1.setParentId(0);
        category1.setSortOrder(1);

        CategoryVo category2 = new CategoryVo();
        category2.setId(2);
        category2.setName("服装");
        category2.setParentId(0);
        category2.setSortOrder(2);

        List<CategoryVo> categoryList = Arrays.asList(category1, category2);

        // 模拟服务层返回
        when(categoryService.selectAll()).thenReturn(ResponseVo.success(categoryList));

        // 执行测试
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0))
                .andExpect(jsonPath("$.data[0].name").value("电子产品"))
                .andExpect(jsonPath("$.data[1].name").value("服装"));
    }

    @Test
    @DisplayName("测试获取当前分类及其子分类 - 成功场景")
    void testGetSubCategories_Success() throws Exception {
        // 准备测试数据
        CategoryVo parentCategory = new CategoryVo();
        parentCategory.setId(1);
        parentCategory.setName("电子产品");
        parentCategory.setParentId(0);

        CategoryVo subCategory1 = new CategoryVo();
        subCategory1.setId(2);
        subCategory1.setName("手机");
        subCategory1.setParentId(1);

        CategoryVo subCategory2 = new CategoryVo();
        subCategory2.setId(3);
        subCategory2.setName("电脑");
        subCategory2.setParentId(1);

        parentCategory.setSubCategories(Arrays.asList(subCategory1, subCategory2));

        // 模拟服务层返回
        when(categoryService.findSubCategoryId(1)).thenReturn(ResponseVo.success(Arrays.asList(1, 2, 3)));

        // 执行测试
        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("测试获取当前分类及其子分类 - 分类不存在")
    void testGetSubCategories_CategoryNotExist() throws Exception {
        // 模拟服务层返回分类不存在错误
        when(categoryService.findSubCategoryId(999)).thenReturn(ResponseVo.error(ResponseEnum.CATEGORY_NOT_EXIST));

        // 执行测试
        mockMvc.perform(get("/categories/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ResponseEnum.CATEGORY_NOT_EXIST.getCode()));
    }
}
