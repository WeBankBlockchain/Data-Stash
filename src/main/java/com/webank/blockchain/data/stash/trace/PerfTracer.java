package com.webank.blockchain.data.stash.trace;

import com.webank.blockchain.data.stash.utils.JsonUtils;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class PerfTracer {

    public static PerfTracer SINGLETON = new PerfTracer();

    private PerfTracer(){}

    private Vector<RunInstance> instances = new Vector<>();
    private ConcurrentHashMap<String, Long> timeCosts = new ConcurrentHashMap<>();

    public RunInstance startInstance(String model){
        RunInstance runInstance
                = new RunInstance();
        runInstance.setModule(model);
        runInstance.start();
        return runInstance;
    }

    public void endInstance(RunInstance instance){
        instance.end();
        long modelElapsed = instance.getEndTime() - instance.getStartTime();
        timeCosts.compute(instance.getModule(), (s, aLong) -> {
            if(aLong == null){
                return modelElapsed;
            }
            return aLong + modelElapsed;
        });
    }

    public String output(){
        //聚合计算，每个model花了多少时间
        return JsonUtils.toJson(this.timeCosts);
    }
}
