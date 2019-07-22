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

//       B-----8-----C-----7-----D
//      /|          / \          |\
//     / |         /   \         | \
//    4  |        2     \        |  9
//   /   |       /       \       |   \
//  /    |      /         \      |    \
// A    11     I           4    14     E
//  \    |    / \           \    |    /
//   \   |   /   \           \   |   /
//    8  |  7     6           \  |  10
//     \ | /       \           \ | /
//      \|/         \           \|/
//       H-----1-----G-----2-----F

public class KruskalTest {
    private String graphS1 =
        "a,b(ab)::4.0;\n"+
            "a,h(ah)::8.0;\n" +
            "b,h(bh)::11.0;\n" +
            " b,c(bc)::8.0;\n" +
            "c,d(cd)::7.0;\n" +
            "c,i(ci)::2.0;\n" +
            "c,f(cf)::4.0;\n" +
            "i,h(ih)::7.0;\n" +
            "i,g(ig)::6.0;\n" +
            "g,f(gf)::2.0;\n" +
            "d,f(df)::14.0;\n" +
            "d,e(de::9.0;\n" +
            "e,f(ef::10.0;";

    private String graphS2 =
            "a,b(ab)::4.0;\n"+
                    "a,h(ah)::8.0;\n" +
                    "b,h(bh)::11.0;\n" +
                    " b,c(bc);\n" + // mo weight
                    "c,d(cd)::7.0;\n" +
                    "c,i(ci)::2.0;\n" +
                    "c,f(cf)::4.0;\n" +
                    "i,h(ih)::7.0;\n" +
                    "i,g(ig)::6.0;\n" +
                    "g,f(gf)::2.0;\n" +
                    "d,f(df);\n" + // no weight
                    "d,e(de)::9.0;\n" +
                    "e,f(ef)::10.0;";

    private Graph graph1;
    private Graph graph2;

    @BeforeEach
    public void before() throws IOException {
        graph1 = GraphLoaderUtil.loadGraphFromString(graphS1);
        graph2 = GraphLoaderUtil.loadGraphFromString(graphS2);
    }

    @Test
    public void testSpanningTree() {
        Kruskal kruskal = runKruskal(graph1);
        List<String> expectedEdges = new ArrayList<String>(List.of("ab","ah","ih","ci","cd","cf","gf","de"));

        List<String> actualEdges = kruskal.getSpanningTreeEdges().stream().map(
                e-> e.getAttribute("name",String.class)).collect(Collectors.toList());

        //sort collection because we want to ignore order
        Collections.sort(expectedEdges);
        Collections.sort(actualEdges);

        assertEquals(expectedEdges, actualEdges);
    }

    @Test
    public void testDefaultWeightZero() {
        Kruskal kruskal = runKruskal(graph2);
        List<String> expectedEdges = new ArrayList<String>(List.of("ab","bc","cf","ci","de","df","gf","ih"));
        List<String> actualEdges = new ArrayList<>() ;
        kruskal.getSpanningTreeEdges().forEach(e -> actualEdges.add(e.getAttribute("name", String.class)));

        //sort collection because we want to ignore order
        Collections.sort(expectedEdges);
        Collections.sort(actualEdges);

        assertEquals(expectedEdges, actualEdges);
    }

    @Test
    public void testDefaultWeightZeroWeight() {
        Kruskal kruskal = runKruskal(graph2);
        assertEquals(28d, kruskal.getSpanningTreeWeight());
    }

    @Test
    public void testSpanningTreeWeight() {
        Kruskal kruskal = runKruskal(graph1);
        assertEquals(43d, kruskal.getSpanningTreeWeight());
    }


    public static Kruskal runKruskal(Graph g) {
        Kruskal kruskal = new Kruskal(g);
        kruskal.doInitialize();
        while(kruskal.doNextStep()) {}
        kruskal.doTermination();
        return kruskal;
    }
}
