package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RuleResult {
    public final String connectionName;
    public final String resourceType;
    public final String resourceName;
    public final Compliancy compliancy;

    public RuleResult(
        String connectionName,
        String resourceType,
        String resourceName,
        Compliancy compliancy
    ) {
        this.connectionName = connectionName;
        this.resourceType = resourceType;
        this.resourceName = resourceName;
        this.compliancy = compliancy;
    }

    public enum Compliancy {
        COMPLIANT,
        NONCOMPLIANT
    }
}
