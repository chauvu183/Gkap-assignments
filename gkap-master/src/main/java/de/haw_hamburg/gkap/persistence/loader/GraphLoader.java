package de.haw_hamburg.gkap.persistence.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;

/**
 * Diese Klasse liest, das in der Aufgabe vorgegebene Dateiformat, aus einem {@link InputStream} Zeile für Zeile als Strings ein, 
 * und extrahiert die Informationen aus diesen.
 * 
 * Sie wird mit einer {@link GraphReceiver} Instanz initialisiert und fungiert als Provider für diese.
 * Gelesene Knoten/Kanten werden an die {@link GraphReceiver} Instanz weitergeleitet
 */
public class GraphLoader {
	
	private final InputStream in;
	private GraphReceiver receiver;
	
	public GraphLoader(InputStream in, GraphReceiver receiver) {
		this.in = in;
		this.receiver = receiver;
	}
	
	/**
	 * Liest den Stream zeilenweise ein.
	 * Achtung: InputStream wird am Ende nicht geschlossen
	 * @throws RuntimeException
	 * @throws IOException
	 */
	public void load(boolean strict) throws RuntimeException, IOException {
		try(Scanner sc = new Scanner(in).useDelimiter("\\s*\n")) {
		while(sc.hasNext()) {
			String line = sc.next();
			try {
				parseLine(line.replaceAll(";", ""));
			} catch (RuntimeException e) {
				if(!strict)
					System.out.println(String.format("RuntimeException occured while processing line %s [%s]", line, e.getMessage()));
				else 
					throw new RuntimeException(String.format("RuntimeException occured while processing line %s [%s]", line, e.getMessage()));
			}
		}
		}
	}
	
	/**
	 * Liest eine Zeile ein und versucht, alle Attribute aus dieser zu extrahieren.
	 * Wenn erfolgreich, wird mindestens ein Knoten oder zwei Knoten und eine Kante and den {@link GraphReceiver} gesendet. 
	 * @param line String aus Input, aus dem Informationen extrahiert werden sollen
	 * @throws RuntimeException
	 */
	private void parseLine(String line) throws RuntimeException{
		//remove all tabs, nl, whitespace
		line = line.replaceAll("\\s", "");
		
		if(line.equals("#directed")) {
			receiver.setDirected(true);
			return;
		}
		
		//find weight of edge if exists
		String[] weightSplit = line.split("::");
		line = weightSplit[0];
		Optional<Double> edgeWeight = Optional.ofNullable(weightSplit.length > 1 ? Double.parseDouble(weightSplit[1]) : null); 
		
		//find name of edge if exists
		String[] edgeSplit = line.split("\\(");
		line = edgeSplit[0];
		Optional<String> edgeName = Optional.ofNullable(edgeSplit.length > 1 ? edgeSplit[1].replaceAll("\\)", "") : null); 
		
		//split node a and optional b node on comma char
		String[] nodeSplit = line.split(",");
		if(nodeSplit.length > 2)
			throw new RuntimeException("Too many nodes (values seperated by comma) in line");
		
		String sourceNodeId = tryAddNode(nodeSplit[0]);
		
		Optional<String> nodeInput2 = Optional.ofNullable(nodeSplit.length > 1 ? nodeSplit[1] : null);

		if(nodeInput2.isPresent()) {
			String targetNodeId = tryAddNode(nodeInput2.get());
			receiver.addEdge(sourceNodeId, targetNodeId, edgeName, edgeWeight);
		}
	}
	//name:4
	private String tryAddNode(String input) {
		if(input == null)
			throw new NullPointerException("node input string is null");	
		String[] splitted = input.split(":");
		String name = splitted[0];
		if(name.equals(""))
			throw new IllegalArgumentException("cannot create node from empty string");
		
		Optional<Double> attribute = Optional.ofNullable(splitted.length > 1 ? Double.parseDouble(splitted[1]) : null);	
		receiver.addNode(name, attribute);
		return name;
	}
}
