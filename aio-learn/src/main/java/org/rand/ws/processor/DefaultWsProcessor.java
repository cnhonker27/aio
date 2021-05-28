package org.rand.ws.processor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.rand.aio.core.decoder.AioPacket;
import org.rand.aio.core.decoder.AioRequest;
import org.rand.aio.core.handler.call.ReadWriteContext;
import org.rand.aio.core.protocol.ws.annotation.AioWsProcessor;
import org.rand.aio.core.protocol.ws.packet.WsChatMsgCommand;
import org.rand.aio.core.protocol.ws.packet.WsChatReceiveMsg;
import org.rand.aio.core.protocol.ws.processor.AbstractWsProcessor;
import org.rand.aio.session.AioServerSession;
import org.rand.aio.util.AioKit;
import org.rand.aio.util.BeanKit;
import org.rand.ws.chat.WsGroup;
import org.rand.ws.chat.WsUser;
import org.rand.ws.msg.WsChatResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@AioWsProcessor
public class DefaultWsProcessor extends AbstractWsProcessor {
    private static Logger logger= LoggerFactory.getLogger(DefaultWsProcessor.class);

    private static ConcurrentHashMap<String,List<ReadWriteContext>> GROUP_CONTEXTS =new ConcurrentHashMap<>();

    private static Set<ReadWriteContext> ALL_CONNECT_CONTEXTS=new HashSet<>();

    private static Map<String ,WsGroup> GROUPS =new ConcurrentHashMap<>();

    private static Map<String ,WsUser> USERS =new ConcurrentHashMap<>();

    private static Set<String> SESSION_IDS =new HashSet<>();

    private static Gson gson= new GsonBuilder().create();

    @Override
    public boolean onConnect(AioRequest request) {
        return valiInfo(request);
    }

    @Override
    public void onReceiver(AioPacket packet, ReadWriteContext readWriteContext) {
        byte[] bytes = packet.getBytes();
        String json = new String(bytes);
        WsChatReceiveMsg wsChatReceiveMsg = gson.fromJson(json, WsChatReceiveMsg.class);
        String chatMsgCommand = wsChatReceiveMsg.getWsChatMsgCommand();
        WsChatMsgCommand wsChatMsgCommand = WsChatMsgCommand.formatValue(chatMsgCommand);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        System.out.println("指令："+wsChatMsgCommand.getCode());
        String targetId = wsChatReceiveMsg.getTargetId();
        wsChatReceiveMsg.setCreateTime(format);
        if (wsChatMsgCommand!=null){
            switch (wsChatMsgCommand) {
                case login:
                    login(wsChatReceiveMsg);
                    break;
                case logout:
                    //logout("",aioServerSession);
                    break;
                case join:
                    join(wsChatReceiveMsg);
                    break;
                case exit:
                   // exit("","");
                    break;
                case add:
                   // add("");
                    break;
                case del:
                    //del("");
                    break;
                case send:
                   sendToGroup(targetId,wsChatReceiveMsg);
                    break;
                case get:
                    getAny(wsChatReceiveMsg);
                    break;
            }
        }else{
            throw new RuntimeException("未知指令："+chatMsgCommand);
        }
    }

    @Override
    public void onClose() {
        String id = getSessionId();
        GROUP_CONTEXTS.remove(id);
        SESSION_IDS.remove(id);
        ALL_CONNECT_CONTEXTS.remove(getReadWriteContext());
        WsUser wsUser = USERS.get(id);
        List<WsGroup> groups = wsUser.getGroups();
        if(groups!=null){
            groups.forEach(g->{
                String groupId = g.getGroupId();
                WsGroup wsGroup = GROUPS.get(groupId);
                List<WsUser> users = wsGroup.getUsers();
                users.remove(wsUser);
            });
        }
       GROUPS.forEach((key, value) -> {
           if (value != null) {
               List<WsUser> users = value.getUsers();
               Iterator<WsUser> iterator = users.iterator();
               while (iterator.hasNext()) {
                   WsUser next = iterator.next();
                   String userId = next.getUserId();
                   if (id.equals(userId)) {
                       iterator.remove();
                   }
               }
           }
       });

        USERS.remove(id);
        getReadWriteContext().close();
        AioKit.delContext(id);
    }

    @Override
    public void onError(Exception e) {
        logger.error("业务出错",e);
    }

    void sendToGroup(String targetId, WsChatReceiveMsg wsChatReceiveMsg){
        WsChatResponseData<Object> wsChatResponseData = new WsChatResponseData<>();
        wsChatResponseData.setMsg("群组消息");
        wsChatResponseData.setStatus(2000);
        wsChatResponseData.setMsgType("talk");
        wsChatResponseData.setData(wsChatReceiveMsg);
        String s = wsChatResponseData.toJson();
        AioKit.sendToGroup(targetId,s);
        String s1 = wsChatResponse("发送成功", "response", 2000, null);
        AioKit.send(getReadWriteContext(),s1);
    }

