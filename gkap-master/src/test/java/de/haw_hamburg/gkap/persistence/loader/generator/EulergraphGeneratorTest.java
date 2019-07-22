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
import java.util.Random;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

public class EulergraphGeneratorTest {
    private final Random _RND = new Random();
    private final int _MAX = 50, _MIN = 10, _TEST_RUNS = 10;
    private Graph _graph;
    private HashMap<Node, ArrayList<Node>> _NodeStorage;
    private GraphStreamGraphReceiver _graphReceiver;
    private EulergraphGenerator _eulergraphGenerator;
    int _edgeCount, _maxWeightNodes, _maxWeightEdges;

    @BeforeEach
    public void setUp()
    {
        _maxWeightEdges = _RND.nextInt(_MIN)+1;
        _maxWeightNodes = _RND.nextInt(_MIN)+1;
        int seed = _RND.nextInt(_MAX)+1;
        _edgeCount = _RND.nextInt(_MAX * _RND.nextInt(_MIN)+_MIN)+_MIN;
        _NodeStorage = new HashMap<>();

        _graphReceiver = new GraphStreamGraphReceiver(new MultiGraph(UUID.randomUUID().toString()));
        _eulergraphGenerator = new EulergraphGenerator(_graphReceiver);
        _eulergraphGenerator.eulerGenerate(_edgeCount, seed, _maxWeightEdges, _maxWeightNodes, false);
        _graph = _graphReceiver.getGraph();
    }

    @Test
    public void addTargetToMapEG()
    {
        for (int i = 0; i < _TEST_RUNS; i++)
        {
            setUp();

            for (int j = 0; j < _graph.getNodeCount(); j++)
                _eulergraphGenerator.addTargetToMapEG(_NodeStorage, _graph.getNode(j), _graph.getNode(j));
            assertEquals(_NodeStorage.size(), _graph.getNodeCount());
            _NodeStorage.forEach((key, value) -> assertEquals((value.size()+1), _NodeStorage.size()));
        }
    }

    @Test
    public void removeSTFromValueEG()
    {
        for (int i = 0; i < _TEST_RUNS; i++)
        {
            setUp();

            for (int j = 0; j < _graph.getNodeCount(); j++)
                _eulergraphGenerator.addTargetToMapEG(_NodeStorage, _graph.getNode(j), _graph.getNode(j));
            Node source = _NodeStorage.get(_graph.getNode(_RND.nextInt(_graph.getNodeCount()))).get(0);
            Node target = _NodeStorage.get(_graph.getNode(_RND.nextInt(_graph.getNodeCount()))).get(0);

            _eulergraphGenerator.removeSTFromValueEG(_NodeStorage, source, target);

            _NodeStorage.forEach((k, v) ->
            {
                if (k.equals(source) || k.equals(target))
                {
                    assertFalse(v.contains(source));
                    assertFalse(v.contains(target));
                }
                else
                {
                    assertTrue(v.contains(source));
                    assertTrue(v.contains(target));
                }
            });
        }
    }

    @Test
    public void findNodeInTwoLists()
    {
        for (int i = 0; i < _TEST_RUNS; i++) {
            setUp();

            for (int j = 0; j < _graph.getNodeCount(); j++)
                _eulergraphGenerator.addTargetToMapEG(_NodeStorage, _graph.getNode(j), _graph.getNode(j));

            Node n0 = _graph.getNode(0);
            Node n1 = _graph.getNode(_graph.getNodeCount() - 1);
            if (n0 != n1)
                assertNotNull(_eulergraphGenerator.findNodeInTwoLists(_NodeStorage.get(n0), _NodeStorage.get(n1)));
        }
    }

    @Test
    public void eulerGenerate()
    {
        for (int i = 1; i < _TEST_RUNS; i++)
        {
            setUp();

            assertEquals(_graph.getEdgeCount(), _edgeCount);
            for (Edge e: _graph.edges().collect(Collectors.toList()))
            {
                assertTrue( e.getAttribute("weight", Double.class) <= _maxWeightEdges);
                assertTrue(e.getNode0().getAttribute("weight", Double.class) <= _maxWeightNodes);
                assertTrue(e.getNode1().getAttribute("weight", Double.class) <= _maxWeightNodes);
            }
            Stream<Edge> connectedEdgeStream = _graph.getNode(0).edges();
            assertEquals(_graph.getNodeCount(), get_connected_nodes(new ArrayList<>(), connectedEdgeStream.collect(Collectors.toSet())).size());

            for (int j = 0; j < _graph.getNodeCount(); j++)
            {
                assertEquals(0, (_graph.getNode(j).edges().count() % 2));
            }

        }
    }

    @Test
    public void noNotesWithUnevenDegreeOrNoConnections() {
        Graph g = EulergraphGenerator.generateEulerGraph(1000, 12345, 5,5, false);
        assertEquals(0, g.nodes().filter(n -> n.getDegree() == 0 || n.getDegree() % 2 != 0).count());
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