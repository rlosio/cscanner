package com.opsbears.cscanner.core;

import org.testng.annotations.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;


@ParametersAreNonnullByDefault
public class ConfigurationConverterTest {
    @Test
    public void testStringConversion() {
        //Setup
        ConfigurationConverter converter = new ConfigurationConverter();
        Map<String, Object> testData = new HashMap<>();
        testData.put("value", "Hello world!");

        //Execute
        StringTarget target = converter.convert(testData, StringTarget.class);

        //Assert
        assertEquals("Hello world!", target.value);
    }

    @Test
    public void testIntConversion() {
        //Setup
        ConfigurationConverter converter = new ConfigurationConverter();
        Map<String, Object> testData = new HashMap<>();
        testData.put("value", 1);

        //Execute
        StringTarget target = converter.convert(testData, StringTarget.class);

        //Assert
        assertEquals("1", target.value);
    }

    @Test
    public void testPatternConversion() {
        //Setup
        ConfigurationConverter converter = new ConfigurationConverter();
        Map<String, Object> testData = new HashMap<>();
        testData.put("value", ".*world.*");

        //Execute
        PatternTarget target = converter.convert(testData, PatternTarget.class);

        //Assert
        assertTrue(target.value.matcher("Hello world!").matches());
        assertFalse(target.value.matcher("Hello foo!").matches());
    }


    @Test
    public void testListConversion() {
        //Setup
        ConfigurationConverter converter = new ConfigurationConverter();
        Map<String, Object> testData = new HashMap<>();
        testData.put("value", Collections.singletonList("Hello world!"));

        //Execute
        ListTarget target = converter.convert(testData, ListTarget.class);

        //Assert
        assertEquals("Hello world!", target.value.get(0));
    }

    @Test
    public void testDeepConversion() {
        //Setup
        ConfigurationConverter converter = new ConfigurationConverter();
        Map<String, Object> testData = new HashMap<>();
        testData.put("value", Collections.singletonList(".*world.*"));

        //Execute
        DeepConversionTarget target = converter.convert(testData, DeepConversionTarget.class);

        //Assert
        assertTrue(target.value.get(0).matcher("Hello world!").matches());
    }

    @Test
    public void testDeepConversionWithMap() {
        //Setup
        ConfigurationConverter converter = new ConfigurationConverter();
        Map<String, Object> testData = new HashMap<>();
        Map<String, String> inner = new HashMap<>();
        inner.put("test", ".*world.*");
        testData.put("value", inner);

        //Execute
        DeepConvertionWithMapTarget target = converter.convert(testData, DeepConvertionWithMapTarget.class);

        //Assert
        assertTrue(target.value.get("test").matcher("Hello world!").matches());
    }

    public static class StringTarget {
        public final String value;

        public StringTarget(
            @CScannerParameter("value")
            String value
        ) {
            this.value = value;
        }
    }

    public static class PatternTarget {
        public final Pattern value;

        public PatternTarget(
            @CScannerParameter("value")
            Pattern value
        ) {
            this.value = value;
        }
    }

    public static class ListTarget {
        public final List<String> value;

        public ListTarget(
            @CScannerParameter("value")
            List<String> value
        ) {
            this.value = value;
        }
    }

    public static class DeepConversionTarget {
        public final List<Pattern> value;

        public DeepConversionTarget(
            @CScannerParameter("value")
            List<Pattern> value
        ) {
            this.value = value;
        }
    }

    public static class DeepConvertionWithMapTarget {
        public final Map<String, Pattern> value;

        public DeepConvertionWithMapTarget(
            @CScannerParameter("value")
            Map<String, Pattern> value
        ) {
            this.value = value;
        }
    }
}
