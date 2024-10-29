package com.hfsolution.app.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {

    private static final DecimalFormat format = new DecimalFormat("#,##0.00"); // Change format if needed

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            gen.writeString(format.format(value));
        }
    }
}
