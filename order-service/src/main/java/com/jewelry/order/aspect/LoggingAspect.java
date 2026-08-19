package com.jewelry.order.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    private static final String CORRELATION_ID_KEY = "traceId";

    @Pointcut("within(com.jewelry.order.controller..*) || within(com.jewelry.order.service.impl..*)")
    public void applicationPackagePointcut() {
    }

    @Around("applicationPackagePointcut()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String correlationId = MDC.get(CORRELATION_ID_KEY);
        if (correlationId == null) {
            correlationId = "unknown";
        }

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.info("[TRACE_ID={}] {}.{}() started", correlationId, className, methodName);
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;
            log.info("[TRACE_ID={}] {}.{}() completed in {}ms", correlationId, className, methodName, elapsedTime);
            return result;
        } catch (Throwable e) {
            long elapsedTime = System.currentTimeMillis() - start;
            log.error("[TRACE_ID={}] {}.{}() failed after {}ms with Exception={}",
                    correlationId, className, methodName, elapsedTime, e.getClass().getSimpleName());
            throw e;
        }
    }
}
