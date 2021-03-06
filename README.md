# SQuaRe Lab - SPLT Coverage

This project is for the coverage measurement in Software Product Line Testing for controlling the software variability.
<br><br>

## Software Variability

A feature is a characteristic or user-visible behavior of a software system. The commonalities and differences of different products can be described in terms of their features.
A software product line consists of single products, where a specific product corresponds to a selection of features.
The idea of software product-line engineering is that we invest some initial work to provide a code basis that later allows us to generate a large number of customized products with relatively low effort.
<br><br>

## What can we do with this?

### It can generate:

- The .exec files that can be read by JaCoCo or Eclemma, contains coverage data divided by each atomic tests(test methods) executed by JUnit.

### It can report:

- Whether the newly performed test covers the newly added code parts.
- The product graph that is hierarchized by its feature set.
<br>

## It works on...

> - Annotation(parameterized)-based-approach
> - Composition-based-approach with Antenna(Java preprocessor)
<br>

## How can we use this?

- Example is under construction...
<br>

## VM Arguments

The 7777 port is used to communicate to the JaCoCo by using RMI.

`-javaagent:[JACOCO_AGENT_PATH]=jmx=true -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=7777 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=localhost`

