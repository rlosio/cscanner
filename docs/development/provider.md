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
        @CScannerParameter(
            value = "key",
            defaultSupplier = NullSupplier.class,
            description = "API key"
        )
        @Nullable String key,
        @CScannerParameter(
            value = "secret",
            defaultSupplier = NullSupplier.class,
            description = "API secret"
        )
        @Nullable String secret
    ) {
        this.key = key;
        this.secret = secret;
    }
}
```

This is very plain and simple and only serves to store the configuration relevant to your cloud provider. This 
configuration will be filled by the system automatically based on the config file. Note that the default parameter value
has to be provided as a class reference to a subclass of `Supplier`. The supplier class can return the default value, 
and a couple of default classes have been provided.

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

You will receive the configuration for your provider as an instance of the configuration class you have specified.
The configuration reader automatically converts the input configuration from YAML, JSON, etc. into the class you have
specified based on your `@CScannerParameter` annotations.

For example:

```java
public class YourCloudProvider implements CloudProvider<YourConfiguration, YourConnection> {
    @Override
    public YourConnection getConnection(
        String name,
        YourConfiguration configuration
    ) {
        return new YourConnection(name, configuration);
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