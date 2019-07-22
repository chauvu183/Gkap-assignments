package de.haw_hamburg.gkap.algorithm;

import de.haw_hamburg.gkap.algorithm.util.EulerCycleProperties;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;
import java.util.stream.Collectors;



public class Fleury extends AbstractAlgorithm {

    private LinkedList<Edge> eulerCircuit;
    private Node startNode;
    private Node currentNode;
    private Edge nextEdge;
    private List<Edge> edgeList;


    public Fleury(Graph graph){
        super(graph);
    }

    @Override
    protected void init() {
        eulerCircuit = new LinkedList<Edge>();
        startNode = this.getRandomNode();
        currentNode = startNode;
        edgeList =  graph.edges().collect(Collectors.toList());
    }


    /*
    Get a random Node in the graph
     */
    private Node getRandomNode() {
        return graph.getNode(this.getRandom(graph.getNodeCount()));
    }

    /*
     This Method give a random number between [0- (value-1)]
     */

    protected int getRandom(int value){
        Random random = new Random();
        /*
        Obtain a random number between [0- (value-1)]
         */
        return random.nextInt(value);
    }

    @Override
    protected boolean nextStep() {
            /*
            Check the conditions of the graph
            */
            EulerCycleProperties checkGraph = new EulerCycleProperties();
            if(!edgeList.isEmpty()){
                if(checkGraph.isEulerGraph(graph) && currentNode != null){
                /*
                Execute the algorithm on the graph to find the Euler Circle
                */
                FleuryAlgo(graph);

                /*
                Put the edge back to the graph
                */
                addEdgesFromEulerCircuit(graph,eulerCircuit);

                System.out.println("Fleury: " + eulerCircuit.toString());

                return true;

                } else {
                    return false;
                }
            } else{
                return false;}
    }


    protected void addEdgesFromEulerCircuit(Graph graph,List<Edge> edges){
        for (int i = 0; i < edges.size(); i++) {
            String id = edges.get(i).getId();
            Node source = edges.get(i).getSourceNode();
            Node target = edges.get(i).getTargetNode();
            graph.addEdge(id,source,target);
            setLayoutClass(graph.getEdge(id),"green");
        }
    }


    /*
    The algorithm adds an initially empty edge sequence, creating an Euler circle.
    1.  Select any node as the current node.
    2.  Choose the edge, with the current node in presidents. In this case, edges that are not bridged edges in the graph must first be selected.
    3.  Delete the selected edge in the graph and add it to the Euler circle.
    4.  Choose the other node the selected edge as new current node.
    5.  If there are any edges in the graph, go to step 2.
    */

    private void FleuryAlgo(Graph graph){
        /*
        For all nodes ,which are adjacent with the start vertex, do
         */
        while(!edgeList.isEmpty()){
            List<Edge> currentEdges = new LinkedList<Edge>();
            /*
            Get all the edges connected with the current Node
             */
            currentEdges.addAll(currentNode.edges().collect(Collectors.toList()));

            if(!currentEdges.isEmpty()){
            /* When the node have more than one connected edges,
               Select any edge connected with the current node but choosing a bridge only if there is no alternative.
             */
                Edge randomEdge;
                Integer randomEdgeNumber = getRandom(currentEdges.size());

            if(currentEdges.size()>1){
                /*
                Choose the edge that is not a bridge
                 */
                do{
                    randomEdge = currentEdges.get(randomEdgeNumber);

                }while (!this.isBridge(randomEdge,currentNode.toString()));

                nextEdge = graph.getEdge(randomEdge.getId());

            }else {
                nextEdge = currentNode.getEdge(0);
            }


                eulerCircuit.add(nextEdge);
            /*
            Remove the edge from the graph
             */
                graph.removeEdge(nextEdge.getId());
                edgeList.remove(nextEdge.getIndex());


                /*
                Choose the next node
                */
                if(nextEdge.getSourceNode() != currentNode){
                    currentNode = nextEdge.getSourceNode();
                }else{
                    currentNode = nextEdge.getTargetNode();
                }
            }else{
                currentNode = getRandomNode();
            }
        }

    }

    /*
    An edge in an undirected connected graph is a bridge if removing it disconnects the graph.

    This Method is to find the Bridges in the Graph by one by one removing  all edges and see if removal of a edge causes disconnected graph.

    Steps:
     For every edge ,do following
      1.   Remove edge from graph
      2.    See if the graph remains connected (We use BFS)
      3.    Add edge back to the graph.
     */

    private boolean isBridge(Edge edge,String currentNode){
        /*
        Delete the edge from graph
         */
        String edgeId = edge.getId();
        Node sourceNode = edge.getSourceNode();
        Node targetNode = edge.getTargetNode();

        graph.removeEdge(edge);
        /*
        Check the connected condition of the graph by using BFS
         */
        BreadthFirstSearch checkGraphBFS = new BreadthFirstSearch(graph,currentNode,startNode.toString());
        /*
        if the current node can not reach the start node, then that edge is a bridge
         */
        if(!checkGraphBFS.foundShortestWay()){
            graph.addEdge(edgeId,sourceNode,targetNode);
            return true;
        }else{
            graph.addEdge(edgeId,sourceNode,targetNode);
            return false;
        }
    }


    public List<Edge> getEulerCircle(){
        return eulerCircuit;
    }



    @Override
    protected void terminate() {

    }

}
