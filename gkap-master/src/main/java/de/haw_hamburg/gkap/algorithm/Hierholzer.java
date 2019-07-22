package de.haw_hamburg.gkap.algorithm;

import de.haw_hamburg.gkap.algorithm.util.FibonacciHeap;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;

public class Hierholzer extends AbstractAlgorithm {

    private long startTime;
    private long runtime;
    private boolean markers;
    private boolean containsEulerTour;
    private Node currentNode;

    private LinkedList<Element> subCircle = new LinkedList<>();
    private int subCircleInEuerStartIndex = 0;
    private LinkedList<Element> eulerCircle = new LinkedList<>();

    public Hierholzer(Graph graph) {
        this(graph, true);
    }

    public Hierholzer(Graph graph, boolean markers) {
        super(graph);
        this.markers = markers;
        clearAll();
    }

    @Override
    protected void init() {
        startTime = System.currentTimeMillis();

        if(graph.nodes().filter(n -> n.getDegree() == 0 || n.getDegree() % 2 != 0).count() > 0) {
            throw new RuntimeException("There is at least one node with uneven degree or no connections");
        }

        currentNode = graph.nodes().findAny().orElseThrow();
    }

    @Override
    protected boolean nextStep() {
        if(currentNode == null) {
            //find nodes with unmarked edges in exisiting euler tour
            for(int i = 0; i < eulerCircle.size(); i++) {
                Element element = eulerCircle.get(i);
                if(element instanceof Node) {
                    if(((Node) element).edges().anyMatch(e -> !e.hasAttribute("marked"))) {
                        currentNode = (Node)element;
                        subCircleInEuerStartIndex = i;
                        return true;
                    }
                }
            }

            //it only an euler tour if all edges are included
            if(graph.edges().count() == eulerCircle.stream().filter(e -> e instanceof Edge).count())
                containsEulerTour = true;

            //no unmarked edges left -> finish
            return false;
        }

        //find next position in sub tour
        Optional<Edge> optionalEdge = currentNode.edges().filter(e -> !e.hasAttribute("marked")).findFirst();

        if(optionalEdge.isPresent()) {
            Edge edge = optionalEdge.get();
            edge.setAttribute("marked", true);
            subCircle.add(currentNode);
            if(markers)
                setLayoutClass(edge,"red");
            subCircle.add(edge);


            Node otherNode = edge.getOpposite(currentNode);
            //merge sub tour into euler tour
            if(subCircle.get(0) == otherNode) {
                eulerCircle.addAll(subCircleInEuerStartIndex, subCircle);
                subCircle.clear();
                currentNode = null;
            } else {
                currentNode = otherNode;
            }
        } else {
            throw new IllegalStateException();
        }
        return true;
    }


    @Override
    protected void terminate() {
        graph.edges().forEach(e -> e.removeAttribute("marked"));
        runtime = System.currentTimeMillis()-startTime;
    }

    public LinkedList<Element> getEulerCircle() {
        return eulerCircle;
    }

    public long getRuntime() {
        return runtime;
    }

    public boolean containsEulerTour() {
        return containsEulerTour;
    }

    public static Hierholzer run(Graph g) {
        Hierholzer hierholzer = new Hierholzer(g);
        hierholzer.doInitialize();
        while(hierholzer.doNextStep()) {}
        hierholzer.doTermination();
        return hierholzer;
    }
}
