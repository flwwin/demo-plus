## mybatis-spring原理解析

​	没有spring使用Mybatis的步骤是

1：创建SqlSessionFactoryBuilder

2:    通过SqlSessionFactoryBuilder创建要给SqlSessionFactory

3:	创建SqlSession

4:   获取Mapper的代理对象

5：执行获取结果

很多都是重复步骤，可以交给spring去管理

​	`mybatis-spring`就是帮助处理这些工作，



Mybatis-spring主要做的内容包含：

1. mybatis相关类 “Spring”化，都注册到Spring 容器中，对mapper额外提供批量扫描功能。
2. 事务对接Spring，SqlSession交由Spring事务管理。

我们从使用过程到执行过程分步讲解原理。

###  1、初始化过程

入口：`SqlSessionFactoryBean`

使用Mybatis-spring，需要主动配置SqlSessionFactoryBean，所以配置初始化的流程从这里开始。

SqlSessionFactoryBean实现了`FactoryBean`,`InitializingBean`,`ApplicationListener`

- FactoryBean，用于自定义Bean实例化逻辑，并注册到Spring容器。SqlSessionFactory是一个很重的类，实例的化的过程比较复杂繁琐。

- ApplicationListener，监听的是ContextRefreshedEvent事件，配置了快速失败时检查MapperedStatement是否加载完毕。
- InitializingBean，真正开始实例化的时机，开始构建SqlSessionFactory。

####  1.1、SqlSessionFactory初始化

```java
@Override
public void afterPropertiesSet() throws Exception {
  notNull(dataSource, "Property 'dataSource' is required");
  notNull(sqlSessionFactoryBuilder, "Property 'sqlSessionFactoryBuilder' is required");
  state((configuration == null && configLocation == null) || !(configuration != null && configLocation != null),
      "Property 'configuration' and 'configLocation' can not specified with together");
 
  // 开始构造
  this.sqlSessionFactory = buildSqlSessionFactory();
}
```



构造过程：也就是解析配置文件，构造Configuration过程

