package org.rand.aio.context;

import org.rand.aio.config.AioConfiguration;
import org.rand.aio.core.factory.AioBeanFactory;
import org.rand.aio.core.factory.BaseBeanFactory;
import org.rand.aio.core.factory.definition.BeanInstance;
import org.rand.aio.core.plugin.IPlugin;
import org.rand.aio.core.task.sender.SenderTaskRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by fengliang on 2021/3/31.
 */
public abstract class AioWebServerContext implements AioServerContext{
    private static Logger logger= LoggerFactory.getLogger(DefaultAioWebServerContext.class);

    private AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    private AioConfiguration aioConfiguration;

    private BaseBeanFactory beanFactory;

    private SenderTaskRunnable senderTaskRunnable;

    private final Object lock=new Object();

    private volatile boolean isInit=false;

    public AioWebServerContext(AsynchronousServerSocketChannel asynchronousServerSocketChannel,AioConfiguration aioConfiguration,AioBeanFactory beanFactory){
        this.asynchronousServerSocketChannel=asynchronousServerSocketChannel;
        this.aioConfiguration=aioConfiguration;
        this.beanFactory= (BaseBeanFactory) beanFactory;
        init();
    }

    @Override
    public void init() {
        if(!isInit){
            synchronized (lock){
                if (!isInit) {
                    ThreadPoolExecutor senderExecutor = new ThreadPoolExecutor(4, 32, 3600, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
                    this.senderTaskRunnable = new SenderTaskRunnable(senderExecutor);
                    isInit=true;
                }
            }
        }

    }

    @Override
    public Object getBean(String beanName) {
        return this.beanFactory.getBean(beanName);
    }

    @Override
    public <T> T getBean(Class<T> classType) {
        return this.beanFactory.getBean(classType);
    }

    @Override
    public <T> T getBean(String beaName, Class<T> classType) {
        return this.beanFactory.getBean(beaName,classType);
    }

    @Override
    public void accept(AioServerContext context, CompletionHandler handler) {
        this.asynchronousServerSocketChannel.accept(context,handler);
    }

    @Override
    public void close() {
        try {
            // TODO 不一定立即关闭,等待所有输出write写完后再关闭
            asynchronousServerSocketChannel.close();
        } catch (IOException e) {
            logger.error("发生异常，程序即将关闭",e);
        }
    }

    @Override
    public AioConfiguration getAioConfiguration() {
        return this.aioConfiguration;
    }

    @Override
    public SenderTaskRunnable getSenderTaskRunnable() {
        return this.senderTaskRunnable;
    }

    private final List<IPlugin> iPlugins=new LinkedList<>();

    protected final void addPlugins(IPlugin plugin){

    }

    @Override
    public void addPlugin(IPlugin plugin) {
        addPlugins(plugin);
    }
}
