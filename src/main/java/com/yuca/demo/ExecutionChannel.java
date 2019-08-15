package com.yuca.demo;

import java.util.ArrayList;
import java.util.List;

public class ExecutionChannel {
    final List<Terminal> terminals = new ArrayList<>();
    public ExecutionChannel(int m) {
        for (int i = 0; i < m; i++) {
            terminals.add(new Terminal(ExecutionChannelFactory.terminalID.getAndIncrement()));
        }
    }

    public boolean getBusyStatus() {
        return !terminals.stream().anyMatch(x -> x.running_status == false);
    }

    public void runTask(IDemoTask task) throws Exception {
        if(!getBusyStatus()){
            Terminal idle_terminal = terminals.stream().filter(x -> x.running_status == false)
                    .findAny()
                    .orElseThrow(() -> new Exception("Could not find idle terminal"));

            idle_terminal.lockTernimal();
            ExecutionChannelFactory.terminalSimulator.submit(()->
                idle_terminal.runTask(task)
            );
        }
    }
}
