package de.haw_hamburg.gkap.persistence.writer;

import de.haw_hamburg.gkap.persistence.loader.GraphLoaderUtil;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GraphWriterTest {

    private Graph directedGraph;

    @BeforeEach
    public void before() throws IOException {
        String input = "#directed;\n" +
                "node1,node2:0::6;\n" +
                "node1,node1(hello);\n" +
                "node1,node3:1.5(mellow)::4;\n" +
                "node6:5,node5:0.004;\n" +
                "node4:1;\n";
        directedGraph = GraphLoaderUtil.loadGraphFromString(input);
    }

    @Test
    public void testPositiveDirected() throws Exception {
        String correctOutput =
                "#directed;\n" +
                        "node1,node2:0::6;\n" +
                        "node1,node1(hello);\n" +
                        "node1,node3:1.5(mellow)::4;\n" +
                        "node6:5,node5:0.004;\n" +
                        "node4:1;\n";
        String output = persistGraphToString(directedGraph);
        assertEquals(correctOutput, output);
    }

    /**
     * Teste ob ein graph als ungerichtet gespeichert wird, wenn eine kante ungerichtet
     * @throws IOException
     */
    @Test
    public void testPositiveUndirected() throws Exception {
        String correctOutput =
                "node1,node2:0::6;\n" +
                        "node1,node1(hello);\n" +
                        "node1,node3:1.5(mellow)::4;\n" +
                        "node6:5,node5:0.004;\n" +
                        "node3:1.5,node4:1;\n";
        directedGraph.addEdge("undirected_edge", "node3", "node4", false);
        String output = persistGraphToString(directedGraph);
        assertEquals(correctOutput, output);
    }

    @Test
    public void testEmpty() throws Exception {
        String correctOutput = "";
        String output = persistGraphToString(new MultiGraph(UUID.randomUUID().toString()));
        assertEquals(correctOutput, output);
    }

    private String persistGraphToString(Graph graph) throws Exception {
        StringWriter stringWriter = new StringWriter();
        SimpleGraphWriter writer = new SimpleGraphWriter(stringWriter);
        GraphStreamGraphPersistor graphPersistor = new GraphStreamGraphPersistor(writer, graph);
        graphPersistor.persist();
        stringWriter.close();
        return stringWriter.toString();
    }
}