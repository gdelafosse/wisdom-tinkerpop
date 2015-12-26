# Wisdom TinkerPop

This project is an extension for Wisdom Framework, to allow the integration of TinkerPop framework.

> It works only if the pull request https://github.com/apache/incubator-tinkerpop/pull/186 is accepted
> Waiting for it, you can checkout and compile https://github.com/gdelafosse/incubator-tinkerpop/tree/osgify.

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

Thanks to a cfg file, you can instanciate a graph and expose it as a service.
For example, add a file called org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph-default.cfg into the instances folder, to instantiate an empty in-memory graph.
Or add a file called org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jGraph-default.cfg to instantiate a Neo4j graph.
The cfg file must contain the properties supported by the chosen GraphFactory implementation. Notice that the property _gremlin.graph_ is not required because the name of the file is used to specify which graph implementation to use.

As a result, your IPojo components or Wisdom controllers can be injected the graph as a service :
````
@Requires
private Graph graph;
````

## Under the hood

The wisdom-tinkerpop extension replaces the TinkerPop pluggabilty by a more dynamic system levering the OSGI features.
The TinkerPop GraphFactory implementations searches in the classpath for a class with name is equal to ${gremlin.graph}.
As the bundle can be deployed and removed dynamically, and as the classloading system is different in a OSGI container, the wisdom-tinkerpop extension works differently :
- it listens for bundle coming and leaving the platform.
- it detects bundle that contain a TinkerPop graph factory.
- it registers a ManageServiceFactory for each graph factory classes found, that will act as a Graph Factory.
- when a configuration arrives, the corresponding ManageServiceFactory opens a Graph and registers it as an OSGI service.
- when the configuration is removed, the graph is closed and the related service is unregistered.
- when a bundle that contains the TinkerPop graph factory implementation is stopped removed, any registered Graph is closed and unregistered.


