package org.rand.aio.core.plugin;

import org.rand.aio.core.factory.AioFactoryBean;

public interface IPlugin<T> {

    T create();

    void destroy();

}
