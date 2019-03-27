package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface Rule<CONNECTION extends CloudProviderConnection> {
    List<RuleResult> evaluate(CONNECTION connection);
}
