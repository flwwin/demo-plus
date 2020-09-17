

## springboot的高级整合

#### @cacheable的初体验

​	1：准备工作，构建一个springboot的项目（添加cache，mybatis，web模块）

​	2：配置好数据库，做一个CRUD并配置好

#### 配置缓存

```java
    // 在主配置类上标注@EanbleCaching注解，表明项目启动缓存
       @MapperScan("com.atguigu.springbootcache.mapper")
       @SpringBootApplication
       @EnableCaching // 提供一个Eablecache的注解，表明项目启动缓存
       public class SpringBootCacheApplication {
       
       	public static void main(String[] args) {
       		SpringApplication.run(SpringBootCacheApplication.class, args);
       	}
       }
       //这是都是缓存中可以配置的属性
       //在Service层的方法上标注@Cacheable注解
       	//Cacheable 中有很多属性可以配置
        //String[] cacheNames() default {};

        //String key() default ""; 缓存的key值

        // String keyGenerator() default "";key的生成器可以指定key的值为id  		   key/keyGenerator二选一

        // String cacheManager() default "";缓存管理器

        // String cacheResolver() default "";和cacheManager二选一

        // String condition() default "";缓存判断条件

        // String unless() default "";缓存判断条件可以使用SPEL表达式

        // boolean sync() default false;配置是否异步的
           @Cacheable(cacheNames = "dept")
           public Department getDepartById(Integer id){
               Department dept = deptMapper.getDepartById(id);
               return dept;
           }
    
```

#####  @CacheEvict注解

```java
 //删
    @CacheEvict(value = "dept")//在删除数据的同时删除缓存，也是同样具有和@Cacheable相同的属性配置
    @GetMapping("/dept/{id}")
    public void deleteDept(@PathVariable("id") Integer id){
        deptMapper.deleteDept(id);
    }
```

##### @CachePut注解

```java
//改
@CachePut(value = "dept") //CachePut同时更新缓存，也是同样具有和@Cacheable相同的属性配
@GetMapping("/dept")
public void updateDepartment(Department dept){
    deptMapper.updateDepartment(dept);
}
```



##### @Caching注解

``` java
public @interface Caching {
    Cacheable[] cacheable() default {};

    CachePut[] put() default {};

    CacheEvict[] evict() default {};
}
//Caching 注解是 Cahceable CahchePut CacheEvict三个注解的组合
```

``` java
  //删
    @Caching(//组合注解，可以定义三种缓存状态 cacheable put  evict三种
            cacheable = {
                    @Cacheable(value = "dept")
            },
            put={
                    @CachePut(value = "dept" ,key = "#result.lastName"),//可以根据不同的key的缓存规则缓存数据
                    @CachePut(value = "dept" , key = "#reslut.id")
            }
    )
    
    public void deleteDept(@PathVariable("id") Integer id){
        deptMapper.deleteDept(id);
    }
}
```



### 缓存的工作原理：





###  和Redis缓存整合

1）在docker中安装redis,

2）在 pom.xml 文件中引入redis的配置文件，在配置文件中配置好redis的ip地址

``` properties
#配置redis
spring.redis.host=192.168.1.102
```

3) 然后再项目中注入RedisTemplate和StringRedisTemplate就可以操作redis了

``` java
	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Autowired
	RedisTemplate redisTemplate;

//测试StringRedisTemplate的操作
	@Test
	public void test01(){
		Long msg = stringRedisTemplate.opsForList().leftPush("msg1", "hello");
		stringRedisTemplate.opsForList().leftPush("msg1", "word");
		System.out.println(msg);
	}
```



我们在Redis中存储对象的时候，会出现报错，因为我们没有序列化对象。

``` java

public class Department implements Serializable {
```



#### 自定义序列化器

我们在保存对象的时候会出现乱码的情况,因为默认的序列化器是JDKSerializer序列化器

