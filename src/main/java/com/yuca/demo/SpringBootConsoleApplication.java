package com.yuca.demo;

import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SpringBootApplication
public class SpringBootConsoleApplication
        implements CommandLineRunner {

    AtomicInteger id;
    final int channelMin = 10;
    final int channelMax = 10;

    private static Logger LOG = LoggerFactory
            .getLogger(SpringBootConsoleApplication.class);

    public static void main(String[] args) {
        LOG.info("STARTING THE APPLICATION");
        SpringApplication.run(SpringBootConsoleApplication.class, args);
        LOG.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) throws InterruptedException {
        // init channel
        ExecutionChannelFactory executionChannelFactory = new ExecutionChannelFactory();
        executionChannelFactory.initExecutionChannel((int) (channelMin + Math.random() * channelMax));

        // init taskFactor
        DemoTaskFactory taskFactory = new DemoTaskFactory();


        // log to console
        ExecutorService executorMonitor = Executors.newSingleThreadExecutor();
        executorMonitor.submit(() -> {
                    while (true) {
                        System.out.println("Undo task number:" + taskFactory.demoTasks.size());
                        System.out.println("Channel Status:" + executionChannelFactory.getBusyStatus());
                        LOG.info("Channels Status:{}" + executionChannelFactory.executionChannels.stream()
                                .map(x -> x.getBusyStatus()).collect(Collectors.toList()));
                        Thread.sleep(1000);
                    }
                }
        );

        // multi thread to add task
        Executors.newSingleThreadExecutor().submit(() -> {
            ExecutorService callerSimulator = Executors.newCachedThreadPool();
            while (true) {
                callerSimulator.submit(
                        () -> {
                            taskFactory.AddNewTask((int) (Math.random() * 10));
                        }
                );
                Thread.sleep(500);
            }
        });

        // new thread to schedule
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true){
                if(!taskFactory.demoTasks.isEmpty()&&!executionChannelFactory.getBusyStatus()){
                    executionChannelFactory.runTasks(taskFactory.demoTasks.poll());
                }
                Thread.sleep(1);
            }
        });
    }
}