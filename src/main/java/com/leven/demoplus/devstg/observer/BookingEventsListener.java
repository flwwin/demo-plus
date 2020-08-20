package com.leven.demoplus.devstg.observer;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


@Component
public class BookingEventsListener implements ApplicationListener<BookingCreatedEvent> {

    public void onApplicationEvent(BookingCreatedEvent event) {
        //do something 
        User booking = event.getBooking();
        System.out.println("发送订酒店信息给："+booking.getName());
    }
}
