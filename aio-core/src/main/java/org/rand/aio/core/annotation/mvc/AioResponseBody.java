package org.rand.aio.core.annotation.mvc;

import org.rand.aio.core.render.AioRenderEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AioResponseBody {

    AioRenderEnum value() default AioRenderEnum.JSON;
}
