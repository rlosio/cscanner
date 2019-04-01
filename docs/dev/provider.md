---
title: Writing a Provider
---

# Writing a Provider

!!! warning "The API may change!"
    CScanner is a very young project and the internal API has not yet stabilized. It is recommended that you contact
    the author before starting to write a rule or provider to coordinate efforts.

Integrating a new provider is quite a challenging task as you will need to decide which rules you want to support. First
off you start with your provider class:

```java
public class YourCloudProvider implements CloudProvider<YourConfiguration, YourConnection> {
    
}
```

Additionally, you will want to look into each of the rules what additional classes you can `implement`, such as
`S3CloudProvider<YourConfiguration, YourConnection>` or `FirewallCloudProvider<YourConfiguration, YourConnection>`.
These interfaces are used when walking through the cloud accounts to determine which cloud supports which rules.

## The configuration class

Next up, you will need to write your configuration class:

```java
public class YourConfiguration {
    @Nullable
    public final String key;
    @Nullable
    public final String secret;

    public YourConfiguration(
        @Nullable String key,
        @Nullable String secret
    ) {
        this.key = key;
        this.secret = secret;
    }
}
```

This is very plain and simple and only serves to store the configuration relevant to your cloud provider. This
configuration class will be filled in your cloud provider class later.

## The connection class

The third thing you need to create is a connection class like so:

```java
public class YourConnection implements CloudProviderConnection {
    private final String name;
    private final YourConfiguration configuration;

    public YourConnection(
        String name,
        YourConfiguration configuration
    ) {
        this.name = name;

        this.configuration = configuration;
    }

    @Override
    public String getConnectionName() {
        return name;
    }
}
```

Based on what your cloud provider will support, you will need to implement additional classes, such as `S3Connection`
and you will need to implement the methods relevant to that interface too, such as returning an S3 client based on
your configuration.

## Reading the configuration

Now we have reached the point to implement some functions in our cloud provider class, most importantly reading
the configuration.

You will receive the configuration for your provider as a `Map<String, Object>`. Keep in mind that the configuration
comes from a YAML, JSON, etc file so it is your responsibility to vet the content and make sure you have the
correct data types.

For example

```java
public class YourCloudProvider implements CloudProvider<YourConfiguration, YourConnection> {
    @Override
    public YourConnection getConnection(
        String name,
        Map<String, Object> configuration
    ) {
        YourConfiguration configObject = new YourConfiguration(
            (String) configuration.getOrDefault("key", null),
            (String) configuration.getOrDefault("secret", null)
        );

        return new YourConnection(name, configObject);
    }
}
```

## Writing a plugin

Finally, you will need to write a plugin. This is quite simple:

```java
@ParametersAreNonnullByDefault
public class YourPlugin implements Plugin {
    @Override
    public List<CloudProvider<?, ?>> getCloudProviders() {
        return Arrays.asList(
            new YourCloudProvider()
        );
    }
}
```

You will need to add this client to the `cli` module as at this time there is no automatic plugin discovery.