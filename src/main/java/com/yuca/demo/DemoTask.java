package com.yuca.demo;

import java.time.Duration;
import java.time.LocalDateTime;

public class DemoTask implements IDemoTask {

    final public int id;
    final long runTime;
    final LocalDateTime createTime;
    // max 5 min 1 higher first
    final int priorityLevel;

    DemoTask(long runTime) {
        this.id = DemoTaskFactory.id.getAndIncrement();
        this.runTime = runTime;
        this.createTime =LocalDateTime.now();
        this.priorityLevel=1+(int)(Math.random()*5);
    }
    @Override
    public boolean run() throws InterruptedException {
        Thread.sleep(runTime*1000);
        return true;
    }
    @Override
    public int getID(){
        return id;
    }
    @Override
    public long getRunTime(){
        return runTime;
    }

    // maybe use long to store time
    public long getWaitingTime() {
        return java.time.Duration.between(createTime,LocalDateTime.now()).toMillis();
    }

    @Override
    public double getRRN() {
        long l = runTime * 1000;
        return ((double) (l + getWaitingTime())) / l;
    }

    @Override
    public int getPriorityLevel(){
        return priorityLevel;
    }
}