package com.opsbears.cscanner.firewall;

import com.opsbears.cscanner.core.ScannerCoreFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public interface TestFirewallClientFactory extends Supplier<TestFirewallClient> {
    ScannerCoreFactory getScannerCore();
}
