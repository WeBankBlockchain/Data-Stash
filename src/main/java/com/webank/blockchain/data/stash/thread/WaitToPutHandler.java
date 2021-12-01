package com.webank.blockchain.data.stash.thread;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * We need to ensure the sequential property
 * @author aaronchu
 * @Description
 * @data 2021/05/08
 */
public class WaitToPutHandler implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        if (!e.isShutdown()) {
            try{
                e.getQueue().put(r);
            }
            catch (InterruptedException ex){}
        }
    }
}
