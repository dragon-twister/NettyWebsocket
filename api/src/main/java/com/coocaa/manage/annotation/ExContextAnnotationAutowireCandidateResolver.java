package com.coocaa.manage.annotation;

import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;

import java.lang.annotation.Annotation;

/**
 * @Description: spring 容器中默认的 ContextAnnotationAutowireCandidateResolver 写死只解析 @Value 注解 ，想解析自定义的 @StaticValue 注解 只能出此继承下策
 * @Author: ysx
 * @Date: 2020/6/4 18:02
 */
public class ExContextAnnotationAutowireCandidateResolver extends ContextAnnotationAutowireCandidateResolver {
    private Class<? extends Annotation> staticValueAnnotationType = StaticValue.class;

    /**
     * Determine a suggested value from any of the given candidate annotations.
     *
     */
    @Override
    protected Object findValue(Annotation[] annotationsToSearch) {
        // 父类 对 @Value 的 value 值解析出来
        Object value = super.findValue(annotationsToSearch);
        if(value!=null){
            return value;
        }
        // 如果 无值 解析 @staticValue
        if (annotationsToSearch.length > 0) {
            AnnotationAttributes attr = AnnotatedElementUtils.getMergedAnnotationAttributes(
                    AnnotatedElementUtils.forAnnotations(annotationsToSearch), this.staticValueAnnotationType);
            if (attr != null) {
                return extractValue(attr);
            }
        }
        return null;
    }
}
