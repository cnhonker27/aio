package org.rand.aio.core.annotation.mvc;

import org.rand.aio.core.annotation.AioComponent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@AioComponent
public @interface AioAutowired {
    String value() default "";
}
