# CScanner: A Cloud Security Scanner

[![Documentation](https://img.shields.io/badge/documentation-available-green.svg)](https://cscanner.io)
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

## Things to do

» [Grab the latest release](https://github.com/janoszen/cscanner/releases)

» [Read the documentation](https://cscanner.io)

» [Join the Discord](https://pasztor.at/discord)

» [Support on Patreon](https://pasztor.at/patreon)

## A brief introduction

This application lets you scan one or more cloud accounts for compliance with a certain ruleset. For example, to scan
an AWS account to make sure there are no publicly readable S3 buckets, you can use this config file:

```yaml
---
connections:
  aws-test:
    type: aws
    accessKeyId: ""
    secretAccessKey: ""
  exoscale-test:
    type: exoscale
    key: ""
    secret: ""
rules:
  - type: S3_PUBLIC_READ_PROHIBITED
    include:
      - .*
    exclude:
      - .*public.*
  - type: FIREWALL_PUBLIC_SERVICE_PROHIBITED
    protocol: 6
    ports:
      - 22
    include:
      - .*
    exclude:
      - .*public.*
```

The result of the application will be like this:

```
exoscale-test	s3	opsbears-honeypot-terraform	COMPLIANT
aws-test	s3	elasticbeanstalk-us-east-1-556933211225	COMPLIANT
aws-test	s3	janoszen-access-test	NONCOMPLIANT
```

It is very similar to AWSConfig in its intention, but it is designed from the ground up to support multiple cloud
providers and accounts at once.

## Requirements

In order to run this project you will require at least a Java 8 JRE.

## License

This project is licensed under the Apache License Version 2.0.

