package de.haw_hamburg.gkap.persistence.loader;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GraphLoaderTest {

    @BeforeEach
    public void before() {

    }

    /**
     * Graph 01 muss korrekt geladen werden
     * @throws RuntimeException
     * @throws IOException
     */
    @Test
    public void testLoadPositive() throws RuntimeException, IOException {
        Graph graph = GraphLoaderUtil.loadGraphFromString(GraphTestData.GRAPH01);
        assertEquals(39, graph.edges().count());
        assertEquals(12, graph.nodes().count());
        assertTrue(graph.edges().allMatch(e -> e.isDirected()));
    }

    /**
     * Graph 02 muss korrekt geladen werden
     * @throws RuntimeException
     * @throws IOException
     */
    @Test
    public void testLoadPositive2() throws RuntimeException, IOException {
        Graph graph = GraphLoaderUtil.loadGraphFromString(GraphTestData.GRAPH02);
        assertEquals(38, graph.edges().count());
        assertEquals(11, graph.nodes().count());
        assertTrue(graph.edges().allMatch(e -> !e.isDirected()));
    }

    /**
     * Prüfe ob Attribute der Knoten gelesen werden
     * @throws RuntimeException
     * @throws IOException
     */
    @Test
    public void testLoadNodeAttribute() throws RuntimeException, IOException {
        Graph graph = GraphLoaderUtil.loadGraphFromString("e:3,f:6.3;");
        assertEquals(3d, graph.getNode("e").getAttribute("weight"));
        assertEquals(6.3d, graph.getNode("f").getAttribute("weight"));
    }

    /**
     * Prüfe ob Namen der Kanten gelesen werden
     * @throws IOException
     * @throws RuntimeException
     */
    @Test
    public void testLoadEdgeName() throws RuntimeException, IOException {
        Graph graph = GraphLoaderUtil.loadGraphFromString("e:5,f(hallo);");
        assertEquals("hallo", graph.getNode("e").getEdgeBetween("f").getAttribute("ui.label"));

        graph = GraphLoaderUtil.loadGraphFromString("e:5,f(hallo)::0;");
        assertEquals("hallo [0]", graph.getNode("e").getEdgeBetween("f").getAttribute("ui.label"));

        graph = GraphLoaderUtil.loadGraphFromString("e:5,f(hallo)::0.24;");
        assertEquals("hallo [0.24]", graph.getNode("e").getEdgeBetween("f").getAttribute("ui.label"));
    }

    /**
     * Testen ob weight korrekt geladen wird
     * @throws RuntimeException
     * @throws IOException
     */
    @Test
    public void testLoadEdgeWeight() throws RuntimeException, IOException {
        Graph graph = GraphLoaderUtil.loadGraphFromString(GraphTestData.GRAPH05);
        assertNull(graph.getNode("v4").getEdgeBetween("v6").getAttribute("weight"));
        assertEquals(3d, graph.getNode("v1").getEdgeBetween("v5").getAttribute("weight"));
    }

    /**
     * Testen ob Fehler in Graph 09 erkannt wird
     * @throws RuntimeException
     * @throws IOException
     */
    @Test
    public void testLoadNegative() throws RuntimeException, IOException {
        assertThrows(RuntimeException.class, () -> GraphLoaderUtil.loadGraphFromString(GraphTestData.GRAPH09));
    }

    /**
     * Leere Datei muss als leerer Graph geladen werden
     * @throws RuntimeException
     * @throws IOException
     */
    @Test
    public void testEmpty() throws RuntimeException, IOException {
        Graph graph = GraphLoaderUtil.loadGraphFromString("");
        assertEquals(0, graph.edges().count());
        assertEquals(0, graph.nodes().count());
    }
}