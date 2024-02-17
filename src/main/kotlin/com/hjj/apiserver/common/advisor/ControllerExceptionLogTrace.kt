package com.hjj.apiserver.common.advisor

import com.hjj.apiserver.common.ApiError
import com.hjj.apiserver.common.ErrCode
import mu.two.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint

// @Aspect
class ControllerExceptionLogTrace {
    private val log = KotlinLogging.logger {}

//    @Around("execution(* com.hjj.apiserver.controller..*(..))")
    fun execute(joinPoint: ProceedingJoinPoint): Any? {
        try {
            return joinPoint.proceed()
        } catch (e: Exception) {
            log.error(
                "[${joinPoint.signature.declaringType.simpleName}] Error request={}, Exception={}",
                joinPoint.args.toString(),
                e.stackTrace,
            )
            return ApiError(ErrCode.ERR_CODE9999)
        }
    }
}
