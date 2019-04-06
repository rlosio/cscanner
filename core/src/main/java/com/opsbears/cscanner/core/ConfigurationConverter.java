package com.opsbears.cscanner.core;

import com.opsbears.webcomponents.typeconverter.TypeConversionFailedException;
import com.opsbears.webcomponents.typeconverter.TypeConverterChain;
import com.opsbears.webcomponents.typeconverter.builtin.DefaultTypeConverterChain;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

//todo this needs splitting
@ParametersAreNonnullByDefault
class ConfigurationConverter {
    private final TypeConverterChain typeConverter =
        DefaultTypeConverterChain.defaultChain().withAddedConverter(new StringToPatternConverter());

    <T> T convert(Map<String, Object> sourceParameters, Class<T> targetClass) {
        List<List<String>> validKeySets = new ArrayList<>();
        for (Constructor constructor : targetClass.getConstructors()) {
            Set<String> parameterNames = new TreeSet<>(sourceParameters.keySet());
            Parameter[] parameters = constructor.getParameters();
            boolean allParametersAreValid = true;
            List<String> validKeys = new ArrayList<>();
            List<Object> parameterValues = new ArrayList<>();
            boolean validConstructor = true;
            int parameterNumber = 0;
            for (Parameter parameter : parameters) {
                CScannerParameter annotation = parameter.getAnnotation(CScannerParameter.class);
                if (annotation == null) {
                    validConstructor = false;
                    break;
                }
                String parameterName = annotation.value();
                Object value = null;
                if (sourceParameters.containsKey(parameterName)) {
                    value = sourceParameters.get(parameterName);
                    parameterNames.remove(parameterName);

                    if (value instanceof Map) {
                        value = processMap((Map) value, constructor.getGenericParameterTypes()[parameterNumber]);
                    } else if (value instanceof Collection) {
                        value = processCollection((Collection) value, constructor.getGenericParameterTypes()[parameterNumber]);
                    } else {
                        try {
                            value = typeConverter.convert(value, parameter.getType());
                        } catch (TypeConversionFailedException e) {
                            //Cannot convert type
                            allParametersAreValid = false;
                        }
                    }
                } else {
                    try {
                        value = annotation.defaultSupplier().newInstance().get();
                    } catch (InstantiationException | IllegalAccessException e) {
                        //We can't instantiate it
                        allParametersAreValid = false;
                    }
                }

                parameterValues.add(value);
                validKeys.add(parameterName + ":" + parameter.getType().getSimpleName());
                parameterNumber++;
            }
            if (!parameterNames.isEmpty()) {
                allParametersAreValid = false;
            }
            if (validConstructor) {
                validKeySets.add(validKeys);
                if (allParametersAreValid) {
                    try {
                        //noinspection unchecked
                        return (T) constructor.newInstance(parameterValues.toArray());
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        //todo log instantiation failure.
                    }
                }
            }
        }
        String parametersProvided = "[" +sourceParameters.keySet().stream().map(sourceParameterKey -> sourceParameterKey + ":" + sourceParameters.get(sourceParameterKey).getClass().getSimpleName()).collect(Collectors.joining(",")) + "]";
        String parametersExpected = validKeySets
            .stream()
            .map(validKeys -> "[" + String.join(",", validKeys) + "]")
            .collect(Collectors.joining(","));
        throw new RuntimeException("Failed to parse parameters into " + targetClass.getSimpleName() + ". Parameters provided: " + parametersProvided + ", parameters expected: " + parametersExpected + " (automatic type conversion may have failed)");
    }

    private <T> T processMap(Map input, Type outputType) {
        Class output = null;
        List<Type> typeArguments = new ArrayList<>();
        if (outputType instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) outputType).getRawType();
            typeArguments.addAll(Arrays.asList(((ParameterizedType) outputType).getActualTypeArguments()));
            if (!(typeArguments.get(0) instanceof Class) || !(typeArguments.get(1) instanceof Class)) {
                throw new TypeConversionFailedException();
            }

            if (rawType instanceof Class) {
                output = (Class) rawType;
            } else {
                throw new TypeConversionFailedException();
            }
        } else if (outputType instanceof Class) {
            output = (Class) outputType;
        }

        if (Map.class.isAssignableFrom(output)) {
            Map target = new HashMap();
            //Go through all items and perform a deep type conversion.
            for (Object mapKey : ((Map) input).keySet()) {
                if (!(mapKey instanceof String)) {
                    mapKey = typeConverter.convert(mapKey, String.class);
                }
                String mapKeyString = (String) mapKey;

                Object entry = input.get(mapKey);
                if (entry instanceof Map) {
                    target.put(mapKeyString, processMap((Map) entry, typeArguments.get(1)));
                } else if (entry instanceof Collection) {
                    //noinspection unchecked
                    target.put(mapKeyString, processCollection((Collection) entry, typeArguments.get(1)));
                } else {
                    target.put(mapKeyString, typeConverter.convert(entry, ((Class<?>)typeArguments.get(1))));
                }
            }
            //noinspection unchecked
            return (T) target;
        } else {
            //Convert map into object using recursion
            //noinspection unchecked
            return (T) convert((Map<String, Object>) input, output);
        }

    }

    private <T> T processCollection(Collection input, Type outputType) {
        Class output = null;
        List<Type> typeArguments = new ArrayList<>();
        if (outputType instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) outputType).getRawType();
            typeArguments.addAll(Arrays.asList(((ParameterizedType) outputType).getActualTypeArguments()));
            if (!(typeArguments.get(0) instanceof Class)) {
                throw new TypeConversionFailedException();
            }

            if (rawType instanceof Class) {
                output = (Class) rawType;
            } else {
                throw new TypeConversionFailedException();
            }
        } else if (outputType instanceof Class) {
            output = (Class) outputType;
        }
        //noinspection ConstantConditions
        if (Collection.class.isAssignableFrom(output)) {
            List target = new ArrayList();
            Type actualTypeParameter = typeArguments.get(0);
            for (Object entry : input) {
                if (actualTypeParameter instanceof Class<?>) {
                    if (entry instanceof Map) {
                        //Map inside of a list
                        //noinspection unchecked
                        target.add(processMap((Map)entry, (Class)actualTypeParameter));
                    } else if (entry instanceof Collection) {
                        //List inside of a list
                        //noinspection unchecked
                        target.add(processCollection((List)entry, (Class)actualTypeParameter));
                    } else {
                        //Other things inside of a list
                        //noinspection unchecked
                        target.add(typeConverter.convert(entry, ((Class<?>)actualTypeParameter)));
                    }
                } else {
                    //We don't know how to deal with non-class types
                    throw new TypeConversionFailedException();
                }
            }
            //noinspection unchecked
            return (T) target;
        } else {
            //Problem: we cannot convert a list into an object
            throw new TypeConversionFailedException();
        }
    }
}
