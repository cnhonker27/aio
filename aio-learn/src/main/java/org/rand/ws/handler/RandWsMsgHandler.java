package org.rand.ws.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.rand.aio.core.protocol.ws.WsProtocol;
import org.rand.aio.core.protocol.ws.packet.WsChatMsgCommand;
import org.rand.aio.core.protocol.ws.packet.WsChatReceiveMsg;
import org.rand.aio.core.protocol.ws.packet.WsChatResponseMsg;
import org.rand.aio.session.AioServerSession;
import org.rand.aio.util.BeanKit;
import org.rand.ws.chat.WsGroup;
import org.rand.ws.chat.WsUser;
import org.rand.ws.msg.WsChatResponseData;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RandWsMsgHandler {
    private static String[] familyName = new String[] { "J-", "刘", "张", "李", "胡", "沈", "朱", "钱", "王", "伍", "赵", "孙", "吕", "马", "秦", "毛", "成", "梅", "黄", "郭", "杨", "季", "童", "习", "郑",
            "吴", "周", "蒋", "卫", "尤", "何", "魏", "章", "郎", " 唐", "汤", "苗", "孔", "鲁", "韦", "任", "袁", "贺", "狄朱" };

    private static String[] secondName = new String[] { "艺昕", "红薯", "明远", "天蓬", "三丰", "德华", "歌", "佳", "乐", "天", "燕子", "子牛", "海", "燕", "花", "娟", "冰冰", "丽娅", "大为", "无为", "渔民", "大赋",
            "明", "远平", "克弱", "亦菲", "靓颖", "富城", "岳", "先觉", "牛", "阿狗", "阿猫", "辰", "蝴蝶", "文化", "冲之", "悟空", "行者", "悟净", "悟能", "观", "音", "乐天", "耀扬", "伊健", "炅", "娜", "春花", "秋香", "春香",
            "大为", "如来", "佛祖", "科比", "罗斯", "詹姆屎", "科神", "科蜜", "库里", "卡特", "麦迪", "乔丹", "魔术师", "加索尔", "法码尔", "南斯", "伊哥", "杜兰特", "保罗", "杭州", "爱湘", "湘湘", "昕", "函", "鬼谷子", "膑", "荡",
            "子家", "德利优视", "五方会谈", "来电话了","轨迹","超"};
    private WsProtocol wsProtocol;

    private Map<String ,WsUser> users=new ConcurrentHashMap<>();

    private Map<String ,WsGroup> groups=new ConcurrentHashMap<>();

    private Gson gson = new GsonBuilder().create();

    public void handler(AioServerSession aioServerSession) {
        wsProtocol=(WsProtocol) aioServerSession.getAioProtocol();
        String json = new String("".getBytes(), StandardCharsets.UTF_8);
        WsChatReceiveMsg wsChatReceiveMsg = gson.fromJson(json, WsChatReceiveMsg.class);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        wsChatReceiveMsg.setCreateTime(format);
        json=new GsonBuilder().create().toJson(wsChatReceiveMsg);
        String chatMsgCommand = wsChatReceiveMsg.getWsChatMsgCommand();
        WsChatMsgCommand wsChatMsgCommand = WsChatMsgCommand.formatValue(chatMsgCommand);
        if (wsChatMsgCommand!=null){
            switch (wsChatMsgCommand) {
                case login:
                    login(json,aioServerSession);
                    break;
                case logout:
                    logout("",aioServerSession);
                    break;
                case join:
                    join(wsChatReceiveMsg);
                    break;
                case exit:
                    exit("","");
                    break;
                case add:
                    add("");
                    break;
                case del:
                    del("");
                    break;
                case send:
                    send(wsChatReceiveMsg);
                    break;
                case get:
                    getAny(wsChatReceiveMsg);
                    break;
            }
        }
    }

    private void getAny(WsChatReceiveMsg msg) {
        String senderId = msg.getSenderId();
        WsUser wsUser = this.users.get(senderId);
        WsUser wsUser1 = BeanKit.copyToTarget(wsUser, WsUser.class);
        WsGroup wsGroup = this.groups.get(msg.getTargetId());
        List<WsGroup> objects = new ArrayList<>();
        objects.add(wsGroup);
        wsUser1.setGroups(objects);
        wsChatResponse("获取成功","get",2000,wsUser1);
    }

    public boolean login(String json,AioServerSession aioServerContext){
        init(aioServerContext);
        WsUser wsUser = new WsUser();
        wsUser.setUserName(getUserName());
        wsUser.setUserId(aioServerContext.getId());
        wsUser.setAvatar("http://images.rednet.cn/articleimage/2013/01/23/1403536948.jpg");
        users.put(aioServerContext.getId(),wsUser);
        wsChatResponse("登录成功",WsChatMsgCommand.login.getCode(),2000,wsUser);
        return true;
    }

    public void logout(String userId,AioServerSession aioServerContext){

    }

    public void send(WsChatReceiveMsg wsChatReceiveMsg){
        WsChatResponseData<Object> wsChatResponseData = new WsChatResponseData<>();
        wsChatResponseData.setMsg("群组消息");
        wsChatResponseData.setStatus(2000);
        wsChatResponseData.setMsgType("talk");
        wsChatResponseData.setData(wsChatReceiveMsg);
       // AioKit.sendToGroup(wsChatReceiveMsg.getTargetId(),wsChatResponseData.toJson());
        wsChatResponse("发送成功","response",2000);
    }

    public void join(WsChatReceiveMsg wsChatReceiveMsg){
        //AioKit.bindGroup(wsChatReceiveMsg.getTargetId(),wsChatReceiveMsg.getSenderId());
        WsGroup wsGroup = groups.get(wsChatReceiveMsg.getTargetId());
        if(wsGroup==null){
            wsGroup = new WsGroup();
            wsGroup.setGroupId(wsChatReceiveMsg.getTargetId());
            wsGroup.setGroupName("Aio聊天室");
            groups.put(wsChatReceiveMsg.getTargetId(),wsGroup);
        }
        List<WsUser> users = wsGroup.getUsers();
        if(users==null){
            users=new ArrayList<>();
            wsGroup.setUsers(users);
        }
        WsUser wsUser = this.users.get(wsChatReceiveMsg.getSenderId());
        if(users.contains(wsUser)){
            wsChatResponse("加入失败，已存在",WsChatMsgCommand.join.getCode(),5000,wsGroup);
        }else{
            users.add(wsUser);
            wsChatResponse("加入成功",WsChatMsgCommand.join.getCode(),5000,wsGroup);
        }
    }

    public void exit(String groupId,String userId){
        //AioKit.unbindGroup(groupId,userId);
    }

    public void add(String userId){

    }

    public void del(String userId){

    }

    public void defaultSend(){

    }

    void init(AioServerSession aioServerContext){
        // 加入会话
     /*   AioKit.addContext(aioServerContext);*/
    }

    private String getUserName(){
        Random random = new Random();
        int start = random.nextInt(familyName.length - 1);
        int end = random.nextInt(secondName.length - 1);
        return familyName[start] + secondName[end];
    }

    private void responseJson(String context,WsChatMsgCommand wsChatMsgCommand){
        WsChatResponseMsg wsChatResponseMsg = new WsChatResponseMsg();
        wsChatResponseMsg.setContext(context);
        wsChatResponseMsg.setSenderId("");
        wsChatResponseMsg.setNick(WsChatMsgCommand.login.getCode().equals(wsChatMsgCommand.getCode())?getUserName():"");
        wsChatResponseMsg.setWsChatMsgCommand(wsChatMsgCommand.getCode());
/*        wsProtocol.setBody(wsChatResponseMsg.toJson().getBytes());*/
    }
    private void wsChatResponse(String msg,String msgType,Integer status){
        wsChatResponse(msg,msgType,status,null);
    }
    private void wsChatResponse(String msg,String msgType,Integer status,Object data){
        WsChatResponseData<Object> wsChatResponseData = new WsChatResponseData<>();
        wsChatResponseData.setMsg(msg);
        wsChatResponseData.setStatus(status);
        wsChatResponseData.setMsgType(msgType);
        wsChatResponseData.setData(data);
        wsChatResponse(wsChatResponseData);
    }

    private void wsChatResponse(WsChatResponseData wsChatResponseData){
        /*wsProtocol.setBody(wsChatResponseData.toJson().getBytes());*/
    }
}
