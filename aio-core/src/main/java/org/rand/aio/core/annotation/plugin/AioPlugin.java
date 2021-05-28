package org.rand.aio.core.annotation.plugin;

import org.rand.aio.core.annotation.AioComponent;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AioComponent
public @interface AioPlugin {

    boolean value() default true;

    String name() default "";
}
