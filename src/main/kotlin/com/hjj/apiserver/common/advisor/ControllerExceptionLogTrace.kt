package com.hjj.apiserver.common.advisor

import com.hjj.apiserver.common.ErrCode
import com.hjj.apiserver.util.ApiUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory

//@Aspect
class ControllerExceptionLogTrace {
    private val log = LoggerFactory.getLogger(ControllerExceptionLogTrace::class.java)

//    @Around("execution(* com.hjj.apiserver.controller..*(..))")
    fun execute(joinPoint: ProceedingJoinPoint): Any? {
        try {
            return joinPoint.proceed()
        } catch (e: Exception) {
            log.error("[${joinPoint.signature.declaringType.simpleName}] Error request={}, Exception={}", joinPoint.args.toString(), e.stackTrace)
            return ApiUtils.error(ErrCode.ERR_CODE9999)
        }
    }
}