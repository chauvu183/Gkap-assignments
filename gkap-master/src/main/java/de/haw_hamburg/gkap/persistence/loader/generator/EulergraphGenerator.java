package de.haw_hamburg.gkap.persistence.loader.generator;

import de.haw_hamburg.gkap.persistence.loader.GraphReceiver;
import de.haw_hamburg.gkap.persistence.loader.GraphStreamGraphReceiver;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.MultiGraph;

import java.util.*;

public class EulergraphGenerator
{
    private final int _RND_MAX = 1000;
    private final double _GENERATE_NEW_NODE_RATE = 0.4; // create new node when math.random < _GENERATE_NEW_NODE_RATE
    private Random _rnd;
    private GraphReceiver _graphReceiver;
    private HashMap<Node, ArrayList<Node>> _NodeStorage;
    private int _edgeCount = 0;

    public EulergraphGenerator(GraphReceiver graphReceiver)
    {
        _graphReceiver = graphReceiver;
    }

    public void eulerGenerate(int edgeCount, int seed, int maxEdgeWeight, int maxNodeWeight, boolean directed)
    {
        _edgeCount = edgeCount;
        _rnd = new Random(seed);
        if (checkGeneratorInput(edgeCount))
            generate(seed, maxEdgeWeight, maxNodeWeight, directed);
    }
    public void eulerGenerate(int edgeCount, int seed, int maxEdgeWeight, boolean directed)
    {
        _edgeCount = edgeCount;
        _rnd = new Random(seed);
        if (checkGeneratorInput(edgeCount))
            generate(seed, maxEdgeWeight, 0, directed);
    }
    public void eulerGenerate(int edgeCount, int seed)
    {
        _edgeCount = edgeCount;
        _rnd = new Random(seed);
        if (checkGeneratorInput(edgeCount))
            generate(seed, 0, 0, false);
    }
    public void eulerGenerate(int edgeCount)
    {
        _edgeCount = edgeCount;
        int seed = new Random().nextInt(_RND_MAX)+1;
        _rnd = new Random(seed);
        if (checkGeneratorInput(edgeCount))
            generate(seed, 0, 0, false);
    }

    /**
     * 1. create sourceNode
     * 2. originNode = sourceNode (to finish the circle later)
     * 3. add sourceNode to map
     * 4. start loop edgesCount > 2
     *  4A. map.sourceNode.values.size == 0 || random factor
     *       YES:
     *        4a. create targetNode
     *        4c. add new targetNode to map
     *       NO:
     *        4d. targetNode = random node from map.sourceNode.values
     *  4B. create edge between sourceNode and targetNode
     *  4C. delete values from map at key sourceNode and targetNode
     *  4D. sourceNode = targetNode
     *  4E. edgesCount --
     * LOOP END
     * 5. targetNode = random node in map.sourceNode.values WHERE node found in map.originNode.values
     *  5A. Node not found
     *       5a. targetNode = create new node
     *  5B. create edge between sourceNode and targetNode
     * 6. sourceNode = targetNode
     * 7. create edge between sourceNode to originNode
     */
    private void generate(int seed, int maxEdgeWeight, int maxNodeWeight, boolean directed)
    {
        final long timeStart = System.currentTimeMillis();
        int edgeNameID = 1, nodeNameID = 2;

        Node sourceNode = addNode("n1", maxNodeWeight); //1.
        Node originNode = sourceNode; //2.
        Node targetNode = null;
        _NodeStorage = new HashMap<>();
        addTargetToMapEG(_NodeStorage, sourceNode, sourceNode); //3.

        while (_edgeCount > 2) //4.
        {
            if (_NodeStorage.get(sourceNode).isEmpty() || Math.random() < _GENERATE_NEW_NODE_RATE) //4A.
            {
                targetNode = addNode("n"+nodeNameID, maxNodeWeight); //4a.
                addTargetToMapEG(_NodeStorage, sourceNode, targetNode); //4c.
                nodeNameID++;
            } else
                targetNode = _NodeStorage.get(sourceNode).get(_rnd.nextInt(_NodeStorage.get(sourceNode).size())); //4d.

            addEdge("e"+edgeNameID, sourceNode, targetNode, maxEdgeWeight, directed); //4B.

            removeSTFromValueEG(_NodeStorage, sourceNode, targetNode); //4C.
            sourceNode = targetNode; //4D.
            edgeNameID++;
            _edgeCount--; //4E.
        }

        targetNode = findNodeInTwoLists(_NodeStorage.get(sourceNode), _NodeStorage.get(originNode)); //5.
        if (targetNode == null || Math.random() < _GENERATE_NEW_NODE_RATE) //5A.
            targetNode = addNode("n"+nodeNameID, maxNodeWeight); //5a.


        addEdge("e"+edgeNameID, sourceNode, targetNode, maxEdgeWeight, directed);//5B.
        edgeNameID++;
        sourceNode = targetNode; //6.


        addEdge("e"+edgeNameID, sourceNode, originNode, maxEdgeWeight, directed); //7.

        final long timeEnd = System.currentTimeMillis();
        System.out.println("SUCCESS Graph generated in " + (timeEnd - timeStart) + " Millisec");
    }

