package com.yuca.demo;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class DemoTaskFactory {

    public static AtomicInteger id= new AtomicInteger(0);
    public ConcurrentLinkedQueue<IDemoTask> demoTasks;

    DemoTaskFactory(){
        demoTasks=new ConcurrentLinkedQueue<>();
    }

    // o(1)
    // thread safe
    public void AddNewTask(long runTime){
        demoTasks.offer(new DemoTask(runTime));
    }

    // o(n) use PriorityBlockingQueue to optimalize o(logn)
    // thread unsafe
    public IDemoTask getShortestTask() {
        IDemoTask min = demoTasks.stream().min(Comparator.comparing(IDemoTask::getRunTime)).get();
        demoTasks.remove(min);
        return min;
    }

    // o(n) use PriorityBlockingQueue to optimalize o(logn)
    // thread unsafe
    public IDemoTask getHighestRRN() {
        IDemoTask min = demoTasks.stream()
                .max(Comparator.comparing(IDemoTask::getRRN)).get();
        demoTasks.remove(min);
        return min;
    }
    public IDemoTask getPriorityAndHighestRNN() {
        IDemoTask task = demoTasks.stream()
                .collect(Collectors.groupingByConcurrent(IDemoTask::getPriorityLevel))
                .entrySet().stream().max(Map.Entry.comparingByKey())
                .get().getValue().stream()
                .max(Comparator.comparing(IDemoTask::getRRN)).get();
        demoTasks.remove(task);
        return task;
    }
}
