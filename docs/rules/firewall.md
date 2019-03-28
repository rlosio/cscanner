---
title: Firewall rules
---

# Firewall rules

This section details how to configure the firewall compliance rules.

## Do not permit a certain port to be public

This rule fails if there is a firewall rule allowing access to a certain port from the whole internet (`0.0.0.0/0` or
`::/0`). The configuration is as follows:

```yaml
- type: FIREWALL_PUBLIC_SERVICE_PROHIBITED
  protocol: tcp
  ports:
    - 22
  include:
    - some.*regexp
  exclude:
    - public
```

### protocol

This option specifies the protocol, either as a name or a number. It accepts only a single protocol.

### ports

This option accepts a list of ports that should be checked.

### include

This option accepts a regular expression to match against the security group name. If include is specified, only
the security groups with the specified name will be considered.

Note that each cloud provider implements security groups differently. The [AWS provider](../clouds/aws.md), for example,
will return an ARN. (To Be Fixed)

### exclude

This option lets you exclude certain firewall / security groups from checking, similar to include above. The
exclude rule takes precedence over include.
