package io.smallrye.opentelemetry.implementation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InterceptionType;
import javax.enterprise.inject.spi.Interceptor;
import javax.enterprise.inject.spi.Prioritized;
import javax.enterprise.util.AnnotationLiteral;
import javax.interceptor.InvocationContext;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.extension.annotations.WithSpan;

public class WithSpanInterceptorBean implements Interceptor<WithSpanInterceptor>, Prioritized {
    private final BeanManager beanManager;

    public WithSpanInterceptorBean(final BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    @Override
    public Set<Annotation> getInterceptorBindings() {
        return Collections.singleton(WithSpanLiteral.INSTANCE);
    }

    @Override
    public boolean intercepts(final InterceptionType type) {
        return InterceptionType.AROUND_INVOKE.equals(type);
    }

    @Override
    public Object intercept(
            final InterceptionType type,
            final WithSpanInterceptor instance,
            final InvocationContext invocationContext)
            throws Exception {

        return instance.span(invocationContext);
    }

    @Override
    public Class<?> getBeanClass() {
        return WithSpanInterceptorBean.class;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public WithSpanInterceptor create(final CreationalContext<WithSpanInterceptor> creationalContext) {
        Bean<?> bean = beanManager.resolve(beanManager.getBeans(OpenTelemetry.class));
        OpenTelemetry openTelemetry = (OpenTelemetry) beanManager.getReference(bean, OpenTelemetry.class, creationalContext);
        return new WithSpanInterceptor(openTelemetry);
    }

    @Override
    public void destroy(
            final WithSpanInterceptor instance,
            final CreationalContext<WithSpanInterceptor> creationalContext) {

    }

    @Override
    public Set<Type> getTypes() {
        return Collections.singleton(this.getBeanClass());
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return Collections.emptySet();
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return Dependent.class;
    }

    @Override
    public String getName() {
        return getBeanClass().getName();
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public boolean isAlternative() {
        return false;
    }

    @Override
    public int getPriority() {
        return 100;
    }

    public static class WithSpanLiteral extends AnnotationLiteral<WithSpan> implements WithSpan {
        public static final WithSpanLiteral INSTANCE = new WithSpanLiteral();

        @Override
        public String value() {
            return null;
        }

        @Override
        public SpanKind kind() {
            return null;
        }
    }
}
