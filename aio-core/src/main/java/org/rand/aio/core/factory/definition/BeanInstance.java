package org.rand.aio.core.factory.definition;

public class BeanInstance {

    private String beanName;

    private Object object;

    public BeanInstance(Object object){
        this(object.getClass().getSimpleName(),object);
    }

    public BeanInstance(String beanName, Object object) {
        this.beanName = beanName;
        this.object = object;
    }


    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
