package de.haw_hamburg.gkap.algorithm.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ElementInformationMap {

	private Map<String, Object> elements = new HashMap<>();
	
	//maybe add also typechecks here
	public <T> boolean contains(String key) {
		return elements.containsKey(key);
	}
	
	public void setValue(String key, Object value) {
		elements.put(key, value);
	}
	
	public Object getValue(String key) {
		return elements.get(key);
	}
	
	public <T> T getValueAs(String key, Class<T> clazz) {
		return (T) elements.get(key);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('\n');
		elements.entrySet().stream().sorted((a,b) -> a.getKey().compareTo(b.getKey())).forEach(entry -> {
			sb.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
		});;
		return sb.toString();
	}
	
}
