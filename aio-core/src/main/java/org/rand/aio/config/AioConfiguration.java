package org.rand.aio.config;

import org.rand.aio.core.handler.dispatcher.DefaultProtocolManager;
import org.rand.aio.core.handler.dispatcher.ProtocolDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by fengliang on 2021/4/2.
 */
public class AioConfiguration {
    private final static Logger logger= LoggerFactory.getLogger(AioConfiguration.class);
    // 最大接收缓冲区 128*1024=128M
    private static final int MAX_BUFFER_SIZE=1<<7;

    // 默认接收缓冲区 16*1024=16M
    private static final Integer DEFAULT_BUFFER_SIZE=16;

    // 用于接收回调的数据大小
    private int byteBufferSize=2048;

    // 默认不做限制绑定主机地址
    private String address="0.0.0.0";

    private int port=6951;

    DefaultProtocolManager defaultProtocolManager;

    ProtocolDispatcher protocolDispatcher;

    public AioConfiguration(DefaultProtocolManager defaultProtocolManager,ProtocolDispatcher protocolDispatcher){
        this(defaultProtocolManager,protocolDispatcher,DEFAULT_BUFFER_SIZE);
    }

    public AioConfiguration(DefaultProtocolManager defaultProtocolManager,ProtocolDispatcher protocolDispatcher,Integer cap){
        if(cap>DEFAULT_BUFFER_SIZE){
            logger.warn("设置缓冲区的值{}不合法，应该小于等于{}，已替换为{}",cap,MAX_BUFFER_SIZE,MAX_BUFFER_SIZE);
        }
        cap=bufferSizeFor(cap);
        this.byteBufferSize=cap*1024;
        this.defaultProtocolManager=defaultProtocolManager;
        this.protocolDispatcher=protocolDispatcher;
    }

    public DefaultProtocolManager getDefaultProtocolManager() {
        return defaultProtocolManager;
    }

    public void setDefaultProtocolManager(DefaultProtocolManager defaultProtocolManager) {
        this.defaultProtocolManager = defaultProtocolManager;
    }

    public ProtocolDispatcher getProtocolDispatcher() {
        return protocolDispatcher;
    }

    public void setProtocolDispatcher(ProtocolDispatcher protocolDispatcher) {
        this.protocolDispatcher = protocolDispatcher;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getByteBufferSize() {
        return byteBufferSize;
    }

    public void setByteBufferSize(int byteBufferSize) {
        this.byteBufferSize = byteBufferSize;
    }
    /**
     *  hashMap里很有意思的一个算法
     *  获取一个总大于等于输入的数，而且为2^n
     * @param cap
     * @return
     */
    static final int bufferSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= (MAX_BUFFER_SIZE)) ? (MAX_BUFFER_SIZE) : n + 1;
    }

}
