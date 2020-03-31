package ThreadDemo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * countDownLatch是基于时间片进行调度的，当一个线程出现异常没有执行countDown方法时候，
 * 就会一直阻塞，或者在计时的情况下是会等待较长的时间
 * 下面的例子就是用三个线程执行一个翻译任务，当其中线程出现异常的时候出现的问题
 */
class TranThread extends Thread {
    private String content;
    private CountDownLatch count;

    public TranThread(String content, CountDownLatch count) {
        this.content = content;
        this.count = count;
    }

    @Override
    public void run() {
        if (Math.random() > 0.5) {
            throw new RuntimeException("出现异常字符。。");
        }
        count.countDown();
        System.out.println(content);

    }
}

public class CountDownLatchTest {

    public static void main(String[] args) {
        CountDownLatch count = new CountDownLatch(3);
        TranThread t1 = new TranThread("1st,翻译", count);
        TranThread t2 = new TranThread("2st,翻译", count);
        TranThread t3 = new TranThread("3st,翻译", count);

        t1.start();
        t2.start();
        t3.start();

        try {
             try {
                 TimeUnit.SECONDS.sleep(3);} catch (InterruptedException e) {e.printStackTrace();}
            System.out.println("还有"+"\t"+count.getCount());
            count.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("翻译全部结束");
    }
}
