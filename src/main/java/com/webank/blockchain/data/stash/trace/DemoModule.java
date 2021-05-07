package com.webank.blockchain.data.stash.trace;

import java.util.concurrent.*;

public class DemoModule {

    public static void main(String[] args) throws Exception{
        PerfTracer perfTracer = PerfTracer.SINGLETON;

        ExecutorService executors
                = Executors.newFixedThreadPool(10);
        CompletableFuture f = CompletableFuture.runAsync(() -> {
                    RunInstance i1 = perfTracer.startInstance("t1");
                    try{
                        t1();
                    }
                    catch (Exception ex){}
                    finally {
                        perfTracer.endInstance(i1);
                    }
                },

                executors);


        CompletableFuture.allOf(f).get();
        String s = perfTracer.output();
        System.out.println(s);
    }

    private static void t1() throws Exception{
        Thread.sleep(2000);

        RunInstance ins = PerfTracer.SINGLETON.startInstance("t2");
        Thread.sleep(1000);
        PerfTracer.SINGLETON.endInstance(ins);
    }

}
