# Janos' Cloud Scanner

[![CircleCI](https://img.shields.io/circleci/project/github/janoszen/cscanner.svg)](https://circleci.com/gh/janoszen/cscanner)
[![GitHub Releases](https://img.shields.io/github/release/janoszen/cscanner.svg)](https://github.com/janoszen/cscanner/releases)
![GitHub last commit](https://img.shields.io/github/last-commit/janoszen/cscanner.svg)
![Discord](https://img.shields.io/discord/413306353545773069.svg)

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

