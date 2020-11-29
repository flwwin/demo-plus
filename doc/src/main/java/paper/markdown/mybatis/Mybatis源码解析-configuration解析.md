###  Mybatis源码解析-Configuration

​	关于Mybatis的配置信息，都是解析在Configuration对象中，找个对象在Mybatis中核心的一个类

   下面一段就是使用mybati的代码

```java
// 获取构建器
    SqlSessionFactoryBuilder factoryBuilder = new SqlSessionFactoryBuilder();
    // 解析XML 并构造会话工厂
    factory = factoryBuilder.build(ExecutorTest.class.getResourceAsStream("/mybatis-config.xml"));
    SqlSession sqlSession = factory.openSession();
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    User user = mapper.selectByid(1);
    System.out.println("user = " + user);
```



####  一、configuration的整体配置体系

configuration的整体配置体系如图所示：

每个配置模块都有对于的解析类，在初始化的时候进行配置解析。

![image-20201018231649067](..\..\image\mybatis源码\configuration的配置体系.png)



#### 2、配置文件解析步骤

整体的流程就是如图，先解析加载配置文件，交给不同的解析器处理，然后返回一个configuration对象。

![image-20201018232808107](..\..\image\mybatis源码\configura解析步骤.png)



每个模块的配置解析对应的类，做了个表格，方便理解。

|      说明       |               文件                |         解析器          |      对象       |
| :-------------: | :-------------------------------: | :---------------------: | :-------------: |
|   主配置文件    |        mybatis-config.xml         |    XMLConfigBuilder     |  Configuration  |
|    映射文件     |      UserMapper.xml（示例）       |    XMLMapperBuilder     | ParameterMap..  |
|  Statement元素  | <select\|insert\|update\|delete/> |   XMLStatementBuilder   | MappedStatement |
| 动态Sql运算元素 |          <if\|foreach/>           |    XMLScriptBuilder     |    BoundSql     |
|    注解元素     |         @Select  @update          | MapperAnnotationBuilder | MappedStatement |



