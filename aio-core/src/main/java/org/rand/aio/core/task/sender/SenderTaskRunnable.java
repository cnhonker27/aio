package org.rand.aio.core.task.sender;

import org.rand.aio.core.task.AbstractTaskRunnable;
import org.rand.aio.core.task.AioTask;

import java.util.concurrent.Executor;

public class SenderTaskRunnable extends AbstractTaskRunnable<AioTask> {

    private Executor executor;

    public SenderTaskRunnable(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void scheduled() {
        executor.execute(this);
    }

    @Override
    public boolean runOnlyOne() {
        return true;
    }
}
