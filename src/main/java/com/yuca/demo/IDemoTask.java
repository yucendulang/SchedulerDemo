package com.yuca.demo;

public interface IDemoTask {
    boolean run() throws InterruptedException;
    int getID();
    long getRunTime();
}
