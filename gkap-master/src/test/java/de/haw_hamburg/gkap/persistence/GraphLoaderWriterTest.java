package de.haw_hamburg.gkap.persistence;

import de.haw_hamburg.gkap.persistence.loader.GraphLoaderUtil;
import de.haw_hamburg.gkap.persistence.loader.GraphTestData;
import de.haw_hamburg.gkap.persistence.writer.GraphStreamGraphPersistor;
import de.haw_hamburg.gkap.persistence.writer.SimpleGraphWriter;
import org.graphstream.graph.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class GraphLoaderWriterTest {

    @BeforeEach
    public void before() {

    }


    @Test
    public void testLoadPositive2() throws RuntimeException, Exception {
        Graph graph = GraphLoaderUtil.loadGraphFromString(GraphTestData.GRAPH_ALL);
        String s = persistGraphToString(graph);
        Graph deserialized = GraphLoaderUtil.loadGraphFromString(s);
        String s2 = persistGraphToString(deserialized);
        assertEquals(GraphTestData.GRAPH_ALL, s2);
        assertEquals(s, s2);
    }

    private String persistGraphToString(Graph graph) throws Exception {
        StringWriter stringWriter = new StringWriter();
        GraphStreamGraphPersistor graphPersistor = new GraphStreamGraphPersistor(new SimpleGraphWriter(stringWriter), graph);
        graphPersistor.persist();
        stringWriter.close();
        return stringWriter.toString();
    }
}