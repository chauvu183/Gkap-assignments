package de.haw_hamburg.gkap.ui.controller;

import de.haw_hamburg.gkap.algorithm.*;
import de.haw_hamburg.gkap.ui.FxApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.graphstream.graph.Graph;

public class AlgorithmTabController {

    @FXML Button primSpanningTreeNoAnimation;
    @FXML Button kruskalSpanningTreeNoAnimation;
    @FXML Button primSpanningTree;
	@FXML TextField bfsSource;
	@FXML TextField bfsTarget;
	@FXML Button bfsStart;
	@FXML Button kruskal;
	@FXML Button fleury;
	@FXML Button hierholzer;

	@FXML
	public void initialize() {
		bfsStart.setOnAction(e -> runBFS());
		primSpanningTree.setOnAction(e -> runPrim());
		primSpanningTreeNoAnimation.setOnAction(e -> runPrimNoVisual());
		kruskalSpanningTreeNoAnimation.setOnAction(e -> runKriskalNoVisual());
		kruskal.setOnAction(e->runKruskal());
		fleury.setOnAction(e-> runFleury());
		hierholzer.setOnAction(e->runHierholzer());

	}
	
	private void runBFS() {
		AsyncAlgorithmManager.getInstance().removeAll();
		String source = bfsSource.getText();
		String target = bfsTarget.getText();
		try {
			BreadthFirstSearch bfs = new BreadthFirstSearch(FxApplication.getInstance().getGraph(), source, target);
			AsyncAlgorithmManager.getInstance().registerAlgorithm(bfs);
		} catch(RuntimeException e) {
			e.printStackTrace();
			Alert errorAlert = new Alert(AlertType.ERROR);
			errorAlert.setHeaderText(e.getClass().getName());
			errorAlert.setContentText(e.getMessage());
			errorAlert.showAndWait();
		}
	}

	private void runHierholzer() {
		AsyncAlgorithmManager.getInstance().removeAll();
		Hierholzer hierholzer = new Hierholzer(FxApplication.getInstance().getGraph());
		AsyncAlgorithmManager.getInstance().registerAlgorithm(hierholzer);
	}

	private void runPrim() {
		AsyncAlgorithmManager.getInstance().removeAll();
			Prim prim = new Prim(FxApplication.getInstance().getGraph());
			AsyncAlgorithmManager.getInstance().registerAlgorithm(prim);
	}

	private void runPrimNoVisual() {
		AsyncAlgorithmManager.getInstance().removeAll();
		Graph graph = FxApplication.getInstance().getGraph();
		Prim prim = new Prim(graph, false);

		prim.doInitialize();
		while(prim.doNextStep()) {}
		prim.doTermination();

		Alert errorAlert = new Alert(AlertType.INFORMATION);
		errorAlert.setHeaderText("Prim finished");
		errorAlert.setContentText(String.format("Runtime (ms): %d\nFound valid spanning tree: %b\nTotal Weight: %f\nNumber of Tree Edges: %d\nNumber of nodes: %d",
				prim.getRuntime(),
				prim.isValidSpanningTree(),
				prim.getSpanningTreeWeight(),
				prim.getSpanningTreeEdges().size(),
				graph.getNodeCount()));
		errorAlert.showAndWait();
	}

	private void runKriskalNoVisual() {
		AsyncAlgorithmManager.getInstance().removeAll();
		Graph graph = FxApplication.getInstance().getGraph();
		Kruskal kruskal = new Kruskal(graph, false);

		kruskal.doInitialize();
		while(kruskal.doNextStep()) {}
		kruskal.doTermination();

		Alert errorAlert = new Alert(AlertType.INFORMATION);
		errorAlert.setHeaderText("Kruskal finished");
		errorAlert.setContentText(String.format("Runtime (ms): %d\nFound valid spanning tree: %b\nTotal Weight: %f\nNumber of Tree Edges: %d\nNumber of nodes: %d",
				kruskal.getRuntime(),
				kruskal.isValidSpanningTree(),
				kruskal.getSpanningTreeWeight(),
				kruskal.getSpanningTreeEdges().size(),
				graph.getNodeCount()));
		errorAlert.showAndWait();
	}

	private void runKruskal() {
		AsyncAlgorithmManager.getInstance().removeAll();
		Kruskal kruskal = new Kruskal(FxApplication.getInstance().getGraph(), true);
		AsyncAlgorithmManager.getInstance().registerAlgorithm(kruskal);
	}

	private void runFleury(){
		AsyncAlgorithmManager.getInstance().removeAll();
		Fleury fleury = new Fleury(FxApplication.getInstance().getGraph());
		AsyncAlgorithmManager.getInstance().registerAlgorithm(fleury);
	}
}
