package de.haw_hamburg.gkap.persistence.loader;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class GraphLoaderUtil {

    public static Graph loadGraphFromString(String input) throws RuntimeException, IOException {
        Graph graph = new MultiGraph(UUID.randomUUID().toString());
        InputStream in = new ByteArrayInputStream(input.getBytes());
        GraphStreamGraphReceiver graphReceiver = new GraphStreamGraphReceiver(graph);
        GraphLoader loader = new GraphLoader(in, graphReceiver);

        //load graph with strict mode (throw exception on any error)
        loader.load(true);
        graphReceiver.end();
        return graph;
    }
}
