package org.rand.aio.core.protocol.ws.packet;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author rand
 * @Date 2020/12/20 23:53
 * @Version 1.0
 */
public enum WsChatMsgCommand {
    login("login","登陆"),

    logout("logout","登出"),

    send("send","发送"),

    join("join","加入群组"),

    exit("exit","退出群组"),

    add("add","添加好友"),

    del("del","删除好友"),

    get("get","获取任何东西"),
    ;

    private String code;

    private String description;

    public final static Map<String,WsChatMsgCommand> COMMAND_MAP=new HashMap<>();

    static {
        for (WsChatMsgCommand value : values()) {
            COMMAND_MAP.put(value.getCode(),value);
        }
    }

    WsChatMsgCommand(String code,String description) {
        this.code=code;
        this.description=description;
    }

    public String getCode(){
        return this.code;
    }

    public static WsChatMsgCommand formatValue(String code){
        return COMMAND_MAP.get(code);
    }
}
