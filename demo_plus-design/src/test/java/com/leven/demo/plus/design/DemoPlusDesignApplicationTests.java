package com.leven.demo.plus.design;

import com.lenven.demo.plus.common.LoginRequest;
import com.lenven.demo.plus.common.LoginType;
import com.lenven.demo.plus.common.User;
import com.leven.demo.plus.design.chain.FilterFactory;
import com.leven.demo.plus.design.chain.LinkedFilterChain;
import com.leven.demo.plus.design.chain.enity.RequestContext;
import com.leven.demo.plus.design.chain.enity.RespondContext;
import com.leven.demo.plus.design.observer.JpaBookingService;
import com.leven.demo.plus.design.strategy.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoPlusDesignApplicationTests {

  @Test
  void contextLoads() {}


  @Autowired
  JpaBookingService bookingService;

  @Autowired
  LoginService loginService;

  @Autowired
  FilterFactory filterFactory;


  @Test
  public void test01(){
    User user = new User();
    user.setName("小明");
    bookingService.persistBooking(user);
  }


  @Test
  void  testStrategyPattern(){
    // 通过QQ进行登录
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setLoginType(LoginType.QQ);
    loginService.login(loginRequest);

    loginRequest.setLoginType(LoginType.WE_CHAT);
    loginService.login(loginRequest);
  }

  @Test
  void testFilteChain(){
    LinkedFilterChain instance = filterFactory.getInstance();
    RequestContext requestContext = new RequestContext();
    RespondContext respondContext = new RespondContext();
    instance.doFilter(requestContext,respondContext);
  }
}
