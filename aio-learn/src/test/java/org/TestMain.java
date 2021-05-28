package org;

import org.impl.TestInterfaceImpl;
import org.rand.aio.core.annotation.mvc.AioAutowired;

import java.lang.reflect.Field;

public class TestMain {

    <T>T getSomeThing(TestFactory testFactory,Class<T> tClass){
        Object t = testFactory.getT();
        return (T)t;
    }

    public void testfactory(){
        TestMain someThing = getSomeThing(() -> {
            return new TestMain("");
        },null);
        System.out.println(someThing);
    }


    public TestMain(String s){

    }

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        Class<?> itl=TestInterfaceImpl.class;
        TestInterfaceImpl o = (TestInterfaceImpl) itl.newInstance();
        Field[] fields = itl.getDeclaredFields();
        for (Field field : fields) {
            boolean annotationPresent = field.isAnnotationPresent(AioAutowired.class);
            if(annotationPresent){
                field.setAccessible(true);
                field.set(o,new TestMain("s"));
            }
        }
        o.say();
    }
}
