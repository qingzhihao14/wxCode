package com.hxxdemo.weixinsaomalogin.controller;

import com.hxxdemo.weixinsaomalogin.util.SNSUserInfo;
import com.hxxdemo.weixinsaomalogin.util.WeiXinUtil;
import com.hxxdemo.weixinsaomalogin.util.WeixinOauth2Token;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/login")
public class loginController {
    //返回微信二维码，可供扫描登录
    @RequestMapping(value = "weixin")
    @ResponseBody
    public Map<String, Object> weixin(HttpServletRequest request) throws IOException {

        userMap = new HashMap<>();

        Map<String, Object> map = new HashMap<String, Object>();
        WeiXinUtil wxU = new WeiXinUtil();
        //http://www.shike.com.cn/index/WeiXinTest是手机用户扫码后会执行的后台方法
        //www.shike.com.cn此域名要与微信公共平台配置的一致
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
                "appid=wx8583413d15fa6494&" +
                "redirect_uri=http://xgz.ccdm.xyz/login/12345/WeiXinTest&uCode=12345" +
                "response_type=code&scope=snsapi_userinfo&state=123#wechat_redirect";
        //将url转换成短链接，提高扫码速度跟成功率
        String shorturl = wxU.shortURL(url, wxU.appid, wxU.appSecret);
        map.put("shorturl", shorturl);
        map.put("uCode", "12345");
        return map;
    }

    int type = 0;
    String type2 = "";


    private HashMap<String, Object> userMap;


    //判断用户是否扫码登录成功，以便于前台页面跳转
    @RequestMapping(value = "successDL")
    @ResponseBody
    public Map<String, Object> successDL(String uCode, HttpServletRequest req) {
        SNSUserInfo snsUserInfo = (SNSUserInfo) userMap.get(uCode);
        System.out.println("userMapSize = " + userMap.keySet().size());
        System.out.println("uCode = " + uCode);
        if (snsUserInfo == null) {
            type = 0;
        } else {
            type = 1;
            System.out.println("========================");
            System.out.println(snsUserInfo.getProvince());
            System.out.println(snsUserInfo.getOpenId());
            System.out.println(snsUserInfo.getNickname());
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put("type", type);
        objectHashMap.put("userInfo", snsUserInfo);

        return objectHashMap;
    }

    @RequestMapping(value = "type")
    public void type(int a) {
        type = a;
    }

    //微信获取用户信息
    @RequestMapping(value = "{uCode}/WeiXinTest")
    public ModelAndView WeiXinTest(@PathVariable String uCode, ModelMap map, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ModelAndView mav = new ModelAndView();
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        String code = request.getParameter("code");
        // 通过code获取access_token
        System.out.println(uCode);
        System.out.println(map.toString());

        WeixinOauth2Token oauth2Token = WeiXinUtil.getOauth2AccessToken(WeiXinUtil.appid, WeiXinUtil.appSecret, code);
        String accessToken = oauth2Token.getAccessToken();
        System.out.println(oauth2Token.getOpenId());
        System.out.println(oauth2Token.getExpiresIn());
        System.out.println(oauth2Token.getRefreshToken());
        String openId = oauth2Token.getOpenId();
        SNSUserInfo snsUserInfo = null;


        if (type == 0) {
            snsUserInfo = WeiXinUtil.getSNSUserInfo(accessToken, openId);
        }
        if (snsUserInfo != null) {

            System.out.println(snsUserInfo.getOpenId());
            System.out.println(snsUserInfo.getCity());
            System.out.println(snsUserInfo.getCountry());
            System.out.println(snsUserInfo.getNickname());
            System.out.println(snsUserInfo.getHeadImgUrl());
            System.out.println(snsUserInfo.getSex());
            System.out.println(snsUserInfo.getProvince());

            userMap.put(uCode, snsUserInfo);


            //证明获取到了微信用户基本信息
            String id = snsUserInfo.getOpenId();
            type2 = id;//可用作用户id，此值是微信用户的唯一识别
//		   查询库中是否有user
            /*逻辑写这*/

            //给手机端返回页面，成功，此处无法给pc端进行页面跳转
            mav.setViewName("info");
        } else {
            //给手机端返回页面，失败，此处无法给pc端进行页面跳转
            mav.setViewName("info2");
        }
        return mav;
    }

}
