#  MVCC原理分析

###  1：什么是MVCC

​	**MVCC**是英文名称*Multi Version Concurrency Control* 的简称，就是多版本并发控制。

MVCC可以说实现，读不加锁，读写不冲突。这个可以大大的提高Mysql的性能。



###  2：MVCC解决了什么问题

多事务的并发进行一般会造成以下几个问题:

**脏读**: A事务读取到了B事务未提交的内容,而B事务后面进行了回滚.

**不可重复读**: 当设置A事务只能读取B事务已经提交的部分,会造成在A事务内的两次查询,结果竟然不一样,因为在此期间B事务进行了提交操作.

**幻读**: A事务读取了一个范围的内容,而同时B事务在此期间插入了一条数据.造成"幻觉".

MVCC在MySQL InnoDB中的实现主要是为了提高数据库并发性能，用更好的方式去处理读-写冲突，做到即使有读写冲突时，也能做到不加锁，非阻塞并发读

在并发读写数据库时，可以做到在读操作时不用阻塞写操作，写操作也不用阻塞读操作，提高了数据库并发读写的性能

同时还可以解决脏读，幻读，不可重复读等事务隔离问题，但不能解决更新丢失问题



###  3: Mysql的事务隔离级别

我们先了解一下数据的隔离级别。

1：RU：*读未提交*，就是可以读取到其他未提交事务中的数据，存在脏读。

2：RC：*读已提交*，只可以读取到已提交的数据，存在幻读。

3：RR：*不可重复读*，不可以重复读。

4：SERIALIZABLE：*串行读取*，没有脏读，幻读，不可重复读。每次操作都是加锁。性能较低

<img src="https://img-blog.csdnimg.cn/20210501135535190.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Zsdzc3NTg=,size_16,color_FFFFFF,t_70" style="zoom:50%;" />



MVCC只在REPEATABLE READ和READ COMMITTED两个隔离级别下工作。

**REPEATABLE READ**读取之前系统版本号的记录，**保证同一个事务中多次读取结果一致。**

**REPEATABLE READ**隔离级别下，MVCC具体操作：

SELECT操作，InnoDB会根据以下两个条件检查每行记录：

a. InnoDB只查找创建版本号早于或等于当前系统版本号的数据行，这样可以确保事务读取的行，要么是在事务开始前已经存在的，要么是事务自身插入或者修改过的。

b. 行的删除版本号要么未定义，要么大于当前的系统版本号(在当前事务开始之后删除的)。这可以确保事务读取到的行，在事务开始之前未被删除。

**READ COMMITTED读取最新的版本号记录，就是所有事务最新提交的结果。**

其他两个隔离级别和MVCC不兼容。READ UNCOMMITTED总是读取最新的数据行，而不是符合当前事务版本的数据行。SERIALIZABLE会对所有读取的行都加锁。



###  3：MVCC的实现原理

InnoDB的存储引擎，的每个记录都会存在三个隐藏的键，分别是：DATA_TRX_ID、DATA_ROLL_PTR、DB_ROW_ID。

- **DATA_TRX_ID**：记录当前记录的事务ID，大小为6个字节。
- **DATA_ROLL_PTR**：表示指向该行回滚段（rollback segment）的指针，大小为 7 个字节，InnoDB 便是通过这个指针找到之前版本的数据。该行记录上所有旧版本，在 undo 中都通过链表的形式组织。
- **DB_ROW_ID**：行标识（隐藏单调自增 ID），大小为 6 字节，如果表没有主键，InnoDB 会自动生成一个隐藏主键（`row_id`并不是必要的，我们创建的表中有主键或者非NULL唯一键时都不会包含`row_id`列）



