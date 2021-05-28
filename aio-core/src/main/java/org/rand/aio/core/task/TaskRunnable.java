package org.rand.aio.core.task;

public interface TaskRunnable<T> extends Runnable {

    // 添加任务
    boolean addTask(T task);

    // 安排执行任务
    void scheduled();

    // 限制线程竞争时只执行一个，即上锁。
    // channel写入写出时，在同一时间内只能写出一个
    boolean runOnlyOne();

}
