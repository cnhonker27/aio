package org.rand.aio.listener;

import org.rand.aio.session.AioServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by fengliang on 2021/3/31.
 */
public class DefaultAioServerListener implements AioServerListener {
    private Logger logger=  LoggerFactory.getLogger(DefaultAioServerListener.class);

    // TODO 新的链接接入时加入黑名单什么的
    @Override
    public void before(AioServerSession aioServerSession) {
        logger.info("新连接：{}",aioServerSession.toString());
    }

    // TODO 经过一系列操作后（如handler,dispatcher,filter）拒绝链接、关闭会话等附加功能
    @Override
    public void after(AioServerSession aioServerSession) {

    }
}
