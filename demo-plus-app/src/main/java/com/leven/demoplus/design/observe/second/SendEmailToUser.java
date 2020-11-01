package com.leven.demoplus.design.observe.second;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Data
public class SendEmailToUser implements Runnable {

    public Map param;

    @Override
    public void run() {
        sendEmail(param);
    }

    public void sendEmail(Map param){
        System.out.println("发送email"+param);
    }
}
