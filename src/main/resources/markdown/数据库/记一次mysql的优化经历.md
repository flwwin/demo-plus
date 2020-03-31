###  记一次Mysql的优化经历

#####  1.建立一个课程表

``` sql
create table Course(

c_id int PRIMARY KEY,

name varchar(10)

)
```

#####  2.建立一个学生表

``` sql
create table Student(

s_id int PRIMARY KEY,

name varchar(10)

)
```

#####  3.建立一个课程学生表

``` sql
CREATE table SC(

    sc_id int PRIMARY KEY,

    s_id int,

    c_id int,

    score int

)
```

#####  4.通过存储过程 插入七十万的数据

```sql
DELIMITER $$
CREATE PROCEDURE insert_dept(IN START INT(10),IN max_num INT(10))
BEGIN
DECLARE i INT DEFAULT 0;
SET autocommit = 0;
REPEAT
SET i=i+ 1;
INSERT INTO course(c_id ,name) VALUES ((START+i),CONCAT("语文"));
UNTIL i = max_num
END REPEAT;
COMMIT;
END $$
```

```sql
DELIMITER $$
CREATE PROCEDURE insert_dept(IN START INT(10),IN max_num INT(10))
BEGIN
DECLARE i INT DEFAULT 0;
SET autocommit = 0;
REPEAT
SET i=i+ 1;
INSERT INTO student(id ,name) VALUES (START+i,(CONCAT("小明",i)));
UNTIL i = max_num
END REPEAT;
COMMIT;
END $$
```

#####  5.查询语文成绩为89的学生信息

```sql
select * from student s where s.s_id in (SELECT sc.s_id from sc sc where sc.score = 89 and sc.c_id = 1)
```

​	执行后发现时间要的很久，我直接停止查询了

然后我们建立索引，提高查询速度

``` sql
drop index  idx_sc_score on sc;
drop index  idx_sc_c_id on sc;

CREATE INDEX  idx_sc_score ON sc(score);
CREATE INDEX  idx_sc_c_id ON sc(c_id);
```

查询发现还是很慢，查看一下查询计划

![](..\..\image\数据库\QQ截图20200329151209.png)

 存在全表扫描，速度太慢了

查看一下mysql的处理执行器优化过后的sql

在explain extended  后执行show warning

```sql
EXPLAIN EXTENDED select * from student s where s.s_id in (SELECT sc.s_id from sc sc where sc.score = 89 and sc.c_id = 1);
show WARNINGS

```

可以看到优化后的sql

```sql
SELECT
	`crm`.`s`.`s_id` AS `s_id`,
	`crm`.`s`.`name` AS `name`
FROM
	`crm`.`student` `s`
WHERE
	< in_optimizer > (
		`crm`.`s`.`s_id` ,< EXISTS > (
			SELECT
				1
			FROM
				`crm`.`sc`
			WHERE
				(
					(`crm`.`sc`.`c_id` = 1)
					AND (`crm`.`sc`.`score` = 89)
					AND (
						< CACHE > (`crm`.`s`.`s_id`) = `crm`.`sc`.`s_id`
					)
				)
		)
	)
```

可以看到mysql  经过优化后 将in修改为exists

```sql
SELECT count(1) from sc sc where sc.score = 89 and sc.c_id = 1
:1162
```



这个不符合exists后接大表的原则，需要循环700000*1162次

所以可以通过连表查询：

``` sql
select * from student s INNER JOIN sc sc on s.s_id = sc.sc_id where sc.score = 89 and sc.c_id = 1
```

这样的话，速度快了很多 0.416s

查看执行计划：

![](..\..\image\数据库\QQ截图20200329155330.png)

还是有可以优化的空间

查看一下优化过后的sql

```sql
SELECT
	`crm`.`s`.`s_id` AS `s_id`,
	`crm`.`s`.`name` AS `name`,
	`crm`.`sc`.`sc_id` AS `sc_id`,
	`crm`.`sc`.`s_id` AS `s_id`,
	`crm`.`sc`.`c_id` AS `c_id`,
	`crm`.`sc`.`score` AS `score`
FROM
	`crm`.`student` `s`
JOIN `crm`.`sc`
WHERE
	(
		(
			`crm`.`s`.`s_id` = `crm`.`sc`.`sc_id`
		)
		AND (`crm`.`sc`.`c_id` = 1)
		AND (`crm`.`sc`.`score` = 89)
	)
```

先连表在去通过where条件过滤，还是很大，可以先将sc表中过滤后再去连表

```sql
select s.* from (SELECT sc.s_id from sc sc where sc.score = 89 and sc.c_id = 1) t INNER JOIN student s on s.s_id = t.s_id 
```

这样子以后，速度更快了0.121s

查看执行计划

![](..\..\image\数据库\explain 03.png)

这里用到了intersect 这样并集操作，就是两个索引同事检索，然后再求并集

在看score 和c_id这两个字段，区分度不高，我们可以建立联合索引，也可以提交高区分度

```sql
CREATE index ind_sc_score_id on sc(score,s_id)
```

删除之前的索引，然后执行后发现 0.056s快了一倍

![](..\..\image\数据库\explain4.png)

这样还可以了

总结：

1. 列类型尽量定义成数值类型，且长度尽可能短，如主键和外键，类型字段等等

2. 建立单列索引

3. 根据需要建立多列联合索引

当单个列过滤之后还有很多数据，那么索引的效率将会比较低，即列的区分度较低，

那么如果在多个列上建立索引，那么多个列的区分度就大多了，将会有显著的效率提高。

4. 根据业务场景建立覆盖索引

只查询业务需要的字段，如果这些字段被索引覆盖，将极大的提高查询效率

5. 多表连接的字段上需要建立索引

这样可以极大的提高表连接的效率

6. where条件字段上需要建立索引

7. 排序字段上需要建立索引

8. 分组字段上需要建立索引

9. Where条件上不要使用运算函数，以免索引失效