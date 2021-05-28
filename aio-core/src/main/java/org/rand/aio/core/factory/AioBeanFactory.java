package org.rand.aio.core.factory;

/**
 * Created by fengliang on 2021/3/31.
 */
public interface AioBeanFactory {

    Object getBean(String beanName);

    <T>T getBean(Class<T> classType);

    <T>T getBean(String beaName,Class<T> classType);

}
