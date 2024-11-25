package com.imooc.mall.controller;

/**
 * 用户控制器测试类
 * 对应测试类: com.imooc.mall.controller.UserController
 * 测试内容：
 * 1. 用户注册接口
 * 2. 用户登录接口
 * 3. 用户信息获取接口
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.mall.form.UserLoginForm;
import com.imooc.mall.form.UserRegisterForm;
import com.imooc.mall.pojo.User;
import com.imooc.mall.service.impl.UserServiceImpl;
import com.imooc.mall.vo.ResponseVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRegisterForm registerForm;
    private UserLoginForm loginForm;

    @BeforeEach
    void setUp() {
        // 初始化注册表单数据
        registerForm = new UserRegisterForm();
        registerForm.setUsername("testuser");
        registerForm.setPassword("123456");
        registerForm.setEmail("test@example.com");

        // 初始化登录表单数据
        loginForm = new UserLoginForm();
        loginForm.setUsername("testuser");
        loginForm.setPassword("123456");
    }

    @Test
    @DisplayName("测试用户注册 - 成功场景")
    void testRegister_Success() throws Exception {
        // 模拟服务层返回成功
        when(userService.register(any(User.class))).thenReturn(ResponseVo.success());

        // 执行测试
        mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0));

        // 验证服务调用
        verify(userService, times(1)).register(any(User.class));
    }

    @Test
    @DisplayName("测试用户注册 - 参数验证失败")
    void testRegister_ValidationFail() throws Exception {
        // 使用无效的注册数据
        registerForm.setUsername("");
        registerForm.setPassword("123");

        // 执行测试
        mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerForm)))
                .andExpect(status().isBadRequest());

        // 验证服务未被调用
        verify(userService, never()).register(any(User.class));
    }

    @Test
    @DisplayName("测试用户登录 - 成功场景")
    void testLogin_Success() throws Exception {
        // 模拟服务层返回成功
        when(userService.login(anyString(), anyString())).thenReturn(ResponseVo.success());

        // 执行测试
        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0));

        // 验证服务调用
        verify(userService, times(1)).login(anyString(), anyString());
    }

    @Test
    @DisplayName("测试用户登录 - 用户名或密码错误")
    void testLogin_WrongCredentials() throws Exception {
        // 模拟服务层返回失败
        when(userService.login(anyString(), anyString()))
                .thenReturn(ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR));

        // 执行测试
        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ResponseEnum.USERNAME_OR_PASSWORD_ERROR.getCode()));
    }

    @Test
    @DisplayName("测试获取用户信息 - 成功场景")
    void testGetUserInfo_Success() throws Exception {
        // 创建模拟用户数据
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        
        // 模拟服务层返回用户信息
        when(userService.getUserInfo(anyInt())).thenReturn(ResponseVo.success(user));

        // 执行测试
        mockMvc.perform(get("/user")
                .sessionAttr(MallConst.CURRENT_USER, user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    @DisplayName("测试登出功能")
    void testLogout() throws Exception {
        // 执行测试
        mockMvc.perform(post("/user/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0));
    }
}
