package com.webank.blockchain.data.stash.query.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.blockchain.data.stash.query.handler.SelectHandler;
import com.webank.blockchain.data.stash.query.model.*;
import com.webank.blockchain.data.stash.query.service.SelectService;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.amop.AmopMsgOut;
import org.fisco.bcos.sdk.amop.topic.AmopMsgIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author aaronchu
 * @Description
 * @data 2021/03/26
 */
@Component
@Slf4j
public class AmopSelectService implements SelectService<AmopMsgIn, AmopMsgOut> {

    @Autowired
    private SelectHandler selectHandler;

    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public AmopMsgOut select(AmopMsgIn msgIn) throws Exception {
        int resultCode = 0;

        SelectResponse2 resultData = null;
        log.trace("Process receives the request: {}", msgIn.getContent());
        Header header = objectMapper.readValue(msgIn.getContent(), Header.class);
        if (header.getOp() == null) {
            throw new Exception("Failed to parse header.op:" + msgIn.getContent());
        }

        if (!header.getOp().equals("select2")){
            return new AmopMsgOut();
        }
        Request<SelectRequest> request = objectMapper.readValue(msgIn.getContent(),
                new TypeReference<Request<SelectRequest>>() {
                });
        SelectRequest selectRequest = request.getParams();
        try {
            resultData = selectHandler.select(selectRequest);
        } catch (Exception e) {
            resultCode = -1;
            log.error("Process request error", e);
        }

        Response response = new Response();
        response.setCode(resultCode);
        response.setResult(resultData);

        String out = objectMapper.writeValueAsString(response);
        AmopMsgOut msgOut = new AmopMsgOut();
        msgOut.setContent(out.getBytes());
        msgOut.setTopic(msgIn.getTopic());
        msgOut.setType(msgIn.getTopicType());
        msgOut.setTimeout(6000);
        log.debug("Send response: {}", response.getResult());
        return msgOut;
    }
}