```java
@Configuration
public class MyRedisconfig {

    @Bean
    public RedisTemplate<Object, Department> deptRedisTemplate(RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
        RedisTemplate<Object, Department> RedisTemplate = new RedisTemplate();
        RedisTemplate.setConnectionFactory(redisConnectionFactory);
        RedisTemplate.setDefaultSerializer(new Jackson2JsonRedisSerializer(Department.class));//设置我们需要的序列化器
        return RedisTemplate;
    }}
```

对于不同的对象配置不同的缓存管理器

​      1:写相应的缓存管理器加入到容器中

​      2:@Paimary配置默认的缓存管理器

​      3:用@Cacheable注解规定定义缓存管理器



```java
 //自己配置缓存管理器
    @Primary //需要配置默认的缓存管理器
    @Bean
    public RedisCacheManager EmpCacheManager(RedisTemplate<Object, Employee> empRedisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(empRedisTemplate);
        cacheManager.setUsePrefix(true);
        return cacheManager;
    }
    //在多个对象进行序列化缓存的时候我们要配置不同的缓存管理器
    @Bean
    public RedisCacheManager DeptCacheManager(RedisTemplate<Object, Department> deptRedisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(deptRedisTemplate);
        cacheManager.setUsePrefix(true);
        return cacheManager;
    }


    //查
    @Cacheable(cacheNames = "emp",cacheManager = "EmpCacheManager")//配置缓存管理器
    public Employee getEmpById(Integer id){
        Employee emp = empMapper.getEmpById(id);
        System.out.println("查询的员工为"+ emp);
        return emp;
    }
```



### springboot和消息队列

	####        消息的概述

1.大多应用中，可通过消息服务中间件来提升系统异步通信、扩展解耦能力

2.消息服务中两个重要概念：

​       消息代理（message broker）和目的地（destination）


当消息发送者发送消息以后，将由消息代理接管，消息代理保证消息传递到指定目的地。


3.消息队列主要有两种形式的目的地

​       1.队列（queue）：点对点消息通信（point-to-point）

​       2.主题（topic）：发布（publish）/订阅（subscribe）消息通信

​       3.点对点式：


​              –消息发送者发送消息，消息代理将其放入一个队列中，消息接收者从队列中获取消息内容，消息读取后            被移出 队列

​              –消息只有唯一的发送者和接受者，但并不是说只能有一个接收者

4.发布订阅式：

​             –发送者（发布者）发送消息到主题，多个接收者（订阅者）监听（订阅）这个主题，那么就会在消息到达时同时收到消息


5.JMS（Java Message Service）JAVA消息服务：

​      –基于JVM消息代理的规范。ActiveMQ、HornetMQ是JMS实现


6.AMQP（Advanced Message Queuing Protocol）

​      –高级消息队列协议，也是一个消息代理的规范，兼容JMS

​      –RabbitMQ是AMQP的实现



|              | JMS                                                          | AMQP                                                         |
| ------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 定义         | Java api                                                     | 网络线级协议                                                 |
| 跨语言       | 否                                                           | 是                                                           |
| 跨平台       | 否                                                           | 是                                                           |
| Model        | 提供两种消息模型：   （1）、Peer-2-Peer   （2）、Pub/sub     | 提供了五种消息模型：   （1）、direct exchange   （2）、fanout exchange   （3）、topic change   （4）、headers exchange   （5）、system exchange   本质来讲，后四种和JMS的pub/sub模型没有太大差别，仅是在路由机制上做了更详细的划分； |
| 支持消息类型 | 多种消息类型：   TextMessage   MapMessage   BytesMessage   StreamMessage   ObjectMessage   Message （只有消息头和属性） | byte[]   当实际应用时，有复杂的消息，可以将消息序列化后发送。 |
| 综合评价     | JMS 定义了JAVA API层面的标准；在java体系中，多个client均可以通过JMS进行交互，不需要应用修改代码，但是其对跨平台的支持较差； | AMQP定义了wire-level层的协议标准；天然具有跨平台、跨语言特性。 |

