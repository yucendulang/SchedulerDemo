package com.yuca.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;

public class Terminal {
    boolean running_status=false;
    int id;
    private static Logger LOG = LoggerFactory
            .getLogger(DemoTask.class);

    Terminal(int id) {
        this.id = id;
    }
    public void runTask(IDemoTask task) {
        running_status = true;
        try {
            LOG.info("TerminalID:{} TaskID:{} RunTime:{} START TO RUN ", id, task.getID(),task.getRunTime());
            task.run();
            LOG.info("TerminalID:{} TaskID:{} END TO RUN", id, task.getID());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        running_status = false;
    }

    public boolean getRunningStatus() {
        return running_status;
    }

    public void lockTernimal(){
        running_status=true;
    }
}
