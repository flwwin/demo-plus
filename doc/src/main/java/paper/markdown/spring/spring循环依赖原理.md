#  Spring循环依赖原理

#  图解Spring的循环依赖

###  前言

Spring的循环依赖是spring架构设计中比较精彩的一环，学习Spring的循环依赖设计，对我们进行业务架构设计还是很有裨益的。

这里通过debug介绍一下，spring如何通过三级缓存来实现循环依赖的。



###  spring的三级缓存

spring通过三级缓存来实现循环依赖就需要了解spring的三级缓存机制。

spring缓存在`BefaultSingletonBeanRegistry`中。

![](https://img2020.cnblogs.com/blog/1376820/202105/1376820-20210509165846614-1098277308.png)

- 一级缓存`singletonObjects` 是最终的Bean对象的缓存。也就是常说的IOC容器。
- 二级缓存`earlySingletonObjects`   也是缓存的bean对象，但是bean对象创建好了，但是并没有走完全部的bean的生命周期，也就是属性没有完成填充的bean对象。
- 三级缓存`singletonFactories`  缓存的bean对象的创建工厂。可以通过工厂的`getObject` 方法创建我们的bean对象。

处理循环依赖的过程中并不是每个对象都要经历三个缓存。

###  创建Demo

我们创建两个对象A，B，同时A依赖B，B依赖A。通过debug来看spring是怎么处理循环依赖的。

```java
public class B {

	protected final Log logger = LogFactory.getLog(getClass());

	private A a;

	public A getA() {
		return a;
	}

	public void setA(A a) {
		logger.info("B createing ......");
		this.a = a;
	}
}
```

```java
public class A {
	protected final Log logger = LogFactory.getLog(getClass());
	private B b;

	public B getB() {
		return b;
	}

	public void setB(B b) {
		logger.info("a creating....");
		this.b = b;
	}
}

```

同时配置xml配置文件。

```xml
	<bean id="a" class="com.leven.spring.reader.cycle.A">
		<property name="b" ref="b"/>
	</bean>

	<bean id="b" class="com.leven.spring.reader.cycle.B">
		<property name="a" ref="a"/>
	</bean>
```



###  Debug演示

这里我画了个流程图，通过流程图比文字更加容易理解。欢迎大家点赞。

https://www.processon.com/view/60770cf05653bb2e1c68f830

文字后续再补充。。。