package org.rand.aio.io;

public class ClassLoaderUtil {

    public static ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }
}
