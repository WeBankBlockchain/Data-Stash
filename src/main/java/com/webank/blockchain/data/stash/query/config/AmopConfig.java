package com.webank.blockchain.data.stash.query.config;

import com.webank.blockchain.data.stash.query.service.impl.AmopSelectService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.amop.Amop;
import org.fisco.bcos.sdk.amop.AmopCallback;
import org.fisco.bcos.sdk.amop.AmopMsgOut;
import org.fisco.bcos.sdk.amop.topic.AmopMsgIn;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.config.model.ConfigProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author aaronchu
 * @Description
 * @data 2021/03/31
 */

@Configuration
@Data
@Slf4j
public class AmopConfig {

    @Value("${data.query.enable}")
    private boolean enable;
    @Value("${data.query.certPath}")
    private String certPath;
    @Value("${data.query.groupId}")
    private int groupId;
    @Value("${data.query.nodeStr}")
    private String nodeStr;
    @Value("${data.query.topic}")
    private String topic;

    private BcosSDK bcosSDK;

    private Amop amop;

    @PostConstruct
    private void initSDK() throws ConfigException {
        if (!enable){
            return;
        }
        ConfigProperty configProperty = new ConfigProperty();
        setPeers(configProperty);
        setCertPath(configProperty);
        ConfigOption option = new ConfigOption(configProperty);
        new Thread(() -> {
            while(true) {
                try {
                    bcosSDK = new BcosSDK(option);
                    initAmop();
                    log.info("bcosSDK connect success ... ...");
                    break;
                } catch (Exception e) {
                    log.warn("bcosSDK connect failed, try again after 5 s ... ...");
                }
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException ignored) {
                }
            }
        });
    }

    public void setPeers(ConfigProperty configProperty) {
        String[] nodes = StringUtils.split(this.nodeStr, ";");
        List<String> peers = Arrays.asList(nodes);
        Map<String, Object> network = new HashMap<>();
        network.put("peers", peers);
        configProperty.setNetwork(network);
    }

    public void setCertPath(ConfigProperty configProperty) {
        Map<String, Object> cryptoMaterial = new HashMap<>();
        cryptoMaterial.put("certPath", certPath);
        configProperty.setCryptoMaterial(cryptoMaterial);
    }

    @Autowired
    private AmopSelectService selectService;

    private void initAmop() {
        amop = bcosSDK.getAmop();
        AmopCallback cb = new AmopCallback() {
            @Override
            public byte[] receiveAmopMsg(AmopMsgIn msg) {
                AmopMsgOut msgOut;
                try {
                    msgOut = selectService.select(msg);
                    return msgOut.getContent();
                } catch (Exception e) {
                    log.error("selectService.select failed, reason :" ,e);
                }
                return null;
            }
        };
        amop.subscribeTopic(this.topic, cb);
    }


}
