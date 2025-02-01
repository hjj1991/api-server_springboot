package ninja.sundry.financial.adapter.input.grpc

import io.grpc.Status
import org.springframework.grpc.server.exception.GrpcExceptionHandler
import org.springframework.stereotype.Component


@Component
class DefaultGrpcExceptionHandler : GrpcExceptionHandler {

    override fun handleException(exception: Throwable): Status {
        return when (exception) {
            is IllegalArgumentException -> Status.INVALID_ARGUMENT.withDescription(exception.message).withCause(exception)
            is IllegalStateException -> Status.FAILED_PRECONDITION.withDescription(exception.message)
            is NullPointerException -> Status.INTERNAL.withDescription("Unexpected null value encountered.")
            else -> Status.UNKNOWN.withDescription("Unknown error occurred.")
        }
    }
}
