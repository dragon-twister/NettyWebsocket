package com.coocaa.websocket.api.annotation;

import java.lang.annotation.*;

/**
 * @Description: 注入静态属性 未完成
 * @Author: ysx
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface StaticAutowired {
    // String value() default "";

    /**
     * Declares whether the annotated dependency is required.
     * <p>Defaults to {@code true}.
     */
    boolean required() default true;
}
