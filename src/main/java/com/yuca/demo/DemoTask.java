package com.yuca.demo;

public class DemoTask implements IDemoTask {

    final public int id;
    final long runTime;

    DemoTask(long runTime) {
        this.id = DemoTaskFactory.id.getAndIncrement();
        this.runTime = runTime;
    }

    public boolean run() throws InterruptedException {
        Thread.sleep(runTime*1000);
        return true;
    }

    public int getID(){
        return id;
    }

    public long getRunTime(){
        return runTime;
    }
}