package de.haw_hamburg.gkap.persistence.loader;

import java.util.Optional;
import java.util.UUID;

import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.stream.SourceBase;

/**
 * Schnittstelle zwischen {@link GraphLoader} und {@link Graph}
 * Diese Klasse implementiert die {@link GraphReceiver} Schnittstelle und die {@link SourceBase} Schnittstelle von GraphStream
 * Wenn von einem Provider ein Element (Knote Kante) geschickt wurde, wird diese an den {@link Graph} weitergeleitet
 */
public class GraphStreamGraphReceiver extends SourceBase implements GraphReceiver {

	private final Graph graph;
	private String sourceId = "GraphStreamGraphReceiver-" + UUID.randomUUID().toString();
	private boolean isDirected = false;

	public GraphStreamGraphReceiver(Graph graph) {
		this.graph = graph;
		this.addSink(graph);
	}
	
	public void end() {
		this.clearSinks();
	}
	
	@Override
	public void addEdge(String sourceNodeId, String targetNodeId, Optional<String> name, Optional<Double> weight) {
		String edgeId = generateRandomId();
		addEdge(edgeId, sourceNodeId, targetNodeId, name, weight);
	}

	@Override
	public void addEdge(String edgeId, String sourceNodeId, String targetNodeId, Optional<String> name, Optional<Double> weight) {
		sendEdgeAdded(sourceId, edgeId, sourceNodeId, targetNodeId, isDirected);

		String edgeName = name.orElse(sourceNodeId + "&" + targetNodeId);
		String edgeLabel = weight.isPresent() ? String.format("%s [%s]", edgeName, removeTrailingZeros(weight.get())) : edgeName;
		sendEdgeAttributeAdded(sourceId, edgeId, "ui.label", edgeLabel);

		name.ifPresent(aString -> sendEdgeAttributeAdded(sourceId, edgeId, "name", aString));
		weight.ifPresent(aDouble -> sendEdgeAttributeAdded(sourceId, edgeId, "weight", aDouble));
		//weight.ifPresent(aDouble -> sendEdgeAttributeAdded(sourceId, edgeId, "layout.weight", aDouble));
	}

	@Override
	public void addNode(String nodeId, Optional<Double> attribute) {
		if(graph.getNode(nodeId) != null)
			return;

		sendNodeAdded(sourceId, nodeId);
		sendNodeAttributeAdded(sourceId, nodeId, "name", nodeId);
		if(attribute.isPresent()) {
			sendNodeAttributeAdded(sourceId, nodeId, "weight", attribute.get());
			//sendNodeAttributeAdded(sourceId, nodeId, "layout.weight", attribute.get());
			sendNodeAttributeAdded(sourceId, nodeId, "ui.label", String.format("%s [%s]", nodeId , removeTrailingZeros(attribute.get())));
		} else {
			sendNodeAttributeAdded(sourceId, nodeId, "ui.label", nodeId);
		}
	}

	@Override
	public void setDirected(boolean isDirected) {
			this.isDirected = isDirected;
	}

	@Override
	public Graph getGraph() {
		return graph;
	}

	private String generateRandomId() {
		return UUID.randomUUID().toString();
	}

	private static String removeTrailingZeros(double d) {
		return String.valueOf(d).replaceAll("\\.0$", "");
	}
}