8.Spring支持

–spring-jms提供了对JMS的支持

–spring-rabbit提供了对AMQP的支持

–需要ConnectionFactory的实现来连接消息代理

–提供JmsTemplate、RabbitTemplate来发送消息

–@JmsListener（JMS）、@RabbitListener（AMQP）注解在方法上监听消息代理发布的消息

–@EnableJms、@EnableRabbit开启支持

–

9.Spring Boot自动配置

–JmsAutoConfiguration

–RabbitAutoConfiguration



####  引用

1：应用解耦

​	![应用解耦](/images/QQ截图20181207111704.png))

2：流量削峰

​	![流浪削峰](/images/QQ截图20181207114022.png)

### Rabitmq的简介

RabbitMQ是一个由erlang开发的AMQP(Advanved Message Queue Protocol)的开源实现。

#### 核心概念

Message
消息，消息是不具名的，它由消息头和消息体组成。消息体是不透明的，而消息头则由一系列的可选属性组成，这些属性包括routing-key（路由键）、priority（相对于其他消息的优先权）、delivery-mode（指出该消息可能需要持久性存储）等。

Publisher
消息的生产者，也是一个向交换器发布消息的客户端应用程序。

Exchange
交换器，用来接收生产者发送的消息并将这些消息路由给服务器中的队列。
Exchange有4种类型：direct(默认)，fanout, topic, 和headers，不同类型的Exchange转发消息的策略有所区别	

Queue

消息队列，用来保存消息直到发送给消费者。它是消息的容器，也是消息的终点。一个消息可投入一个或多个队列。消息一直在队列里面，等待消费者连接到这个队列将其取走。

Binding


绑定，用于消息队列和交换器之间的关联。一个绑定就是基于路由键将交换器和消息队列连接起来的路由规则，所以可以将交换器理解成一个由绑定构成的路由表。

Exchange 和Queue的绑定可以是多对多的关系。

Connection

网络连接，比如一个TCP连接。

Channel

信道，多路复用连接中的一条独立的双向数据流通道。信道是建立在真实的TCP连接内的虚拟连接，AMQP 命令都是通过信道发出去的，不管是发布消息、订阅队列还是接收消息，这些动作都是通过信道完成。因为对于操作系统来说建立和销毁 TCP 都是非常昂贵的开销，所以引入了信道的概念，以复用一条 TCP 连接。

 Consumer

消息的消费者，表示一个从消息队列中取得消息的客户端应用程序。

Virtual Host

虚拟主机，表示一批交换器、消息队列和相关对象。虚拟主机是共享相同的身份认证和加密环境的独立服务器域。每个 vhost 本质上就是一个 mini 版的 RabbitMQ 服务器，拥有自己的队列、交换器、绑定和权限机制。vhost 是 AMQP 概念的基础，必须在连接时指定，RabbitMQ 默认的 vhost 是 / 。

Broker

表示消息队列服务器实体



​    ![](/images/图片1.png)



#### RabbitMQ运行机制

​	AMQP 中的消息路由


•AMQP 中消息的路由过程和 Java 开发者熟悉的 JMS 存在一些差别，AMQP 中增加了 Exchange 和 Binding 的角色。生产者把消息发布到 Exchange 上，消息最终到达队列并被消费者接收，而 Binding 决定交换器的消息应该发送到那个队列。

  

![](/images/图片2.png)

####  Exchange类型

•Exchange分发消息时根据类型的不同分发策略有区别，目前共四种类型：direct、fanout、topic、headers 。headers 匹配 AMQP 消息的 header 而不是路由键， headers 交换器和 direct 交换器完全一致，但性能差很多，目前几乎用不到了，所以直接看另外三种类型：

##### Direct:

![](/images/图片3.png)

