package com.imooc.mall.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 打印请求和响应信息
 */
@Aspect
@Component
public class WebLogAspect {
    //生成loger类
    private final Logger log = LoggerFactory.getLogger(WebLogAspect.class);
//10.增加一个拦截点AOP
    @Pointcut("execution(public * com.imooc.mall.controller..*.*(..))")
    public void webLog(){

    }
//  10.提供请求参数
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint){
        //收到请求,记录请求内容  请求到来所作的事情
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        log.info("URL: " + request.getRequestURI().toString());
        log.info("HTTP_METHOD: " + request.getMethod());
        log.info("IP: " + request.getRemoteAddr());
        log.info("CLASS_METHOD: " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        log.info("ARGS: " + Arrays.toString(joinPoint.getArgs()));
    }
//  11.返回的时候也要拦截  返回参数res 拦截点webLog  返回UserController返回对象
    @AfterReturning(returning = "res",pointcut = "webLog()")
    public void doAfterReturning(Object res)throws Exception{
        //处理完请求,返回内容
        log.info("RESPONSE: " + new ObjectMapper().writeValueAsString(res));
    }
}
