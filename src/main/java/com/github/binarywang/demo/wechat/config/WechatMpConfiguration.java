package com.github.binarywang.demo.wechat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.binarywang.demo.wechat.handler.AbstractHandler;
import com.github.binarywang.demo.wechat.handler.KfSessionHandler;
import com.github.binarywang.demo.wechat.handler.LocationHandler;
import com.github.binarywang.demo.wechat.handler.LogHandler;
import com.github.binarywang.demo.wechat.handler.MenuHandler;
import com.github.binarywang.demo.wechat.handler.MsgHandler;
import com.github.binarywang.demo.wechat.handler.NullHandler;
import com.github.binarywang.demo.wechat.handler.StoreCheckNotifyHandler;
import com.github.binarywang.demo.wechat.handler.SubscribeHandler;
import com.github.binarywang.demo.wechat.handler.UnsubscribeHandler;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;

/**
 * wechat mp configuration
 *
 * @author Binary Wang
 */
@Configuration
@ConditionalOnClass(WxMpService.class)
@EnableConfigurationProperties(WechatMpProperties.class)
public class WechatMpConfiguration {
    @Autowired
    private WechatMpProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public WxMpConfigStorage configStorage() {
        WxMpInMemoryConfigStorage configStorage = new WxMpInMemoryConfigStorage();
        configStorage.setAppId(this.properties.getAppId());
        configStorage.setSecret(this.properties.getSecret());
        configStorage.setToken(this.properties.getToken());
        configStorage.setAesKey(this.properties.getAesKey());
        configStorage.setPartnerId(this.properties.getPartnerId());
        configStorage.setPartnerKey(this.properties.getPartnerKey());
        return configStorage;
    }

    @Bean
    @ConditionalOnMissingBean
    public WxMpService wxMpService(WxMpConfigStorage configStorage) {
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(configStorage);
        return wxMpService;
    }

    @Bean 
    public WxMpMessageRouter router(WxMpService wxMpService) {
        final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);

        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(this.logHandler).next();

        // 接收客服会话管理事件
        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_EVENT)
            .event(WxConsts.EVT_KF_CREATE_SESSION)
            .handler(this.kfSessionHandler).end();
        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_EVENT)
            .event(WxConsts.EVT_KF_CLOSE_SESSION).handler(this.kfSessionHandler)
            .end();
        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_EVENT)
            .event(WxConsts.EVT_KF_SWITCH_SESSION)
            .handler(this.kfSessionHandler).end();
        
        // 门店审核事件
        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_EVENT)
            .event(WxConsts.EVT_POI_CHECK_NOTIFY)
            .handler(this.storeCheckNotifyHandler).end();

        // 自定义菜单事件
        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_EVENT)
            .event(WxConsts.BUTTON_CLICK).handler(this.getMenuHandler()).end();

        // 点击菜单连接事件
        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_EVENT)
            .event(WxConsts.BUTTON_VIEW).handler(this.nullHandler).end();

        // 关注事件
        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_EVENT)
            .event(WxConsts.EVT_SUBSCRIBE).handler(this.getSubscribeHandler())
            .end();

        // 取消关注事件
        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_EVENT)
            .event(WxConsts.EVT_UNSUBSCRIBE)
            .handler(this.getUnsubscribeHandler()).end();

        // 上报地理位置事件
        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_EVENT)
            .event(WxConsts.EVT_LOCATION).handler(this.getLocationHandler())
            .end();

        // 接收地理位置消息
        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_LOCATION)
            .handler(this.getLocationHandler()).end();

        // 扫码事件
        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_EVENT)
            .event(WxConsts.EVT_SCAN).handler(this.getScanHandler()).end();

        // 默认
        newRouter.rule().async(false).handler(this.getMsgHandler()).end();

        return newRouter;
    }

    @Autowired
    private LocationHandler locationHandler;

    @Autowired
    private MenuHandler menuHandler;

    @Autowired
    private MsgHandler msgHandler;

    @Autowired
    protected LogHandler logHandler;

    @Autowired
    protected NullHandler nullHandler;

    @Autowired
    protected KfSessionHandler kfSessionHandler;

    @Autowired
    protected StoreCheckNotifyHandler storeCheckNotifyHandler;

    @Autowired
    private UnsubscribeHandler unsubscribeHandler;

    @Autowired
    private SubscribeHandler subscribeHandler;

    protected MenuHandler getMenuHandler() {
        return this.menuHandler;
    }

    protected SubscribeHandler getSubscribeHandler() {
        return this.subscribeHandler;
    }

    protected UnsubscribeHandler getUnsubscribeHandler() {
        return this.unsubscribeHandler;
    }

    protected AbstractHandler getLocationHandler() {
        return this.locationHandler;
    }

    protected MsgHandler getMsgHandler() {
        return this.msgHandler;
    }

    protected AbstractHandler getScanHandler() {
        return null;
    }

}
