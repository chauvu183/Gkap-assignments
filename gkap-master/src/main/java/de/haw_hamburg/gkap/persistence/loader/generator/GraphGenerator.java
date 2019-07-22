package de.haw_hamburg.gkap.persistence.loader.generator;

import de.haw_hamburg.gkap.persistence.loader.GraphReceiver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class GraphGenerator
{
    private int _nodeCount, _edgeCount;
    private GraphReceiver graphReceiver;


    public GraphGenerator(GraphReceiver graphReceiver) {
        this.graphReceiver = graphReceiver;
    }

    /**
     * if max_weight_edges == 0 || max_weight_nodes == 0
     * => no weight will be displayed in the graph
     */
    public void generate(int nodeCount, int edgeCount, int seed, boolean directed)
    {
        generate(nodeCount, edgeCount, seed, directed, 0);
    }

    public void generate(int nodeCount, int edgeCount, int seed, boolean directed, int max_weight_edges)
    {
        generate(nodeCount, edgeCount, seed, directed, max_weight_edges, 0);
    }

    public void generate(int nodeCount, int edgeCount, int seed, boolean directed, int max_weight_edges, int max_weight_nodes)
    {
        check_node_edge_count(nodeCount,edgeCount);
        graphReceiver.setDirected(directed);
        generate_engine(_nodeCount, _edgeCount, seed, max_weight_edges, max_weight_nodes);
    }


    private void generate_engine(int nodeCount, int edgeCount, int seed, int max_weight_edges, int max_weight_nodes)
    {
        Random rnd = new Random(seed);

        List<String> unconnected = new ArrayList<>(), connected = new ArrayList<>();

        /* add all nodes to the graph */
        for(int i = 0; i < nodeCount; i++ )
        {
            String node = "n" + i;
            add_node(node, rnd.nextInt(max_weight_nodes + 1));
            unconnected.add(node);
        }

        /* add a node to the connected list (by random) */
        int edgeIdCount = 0;
        String firstNode, secondNode;

        firstNode = unconnected.remove(rnd.nextInt(unconnected.size()));
        connected.add(firstNode);

        /* all nodes are connected with at least one edge (connected graph) */
        while (unconnected.size() > 0)
        {
            firstNode = connected.get(rnd.nextInt(connected.size()));
            secondNode = unconnected.remove(rnd.nextInt(unconnected.size()));

            connected.add(secondNode);
            add_edge("e" + edgeIdCount, firstNode, secondNode, rnd.nextInt(max_weight_edges + 1));

            edgeIdCount++;
            edgeCount --;
        }

        /* add remaining edges to the graph */
        while (edgeCount > 0)
        {
            firstNode = connected.get(rnd.nextInt(connected.size()));
            secondNode = connected.get(rnd.nextInt(connected.size()));

            if(!firstNode.equals(secondNode) && graphReceiver.getGraph().getEdge(firstNode + "&" + secondNode) == null)
            {
                add_edge("e" + edgeIdCount, firstNode, secondNode, rnd.nextInt(max_weight_edges + 1));

                edgeIdCount++;
                edgeCount --;
            }
        }
    }


    /**
     * edgeCount will be changed here
     * if edgeCount > maxEdgeCount || edgeCount < nodeCount
     */
    private void check_node_edge_count(int nodeCount, int edgeCount)
    {
        int maxedgeCount = fac(nodeCount - 1) * 2;
        _edgeCount = edgeCount;
        _nodeCount = nodeCount;

        if (nodeCount > edgeCount)
        {
            System.out.println("GRAPHGENERATOR: [WARNING] additional edges needed.. edgeCount = " + (nodeCount - 1));
            _edgeCount = nodeCount - 1;
        }
        else if (edgeCount > maxedgeCount)
        {
            System.out.println("GRAPHGENERATOR: [WARNING] to many edges.. edgeCount = " + maxedgeCount);
            _edgeCount = maxedgeCount;
        }
    }

    private int fac(int n) //change from recursion to loop to avoid StackOverflowException when generating big graph
    {
        int fact = 1;
        for (int i = 2; i <= n; i++) {
            fact += i;
        }
        return fact;
    }

    /**
     * access weight and name by getting edge attribute "name", "weight"
     * edgeID is composed of "node0 + & + node1"
     */
    private void add_edge(String edgename, String node1, String node2, int weight)
    {
        graphReceiver.addEdge(node1 + "&" + node2,node1, node2, Optional.ofNullable(edgename), Optional.of((double)weight));
    }

    /**
     * access weight and name by getting node attribute "name", "weight"
     */
    private void add_node(String node, int weight)
    {
        graphReceiver.addNode(node, Optional.of((double)weight));
    }
}
