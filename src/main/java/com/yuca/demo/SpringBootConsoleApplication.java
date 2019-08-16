package com.yuca.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SpringBootApplication
public class SpringBootConsoleApplication
        implements CommandLineRunner {

    public static final int TASK_PRODUCE_NUM = 500;
    public static final int LOG_FREQUENCY = 1000;
    final int channelMin = 2;
    final int channelMax = 2;

    @Autowired
    DemoTaskFactory taskFactory;

    @Autowired
    ExecutionChannelFactory executionChannelFactory;

    private static Logger LOG = LoggerFactory
            .getLogger(SpringBootConsoleApplication.class);

    public static void main(String[] args) {
        LOG.info("STARTING THE APPLICATION");
        SpringApplication.run(SpringBootConsoleApplication.class, args);
        LOG.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) {
        // init channel
        executionChannelFactory.initExecutionChannel((int) (channelMin + Math.random() * (channelMax - channelMin)));

        // log to console
        logToConsole(executionChannelFactory, taskFactory);

        // multi thread to add task
        produceTask(taskFactory);

        String scheduleType = args != null ? args[0] : "fcfs";

        // schedule thread
        schedule(scheduleType);
    }

    private void schedule(String scheduleType) {
        Supplier<IDemoTask> supplier;
        switch (scheduleType) {
            case "fcfs":
                supplier = () -> taskFactory.demoTasks.poll();
                break;
            case "sjn":
                supplier = () -> taskFactory.getShortestTask();
                break;
            case "hrrn":
                supplier = () -> taskFactory.getHighestRRN();
                break;
            case "pcf":
                supplier = () -> taskFactory.getPriorityAndHighestRNN();
                break;
            default:
                supplier = () -> taskFactory.demoTasks.poll();
        }
        Schedule(supplier);
    }

    private void Schedule(Supplier<IDemoTask> supplier) {
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                if (!taskFactory.demoTasks.isEmpty() && !executionChannelFactory.getBusyStatus()) {
                    executionChannelFactory.runTasks(supplier.get());
                }
                Thread.sleep(1);
            }
        });
    }
    private void produceTask(DemoTaskFactory taskFactory) {
        Executors.newSingleThreadExecutor().submit(() -> {
            ExecutorService callerSimulator = Executors.newCachedThreadPool();
            for (int i = 0; i < TASK_PRODUCE_NUM; i++) {
                callerSimulator.submit(
                        () -> taskFactory.AddNewTask((int) (1 + Math.random() * 10))
                );
                try {
                    Thread.sleep(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void logToConsole(ExecutionChannelFactory executionChannelFactory, DemoTaskFactory taskFactory) {
        ExecutorService executorMonitor = Executors.newSingleThreadExecutor();
        executorMonitor.submit(() -> {
                    while (true) {
                        LOG.info("Undo task number:{}", taskFactory.demoTasks.size());
                        LOG.info("Channel Busy:{},Channels Busy:{}",
                                executionChannelFactory.getBusyStatus(),
                                executionChannelFactory.executionChannels.stream()
                                        .map(x -> x.getBusyStatus()).collect(Collectors.toList()));
                        Thread.sleep(LOG_FREQUENCY);
                    }
                }
        );
    }
}