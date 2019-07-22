package de.haw_hamburg.gkap.algorithm;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EulerTourValidationUtil {

    public static void validateEulerTour(Graph graph, List<Element> elements) {
        Set<Edge> edgesInTour = elements.stream().filter(e -> e instanceof Edge).map(e -> (Edge)e).collect(Collectors.toSet());
        Set<Edge> edgesInGraph = graph.edges().collect(Collectors.toSet());

        if(edgesInGraph.size() != elements.stream().filter(e -> e instanceof Edge).count())
            throw new IllegalStateException();

        if(!edgesInGraph.containsAll(edgesInTour) || !edgesInTour.containsAll(edgesInGraph)) {
            throw new IllegalStateException();
        }

        Set<Node> nodesFromEdges = elements.stream()
                .filter(e -> e instanceof Edge)
                .map(e -> (Edge)e)
                .flatMap(e -> Stream.of(e.getNode0(), e.getNode1()))
                .collect(Collectors.toSet());

        Set<Node> nodesInTour = elements.stream()
                .filter(e -> e instanceof Node)
                .map(e -> (Node)e)
                .collect(Collectors.toSet());

        if(!nodesFromEdges.containsAll(nodesInTour) || !nodesInTour.containsAll(nodesFromEdges)) {
            throw new IllegalStateException();
        }

        Element lastElement = null;
        for (Element e:elements) {
            if(lastElement == null) {
                lastElement = e;
                continue;
            } else {
                if(e instanceof Edge && lastElement instanceof Node) {
                    if(((Edge) e).getNode0() != lastElement && ((Edge) e).getNode1() != lastElement)
                        throw new RuntimeException();
                } else if(e instanceof Node && lastElement instanceof Edge) {
                    if(((Edge) lastElement).getNode0() != e && ((Edge) lastElement).getNode1() != e)
                        throw new RuntimeException();
                } else
                    throw new RuntimeException();
            }
            lastElement = e;
        }
        if(lastElement instanceof Edge) {
            if(((Edge) lastElement).getNode0() != elements.get(0) && ((Edge) lastElement).getNode1() != elements.get(0))
                throw new RuntimeException();
        } else
            throw new RuntimeException();
    }
}
