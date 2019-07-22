package de.haw_hamburg.gkap.algorithm;

import de.haw_hamburg.gkap.persistence.loader.GraphLoaderUtil;
import de.haw_hamburg.gkap.persistence.loader.GraphStreamGraphReceiver;
import de.haw_hamburg.gkap.persistence.loader.generator.GraphGenerator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

class PrimTest {

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
        Prim prim = runPrim(graph1);
        Set<String> expectedEdges = new HashSet<>(Arrays.asList("st","vu","rv","rs"));
        Set<String> actualEdges = prim.getSpanningTreeEdges().stream().map(e -> e.getAttribute("name", String.class)).collect(Collectors.toSet());

        assertEquals(expectedEdges, actualEdges);
    }

    @Test
    public void testDefaultWeightZero() {
        Prim prim = runPrim(graph2);
        Set<String> expectedEdges = new HashSet<>(Arrays.asList("tv","vu","rv","rs"));
        Set<String> actualEdges = prim.getSpanningTreeEdges().stream().map(e -> e.getAttribute("name", String.class)).collect(Collectors.toSet());

        assertEquals(expectedEdges, actualEdges);
    }

    @Test
    public void testDefaultWeightZeroWeight() {
        Prim prim = runPrim(graph2);
        assertEquals(9d, prim.getSpanningTreeWeight());
    }

    @Test
    public void testSpanningTreeWeight() {
        Prim prim = runPrim(graph1);
        assertEquals(12d, prim.getSpanningTreeWeight());
    }

    @Test
    public void testRandomGraph() {
        Graph graph = new MultiGraph("124test");
        GraphGenerator g = getGenerator(graph);
        g.generate(700, 1000, 12345, false, 20, 20);
        Prim p = runPrim(graph);
        assertEquals(699, p.getSpanningTreeEdges().size());
        assertEquals(5226, p.getSpanningTreeWeight());
    }

    @Test
    public void testRandomGraphHuge() {
        Graph graph = new MultiGraph("124test");
        GraphGenerator g = getGenerator(graph);
        g.generate(100000, 200000, 12345, false, 20, 20);

        Prim p = runPrim(graph);
        System.out.println(p.getRuntime());
        assertEquals(99999, p.getSpanningTreeEdges().size());
        assertEquals(550334, p.getSpanningTreeWeight());
    }

    private GraphGenerator getGenerator(Graph graph) {
        GraphStreamGraphReceiver gr = new GraphStreamGraphReceiver(graph);
        return new GraphGenerator(gr);
    }

    public static Prim runPrim(Graph g) {
        Prim prim = new Prim(g);
        prim.doInitialize();
        while(prim.doNextStep()) {}
        prim.doTermination();
        return prim;
    }


}