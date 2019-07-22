package de.haw_hamburg.gkap.algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Diese Klasse verwaltet eine Liste von Algorithmen und führt diese aus.
 * Zunächst zwischen jeder Ausführung der Algorithmen wird delayInMillis Millisekunden gewartet
 */
public class AsyncAlgorithmManager implements Runnable {

	private static AsyncAlgorithmManager INSTANCE;
	private static long delayInMillis = 100L;
	private boolean running = false;
	private List<AbstractAlgorithm> algorithms = new ArrayList<>();

	private AsyncAlgorithmManager() {}
		
	public void removeAll() {
		algorithms.forEach(algo -> algo.doTermination());
		algorithms.clear();
	}
	
	public void registerAlgorithm(AbstractAlgorithm algo) {
		algorithms.add(algo);
	}
	
	public void unregisterAlgorithm(AbstractAlgorithm algo) {
		if(algorithms.contains(algo)) {
			algo.doTermination();
			algorithms.remove(algo);
		}
	}
	
	public void start() {
		Thread t = new Thread(this);
		this.running = true;
		t.start();
	}
	
	public void stop() {
		running = false;
	}
	
	@Override
	public void run() {
		while(running) {
			
			Iterator<AbstractAlgorithm> itr = algorithms.iterator();
			while (itr.hasNext()) {
				try {
					AbstractAlgorithm algo = itr.next();
					if(!algo.isInitialized()) {
						algo.doInitialize();
						continue;
					}
					if (!algo.doNextStep()) {
						algo.doTermination();
						itr.remove();
					}
				} catch (Exception e) {
					itr.remove();
					e.printStackTrace();
				}
			}
			Thread.currentThread();
			try {
				Thread.sleep(delayInMillis);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static AsyncAlgorithmManager getInstance() {
		return INSTANCE;
	}
	
	public static void init() {
		if(INSTANCE != null) {
			INSTANCE.stop();
			INSTANCE.removeAll();
		}
		INSTANCE = new AsyncAlgorithmManager();
		INSTANCE.start();
	}
}