消息中的路由键（routing key）如果和 Binding 中的 binding key 一致， 交换器就将消息发到对应的队列中。路由键与队列名完全匹配，如果一个队列绑定到交换机要求路由键为“dog”，则只转发 routing key 标记为“dog”的消息，不会转发“dog.puppy”，也不会转发“dog.guard”等等。它是完全匹配、单播的模式。

#####  Fanout:

![](/images/图片4.png)

每个发到 fanout 类型交换器的消息都会分到所有绑定的队列上去。fanout 交换器不处理路由键，只是简单的将队列绑定到交换器上，每个发送到交换器的消息都会被转发到与该交换器绑定的所有队列上。很像子网广播，每台子网内的主机都获得了一份复制的消息。fanout 类型转发消息是最快的。

##### Topic:

![](/images/图片5.png)


交换器通过模式匹配分配消息的路由键属性，将路由键和某个模式进行匹配，此时队列需要绑定到一个模式上。它将路由键和绑定键的字符串切分成单词，这些单词之间用点隔开。它同样也会识别两个通配符：符号“#”和符号“*”。#匹配0个或多个单词，* * 匹配一个单词

### springBoot 和rabbitmq的整合

​	1: 在pom.xml文件中配置rabbitmq的的starter

​	2: 注入RabbitmqTemplate操作rabbitmq

``` 
//发送消息给消息队列
	@Test
	public void  test03() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("msg","hello");
		map.put("msg1","word");
		rabbitTemplate.convertAndSend("exchange.direct","atguigu.news",map);
	}
	//从队列中接受消息
	@Test
	public void  test04() {
		Message receive = rabbitTemplate.receive("atguigu.news");
		System.out.println(receive);
	}

	//广播
	@Test
	public void test05(){
		HashMap<String, Object> map = new HashMap<>();
		map.put("msg","hello");
		map.put("msg1","word");
		rabbitTemplate.convertAndSend("exchange.fanout","atguigu.news",map);
	}
```



#### 监听消息队列

1:在方法上添加@RabbitListener监听器

```java
@Service
public class RbbitmqService {

    @RabbitListener(queues = "atguigu")
    public void recive(HashMap map){//也可以用message参数，接受消息，可以获取头等其他信息
        System.out.println("消息队列收到消息"+map);
    }
}
```



2：在配置类上开启rabbitmq注解@EnableRabbit

```java
@MapperScan("com.atguigu.springbootcache.mapper")
@SpringBootApplication
@EnableCaching
@EnableRabbit//开启rabbitmq的监听
public class SpringBootCacheApplication {

   public static void main(String[] args) {
      SpringApplication.run(SpringBootCacheApplication.class, args);
   }
}
```



#####  AmqpAdmin来创建交换器，队列，绑定关系

如何创建exchange

``` java
@Autowired
	RabbitAdmin rabbitAdmin;//注入RabbitmqAmdin

	@Test
	public void test06(){
//		rabbitAdmin.declareExchange(new DirectExchange("dept:dircet"));//声明交换器
		rabbitAdmin.declareQueue(new Queue("dept.news",true));//声明队列
		rabbitAdmin.declareBinding(new Binding("dept.news", Binding.DestinationType.QUEUE,"dept:dircet","atguigu.hah",null));//声明绑定关系
	}


```



### srpingBoot和检索

##### 安装ElacsticSerch

``` xml
docker run -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -d -p 9200:9200 -p 9300:9300 --name ES01 5acf0e8da90b
#在docker中安装ElacsticSearch，然后再容器中启动，默认会占用2G的内存，可以用参数对内存限制。
访问9200端口出现json数据就是启动成功了http://192.168.1.102:9200/
```

![ES结构图](/images/QQ截图20181208114928.png)

安装好ElacsticSerch以后，我们可以参考文档，操作索引：

在postman中发送put请求，把JSON数据放到方法体中发送局可以把数据存到ES中

