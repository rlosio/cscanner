package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface ScannerCoreFactory {
    ScannerCore create(List<RuleConfiguration> rules);
}
