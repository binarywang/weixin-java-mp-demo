[![码云Gitee](https://gitee.com/binary/weixin-java-mp-demo-springboot/badge/star.svg?theme=blue)](https://gitee.com/binary/weixin-java-mp-demo-springboot)
[![Github](http://github-svg-buttons.herokuapp.com/star.svg?user=binarywang&repo=weixin-java-mp-demo-springboot&style=flat&background=1081C1)](https://github.com/binarywang/weixin-java-mp-demo-springboot)
[![Build Status](https://travis-ci.org/binarywang/weixin-java-mp-demo-springboot.svg?branch=master)](https://travis-ci.org/binarywang/weixin-java-mp-demo-springboot)
-----------------------

### 本Demo基于Spring Boot构建，实现微信公众号后端开发功能。
### 本项目为weixin-java-tools的Demo演示程序，更多Demo请[查阅此处](https://github.com/Wechat-Group/weixin-java-tools/blob/master/demo.md)。
#### 如有问题请[【在此提问】](https://github.com/binarywang/weixin-java-mp-demo-springboot/issues)，谢谢配合。

## 使用步骤：
1. 请注意，本demo为简化代码编译时加入了lombok支持，如果不了解lombok的话，请先学习下相关知识，比如可以阅读[此文章](https://mp.weixin.qq.com/s/cUc-bUcprycADfNepnSwZQ)；
1. 配置：复制 `/src/main/resources/application.yml.template` 或修改其扩展名生成 `application.yml` 文件，根据自己需要填写相关配置（需要注意的是：yml文件内的属性冒号后面的文字之前需要加空格，可参考已有配置，否则属性会设置不成功）；
2. 主要配置说明如下：
```
wx:
  mp:
    configs:
      - appId: 1111 （一个公众号的appid）
        secret: 1111（公众号的appsecret）
        token: 111 （接口配置里的Token值）
        aesKey: 111 （接口配置里的EncodingAESKey值）
      - appId: 2222 （另一个公众号的appid，以下同上）
        secret: 1111
        token: 111
        aesKey: 111
```
3. 运行Java程序：`WxMpDemoApplication`；
4. （可选，如果自己有公网域名，可自行配置）打开shell或cmd，进入ngrok目录，运行 `ngrok -config ngrok.cfg -subdomain my-domain 8080` 如果运行失败，请更换my-domain为其它字符串，直至连接成功；
5. 配置微信公众号中的接口地址：http://my-domain.tunnel.qydev.com/wx/portal/xxxxx （注意xxxxx为对应公众号的appid值，my-domain要跟上面的一致，需要符合微信官方的要求）；
6. 根据自己需要修改各个handler的实现，加入自己的业务逻辑。
	
