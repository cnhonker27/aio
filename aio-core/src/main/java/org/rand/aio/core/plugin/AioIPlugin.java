package org.rand.aio.core.plugin;

import org.rand.aio.core.factory.definition.BeanDefinition;
import org.rand.aio.core.factory.definition.BeanInstance;

import java.util.Collections;
import java.util.List;

public abstract class AioIPlugin<T> implements IPlugin<T> {
    public List<BeanInstance> getInstances(){
        return Collections.emptyList();
    }
}
