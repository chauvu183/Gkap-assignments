package de.haw_hamburg.gkap.algorithm;

import de.haw_hamburg.gkap.persistence.loader.GraphLoaderUtil;
import de.haw_hamburg.gkap.persistence.loader.GraphStreamGraphReceiver;
import de.haw_hamburg.gkap.persistence.loader.generator.GraphGenerator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KruskalPrimTest {

    private String graphS1 =
            "r,s(rs)::2.0;\n" +
            "r,v(rv)::2.0;\n" +
            "s,t(st)::3.0;\n" +
            "t,u(tu)::6.0;\n" +
            "s,v(sv)::3.0;\n" +
            "t,v(tv)::4.0;\n" +
            "v,u(vu)::5.0;";

    private String graphS2 =
            "r,s(rs)::2.0;\n" +
            "r,v(rv)::2.0;\n" +
            "s,t(st)::3.0;\n" +
            "t,u(tu)::6.0;\n" +
            "s,v(sv)::3.0;\n" +
            "t,v(tv);\n" + //no weight given here!
            "v,u(vu)::5.0;";

    private Graph graph1;
    private Graph graph2;

    @BeforeEach
    public void before() throws IOException {
        graph1 = GraphLoaderUtil.loadGraphFromString(graphS1);
        graph2 = GraphLoaderUtil.loadGraphFromString(graphS2);
    }

    @Test
    public void testSpanningTree() {
        Prim prim = PrimTest.runPrim(graph1);
        Kruskal kruskal = KruskalTest.runKruskal(graph1);

        List<String> expectedEdges = new ArrayList<>(Arrays.asList("st","vu","rv","rs"));
        List<String> actualEdgesPrim = prim.getSpanningTreeEdges().stream().map(e -> e.getAttribute("name", String.class)).collect(Collectors.toList());
        List<String> actualEdgesKruskal = kruskal.getSpanningTreeEdges().stream().map(e -> e.getAttribute("name", String.class)).collect(Collectors.toList());

        //sort collection because we want to ignore order
        Collections.sort(expectedEdges);
        Collections.sort(actualEdgesPrim);
        Collections.sort(actualEdgesKruskal);

        assertEquals(expectedEdges, actualEdgesPrim);
        assertEquals(expectedEdges, actualEdgesKruskal);
    }

    @Test
    public void testDefaultWeightZero() {
        Prim prim = PrimTest.runPrim(graph2);
        Kruskal kruskal = KruskalTest.runKruskal(graph2);
        List<String> expectedEdges = new ArrayList<>(Arrays.asList("tv","vu","rv","rs"));
        List<String> actualEdgesPrim = prim.getSpanningTreeEdges().stream().map(e -> e.getAttribute("name", String.class)).collect(Collectors.toList());
        List<String> actualEdgesKruskal = kruskal.getSpanningTreeEdges().stream().map(e -> e.getAttribute("name", String.class)).collect(Collectors.toList());

        //sort collection because we want to ignore order
        Collections.sort(expectedEdges);
        Collections.sort(actualEdgesPrim);
        Collections.sort(actualEdgesKruskal);

        assertEquals(expectedEdges, actualEdgesPrim);
        assertEquals(expectedEdges, actualEdgesKruskal);
    }

    @Test
    public void testDefaultWeightZeroWeight() {
        Prim prim = PrimTest.runPrim(graph2);
        Kruskal kruskal = KruskalTest.runKruskal(graph2);
        assertEquals(9d, prim.getSpanningTreeWeight());
        assertEquals(9d, kruskal.getSpanningTreeWeight());
    }

    @Test
    public void testSpanningTreeWeight() {
        Prim prim = PrimTest.runPrim(graph1);
        Kruskal kruskal = KruskalTest.runKruskal(graph1);
        assertEquals(12d, prim.getSpanningTreeWeight());
        assertEquals(12d, kruskal.getSpanningTreeWeight());
    }

    @Test
    public void testRandomGraph() {
        Graph graph = new MultiGraph("124test");
        GraphGenerator g = getGenerator(graph);
        g.generate(700, 1000, 12345, false, 20, 20);

        Prim p = PrimTest.runPrim(graph);
        Kruskal k = KruskalTest.runKruskal(graph);

        assertEquals(699, p.getSpanningTreeEdges().size());
        assertEquals(5226, p.getSpanningTreeWeight());

        assertEquals(699, k.getSpanningTreeEdges().size());
        assertEquals(5226, k.getSpanningTreeWeight());
    }

    @Test
    public void testRandomGraphHuge() {
        Graph graph = new MultiGraph("124test");
        GraphGenerator g = getGenerator(graph);
        g.generate(100000, 200000, 12345, false, 20, 20);

        Prim p = PrimTest.runPrim(graph);
        Kruskal k = KruskalTest.runKruskal(graph);

        assertEquals(99999, p.getSpanningTreeEdges().size());
        assertEquals(550334, p.getSpanningTreeWeight());

        assertEquals(99999, k.getSpanningTreeEdges().size());
        assertEquals(550334, k.getSpanningTreeWeight());
    }

    private GraphGenerator getGenerator(Graph graph) {
        GraphStreamGraphReceiver gr = new GraphStreamGraphReceiver(graph);
        return new GraphGenerator(gr);
    }

}