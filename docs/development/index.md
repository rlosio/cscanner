---
title: Contributing to CScanner
---

# Contributing to CScanner

First of all, thank you for your interest in helping out CScanner! Before you jump in, please keep in mind that
CScanner is a very young project and the APIs have not yet stabilized. Before you jump in and write some code,
[make sure to open an issue with your proposal](https://github.com/janoszen/cscanner) or
[jump on Discord](https://pasztor.at/discord) so you can receive adequate support for doing so.

## What to contribute

There is a great need for cloud provider implementations as well as new rule sets. However, please refrain (for now) 
from contributing on-premises providers such as VMWare, because there is no affordable infrastructure to run CScanner
automated tests against. 

## Setting up your development environment.

In order to write code for CScanner you will need three things:

1. A Java 8 JDK. Any will do, but OpenJDK is preferred.
2. Maven. This is our dependency manager.
3. An account with the cloud provider you want to work with.

It is also recommended that you use an IDE as Java is not the easiest language to work with if you don't have one.
The project is built with IntelliJ IDEA, but any will do.

## Building the project

The project can be built with a simple `mvn package`. However, keep in mind that some tests depend on a cloud provider
configuration to be available, which will be documented at a later stage. Please open an issue or go on Discord to
receive some help in setting this up. (Yes, this will be improved later on.)

## Running tests

The tests in this project are written with [TestNG](https://testng.org/). The simplest way to run them is via Maven:
`mvn test`. However, you may wish to integrate them with your IDE. For this purpose the file `testng.xml` is provided
in the repository.

If you want to run tests that do a live fire exercise against a cloud account (e.g. Exoscale, AWS) you will have
to provide credentials for these accounts. This can be done using the following environment variables:

- `EXOSCALE_KEY`
- `EXOSCALE_SECRET`
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`

Additionally you can provide the `TEST_RESOURCE_PREFIX` environment variable to create resources with fixed names. This
is not strictly necessary as the tests will do their best to clean up after themselves, but may be helpful nonetheless. 

## Writing tests

If you want to write unit tests, go ahead, write them as you usually do. However, integration tests are a bit more
complicated. Due to how Maven works all integration tests are located in the `test` module. The test suites generally
implement a test for a certain rule and data providers supply the different implementations for testing.
This will make it easy to add a new provider, but may make it a bit unusual at first. It is recommended that you take a 
look at the current implementations for a good template.

## Sending a pull request

Once you're done, please [send a pull request](https://github.com/janoszen/cscanner/pulls) with your changes.
The integration may take some time as documentation may need to be written and a proper QA environment has to be set up.