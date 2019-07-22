package de.haw_hamburg.gkap.persistence.writer;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 * Diese Klasse ist eine Schnittstelle zwischen {@link Graph} und {@link GraphWriter}.
 * Sie extrahiert die Attribute (z.B. Name, Attribute) von Elementen (Knoten, Kanten) aus {@link Graph} und ruft 
 * mit diesen die Methoden der {@link GraphWriter} Klasse auf
 */
public class GraphStreamGraphPersistor {

	private Graph graph;
	private Set<String> writtenNodes = new HashSet<>();
	private GraphWriter writer;
	
	public GraphStreamGraphPersistor(GraphWriter writer, Graph graph) {
		this.graph = graph;
		this.writer = writer;
	}
	
	/**
	 * Schickt alle Kanten und Knoten eines {@link Graph} Objektes an den {@link GraphWriter}
	 * @throws IOException
	 */
	public void persist() throws Exception {
		//Ein Graph, in dem alle Kanten gerichtet sind ist ein gerichteter Graph
		if(graph.edges().count() > 0 && graph.edges().allMatch(e -> e.isDirected())) {
			writer.writeDirected();
		}
		
		//Iteriere über alle Kanten und schicke diesen an GrapgWriter
		for(Edge e: graph.edges().collect(Collectors.toList())) {
			writeEdge(e);
		}
		
		//Finde alle Knoten, die mit keiner Kante verbunden sind und schicke diese an GraphWriter
		Collection<Node> singleNodes = graph.nodes().filter(elem -> !writtenNodes.contains(elem.getId())).collect(Collectors.toList());
		for(Node n: singleNodes) {
			writeSingleNode(n);
		}
	}

	/**
	 * Zieht alle relevanten Attribute aus einer Kante und schickt diese
	 * an den {@link GraphWriter} 
	 * @throws IOException
	 */
	private void writeEdge(Edge e) throws Exception {
		String node1 = getName(e.getSourceNode()).orElseThrow(() -> new Exception("Treid to write edge without start node"));
		Optional<Double> attr1 = getWeight(e.getSourceNode());
		Optional<String> node2 = getName(e.getTargetNode());
		Optional<Double> attr2 = getWeight(e.getTargetNode());
		Optional<String> edgeName = getName(e);
		Optional<Double> edgeWeight = getWeight(e);
		writer.writeUnit(node1, attr1, node2, attr2, edgeName, edgeWeight);
		writtenNodes.add(node1);
		node2.ifPresent(c -> writtenNodes.add(c));
	}
	
	/**
	 * Zieht alle relevanten Attribute aus einem Knoten und schickt diese
	 * an den {@link GraphWriter} 
	 * @throws IOException
	 */
	private void writeSingleNode(Node n) throws IOException {
		Optional<Double> attr1 = getWeight(n);
		writer.writeUnit(n.getId(), attr1, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
		writtenNodes.add(n.getId());
	}
	
	private Optional<Double> getWeight(Element e) {
		return Optional.ofNullable(e.getAttribute("weight", Double.class));
	}

	private Optional<String> getName(Element e) {
		return Optional.ofNullable(e.getAttribute("name", String.class));
	}
}
