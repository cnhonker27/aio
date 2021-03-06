package org.rand.aio;

import org.apache.commons.lang3.StringUtils;
import org.rand.aio.config.AioConfiguration;
import org.rand.aio.context.AioServerContext;
import org.rand.aio.context.DefaultAioWebServerContext;
import org.rand.aio.core.factory.BaseBeanFactory;
import org.rand.aio.core.handler.AcceptCompletionHandler;
import org.rand.aio.core.handler.dispatcher.DefaultProtocolManager;
import org.rand.aio.core.handler.dispatcher.ProtocolDispatcherImpl;
import org.rand.aio.core.protocol.AioProtocol;
import org.rand.aio.core.protocol.http.HttpProtocol;
import org.rand.aio.core.protocol.http.HttpRenderHandler;
import org.rand.aio.core.protocol.ws.WsProtocol;
import org.rand.aio.core.protocol.ws.processor.AbstractWsProcessor;
import org.rand.aio.core.protocol.ws.processor.WsProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by fengliang on 2021/3/31.
 */
public class AioServer {

    private final static Logger logger= LoggerFactory.getLogger(AioServer.class);

    private ExecutorService executorServiceServer;

    private AsynchronousChannelGroup asynchronousChannelGroup;

    private AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    private Class<?> mainClazz;

    private AioServerContext aioServerContext;

    private boolean isDone=false;

    private AioServer (Class<?> clazz){
        this.mainClazz=clazz;
    }

    public static AioServerContext run(Class<?> clazz, String... args){
        AioServer aioServer= new AioServer(clazz);
        try {
            return aioServer.start(args);
        } catch (Throwable e) {
            logger.error("????????????",e);
            throw new RuntimeException(e);
        }finally {
            Thread thread = new Thread(aioServer::stop);
            Runtime.getRuntime().addShutdownHook(thread);
        }

    }

    public AioServerContext start(String... args) throws IOException {
        logger.info("Aio-Server ????????????......");
        CalcStartWatch calcStartWatcher = new CalcStartWatcher();
        calcStartWatcher.startWatch();
        // ??????bean??????
        BaseBeanFactory baseBeanFactory = prepareFactory();
        // ??????????????????
        AioConfiguration aioConfiguration = prepareConfig(baseBeanFactory);
        // ??????????????????
        startSocketServer(baseBeanFactory, aioConfiguration);

        calcStartWatcher.stopWatch().print(aioServerContext.getAioConfiguration());
        return aioServerContext;
    }

    private void startSocketServer(BaseBeanFactory baseBeanFactory, AioConfiguration aioConfiguration) throws IOException {
        LinkedBlockingQueue<Runnable> server = new LinkedBlockingQueue<>();
        this.executorServiceServer=new ThreadPoolExecutor(4,16,3600, TimeUnit.SECONDS,server);
        // ??????????????????????????????????????????
        ((ThreadPoolExecutor) executorServiceServer).prestartCoreThread();
        this.asynchronousChannelGroup = AsynchronousChannelGroup.withThreadPool(executorServiceServer);
        this.asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open(this.asynchronousChannelGroup);
        this.asynchronousServerSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        this.asynchronousServerSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 65536);
        InetSocketAddress inetSocketAddress ;
        String ip=aioConfiguration.getAddress();
        int port=aioConfiguration.getPort();
        if (StringUtils.isEmpty(ip)) {
            inetSocketAddress = new InetSocketAddress(port);
        } else {
            inetSocketAddress = new InetSocketAddress(ip, port);
        }
        this.asynchronousServerSocketChannel.bind(inetSocketAddress,10);
        AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler();
        this.aioServerContext= createAioServerContext(baseBeanFactory,aioConfiguration);
        this.aioServerContext.accept(aioServerContext,acceptCompletionHandler);
        isDone=true;
    }

    private AioServerContext createAioServerContext(BaseBeanFactory baseBeanFactory,AioConfiguration aioConfiguration) {
        // ????????????DefaultAioWebServerContext ???????????????????????????
        return new DefaultAioWebServerContext(asynchronousServerSocketChannel,aioConfiguration,baseBeanFactory);
    }

    private BaseBeanFactory prepareFactory(){
        return new BaseBeanFactory(mainClazz);
    }

    // TODO ?????????????????????????????????
    AioConfiguration prepareConfig(BaseBeanFactory baseBeanFactory){
        LinkedList<AioProtocol> protocols = new LinkedList<>();
        HttpProtocol httpProtocol = new HttpProtocol(new HttpRenderHandler(baseBeanFactory));
        WsProtocol bean = baseBeanFactory.getBean(WsProtocol.class);
        protocols.add(httpProtocol);
        protocols.add(bean);
        DefaultProtocolManager defaultProtocolManager = new DefaultProtocolManager(protocols);
        ProtocolDispatcherImpl protocolDispatcher = new ProtocolDispatcherImpl(defaultProtocolManager);
        return new AioConfiguration(defaultProtocolManager,protocolDispatcher);
    }

    public void stop(){
        logger.info(" Aio-Server ????????????......");
            try {
                this.asynchronousChannelGroup.shutdown();
                this.executorServiceServer.shutdown();
                this.asynchronousServerSocketChannel.close();
            } catch (Exception e) {
                logger.error(" Aio-Server ????????????");
            }
        logger.info(" Aio-Server ?????????......");
    }

    interface CalcStartWatch{
        CalcStartWatch startWatch();
        CalcStartWatch stopWatch();
        void print(AioConfiguration aioConfiguration);
    }

    static class CalcStartWatcher implements CalcStartWatch {

        Long startTime;

        Long endTime;

        public CalcStartWatcher startWatch(){
            return start();
        }

        public CalcStartWatch stopWatch(){
            return stop();
        }

        public Long getNowTime(){
            return  System.currentTimeMillis();
        }

        private CalcStartWatcher start(){
            this.startTime=getNowTime();
            return this;
        }

        private CalcStartWatcher stop(){
            this.endTime=getNowTime();
            return this;
        }


        public void print(AioConfiguration aioConfiguration){
            logger.info("???????????????{}",new Timestamp(startTime).toString());
            logger.info("???????????????{}",new Timestamp(endTime).toString());
            logger.info("???????????????{}???",((endTime-startTime)/1000d));
            try {
                InetAddress localHost = InetAddress.getLocalHost();
                String hostName = localHost.getHostName();
                String hostAddress = localHost.getHostAddress();
                System.out.println("http://"+hostName+":"+aioConfiguration.getPort());
                System.out.println("http://"+hostAddress+":"+aioConfiguration.getPort());
            } catch (UnknownHostException e) {
               logger.error("??????ip??????????????????",e);
            }
        }
    }
}

