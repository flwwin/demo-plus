package com.leven.demoplus.devstg.observer;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service("bookingService")
@Repository
public class JpaBookingService implements ApplicationContextAware {

    private ApplicationContext context; //也可以直接AutoWired

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    /**
     * 可以通过ApplicationListener实现一个观察者模式
     * 这个案例中，通过booking订阅一个酒店的时候就可以发送一个事件，BookingEventsListener监听到了
     * 以后就可以发送短信给用户
     * @param user
     */
    public void persistBooking(User user) {

        BookingCreatedEvent event = new BookingCreatedEvent(this, user);
        System.out.println("预定一个酒店");
        //触发event
        this.context.publishEvent(event);

    }


}
