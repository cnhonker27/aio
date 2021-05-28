package org.rand.aio.core.dispatcher;

import org.rand.aio.core.render.AioRenderEnum;

import java.lang.reflect.Method;

public class AioRequestMethodMapping {

    private Class<?>[] parameterType;

    private String[] parameterName;

    private String queryingString;

    private Method mappingMethod;

    private Object controller;

    private AioRenderEnum aioRenderEnum=AioRenderEnum.VIEW;

    public AioRequestMethodMapping(String queryingString, Method mappingMethod, Object controller) {
        this.queryingString = queryingString;
        this.mappingMethod = mappingMethod;
        this.controller = controller;
    }

    public Class<?>[] getParameterType() {
        return parameterType;
    }

    public void setParameterType(Class<?>[] parameterType) {
        this.parameterType = parameterType;
    }

    public String[] getParameterName() {
        return parameterName;
    }

    public void setParameterName(String[] parameterName) {
        this.parameterName = parameterName;
    }

    public String getQueryingString() {
        return queryingString;
    }

    public void setQueryingString(String queryingString) {
        this.queryingString = queryingString;
    }

    public Method getMappingMethod() {
        return mappingMethod;
    }

    public void setMappingMethod(Method mappingMethod) {
        this.mappingMethod = mappingMethod;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public AioRenderEnum getAioRenderEnum() {
        return aioRenderEnum;
    }

    public void setAioRenderEnum(AioRenderEnum aioRenderEnum) {
        this.aioRenderEnum = aioRenderEnum;
    }

}
