package de.haw_hamburg.gkap.persistence.writer;

import java.io.IOException;
import java.util.Optional;

public interface GraphWriter {

	void writeUnit(String node1, Optional<Double> attr1, Optional<String> node2, Optional<Double> attr2, Optional<String> edge, Optional<Double> weight) throws IOException;
	void writeDirected() throws IOException;
}
