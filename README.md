# Janos' Cloud Scanner

This application lets you scan one or more cloud accounts for compliance with a certain ruleset. For example, to scan
an AWS account to make sure there are no publicly readable S3 buckets, you can use this config file:

```yaml
---
connections:
  aws-s3:
    type: s3
    AWS_ACCESS_KEY_ID: ""
    AWS_SECRET_ACCESS_KEY: ""
rules:
  - type: S3_PUBLIC_READ_PROHIBITED
    connections:
      - aws-s3

```

It is very similar to AWSConfig in its intention, but it is designed from the ground up to support multiple cloud
providers and accounts at once.

Note that at this time it is very early in development, so use at your own risk.