```java
protected SqlSessionFactory buildSqlSessionFactory() throws Exception {
 
  final Configuration targetConfiguration;
 
  XMLConfigBuilder xmlConfigBuilder = null;
    // 指定了mybatis-config.xml的路径时
    else if (this.configLocation != null) {
      // 构造xml解析器
      xmlConfigBuilder = new XMLConfigBuilder(this.configLocation.getInputStream(), null, this.configurationProperties);
      targetConfiguration = xmlConfigBuilder.getConfiguration();
    }
    // 啥都没指定，直接实例化Configuration，使用默认配置
    else {
      LOGGER.debug(
          () -> "Property 'configuration' or 'configLocation' not specified, using default MyBatis Configuration");
      targetConfiguration = new Configuration();
      Optional.ofNullable(this.configurationProperties).ifPresent(targetConfiguration::setVariables);
    }
 
  // 默认配置设置
  Optional.ofNullable(this.objectFactory).ifPresent(targetConfiguration::setObjectFactory);
  Optional.ofNullable(this.objectWrapperFactory).ifPresent(targetConfiguration::setObjectWrapperFactory);
  Optional.ofNullable(this.vfs).ifPresent(targetConfiguration::setVfsImpl);
 
  if (hasLength(this.typeAliasesPackage)) {
    scanClasses(this.typeAliasesPackage, this.typeAliasesSuperType).stream()
        .filter(clazz -> !clazz.isAnonymousClass()).filter(clazz -> !clazz.isInterface())
        .filter(clazz -> !clazz.isMemberClass()).forEach(targetConfiguration.getTypeAliasRegistry()::registerAlias);
  }
 
  // 别名注册
  if (!isEmpty(this.typeAliases)) {
    Stream.of(this.typeAliases).forEach(typeAlias -> {
      targetConfiguration.getTypeAliasRegistry().registerAlias(typeAlias);
      LOGGER.debug(() -> "Registered type alias: '" + typeAlias + "'");
    });
  }
 
  // 插件注册
  if (!isEmpty(this.plugins)) {
    Stream.of(this.plugins).forEach(plugin -> {
      targetConfiguration.addInterceptor(plugin);
      LOGGER.debug(() -> "Registered plugin: '" + plugin + "'");
    });
  }
 
  // 自定义类型处理器注册
  if (hasLength(this.typeHandlersPackage)) {
    scanClasses(this.typeHandlersPackage, TypeHandler.class).stream().filter(clazz -> !clazz.isAnonymousClass())
        .filter(clazz -> !clazz.isInterface()).filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
        .forEach(targetConfiguration.getTypeHandlerRegistry()::register);
  }
 
  if (!isEmpty(this.typeHandlers)) {
    Stream.of(this.typeHandlers).forEach(typeHandler -> {
      targetConfiguration.getTypeHandlerRegistry().register(typeHandler);
      LOGGER.debug(() -> "Registered type handler: '" + typeHandler + "'");
    });
  }
 
  if (!isEmpty(this.scriptingLanguageDrivers)) {
    Stream.of(this.scriptingLanguageDrivers).forEach(languageDriver -> {
      targetConfiguration.getLanguageRegistry().register(languageDriver);
      LOGGER.debug(() -> "Registered scripting language driver: '" + languageDriver + "'");
    });
  }
  Optional.ofNullable(this.defaultScriptingLanguageDriver)
      .ifPresent(targetConfiguration::setDefaultScriptingLanguage);
 
  if (this.databaseIdProvider != null) {// fix #64 set databaseId before parse mapper xmls
    try {
      targetConfiguration.setDatabaseId(this.databaseIdProvider.getDatabaseId(this.dataSource));
    } catch (SQLException e) {
      throw new NestedIOException("Failed getting a databaseId", e);
    }
  }
 
  Optional.ofNullable(this.cache).ifPresent(targetConfiguration::addCache);
 
  // 执行了Config路径时需要解析
  if (xmlConfigBuilder != null) {
    try {
      xmlConfigBuilder.parse();
      LOGGER.debug(() -> "Parsed configuration file: '" + this.configLocation + "'");
    } catch (Exception ex) {
      throw new NestedIOException("Failed to parse config resource: " + this.configLocation, ex);
    } finally {
      ErrorContext.instance().reset();
    }
  }
 
  targetConfiguration.setEnvironment(new Environment(this.environment,
      this.transactionFactory == null ? new SpringManagedTransactionFactory() : this.transactionFactory,
      this.dataSource));
 
  // mapper.xml文件地址
  if (this.mapperLocations != null) {
    if (this.mapperLocations.length == 0) {
      LOGGER.warn(() -> "Property 'mapperLocations' was specified but matching resources are not found.");
    } else {
      for (Resource mapperLocation : this.mapperLocations) {
        if (mapperLocation == null) {
          continue;
        }
        try {
          // 遍历解析xml并注册
          XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(mapperLocation.getInputStream(),
              targetConfiguration, mapperLocation.toString(), targetConfiguration.getSqlFragments());
          xmlMapperBuilder.parse();
        } catch (Exception e) {
          throw new NestedIOException("Failed to parse mapping resource: '" + mapperLocation + "'", e);
        } finally {
          ErrorContext.instance().reset();
        }
        LOGGER.debug(() -> "Parsed mapper file: '" + mapperLocation + "'");
      }
    }
  } else {
    LOGGER.debug(() -> "Property 'mapperLocations' was not specified.");
  }
 
  // 将设置好的Configuration交由SqlSessionFactoryBuilder进行构造
  return this.sqlSessionFactoryBuilder.build(targetConfiguration);
}
```

  	SqlSessionFactory就已经初始化完毕了，SqlSessionFactory的实例单例保存在Spring容器中，具体在Mybatis的哪个类中自动使用，稍后再说。



####  1.2 Mapper扫描、注册

mapper扫描路径配置常见有两种方式

- MapperScan注解，标注在应用的根目录下，自动扫描自包的Mapper文件。
- 手动配置MapperScannerConfigurer，这个Bean，也是指定扫描路径。

因为MapperScan注解最终也是生成MapperScannerConfigurer类的实例，其使用也更方便，那么就从MapperScan开始介绍。

过程概览：

1. MapperScan注解通过@Import(MapperScannerRegistrar.class)，导入了MapperScannerRegistrar类
2. MapperScannerRegistrar获得MapperScan注解上的值（主要是basePackages），构造MapperScannerConfigurer的BeanDefinition并注册。
3. postProcessBeanDefinitionRegistry回调进行扫描动作，实例化临时的ClassPathMapperScanner，借助其完成扫描注册。
4. ClassPathMapperScanner借助Spring的ClassPathBeanDefinitionScanner扫描Mapper,替换每个Mapper BeanDefinition的信息。完成Mapper的 mybatis和Spring的对接。

##### 1.2.1 MapperScan

