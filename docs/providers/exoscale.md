---
title: Exoscale
---

# Exoscale

[Exoscale](https://exoscale.com) is a European/Swiss IaaS cloud provider and is supported by cscanner. Under the hood
it implements a CloudStack-like API.

The configuration goes like this:

```yaml
  exoscale-test:
    type: exoscale
    key: ""
    secret: ""
```

Note, that `cloudstack.ini` is currently not supported.


## Rules

The Exoscale provider supports the following rule sets:

- [Firewall](../rules/firewall.md)
- [S3](../rules/s3.md)
