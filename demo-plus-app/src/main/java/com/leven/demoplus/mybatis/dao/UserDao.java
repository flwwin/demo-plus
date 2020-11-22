package com.leven.demoplus.mybatis.dao;

import com.leven.demoplus.enity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Mapper
@Service
public interface UserDao {

    User get(Integer id);
}
