package com.webank.blockchain.data.stash.thread;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
public class MultiPartsTask {
    private long blockNum;
    private AtomicInteger counter;
    private Consumer<Long> taskDoneHandler;
    private BiConsumer<Long, Exception> exceptionHandler;

    public MultiPartsTask(long blockNum, int taskParts, Consumer<Long> taskDoneHandler, BiConsumer<Long, Exception> exceptionHandler) {
        this.blockNum = blockNum;
        this.counter = new AtomicInteger(taskParts);
        this.taskDoneHandler = taskDoneHandler;
        this.exceptionHandler = exceptionHandler;
    }

    public void finishPart() {
        int decreaseVal = this.counter.decrementAndGet();
        if(decreaseVal == 0){
            taskDoneHandler.accept(blockNum);
        }
    }
}