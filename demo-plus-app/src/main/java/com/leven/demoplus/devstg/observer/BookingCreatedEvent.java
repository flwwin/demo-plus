package com.leven.demoplus.devstg.observer;

import org.springframework.context.ApplicationEvent;


public class BookingCreatedEvent extends ApplicationEvent {
    private static final long serialVersionUID = 8449798628774579104L;

    private User user;

    public BookingCreatedEvent(Object source, User user) {
        super(source);
        this.user = user;

    }

    public User getBooking() {
        return user;
    }
}
