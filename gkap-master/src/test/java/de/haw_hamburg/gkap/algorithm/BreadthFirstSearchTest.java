package de.haw_hamburg.gkap.algorithm;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BreadthFirstSearchTest {

    private Graph graph;
    private Graph directedGraph;
    private Map<String, Integer> graphIndexMapFromThree;

    @BeforeEach
    public void before() {
        directedGraph = new MultiGraph(UUID.randomUUID().toString());
        directedGraph.addNode("node1");
        directedGraph.addNode("node2");
        directedGraph.addEdge("e1", "node1", "node2", true);

        graph = new MultiGraph(UUID.randomUUID().toString());

        graph.addNode("node1");
        graph.addNode("node2");
        graph.addNode("node3");
        graph.addNode("node4");
        graph.addNode("node5");
        graph.addNode("nodeX");
        graph.addNode("nodeXX");
        graph.addEdge("eX", "nodeX", "nodeXX");

        graph.addEdge("e1", "node1", "node2");
        graph.addEdge("e2", "node2", "node3");
        graph.addEdge("e3", "node3", "node4");
        graph.addEdge("e4", "node4", "node5");

        graphIndexMapFromThree = new HashMap<String, Integer>();
        graphIndexMapFromThree.put("node2",1);
        graphIndexMapFromThree.put("node5",2);
        graphIndexMapFromThree.put("node4",1);
        graphIndexMapFromThree.put("node3",0);
        graphIndexMapFromThree.put("node1",2);
    }

    /**
     * Eingegebener Graph darf nicht null sein
     */
    @Test
    public void testEx1ceptionOnNullGraph() {
        assertThrows(NullPointerException.class,
                () -> new BreadthFirstSearch(null, "node1", "node2"));
    }

    /**
     * Die Werte der Knoten Ids müssen im Graph vorhanden sein.
     */
    @Test
    public void testAttributesInGraph() {
        assertThrows(NullPointerException.class,
                () -> new BreadthFirstSearch(null, "nodeXXXX1", "node2"));
    }

    /**
     * Die Werte der Knoten Ids müssen im Graph vorhanden sein.
     */
    @Test
    public void testAttributesInGraph2() {
        assertThrows(NullPointerException.class,
                () -> new BreadthFirstSearch(null, "node1", "nodeXXX2"));
    }

    /**
     * Prüfen ob Markierungen Korrekt sind
     */
    @Test
    public void testMarkersCorrect() {
        BreadthFirstSearch bfs = runBfs(graph, "node3", "node1");
        assertEquals(graphIndexMapFromThree, bfs.getMarkerMap());
    }

    /**
     * Es darf kein Weg von nodeX zu node1 gefunden werden
     */
    @Test
    public void testFindNoImpossibleWay() {
        BreadthFirstSearch bfs = runBfs(graph, "nodeX", "node1");
        assertFalse(bfs.foundShortestWay());
    }

    /**
     * Finde einen Weg in Graph wo s = t
     */
    @Test
    public void testFindWayWhenSEqualsT() {
        BreadthFirstSearch bfs = runBfs(graph, "nodeX", "nodeX");
        assertTrue(bfs.foundShortestWay());
        assertEquals(Arrays.asList("nodeX"), bfs.getShortestWay());
        assertEquals(0, bfs.getLengthOfShortestWay());
    }

    /**
     * Teste ob ein weg von node1 zu node5 gefunden wird und ob es in beide Richtungen funktioniert
     */
    @Test
    public void testFindWay() {
        BreadthFirstSearch bfs = runBfs(graph, "node1", "node5");
        List<String> correctWay = Arrays.asList("node1", "node2","node3","node4","node5");
        List<String> way = bfs.getShortestWay();
        assertEquals(correctWay, way);

        bfs = runBfs(graph, "node5", "node1");
        Collections.reverse(correctWay);
        way = bfs.getShortestWay();
        assertEquals(correctWay, way);
    }

    /**
     * Teste ob die Länge eines Weges die Anzahl der Knoten - 1 ist
     */
    @Test
    public void testFindWayLength() {
        BreadthFirstSearch bfs = runBfs(graph, "node1", "node5");
        assertEquals(4, bfs.getLengthOfShortestWay());
    }

    /**
     * Finde einen Weg in einem gerichteten Graph mit 2 Knoten, wo s nach t zeigt
     */
    @Test
    public void tesFindWayInDirectedGraph() {
        BreadthFirstSearch bfs = runBfs(directedGraph, "node1", "node2");
        assertTrue(bfs.foundShortestWay());
    }

    /**
     * Finde keinen Weg in einem gerichteten Graph mit 2 Knoten, wo t nach s zeigt
     */
    @Test
    public void tesFindNoIllegalWayInDirectedGraph() {
        BreadthFirstSearch bfs = runBfs(directedGraph, "node2", "node1");
        assertFalse(bfs.foundShortestWay());
    }

    private BreadthFirstSearch runBfs(Graph g, String s, String t) {
        BreadthFirstSearch bfs = new BreadthFirstSearch(g, s, t);
        bfs.doInitialize();
        while(bfs.doNextStep()) {}
        bfs.doTermination();
        return bfs;
    }
}