package com.imooc.mall.dao;

import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.pojo.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class UserMapperTest extends MallApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("测试插入用户")
    void testInsertUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("123456");
        user.setEmail("test@example.com");
        user.setRole(1);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        int result = userMapper.insertSelective(user);
        assertNotNull(user.getId());
        assertEquals(1, result);
    }

    @Test
    @DisplayName("测试根据用户名查询用户")
    void testSelectByUsername() {
        // 先插入测试数据
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("123456");
        user.setEmail("test@example.com");
        user.setRole(1);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userMapper.insertSelective(user);

        // 测试查询
        User result = userMapper.selectByUsername("testuser");
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    @DisplayName("测试根据用户ID查询用户")
    void testSelectByPrimaryKey() {
        // 先插入测试数据
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("123456");
        user.setEmail("test@example.com");
        user.setRole(1);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userMapper.insertSelective(user);

        // 测试查询
        User result = userMapper.selectByPrimaryKey(user.getId());
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    @DisplayName("测试更新用户信息")
    void testUpdateByPrimaryKey() {
        // 先插入测试数据
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("123456");
        user.setEmail("test@example.com");
        user.setRole(1);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userMapper.insertSelective(user);

        // 更新数据
        user.setEmail("newemail@example.com");
        user.setUpdateTime(new Date());
        int result = userMapper.updateByPrimaryKeySelective(user);

        // 验证更新结果
        assertEquals(1, result);
        User updated = userMapper.selectByPrimaryKey(user.getId());
        assertEquals("newemail@example.com", updated.getEmail());
    }

    @Test
    @DisplayName("测试查询不存在的用户")
    void testSelectNonExistentUser() {
        User result = userMapper.selectByUsername("nonexistent");
        assertNull(result);
    }

    @Test
    @DisplayName("测试用户名重复检查")
    void testUsernameExists() {
        // 先插入测试数据
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("123456");
        user.setEmail("test@example.com");
        user.setRole(1);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userMapper.insertSelective(user);

        // 测试重复用户名
        User duplicate = new User();
        duplicate.setUsername("testuser");
        duplicate.setPassword("123456");
        duplicate.setEmail("another@example.com");
        duplicate.setRole(1);
        duplicate.setCreateTime(new Date());
        duplicate.setUpdateTime(new Date());

        User existingUser = userMapper.selectByUsername(duplicate.getUsername());
        assertNotNull(existingUser);
        assertEquals(user.getId(), existingUser.getId());
    }

    @Test
    @DisplayName("测试邮箱重复检查")
    void testEmailExists() {
        // 先插入测试数据
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("123456");
        user.setEmail("test@example.com");
        user.setRole(1);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userMapper.insertSelective(user);

        // 测试重复邮箱
        User existingUser = userMapper.selectByEmail(user.getEmail());
        assertNotNull(existingUser);
        assertEquals(user.getId(), existingUser.getId());
    }
}
