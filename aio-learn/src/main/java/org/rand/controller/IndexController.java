package org.rand.controller;

import org.rand.aio.common.MsgResult;
import org.rand.aio.core.annotation.mvc.*;
import org.rand.aio.core.decoder.AioRequest;
import org.rand.aio.util.StringKit;
import org.rand.service.TestService;
import org.rand.util.UserKit;

import java.util.HashMap;

@AioController
public class IndexController {

    @AioValue("db.password")
    String a;

    @AioAutowired
    private TestService testService;

    @AioRequestMapping("index")
    public String index(){
        return "index";
    }

    @AioRequestMapping("index1")
    @AioResponseBody
    public String index1(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "index1";
    }

    @AioRequestMapping("login")
    public String login(){
        //testService.test();
        System.out.println(a);
        return "login/login";
    }

    @AioRequestMapping("doLogin")
    @AioResponseBody
    public  MsgResult<Object> doLogin(AioRequest aioRequest){
        HashMap<String, String> objectObjectHashMap = new HashMap<>();
        String username = aioRequest.getParameter("username");
        String token = UserKit.getToken(username);
        MsgResult<Object> objectMsgResult = new MsgResult<>();
        if(StringKit.isNotEmpty(token)){
            objectMsgResult.setCode(5000);
            objectMsgResult.setMsg("该用户已被占用");
            return objectMsgResult;
        }
        String uuid = StringKit.getUUID();
        objectObjectHashMap.put("token", uuid);
        UserKit.setToken(username,uuid);
        objectMsgResult.setData(objectObjectHashMap);
        return objectMsgResult;
    }

    @AioRequestMapping("websocket")
    public String websocket(){
        return "websocket/wschat";
    }
}
