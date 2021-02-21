package com.coocaa.manage.annotation;

import java.lang.annotation.*;

/**
 * @Description: 注入静态属性  未完成
 * @Author: ysx
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface StaticValue {
    String value();
}
