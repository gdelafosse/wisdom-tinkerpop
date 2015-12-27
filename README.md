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

## Selectionning a graph instance

Any property added in the graph configuration is added as a service property. So you can filter the required Graph you need this way :
````
@Requires(filter ="(my.prop=my.value)")
private Graph graph;
````

To standardize the way to get a Graph, wisdom-tinkerpop proposes to set the property _wisdom.tinkerpop.graphid_ (WisdomTinkerPopConstans.GRAPH_ID) to select a Graph instance.
If it's not set, wisdom-tinkerpop sets this property to the corresponding OSGI service pid and also in the graph configuration.
As a result, you will select a graph this way :
````
@Requires(filter ="(wisdom.tinkerpop.graphId=mygraph)")
private Graph graph;
````

## Example

The wisdom-tinkerpop extension comes with a sample that show how simple it makes developing a graph application so simple.
The example adapts the TinkerPop Modern graph described in the [TinkerPop introduction](http://tinkerpop.apache.org/docs/3.1.0-incubating/#intro).:
- by creating a cfg file called org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph-modern.cfg, containing :
````
wisdom.tinkerpop.graphid=modern;
````
- and by implementing an IPojo component that creates the Modern graph edges and vertices :
````
@Component
@Instantiate
public class ModernGraph
{
    @Requires(filter = "(wisdom.tinkerpop.graphid=modern)")
    private Graph g;

    @Validate
    public void validate()
    {
        final Vertex marko = g.addVertex(T.id, 1, T.label, "person", "name", "marko", "age", 29);
        final Vertex vadas = g.addVertex(T.id, 2, T.label, "person", "name", "vadas", "age", 27);
        final Vertex lop = g.addVertex(T.id, 3, T.label, "software", "name", "lop", "lang", "java");
        final Vertex josh = g.addVertex(T.id, 4, T.label, "person", "name", "josh", "age", 32);
        final Vertex ripple = g.addVertex(T.id, 5, T.label, "software", "name", "ripple", "lang", "java");
        final Vertex peter = g.addVertex(T.id, 6, T.label, "person", "name", "peter", "age", 35);
        marko.addEdge("knows", vadas, T.id, 7, "weight", 0.5d);
        marko.addEdge("knows", josh, T.id, 8, "weight", 1.0d);
        marko.addEdge("created", lop, T.id, 9, "weight", 0.4d);
        josh.addEdge("created", ripple, T.id, 10, "weight", 1.0d);
        josh.addEdge("created", lop, T.id, 11, "weight", 0.4d);
        peter.addEdge("created", lop, T.id, 12, "weight", 0.2d);
    }
};
````

Visiting the page http://localhost:9000/graphs/modern, you will see a view a the graph :
![Wisdom TinkerPop Modern graph view](screenshots/wisdom-tinkerpop-modern.png?raw=true "Wisdom TinkerPop Modern graph view")