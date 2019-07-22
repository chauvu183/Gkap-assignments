package de.haw_hamburg.gkap.algorithm;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.SourceBase;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import de.haw_hamburg.gkap.algorithm.util.ElementInformationMap;

/**
 * Dies ist die Grundklasse für Algorithmen. Es gibt 3 abstrankte Methoden init, nextStep und terminate. 
 * In der init Methode wird der Algorithmus initialisiert
 * In der nextStep Methode wird der nächste Schritt des Algorithmus ausgeführt. Wenn sie false zurückgibt, bedeutet dies, dass es keinen nächsten Schritt gibt.
 * In der terminate Methode wird der Algorithmus beendet, falls notwendig
 * Wenn der Algorithmus aufgerufen wird, sind die doInitialize, doNextStep und doTermination methoden zu verwenden
 *
 * Diese Klasse stellt außerdem Methoden zum speichern von Attributen auf Elementen des Graphen bereit und ermöglicht, 
 * Elementen eine im Stylesheet des Graphen definierte CSS Klasse zuzuweisen.
 *
 */
public abstract class AbstractAlgorithm extends SourceBase {

	protected Graph graph;
	private boolean isInitialized;
	private boolean isTerminated;
	protected SpriteManager spriteManager;
	
	public AbstractAlgorithm(Graph graph) {
		if(graph == null)
			throw new NullPointerException("Graph cannot be null");
		this.graph = graph;
		spriteManager = new SpriteManager(graph);
		addSink(graph);
	}
	
	protected abstract void init();
	protected abstract boolean nextStep();
	protected abstract void terminate();
	
	public void doInitialize() {
		init();
		this.isInitialized = true;
	}
	
	public boolean isInitialized() {
		return isInitialized;
	}
	
	public boolean doNextStep() {
		if(!isInitialized)
			throw new IllegalStateException("algorithm is not initialized");
		if(isTerminated)
			throw new IllegalStateException("algorithm is already terminated");
		
		return nextStep();
	}
	
	public void doTermination() {
		terminate();
		clearSinks();
		isTerminated = true;
	}
	
	public boolean isTerminated() {
		return isTerminated;
	}
	
	/**
	 * Setzt die CSS Klasse von element zu uiClass
	 * @param element
	 * @param uiClass
	 */
	protected void setLayoutClass(Element element, String uiClass) {
		if(element instanceof Node)
			sendNodeAttributeAdded(sourceId, element.getId(), "ui.class", uiClass);
		else if(element instanceof Edge)
			sendEdgeAttributeAdded(sourceId, element.getId(), "ui.class", uiClass);
		else throw new IllegalArgumentException("element "+element.getClass()+" must of of type Node or Edge");
	}
	
	/**
	 * Setzt CSS Klassen von allen Elementen zurück
	 */
	protected void clearClasses() {
		graph.nodes().forEach(n -> sendNodeAttributeRemoved(sourceId, n.getId(), "ui.class"));
		graph.edges().forEach(e -> sendEdgeAttributeRemoved(sourceId, e.getId(), "ui.class"));
	}
	
	/**
	 * Setzt alle assoziierte Attribute aller Elemente zurück
	 */
	protected void clearAttributes() {
		spriteManager.sprites().forEach(s -> {
			s.setAttribute("ElementInformationMap", new ElementInformationMap());
			displayAttributes(s);
		});
	}
	
	/**
	 * Setzt CSS Klassen und assoziierte Attribute von allen Elementen zurück
	 */
	protected void clearAll() {
		clearAttributes();
		clearClasses();
	}
	
	/**
	 * Prüft ob ein Element ein Attribute mit ID key besitzt
	 * @param element
	 * @param key
	 * @return
	 */
	protected boolean containsAttribute(Element element, String key) {
		Sprite sprite = getOrCreateSpriteWithMap(element);
		ElementInformationMap map = sprite.getAttribute("ElementInformationMap", ElementInformationMap.class);
		return map.contains(key);
	}
	
	/**
	 * Gibt Wert eines Attributes mit ID key von einem Element elment zurück und castet diesen Wert zu T
	 * @param <T>
	 * @param element
	 * @param key
	 * @param clazz
	 * @return
	 */
	protected <T> T getAttribute(Element element, String key, Class<T> clazz) {
		Sprite sprite = getOrCreateSpriteWithMap(element);
		ElementInformationMap map = sprite.getAttribute("ElementInformationMap", ElementInformationMap.class);
		return map.getValueAs(key, clazz);
	}
	
	/**
	 * Prüft ob ein bestimmtes Attribut eines Elementes gleich einem Vergleichswert ist
	 * @param element
	 * @param key
	 * @param otherAttribute
	 * @return
	 */
	protected boolean isAttributeEqual(Element element, String key, Object otherAttribute) {
		if(!containsAttribute(element, key))
			return false;
		Object attribute = getAttribute(element, key, Object.class);
		if(attribute == null && otherAttribute == null)
			return true;
		else 
			
		return attribute.equals(otherAttribute);
	}
	
	/**
	 * Setzt ein Attribut mit ID key und Wert attribute zu einem bestimmten Element
	 * @param element
	 * @param key
	 * @param attribute
	 */
	protected void setAttribute(Element element, String key, Object attribute) {
		Sprite sprite = getOrCreateSpriteWithMap(element);
		ElementInformationMap map = sprite.getAttribute("ElementInformationMap", ElementInformationMap.class);
		map.setValue(key, attribute);
		displayAttributes(sprite);
	}
	
	private void displayAttributes(Sprite sprite) {
		if(sprite.hasAttribute("ElementInformationMap", ElementInformationMap.class)) {
			ElementInformationMap map = sprite.getAttribute("ElementInformationMap", ElementInformationMap.class);
			sprite.setAttribute("ui.label", map.toString());
		}
	}
	
	private Sprite getOrCreateSpriteWithMap(Element element) {
		Sprite sprite = spriteManager.getSprite(element.getId());
		if(sprite == null) {
			sprite = spriteManager.addSprite(element.getId());
			if(element instanceof Node)
				sprite.attachToNode(element.getId());
			else if(element instanceof Edge)
				sprite.attachToEdge(element.getId());
			else throw new IllegalArgumentException("element "+element.getClass()+" must of of type Node or Edge");
		}
		if(!sprite.hasAttribute("ElementInformationMap", ElementInformationMap.class)) {
			sprite.setAttribute("ElementInformationMap", new ElementInformationMap());
		}
		return sprite;
	}

}
