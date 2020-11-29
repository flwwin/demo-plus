##  Spring Boot配置FastJsonHttpMessageConverter报错'Content-Type' cannot contain wildcard type '*'

####  背景：

​	业务需求中属性名称和接口的名称不匹配，项目中用fastjosn的@JSONFiled注解自定义返回json属性名称。

所有修改了SpringMVC的默认jackjson的httpMessage解析器。

​	导致原来业务采用content-type为`application/octet-stream`的业务报错。废话少说，看代码。



####  原因：

​	在`FastJsonHttpMessageConverter`的构造器中在不指定conent-type类型的情况下支持所有类型

![构造器](.\images\image-20201120194718372.png)

这个`MediaType ALL`  代表支持所有的content-type类型

```java
 public static final MediaType ALL = new MediaType("*", "*");
```



但Fastjson只是支持content-type只是支持`application/json`类型的解析。

在SpringMVC在解析请求头的时候会遍历所有的convert看那个支持就用哪个

![image-20201120200915992](.\images\image-20201120200915992.png)

`FastJsonHttpMessageConvert`在列表的前面，优先使用它来解析数据。

我们继续看怎么解析的。

![image-20201120201225759](.\images\image-20201120201225759.png)

我们可以看到`FastJsonHttpMessageConvert`的抽象类中，都会判断该convert是否支持解析，细节可以自己去看

引文前面默认添加所有导致这里返回是true的。

![image-20201120202028790](.\images\image-20201120202028790.png)

然后在后面改写请求头的content-type为`application/octet-stream` ，改成*，后续在框架中判断请求头不能为* `*`  

![image-20201120204226984](.\images\image-20201120204226984.png)

这里就会报错。

后续版本好像有改进，大家可以去探究一下。

改写springboot自定义框架，还是自己定义一下支持的类型，以免影响其他数据类型的解析。

```java
 @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        //自定义配置...
        FastJsonConfig config = new FastJsonConfig();
        converter.setFastJsonConfig(config);
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
        converters.add(0, converter);
    }
```




