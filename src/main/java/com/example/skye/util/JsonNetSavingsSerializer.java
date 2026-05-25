package com.example.skye.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Serializes report net savings: zero as {@code 0}, non-zero with two decimals.
 */
public class JsonNetSavingsSerializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            gen.writeNumber(0);
            return;
        }
        gen.writeRawValue(value.setScale(2, RoundingMode.HALF_UP).toPlainString());
    }
}
