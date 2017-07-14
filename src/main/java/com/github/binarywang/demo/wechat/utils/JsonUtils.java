package com.github.binarywang.demo.wechat.utils;

import me.chanjar.weixin.mp.util.json.WxMpGsonBuilder;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
public class JsonUtils {
    public static String toJson(Object obj) {
        return WxMpGsonBuilder.create().toJson(obj);
    }
}
