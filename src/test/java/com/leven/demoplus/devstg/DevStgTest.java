package com.leven.demoplus.devstg;

import com.leven.demoplus.devstg.dataconsumer.ConsumeData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class DevStgTest {

    @Autowired
    ConsumeData consumerData;

    @Test
    public void test01() {

    }

    @Test
    public void test02() throws InterruptedException {
        String s = "1";
        for (int i = 0; i < 1000; i++) {
            consumerData.submit(i);
        }
        TimeUnit.SECONDS.sleep(10);
    }
}
