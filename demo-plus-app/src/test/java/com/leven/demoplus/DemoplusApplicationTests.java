package com.leven.demoplus;

import com.leven.demoplus.mybatis.dao.UsersMapper;
import com.leven.demoplus.mybatis.entity.Users;
import com.leven.demoplus.mybatis.entity.UsersExample;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
class DemoplusApplicationTests {

    @Autowired
    UsersMapper usersMapper;

    @Test
    void contextLoads() {
    System.out.println("年后");
    }

    @Test
    void test01(){
        UsersExample example = new UsersExample();
        UsersExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo("小明");

        List<Users> users = usersMapper.selectByExample(example);
        log.info("query result ,{}",users);
    }
}
