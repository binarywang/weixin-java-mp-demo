#### 本Demo基于Spring Boot构建，实现微信公众号开发功能。

[![Build Status](https://travis-ci.org/Wechat-Group/weixin-java-mp-demo-springboot.svg?branch=master)](https://travis-ci.org/Wechat-Group/weixin-java-mp-demo-springboot)
-----------------------

## 使用步骤：
1. 配置:复制`/src/main/resources/application.yml.template` 生成application.yml文件，根据自己需要填写相关配置；	
1. 运行Java程序：`com.github.binarywang.demo.wechat.WechatMpDemoApplication`；
1. 打开shell或cmd，进入ngrok目录，运行 `ngrok -config ngrok.cfg -subdomain my-domain 8080` 如果运行失败，请更换my-domain为其它字符串，直至连接成功；
1. 配置微信公众号中的接口地址：http://my-domain.tunnel.qydev.com/wechat/portal （注意my-domain要跟上面的一致，需要符合微信官方的要求）；
1. 根据自己需要修改各个handler的实现，加入自己的业务逻辑。
	
