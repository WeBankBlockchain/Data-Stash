# WeBankBlockchain-Data-Stash

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

WeBankBlockchain-Data-Stash 是一个基于[FISCO-BCOS](https://github.com/FISCO-BCOS/FISCO-BCOS)平台的数据治理工具。

如何以有限的磁盘空间来存储无限膨胀的账本数据，是区块链数据治理领域的关键问题之一。数据仓库组件Data-Stash用于解决账本和磁盘之间的矛盾。通过为节点生成全量备份作为后备保障，使节点便可以放心地进行数据裁剪，而不必担心数据丢失。Data-Stash还可以用于节点快速同步、监管审计等场景。

## 关键特性

- 节点账本全量备份

- 多维度账本校验

- 备份数据可信存储

- 断点续传

- 易于使用


## 环境要求


在使用本组件前，请确认系统环境已安装相关依赖软件，清单如下：

| 依赖软件   | 说明                                                         | 备注 |
| ---------- | ------------------------------------------------------------ | ---- |
| FISCO-BCOS       | >= 2.0 |      |
| Java       | \>= JDK[1.8]                                                 |      |
| Git        | 下载安装包需要使用Git                                          |      |


## 文档

- [**中文**](https://data-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Data-Stash/index.html)
- [**快速安装**](https://data-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Data-Stash/quickstart.html)


## 贡献代码
欢迎参与本项目的社区建设：
- 如项目对您有帮助，欢迎点亮我们的小星星(点击项目左上方Star按钮)。
- 欢迎提交代码(Pull requests)。
- [提问和提交BUG](https://github.com/WeBankBlockchain/WeBankBlockchain-Data-Stash/issues)。
- 如果发现代码存在安全漏洞，请在[这里](https://security.webank.com)上报。

## 加入我们的社区

FISCO BCOS开源社区是国内活跃的开源社区，社区长期为机构和个人开发者提供各类支持与帮助。已有来自各行业的数千名技术爱好者在研究和使用FISCO BCOS。如您对FISCO BCOS开源技术及应用感兴趣，欢迎加入社区获得更多支持与帮助。


![](https://media.githubusercontent.com/media/FISCO-BCOS/LargeFiles/master/images/QR_image.png)


## License
![license](http://img.shields.io/badge/license-Apache%20v2-blue.svg)

开源协议为[Apache License 2.0](http://www.apache.org/licenses/). 详情参考[LICENSE](../LICENSE)。
