package com.hjj.apiserver.util

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValueOfEnumValidator::class])
annotation class EnumValue(
    val enumClass: KClass<out Enum<*>>,
    val message: String = "",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
    val ignoreCase: Boolean = false
)

class ValueOfEnumValidator: ConstraintValidator<EnumValue, Enum<*>> {
    private lateinit var enumValues: Array<out Enum<*>>

    override fun initialize(constraintAnnotation: EnumValue) {
        val enumClass = constraintAnnotation.enumClass.java
        enumValues = enumClass.enumConstants
            ?: throw IllegalArgumentException("$enumClass must be an enum type.")
    }

    override fun isValid(value: Enum<*>?, context: ConstraintValidatorContext): Boolean {
        if (value == null) {
            return true
        }

        return enumValues.contains(value)
    }
}