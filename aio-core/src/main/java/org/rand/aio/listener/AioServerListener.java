package org.rand.aio.listener;

import org.rand.aio.session.AioServerSession;

/**
 * Created by fengliang on 2021/3/31.
 */
public interface AioServerListener {

    void before(AioServerSession aioServerSession);

    void after(AioServerSession aioServerSession);
}
