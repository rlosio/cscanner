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

## Sending a pull request

Once you're done, please [send a pull request](https://github.com/janoszen/cscanner/pulls) with your changes.
The integration may take some time as documentation may need to be written and a proper QA environment has to be set up.