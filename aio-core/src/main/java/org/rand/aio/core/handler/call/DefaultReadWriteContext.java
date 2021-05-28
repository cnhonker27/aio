package org.rand.aio.core.handler.call;

import org.rand.aio.context.AioServerContext;
import org.rand.aio.core.decoder.AioPacket;
import org.rand.aio.core.handler.ReadCompletionHandler;
import org.rand.aio.core.handler.WriteCompletionHandler;
import org.rand.aio.core.protocol.AioProtocol;
import org.rand.aio.core.protocol.ws.handler.WsPacket;
import org.rand.aio.core.task.AioTask;
import org.rand.aio.core.task.sender.SenderTaskRunnable;
import org.rand.aio.session.AioServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.WritePendingException;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by fengliang on 2021/3/31.
 */
public class DefaultReadWriteContext implements ReadWriteContext{

    private static Logger logger= LoggerFactory.getLogger(DefaultReadWriteContext.class);

    private ByteBuffer readByteBuffer;

    private AioServerSession aioServerSession;

    private ReadCompletionHandler readCompletionHandler;

    private WriteCompletionHandler writeCompletionHandler;

    private AsynchronousSocketChannel asynchronousSocketChannel;

    private boolean isClose=false;

    private final ReentrantLock reentrantLock=new ReentrantLock();

    private final Condition condition=reentrantLock.newCondition();

    private AioServerContext aioServerContext;

    public DefaultReadWriteContext(ByteBuffer byteBuffer, AioServerSession aioServerSession, AsynchronousSocketChannel asynchronousSocketChannel, AioServerContext aioServerContext) {
        this.readByteBuffer = byteBuffer;
        this.aioServerSession=aioServerSession;
        this.asynchronousSocketChannel=asynchronousSocketChannel;
        this.readCompletionHandler=new ReadCompletionHandler(this,aioServerContext.getAioConfiguration().getProtocolDispatcher());
        this.writeCompletionHandler=new WriteCompletionHandler(this);
        this.aioServerContext=aioServerContext;
    }

    @Override
    public ByteBuffer getReadByteBuffer() {
        return this.readByteBuffer;
    }

    @Override
    public AioServerSession getAioServerSession() {
        return this.aioServerSession;
    }

    @Override
    public AsynchronousSocketChannel getAsynchronousSocketChannel() {
        return this.asynchronousSocketChannel;
    }

    @Override
    public void read() {
        if(isClose){
            return;
        }
        if(this.readByteBuffer.position()>0){
            this.readByteBuffer.clear();
        }
        this.asynchronousSocketChannel.read(this.readByteBuffer,this.readByteBuffer,this.readCompletionHandler);
    }


    @Override
    public void write(AioPacket aioPacket) {
        SenderTaskRunnable senderTaskRunnable = this.aioServerContext.getSenderTaskRunnable();
        ByteBuffer wrap = ByteBuffer.wrap(aioPacket.getBytes());
        boolean b = senderTaskRunnable.addTask(() -> await(() -> write(wrap)));
        if(!b){
            logger.error("任务添加失败{}",aioPacket);
        }else{
            senderTaskRunnable.scheduled();
        }
    }

    public void write(ByteBuffer writeBuffer) {
        // 通道没有关闭
        if(!this.isClose&&writeBuffer!=null&&writeBuffer.hasRemaining()){
            this.asynchronousSocketChannel.write(writeBuffer,writeBuffer,this.writeCompletionHandler);
        }
    }

    @Override
    public boolean isClose() {
        return isClose;
    }

    @Override
    public void close() {
        if(!isClose){
            readCompletionHandler.close(this);
            isClose=true;
        }
    }


    @Override
    public void await(AioTask task){
        reentrantLock.lock();
        try {
            if(task!=null){
                task.doTask();
                condition.await();
            }
        } catch (InterruptedException e) {
            logger.error("中断异常：{}-{}",Thread.currentThread().getName(),aioServerSession.getId(),e);
        } catch (Throwable throwable) {
            logger.error("await doTask Error{}-{}",Thread.currentThread().getName(),aioServerSession.getId(),throwable);
        }
        reentrantLock.unlock();
    }

    @Override
    public void signal(AioTask aioTask){
        reentrantLock.lock();
        try {
            if(aioTask!=null){
                aioTask.doTask();
            }
        } catch (Throwable throwable) {
            logger.error("signal doTask Error",throwable);
        }finally {
            condition.signal();
        }
        reentrantLock.unlock();
    }
}
