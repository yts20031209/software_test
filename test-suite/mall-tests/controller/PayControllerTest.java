package com.imooc.mall.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.consts.MallConst;
import com.imooc.mall.form.PayCreateForm;
import com.imooc.mall.pojo.User;
import com.imooc.mall.service.IPayService;
import com.imooc.mall.vo.ResponseVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class PayControllerTest extends MallApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPayService payService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        // 设置模拟session
        User user = new User();
        user.setId(1);
        user.setUsername("test");
        user.setEmail("test@test.com");
        session = new MockHttpSession();
        session.setAttribute(MallConst.CURRENT_USER, user);
    }

    @Test
    @DisplayName("测试创建支付")
    void testCreate() throws Exception {
        // 准备测试数据
        PayCreateForm form = new PayCreateForm();
        form.setOrderNo(12345678L);

        // 模拟服务层返回
        when(payService.create(eq(1), eq(12345678L)))
                .thenReturn(ResponseVo.success());

        // 执行测试
        mockMvc.perform(post("/pay")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0))
                .andExpect(jsonPath("$.msg").value("成功"));
    }

    @Test
    @DisplayName("测试创建支付 - 未登录")
    void testCreateWithoutLogin() throws Exception {
        // 准备测试数据
        PayCreateForm form = new PayCreateForm();
        form.setOrderNo(12345678L);

        // 执行测试
        mockMvc.perform(post("/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(10));
    }

    @Test
    @DisplayName("测试查询支付状态")
    void testQuery() throws Exception {
        // 准备测试数据
        Long orderNo = 12345678L;

        // 模拟服务层返回
        when(payService.queryByOrderNo(eq(orderNo), eq(1)))
                .thenReturn(ResponseVo.success());

        // 执行测试
        mockMvc.perform(get("/pay/query")
                .session(session)
                .param("orderNo", String.valueOf(orderNo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0));
    }

    @Test
    @DisplayName("测试支付异步通知")
    void testAsyncNotify() throws Exception {
        // 准备测试数据
        String platformNumber = "2023112012345678";

        // 模拟服务层返回
        when(payService.notify(eq(platformNumber), any()))
                .thenReturn(ResponseVo.success());

        // 执行测试
        mockMvc.perform(post("/pay/notify")
                .param("trade_no", platformNumber)
                .param("trade_status", "TRADE_SUCCESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0));
    }

    @Test
    @DisplayName("测试查询支付状态 - 订单号为空")
    void testQueryWithEmptyOrderNo() throws Exception {
        // 执行测试
        mockMvc.perform(get("/pay/query")
                .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(1));
    }

    @Test
    @DisplayName("测试创建支付 - 订单号为空")
    void testCreateWithEmptyOrderNo() throws Exception {
        // 准备测试数据
        PayCreateForm form = new PayCreateForm();

        // 执行测试
        mockMvc.perform(post("/pay")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(1));
    }

    @Test
    @DisplayName("测试支付异步通知 - 参数缺失")
    void testAsyncNotifyWithMissingParams() throws Exception {
        // 执行测试
        mockMvc.perform(post("/pay/notify"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(1));
    }

    @Test
    @DisplayName("测试支付异步通知 - 无效的支付状态")
    void testAsyncNotifyWithInvalidStatus() throws Exception {
        // 准备测试数据
        String platformNumber = "2023112012345678";

        // 执行测试
        mockMvc.perform(post("/pay/notify")
                .param("trade_no", platformNumber)
                .param("trade_status", "INVALID_STATUS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(1));
    }
}
