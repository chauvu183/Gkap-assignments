package de.haw_hamburg.gkap.algorithm;

import de.haw_hamburg.gkap.algorithm.util.DisjointSet;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;
import java.util.stream.Collectors;

//D:\UNI\GKAP_4\gkap\graphs

public class Kruskal extends AbstractAlgorithm{

    private List<Node> nodesList = new ArrayList<>();

    // Store all the edges of MST
    private List<Edge> spanningTreeEdges;

    private List<Edge> sortedEdges;
    private Iterator<Edge> edgeIterator;
    private DisjointSet<Node> disjointSet;
    private long startTime;
    private long runtime;
    private boolean visualisation;
    private boolean isValidSpanningTree = false;

    public Kruskal(Graph graph) {
        this(graph, false);
    }

    public Kruskal(Graph graph, boolean visualisation) {
        super(graph);
        clearAll();
        this.visualisation = visualisation;
    }

    @Override
    protected void init() {
        startTime = System.currentTimeMillis();

        //Initialize the MST
        if (spanningTreeEdges == null){
            spanningTreeEdges = new ArrayList<>(graph.getEdgeCount());
        }else{
            spanningTreeEdges.clear();}


        nodesList.add(graph.nodes().findAny().orElseThrow());

        // create the list to store the sorted edge in increasingly order
        sortedEdges = graph.edges().sorted(Comparator.comparingDouble(this::getEdgeWeight)).collect(Collectors.toList());


        //make Disjoint Set
        disjointSet = new DisjointSet<Node>(graph.getNodeCount());

        // for each node of the graph,make a disjoint set with itself
        for(Node node : graph){
            disjointSet.add(node);
        }
        edgeIterator = sortedEdges.iterator();
    }

    //the loop is executed from the outside, so that the viewer can pause it for visualisation.
    protected void kruskalAlgo(Edge edge){
            // Check if it forms a cycle with the spanning tree formed so far. If cycle is not formed, include this edge.
            if(disjointSet.union(edge.getNode0(),edge.getNode1())){
                // add an edge to the spanning tree
                spanningTreeEdges.add(edge);

                //set attribures
                if(visualisation) {
                    setLayoutClass(edge.getNode0(),"green");
                    setAttribute(edge.getNode0(), "inMST", true);
                    setLayoutClass(edge.getNode1(),"green");
                    setAttribute(edge.getNode1(), "inMST", true);
                    setLayoutClass(edge, "green");
                    setAttribute(edge, "inMST", true);
                }
            }
    }

    @Override
    protected boolean nextStep() {
        // end the process if there are enough edges in teh MST
        //A minimum spanning tree has (V – 1) edges where V is the number of vertices in the given graph.
        if(spanningTreeEdges.size() == graph.getNodeCount()-1){
            isValidSpanningTree = true;
            return false;
        }
        //Pick the smallest edge.
        if(edgeIterator.hasNext()){
            Edge edge = edgeIterator.next();
            kruskalAlgo(edge);
            edgeIterator.remove();
            return true;
        } else {
            //in this case we checked all edges but MST still not complete -> no spanning three found
            return false;
        }

    }

    //help method to get the attribute weight of the edge
    protected double getEdgeWeight(Edge edge){
        Double weight = edge.getAttribute("weight", Double.class);
        if( weight == null ){
            return 0.0;
        }
        return weight;
    }

    public long getRuntime() {
        return runtime;
    }

    public List<Edge> getSpanningTreeEdges(){
        return spanningTreeEdges;
    }

    public double getSpanningTreeWeight() {
        return spanningTreeEdges.stream()
                .filter(e -> e.hasAttribute("weight"))
                .mapToDouble(e -> e.getAttribute("weight", Double.class))
                .sum();
    }

    @Override
    protected void terminate() {
        runtime = System.currentTimeMillis()-startTime;
    }

    public boolean isValidSpanningTree() {
        return isValidSpanningTree;
    }
}






