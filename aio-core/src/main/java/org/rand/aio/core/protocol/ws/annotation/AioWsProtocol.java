package org.rand.aio.core.protocol.ws.annotation;

import org.rand.aio.core.annotation.AioComponent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@AioComponent
public @interface AioWsProtocol {
    String value() default "";
}
