package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface Rule {
     List<Result> evaluate();

     class Result {
         public final String connectionName;
         public final String resourceType;
         public final String resourceName;
         public final Compliancy compliancy;

         public Result(
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
     }

     enum Compliancy {
         COMPLIANT,
         NONCOMPLIANT
     }
}
