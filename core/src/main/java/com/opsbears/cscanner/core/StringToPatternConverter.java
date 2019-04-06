package com.opsbears.cscanner.core;

import com.opsbears.webcomponents.typeconverter.FixedTypeConverter;
import com.opsbears.webcomponents.typeconverter.TypeConversionFailedException;
import com.opsbears.webcomponents.typeconverter.TypeConverter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@ParametersAreNonnullByDefault
public class StringToPatternConverter implements FixedTypeConverter {
    @Override
    public Class[] getInputTypes() {
        return new Class[] {
            String.class
        };
    }

    @Override
    public Class[] getOutputTypes() {
        return new Class[] {
            Pattern.class
        };
    }

    @Override
    public Object convert(
        Object input,
        Class outputType
    ) throws TypeConversionFailedException {
        try {
            return Pattern.compile((String) input);
        } catch (Exception e) {
            throw new TypeConversionFailedException(e.getMessage(), e);
        }
    }
}
