---
title: Writing a Rule
---

# Writing a Rule

!!! warning "The API may change!"
    CScanner is a very young project and the internal API has not yet stabilized. It is recommended that you contact
    the author before starting to write a rule or provider to coordinate efforts.

Writing a rule is a lot less well described than writing a provider as each rule has its very own implementation. For
this example we will stick with an S3 implementation for various cloud providers.

## Writing a rule class

First of all, we are going to write our actual rule class:

```java
public class MyS3Rule implements Rule<S3Connection> {
    public final static String RULE = "S3_MY_RULE";
    //This is a parameter that comes from the configuration
    private final boolean scanContents;
    
    public MyS3Rule(
        boolean scanContents
    ) {
        this.scanContents = scanContents;
    }
    
    @Override
    public List<RuleResult> evaluate(S3Connection s3Connection) {
        //Evaluate rules here and respond with a list of results. These results contain resources and their compliancy
        //status.        
    }
}
```

## Writing a connection interface

Now, if you look at this class you can see that it needs an `S3Connection`. This is an interface that will let you 
retrieve the actual S3 client.

```java
public interface S3Connection extends CloudProviderConnection {
    AmazonS3 getS3Client();
}
```

This connection will be injected to the evaluate call for each connection that needs to be checked. Keep in mind though
that some implementations may be more complicated as the cloud resource may be, for example, bound to a region that
you need to handle.

## Writing a rule builder

In order for the system to be able to construct the rule with the appropriate parameters, you will also need a 
rule builder like so:

```java
public class MyS3RuleBuilder implements RuleBuilder<S3PublicReadProhibitedRule, S3Connection> {
    @Override
    public String getType() {
        return S3PublicReadProhibitedRule.RULE;
    }

    @Override
    public Class<S3Connection> getConnectionType() {
        return S3Connection.class;
    }

    @Override
    public MyS3Rule create(Map<String, Object> options) {
        //return the configured rule here.
    }
}
```

## Writing a plugin

Finally you will need to write a plugin to load the rule. This is quite simple:

```java
public class MyS3Plugin implements Plugin {
    @Override
    public List<RuleBuilder<?, ?>> getSupportedRules() {
        //noinspection unchecked
        return Arrays.asList(
            new MyS3RuleBuilder()
        );
    }
}
```

The plugin will need to be added to the CLI module as there is no automatic plugin discovery at this time.