​	主要作用就是：主要作用就是提供可配置路径，导入MapperScannerRegistrar。比较简单。

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
//导入Bean，
@Import(MapperScannerRegistrar.class)
@Repeatable(MapperScans.class)
public @interface MapperScan 
```



##### 1.2.2 MapperScannerRegistrar

​	MapperScannerRegistrar就是一个mapper扫描注册器，用于读取注解上的值并注册一个Mapper扫描配置类。

```java
public class MapperScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware
```

借助Spring的import机制，构造并注册MapperScannerConfigurerBeanDefinition

```java
void registerBeanDefinitions(AnnotationAttributes annoAttrs, BeanDefinitionRegistry registry, String beanName) {
 
  // mapperScanner注解属性构造，构造MapperScannerConfigurer的BeanDefinition
  BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
  builder.addPropertyValue("processPropertyPlaceHolders", true);
 
  Class<? extends Annotation> annotationClass = annoAttrs.getClass("annotationClass");
  if (!Annotation.class.equals(annotationClass)) {
    builder.addPropertyValue("annotationClass", annotationClass);
  }
 
  Class<?> markerInterface = annoAttrs.getClass("markerInterface");
  if (!Class.class.equals(markerInterface)) {
    builder.addPropertyValue("markerInterface", markerInterface);
  }
 
  Class<? extends BeanNameGenerator> generatorClass = annoAttrs.getClass("nameGenerator");
  if (!BeanNameGenerator.class.equals(generatorClass)) {
    builder.addPropertyValue("nameGenerator", BeanUtils.instantiateClass(generatorClass));
  }
 
  Class<? extends MapperFactoryBean> mapperFactoryBeanClass = annoAttrs.getClass("factoryBean");
  if (!MapperFactoryBean.class.equals(mapperFactoryBeanClass)) {
    builder.addPropertyValue("mapperFactoryBeanClass", mapperFactoryBeanClass);
  }
 
  String sqlSessionTemplateRef = annoAttrs.getString("sqlSessionTemplateRef");
  if (StringUtils.hasText(sqlSessionTemplateRef)) {
    builder.addPropertyValue("sqlSessionTemplateBeanName", annoAttrs.getString("sqlSessionTemplateRef"));
  }
 
  String sqlSessionFactoryRef = annoAttrs.getString("sqlSessionFactoryRef");
  if (StringUtils.hasText(sqlSessionFactoryRef)) {
    builder.addPropertyValue("sqlSessionFactoryBeanName", annoAttrs.getString("sqlSessionFactoryRef"));
  }
 
  List<String> basePackages = new ArrayList<>();
  basePackages.addAll(
      Arrays.stream(annoAttrs.getStringArray("value")).filter(StringUtils::hasText).collect(Collectors.toList()));
 
  basePackages.addAll(Arrays.stream(annoAttrs.getStringArray("basePackages")).filter(StringUtils::hasText)
      .collect(Collectors.toList()));
 
  basePackages.addAll(Arrays.stream(annoAttrs.getClassArray("basePackageClasses")).map(ClassUtils::getPackageName)
      .collect(Collectors.toList()));
 
  String lazyInitialization = annoAttrs.getString("lazyInitialization");
  if (StringUtils.hasText(lazyInitialization)) {
    builder.addPropertyValue("lazyInitialization", lazyInitialization);
  }
 
  builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(basePackages));
 
  registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
 
}
```



#####  1.2.3 MapperScannerConfigurer

```java
public class MapperScannerConfigurer
    implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware, BeanNameAware 
