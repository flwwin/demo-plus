##  mySQL高水位问题导致的sql查询变慢

**背景：**
发现一条SQL需要10s才能出结果，严重影响性能

**过程：**

​	首先怀疑sql问题，源sql如下

```sql
select * from 
```

发现没有必要多次扫描表，优化sql后如下：

```sql

```

执行后发现效率并没有提升很多

通过执行计划发现瓶颈主要在 IBAK_KB_PROJ_INFO_HF

,查询该表数据量不大，不足十万，单表查询这张表性能也不高。

怀疑是高水位的问题导致，该表频繁插入，删除数据，

然后收缩表后发现性能大幅提升。

降低水位方法：

```sql
Alter table xxx enable row movement;
alter table xxx shrink space;
```

