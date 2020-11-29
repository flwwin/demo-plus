#  Mybatis的缓存体系

​	Mybatis的缓存体系有两种，1：一级缓存，2：二级缓存

一级缓存作用域会话，也就是是sqlSession；二级缓存是跨会话的。一级缓存默认开启，不可以关闭。

二级缓存的作用域是sqlSessionFactory，默认是关闭的，需要在mybatis-config.xml指定开启，在一个会话完成后。



###  一级缓存

一级缓存在创建Excutor执行器的时候，创建。

```java
 public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
    executorType = executorType == null ? defaultExecutorType : executorType;
    executorType = executorType == null ? ExecutorType.SIMPLE : executorType;
    Executor executor;
    // 根据数据操作类型创建实际执行器
    if (ExecutorType.BATCH == executorType) {
      executor = new BatchExecutor(this, transaction);
    } else if (ExecutorType.REUSE == executorType) {
      executor = new ReuseExecutor(this, transaction);
    } else {
      executor = new SimpleExecutor(this, transaction);
    }
    // 根据配置文件中settings节点cacheEnabled配置项确定是否启用缓存
    if (cacheEnabled) { // 如果配置启用缓存
      // 使用CachingExecutor装饰实际执行器
      executor = new CachingExecutor(executor);
    }
    // 为执行器增加拦截器（插件），以启用各个拦截器的功能，同时执行一遍拦截器
    executor = (Executor) interceptorChain.pluginAll(executor);
    return executor;
  }
```

从源码可以看出来，创建`executor`时候会判断是否开启缓存，默认`cacheEnabled`就是开启的。然后将原来的`executor`进行包装，这里采用就是装饰器模式。下图就是`cache`接口的类图

<img src="D:\opensorce\demo-plus\doc\src\main\java\paper\image\mybatis源码\cache接口类图.png" style="zoom:75%;" />

`CachingExecutor`中有熟悉delegate,实现对其他exector的装饰。

#### 1.2：缓存中的key

​	我们可以看`CachingExecutor`中的query方法。

```java
 @Override
  public <E> List<E> query(MappedStatement ms, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
    BoundSql boundSql = ms.getBoundSql(parameterObject);
    CacheKey key = createCacheKey(ms, parameterObject, rowBounds, boundSql);
    return query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
  }
```

`createCacheKey`就是创建缓存key的方法。源码看出作为一个key需要

1：userMapper的相对路径+名称

2：offset

3：limit

4：sql

5：参数

只有这几参数相同才能命中缓存。其实实际作为key还回取hashcode，可以在`CacheKey`对象中研究，这里不展开了。

![image-20201025124655435](D:\opensorce\demo-plus\doc\src\main\java\paper\image\mybatis源码\创建cacheKye.png)



###  二级缓存

二级缓存在在mybatis-config.xml可以配置

```xml
<settings>
    <!--  开启驼峰匹配：完成经典的数据库命名到java属性的映射
                      相当于去掉数据中的名字的下划线，和java进行匹配
    -->
    <!--<setting name="cacheEnabled" value="true" /> 默认开启，可以不用配置-->
    <setting name="mapUnderscoreToCamelCase" value="true"/>
</settings>
```

在mapper接口上加上注解`@CacheNamespace`

```java
@CacheNamespace
public interface UserMapper {
```

```java
  @Test
  public void test02() {
    // 二级缓存是一个跨会话
    SqlSession session01 = factory.openSession();
    SqlSession session02 = factory.openSession();
    UserMapper mapper01 = session01.getMapper(UserMapper.class);
    UserMapper mapper02 = session01.getMapper(UserMapper.class);
    mapper01.selectByid(1);
    mapper02.selectByid(1);
  }
```

这里再一个会话中执行同一个sql，看看缓存命中结果

```java
13:38:08,271 DEBUG  - Cache Hit Ratio [mapper.UserMapper]: 0.0
13:38:08,301 DEBUG  - ==>  Preparing: select * from users where id=? 
13:38:08,321 DEBUG  - ==> Parameters: 1(Integer)
13:38:08,337 DEBUG  - <==      Total: 1
13:38:08,339 DEBUG  - Cache Hit Ratio [mapper.UserMapper]: 0.0
```

结果只打印了一条sql， Cache Hit Ratio [mapper.UserMapper]: 0.0是一级缓存命中率，为0.0说明是没有命中一级缓存，也是间接说明，一级缓存不能跨会话的

