package com.coocaa.manage.annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 自动注入静态属性
 * @Author: ysx
 */
@Component
public class StaticAutowiredAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
        implements MergedBeanDefinitionPostProcessor, PriorityOrdered, BeanFactoryAware {
    private final Log logger = LogFactory.getLog(getClass());

    private DefaultListableBeanFactory beanFactory;

    private final Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<>(256);


    /**
     *  支持的注解
     */
    private final Set<Class<? extends Annotation>> autowiredAnnotationTypes = new LinkedHashSet<>(4);

    private String requiredParameterName = "required";

    private boolean requiredParameterValue = true;

    private final ExContextAnnotationAutowireCandidateResolver exContextAnnotationAutowireCandidateResolver =  new ExContextAnnotationAutowireCandidateResolver();

    @SuppressWarnings("unchecked")
    public StaticAutowiredAnnotationBeanPostProcessor() {
        this.autowiredAnnotationTypes.add(StaticAutowired.class);
        this.autowiredAnnotationTypes.add(StaticValue.class);
    }



    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass(), pvs);
        AutowireCandidateResolver autowireCandidateResolver = beanFactory.getAutowireCandidateResolver();
        // 为了 解析 @StaticValue 必须使用 自定义的 ExContextAnnotationAutowireCandidateResolver
        boolean isExContextAnnotationAutowireCandidateResolver = autowireCandidateResolver instanceof ExContextAnnotationAutowireCandidateResolver;
        try {
            if (!isExContextAnnotationAutowireCandidateResolver) {
                beanFactory.setAutowireCandidateResolver(exContextAnnotationAutowireCandidateResolver);
            }
            metadata.inject(bean, beanName, pvs);
        }
        catch (BeanCreationException ex) {
            throw ex;
        }
        catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of static autowired dependencies failed", ex);
        }finally {
            // 设置回原来的
            if (!isExContextAnnotationAutowireCandidateResolver) {
                beanFactory.setAutowireCandidateResolver(autowireCandidateResolver);
            }
        }
        return pvs;
    }



    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof DefaultListableBeanFactory)) {
            throw new IllegalArgumentException(
                    "StaticAutowiredAnnotationBeanPostProcessor requires a DefaultListableBeanFactory: " + beanFactory);
        }
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }


    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        InjectionMetadata metadata = findAutowiringMetadata(beanName, beanType, null);
        metadata.checkConfigMembers(beanDefinition);
    }


    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 2;
    }



    private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> clazz, @Nullable PropertyValues pvs) {
        // Fall back to class name as cache key, for backwards compatibility with custom callers.
        String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
        // Quick check on the concurrent map first, with minimal locking.
        InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    metadata = buildAutowiringMetadata(clazz);
                    this.injectionMetadataCache.put(cacheKey, metadata);
                }
            }
        }
        return metadata;
    }


    private InjectionMetadata buildAutowiringMetadata(final Class<?> clazz) {
        List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
        Class<?> targetClass = clazz;

        do {
            final List<InjectionMetadata.InjectedElement> currElements = new ArrayList<>();
            ReflectionUtils.doWithLocalFields(targetClass, field -> {
                AnnotationAttributes ann = findAutowiredAnnotation(field);
                if (ann != null) {
                    //  这里把不做静态检查 -- @Autowired 在这里做了静态校验，因此无法自动注入静态属性
                    boolean required = determineRequiredStatus(ann);
                    currElements.add(new StaticAutowiredFieldElement(field, required));
                }
            });

            elements.addAll(0, currElements);
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);
        return new InjectionMetadata(clazz, elements);
    }


    /**
     * Determine if the annotated field or method requires its dependency.
     * <p>A 'required' dependency means that autowiring should fail when no beans
     * are found. Otherwise, the autowiring process will simply bypass the field
     * or method when no beans are found.
     * @param ann the Autowired annotation
     * @return whether the annotation indicates that a dependency is required
     */
    protected boolean determineRequiredStatus(AnnotationAttributes ann) {
        return (!ann.containsKey(this.requiredParameterName) ||
                this.requiredParameterValue == ann.getBoolean(this.requiredParameterName));
    }


    @Nullable
    private AnnotationAttributes findAutowiredAnnotation(AccessibleObject ao) {
        // autowiring annotations have to be local
        if (ao.getAnnotations().length > 0) {
            for (Class<? extends Annotation> type : this.autowiredAnnotationTypes) {
                AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(ao, type);
                if (attributes != null) {
                    return attributes;
                }
            }
        }
        return null;
    }





    /**
     * Class representing injection information about an annotated field.
     */
    private class StaticAutowiredFieldElement extends InjectionMetadata.InjectedElement {

        private final boolean required;

        private volatile boolean cached = false;

        @Nullable
        private volatile Object cachedFieldValue;

        public StaticAutowiredFieldElement(Field field, boolean required) {
            super(field, null);
            this.required = required;
        }

        @Override
        protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
            Field field = (Field) this.member;
            Object value;
            if (this.cached) {
                value = resolvedCachedArgument(beanName, this.cachedFieldValue);
            }
            else {
                DependencyDescriptor desc = new DependencyDescriptor(field, this.required);
                desc.setContainingClass(bean.getClass());
                Set<String> autowiredBeanNames = new LinkedHashSet<>(1);
                Assert.state(beanFactory != null, "No BeanFactory available");
                TypeConverter typeConverter = beanFactory.getTypeConverter();
                try {
                    value = beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);
                }
                catch (BeansException ex) {
                    throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(field), ex);
                }
                synchronized (this) {
                    if (!this.cached) {
                        if (value != null || this.required) {
                            this.cachedFieldValue = desc;
                            registerDependentBeans(beanName, autowiredBeanNames);
                            if (autowiredBeanNames.size() == 1) {
                                String autowiredBeanName = autowiredBeanNames.iterator().next();
                                if (beanFactory.containsBean(autowiredBeanName) &&
                                        beanFactory.isTypeMatch(autowiredBeanName, field.getType())) {
                                    this.cachedFieldValue = new ShortcutDependencyDescriptor(
                                            desc, autowiredBeanName, field.getType());
                                }
                            }
                        }
                        else {
                            this.cachedFieldValue = null;
                        }
                        this.cached = true;
                    }
                }
            }
            if (value != null) {
                ReflectionUtils.makeAccessible(field);
                field.set(bean, value);
            }
        }
    }



    /**
     * Register the specified bean as dependent on the staticAutowired beans.
     */
    private void registerDependentBeans(@Nullable String beanName, Set<String> autowiredBeanNames) {
        if (beanName != null) {
            for (String autowiredBeanName : autowiredBeanNames) {
                if (this.beanFactory != null && this.beanFactory.containsBean(autowiredBeanName)) {
                    this.beanFactory.registerDependentBean(autowiredBeanName, beanName);
                }
                if (logger.isTraceEnabled()) {
                    logger.trace("StaticAutowiring by type from bean name '" + beanName +
                            "' to bean named '" + autowiredBeanName + "'");
                }
            }
        }
    }



    /**
     * Resolve the specified cached method argument or field value.
     */
    @Nullable
    private Object resolvedCachedArgument(@Nullable String beanName, @Nullable Object cachedArgument) {
        if (cachedArgument instanceof DependencyDescriptor) {
            DependencyDescriptor descriptor = (DependencyDescriptor) cachedArgument;
            Assert.state(this.beanFactory != null, "No BeanFactory available");
            return this.beanFactory.resolveDependency(descriptor, beanName, null, null);
        }
        else {
            return cachedArgument;
        }
    }


    /**
     * DependencyDescriptor variant with a pre-resolved target bean name.
     */
    @SuppressWarnings("serial")
    private static class ShortcutDependencyDescriptor extends DependencyDescriptor {

        private final String shortcut;

        private final Class<?> requiredType;

        public ShortcutDependencyDescriptor(DependencyDescriptor original, String shortcut, Class<?> requiredType) {
            super(original);
            this.shortcut = shortcut;
            this.requiredType = requiredType;
        }

        @Override
        public Object resolveShortcut(BeanFactory beanFactory) {
            return beanFactory.getBean(this.shortcut, this.requiredType);
        }
    }

}
