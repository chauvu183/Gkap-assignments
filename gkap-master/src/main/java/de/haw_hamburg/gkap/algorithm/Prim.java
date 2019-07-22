package de.haw_hamburg.gkap.algorithm;

import de.haw_hamburg.gkap.algorithm.util.FibonacciHeap;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.HashSet;
import java.util.Set;

public class Prim extends AbstractAlgorithm {

    private boolean markers;
    private FibonacciHeap<NodeEdgeTupel> fibonacciHeap = new FibonacciHeap<>();
    private Set<Edge> spanningTreeEdges = new HashSet<>();
    private boolean isValidSpanningTree = false;
    private long startTime;
    private long runtime;

    public Prim(Graph graph) {
        this(graph, true);
    }

    public Prim(Graph graph, boolean markers) {
        super(graph);
        this.markers = markers;
        clearAll();
    }

    @Override
    protected void init() {
        startTime = System.currentTimeMillis();
        graph.nodes().forEach(node -> {
            FibonacciHeap.Entry heapEntry = fibonacciHeap.enqueue(new NodeEdgeTupel(node), Double.POSITIVE_INFINITY);
            node.setAttribute("fibonacciHeap", heapEntry);
        });
        graph.nodes().findFirst().ifPresent(n -> fibonacciHeap.decreaseKey(n.getAttribute("fibonacciHeap", FibonacciHeap.Entry.class), Double.MAX_VALUE));
    }

    @Override
    protected boolean nextStep() {
        if(!fibonacciHeap.isEmpty()) {
            FibonacciHeap.Entry<NodeEdgeTupel> heapEntry = fibonacciHeap.dequeueMin();
            if(heapEntry.getPriority() == Double.POSITIVE_INFINITY)
                return false; //es wurden keine knoten markiert, somit graph nicht zusammenhängend
            heapEntry.getValue().node.removeAttribute("fibonacciHeap");
            if(markers)
                setLayoutClass(heapEntry.getValue().node, "green");
            if(heapEntry.getValue().edge != null) {
                spanningTreeEdges.add(heapEntry.getValue().edge); //add edge to spanning tree
                if(markers)
                    setLayoutClass(heapEntry.getValue().edge, "green");
            }
            onDequeNode(heapEntry.getValue().node);
            return true;
        } else {//all nodes are in spanning tree -> finish
            this.isValidSpanningTree = true;
            return false;
        }
    }

    private void onDequeNode(Node node) {
        node.edges()
                .filter(e -> e.getOpposite(node).hasAttribute("fibonacciHeap"))
                .forEach(e -> {
                    FibonacciHeap.Entry<NodeEdgeTupel> heapEntry = e.getOpposite(node).getAttribute("fibonacciHeap", FibonacciHeap.Entry.class);
                    Double weight = e.getAttribute("weight", Double.class);
                    if(weight == null)
                        weight = 0d;

                    if(weight < heapEntry.getPriority()) {
                        if(markers) {
                            setLayoutClass(e.getOpposite(node), "red");
                            setAttribute(e.getOpposite(node), "Priority", weight);
                        }
                        fibonacciHeap.decreaseKey(heapEntry, weight);
                        heapEntry.getValue().edge = e;
                    }
                });
    }

    @Override
    protected void terminate() {
        runtime = System.currentTimeMillis()-startTime;
    }

    public Set<Edge> getSpanningTreeEdges() {
        return spanningTreeEdges;
    }

    public double getSpanningTreeWeight() {
        return spanningTreeEdges.stream()
                .filter(e -> e.hasAttribute("weight"))
                .mapToDouble(e -> e.getAttribute("weight", Double.class))
                .sum();
    }

    public boolean isValidSpanningTree() {
        return isValidSpanningTree;
    }

    public long getRuntime() {
        return runtime;
    }

    private class NodeEdgeTupel {
        final Node node;
        Edge edge;

        private NodeEdgeTupel(Node node) {
            this.node = node;
        }
    }
}