``` java
对于雇员目录，我们将做如下操作：

每个雇员索引一个文档，包含该雇员的所有信息。
每个文档都将是 employee 类型 。
该类型位于 索引 megacorp 内。
该索引保存在我们的 Elasticsearch 集群中。
实践中这非常简单（尽管看起来有很多步骤），我们可以通过一条命令完成所有这些动作：
PUT /megacorp/employee/1
{
    "first_name" : "John",
    "last_name" :  "Smith",
    "age" :        25,
    "about" :      "I love to go rock climbing",
    "interests": [ "sports", "music" ]
}
```

![postman发送请求](/images/QQ截图20181208121711.png)



存储后的结果：

   	![](/images/QQ截图20181208123429.png)

##### 检索文档：

``` 
GET "localhost:9200/megacorp/employee/1"
GET "localhost:9200/megacorp/employee/_search?q=last_name:Smith"//搜索姓式为Smith的人


```

##### 使用检索表达式

``` 
GET "localhost:9200/megacorp/employee/_search" 
{
    "query" : {
        "match" : {
            "last_name" : "Smith"
        }
    }
}
更多参考官方文档

```

##### springBoot和EalsacticSearch的整合

springBoot对EasacticSearch有两种整合方式

一种是springdata中的方式，一直是jest方式

##### jest操作ealsacticSearch

1:在pom.xml文件中排除，然后加入jest的依赖

``` xml
<dependency>
			<groupId>io.searchbox</groupId>
			<artifactId>jest</artifactId>
			<version>5.3.3</version>
		</dependency>
```



2:在对象上的主键上加上注解@JestId，用做es的id

``` java
public class Article {
    @JestId
    private Integer id;
```

3：在配置文件中配置ES的地址

``` java
spring.elasticsearch.jest.uris=http://192.168.1.102:9200
```



4:注入JestClient,用Jestclient操作数据库。

``` java
	public class SpirngBootElasticsearchApplicationTests {

	@Autowired
	JestClient jestClient;
	@Test
	public void contextLoads() {
		Article article=new Article();
		article.setId(1);
		article.setAuthor("吴承恩");
		article.setName("西游记");

		Index index=new Index.Builder(article).index("atguigu").type("news").build();
		try {
			jestClient.execute(index);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//检索
	@Test
	public  void test01(){
		String json ="\"query\" : {\n" +
				"        \"match\" : {\n" +
				"            \"author\" : \"吴承恩\"\n" +
				"        }";

		Search.Builder builder = new                             Search.Builder(json).addIndex("atguigu").addType("news");
	}
}

```



#####  springData ElasticSearch

1:在pom.xml文件中配置好springData ElasticSearch的依赖

2:配置文件中配置好节点信息和端口（9300）

``` xml
spring.data.elasticsearch.cluster-name=elasticsearch//访问9200端口会有节点名称
spring.data.elasticsearch.cluster-nodes=http://192.168.1.102:9300
```

3:启动项目，如果报错的话，可能是我们springboot -data-lasticSeearch的版本和我们安装的版本不适配。

![](/images/QQ截图20181208224158.png)

4：操作es

1：创建一个接口myRepository继承ElasticSearchRepository

``` 
public interface ElasticSearchBookRepository extends ElasticsearchRepository<Book,Integer> {

}
```

  在实体类上标注@Document注解，配置好index 和type

``` 
@Document(indexName = "atguigu",type = "book")
```



2：注入创建的myRepository，用该对象中方法操作es





###  springboot与任务

 ####  	开启异步

​		1：在主配置类上开启异步注解

``` 
@SpringBootApplication
@EnableAsync//开启异步注解
public class SpirngBootElasticsearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpirngBootElasticsearchApplication.class, args);
	}
}

```

​		2:在方法上标注开启注解

``` 
@SpringBootApplication
@EnableAsync//开启异步注解
public class SpirngBootElasticsearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpirngBootElasticsearchApplication.class, args);
	}
}

```



####  开启定时任务

​	开启定时任务就是两个注解@EableSchedule 和 @ Schedule

​	1：在主配置类上开启定时任务注解

