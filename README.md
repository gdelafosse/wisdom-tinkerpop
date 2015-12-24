# Wisdom TinkerPop

This project is an extension for Wisdom Framework, to allow the integration of TinkerPop framework.

## Installation

Add the following dependency to your `pom.xml` file:

````
<dependency>
  <groupId>org.wisdom-framework.tinkerpop</groupId>
  <artifactId>wisdom-tinkerpop</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
````

and the TinkerPop bundles.

````
<dependency>
    <groupId>org.apache.tinkerpop</groupId>
    <artifactId>gremlin-osgi-deps</artifactId>
    <version>${tinkerpop.version}</version>
</dependency>

<dependency>
    <groupId>org.apache.tinkerpop</groupId>
    <artifactId>gremlin-core</artifactId>
    <version>${tinkerpop.version}</version>
</dependency>

<dependency>
    <groupId>org.apache.tinkerpop</groupId>
    <artifactId>gremlin-groovy</artifactId>
    <version>${tinkerpop.version}</version>
</dependency>
````

Then add the Tinkerpop provider bundle. For example, if you use the TinkerPop in-memory graph implementation :

````
<dependency>
    <groupId>org.apache.tinkerpop</groupId>
    <artifactId>tinkergraph-gremlin</artifactId>
    <version>${tinkerpop.version}</version>
</dependency>
````

## Instantiating a graph

Thanks to a cfg file, we can instanciate a graph and expose it as a service.
For example, add a file called osg.wisdom.tinkerpop-default.cfg into the instances folder, to instanciate an empty in-memory graph :
````
wisdom.tinkerpop.name=default
gremlin.graph=org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
````

- wisdom.tinkerpop.name : is a wisdom-tinkerpo specific property to give a name to the graph.
- gremlin.graph : is a TinkerPop specific property to specify the GraphFactory.
- then the cfg file must contain the properties supported by the chosen GraphFactory implementation.

As a result, your IPojo components or Wisdom controllers can be injected the graph as a service :
````
@Requires(filter="(wisdom.tinkerpop.name=default)")
private Graph graph;
````

## Under the hood

The wisdom-tinkerpop extension replaces the TinkerPop pluggabilty by a more dynamic system levering the OSGI features.
The TinkerPop GraphFactory implementations searches in the classpath for a class with name is equal to ${gremlin.graph}.
As the bundle can be deployed and removed dynamically, and as the classloading system is different in a OSGI container, the wisdom-tinkerpop extension works differently :
- it listens for bundle coming and leaving the platform.
- it maintains a set of classes present in the bundle.
- when a configuration arrives, it finds a loads the specified ${gremlin.graph} class from the TinkerPop implementation bundle.
- it instanciate the graph given the configuration and exposes the graph a service.
- when the configuration is removed, the graph is closed and the related service is unregistered.

