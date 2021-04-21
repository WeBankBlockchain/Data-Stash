package com.webank.blockchain.data.stash.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DataStashThreadFactory implements ThreadFactory {

    private AtomicInteger counter = new AtomicInteger();
    private String poolName;

    public DataStashThreadFactory(String poolName){
        this.poolName = poolName;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(poolName + counter.incrementAndGet());
        return thread;
    }
}