![](https://img-blog.csdnimg.cn/20210501140955858.png)

再undo.log中形成一个这样的版本链。

####  理论解释：

这里开创了三个事务，

假设我们最初的数据的**事务Id是80**,那他的数据结构如图所示：

![](https://img-blog.csdnimg.cn/20210501142646791.png)

假设之后两个`id`分别为`100`、`200`的事务对这条记录进行`UPDATE`操作，操作流程如下：

![](https://img-blog.csdnimg.cn/20210501143300538.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Zsdzc3NTg=,size_16,color_FFFFFF,t_70)

每次对记录进行改动，都会记录一条`undo日志`，每条`undo日志`也都有一个`roll_pointer`属性（`INSERT`操作对应的`undo日志`没有该属性，因为该记录并没有更早的版本），可以将这些`undo日志`都连起来，串成一个链表，所以现在的情况就像下图一样：

![](https://img-blog.csdnimg.cn/20210501143354406.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Zsdzc3NTg=,size_16,color_FFFFFF,t_70)

####  ReadView

对于使用`READ UNCOMMITTED`隔离级别的事务来说，直接读取记录的最新版本就好了，对于使用`SERIALIZABLE`隔离级别的事务来说，使用加锁的方式来访问记录。对于使用`READ COMMITTED`和`REPEATABLE READ`隔离级别的事务来说，就需要用到我们上边所说的`版本链`了，核心问题就是：需要判断一下版本链中的哪个版本是当前事务可见的。所以设计`InnoDB`的大叔提出了一个`ReadView`的概念，这个`ReadView`中主要包含当前系统中还有哪些活跃的读写事务，把它们的事务id放到一个列表中，我们把这个列表命名为为`m_ids`。这样在访问某条记录时，只需要按照下边的步骤判断记录的某个版本是否可见：



- 如果被访问版本的`trx_id`属性值小于`m_ids`列表中最小的事务id，表明生成该版本的事务在生成`ReadView`前已经提交，所以该版本可以被当前事务访问。

  

- 如果被访问版本的`trx_id`属性值大于`m_ids`列表中最大的事务id，表明生成该版本的事务在生成`ReadView`后才生成，所以该版本不可以被当前事务访问。

  

- 如果被访问版本的`trx_id`属性值在`m_ids`列表中最大的事务id和最小事务id之间，那就需要判断一下`trx_id`属性值是不是在`m_ids`列表中，如果在，说明创建`ReadView`时生成该版本的事务还是活跃的，该版本不可以被访问；如果不在，说明创建`ReadView`时生成该版本的事务已经被提交，该版本可以被访问。



如果某个版本的数据对当前事务不可见的话，那就顺着版本链找到下一个版本的数据，继续按照上边的步骤判断可见性，依此类推，直到版本链中的最后一个版本，如果最后一个版本也不可见的话，那么就意味着该条记录对该事务不可见，查询结果就不包含该记录。

在`MySQL`中，`READ COMMITTED`和`REPEATABLE READ`隔离级别的的一个非常大的区别就是它们生成`ReadView`的时机不同，我们来看一下。



###  4：实践验证RR，RC隔离级别下的MVCC

####  RC模式下：

现在通过两个事务来处理一个数据，解读一下MVCC的实现原理。这里事务提交方式改为手动，事务隔离级别改为RC

![](https://img-blog.csdnimg.cn/20210501144126893.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Zsdzc3NTg=,size_16,color_FFFFFF,t_70)

1: 查询结果为：刘备

2：执行事务100，不提交

我们可以得到再undo.log中的一个这样的版本链

![](https://img-blog.csdnimg.cn/20210501144224212.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Zsdzc3NTg=,size_16,color_FFFFFF,t_70)

这个时候我们再执行，查询，可以看到查询结果

![](https://img-blog.csdnimg.cn/20210501144445543.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Zsdzc3NTg=,size_16,color_FFFFFF,t_70)

可以看到结果还是刘备。

分析：

- 在执行`SELECT`语句时会先生成一个`ReadView`，`ReadView`的`m_ids`列表的内容就是`[100]`。

  

- 然后从版本链中挑选可见的记录，从图中可以看出，最新版本的列`c`的内容是`'张飞'`，该版本的`trx_id`值为`100`，在`m_ids`列表内，所以不符合可见性要求，根据`roll_pointer`跳到下一个版本。

  

- 下一个版本的列`c`的内容是`'关羽'`，该版本的`trx_id`值也为`100`，也在`m_ids`列表内，所以也不符合要求，继续跳到下一个版本。

  

- 下一个版本的列`c`的内容是`'刘备'`，该版本的`trx_id`值为`80`，小于`m_ids`列表中最小的事务id`100`，所以这个版本是符合要求的，最后返回给用户的版本就是这条列`c`为`'刘备'`的记录。



接下来我们执行操作：提交事务100的操作，同时执行事务200的操作，但是不提交，然后执行查询操作。

![](https://img-blog.csdnimg.cn/20210501144900337.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Zsdzc3NTg=,size_16,color_FFFFFF,t_70)

我们可以看到查询结果，为**张飞**

分析：这个时候的版本链是这样的

![](https://img-blog.csdnimg.cn/20210501145005983.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Zsdzc3NTg=,size_16,color_FFFFFF,t_70)

- 在执行`SELECT`语句时会先生成一个`ReadView`，`ReadView`的`m_ids`列表的内容就是`[200]`（事务id为`100`的那个事务已经提交了，所以生成快照时就没有它了）。

  

- 然后从版本链中挑选可见的记录，从图中可以看出，最新版本的列`c`的内容是`'诸葛亮'`，该版本的`trx_id`值为`200`，在`m_ids`列表内，所以不符合可见性要求，根据`roll_pointer`跳到下一个版本。

  

- 下一个版本的列`c`的内容是`'赵云'`，该版本的`trx_id`值为`200`，也在`m_ids`列表内，所以也不符合要求，继续跳到下一个版本。

  

- 下一个版本的列`c`的内容是`'张飞'`，该版本的`trx_id`值为`100`，比`m_ids`列表中最小的事务id`200`还要小，所以这个版本是符合要求的，最后返回给用户的版本就是这条列`c`为`'张飞'`的记录。



以此类推，如果之后事务id为`200`的记录也提交了，再此在使用`READ COMMITTED`隔离级别的事务中查询表`t`中`id`值为`1`的记录时，得到的结果就是`'诸葛亮'`了，具体流程我们就不分析了。总结一下就是：**使用READ COMMITTED隔离级别的事务在每次查询开始时都会生成一个独立的ReadView。**

![](https://img-blog.csdnimg.cn/20210501145309898.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Zsdzc3NTg=,size_16,color_FFFFFF,t_70)

###  RR模式下的MVCC

对于使用`REPEATABLE READ`隔离级别的事务来说，只会在第一次执行查询语句时生成一个`ReadView`，之后的查询就不会重复生成了。我们还是用例子看一下是什么效果。

同样，按照上面的操作，重新操作。

事务100，操作，但是不提交，事务200 不进行操作，然后查询，

![](https://img-blog.csdnimg.cn/20210501145613817.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Zsdzc3NTg=,size_16,color_FFFFFF,t_70)

结果就是这样，原因和之前的一样，可以分析。这里就不在分析了。



接下来我们执行查询，但是查询事务不提交，然后提交事务100；同时执行事务200，但是不提交；然后再次执行查询

![](https://img-blog.csdnimg.cn/20210501150753553.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Zsdzc3NTg=,size_16,color_FFFFFF,t_70)



#####  分析：

上面执行可以得到undo.log的版本链

![](https://img-blog.csdnimg.cn/20210501150229820.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Zsdzc3NTg=,size_16,color_FFFFFF,t_70)







- 因为之前已经生成过`ReadView`了，所以此时直接复用之前的`ReadView`，之前的`ReadView`中的`m_ids`列表就是`[100, 200]`。

  

- 然后从版本链中挑选可见的记录，从图中可以看出，最新版本的列`c`的内容是`'诸葛亮'`，该版本的`trx_id`值为`200`，在`m_ids`列表内，所以不符合可见性要求，根据`roll_pointer`跳到下一个版本。

  

- 下一个版本的列`c`的内容是`'赵云'`，该版本的`trx_id`值为`200`，也在`m_ids`列表内，所以也不符合要求，继续跳到下一个版本。

  

- 下一个版本的列`c`的内容是`'张飞'`，该版本的`trx_id`值为`100`，而`m_ids`列表中是包含值为`100`的事务id的，所以该版本也不符合要求，同理下一个列`c`的内容是`'关羽'`的版本也不符合要求。继续跳到下一个版本。

  

- 下一个版本的列`c`的内容是`'刘备'`，该版本的`trx_id`值为`80`，`80`小于`m_ids`列表中最小的事务id`100`，所以这个版本是符合要求的，最后返回给用户的版本就是这条列`c`为`'刘备'`的记录。

  

也就是说两次`SELECT`查询得到的结果是重复的，记录的列`c`值都是`'刘备'`，这就是`可重复读`的含义。如果我们之后再把事务id为`200`的记录提交了，之后再到刚才使用`REPEATABLE READ`隔离级别的事务中继续查找这个id为`1`的记录，得到的结果还是`'刘备'`，具体执行过程大家可以自己分析一下。



###  总结

从上边的描述中我们可以看出来，所谓的MVCC（Multi-Version Concurrency Control ，多版本并发控制）指的就是在使用`READ COMMITTD`、`REPEATABLE READ`这两种隔离级别的事务在执行普通的`SEELCT`操作时访问记录的版本链的过程，这样子可以使不同事务的`读-写`、`写-读`操作并发执行，从而提升系统性能。`READ COMMITTD`、`REPEATABLE READ`这两个隔离级别的一个很大不同就是生成`ReadView`的时机不同，`READ COMMITTD`在每一次进行普通`SELECT`操作前都会生成一个`ReadView`，而`REPEATABLE READ`只在第一次进行普通`SELECT`操作前生成一个`ReadView`，之后的查询操作都重复这个`ReadView`就好了。