package org.rand.aio.core.protocol.tcp;

/**
 * Created by fengliang on 2021/3/24.
 */
public class DestConfigDto {
    Integer remotePort;
    String tunnelName;
    String protocol;

    public Integer getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(Integer remotePort) {
        this.remotePort = remotePort;
    }

    public String getTunnelName() {
        return tunnelName;
    }

    public void setTunnelName(String tunnelName) {
        this.tunnelName = tunnelName;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String toString() {
        return "DestConfigDto{" +
                "remotePort='" + remotePort + '\'' +
                ", tunnelName='" + tunnelName + '\'' +
                ", protocol='" + protocol + '\'' +
                '}';
    }
}
