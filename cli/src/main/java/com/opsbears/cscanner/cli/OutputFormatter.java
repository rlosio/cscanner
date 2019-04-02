package com.opsbears.cscanner.cli;

import com.opsbears.cscanner.core.RuleResult;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface OutputFormatter {
    String getType();
    String format(List<RuleResult> results);
}
