package org.rand.aio.context;

import org.rand.aio.config.AioConfiguration;
import org.rand.aio.core.factory.AioBeanFactory;
import org.rand.aio.core.plugin.IPlugin;
import org.rand.aio.core.task.sender.SenderTaskRunnable;
import org.rand.aio.listener.AioServerListener;

import java.nio.channels.CompletionHandler;

/**
 * Created by fengliang on 2021/3/31.
 */
public interface AioServerContext extends AioBeanFactory {

    void init();

    AioConfiguration getAioConfiguration();

    void accept(AioServerContext context, CompletionHandler handler);

    AioServerListener getAioServerListener();

    void close();

    void addPlugin(IPlugin plugin);

    SenderTaskRunnable getSenderTaskRunnable();
}
