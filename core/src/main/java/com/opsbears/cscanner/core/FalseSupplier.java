package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class FalseSupplier extends AbstractSupplier {
    @Override
    public Object get() {
        return false;
    }
}
