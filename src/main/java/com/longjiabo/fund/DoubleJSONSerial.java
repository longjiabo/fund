package com.longjiabo.fund;

import com.fasterxml.jackson.databind.SerializerProvider;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.text.DecimalFormat;

@JsonComponent
public class DoubleJSONSerial extends JsonSerializer<Double> {

    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (null == value) {
            //write the word 'null' if there's no value available
            gen.writeNull();
        } else {
            final String pattern = "#.####";
            //final String pattern = "###,###,##0.00";
            final DecimalFormat myFormatter = new DecimalFormat(pattern);
            final String output = myFormatter.format(value);
            gen.writeNumber(output);
        }
    }
}

