(function($){

    var Renderer = function(canvas){
        var canvas = $(canvas).get(0)
        var ctx = canvas.getContext("2d");
        var particleSystem

        var that = {
            init:function(system){
                //
                // the particle system will call the init function once, right before the
                // first frame is to be drawn. it's a good place to set up the canvas and
                // to pass the canvas size to the particle system
                //
                // save a reference to the particle system for use in the .redraw() loop
                particleSystem = system

                // inform the system of the screen dimensions so it can map coords for us.
                // if the canvas is ever resized, screenSize should be called again with
                // the new dimensions
                particleSystem.screenSize(canvas.width, canvas.height)
                particleSystem.screenPadding(80) // leave an extra 80px of whitespace per side

                // set up some event handlers to allow for node-dragging
                that.initMouseHandling()
            },

            redraw:function(){
                //
                // redraw will be called repeatedly during the run whenever the node positions
                // change. the new positions for the nodes can be accessed by looking at the
                // .p attribute of a given node. however the p.x & p.y values are in the coordinates
                // of the particle system rather than the screen. you can either map them to
                // the screen yourself, or use the convenience iterators .eachNode (and .eachEdge)
                // which allow you to step through the actual node objects but also pass an
                // x,y point in the screen's coordinate system
                //
                ctx.fillStyle = "white"
                ctx.fillRect(0,0, canvas.width, canvas.height)

                particleSystem.eachEdge(function(edge, pt1, pt2){
                    // edge: {source:Node, target:Node, length:#, data:{}}
                    // pt1:  {x:#, y:#}  source position in screen coords
                    // pt2:  {x:#, y:#}  target position in screen coords

                    // draw a line from pt1 to pt2
                    ctx.strokeStyle = "rgba(0,0,0, .333)"
                    ctx.lineWidth = 1
                    ctx.beginPath()
                    ctx.moveTo(pt1.x, pt1.y)
                    ctx.lineTo(pt2.x, pt2.y)
                    ctx.stroke()

                    ctx.font = "bold 11px Arial"
                    ctx.textAlign = "center"
                    ctx.fillStyle = ctx.strokeStyle

                    textX = pt1.x>pt2.x?pt2.x:pt1.x
                    textX += Math.abs(pt2.x-pt1.x)/2

                    textY = pt1.y>pt2.y?pt2.y:pt1.y
                    textY += Math.abs(pt2.y-pt1.y)/2

                    ctx.fillText(edge.label, textX, textY)
                })

                particleSystem.eachNode(function(node, pt){
                    // determine the box size and round off the coords if we'll be
                    // drawing a text label (awful alignment jitter otherwise...)
                    var label = node.label + ':' + node.data.name[0].value
                    var w = ctx.measureText(label||"").width + 6

                    if (!(label||"").match(/^[ \t]*$/)){
                        pt.x = Math.floor(pt.x)
                        pt.y = Math.floor(pt.y)
                    }else{
                        label = null
                    }

                    // clear any edges below the text label
                    // ctx.fillStyle = 'rgba(255,255,255,.6)'
                    // ctx.fillRect(pt.x-w/2, pt.y-7, w,14)


                    ctx.clearRect(pt.x-w/2, pt.y-7, w,14)



                    // draw the text
                    if (label){
                        ctx.font = "bold 11px Arial"
                        ctx.textAlign = "center"

                        // if (node.data.region) ctx.fillStyle = palette[node.data.region]
                        // else ctx.fillStyle = "#888888"
                        ctx.fillStyle = "#000000"

                        // ctx.fillText(label||"", pt.x, pt.y+4)
                        ctx.fillText(label||"", pt.x, pt.y+4)
                    }})
            },

            initMouseHandling:function(){
                // no-nonsense drag and drop (thanks springy.js)
                var dragged = null;

                // set up a handler object that will initially listen for mousedowns then
                // for moves and mouseups while dragging
                var handler = {
                    clicked:function(e){
                        var pos = $(canvas).offset();
                        _mouseP = arbor.Point(e.pageX-pos.left, e.pageY-pos.top)
                        dragged = particleSystem.nearest(_mouseP);

                        if (dragged && dragged.node !== null){
                            // while we're dragging, don't let physics move the node
                            dragged.node.fixed = true
                        }

                        $(canvas).bind('mousemove', handler.dragged)
                        $(window).bind('mouseup', handler.dropped)

                        return false
                    },
                    dragged:function(e){
                        var pos = $(canvas).offset();
                        var s = arbor.Point(e.pageX-pos.left, e.pageY-pos.top)

                        if (dragged && dragged.node !== null){
                            var p = particleSystem.fromScreen(s)
                            dragged.node.p = p
                        }

                        return false
                    },

                    dropped:function(e){
                        if (dragged===null || dragged.node===undefined) return
                        if (dragged.node !== null) dragged.node.fixed = false
                        dragged.node.tempMass = 1000
                        dragged = null
                        $(canvas).unbind('mousemove', handler.dragged)
                        $(window).unbind('mouseup', handler.dropped)
                        _mouseP = null
                        return false
                    }
                }

                // start listening
                $(canvas).mousedown(handler.clicked);

            }

        }
        return that
    }

    $(document).ready(function(){
        var sys = arbor.ParticleSystem(1000, 600, 0.5) // create the system with sensible repulsion/stiffness/friction
        sys.parameters({gravity:true}) // use center-gravity to make the graph settle nicely (ymmv)
        sys.renderer = Renderer("#graphSON") // our newly created renderer will have its .init() method called shortly by sys...

        var canvas = $("#graphSON").get(0);
        var g = JSON.parse(canvas.textContent);

        _.each(g.vertices, function (v) {
            var node = sys.addNode(v.id, v.properties);
            node.label = v.label;
        });

        _.each(g.vertices, function (v) {
            _.mapObject(v.inE, function (value, key) {
                _.each(value, function (o) {
                    var edge = sys.addEdge(v.id, o.outV, o.properties);
                    if (edge) edge.label = this.key;
                }, {value:value, key:key})
            })
            _.mapObject(v.outE, function (value, key) {
                _.each(value, function (i) {
                    var edge = sys.addEdge(i.inV, v.id, i.properties);
                    if (edge) edge.label = this.key;
                }, {value:value, key:key})
            })
        });

    })

})(this.jQuery)