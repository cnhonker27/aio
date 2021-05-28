package org.rand.aio.session;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * Created by fengliang on 2021/3/31.
 */
public abstract class AioWebServerSession implements AioServerSession {

    private String id;

    private String host;

    private Integer port;

    private Boolean isHandshake=false;

    public AioWebServerSession(){

    }

    public AioWebServerSession(InetSocketAddress inetSocketAddress){
        init(inetSocketAddress);
    }

    // TODO 简单获取，往后不一定这样做
    private void init(InetSocketAddress inetSocketAddress) {
        this.id= UUID.randomUUID().toString().replace("-","");
        this.host=inetSocketAddress.getHostString();
        this.port=inetSocketAddress.getPort();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public Integer getPort() {
        return this.port;
    }

    @Override
    public boolean getHandshake() {
        return this.isHandshake;
    }

    @Override
    public void setHandshake(Boolean handshake) {
        isHandshake = handshake;
    }
}
