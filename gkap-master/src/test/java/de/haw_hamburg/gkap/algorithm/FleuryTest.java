package de.haw_hamburg.gkap.algorithm;

import de.haw_hamburg.gkap.persistence.loader.GraphLoaderUtil;
import de.haw_hamburg.gkap.persistence.loader.generator.EulergraphGenerator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class FleuryTest {
    private String graphS1 =
            "a,b;\n" +
                    "a,s;\n" +
                    "a,d;\n" +
                    "a,c;\n" +
                    "b,s;\n" +
                    "c,d;\n" +
                    "d,s;\n" +
                    "d,f;\n" +
                    "s,e;\n" +
                    "s,g;\n" +
                    "s,f;\n" +
                    "e,h;\n" +
                    "h,g;";

    private String connectedG =
            "a,b;\n" +
                    "a,s;\n" +
                    "a,d;\n" +
                    "a,c;\n" +
                    "b,s;\n" +
                    "c,d;\n" +
                    "d,s;\n" +
                    "d,f;\n" +
                    "s,e;\n" +
                    "s,g;\n" +
                    "s,f;\n" +
                    "e,h;\n" +
                    "h,g;\n" +
                    "x,y;\n" +
                    "y,z;\n" +
                    "z,y;";
    private String directedG =
                "#directed;\n" +
                        "a,b;\n" +
                        "a,s;\n" +
                        "a,d;\n" +
                        "a,c;\n" +
                        "b,s;\n" +
                        "c,d;\n" +
                        "d,s;\n" +
                        "d,f;\n" +
                        "s,e;\n" +
                        "s,g;\n" +
                        "s,f;\n" +
                        "e,h;\n" +
                        "h,g;";
    private String notAllEven =
            "a,b;\n" +
                    "a,s;\n" +
                    "a,d;\n" +
                    "a,c;\n" +
                    "a,h;\n" +
                    "b,s;\n" +
                    "c,d;\n" +
                    "d,s;\n" +
                    "d,f;\n" +
                    "s,e;\n" +
                    "s,g;\n" +
                    "s,f;\n" +
                    "e,h;\n" +
                    "h,g;";

    private Graph graph1;
    private Graph notConnectedGraph;
    private Graph directedGraph;
    private Graph notAllEvenNodeGraph;



    @BeforeEach
    public void before() throws IOException {
        graph1 = GraphLoaderUtil.loadGraphFromString(graphS1);
        notConnectedGraph = GraphLoaderUtil.loadGraphFromString(connectedG);
        directedGraph = GraphLoaderUtil.loadGraphFromString(directedG);
        notAllEvenNodeGraph = GraphLoaderUtil.loadGraphFromString(notAllEven);
    }

    /*
    Negative test (First Conditions)
     */
    /**
     * Graph is not connected
     */

    @Test
    public void preFleuryNotConnected() throws IllegalArgumentException{
        assertThrows(IllegalArgumentException.class,()-> runFleury(notConnectedGraph));
    }


    /*
    Graph is directed.
    Return IllegalArgumentException if there is a directed Graph
     */

    @Test
    public void preFleuryUndirected(){
        assertThrows(IllegalArgumentException.class,()-> runFleury(directedGraph));
    }


    /*
    Not all the Nodegrads sind even
     */

    @Test
    public void preFleuryNotEven(){
        assertThrows(IllegalArgumentException.class,()-> runFleury(notAllEvenNodeGraph));
    }

    /*
    Positive Test
     */

    @Test
    public void fleuryEulerCycleTest(){
        Fleury fleuryGraph = runFleury(graph1);
        assertEquals(fleuryGraph.getEulerCircle().size(),graph1.edges().count());
    }

    @Test
    /*
    Test if the last Edge go back to the the StartNode
     */
    public void fleuryCycleSequenceTest(){
        Fleury fleury = runFleury(graph1);

        List<Edge> eulerCircle = fleury.getEulerCircle();
        /*
        Each edge has to lead to annother edge in the eulerTour
         */

//        for (int i = 0; i< eulerCircle.size()-1; i++){
//            List<Node> nodeList = new LinkedList<>();
//            nodeList.add(eulerCircle.get(i).getSourceNode());
//            nodeList.add(eulerCircle.get(i).getTargetNode());
//          assertTrue(nodeList.contains(eulerCircle.get(i+1).getSourceNode()));
//        }

        for (int i = 0; i< eulerCircle.size()-1; i++){
        assertEquals(eulerCircle.get(i).getSourceNode(),eulerCircle.get(i+1).getSourceNode());}

        /*
        last edge lead to start node
         */
//        assertEquals(eulerCircle.get(0).getSourceNode(),eulerCircle.get(eulerCircle.size() - 1).getTargetNode());
    }

    @Test
    public void testRandomized() {
        Random r = new Random();
        for(int i = 0; i < 100 ; i++) {
            Graph g = EulergraphGenerator.generateEulerGraph(r.nextInt(10000)+1, r.nextInt(1000000), r.nextInt(100), r.nextInt(100), r.nextBoolean());
            Fleury fleuryGraph = runFleury(g);
            assertEquals(fleuryGraph.getEulerCircle().size(),g.edges().count());
        }
    }


    public static Fleury runFleury(Graph g) {
        Fleury fleury = new Fleury(g);
        fleury.doInitialize();
        while(fleury.doNextStep()) {}
        fleury.doTermination();
        return fleury;
    }
}


