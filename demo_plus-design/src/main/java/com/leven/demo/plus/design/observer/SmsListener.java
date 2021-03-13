package com.leven.demo.plus.design.observer;

import com.lenven.demo.plus.common.User;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


@Component
public class SmsListener implements ApplicationListener<BookingCreatedEvent> {

    public void onApplicationEvent(BookingCreatedEvent event) {
        //do something 
        User booking = event.getBooking();
        System.out.println("发送订酒店信息给："+booking.getName());
    }
}
