package de.haw_hamburg.gkap.persistence.loader.generator;

import de.haw_hamburg.gkap.persistence.loader.GraphStreamGraphReceiver;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.MultiGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class GraphGeneratorTest {

    private final int _GENERATE_TESTS = 10;
    private final int _MAX = 50;
    private GraphGenerator _graphGenerator;
    private GraphStreamGraphReceiver _graphReceiver;
    private Random _rnd;

    @BeforeEach
    public void setUp() {
        _graphReceiver = new GraphStreamGraphReceiver(new MultiGraph(UUID.randomUUID().toString()));
        _graphGenerator = new GraphGenerator(_graphReceiver);
        _rnd = new Random();
    }


    @Test
    public void generate()
    {
        int nodeCount, edgeCount, seed, max_weight_nodes, max_weight_edges;

        for (int i = 1; i < _GENERATE_TESTS; i++)
        {
            seed = _rnd.nextInt(_MAX) + 1;
            max_weight_nodes = _rnd.nextInt(_MAX) + 1;
            max_weight_edges = _rnd.nextInt(_MAX) + 1;
            nodeCount = i * _MAX;
            edgeCount = i * nodeCount;


            Graph graph = _graphReceiver.getGraph();
            graph.clear(); //der graph receiver nutzt immer den gleichen graph. deswegen einmal leeren vor jedem durchlauf
            _graphGenerator.generate(nodeCount,edgeCount, seed, true, max_weight_edges, max_weight_nodes);

            assertEquals(graph.getEdgeCount(), edgeCount);
            assertNotNull(graph.getEdge((nodeCount - 1)));
            assertTrue(graph.getEdge((edgeCount - 1)).isDirected());

            for (Edge e: graph.edges().collect(Collectors.toList()))
            {
                assertTrue( e.getAttribute("weight", Double.class) <= max_weight_edges);
                assertTrue(e.getNode0().getAttribute("weight", Double.class) <= max_weight_nodes);
                assertTrue(e.getNode1().getAttribute("weight", Double.class) <= max_weight_nodes);
            }
            Stream<Edge> connectedEdgeStream = graph.getNode(0).edges();
            assertEquals(nodeCount, get_connected_nodes(new ArrayList<>(), connectedEdgeStream.collect(Collectors.toSet())).size());
        }
    }

    private ArrayList get_connected_nodes(ArrayList<Node> nodes, Collection<Edge> edges)
    {
        for (Edge edge: edges)
        {
            Node n0 = edge.getNode0();
            Node n1 = edge.getNode1();

            if (!nodes.contains(n0)){
                nodes.add(n0);
                Stream<Edge> connectedEdgeStream = n0.edges();
                get_connected_nodes(nodes,connectedEdgeStream.collect(Collectors.toSet()));
            }

            if (!nodes.contains(n1)){
                nodes.add(n1);
                Stream<Edge> connectedEdgeStream = n1.edges();
                get_connected_nodes(nodes,connectedEdgeStream.collect(Collectors.toSet()));
            }
        }
        return nodes;
    }

}