package com.leven.demoplus.mybatis.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Mapper
@Service
public interface UserDao {

    void get(Integer id);
}
