#  BeanPostProcessor 接口解读

###  一：什么时候执行

BeanPostProcessor是Spring IOC容器给我们提供的一个扩展接口。

```java
public interface BeanPostProcessor {
    //bean初始化方法调用前被调用
    Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;
    //bean初始化方法调用后被调用
    Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;
}
```

在bean实例化的时候，也就是Spring激活bean 的init-method 的前后，会调用BeanPostProcessor的 postProcessBeforeInitialization方法和postProcessAfterInitialization方法。

![](https://img-blog.csdnimg.cn/20210510001551674.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Zsdzc3NTg=,size_16,color_FFFFFF,t_70)

实现这个接口的类很多，有的类也有其他方法在其他地方调用。

###  二：Spring中这个接口的作用

BeanPostProcessor 接口主要是在创建bean的前后可以拓展一些对于bean的一些工作。他和主流程的IOC脱离，对外也实现了拓展。很好的实现了架构设计中的开闭原则。

spring内部也有很对对这个接口的实现

![](https://img-blog.csdnimg.cn/20210510235952704.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Zsdzc3NTg=,size_16,color_FFFFFF,t_70)

我们以ApplicationContextAwareProcessor为例，看看ApplicationContextAwareProcessor在bean的创建前后都做了什么，通过这个类的名称可以大概知道，这个类是处理Aware的感知接口相关的工作。

我们可以看`postProcessBeforeInitialization`这个方法。

![](https://img-blog.csdnimg.cn/20210511000525739.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Zsdzc3NTg=,size_16,color_FFFFFF,t_70)

我们可以看到这个逻辑挤半就是如果是这几个接口的实例，然后就回去执行invokeAwareInterfaces这个方法。

继续跟进这个方法。

![](https://img-blog.csdnimg.cn/20210511000733126.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Zsdzc3NTg=,size_16,color_FFFFFF,t_70)

这个意图就是为这些Aware设置设置一些相关的值，也就是为这些感知接口准备"原料" 。

我们继续看后置接口

![](https://img-blog.csdnimg.cn/20210511000957819.png)

这个后置处理接口，没有做任务事情。

总结起来，这个`ApplicationContextAwareProcessor`这个类是为那些感知接口准备“原料”。

那这些感知接口做什么用，在哪里执行，后续我会写博客跟进。



###  三：拓展实践

在项目中需要对某个bean进行代理的时候就可以通过BeanPostProcessor进行代理。



```java
public class MyBeanPostProcesser implements BeanPostProcessor {  
    private Map map = new ConcurrentHashMap(100);  
    private static final Logger log = LoggerFactory.getLogger("myBeanPostProcesser");  
  
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {  
        MyProxy proxy = new MyProxy();  
  
        if (beanName.contains("DB")) {  
            return bean;  
        }  
  
        if (bean.toString().contains("Proxy")) {  
            log.info(beanName + "为代理类,不进行再次代理!");  
            return bean;  
        }  
        if (beanName.contains("TransactionTemplate")) {  
            log.info(beanName + "为TransactionTemplate类,不进行再次代理!该类为:" + bean);  
            return bean;  
        }  
  
        if (map.get(beanName) != null) {  
            log.info(beanName + "已经代理过,不进行再次代理!");  
            return map.get(beanName);  
        }  
        proxy.setObj(bean);  
        proxy.setName(beanName);  
        Class[] iterClass = bean.getClass().getInterfaces();  
        if (iterClass.length > 0) {  
            Object proxyO = Proxy.newProxyInstance(bean.getClass().getClassLoader(), iterClass, proxy);  
            map.put(beanName, proxyO);  
            return proxyO;  
        } else {  
            log.info(beanName + "没有接口不进行代理!");  
            return bean;  
        }  
    }  
  
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {  
        return bean;  
    }  
  
```

代理类：

```java
 private static final Logger log = LoggerFactory.getLogger("myself");  
  
    private Object obj;  
  
    private String name;  
  
    public String getName() {  
        return name;  
    }  
  
    public void setName(String name) {  
        this.name = name;  
    }  
  
    public Object getObj() {  
        return obj;  
    }  
  
    public void setObj(Object obj) {  
        this.obj = obj;  
    }  
  
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {  
        return method.invoke(obj, args);  
    }  
  
    public void printDetail(String detail) {  
        log.error(detail);  
    }  
  
```



