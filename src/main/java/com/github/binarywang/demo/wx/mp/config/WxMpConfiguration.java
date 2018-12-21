package com.github.binarywang.demo.wx.mp.config;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.binarywang.demo.wx.mp.handler.KfSessionHandler;
import com.github.binarywang.demo.wx.mp.handler.LocationHandler;
import com.github.binarywang.demo.wx.mp.handler.LogHandler;
import com.github.binarywang.demo.wx.mp.handler.MenuHandler;
import com.github.binarywang.demo.wx.mp.handler.MsgHandler;
import com.github.binarywang.demo.wx.mp.handler.NullHandler;
import com.github.binarywang.demo.wx.mp.handler.ScanHandler;
import com.github.binarywang.demo.wx.mp.handler.StoreCheckNotifyHandler;
import com.github.binarywang.demo.wx.mp.handler.SubscribeHandler;
import com.github.binarywang.demo.wx.mp.handler.UnsubscribeHandler;
import com.google.common.collect.Maps;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.constant.WxMpEventConstants;

import static me.chanjar.weixin.common.api.WxConsts.*;

/**
 * wechat mp configuration
 *
 * @author Binary Wang(https://github.com/binarywang)
 */
@Configuration
@EnableConfigurationProperties(WxMpProperties.class)
public class WxMpConfiguration {
    private LogHandler logHandler;
    private NullHandler nullHandler;
    private KfSessionHandler kfSessionHandler;
    private StoreCheckNotifyHandler storeCheckNotifyHandler;
    private LocationHandler locationHandler;
    private MenuHandler menuHandler;
    private MsgHandler msgHandler;
    private UnsubscribeHandler unsubscribeHandler;
    private SubscribeHandler subscribeHandler;
    private ScanHandler scanHandler;

    private WxMpProperties properties;

    private static Map<String, WxMpMessageRouter> routers = Maps.newHashMap();
    private static Map<String, WxMpService> mpServices = Maps.newHashMap();

    @Autowired
    public WxMpConfiguration(LogHandler logHandler, NullHandler nullHandler, KfSessionHandler kfSessionHandler,
                             StoreCheckNotifyHandler storeCheckNotifyHandler, LocationHandler locationHandler,
                             MenuHandler menuHandler, MsgHandler msgHandler, UnsubscribeHandler unsubscribeHandler,
                             SubscribeHandler subscribeHandler, ScanHandler scanHandler, WxMpProperties properties) {
        this.logHandler = logHandler;
        this.nullHandler = nullHandler;
        this.kfSessionHandler = kfSessionHandler;
        this.storeCheckNotifyHandler = storeCheckNotifyHandler;
        this.locationHandler = locationHandler;
        this.menuHandler = menuHandler;
        this.msgHandler = msgHandler;
        this.unsubscribeHandler = unsubscribeHandler;
        this.subscribeHandler = subscribeHandler;
        this.scanHandler = scanHandler;
        this.properties = properties;
    }

    public static Map<String, WxMpMessageRouter> getRouters() {
        return routers;
    }

    public static Map<String, WxMpService> getMpServices() {
        return mpServices;
    }

    @Bean
    public Object services() {
        // 代码里getConfigs()处报错的同学，请注意仔细阅读项目说明，你的IDE需要引入lombok插件！！！！
        final List<WxMpProperties.MpConfig> configs = this.properties.getConfigs();
        if (configs == null) {
            throw new RuntimeException("大哥，拜托先看下项目首页的说明（readme文件），添加下相关配置，注意别配错了！");
        }

        mpServices = configs.stream().map(a -> {
            WxMpInMemoryConfigStorage configStorage = new WxMpInMemoryConfigStorage();
            configStorage.setAppId(a.getAppId());
            configStorage.setSecret(a.getSecret());
            configStorage.setToken(a.getToken());
            configStorage.setAesKey(a.getAesKey());

            WxMpService service = new WxMpServiceImpl();
            service.setWxMpConfigStorage(configStorage);
            routers.put(a.getAppId(), this.newRouter(service));
            return service;
        }).collect(Collectors.toMap(s -> s.getWxMpConfigStorage().getAppId(), a -> a, (o, n) -> o));

        return Boolean.TRUE;
    }

    private WxMpMessageRouter newRouter(WxMpService wxMpService) {
        final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);

        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(this.logHandler).next();

        // 接收客服会话管理事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
            .event(WxMpEventConstants.CustomerService.KF_CREATE_SESSION)
            .handler(this.kfSessionHandler).end();
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
            .event(WxMpEventConstants.CustomerService.KF_CLOSE_SESSION)
            .handler(this.kfSessionHandler)
            .end();
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
            .event(WxMpEventConstants.CustomerService.KF_SWITCH_SESSION)
            .handler(this.kfSessionHandler).end();

        // 门店审核事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
            .event(WxMpEventConstants.POI_CHECK_NOTIFY)
            .handler(this.storeCheckNotifyHandler).end();

        // 自定义菜单事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
            .event(MenuButtonType.CLICK).handler(this.menuHandler).end();

        // 点击菜单连接事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
            .event(MenuButtonType.VIEW).handler(this.nullHandler).end();

        // 关注事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
            .event(EventType.SUBSCRIBE).handler(this.subscribeHandler)
            .end();

        // 取消关注事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
            .event(EventType.UNSUBSCRIBE)
            .handler(this.unsubscribeHandler).end();

        // 上报地理位置事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
            .event(EventType.LOCATION).handler(this.locationHandler)
            .end();

        // 接收地理位置消息
        newRouter.rule().async(false).msgType(XmlMsgType.LOCATION)
            .handler(this.locationHandler).end();

        // 扫码事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
            .event(EventType.SCAN).handler(this.scanHandler).end();

        // 默认
        newRouter.rule().async(false).handler(this.msgHandler).end();

        return newRouter;
    }

}
