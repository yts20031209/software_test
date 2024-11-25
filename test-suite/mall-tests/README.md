# Mall电商系统测试套件

本测试套件为Mall电商系统提供全面的测试覆盖，包括单元测试、集成测试和性能测试。

## 测试架构

```
mall-tests/
├── controller/       # 控制层测试
├── service/         # 服务层测试
├── dao/             # 数据访问层测试
├── integration/     # 集成测试
└── performance/     # 性能测试
```

## 测试类说明

### 1. 数据访问层测试 (DAO Tests)

- **UserMapperTest**
  - 用户数据的CRUD操作测试
  - 用户查询和过滤测试
  - 用户状态更新测试

- **ProductMapperTest**
  - 商品信息的CRUD操作测试
  - 商品库存管理测试
  - 商品分类查询测试

- **OrderMapperTest**
  - 订单创建和更新测试
  - 订单状态流转测试
  - 订单查询和统计测试

- **CategoryMapperTest**
  - 商品类目的CRUD操作测试
  - 类目树形结构测试
  - 类目关系维护测试

- **CartMapperTest**
  - 购物车操作测试
  - 购物车商品管理测试
  - 购物车统计测试

- **ShippingMapperTest**
  - 收货地址管理测试
  - 地址信息验证测试
  - 默认地址设置测试

- **PayInfoMapperTest**
  - 支付信息记录测试
  - 支付状态更新测试
  - 支付记录查询测试

### 2. 服务层测试 (Service Tests)

- **UserServiceTest**
  - 用户注册业务测试
  - 登录认证测试
  - 用户信息管理测试

- **ProductServiceTest**
  - 商品上下架测试
  - 库存管理测试
  - 商品搜索测试

- **OrderServiceTest**
  - 订单创建流程测试
  - 订单支付流程测试
  - 订单取消和退款测试

- **CategoryServiceTest**
  - 类目管理测试
  - 类目树形结构测试
  - 商品分类关联测试

- **CartServiceTest**
  - 购物车添加商品测试
  - 购物车更新测试
  - 购物车结算测试

- **PayServiceTest**
  - 支付创建测试
  - 支付回调处理测试
  - 支付查询测试

### 3. 控制层测试 (Controller Tests)

- **UserControllerTest**
  - 用户接口参数验证
  - 用户会话管理测试
  - 接口权限测试

- **ProductControllerTest**
  - 商品接口测试
  - 商品列表分页测试
  - 商品搜索接口测试

- **OrderControllerTest**
  - 订单接口测试
  - 订单状态变更测试
  - 订单查询接口测试

- **CartControllerTest**
  - 购物车接口测试
  - 购物车商品管理测试
  - 购物车结算接口测试

- **PayControllerTest**
  - 支付接口测试
  - 支付回调接口测试
  - 支付查询接口测试

### 4. 集成测试 (Integration Tests)

- **UserIntegrationTest**
  - 用户注册到登录的完整流程测试
  - 用户信息管理的集成测试

- **ProductIntegrationTest**
  - 商品从创建到上架的完整流程测试
  - 商品库存变更的集成测试

- **OrderIntegrationTest**
  - 从购物车到订单创建的完整流程测试
  - 订单支付流程的集成测试

- **PayIntegrationTest**
  - 支付创建到完成的完整流程测试
  - 支付异步通知的集成测试

### 5. 性能测试 (Performance Tests)

- **MallPerformanceTest**
  - 接口并发性能测试
  - 响应时间测试
  - 系统吞吐量测试
  - 测试场景：
    - 商品列表（100并发）
    - 用户登录（50并发）
    - 订单创建（20并发）
    - 支付创建（10并发）
    - 购物车操作（30并发）

- **DatabasePerformanceTest**
  - 数据库操作性能测试
  - 并发写入测试
  - 查询性能测试
  - 测试场景：
    - 商品批量插入
    - 商品查询性能
    - 订单创建性能
    - 购物车操作性能
    - 类目查询性能

## 运行测试

1. 单元测试运行
```bash
mvn test
```

2. 集成测试运行
```bash
mvn verify
```

3. 性能测试运行
```bash
mvn test -Dtest=MallPerformanceTest
mvn test -Dtest=DatabasePerformanceTest
```

## 测试报告

- 测试报告位置：`target/surefire-reports/`
- 性能测试报告位置：`performance-reports/`

## 注意事项

1. 运行测试前确保数据库配置正确
2. 性能测试需要合适的测试环境
3. 部分测试依赖测试数据的初始化
4. 注意并发测试对系统资源的占用

## 维护说明

1. 添加新功能时及时补充相应测试
2. 定期检查和更新测试用例
3. 保持测试代码的可维护性
4. 关注测试覆盖率报告
