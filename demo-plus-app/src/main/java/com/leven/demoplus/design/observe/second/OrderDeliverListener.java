package com.leven.demoplus.design.observe.second;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderDeliverListener implements ApplicationListener<OrderDeliver> {


    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void onApplicationEvent(OrderDeliver orderDeliver) {
        Map param = orderDeliver.getParam();
        SendEmailToUser sendEmailToUser = new SendEmailToUser();
        sendEmailToUser.setParam(param);
        taskExecutor.execute(sendEmailToUser);
    }
}
