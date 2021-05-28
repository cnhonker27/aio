package org.rand.aio.core.render;

public class AioRenderModel {
    private String viewName;

    private Object renderObject;

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Object getRenderObject() {
        return renderObject;
    }

    public void setRenderObject(Object renderObject) {
        this.renderObject = renderObject;
    }
}
