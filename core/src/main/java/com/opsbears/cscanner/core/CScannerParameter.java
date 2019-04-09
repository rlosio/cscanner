package com.opsbears.cscanner.core;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CScannerParameter {
    String value();
    @Nullable
    String description() default "";
    @Nullable
    Class<? extends Supplier<?>> defaultSupplier() default AbstractSupplier.class;
}
