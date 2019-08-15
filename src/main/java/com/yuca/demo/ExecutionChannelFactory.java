package com.yuca.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ExecutionChannelFactory {

    List<ExecutionChannel> executionChannels = new ArrayList<>();
    public static ExecutorService terminalSimulator = Executors.newCachedThreadPool();
    public static AtomicInteger terminalID= new AtomicInteger(0);
    final int min = 3;
    final int range = 5;

    static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    static Lock rLock = rwl.readLock();

    public void initExecutionChannel(int n) {
        for (int i = 0; i < n; i++) {
            executionChannels.add(new ExecutionChannel(ThreadLocalRandom.current().nextInt(min, min + range)));
        }
    }

    public boolean getBusyStatus() {
        return !executionChannels.stream().anyMatch(x -> x.getBusyStatus() == false);
    }

    public void runTasks(IDemoTask task) throws Exception {
        rLock.lock();
        if(!getBusyStatus()){
            executionChannels.stream().filter(x->x.getBusyStatus() ==false)
                    .findAny()
                    .orElseThrow(()->new Exception("Could not find idle channel"))
                    .runTask(task);
        }
        rLock.unlock();
    }
}