```



```java
@Override
public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
  // 占位符替换
  if (this.processPropertyPlaceHolders) {
    processPropertyPlaceHolders();
  }
 
  // 构造扫描器进行扫描包，批量构造mapper
  ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
  scanner.setAddToConfig(this.addToConfig);
  scanner.setAnnotationClass(this.annotationClass);
  scanner.setMarkerInterface(this.markerInterface);
  scanner.setSqlSessionFactory(this.sqlSessionFactory);
  scanner.setSqlSessionTemplate(this.sqlSessionTemplate);
  scanner.setSqlSessionFactoryBeanName(this.sqlSessionFactoryBeanName);
  scanner.setSqlSessionTemplateBeanName(this.sqlSessionTemplateBeanName);
  scanner.setResourceLoader(this.applicationContext);
  scanner.setBeanNameGenerator(this.nameGenerator);
  scanner.setMapperFactoryBeanClass(this.mapperFactoryBeanClass);
  if (StringUtils.hasText(lazyInitialization)) {
    scanner.setLazyInitialization(Boolean.valueOf(lazyInitialization));
  }
  scanner.registerFilters();
  // 执行扫描注册
  scanner.scan(
      StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
}
```

主要点就在于构造扫描器。扫描Mapper的BeanDefinition并注册。

#####  1.2.4 ClassPathMapperScanner

```java
public class ClassPathMapperScanner extends ClassPathBeanDefinitionScanner
```

继承Spring的扫描工具类，覆盖doScan完成自定义部分。

```java
@Override
public Set<BeanDefinitionHolder> doScan(String... basePackages) {
  // 借助spring的 ClassPathBeanDefinitionScanner 扫描出指定路径下的所有Mapper的Bean定义
  Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
 
  if (beanDefinitions.isEmpty()) {
    LOGGER.warn(() -> "No MyBatis mapper was found in '" + Arrays.toString(basePackages)
        + "' package. Please check your configuration.");
  } else {
    // 处理mapper bean定义
    processBeanDefinitions(beanDefinitions);
  }
 
  return beanDefinitions;
}
```

```java
private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
  GenericBeanDefinition definition;
  for (BeanDefinitionHolder holder : beanDefinitions) {
    definition = (GenericBeanDefinition) holder.getBeanDefinition();
    String beanClassName = definition.getBeanClassName();
    LOGGER.debug(() -> "Creating MapperFactoryBean with name '" + holder.getBeanName() + "' and '" + beanClassName
        + "' mapperInterface");
 
    // 改变扫描到的Mapper原本的BeanDefinition，beanClass都使用MapperFactoryBean.class
    // 目的是为了创建Mapper的代理对象
    // the mapper interface is the original class of the bean
    // but, the actual class of the bean is MapperFactoryBean
    definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName); // issue #59
    definition.setBeanClass(this.mapperFactoryBeanClass);
 
    definition.getPropertyValues().add("addToConfig", this.addToConfig);
 
    // 是否显示指定了SqlSessionFactory
    boolean explicitFactoryUsed = false;
 
    if (StringUtils.hasText(this.sqlSessionFactoryBeanName)) {
      definition.getPropertyValues().add("sqlSessionFactory",
          new RuntimeBeanReference(this.sqlSessionFactoryBeanName));
      explicitFactoryUsed = true;
    } else if (this.sqlSessionFactory != null) {
      definition.getPropertyValues().add("sqlSessionFactory", this.sqlSessionFactory);
      explicitFactoryUsed = true;
    }
 
    if (StringUtils.hasText(this.sqlSessionTemplateBeanName)) {
      if (explicitFactoryUsed) {
        LOGGER.warn(
            () -> "Cannot use both: sqlSessionTemplate and sqlSessionFactory together. sqlSessionFactory is ignored.");
      }
      definition.getPropertyValues().add("sqlSessionTemplate",
          new RuntimeBeanReference(this.sqlSessionTemplateBeanName));
      explicitFactoryUsed = true;
    } else if (this.sqlSessionTemplate != null) {
      if (explicitFactoryUsed) {
        LOGGER.warn(
            () -> "Cannot use both: sqlSessionTemplate and sqlSessionFactory together. sqlSessionFactory is ignored.");
      }
      definition.getPropertyValues().add("sqlSessionTemplate", this.sqlSessionTemplate);
      explicitFactoryUsed = true;
    }
 
    // 如果未显示指定SqlSessionFactory，则启用自动注入
    if (!explicitFactoryUsed) {
      LOGGER.debug(() -> "Enabling autowire by type for MapperFactoryBean with name '" + holder.getBeanName() + "'.");
      definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
    }
    definition.setLazyInit(lazyInitialization);
  }
}
```

这里有两个点：

1. 扫描的Mapper BeanDefinition会将BeanClass统一替换成MapperFactory.class，是为了为每一个Mapper都创建代理类，而不是其接口对应类型的实例。
2. 未直接指定SqlSessionFactory（往往使用过程不会指定），则会设置BeanDefinition的模式为自动注入，即Spring提供的不适用@Autowire、@Resource也能自动注入属性，解决了自动注入SqlSessionFactory的问题。



###  执行过程

MapperFactoryBean负责Mapper实例的创建，来看看MapperFactoryBean这个类。

类图如下：

- 继承FactoryBean是为了自定义Mapper实例化的操作。
- 继承SqlSessionDaoSupport是为了管理SqlSessionFactory。

```java
public class MapperFactoryBean<T> extends SqlSessionDaoSupport implements FactoryBean<T> {
 