    private String wsChatResponse(String msg, String msgType, Integer status, Object data){
        WsChatResponseData<Object> wsChatResponseData = new WsChatResponseData<>();
        wsChatResponseData.setMsg(msg);
        wsChatResponseData.setStatus(status);
        wsChatResponseData.setMsgType(msgType);
        wsChatResponseData.setData(data);
        return wsChatResponseData.toJson();
    }

    private boolean valiInfo(AioRequest aioRequest){
        String id = getSessionId();
        if (SESSION_IDS.contains(id)) {
            return false;
        }
        SESSION_IDS.add(id);
        ALL_CONNECT_CONTEXTS.add(getReadWriteContext());
        ReadWriteContext readWriteContext = getReadWriteContext();
        init(readWriteContext);
        return true;
    }

    public boolean login(WsChatReceiveMsg wsChatReceiveMsg){
        WsUser wsUser = new WsUser();
        wsUser.setUserName(getUserName());
        wsUser.setUserId(getSessionId());
        wsUser.setAvatar("http://images.rednet.cn/articleimage/2013/01/23/1403536948.jpg");
        USERS.put(getSessionId(),wsUser);
        String msg = wsChatResponse("登录成功", WsChatMsgCommand.login.getCode(), 2000, wsUser);
        AioKit.send(getReadWriteContext(),msg);
        return true;
    }

    private void init(ReadWriteContext readWriteContext) {
        AioKit.addContext(readWriteContext);
    }

    public void join(WsChatReceiveMsg wsChatReceiveMsg){
        AioKit.bindGroup(wsChatReceiveMsg.getTargetId(),wsChatReceiveMsg.getSenderId());
        WsGroup wsGroup = GROUPS.get(wsChatReceiveMsg.getTargetId());
        if(wsGroup==null){
            wsGroup = new WsGroup();
            wsGroup.setGroupId(wsChatReceiveMsg.getTargetId());
            wsGroup.setGroupName("Aio聊天室");
            GROUPS.put(wsChatReceiveMsg.getTargetId(),wsGroup);
        }
        List<WsUser> users = wsGroup.getUsers();
        if(users==null){
            users=new ArrayList<>();
            wsGroup.setUsers(users);
        }
        WsUser wsUser = this.USERS.get(wsChatReceiveMsg.getSenderId());
        String msg="";
        if(users.contains(wsUser)){
            msg= wsChatResponse("加入失败，已存在", WsChatMsgCommand.join.getCode(), 5000, wsGroup);
        }else{
            users.add(wsUser);
            msg = wsChatResponse("加入成功", WsChatMsgCommand.join.getCode(), 5000, wsGroup);
        }
        AioKit.send(getReadWriteContext(),msg);
    }

    private void getAny(WsChatReceiveMsg msg) {
        String senderId = msg.getSenderId();
        WsUser wsUser = this.USERS.get(senderId);
        WsUser wsUser1 = BeanKit.copyToTarget(wsUser, WsUser.class);
        WsGroup wsGroup = this.GROUPS.get(msg.getTargetId());
        List<WsGroup> objects = new ArrayList<>();
        objects.add(wsGroup);
        wsUser1.setGroups(objects);
        String s = wsChatResponse("获取成功", "get", 2000, wsUser1);
        AioKit.send(getReadWriteContext(),s);
        //write(s.getBytes());
    }

    private static String[] familyName = new String[] { "J-", "刘", "张", "李", "胡", "沈", "朱", "钱", "王", "伍", "赵", "孙", "吕", "马", "秦", "毛", "成", "梅", "黄", "郭", "杨", "季", "童", "习", "郑",
            "吴", "周", "蒋", "卫", "尤", "何", "魏", "章", "郎", " 唐", "汤", "苗", "孔", "鲁", "韦", "任", "袁", "贺", "狄朱" };

    private static String[] secondName = new String[] { "艺昕", "红薯", "明远", "天蓬", "三丰", "德华", "歌", "佳", "乐", "天", "燕子", "子牛", "海", "燕", "花", "娟", "冰冰", "丽娅", "大为", "无为", "渔民", "大赋",
            "明", "远平", "克弱", "亦菲", "靓颖", "富城", "岳", "先觉", "牛", "阿狗", "阿猫", "辰", "蝴蝶", "文化", "冲之", "悟空", "行者", "悟净", "悟能", "观", "音", "乐天", "耀扬", "伊健", "炅", "娜", "春花", "秋香", "春香",
            "大为", "如来", "佛祖", "科比", "罗斯", "詹姆屎", "科神", "科蜜", "库里", "卡特", "麦迪", "乔丹", "魔术师", "加索尔", "法码尔", "南斯", "伊哥", "杜兰特", "保罗", "杭州", "爱湘", "湘湘", "昕", "函", "鬼谷子", "膑", "荡",
            "子家", "德利优视", "五方会谈", "来电话了","轨迹","超"};

    private String getUserName(){
        Random random = new Random();
        int start = random.nextInt(familyName.length - 1);
        int end = random.nextInt(secondName.length - 1);
        return familyName[start] + secondName[end];
    }
}
