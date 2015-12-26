package org.wisdom.tinkerpop.view;

import java.util.Arrays;
import java.util.Collection;

import org.apache.felix.ipojo.annotations.Requires;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.Parameter;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.annotations.View;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;
import org.wisdom.api.templates.Template;

/**
 * A controller that displays graph list and graph details
 */
@Controller
public class GraphController extends DefaultController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphController.class);

    @View("graph-list")
    private Template graphList;

    @View("graph-view")
    private Template graphView;

    @Requires(optional = true)
    private Graph[] graphs;

    private final BundleContext bundleContext;

    public GraphController(final BundleContext bundleContext)
    {
        this.bundleContext = bundleContext;
    }

    /**
     * The action returning the graph list.
     *
     * @return the graph list page
     */
    @Route(method = HttpMethod.GET, uri = "/graphs")
    public Result list()
    {
        return ok(render(graphList, "graphs", Arrays.stream(graphs).map(g -> new GraphView(g)).toArray()));
    }

    /**
     * The action returning the graph view.
     *
     * @return the graph detail page
     */
    @Route(method = HttpMethod.GET, uri = "/graph/{id}")
    public Result view(@Parameter("id") String id)
    {
        Graph g = findGraph(id);
        if (g == null)
        {
            return notFound();
        }
        else
        {
            return ok(render(graphView, "graph", new GraphView(g)));
        }
    }

    Graph findGraph(String id)
    {
        try
        {
            Collection<ServiceReference<Graph>> sr = bundleContext.getServiceReferences(Graph.class, "(id=" + id);
            if (sr.isEmpty())
            {
                return null;
            }
            else
            {
                return bundleContext.getService(sr.stream().findFirst().get());
            }
        }
        catch (InvalidSyntaxException e)
        {
            LOGGER.error("Fail to find Graph service {}.", id);
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }
}
