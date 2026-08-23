package com.zupu.zupu.aspect;

import com.zupu.zupu.security.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class FamilyLogAspect {

    @Pointcut("execution(* com.zupu.zupu.service..*Service*.*(..))")
    public void servicePointcut() {
    }

    @Before("servicePointcut()")
    public void beforeAdvice(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        LoginUser user = currentUser();
        if (user != null) {
            log.info("[AOP] 用户 {} 执行 {}", user.getUsername(), methodName);
        } else {
            log.info("[AOP] 执行 {}", methodName);
        }
    }

    @AfterReturning(pointcut = "servicePointcut()", returning = "result")
    public void afterReturningAdvice(JoinPoint joinPoint, Object result) {
        log.info("[AOP] {} 执行成功", joinPoint.getSignature().getName());
    }

    private LoginUser currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof LoginUser user) {
            return user;
        }
        return null;
    }
}
