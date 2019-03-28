package com.opsbears.cscanner.cli;

import com.opsbears.cscanner.aws.AWSPlugin;
import com.opsbears.cscanner.core.Rule;
import com.opsbears.cscanner.core.RuleResult;
import com.opsbears.cscanner.core.ScannerCore;
import com.opsbears.cscanner.exoscale.ExoscalePlugin;
import com.opsbears.cscanner.firewall.FirewallPlugin;
import com.opsbears.cscanner.s3.S3Plugin;
import com.opsbears.cscanner.yaml.YamlPlugin;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

@ParametersAreNonnullByDefault
public class CLIApplication {
    public static void main(String[] argv) {
        ScannerCore scannerCore = new ScannerCore(Arrays.asList(
            new YamlPlugin(argv[0]),
            new S3Plugin(),
            new FirewallPlugin(),
            new AWSPlugin(),
            new ExoscalePlugin()
        ));

        List<RuleResult> results = scannerCore.scan();

        for (RuleResult result : results) {
            System.out.println(
                result.connectionName + "\t" + result.resourceType + "\t" + result.resourceName + "\t" + result.compliancy
            );
        }
    }
}
