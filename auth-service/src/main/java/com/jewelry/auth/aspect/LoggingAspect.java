package com.jewelry.auth.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    private static final String CORRELATION_ID_KEY = "traceId";

    @Pointcut("within(com.jewelry.auth.controller..*) || within(com.jewelry.auth.service.impl..*)")
    public void applicationPackagePointcut() {
    }

    @Around("applicationPackagePointcut()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String correlationId = setCorrelationId();
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
        } finally {
            MDC.remove(CORRELATION_ID_KEY);
        }
    }

    private String setCorrelationId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String correlationId = null;
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            correlationId = request.getHeader("X-Correlation-ID");
        }
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        MDC.put(CORRELATION_ID_KEY, correlationId);
        return correlationId;
    }
}
