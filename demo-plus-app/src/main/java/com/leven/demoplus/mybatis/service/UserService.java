package com.leven.demoplus.mybatis.service;

import com.leven.demoplus.mybatis.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService{

    @Autowired
    UserDao userDao;

    public void get(Integer id) {
        userDao.get(id);
    }

}
