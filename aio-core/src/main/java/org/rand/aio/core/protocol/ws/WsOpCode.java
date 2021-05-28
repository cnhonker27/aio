package org.rand.aio.core.protocol.ws;

import java.util.HashMap;
import java.util.Map;

public enum  WsOpCode {
    // opcode=1、2、8、9、10 对应 text、binary、close、ping、pong
    TEXT((byte)1),BINARY((byte)2),CLOSE((byte)8),PING((byte)9),PONG((byte)10);
    private final static Map<Byte,WsOpCode> OPCODE_MAP=new HashMap<>();
    static {
        for(WsOpCode val:values()){
            OPCODE_MAP.put(val.getCode(),val);
        }
    }
    private byte code;

    private WsOpCode(byte code) {
        this.code=code;
    }

    public static WsOpCode formatValue(byte code){
        return OPCODE_MAP.get(code);
    }

    public byte getCode(){
        return this.code;
    }


}
