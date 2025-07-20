package com.hjj.apiserver.adapter.out.persistence.financial.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Converter
class FloatArrayConverter : AttributeConverter<FloatArray, ByteArray> {

    override fun convertToDatabaseColumn(attribute: FloatArray?): ByteArray? {
        if (attribute == null) {
            return null
        }
        val buffer = ByteBuffer.allocate(attribute.size * 4).order(ByteOrder.LITTLE_ENDIAN)
        for (value in attribute) {
            buffer.putFloat(value)
        }
        return buffer.array()
    }

    override fun convertToEntityAttribute(dbData: ByteArray?): FloatArray? {
        if (dbData == null) {
            return null
        }
        val buffer = ByteBuffer.wrap(dbData).order(ByteOrder.LITTLE_ENDIAN)
        val floatArray = FloatArray(dbData.size / 4)
        for (i in floatArray.indices) {
            floatArray[i] = buffer.float
        }
        return floatArray
    }
}
