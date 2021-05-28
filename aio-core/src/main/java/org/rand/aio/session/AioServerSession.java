package org.rand.aio.session;

import org.rand.aio.core.protocol.AioProtocol;

/**
 * Created by fengliang on 2021/3/31.
 */
public interface AioServerSession {

    String getId();

    String getHost();

    Integer getPort();

    String toString();

    AioProtocol getAioProtocol();

    void setAioProtocol(AioProtocol aioProtocol);

    boolean getHandshake();

    void setHandshake(Boolean handshake);
}
