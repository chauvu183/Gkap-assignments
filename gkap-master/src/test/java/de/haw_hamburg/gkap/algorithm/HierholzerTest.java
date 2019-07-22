package de.haw_hamburg.gkap.algorithm;

import de.haw_hamburg.gkap.persistence.loader.GraphLoaderUtil;
import de.haw_hamburg.gkap.persistence.loader.generator.EulergraphGenerator;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class HierholzerTest {

    private Graph g1, g2, g3, g4, g5;

    @BeforeEach
    public void setup() throws IOException {
        String s1 = "n1,n2;\n" +
                "n1,n5;\n" +
                "n2,n5;";
        g1 = GraphLoaderUtil.loadGraphFromString(s1);

        String s2 = "v4,s;\n" +
                "v5,v6;\n" +
                "v4,v1;\n" +
                "v2,v4;\n" +
                "v6,v3;\n" +
                "v7,s;\n" +
                "v1,v3;\n" +
                "v8,v2;\n" +
                "v6,v4;\n" +
                "v5,v2;\n" +
                "v8,v7;\n" +
                "v2,v6;";

        g2 = GraphLoaderUtil.loadGraphFromString(s2);

        String twoPartitions = "v1,v2;\n" +
                "v5,v6;\n" +
                "v4,v1;\n" +
                "v2,v4;\n" +
                "v6,v3;\n" +
                "v5,v3;";
        g3 = GraphLoaderUtil.loadGraphFromString(twoPartitions);

        String oneNodeNotConnected = "n1,n2;\n" +
                "n1,n5;\n" +
                "n7;\n" +
                "n2,n5;";
        g4 = GraphLoaderUtil.loadGraphFromString(oneNodeNotConnected);

        String unevenDegree = "v1,v2;\n" +
                "v4,v1;\n" +
                "v4,v2;\n" +
                "v4,v6;";
        g5 = GraphLoaderUtil.loadGraphFromString(unevenDegree);
    }

    @Test
    public void testG1() {
        Hierholzer hierholzer = Hierholzer.run(g1);
        List<Element> tour = hierholzer.getEulerCircle();

        assertTrue(hierholzer.containsEulerTour());
        EulerTourValidationUtil.validateEulerTour(g1, tour);
    }

    @Test
    public void testG2() {
        Hierholzer hierholzer = Hierholzer.run(g2);
        List<Element> tour = hierholzer.getEulerCircle();

        assertTrue(hierholzer.containsEulerTour());
        EulerTourValidationUtil.validateEulerTour(g2, tour);


    }

    @Test
    public void testOneNodeNotConnected() {
        assertThrows(RuntimeException.class,() -> Hierholzer.run(g4));
    }

    @Test
    public void testTwoPartitions() {
        Hierholzer hierholzer = Hierholzer.run(g3);
        List<Element> tour = hierholzer.getEulerCircle();

        assertFalse(hierholzer.containsEulerTour());
        assertThrows(RuntimeException.class,() ->EulerTourValidationUtil.validateEulerTour(g3, tour));
}

    @Test
    public void testRandomized() {
        Random r = new Random();
        for(int i = 0; i < 100 ; i++) {
            Graph g = EulergraphGenerator.generateEulerGraph(r.nextInt(10000)+1, r.nextInt(1000000), r.nextInt(100), r.nextInt(100), r.nextBoolean());
            Hierholzer hierholzer = Hierholzer.run(g);

            assertTrue(hierholzer.containsEulerTour());
            EulerTourValidationUtil.validateEulerTour(g, hierholzer.getEulerCircle());
        }
    }

    @Test
    public void testUnevenDegree() {
        assertThrows(RuntimeException.class,() -> Hierholzer.run(g5));
    }


}