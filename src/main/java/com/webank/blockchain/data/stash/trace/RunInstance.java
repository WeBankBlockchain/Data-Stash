package com.webank.blockchain.data.stash.trace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public  class RunInstance {

    private String module;

    private String tname;

    private long startTime;

    private long endTime;

     void start(){
        this.tname = Thread.currentThread().getName();
        this.startTime = System.currentTimeMillis();
    }

     void end(){
        this.endTime = System.currentTimeMillis();
    }

}
