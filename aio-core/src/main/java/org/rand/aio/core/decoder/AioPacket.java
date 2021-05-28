package org.rand.aio.core.decoder;

/**
 * Created by fengliang on 2021/4/8.
 */
public interface AioPacket {

    byte[] getBytes();

    String getId();
}
