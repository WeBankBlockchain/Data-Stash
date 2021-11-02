package com.webank.blockchain.data.stash.handler;

import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author aaronchu
 * @Description
 * @date 2021/11/01
 */
@Component
public class TaskCounterHandler {
    private volatile int counter;
    private ReentrantLock lock;
    private Condition condition;
    public TaskCounterHandler(){
        this.counter = 0;
        this.lock = new ReentrantLock(false);
        this.condition = lock.newCondition();
    }

    public void increase(){
        try{
            lock.lock();
            this.counter++;
        }
        finally {
            lock.unlock();
        }
    }

    public void decrease(){
        if(this.counter <= 0){
            throw new IllegalStateException("counter can't be less than zero");
        }
        try{
            lock.lock();
            this.counter--;
            if(this.counter == 0){
                this.condition.signalAll();
            }
        }
        finally {
            lock.unlock();
        }
    }

    public void await(){
        try{
            lock.lock();
            while(this.counter != 0){
                this.condition.await();
            }
        }
        catch (InterruptedException ex){
        }
        finally {
            lock.unlock();
        }
    }
}
