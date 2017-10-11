// set up SVG for D3
var width = 1000,
    height = 1000,
    colors = d3.scale.category10();

function inputted(value) {
	force.gravity(value);
	restart();
}

function fixall() {
	for (var i=0; i<nodes.length; i++)
		if (nodes[i].fixed == true) 
			nodes[i].fixed = false;
		else nodes[i].fixed = true;
}
function clean() {
	links.splice(0,links.length);
	nodes.splice(0,nodes.length);
	restart();
}

var svg = d3.select('body')
    .append('svg')
    //.attr('oncontextmenu', 'return false;')
    .attr('width', width)
    .attr('height', height);

// set up initial nodes and links
//  - nodes are known by 'id', not by index in array.
//  - reflexive edges are indicated on the node (as a bold black circle).
//  - links are always source < target; edge directions are set by 'left' and 'right'.
var nodes = [],
    links = [];

// init D3 force layout
var force = d3.layout.force()
    .nodes(nodes)
    .links(links)
    .size([width, height])
    .linkDistance(10)
    .linkStrength(0.5)
    .charge(-2000)
    .on('tick', tick)

// define arrow markers for graph links
svg.append('svg:defs').append('svg:marker')
    .attr('id', 'end-arrow')
    .attr('viewBox', '0 -5 10 10')
    .attr('refX', 6)
    .attr('markerWidth', 3)
    .attr('markerHeight', 3)
    .attr('orient', 'auto')
    .append('svg:path')
    .attr('d', 'M0,-5L10,0L0,5')
    .attr('fill', '#000');

// handles to link and node element groups
var path = svg.append('svg:g').selectAll('path'),
    circle = svg.append('svg:g').selectAll('g'),
    tag = svg.append('svg:g').selectAll('text');
// mouse event vars

var selected_node = null;
var select_bridge_word = false;
var select_path = false;

function addNode(name) {
    var node = {
        id: name,
        reflexive: false,
        focused: false,
        fixed: false
    };
    nodes.push(node);
    restart();
}

function focusNode(name) {
	nodes[nodeIndex(name)].focused = true;
	restart();
}

function cleanNode() {
	for (var i = 0; i < nodes.length; i++)
		nodes[i].focused = false;
	restart();
}

function nodeIndex(id) {
    for (var i = 0; i < nodes.length; i++) {
        if (nodes[i].id === id) return i;
    }
    return -1;
}

function linkIndex(srcID, tarID) {
	for (var i = 0; i < links.length; i++) {
		if (links[i].source === nodes[srcID] && links[i].target === nodes[tarID])
			return i;
	}
	return -1;
}

function addLink(src, tar, wgt) {
    var srcIndex = nodeIndex(src);
    var tarIndex = nodeIndex(tar);
    if (srcIndex === -1 || tarIndex === -1) return;
    var link = {
        source: nodes[srcIndex],
        target: nodes[tarIndex],
        left: false,
        right: true,
        weight: wgt,
        selected: false
    }
    links.push(link);
    restart();
}

function selectNode(node) {
	var nodeID = nodeIndex(node);
	if (nodeID != -1)
		selected_node = nodes[nodeID];
	else
		selected_node = null;
	restart();
}

function walkPath(src, tar) {
	var srcIndex = nodeIndex(src);
    var tarIndex = nodeIndex(tar);
    if (srcIndex === -1 || tarIndex === -1) return;
    var linkID = linkIndex(srcIndex, tarIndex);
    if (linkID != -1) {
    	links[linkID].selected = true;
    	selected_node = links[linkID].target;
    }
    restart();
}

function cleanPath() {
	for (var i = 0; i < links.length; i++) links[i].selected = false;
	selected_node = null;
	restart();
}

function dist(link) {
    var deltaX = link.target.x - link.source.x;
    var deltaY = link.target.y - link.source.y;
    return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
}

// update force layout (called automatically each iteration)
function tick() {
    // draw directed edges with proper padding from node centers
    path.attr('d', function(d) {
        var deltaX = d.target.x - d.source.x,
            deltaY = d.target.y - d.source.y,
            dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY),
            normX = deltaX / dist,
            normY = deltaY / dist,
            sourcePadding = d.left ? 17 : 12,
            targetPadding = d.right ? 17 : 12,
            sourceX = d.source.x + (sourcePadding * normX),
            sourceY = d.source.y + (sourcePadding * normY),
            targetX = d.target.x - (targetPadding * normX),
            targetY = d.target.y - (targetPadding * normY);
        return 'M' + sourceX + ',' + sourceY + 'L' + targetX + ',' + targetY;
    });


    circle.attr('transform', function(d) {
        return 'translate(' + d.x + ',' + d.y + ')';
    });

    tag.attr('x', function(d) {
            return (d.source.x + d.target.x) / 2
        })
        .attr('y', function(d) {
            return (d.source.y + d.target.y) / 2
        })
        .text(function(d) {
            if (d.source === selected_node || d.target === selected_node)
                return d.weight;

        });
}

// update graph (called when needed)
function restart() {
    tag = tag.data(links)
    tag.enter().append('svg:text')
        .attr('class', 'linktext')
        .attr('x', function(d) {
            return (d.source.x + d.target.x) / 2
        })
        .attr('y', function(d) {
            return (d.source.y + d.target.y) / 2
        })
        .style('fill', 'red');
	tag.exit().remove();
    // path (link) group
    path = path.data(links);
    // update existing links
    path.classed('selected', function(d) {
        return !d.selected && d.source === selected_node;
    })
    .classed('walked', function(d) {
        return d.selected;
    });
    // add new links
    path.enter().append('svg:path')
        .attr('class', 'link')
        .attr('stroke-width', function(d) {
            return Math.sqrt(d.weight);
        })
        .style('marker-end', function(d) {
            return d.right ? 'url(#end-arrow)' : '';
        });
	path.exit().remove();
    // circle (node) group
    // NB: the function arg is crucial here! nodes are known by id, not by index!
    circle.call(force.drag);
    circle = circle.data(nodes, function(d) {
        return d.id;
    });

    // update existing nodes (reflexive & selected visual states)
    circle.selectAll('circle')
    	.classed('focus', function(d) {
        	return d.focused;
    	})
        .style('fill', function(d) {
            return (d === selected_node) ? d3.rgb(colors(d.id)).brighter().toString() : colors(d.id);
        });
    // add new nodes
    var g = circle.enter().append('svg:g');
    g.append('svg:circle')
        .attr('class', 'node')
        .attr('r', 15)
        .on('mousedown', function(d) {
        	src = tar;
        	tar = d.id;
           	if (d != selected_node)
               	selected_node = d;
            else
                selected_node = null;
            restart();
        });

    // show node IDs
    g.append('svg:text')
        .attr('x', 0)
        .attr('y', 4)
        .attr('class', 'id')
        .style('fill', 'black')
        .text(function(d) {
            return d.id;
        });
	circle.exit().remove();
    // set the graph in motion
    force.start();
}
var src = null;
var tar = null;

// app starts here
restart();