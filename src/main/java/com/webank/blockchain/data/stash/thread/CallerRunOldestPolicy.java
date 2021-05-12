package com.webank.blockchain.data.stash.thread;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author aaronchu
 * @Description
 * @data 2021/05/08
 */
public class CallerRunOldestPolicy implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        if (!e.isShutdown()) {
            Runnable oldest = e.getQueue().poll();
            oldest.run();
            e.execute(r);
        }
    }
}
