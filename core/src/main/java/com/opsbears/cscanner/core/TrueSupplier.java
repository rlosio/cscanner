package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TrueSupplier extends AbstractSupplier {
    @Override
    public Object get() {
        return true;
    }
}
