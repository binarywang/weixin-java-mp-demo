package com.github.binarywang.demo.wx.mp.controller;

import io.restassured.http.ContentType;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * 菜单测试.
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 * @date 2020-08-19
 */
public class WxMenuControllerTest extends BaseControllerTest {

    @Test
    public void testMenuCreate() {
        given()
            .when()
            .body(new WxMenu())
            .contentType(ContentType.JSON)
            .post("/wx/menu/wxappid/create")
            .then()
            .log().everything();
    }
}
