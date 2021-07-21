package com.webank.blockchain.data.stash.query.model;

import lombok.Data;

import java.util.List;

/**
 * @author aaronchu
 * @Description
 * @data 2021/03/26
 */
@Data
public class SelectRequest {

    private String blockHash;
    private Integer num;
    private String table;
    private String key;
    private List<List<String>>	condition;

}