​	2：在方法上标注@Schedule，配置属性 corn 

```  

@Service
public class ScheduleService {

   // @Scheduled(cron = "0 * * * * ?")//每一分钟启动一次
   // @Scheduled(cron = "0,1,2,3,4 * * * * MON-SAT")//周一到周五每0 1 2 3 4秒都会启动一次
    //@Scheduled(cron = "0-4 * * * * MON-SAT")//周一到周五0 -4秒每秒启动一次
    @Scheduled(cron = "0/4 * * * * MON-SAT")//周一到周五0秒启动每四秒启动一次
    public void scheduleTask(){
        System.out.println("hello");
    }
}


```

​	关于cron的配置

![](/images/QQ截图20181209130219.png)

####   邮箱任务

​	1：引入邮箱的starter

``` 
	<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-mail</artifactId>
		</dependency>
```



​	2：在配置文件中配置好发件人，密码，等

``` xml
#配置springBoot mail
spring.mail.host=smtp.163.com
spring.mail.username=18558702594@163.com
spring.mail.password=flwwin19939567 //qq邮箱需要设置授权密码
spring.mail.properties.mail.smtp.ssl.enable=true// 开启ssl
```

​	3：注入JavaMailSenderImpl 

``` java
  @Test
    public void test03() throws MessagingException {
        //创建一个复杂邮件
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);

        //设置发件人和收件人
        helper.setTo("879465780@qq.com");
        helper.setFrom("18558702594@163.com");

        //设置邮件内容
        helper.setSubject("通知");
        helper.setText("<h3>helloword</h3>",true);

        //文件上传
        helper.addAttachment("1.gif",new File("C:\\Users\\Administrator\\Desktop\\1.gif"));
        helper.addAttachment("2.gif",new File("C:\\Users\\Administrator\\Desktop\\2.gif"));
        javaMailSender.send(mimeMessage);

    }
```



### springboot和安全 shiro， spring security

1:在pom.xml文件中配置security

2:写一个配置类

 ``` java
@EnableWebSecurity
public class mySecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/*").permitAll();
        http.authorizeRequests().antMatchers("/level1/**").hasRole("vip1");
        http.authorizeRequests().antMatchers("/level2/**").hasRole("vip2");
        http.authorizeRequests().antMatchers("/level3/**").hasRole("vip3");

        /**
         * 1：开启登录也功能，默认是/login并生产login.html页面
         * 2：也可以自定义登录页面 http.formLogin().loginPage("/userlogin");
         * 3：默认登录页面是默认定义username 和password作为登录页面的账号和密码的属性名称的
         * 4：可以修改登录页面的属性名称和密码名称用方法 passwordParameter 和usernameParameter
         * 5: 默认发送post的/login请求的时候是发送登录请求的
         * 6：在自定义了loginpage页面的时候我们请求登录的请求地址也是loginpage一致的，所以用loginProcessingUrl("/userlogin")方法定义登录请求
         */
        http.formLogin().passwordParameter("use").usernameParameter("pwd").loginPage("/userlogin");

        /**
         * 1：配置注销功能，发送/lagout请求就会帮我们清空session
         * 2:退出完成后返回/login?logout页面
         */
        http.logout().logoutSuccessUrl("/");

        /**
         * 1:开启记住我的功能，在下次登录的时候就不需要进行登录操作了
         * 2：选中remeber me后火灾浏览器中加入cookie
         * 3: 定义remeberme的属性名称
         */
        http.rememberMe().rememberMeParameter("remeberme");

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("zhangsan").password("123456").roles("vip1","vip2").and()
                .withUser("lisi").password("123456").roles("vip2","vip3").and()
                .withUser("wangwu").password("123456").roles("vip1","vip3");
    }
}

 ```

ps:

Exception parsing document: template="welcome", line 6 - column 3  是thymeleaf的版本不兼容导致的

