package de.haw_hamburg.gkap.persistence.loader;

import org.graphstream.graph.Graph;

import java.util.Optional;

/**
 * Schnittstelle zwischen Providern von Graphen und Empfängern dieser Graphen.
 */
public interface GraphReceiver {
	
	/**
	 * Wird vom Provider aufgerufen, wenn eine Kante hinzugefügt wird. Source/Target Knoten sollten schon bestehen.
	 */
	void addEdge(String sourceNodeId, String targetNodeId, Optional<String> name, Optional<Double> weight);

	/**
	 * Wird vom Provider aufgerufen, wenn eine Kante hinzugefügt wird. Source/Target Knoten sollten schon bestehen.
	 */
	void addEdge(String edgeId, String sourceNodeId, String targetNodeId, Optional<String> name, Optional<Double> weight);

	/**
	 * Wird vom Provider aufgerufen, wenn eine Kante hinzugefügt werden soll.
	 */
	void addNode(String nodeId, Optional<Double> attribute);
	
	/**
	 * Setzt die Information ob der Graph gerichtet ist oder nicht.
	 * Sollte möglichst am Anfang vom Provider aufgerufen werden.
	 */
	void setDirected(boolean isDirected);

	/**
	 * Gibt den Graph zurück, der bearbeitet wird
	 * @return
	 */
	Graph getGraph();
}
