package org.impl;

import org.TestInterface;
import org.TestMain;
import org.rand.aio.core.annotation.mvc.AioAutowired;

public class TestInterfaceImpl implements TestInterface {
    @AioAutowired
    TestMain testMain;


    @Override
    public void say() {
        testMain.testfactory();
    }
}
