package com.leven.demoplus.springtest;

import com.leven.demoplus.devstg.factory.HandService;
import com.leven.demoplus.devstg.observer.User;
import com.leven.demoplus.devstg.observer.JpaBookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpringTest {
    @Autowired
    JpaBookingService bookingService;

    @Autowired
    HandService handService;

    @Test
    void test01(){
        User user = new User();
        user.setName("小明");
        bookingService.persistBooking(user);
    }

    @Test
    void test00(){
        handService.consumerData();
    }
}
