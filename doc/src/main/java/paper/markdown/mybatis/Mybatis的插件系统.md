#  Mybatis的插件体系

​	Mybatis的插件体系是基于一个动态代理加要给责任链设计模式的运用。

Mybatis插件的源代码都在plugin目录下面。

```java
/**
 * @author Clinton Begin
 */
// 拦截器    // Invocation：调用
  // 这是插件接口，所有插件需要实现该接口
public interface Interceptor {
  /**
   * 该方法内是拦截器拦截到目标方法时的操作
   * @param invocation 拦截到的目标方法的信息
   * @return 经过拦截器处理后的返回结果
   * @throws Throwable
   */
  Object intercept(Invocation invocation) throws Throwable;

  /**
   * 用返回值替代入参对象。
   * 通常情况下，可以调用Plugin的warp方法来完成，因为warp方法能判断目标对象是否需要拦截，并根据判断结果返回相应的对象来替换目标对象
   * @param target MyBatis传入的支持拦截的几个类（ParameterHandler、ResultSetHandler、StatementHandler、Executor）的实例
   * @return 如果当前拦截器要拦截该实例，则返回该实例的代理；如果不需要拦截该实例，则直接返回该实例本身
   */
  default Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }

  /**
   * 设置拦截器的属性
   * @param properties 要给拦截器设置的属性
   */
  default void setProperties(Properties properties) {
    // NOP
  }

}

```

​	首先看看interface接口，所有的插件首先实现该接口。具体的方法的含义再源码中用中文加上了注释。

​	`InterceptorChain`	我们看看这个类，他是所有的插件的一个对象化的类。

```java
 /**
     * 向所有的拦截器链提供目标对象，由拦截器链给出替换目标对象的对象
     * @param target 目标对象，是MyBatis中支持拦截的几个类（ParameterHandler、ResultSetHandler、StatementHandler、Executor）的实例
     * @return 用来替换目标对象的对象
     */
    public Object pluginAll(Object target) {
        // 依次交给每个拦截器完成目标对象的替换工作
        for (Interceptor interceptor : interceptors) {
            target = interceptor.plugin(target); //这样就是层层代理，点责任链的意思了
        }
        return target;
    }
```

​	这个`pluginAll`方法就是给target对象进行代理的方法，通过循环依次对目标对象进行层层代理，生成最终的代理对象。

   Mybatis会在这个几个地方执行ParameterHandler、ResultSetHandler，StatementHandler、Executor这个`pluginAll`方法。所有执行这几个累中的方法会执行拦截器。



###  动态代理类

​	`Plugin`是Mybatis的动态代理类，这个类实现了`InvocationHandler`接口，当代理类执行的时候实际执行就是代理类的invoke方法。

```java
 public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      // 获取该类所有需要拦截的方法
      Set<Method> methods = signatureMap.get(method.getDeclaringClass());
      if (methods != null && methods.contains(method)) {
        // 该方法确实需要被拦截器拦截，因此交给拦截器处理
        return interceptor.intercept(new Invocation(target, method, args));
      }
      // 这说明该方法不需要拦截，交给被代理对象处理
      return method.invoke(target, args);
    } catch (Exception e) {
      throw ExceptionUtil.unwrapThrowable(e);
    }
  }
```



这里就是invoke的源码。先获取目标方法所在类需要拦截的所有方法，然后判断目标方法是否再里面，是的话就交给拦截器执行，不是的话直接执行目标方法。