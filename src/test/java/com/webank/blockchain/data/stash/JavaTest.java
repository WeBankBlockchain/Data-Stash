package com.webank.blockchain.data.stash;

import com.webank.blockchain.data.stash.fetch.BinlogFileDir;
import com.webank.blockchain.data.stash.fetch.BinlogFileDir;
import com.webank.blockchain.data.stash.fetch.HttpFileFetcher;
import com.webank.blockchain.data.stash.fetch.HttpFileScanner;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.model.ConfigProperty;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author aaronchu
 * @Description
 * @data 2020/10/29
 */
public class JavaTest {

    @Test
    public void test() throws Exception{
//        BinlogFileDir dir = HttpFileScanner.scan("http://106.12.193.68:5299");
//        for(int i=1;i<3 && dir.getSize() <= 0;i++){
//            dir = HttpFileScanner.scan("http://106.12.193.68:5299");
//        }
//        System.out.println(dir.getSize());

        ConfigProperty configProperty = new ConfigProperty();
        setPeers(configProperty);
        setCertPath(configProperty);
        ConfigOption option = new ConfigOption(configProperty);
        BcosSDK bcosSDK = new BcosSDK(option);
//        log.info("bcosSDK connect success ... ...");
    }


    public void setPeers(ConfigProperty configProperty) {
        String[] nodes = StringUtils.split("127.0.0.1:20200", ";");
        List<String> peers = Arrays.asList(nodes);
        Map<String, Object> network = new HashMap<>();
        network.put("peers", peers);
        configProperty.setNetwork(network);
    }

    public void setCertPath(ConfigProperty configProperty) {
        Map<String, Object> cryptoMaterial = new HashMap<>();
        cryptoMaterial.put("certPath", "./config");
        configProperty.setCryptoMaterial(cryptoMaterial);
    }
}
