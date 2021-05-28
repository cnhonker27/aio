package org.rand.aio.session;

import org.rand.aio.core.protocol.AioProtocol;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * Created by fengliang on 2021/3/31.
 */
public class DefaultAioServerSession extends AioWebServerSession {

    private String id;

    private String host;

    private Integer port;

    private AioProtocol aioProtocol;

    public DefaultAioServerSession(){

    }

    public DefaultAioServerSession(InetSocketAddress inetSocketAddress){
        init(inetSocketAddress);
    }

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
    public AioProtocol getAioProtocol() {
        return aioProtocol;
    }

    @Override
    public void setAioProtocol(AioProtocol aioProtocol) {
        this.aioProtocol = aioProtocol;
    }

    @Override
    public String toString() {
        return "DefaultAioServerSession{" +
                "id='" + id + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
