package org.rand.aio.core.annotation.plugin;

public enum EnablePluginEnum {
    TRUE(true),FALSE(false);
    boolean status;
    EnablePluginEnum(Boolean status){
        this.status=status;
    }

    public boolean getStatus(){
        return this.status;
    }
}