  private Class<T> mapperInterface;
 
  private boolean addToConfig = true;
 
  public MapperFactoryBean() {
    // intentionally empty
  }
 
  public MapperFactoryBean(Class<T> mapperInterface) {
    this.mapperInterface = mapperInterface;
  }
 
  /**
   * 为mapper类创建统一的代理对象
   * {@inheritDoc}
   */
  @Override
  public T getObject() throws Exception {
    return getSqlSession().getMapper(this.mapperInterface);
  }
  
  ...
}
```

获取Mapper实例的逻辑和单独使用Mapper的时候相同，获取SqlSession，使用SqlSession为mapper创建代理对象。

getSqlSession()方法在父类SqlSessionDaoSupport中。

```java
public abstract class SqlSessionDaoSupport extends DaoSupport {
 
  private SqlSessionTemplate sqlSessionTemplate;
 
  public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
    if (this.sqlSessionTemplate == null || sqlSessionFactory != this.sqlSessionTemplate.getSqlSessionFactory()) {
      this.sqlSessionTemplate = createSqlSessionTemplate(sqlSessionFactory);
    }
  }
  @SuppressWarnings("WeakerAccess")
  protected SqlSessionTemplate createSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
    return new SqlSessionTemplate(sqlSessionFactory);
  }
 
 
  public SqlSession getSqlSession() {
    return this.sqlSessionTemplate;
  }
 
}
```

####  2.2  SqlSession实例创建

​		SqlSessionTemplate属性就是在MapperFactoryBean实例化的时候自动注入进去的，我们看一看SqlSessionTemplate。实现了SqlSession，可以代表成一个SqlSession，主要还是为了管理SqlSession的代理类。

```java
public class SqlSessionTemplate implements SqlSession, DisposableBean {
 
  private final SqlSessionFactory sqlSessionFactory;
 
  private final ExecutorType executorType;
  // SqlSession代理类
  private final SqlSession sqlSessionProxy;
 
  // 异常翻译器，将mybatis的异常转换成Spring的异常
  private final PersistenceExceptionTranslator exceptionTranslator;
}
```



来看下SqlSessionTemplate的构造方法。

sqlSessionProxy是真正的SqlSession代理类，SqlSession相关方法的实现，SqlSessionTemplate都是委托给SqlSessionProxy实现的。

```java
public SqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType,
    PersistenceExceptionTranslator exceptionTranslator) {
 
  notNull(sqlSessionFactory, "Property 'sqlSessionFactory' is required");
  notNull(executorType, "Property 'executorType' is required");
 
  this.sqlSessionFactory = sqlSessionFactory;
  this.executorType = executorType;
  this.exceptionTranslator = exceptionTranslator;
  // 创建SqlSession代理类实例
  this.sqlSessionProxy = (SqlSession) newProxyInstance(SqlSessionFactory.class.getClassLoader(),
      new Class[] { SqlSession.class }, new SqlSessionInterceptor());
}
```

重点在于SqlSession代理类，这个类使用了JDK的动态代理，来看看究竟拦截方法并做了什么。

让我们看看SqlSessionInterceptor

```java
private class SqlSessionInterceptor implements InvocationHandler {
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // 获取真正的SqlSession实例
    SqlSession sqlSession = SqlSessionUtils.getSqlSession(SqlSessionTemplate.this.sqlSessionFactory,
        SqlSessionTemplate.this.executorType, SqlSessionTemplate.this.exceptionTranslator);
    try {
      // 执行SqlSession的方法（举例：sqlSession.getMapper(XXX.class)）
      Object result = method.invoke(sqlSession, args);
      if (!isSqlSessionTransactional(sqlSession, SqlSessionTemplate.this.sqlSessionFactory)) {
       // 如果SqlSession未处于spring事务，那设置未自动提交
        sqlSession.commit(true);
      }
      return result;
    } catch (Throwable t) {
      Throwable unwrapped = unwrapThrowable(t);
      if (SqlSessionTemplate.this.exceptionTranslator != null && unwrapped instanceof PersistenceException) {
        // release the connection to avoid a deadlock if the translator is no loaded. See issue #22
        closeSqlSession(sqlSession, SqlSessionTemplate.this.sqlSessionFactory);
        sqlSession = null;
        Throwable translated = SqlSessionTemplate.this.exceptionTranslator
            .translateExceptionIfPossible((PersistenceException) unwrapped);
        if (translated != null) {
          unwrapped = translated;
        }
      }
      throw unwrapped;
    } finally {
      if (sqlSession != null) {
        closeSqlSession(sqlSession, SqlSessionTemplate.this.sqlSessionFactory);
      }
    }
  }
}
```

SqlSessionInterceptor是SqlSessionTemplate的内部类，实现了InvocationHandler（JDK动态代理必备）。

SqlSessionTemplate伪装成SqlSession，实现其接口，内部实现委托给SqlSessionProxy完成。

####  2.3 事务

事务控制着Connection，与Connection对应的则是SqlSession。看看mybatis-spring怎么对接的Spring事务去控制SqlSession。

在上面SqlSessionInterceptor的invoke部分，获取SqlSession，调用的是

```java
SqlSession sqlSession = SqlSessionUtils.getSqlSession(SqlSessionTemplate.this.sqlSessionFactory,
    SqlSessionTemplate.this.executorType, SqlSessionTemplate.this.exceptionTranslator);