``` xml
	<thymeleaf.version>2.1.6.RELEASE</thymeleaf.version>
		<thymeleaf-extras-springsecurity4.version>2.1.3.RELEASE</thymeleaf-extras-springsecurity4.version>
		<thymeleaf-extras-conditionalcomments.version>2.1.2.RELEASE</thymeleaf-extras-conditionalcomments.version>
		<thymeleaf-layout-dialect.version>1.4.0</thymeleaf-layout-dialect.version>
		<thymeleaf-extras-data-attribute.version>1.3</thymeleaf-extras-data-attribute.version>
		<thymeleaf-extras-java8time.version>2.1.0.RELEASE</thymeleaf-extras-java8time.version>
```

 3:引入security的thymeleaf的语法依赖，然后再页面引入命名空间

``` 
         <dependency>
			<groupId>org.thymeleaf.extras</groupId>
			<artifactId>thymeleaf-extras-springsecurity4</artifactId>
		</dependency>
```

在页面引入命名空间：

``` 
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity4">
```

页面可以根据不同的语法显示不同的内容：

``` html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity4">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Insert title here</title>
</head>
<body>
<h1 align="center">欢迎光临武林秘籍管理系统</h1>
<div sec:authorize="!isAuthenticated()">
    <h2 align="center">游客您好，如果想查看武林秘籍 <a th:href="@{/login}">请登录</a></h2>
</div>
<div sec:authorize="isAuthenticated()">
    <form th:action="@{/logout}" th:method="post">
        <h5>您登录的用户<span sec:authentication="name"></span>，拥有角色<span sec:authentication="principal.authorities">
    </span></h5>
        <input type="submit" value="注销">
    </form>
</div>
<hr>

<h3>普通武功秘籍</h3>

<div sec:authorize="hasRole('vip1')">
    <ul>
        <li><a th:href="@{/level1/1}">罗汉拳</a></li>
        <li><a th:href="@{/level1/2}">武当长拳</a></li>
        <li><a th:href="@{/level1/3}">全真剑法</a></li>
    </ul>
</div>

<h3>高级武功秘籍</h3>
<div sec:authorize="hasRole('vip2')">
    <ul>
        <li><a th:href="@{/level2/1}">太极拳</a></li>
        <li><a th:href="@{/level2/2}">七伤拳</a></li>
        <li><a th:href="@{/level2/3}">梯云纵</a></li>
    </ul>
</div>


<h3>绝世武功秘籍</h3>
<div sec:authorize="hasRole('vip3')">
    <ul>
        <li><a th:href="@{/level3/1}">葵花宝典</a></li>
        <li><a th:href="@{/level3/2}">龟派气功</a></li>
        <li><a th:href="@{/level3/3}">独孤九剑</a></li>
    </ul>
</div>

</body>
</html>
```

[详情请参照官方文档]: https://docs.spring.io/spring-security/site/docs/4.2.10.RELEASE/guides/html5/helloworld-boot.html



###   spingboot和分布式

####  分布式应用简述

​	在分布式系统中，国内常用zookeeper+dubbo组合，而Spring Boot推荐使用全栈的Spring，Spring Boot+Spring Cloud。


  ####  构建一个简单的分布式应用系统：

1：docker中安装zookeeper

2：构建项目提供者和消费者

3：引入zookeeper的客户端zkclient

4：引入dubbo的starter

5：写提供者接口方法，写好配置类

``` xml
dubbo.application.name=provider-ticket
dubbo.registry.address=zookeeper://192.168.1.102:2181
dubbo.scan.base-packages=com.atguigu.providerticket.service
```

service

```java
import com.alibaba.dubbo.config.annotation.Service;

@Service//将服务发布到注册中心
public interface TickerService {

    public void getTicker();
}
//实现类
public class TickerServiceImpl implements TickerService {

    @Override
    public void getTicker() {
        System.out.println("《厉害的，我的国》");
    }
}

```



6：创建一个消费者模块comsumer-user，做好配置，把provider-ticker的服务拷贝到consumer-user模块中

