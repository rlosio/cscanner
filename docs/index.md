# CScanner: A Cloud Security Scanner

[![GitHub Releases](https://img.shields.io/github/release/janoszen/cscanner.svg)](https://github.com/janoszen/cscanner/releases)
[![GitHub](https://img.shields.io/github/license/janoszen/cscanner.svg)](https://github.com/janoszen/cscanner/blob/master/LICENSE)
[![Discord](https://img.shields.io/discord/413306353545773069.svg)](https://pasztor.at/discord)
[![CircleCI](https://img.shields.io/circleci/project/github/janoszen/cscanner.svg)](https://circleci.com/gh/janoszen/cscanner)
[![GitHub last commit](https://img.shields.io/github/last-commit/janoszen/cscanner.svg)](https://github.com/janoszen/cscanner)
[![GitHub top language](https://img.shields.io/github/languages/top/janoszen/cscanner.svg)](https://github.com/janoszen/cscanner)
[![GitHub repo size](https://img.shields.io/github/repo-size/janoszen/cscanner.svg)](https://github.com/janoszen/cscanner)
[![GitHub issues](https://img.shields.io/github/issues/janoszen/cscanner.svg)](https://github.com/janoszen/cscanner/issues)
[![GitHub pull requests](https://img.shields.io/github/issues-pr/janoszen/cscanner.svg)](https://github.com/janoszen/cscanner/pulls)
[![GitHub stars](https://img.shields.io/github/stars/janoszen/cscanner.svg?style=social)](https://github.com/janoszen/cscanner)
[![Twitter Follow](https://img.shields.io/twitter/follow/janoszen.svg?style=social)](https://twitter.com/janoszen)

This utility is intended to check your cloud configuration for compliance with your companies rules in an automated
fashion, not unlike AWS config.

For example, if you want to make sure that your port 22 is never open to the world, across all your cloud providers,
you could do something like this:

```yaml
connections:
  # Configure your connections here
rules:
  - type: FIREWALL_PUBLIC_SERVICE_PROHIBITED
    protocol: "tcp"
    ports:
      - 22
```

You would then get a report detailing all your security groups across all your cloud providers and if they
are compliant or are violating the rules.

## Downloading

You can grab [one of the releases](https://github.com/janoszen/cscanner/releases) from GitHub.

## Running

To run the cscanner, simply point it to your config file:

```
java -jar cscanner.jar your-config-file.yaml
```

Make sure you have at least Java 8 to run this application. Note that you can use the `-h` or `--help` option to get a 
full list of possible filtering and output options.

## Configuring

The cloud scanner is configured using YAML files. The yaml files have two sections: `connections` and `rules`. The
layout looks roughly like this:

```yaml
---
connections:
  # Cloud provider connections go here
rules:
  # Compliance rules go here
```

In `connections` you configure your various cloud accounts, such as this:

```yaml
exoscale-test:
  type: exoscale
  key: ""
  secret: ""
```

The respective options for each cloud provider are documented in their documentation. The connection name can then 
referenced with the rules.

The next section is the `rules` section, which you can specify your rules in:

```yaml
- type: FIREWALL_PUBLIC_SERVICE_PROHIBITED
  protocol: tcp
  ports:
    - 22
```

Each rule has two universal parameters: `type` to specify the rule type and `connections` which you can use to
limit the rule to only certian connections. The default is to use all connections.

If a certain cloud provider doesn't support a specific functionality, that cloud provider will be simply skipped for
the specified rule.

### Includes

The configuration supports includes. These includes can either be local files, or http/https URLs. Include files can
include other files.

For connections they work as follows:

```yaml
---
connections:
  include:
    - config-file-1.yaml
    - https://example.com/config-file-2.yaml
  exoscale-test:
    type: exoscale
    key: ""
    secret: ""
rules:
  # ...
```

In other words you have one include key and you need to list all files you want to load.

For rules it works a little different:

```yaml
connections:
  # ...
rules:
  - include: config-file-1.yaml
  - include: https://example.com/config-file-2.yaml
  - type: FIREWALL_PUBLIC_SERVICE_PROHIBITED
    protocol: tcp
    ports:
      - 22
```

Note that both for connections and rules the target config file only needs to have a list of connections / rules, NOT 
the `connections` or `rules` key.

!!! note
    At this time there is no way to restrict included rules to a certain resource name as the resource filtering is done
    on a per-rule basis.

## Supported cloud providers

Currently the following cloud providers are supported:

- [Amazon Web Services](clouds/aws.md)
- [Exoscale](clouds/exoscale.md)

## Supported rules

Currently the following rule sets are supported:

- [Firewall](rules/firewall.md)
- [S3](rules/s3.md)

## Compiling

If you want to compile the utility from source, you will need [Maven](https://maven.org). Using Maven and at least
a JDK compatible with Java 8 you should be able to simply run `mvn package` to compile the source code and then find
the compiled binaries in `cli/target`.
