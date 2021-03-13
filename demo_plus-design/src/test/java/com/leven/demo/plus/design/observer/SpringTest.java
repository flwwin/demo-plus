package com.leven.demo.plus.design.observer;

import com.lenven.demo.plus.common.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpringTest {
    @Autowired
    JpaBookingService bookingService;


    @Test
    public void test01(){
        User user = new User();
        user.setName("小明");
        bookingService.persistBooking(user);
    }

}
