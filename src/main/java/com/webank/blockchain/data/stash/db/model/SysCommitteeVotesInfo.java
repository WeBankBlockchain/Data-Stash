package com.webank.blockchain.data.stash.db.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author aaronchu
 * @Description
 * @data 2021/01/07
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SysCommitteeVotesInfo extends BaseInfo {

    private String key;
    private String value;
    private String origin;
    private String blockLimit;

}
