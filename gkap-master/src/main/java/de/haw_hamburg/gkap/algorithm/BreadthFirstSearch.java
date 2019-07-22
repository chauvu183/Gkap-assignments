package de.haw_hamburg.gkap.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class BreadthFirstSearch extends AbstractAlgorithm{

	private final Node s,t;
	private List<Node> nodesAtIndex = new ArrayList<Node>();
	private int i = 0;
	
	private Integer lengthOfShortestWay;
	private List<Node> shortestWay = new ArrayList<Node>();
	private boolean foundShortestWay = false;
	private Node currentNodeShortestWay;
	
	public BreadthFirstSearch(Graph graph, String nodeS, String nodeT) {
		super(graph);
		s = graph.getNode(nodeS);
		t = graph.getNode(nodeT);
		if(s == null)
			throw new NullPointerException("s cannot be null");
		if(t == null)
			throw new NullPointerException("t cannot be null");
	}

	@Override
	protected void init() {
		clearAll();
		//Schritt 1: Man kennzeichne den Knoten s mit 0 und setze i = 0.
		nodesAtIndex.add(s);
		setLayoutClass(s, "red");
		setAttribute(s, "index", i);
	}
	
	@Override
	protected boolean nextStep() {
		//der Start des Weges ist der Endpunkt
		if(s == t) {
			foundShortestWay = true;
			lengthOfShortestWay = 0;
			shortestWay.add(s);
			System.out.println("Der Anfangspunkt ist der Endpunkt");
			setLayoutClass(s, "green");
			return false;
		}
		if(!foundShortestWay) {
			return markDistanceToStart();
		} else {
			return findShortestWay();
		}
	}
	
	@Override
	protected void terminate() {
	}
	
	/**
	 * Rückverfolgungsalgorithmus
	 * @return
	 */
	private boolean findShortestWay() {
		if(i > 0) {
			//Schritt 2: Man ermittle einen Knoten u, der zu v i benachbart ist und mit λ(u) = i − 1 gekennzeichnet ist. 
			Optional<Node> neighbor = currentNodeShortestWay
					.edges()
					.filter(e -> !e.isDirected() || e.getTargetNode() == currentNodeShortestWay)
					.map(e -> e.getOpposite(currentNodeShortestWay))
					.filter(element -> isAttributeEqual(element, "index", i-1))
					.findFirst();
			
			if(!neighbor.isPresent())
				throw new IllegalStateException("Could not find neighbor of node " + currentNodeShortestWay.getId() + " with i=" + (i-1));
			
			currentNodeShortestWay = neighbor.get();
			//Man ordne v i−1 = u zu. 
			//Achtung: Liste wird später umgekehrt
			shortestWay.add(currentNodeShortestWay);
			setLayoutClass(currentNodeShortestWay, "green");
			//erniedrige i um eins und gehe zu Schritt 2.
			i--;
			return true;
		} else {
			//Schritt 3: Wenn i = 1 ist, ist der Algorithmus beendet. (immer einen Schritt voraus) 
			Collections.reverse(shortestWay);
			System.out.println("Kürzester Weg gefunden: " + shortestWay);
			return false;
		}
	}

	/**
	 * BFS Algorithmus
	 * @return
	 */
	private boolean markDistanceToStart() {
		if(nodesAtIndex.isEmpty())
			throw new IllegalStateException("indexnodes cannot be empty");
		
		//Schritt 2: Ermittle alle nichtgekennzeichneten Knoten in G, die zu den mit i gekennzeichneten Knoten benachbart sind
		Set<Node> neighbors = new HashSet<Node>();
		for(Node node : nodesAtIndex) {
			neighbors.addAll(node.edges()
			.filter(e -> !e.isDirected() || e.getSourceNode() == node)
			.map(e -> e.getOpposite(node))
			.filter(element -> !containsAttribute(element, "index"))
			.collect(Collectors.toSet()));
		}
		
		nodesAtIndex.clear();
		//Markiere gefunden Knoten mit i+1
		neighbors.forEach(node -> {
			nodesAtIndex.add(node);
			setAttribute(node, "index", i+1);
			setLayoutClass(node, "red");
		});
		
		//Schritt 3:
		if(nodesAtIndex.isEmpty()) {
			//es gibt keinen Weg von s zu t. Beende.
			System.out.println("Es gibt keinen Weg von " + s.getId() + " zu " + t.getId());
			return false;
		} else if(nodesAtIndex.contains(t)) {
			//t wurde markiert. Es gibt einen weg von s zu t
			//foundShortestWay = true beendet diesen Algorithmus
			//Schritt 4: Die Länge des kürzesten Weges von s nach t ist i + 1.
			lengthOfShortestWay = i + 1;
			System.out.println("Der küzeste Weg von " + s.getId() + " zu " + t.getId() + " hat die Länge " + (i+1));
			foundShortestWay = true;
			
			//bereite findShortestWay() vor
			//[Kürzester Weg für BFS] Schritt 1: Man setze i = λ(t) und ordne v i = t zu.
			currentNodeShortestWay = t;
			shortestWay.add(currentNodeShortestWay);
			setLayoutClass(currentNodeShortestWay, "green");
		}
		
		//Zähle i um eins hoch und gehe zu Schritt 2, wenn foundShortestWay = false.
		//Andernfalls beginne mit findShortestWay()
		i++;
		return true;
	}
	
	public Map<String, Integer> getMarkerMap() {
		return graph.nodes()
			.filter(n -> containsAttribute(n, "index"))
			.collect(Collectors.toMap(n -> n.getId(), n -> getAttribute(n, "index", Integer.class)));
	}

	public List<String> getShortestWay() {
		return shortestWay.stream().map(n -> n.getId()).collect(Collectors.toList());
	}

	public boolean foundShortestWay() {
		return foundShortestWay;
	}

	public int getLengthOfShortestWay() {
		return lengthOfShortestWay;
	}

}
