package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class NullSupplier extends AbstractSupplier {
    @Override
    public Object get() {
        return null;
    }
}
