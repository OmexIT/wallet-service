package com.logispin.wallet.infrastructure;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

  private static final String CORRELATION_ID = "correlationId";
  private static final String CLASS = "class";
  private static final String METHOD = "method";

  @Around("execution(* com.logispin.wallet..*.*(..))")
  public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
    HttpServletRequest request = null;
    var correlationId = UUID.randomUUID().toString();

    try {
      request =
          ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
      request.setAttribute(CORRELATION_ID, correlationId);
    } catch (IllegalStateException e) {
      // No request available, proceed without setting the attribute
    }

    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    String[] parameterNames = methodSignature.getParameterNames();
    Object[] parameterValues = joinPoint.getArgs();
    Map<String, Object> parameters = combineParameters(parameterNames, parameterValues);

    log.info(
        "Entering method",
        StructuredArguments.keyValue("correlationId", correlationId),
        StructuredArguments.keyValue(
            "class", joinPoint.getSignature().getDeclaringType().getSimpleName()),
        StructuredArguments.keyValue("method", joinPoint.getSignature().toShortString()),
        StructuredArguments.keyValue("params", parameters));

    var startTime = System.currentTimeMillis();
    Object result;

    try {
      result = joinPoint.proceed();
    } catch (Exception e) {
      log.error(
          "Exception in method",
          StructuredArguments.keyValue("correlationId", correlationId),
          StructuredArguments.keyValue(
              CLASS, joinPoint.getSignature().getDeclaringType().getSimpleName()),
          StructuredArguments.keyValue(METHOD, joinPoint.getSignature().toShortString()),
          StructuredArguments.keyValue("exception", e));
      throw e;
    }

    var endTime = System.currentTimeMillis();

    log.info(
        "Exiting method",
        StructuredArguments.keyValue(CORRELATION_ID, correlationId),
        StructuredArguments.keyValue(
            CLASS, joinPoint.getSignature().getDeclaringType().getSimpleName()),
        StructuredArguments.keyValue(METHOD, joinPoint.getSignature().toShortString()),
        StructuredArguments.keyValue("result", result),
        StructuredArguments.keyValue("duration", endTime - startTime));

    return result;
  }

  private Map<String, Object> combineParameters(String[] parameterNames, Object[] parameterValues) {
    Map<String, Object> parameters = new HashMap<>();
    for (var i = 0; i < parameterNames.length; i++) {
      parameters.put(parameterNames[i], parameterValues[i]);
    }
    return parameters;
  }
}
