package com.yuca.demo;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class DemoTaskFactory {

    public static AtomicInteger id= new AtomicInteger(0);
    public ConcurrentLinkedQueue<IDemoTask> demoTasks;

    DemoTaskFactory(){
        demoTasks=new ConcurrentLinkedQueue<>();
    }

    public void AddNewTask(long runTime){
        demoTasks.offer(new DemoTask(runTime));
    }
}
