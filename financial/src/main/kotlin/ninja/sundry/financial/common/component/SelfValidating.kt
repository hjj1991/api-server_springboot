package ninja.sundry.financial.common.component

import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validation
import jakarta.validation.Validator

abstract class SelfValidating<T> {
    val validator: Validator

    init {
        val buildDefaultValidatorFactory = Validation.buildDefaultValidatorFactory()
        this.validator = buildDefaultValidatorFactory.validator
    }

    protected fun validateSelf() {
        val violations: Set<ConstraintViolation<SelfValidating<T>>> = validator.validate(this)

        if (violations.isEmpty()) {
            throw ConstraintViolationException(violations)
        }
    }
}
