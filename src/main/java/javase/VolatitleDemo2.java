package javase;

import java.util.concurrent.TimeUnit;

/**
 * 验证volatitle的可见性
 */
class Mydate{
    volatile int num = 0;
    public void incrNum(){
        this.num = 60;
    }
}
public class VolatitleDemo2
{
    public static void main(String[] args) {
        final Mydate mydate = new Mydate();
        new Thread(()->{
            System.out.println(Thread.currentThread().getName()+"\t"+"come in");
            try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }
            //三秒后修改num为60
            mydate.incrNum();
            System.out.println(Thread.currentThread().getName()+"\t"+"update after"+ mydate.num);

        }).start();

        //其他线程修改后通知主内存后 num 为60 不会阻塞
        while (mydate.num == 0){
            //num为0时 一直自旋
        }

        System.out.println("VolatitleDemo2.main\t"+mydate.num);
    }
}
