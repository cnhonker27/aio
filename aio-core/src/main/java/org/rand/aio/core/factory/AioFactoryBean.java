package org.rand.aio.core.factory;

public interface AioFactoryBean {

    <T>T getObject(String beanName);
}
