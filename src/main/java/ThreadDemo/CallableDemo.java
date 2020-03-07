package ThreadDemo;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class CallableDemo {
    public static void main(String[] args) {
        FutureTask<Integer> futureTask = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                try {
                    TimeUnit.SECONDS.sleep(3);} catch (InterruptedException e) {e.printStackTrace();}
                return 10 * 10;
            }
        });
        futureTask.run();
        while (!futureTask.isDone()){

        }
        try {
            System.out.println(futureTask.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
