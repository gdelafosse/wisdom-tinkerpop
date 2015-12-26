package org.wisdom.tinkerpop.view;

import java.util.Arrays;

import org.apache.felix.ipojo.annotations.Requires;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Controller;
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
    @View("graph-list")
    private Template graphList;

    @Requires(optional = true)
    private Graph[] graphs;

    /**
     * The action returning the graph list.
     *
     * @return the graph list page
     */
    @Route(method = HttpMethod.GET, uri = "/graphs")
    public Result list() {
        return ok(render(graphList, "graphs", Arrays.stream(graphs).map(g -> new GraphView(g)).toArray()));
    }
}
