package de.haw_hamburg.gkap.persistence.writer;

import java.io.IOException;
import java.io.Writer;
import java.util.Optional;

/**
 * {@link SimpleGraphWriter} ist verwantwortlich für das schreiben des vogegebenen Datenformats in einen Writer.
 * Es stellt Methoden bereit, mit denen die Attribute (z.B. Name, Attribute) von Elementen (Knoten, Kanten),
 * auf einen String abgebildet werden, der dann in den {@link Writer} geschrieben wird.
 */
public class SimpleGraphWriter implements GraphWriter {

	private Writer writer;
	
	public SimpleGraphWriter(Writer outputWriter) {
		this.writer = outputWriter;
	}

	/**
	 * Schreibt eine Einheit (Zeile) in den Writer
	 * Es muss mindestens ein Node Name gegeben sein.
	 * Optionale Werte sind an der verwendung der Klasse {@link Optional} zu erkennen
	 * @throws IOException
	 */
	public void writeUnit(String node1, Optional<Double> attr1, Optional<String> node2, Optional<Double> attr2, Optional<String> edge, Optional<Double> weight) throws IOException {
		if(node1 == null || node1.equals(""))
			throw new IllegalArgumentException("node1 has no value");
		StringBuilder sb = new StringBuilder();
		sb.append(node1);
		if(attr1.isPresent()) 
			sb.append(':').append(removeTrailingZeros(attr1.get()));
		if(node2.isPresent()) {
			sb.append(',').append(node2.get());
			if(attr2.isPresent()) 
				sb.append(':').append(removeTrailingZeros(attr2.get()));
			if(edge.isPresent()) {
				sb.append('(').append(edge.get()).append(')');
			}
			if(weight.isPresent()) {
				sb.append("::").append(removeTrailingZeros(weight.get()));
			}
		}
		sb.append(";\n");
		String line = sb.toString();
		writer.write(line);
	}
	
	/**
	 * Schreibt die Information, dass der Graph gerichtet ist.
	 * Muss am Anfang ausgerufen werden, wenn der Ganze Graph gerichtet ist.
	 * @throws IOException
	 */
	public void writeDirected() throws IOException {
		String line = "#directed;\n";
		writer.write(line);
	}

	private static String removeTrailingZeros(double d) {
		return String.valueOf(d).replaceAll("\\.0$", "");
	}
}
