# Mall电商系统支付模块测试说明

本文档详细说明支付模块的测试架构、测试用例和测试策略。

## 测试架构

```
pay_tests/
├── dao/              # 数据访问层测试
│   └── PayInfoMapperTest.java
├── service/          # 服务层测试
│   └── PayServiceTest.java
├── controller/       # 控制层测试
│   └── PayControllerTest.java
└── integration/      # 集成测试
    └── PayIntegrationTest.java
```

## 测试类详细说明

### 1. PayInfoMapperTest（数据访问层测试）

测试支付信息的数据库操作，包括：
- 创建支付记录
- 查询支付信息
- 更新支付状态
- 支付记录统计

主要测试用例：
```java
@Test void testCreatePayInfo()
@Test void testQueryByOrderNo()
@Test void testUpdatePayStatus()
@Test void testQueryByUserId()
@Test void testQueryByPlatformNumber()
```

### 2. PayServiceTest（服务层测试）

测试支付业务逻辑，包括：
- 创建支付订单
- 处理支付回调
- 查询支付状态
- 支付超时处理

主要测试用例：
```java
@Test void testCreatePay()
@Test void testHandlePayNotify()
@Test void testQueryPayStatus()
@Test void testPayTimeout()
@Test void testDuplicatePay()
```

### 3. PayControllerTest（控制层测试）

测试支付相关接口，包括：
- 支付创建接口
- 支付回调接口
- 支付查询接口
- 接口参数验证
- 异常处理

主要测试用例：
```java
@Test void testCreate()
@Test void testAsyncNotify()
@Test void testQuery()
@Test void testCreateWithoutLogin()
@Test void testInvalidOrderNo()
```

### 4. PayIntegrationTest（集成测试）

测试完整支付流程，包括：
- 订单创建到支付完成
- 支付回调处理
- 订单状态变更
- 异常流程处理

主要测试用例：
```java
@Test void testCreatePayFlow()
@Test void testPayNotifyFlow()
@Test void testPayQueryFlow()
@Test void testPayTimeoutFlow()
```

## 测试场景说明

### 1. 正常支付流程
1. 创建订单
2. 发起支付
3. 接收支付回调
4. 更新订单状态
5. 查询支付结果

### 2. 异常场景测试
1. 重复支付
2. 支付超时
3. 订单不存在
4. 订单状态异常
5. 回调签名错误
6. 金额不匹配

### 3. 并发场景测试
1. 并发支付请求
2. 并发回调处理
3. 并发状态查询

## 测试数据准备

### 1. 基础数据
```sql
-- 测试用户
INSERT INTO mall_user (username, password, email, role)
VALUES ('test_user', 'password', 'test@test.com', 0);

-- 测试订单
INSERT INTO mall_order (order_no, user_id, payment, status)
VALUES (12345678, 1, 100.00, 10);
```

### 2. 模拟数据
```java
// 支付信息
PayInfo payInfo = new PayInfo();
payInfo.setOrderNo(12345678L);
payInfo.setUserId(1);
payInfo.setPlatformNumber("TEST_2023112012345678");
payInfo.setPlatformStatus("TRADE_SUCCESS");
```

## 测试环境配置

### 1. 数据库配置
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mall_test
spring.datasource.username=root
spring.datasource.password=123456
```

### 2. 支付配置
```properties
# 支付宝配置
alipay.appId=test_appid
alipay.privateKey=test_private_key
alipay.publicKey=test_public_key
alipay.notifyUrl=http://localhost:8080/pay/notify
```

## 运行测试

### 1. 运行单个测试类
```bash
mvn test -Dtest=PayServiceTest
mvn test -Dtest=PayControllerTest
mvn test -Dtest=PayIntegrationTest
```

### 2. 运行所有支付测试
```bash
mvn test -Dtest=Pay*Test
```

## 测试报告

测试报告位置：
- 单元测试报告：`target/surefire-reports/`
- 覆盖率报告：`target/site/jacoco/`

## 注意事项

1. 测试前检查配置
   - 数据库连接
   - 测试数据准备
   - 环境参数设置

2. 测试数据清理
   - 使用 @Transactional 注解
   - 测试后清理测试数据
   - 避免数据污染

3. 并发测试注意事项
   - 控制并发线程数
   - 监控系统资源
   - 注意数据一致性

## 维护建议

1. 定期更新测试用例
2. 保持测试代码整洁
3. 及时补充新功能测试
4. 关注测试覆盖率
5. 优化测试性能
