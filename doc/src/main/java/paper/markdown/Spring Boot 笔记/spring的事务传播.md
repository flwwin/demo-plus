## spring的事务传播

​		事务传播行为（propagation behavior）指的就是当一个事务方法被另一个事务方法调用时，这个事务方法应该如何进行。 
​		例如：methodA事务方法调用methodB事务方法时，methodB是继续在调用者methodA的事务中运行呢，还是为自己开启一个新事务运行，这就是由methodB的事务传播行为决定的。 

​	spring的事务传播有7种隔离级别级别，如下表所示。

| PROPAGATION_REQUIRED      | 如果存在一个事务，则支持当前事务。如果没有事务则开启一个新的事务。 |
| ------------------------- | ------------------------------------------------------------ |
| PROPAGATION_SUPPORTS      | 如果存在一个事务，支持当前事务。如果没有事务，则非事务的执行 |
| PROPAGATION_MANDATORY     | 如果已经存在一个事务，支持当前事务。如果没有一个活动的事务，则抛出异常。 |
| PROPAGATION_REQUIRES_NEW  | 使用PROPAGATION_REQUIRES_NEW,需要使用 JtaTransactionManager作为事务管理器。 它会开启一个新的事务。如果一个事务已经存在，则先将这个存在的事务挂起。 |
| PROPAGATION_NOT_SUPPORTED | PROPAGATION_NOT_SUPPORTED 总是非事务地执行，并挂起任何存在的事务 |
| PROPAGATION_NEVER         | 总是非事务地执行，如果存在一个活动事务，则抛出异常。         |
| PROPAGATION_NESTED        | 如果一个活动的事务存在，则运行在一个嵌套的事务中。 如果没有活动事务, 则按TransactionDefinition.PROPAGATION_REQUIRED 属性执行。 这是一个嵌套事务,使用JDBC 3.0驱动时,仅仅支持DataSourceTransactionManager作为事务管理器。 需要JDBC 驱动的java.sql.Savepoint类。使用PROPAGATION_NESTED，还需要把PlatformTransactionManager的nestedTransactionAllowed属性设为true(属性值默认为false)。 |