    private boolean checkGeneratorInput(int edgeCount)
    {
        if (edgeCount <= 2)
        {
            _edgeCount = _rnd.nextInt(_RND_MAX)+1;
            System.out.println("EULER GRAPHGENERATOR: [WARNING] additional edges needed.. setting edgeCount:" + _edgeCount);
        }
        return true;
    }

    public HashMap<Node, ArrayList<Node>> addTargetToMapEG(HashMap<Node, ArrayList<Node>> map, Node sourceNode, Node targetNode)
    {
        ArrayList<Node> nodes = new ArrayList<>();
        map.forEach((key, value) -> nodes.add(key));
        map.forEach((k,v) -> {
            if (k != sourceNode || k != targetNode)
            {
                v.add(targetNode);
            }
        });
        map.put(targetNode, nodes);
        return map;
    }

    public HashMap<Node, ArrayList<Node>> removeSTFromValueEG(HashMap<Node, ArrayList<Node>> map, Node sourceNode, Node targetNode)
    {
        ArrayList<Node> valuesSource = map.get(sourceNode);
        ArrayList<Node> valuesTarget = map.get(targetNode);
        if (valuesSource.contains(targetNode))
        {
            valuesSource.remove(targetNode);
            map.put(sourceNode, valuesSource);
        }
        if (valuesTarget.contains(sourceNode))
        {
            valuesTarget.remove(sourceNode);
            map.put(targetNode, valuesTarget);
        }
        return map;
    }

    public Node findNodeInTwoLists(ArrayList<Node> alist1, ArrayList<Node> alist2)
    {
        for (Node n1: alist1)
            for (Node n2: alist2)
                if (n1.equals(n2))
                    return n1;
        return null;
    }

    private void addEdge(String edgename, Node node1, Node node2, int weight, boolean directed)
    {
        _graphReceiver.addEdge(node1.getId() + "&" + node2.getId(),node1.getId(), node2.getId(), Optional.ofNullable(edgename), Optional.of((double)weight));
    }

    private Node addNode(String node, int weight)
    {
        _graphReceiver.addNode(node, Optional.of((double)weight));
        return _graphReceiver.getGraph().getNode(node);
    }

    public static Graph generateEulerGraph(int edgeCount, int seed, int maxWeightEdges, int maxWeightNodes, boolean directed) {
        GraphStreamGraphReceiver _graphReceiver = new GraphStreamGraphReceiver(new MultiGraph(UUID.randomUUID().toString()));
        EulergraphGenerator _eulergraphGenerator = new EulergraphGenerator(_graphReceiver);
        _eulergraphGenerator.eulerGenerate(edgeCount, seed, maxWeightEdges, maxWeightNodes, false);
        return _graphReceiver.getGraph();
    }
}
