package com.webank.blockchain.data.stash;

import com.webank.blockchain.data.stash.fetch.BinlogFileDir;
import com.webank.blockchain.data.stash.fetch.BinlogFileDir;
import com.webank.blockchain.data.stash.fetch.HttpFileFetcher;
import com.webank.blockchain.data.stash.fetch.HttpFileScanner;
import org.junit.Test;

import java.io.File;

/**
 * @author aaronchu
 * @Description
 * @data 2020/10/29
 */
public class JavaTest {

    @Test
    public void test() throws Exception{
        BinlogFileDir dir = HttpFileScanner.scan("http://106.12.193.68:5299");
        for(int i=1;i<3 && dir.getSize() <= 0;i++){
            dir = HttpFileScanner.scan("http://106.12.193.68:5299");
        }
        System.out.println(dir.getSize());
    }
}
