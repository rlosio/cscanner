package com.opsbears.cscanner.core;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
public class RuleResult {
    public final String connectionName;
    public final String resourceType;
    @Nullable
    public final String resourceRegion;
    public final String resourceName;
    public final Compliancy compliancy;
    public final List<Violation> violations;

    public RuleResult(
        String connectionName,
        String resourceType,
        @Nullable
        String resourceRegion,
        String resourceName,
        Compliancy compliancy,
        @Nullable
        List<Violation> violations
    ) {
        this.connectionName = connectionName;
        this.resourceType = resourceType;
        this.resourceRegion = resourceRegion;
        this.resourceName = resourceName;
        this.compliancy = compliancy;
        if (violations == null) {
            this.violations = Collections.emptyList();
        } else {
            this.violations = Collections.unmodifiableList(violations);
        }
    }

    public enum Compliancy {
        COMPLIANT,
        NONCOMPLIANT
    }

    public static class Violation {
        @Nullable
        public final String subresource;
        public final String description;

        public Violation(
            @Nullable
            String subresource,
            String description
        ) {
            this.subresource = subresource;
            this.description = description;
        }
    }
}
