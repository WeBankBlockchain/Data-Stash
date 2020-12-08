-- ----------------------------------------
-- Table structure for _contract_data_
-- ----------------------------------------
DROP TABLE IF EXISTS `_contract_data_`;
CREATE TABLE `_contract_data_` (
  `entry_id` bigint(20) unsigned NOT NULL,
  `entry_status` int(11) DEFAULT NULL,
  `block_num` bigint(20) DEFAULT NULL,
  `balance` text,
  `nonce` text,
  `code` text,
  `code_hash` text,
  `alive` text,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------------------
-- Table structure for _sys_block_2_nonces_
-- ----------------------------------------
DROP TABLE IF EXISTS `_sys_block_2_nonces_`;
CREATE TABLE `_sys_block_2_nonces_` (
  `entry_id` bigint(20) unsigned NOT NULL,
  `entry_status` int(11) DEFAULT NULL,
  `block_num` bigint(20) DEFAULT NULL,
  `number` text,
  `value` text,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------------------
-- Table structure for _sys_cns_
-- ----------------------------------------
DROP TABLE IF EXISTS `_sys_cns_`;
CREATE TABLE `_sys_cns_` (
  `entry_id` bigint(20) unsigned NOT NULL,
  `entry_status` int(11) DEFAULT NULL,
  `block_num` bigint(20) DEFAULT NULL,
  `version` text,
  `address` text,
  `abi` text,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------------------
-- Table structure for _sys_config_
-- ----------------------------------------
DROP TABLE IF EXISTS `_sys_config_`;
CREATE TABLE `_sys_config_` (
  `entry_id` bigint(20) unsigned NOT NULL,
  `entry_status` int(11) DEFAULT NULL,
  `block_num` bigint(20) DEFAULT NULL,
  `key` text,
  `value` text,
  `enable_num` text,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------------------
-- Table structure for _sys_consensus_
-- ----------------------------------------
DROP TABLE IF EXISTS `_sys_consensus_`;
CREATE TABLE `_sys_consensus_` (
  `entry_id` bigint(20) unsigned NOT NULL,
  `entry_status` int(11) DEFAULT NULL,
  `block_num` bigint(20) DEFAULT NULL,
  `name` text,
  `type` text,
  `node_id` text,
  `enable_num` text,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------------------
-- Table structure for _sys_current_state_
-- ----------------------------------------
DROP TABLE IF EXISTS `_sys_current_state_`;
CREATE TABLE `_sys_current_state_` (
  `entry_id` bigint(20) unsigned NOT NULL,
  `entry_status` int(11) DEFAULT NULL,
  `block_num` bigint(20) DEFAULT NULL,
  `key` text,
  `value` text,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------------------
-- Table structure for _sys_hash_2_block_
-- ----------------------------------------
DROP TABLE IF EXISTS `_sys_hash_2_block_`;
CREATE TABLE `_sys_hash_2_block_` (
  `entry_id` bigint(20) unsigned NOT NULL,
  `entry_status` int(11) DEFAULT NULL,
  `block_num` bigint(20) DEFAULT NULL,
  `hash` text,
  `value` text,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------------------
-- Table structure for _sys_table_access_
-- ----------------------------------------
DROP TABLE IF EXISTS `_sys_table_access_`;
CREATE TABLE `_sys_table_access_` (
  `entry_id` bigint(20) unsigned NOT NULL,
  `entry_status` int(11) DEFAULT NULL,
  `block_num` bigint(20) DEFAULT NULL,
  `address` text,
  `enable_num` text,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------------------
-- Table structure for _sys_tables_
-- ----------------------------------------
DROP TABLE IF EXISTS `_sys_tables_`;
CREATE TABLE `_sys_tables_` (
  `entry_id` bigint(20) unsigned NOT NULL,
  `entry_status` int(11) DEFAULT NULL,
  `block_num` bigint(20) DEFAULT NULL,
  `table_name` text,
  `key_field` text,
  `value_field` text,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------------------
-- Table structure for _sys_tx_hash_2_block_
-- ----------------------------------------
DROP TABLE IF EXISTS `_sys_tx_hash_2_block_`;
CREATE TABLE `_sys_tx_hash_2_block_` (
  `entry_id` bigint(20) unsigned NOT NULL,
  `entry_status` int(11) DEFAULT NULL,
  `block_num` bigint(20) DEFAULT NULL,
  `hash` text,
  `value` text,
  `index` text,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------------------
-- Table structure for _user_table_
-- ----------------------------------------
DROP TABLE IF EXISTS `_user_table_`;
CREATE TABLE `_user_table_` (
  `entry_id` bigint(20) unsigned NOT NULL,
  `entry_status` int(11) DEFAULT NULL,
  `block_num` bigint(20) DEFAULT NULL,
  `user_info` text,
  PRIMARY KEY (`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------------------
-- Table structure for test
-- ----------------------------------------
CREATE TABLE `test` (
  `pkid` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `id` bigint(11) DEFAULT NULL,
  `num` bigint(11) DEFAULT NULL,
  `hash` varchar(255) DEFAULT NULL,
  `content` varchar(11) DEFAULT NULL,
  `testcontent` mediumtext,
  PRIMARY KEY (`pkid`),
  KEY `_num_` (`num`),
  KEY `id_num` (`id`,`num`),
  KEY `_id_` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000001 DEFAULT CHARSET=latin1;
