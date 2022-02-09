<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <base href="<%=basePath%>">

    <title>微信扫码登录</title>

</head>

<body>
<div  id="ma">
<div  id="info">
</div>

<script src="//apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
<script type="application/javascript">

    var uCode ;
    var that  = this;
    $(function(){
        $.post("<%=basePath%>login/weixin",function(data){

            var ma = data.shorturl;
            this.uCode = data.uCode;

            var srcMa = "http://qr.topscan.com/api.php?text="+ma;
            var imgg = "<img class='h-img' alt='' src="+srcMa+">";
            //将生成的二维码放到div里
            $("#ma").empty().append(imgg);
            panduan(data.uCode);
        });
        //置初始值
        $.post("<%=basePath%>index/type",{"a":0});

    })
    //微信扫码是否成功的判断
    var test = 0;
    function panduan(code){
        $.post("<%=basePath%>login/successDL", {uCode:code}, function(data){
            debugger
            $("#info").empty().append("<span >" + "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx8583413d15fa6494&redirect_uri=http://xgz.ccdm.xyz/login/12345/WeiXinTest&uCode=12345response_type=code&scope=snsapi_userinfo&state=123#wechat_redirect")" + "<span >";
            window.close();
            if(data.type==1){

                var info = "<span >" + data.userInfo + "<span >";
                //将生成的二维码放到div里
                $("#info").empty().append("");
                window.close();
            }else if(data.type==0 && test!=300){
                //没扫码成功，将test+1,达到三百次（150秒），就不扫了。
                test = test+1;
                panduan(code);
            }else if(test==300){
                alert("登录码已失效，请刷新页面更新验证码！");
                $.post("<%=basePath%>index/type",{"a":5});
            }
        });
    }
</script>
</body>
</html>
