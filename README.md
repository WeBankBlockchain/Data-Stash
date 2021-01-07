# WeBankBlockchain-Data-Stash

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

WeBankBlockchain-Data-Stash 是一个基于[FISCO-BCOS](https://github.com/FISCO-BCOS/FISCO-BCOS)平台的数据治理工具，为区块链底层平台FISCO BCOS提供节点数据扩容、备份、裁剪及快速同步的能力。用户可基于binlog协议同步区块链底层节点全量数据，实现冷热数据分离，支持断点续传、数据可信验证，并提供快速同步机制，助力FISCO BCOS轻松应对节点海量数据的运维场景。


**此版本只支持**[FISCO BCOS 2.7.1及以上](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/)。

## 关键特性

- 节点账本全量备份

- 多维度账本校验

- 备份数据可信存储

- 断点续传

- 易于使用

## 环境要求

在使用本组件前，请确认系统环境已安装相关依赖软件，清单如下：

| 依赖软件 | 说明 |备注|
| --- | --- | --- |
| FISCO-BCOS | >= 2.6.0 | 需要为节点开启binlog选项|
| MySQL | >= mysql-community-server[5.7] | |
| Nginx | >= nginx[1.17.3]| |
| Java | JDK[1.8] | |
| Git |  | |

## 文档
- [**中文**](https://data-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Data-Stash/index.html)
- [**快速安装**](https://data-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Data-Stash/quickstart.html)


## 贡献代码
欢迎参与本项目的社区建设：
- 如项目对您有帮助，欢迎点亮我们的小星星(点击项目上方Star按钮)。
- 欢迎提交代码(Pull requests)。
- [提问和提交BUG](https://github.com/WeBankBlockchain/WeBankBlockchain-Data-Stash/issues)。
- 如果发现代码存在安全漏洞，请在[这里](https://security.webank.com)上报。

## License
![license](http://img.shields.io/badge/license-Apache%20v2-blue.svg)

开源协议为[Apache License 2.0](http://www.apache.org/licenses/). 详情参考[LICENSE](../LICENSE)。
