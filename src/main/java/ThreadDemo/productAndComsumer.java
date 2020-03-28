package ThreadDemo;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class resource {
    private  int i = 0;
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void incr() {
        lock.lock();
        try {
            while (i != 0) {
                condition.await();
            }

            i++;
            System.out.println(Thread.currentThread().getName()+"号线程" + i);
            condition.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void desc() {
        lock.lock();
        try {
            while (i == 0) {
                condition.await();
            }

            i--;
            System.out.println(Thread.currentThread().getName()+"号线程" + i);
            condition.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}

public class productAndComsumer {
    public static void main(String[] args) {
        resource resource = new resource();

        for (int i = 0; i < 100; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    resource.incr();
                }
            }, String.valueOf(i)).start();
        }

        for (int i = 0; i < 100; i++) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    resource.desc();
                }
            }, String.valueOf(i)).start();

        }
    }
}
