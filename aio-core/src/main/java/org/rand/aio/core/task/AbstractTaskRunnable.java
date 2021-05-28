package org.rand.aio.core.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class AbstractTaskRunnable<T extends AioTask> implements TaskRunnable<T> {

    private final static Logger logger= LoggerFactory.getLogger(AbstractTaskRunnable.class);

    private LinkedBlockingQueue<T> tasksQueue=new LinkedBlockingQueue<>();

    private final Object taskLock=new Object();

    @Override
    public void run() {
        if (runOnlyOne()) {
            synchronized (taskLock){
                doTask();
            }
        }else{
            doTask();
        }
    }

    @Override
    public boolean addTask(T task) {
        return tasksQueue.add(task);
    }

    private void doTask(){
        try {
            // 多线程执行时不使用take,take会导致线程阻塞
            // 原因：如果有两个线程在消费队列，其中一个线程把队列的消息消费完，
            // 那么另外一个线程就一直在等待，导致线程没有被回收
            // 注：前提是某些特殊业务存在循环消费，才回会发生该问题
            AioTask take = tasksQueue.poll();
            if(take!=null){
                take.doTask();
            }
        } catch (InterruptedException e) {
            logger.error("队列执行出错");
        } catch (Throwable throwable) {
            logger.error("任务执行出错");
        }
    }
}
