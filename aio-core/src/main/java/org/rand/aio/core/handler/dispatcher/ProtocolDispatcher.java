package org.rand.aio.core.handler.dispatcher;

import org.rand.aio.core.handler.call.ReadWriteContext;

/**
 * Created by fengliang on 2021/4/1.
 */
public interface ProtocolDispatcher {

    void doProtocolReceiver(ReadWriteContext writeContext) throws Throwable;

}
