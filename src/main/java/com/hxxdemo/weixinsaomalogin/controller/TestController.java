package com.hxxdemo.weixinsaomalogin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.IOException;

@Controller
public class TestController {
    //返回微信二维码，可供扫描登录
    @RequestMapping(value = "/index")
    public String weixin() throws IOException {
        return "index";
    }
    @RequestMapping(value = "/info")
    public String info() throws IOException {
        return "info";
    }

}