配置好。

```xml
dubbo.application.name=consumer-user
dubbo.registry.address=zookeeper://192.168.1.102:2181
```

```java
@Service  //容器中的一个组件
public class UserService {

    @Reference //标注为远程调用
    TickerService tickerService;

    public void getTicket(){
        tickerService.getTicker();
    }
}
```

7:然后再调用userserivce



####   springboot和springcloud

Spring Cloud
Spring Cloud是一个分布式的整体解决方案。Spring Cloud 为开发者提供了在分布式系统（配置管理，服务发现，熔断，路由，微代理，控制总线，一次性token，全局琐，leader选举，分布式session，集群状态）中快速构建的工具，使用Spring Cloud的开发者可以快速的启动服务或构建应用、同时能够快速和云平台资源进行对接

SpringCloud分布式开发五大常用组件
​       服务发现——Netflix Eureka
​       客服端负载均衡——Netflix Ribbon
​       断路器——Netflix Hystrix
​       服务网关——Netflix Zuul
​       分布式配置——Spring Cloud Config



1：创建eureka server作为注册中心（选择eureka server）

2：创建provider 和 consumer （选择eureka discover）

3：在eureka server配置

``` yml
server:
  port: 8761
eureka:
  instance:
    hostname: eureka-server #实例主机名
  client:
    register-with-eureka: false #不将自己注册到eureka上
    fetch-registry: false #不获取注册中心的信息
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

加上注解@EnableEurekaServer

``` java
@EnableEurekaServer //启动eureka注解
@SpringBootApplication
public class EurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}
}
```

启动服务器访问localhost:8761 可以看到eureka的注册中心

part2：

配置provider

1：写好测试的service 和controller

2：写好配置文件

``` yml
server:
  port: 8001
spring:
  application:
    name: provider

eureka:
  instance:
    prefer-ip-address: true #注册服务的时候用服务的ip注册
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

3 : 访问8001端口下的服务路径，可以在eureka界面看到provider注册的服务

part3：

consumer部分：

​	1：写好消费者配置

``` yml
server:
  port: 8200
spring:
  application:
    name: consumer

eureka:
  instance:
    prefer-ip-address: true #注册服务的时候用服务的ip注册
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

​	2:在主配置类中开启服务发现功能，在容器中注入RestTemplate

```` java
@EnableDiscoveryClient//开启发现服务
@SpringBootApplication
public class ConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}

	/**
	 * 在容器中加入RestTemplate
	 * @return
	 */
	@LoadBalanced //启用负载均衡
	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
}

````



​	3:写好controller中远程调用provider的服务

``` 
@Controller
public class TicketService {

    @Autowired
    RestTemplate restTemplate;

    @ResponseBody
    @GetMapping("/buy")
    public String buyTicket(){
        /**
         * 第一个参数是服务地址 第二个参数是返回数据类型
         */
        String s = restTemplate.getForObject("http://PROVIDER/hello", String.class);
        return "买到了"+s;
    }
}
```

​	4:测试8200下的/buy服务，可以调用到provider中的/hello服务，如果是在eureka中注册了两个provider的两个服务，会对访问进行负载均衡。



####  springboot和开发的热部署

​	在开发中我们修改一个Java文件后想看到效果不得不重启应用，这导致大量时间花费，我们希望不重启应用的情况下，程序可以自动部署（热部署）。有以下四种情况，如何能实现热部署。

​	Spring Boot Devtools（推荐）


–引入依赖


–IDEA使用ctrl+F9

–或做一些小调整

  Intellij IEDA和Eclipse不同，Eclipse设置了自动编译之后，修改类它会自动编译，而IDEA在非RUN或DEBUG情况下才会自动编译（前提是你已经设置了Auto-Compile）。

•设置自动编译（settings-compiler-make project automatically）

•ctrl+shift+alt+/（maintenance）

•勾选compiler.automake.allow.when.app.running