```

继续看下内部如何实现

```java
public static SqlSession getSqlSession(SqlSessionFactory sessionFactory, ExecutorType executorType,
    PersistenceExceptionTranslator exceptionTranslator) {
 
  notNull(sessionFactory, NO_SQL_SESSION_FACTORY_SPECIFIED);
  notNull(executorType, NO_EXECUTOR_TYPE_SPECIFIED);
 
  // 从spring 事务中获取SqlSession
  SqlSessionHolder holder = (SqlSessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
 
  SqlSession session = sessionHolder(executorType, holder);
  if (session != null) {
    return session;
  }
 
  LOGGER.debug(() -> "Creating a new SqlSession");
  session = sessionFactory.openSession(executorType);
 
  // 将SqlSession注册到 spring事务中
  registerSessionHolder(sessionFactory, executorType, exceptionTranslator, session);
 
  return session;
}
```

这里简单的提一点Spring事务，Spring会使用ThreadLocal保存事务的信息，在这里mybatis=spring借助TransactionSynchronizationManager.getResource/bindResource来获取/绑定SqlSession。

```java
private static void registerSessionHolder(SqlSessionFactory sessionFactory, ExecutorType executorType,
    PersistenceExceptionTranslator exceptionTranslator, SqlSession session) {
  SqlSessionHolder holder;
  // 判断当前同步是否激活
  if (TransactionSynchronizationManager.isSynchronizationActive()) {
    Environment environment = sessionFactory.getConfiguration().getEnvironment();
 
    // 如果是SpringManagedTransactionFactory则进行spring事务对接
    if (environment.getTransactionFactory() instanceof SpringManagedTransactionFactory) {
      LOGGER.debug(() -> "Registering transaction synchronization for SqlSession [" + session + "]");
 
      holder = new SqlSessionHolder(session, executorType, exceptionTranslator);
      // 绑定SqlSession信息到当前线程中，key是SqlSessionFactory
      TransactionSynchronizationManager.bindResource(sessionFactory, holder);
      TransactionSynchronizationManager
          .registerSynchronization(new SqlSessionSynchronization(holder, sessionFactory));
      holder.setSynchronizedWithTransaction(true);
      holder.requested();
    } else {
      if (TransactionSynchronizationManager.getResource(environment.getDataSource()) == null) {
        LOGGER.debug(() -> "SqlSession [" + session
            + "] was not registered for synchronization because DataSource is not transactional");
      } else {
        throw new TransientDataAccessResourceException(
            "SqlSessionFactory must be using a SpringManagedTransactionFactory in order to use Spring transaction synchronization");
      }
    }
  } else {
    LOGGER.debug(() -> "SqlSession [" + session
        + "] was not registered for synchronization because synchronization is not active");
  }
 
}
```

到这里可以看到，通过对接TransactionSynchronizationManager，实现了与Spring事务的对接。

通过ThreadLocal存储了一个HashMap，key是SqlSessionFactory，value是SqlSession，代表一个线程在一个事务中一个数据源只能有一个Connection，事务中不能切换Connection，所以SqlSession将会被复用。

mybatis-spring事务总结一下：

对接事务就是通过对接Spring事务来管理SqlSession的创建/获取/销毁，

事务开启时获取SqlSession实例时使用TransactionSynchronizationManager获取，一个线程一个map，key是SqlSessionFactory，value是SqlSession。

事务关闭、提交时，也是先删除资源，再调用sqlSession的close方法，完成资源关闭。

