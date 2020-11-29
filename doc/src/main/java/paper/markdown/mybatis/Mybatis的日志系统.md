#  Mybatis的日志系统

​	首先说一下，在项目中需要打印Mybatis日志需要做那些配置

在项目中引入相应的日志体系，然后在`mybatis-config.xml`配置文件中配置日志体系

```xml
<setting name="logImpl" value="LOG4J" />
```

Mybatis定义要给`log`接口，实现类如下图；

![](D:\opensorce\demo-plus\doc\src\main\java\paper\image\mybatis源码\Log接口实现类.png)

每个实现类都是支持的是指框架

​	需要打印日志的时候，通过`LogFactory`类中的`getLog`方法获取打印日志的实际方法。

可以看下``LogFactory``的源码

```java

public final class LogFactory {

  /**
   * Marker to be used by logging implementations that support markers.
   */
  public static final String MARKER = "MYBATIS";

  private static Constructor<? extends Log> logConstructor;

  static {
    //这里注入顺序就是log的设置的优先级
    tryImplementation(LogFactory::useSlf4jLogging);
    tryImplementation(LogFactory::useCommonsLogging);
    tryImplementation(LogFactory::useLog4J2Logging);
    tryImplementation(LogFactory::useLog4JLogging);
    tryImplementation(LogFactory::useJdkLogging);
    tryImplementation(LogFactory::useNoLogging);
  }

  private LogFactory() {
    // disable construction
  }

  public static Log getLog(Class<?> aClass) {
    return getLog(aClass.getName());
  }

  public static Log getLog(String logger) {
    try {
      return logConstructor.newInstance(logger);
    } catch (Throwable t) {
      throw new LogException("Error creating logger for logger " + logger + ".  Cause: " + t, t);
    }
  }

  public static synchronized void useCustomLogging(Class<? extends Log> clazz) {
    setImplementation(clazz);
  }

  public static synchronized void useSlf4jLogging() {
    setImplementation(org.apache.ibatis.logging.slf4j.Slf4jImpl.class);
  }

  public static synchronized void useCommonsLogging() {
    setImplementation(org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl.class);
  }

  public static synchronized void useLog4JLogging() {
    setImplementation(org.apache.ibatis.logging.log4j.Log4jImpl.class);
  }

  public static synchronized void useLog4J2Logging() {
    setImplementation(org.apache.ibatis.logging.log4j2.Log4j2Impl.class);
  }

  public static synchronized void useJdkLogging() {
    setImplementation(org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl.class);
  }

  public static synchronized void useStdOutLogging() {
    setImplementation(org.apache.ibatis.logging.stdout.StdOutImpl.class);
  }

  public static synchronized void useNoLogging() {
    setImplementation(org.apache.ibatis.logging.nologging.NoLoggingImpl.class);
  }


  /**
   * 尝试实现一个日志实例
   * @param runnable 用来尝试实现日志实例的操作
   */
  private static void tryImplementation(Runnable runnable) {
    if (logConstructor == null) {
      try {
        runnable.run();
      } catch (Throwable t) {
        // ignore
      }
    }
  }

  /**
   * 设置日志实现
   * @param implClass 日志实现类
   */
  private static void setImplementation(Class<? extends Log> implClass) {
    try {
      // 当前日志实现类的构造方法
      Constructor<? extends Log> candidate = implClass.getConstructor(String.class);
      // 尝试生成日志实现类的实例
      Log log = candidate.newInstance(LogFactory.class.getName());
      if (log.isDebugEnabled()) {
        log.debug("Logging initialized using '" + implClass + "' adapter.");
      }
      // 如果运行到这里，说明没有异常发生。则实例化日志实现类成功。
      logConstructor = candidate;
    } catch (Throwable t) {
      throw new LogException("Error setting Log implementation.  Cause: " + t, t);
    }
  }

}

```

源码上面都加上了中文注释，还是比较容易看明白的

​	首先通过静态代码块初始化所有log接口实现类，获得日志实现

```java
 try {
      // 当前日志实现类的构造方法
      Constructor<? extends Log> candidate = implClass.getConstructor(String.class);
      // 尝试生成日志实现类的实例
      Log log = candidate.newInstance(LogFactory.class.getName());
      if (log.isDebugEnabled()) {
        log.debug("Logging initialized using '" + implClass + "' adapter.");
      }
      // 如果运行到这里，说明没有异常发生。则实例化日志实现类成功。
      logConstructor = candidate;
    } catch (Throwable t) {
      throw new LogException("Error setting Log implementation.  Cause: " + t, t);
    }
```

获取入职实现失败，就会抛出异常，但是在上层cache，并且没有做任何处理，静态代码块继续执行。

```java
  /**
   * 尝试实现一个日志实例
   * @param runnable 用来尝试实现日志实例的操作
   */
  private static void tryImplementation(Runnable runnable) {
    if (logConstructor == null) {
      try {
        runnable.run();
      } catch (Throwable t) {
        // ignore
      }
    }
  }
```

这样，静态代码块的顺序也是mybatis加载日志框架的优先级,这个在代码注释中也有说到。

```java

    tryImplementation(LogFactory::useCommonsLogging);
    tryImplementation(LogFactory::useLog4J2Logging);
    tryImplementation(LogFactory::useLog4JLogging);
    tryImplementation(LogFactory::useJdkLogging);
    tryImplementation(LogFactory::useNoLogging);
```



###  日志实现类

​	我们继续看看log接口实现类，采用的适配器模式，每个日志实现类都持有第三方接口的引用。

```java
package org.apache.ibatis.logging.log4j;

import org.apache.ibatis.logging.Log;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Eduardo Macarron
 * 实现包
 */
public class Log4jImpl implements Log {

  private static final String FQCN = Log4jImpl.class.getName();

  private final Logger log;

  public Log4jImpl(String clazz) {
    log = Logger.getLogger(clazz);
  }

  @Override
  public boolean isDebugEnabled() {
    return log.isDebugEnabled();
  }

  @Override
  public boolean isTraceEnabled() {
    return log.isTraceEnabled();
  }

  @Override
  public void error(String s, Throwable e) {
    log.log(FQCN, Level.ERROR, s, e);
  }

  @Override
  public void error(String s) {
    log.log(FQCN, Level.ERROR, s, null);
  }

  @Override
  public void debug(String s) {
    log.log(FQCN, Level.DEBUG, s, null);
  }

  @Override
  public void trace(String s) {
    log.log(FQCN, Level.TRACE, s, null);
  }

  @Override
  public void warn(String s) {
    log.log(FQCN, Level.WARN, s, null);
  }

}

```

​	当系统初始化的时候，创建日志实例，加入没有加入日志的实现类，就会报错，继续执行下一个日志框架的初始化。