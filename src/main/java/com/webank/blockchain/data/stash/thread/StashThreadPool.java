package com.webank.blockchain.data.stash.thread;

import org.fisco.bcos.sdk.abi.datatypes.Int;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class StashThreadPool extends ThreadPoolExecutor {
    private AtomicLong runningTasks;
    private AtomicBoolean canSubmitNewTask;
    private ReentrantLock lock;
    private Condition emptyTaskCondition;

    public StashThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, rejectedExecutionHandler);
        this.runningTasks = new AtomicLong();
        this.canSubmitNewTask = new AtomicBoolean();
        this.lock = new ReentrantLock();
        this.emptyTaskCondition = lock.newCondition();
    }



    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        runningTasks.incrementAndGet();
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        runningTasks.decrementAndGet();
    }

    public long getRunningTasksCount(){
        return runningTasks.get();
    }

    public void waitAllRunningTasksComplete() throws InterruptedException {
        this.lock.lock();
        try{
            while ()
        }
        finally {

        }
        return runningTasks.get();
    }
}
