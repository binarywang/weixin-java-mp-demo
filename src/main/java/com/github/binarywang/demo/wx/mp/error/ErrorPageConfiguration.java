package com.github.binarywang.demo.wx.mp.error;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * 配置错误状态与对应访问路径
 * Created by Binary Wang on 2018/8/25.
 * </pre>
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Component
public class ErrorPageConfiguration implements ErrorPageRegistrar {
    @Override
    public void registerErrorPages(ErrorPageRegistry errorPageRegistry) {
        errorPageRegistry.addErrorPages(
            new ErrorPage(HttpStatus.NOT_FOUND, "/error/404"),
            new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500")
        );
    }

}
