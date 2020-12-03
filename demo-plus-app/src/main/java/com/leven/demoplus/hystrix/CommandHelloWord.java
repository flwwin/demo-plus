package com.leven.demoplus.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CommandHelloWord extends HystrixCommand<String> {
    private final String name;

    public CommandHelloWord(String name) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        throw new RuntimeException("this comand always fails");
        //return "Hello " + name + "!";
    }

    /**
     * 熔断时候的回调方法，包括执行时候的异常
     * @return
     */
    @Override
    protected String getFallback() {
        return "Hello Failure " + name + "!";
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CommandHelloWord hello = new CommandHelloWord("hello");
        Future<String> queue = hello.queue();
        System.out.println("queue = " + queue.get());
    }


}
