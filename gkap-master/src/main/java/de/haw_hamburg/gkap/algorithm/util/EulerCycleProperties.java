package de.haw_hamburg.gkap.algorithm.util;

import de.haw_hamburg.gkap.algorithm.AbstractAlgorithm;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class EulerCycleProperties {

     /*
        Check if all the Verticles Grad in the Graph is even and the Graph is directed
     */

    public boolean isEulerGraph(Graph graph){
        boolean wert = false;
        if(graph == null) {
            throw new IllegalArgumentException("The Conditions is not complete");};
        if(isAllEdgeGradEven(graph) && isDirected(graph)){
            wert = true;
        }
        return wert;
    }
    /*
        Check if all the Verticles Grad in the Graph is even

     */
    public boolean isAllEdgeGradEven(Graph graph){
        boolean wert = false;
        /*
        All Edge grad muss be even
         */
        List<Node> nodeList = graph.nodes().collect(Collectors.toList());

        for(int i =0; i< nodeList.size();i++){
            List<Edge> edgeList = nodeList.get(i).edges().collect(Collectors.toList());
            /*
                check if the graph is connected
             */
            if(edgeList.isEmpty()){
                throw new IllegalArgumentException("Graph is not connected");
            }

            // get the node degree
            int nodeDegree = edgeList.size();

            //check the even node degree
            if(nodeDegree %2 !=0){
                throw new IllegalArgumentException("All the node degree should be even");
            }else {
                wert = true;
            }
        }
        return wert;
    }

    public boolean isDirected(Graph graph){
        boolean wert = false;
        if(graph.getEdge(0).isDirected()){
            throw new IllegalArgumentException("EulerCircle is only applied for the undirected graph ");
        }else {
            wert = true;
        }
        return wert;
    }



}
