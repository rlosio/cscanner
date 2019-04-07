---
title: Amazon Web Services
---

# Amazon Web Services

[Amazon Web Services](https://aws.amazon.com), or AWS, is a popular cloud provider and is supported by cscanner.
The configuration does as follows:

```yaml
connection-name:
  type: aws
  accessKeyId: ""
  secretAccessKey: ""
  sessionToken: ""
  profile: ""
``` 

Each of the parameters is optional. If they are not provided, the default is to fall back to the AWS client default
behavior, such as reading options from environment variables or credentials files.

## Rules

The AWS provider currently supports the following rules:

- [Firewall](../rules/firewall.md)
- [S3](../rules/s3.md)