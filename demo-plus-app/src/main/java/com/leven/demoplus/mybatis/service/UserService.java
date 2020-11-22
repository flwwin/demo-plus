package com.leven.demoplus.mybatis.service;

import com.leven.demoplus.mybatis.dao.UsersMapper;
import com.leven.demoplus.mybatis.entity.Users;
import com.leven.demoplus.mybatis.entity.UsersExample;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService {

  private final UsersMapper usersMapper;


  public UserService(UsersMapper userDao) {
    this.usersMapper = userDao;
  }

  public void get(Integer id) {
    UsersExample usersExample = new UsersExample();
    usersExample.createCriteria().andIdEqualTo(id);

    List<Users> users = usersMapper.selectByExample(usersExample);
    System.out.println("users = " + users);
  }
}
