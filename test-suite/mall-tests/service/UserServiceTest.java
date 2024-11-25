package com.imooc.mall.service;

/**
 * 用户服务测试类
 * 对应测试类: com.imooc.mall.service.impl.UserServiceImpl
 * 测试内容：
 * 1. 用户注册功能
 * 2. 用户登录功能
 * 3. 用户信息校验
 */

import com.imooc.mall.dao.UserMapper;
import com.imooc.mall.pojo.User;
import com.imooc.mall.service.impl.UserServiceImpl;
import com.imooc.mall.enums.RoleEnum;
import com.imooc.mall.vo.ResponseVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    @MockBean
    private UserMapper userMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 初始化测试用户数据
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("123456");
        testUser.setEmail("test@example.com");
        testUser.setRole(RoleEnum.CUSTOMER.getCode());
    }

    @Test
    @DisplayName("测试用户注册 - 成功场景")
    void testRegister_Success() {
        // 模拟用户名不存在
        when(userMapper.selectByUsername(testUser.getUsername())).thenReturn(null);
        // 模拟邮箱不存在
        when(userMapper.selectByEmail(testUser.getEmail())).thenReturn(null);
        // 模拟插入成功
        when(userMapper.insertSelective(any(User.class))).thenReturn(1);

        // 执行注册
        ResponseVo<User> response = userService.register(testUser);

        // 验证结果
        assertNotNull(response);
        assertEquals(0, response.getStatus());
        verify(userMapper, times(1)).insertSelective(any(User.class));
    }

    @Test
    @DisplayName("测试用户注册 - 用户名已存在")
    void testRegister_UsernameExists() {
        // 模拟用户名已存在
        when(userMapper.selectByUsername(testUser.getUsername())).thenReturn(testUser);

        // 执行注册
        ResponseVo<User> response = userService.register(testUser);

        // 验证结果
        assertNotNull(response);
        assertNotEquals(0, response.getStatus());
        verify(userMapper, never()).insertSelective(any(User.class));
    }

    @Test
    @DisplayName("测试用户登录 - 成功场景")
    void testLogin_Success() {
        // 模拟用户存在且密码正确
        when(userMapper.selectByUsername(testUser.getUsername())).thenReturn(testUser);

        // 执行登录
        ResponseVo<User> response = userService.login(testUser.getUsername(), testUser.getPassword());

        // 验证结果
        assertNotNull(response);
        assertEquals(0, response.getStatus());
        assertEquals(testUser.getUsername(), response.getData().getUsername());
    }

    @Test
    @DisplayName("测试用户登录 - 用户名不存在")
    void testLogin_UserNotFound() {
        // 模拟用户不存在
        when(userMapper.selectByUsername(testUser.getUsername())).thenReturn(null);

        // 执行登录
        ResponseVo<User> response = userService.login(testUser.getUsername(), testUser.getPassword());

        // 验证结果
        assertNotNull(response);
        assertNotEquals(0, response.getStatus());
    }

    @Test
    @DisplayName("测试用户登录 - 密码错误")
    void testLogin_WrongPassword() {
        // 模拟用户存在
        when(userMapper.selectByUsername(testUser.getUsername())).thenReturn(testUser);

        // 执行登录（使用错误密码）
        ResponseVo<User> response = userService.login(testUser.getUsername(), "wrongpassword");

        // 验证结果
        assertNotNull(response);
        assertNotEquals(0, response.getStatus());
    }

    @Test
    @DisplayName("测试用户信息校验")
    void testValidateUserInfo() {
        // 测试无效的用户名
        assertFalse(userService.validateUsername("a")); // 用户名太短
        assertFalse(userService.validateUsername("thisusernameistoolong")); // 用户名太长
        assertTrue(userService.validateUsername("validuser")); // 有效用户名

        // 测试无效的邮箱
        assertFalse(userService.validateEmail("invalid-email")); // 无效邮箱格式
        assertTrue(userService.validateEmail("valid@example.com")); // 有效邮箱

        // 测试无效的密码
        assertFalse(userService.validatePassword("123")); // 密码太短
        assertTrue(userService.validatePassword("validpass123")); // 有效密码
    }
}
