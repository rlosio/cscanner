---
title: DigitalOcean
---

# DigitalOcean

[DigitalOcean](https://www.digitalocean.com/) (DO) is a low cost VM provider with various services.

The configuration does as follows:

```yaml
connection-name:
  type: digitalocean
  apiToken: ""
  spacesKey: ""
  spacesSecret: ""
``` 

The `apiToken` is used to interact with the regular API, while `spacesKey` and `spacesSecret` are used to interact with
the object storage called "spaces".

## Rules

The DO provider currently supports the following rules:

- [Firewall](../rules/firewall.md)
- [S3](../rules/s3.md)

## Developemnt notes

DigitalOcean has a couple of non-trivial quirks. This section serves to document them for the future. Keep in mind that
these are anecdotal notes and may / may no longer apply.

### Object storage quirks

- The object storage implementation does not accept object names starting with a slash (`/`) for `putObject`. Objects
  must be specified without a starting slash.
- The object storage requires the content type and content length to be specified for `putObject`, otherwise a bad
  request error will be returned with no further explanation.
  
### Firewall quirks

- At least one firewall rule must be supplied when a firewall is created. Firewalls with no rules in them are not
  allowed. However, a rule with no sources/destinations is valid. That's why a dummy firewall rule is supplied in tests.
- While firewall creation accepts `null` as a port value, the update does not. This causes issues when updating an
  existing rule.
- Even though the documentation states that `all` is a valid value for ports, it is in fact not. The right
  value is `1-65535`.