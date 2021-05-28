package org.rand.aio.core.annotation.plugin;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExcludeAioPlugin {

    Class<? extends Annotation>[] value();

}
