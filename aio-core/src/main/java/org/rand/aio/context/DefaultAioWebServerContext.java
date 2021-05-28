package org.rand.aio.context;

import org.rand.aio.config.AioConfiguration;
import org.rand.aio.core.factory.AioBeanFactory;
import org.rand.aio.listener.DefaultAioServerListener;
import org.rand.aio.listener.AioServerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.AsynchronousServerSocketChannel;

/**
 * Created by fengliang on 2021/3/31.
 */
public class DefaultAioWebServerContext extends AioWebServerContext {

    private static Logger log= LoggerFactory.getLogger(DefaultAioWebServerContext.class);

    private DefaultAioServerListener defaultAioServerListener=new DefaultAioServerListener();

    public DefaultAioWebServerContext(AsynchronousServerSocketChannel asynchronousServerSocketChannel, AioConfiguration aioConfiguration, AioBeanFactory aioBeanFactory){
       super(asynchronousServerSocketChannel,aioConfiguration,aioBeanFactory);
    }

    @Override
    public AioServerListener getAioServerListener() {
        return this.defaultAioServerListener;
    }

    void aVoid(){
        super.addPlugins(null);
    }

}
