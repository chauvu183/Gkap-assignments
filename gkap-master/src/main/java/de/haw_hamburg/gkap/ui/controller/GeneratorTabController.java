package de.haw_hamburg.gkap.ui.controller;

import de.haw_hamburg.gkap.algorithm.AsyncAlgorithmManager;
import de.haw_hamburg.gkap.algorithm.BreadthFirstSearch;
import de.haw_hamburg.gkap.algorithm.Prim;
import de.haw_hamburg.gkap.persistence.loader.GraphStreamGraphReceiver;
import de.haw_hamburg.gkap.persistence.loader.generator.EulergraphGenerator;
import de.haw_hamburg.gkap.persistence.loader.generator.GraphGenerator;
import de.haw_hamburg.gkap.ui.FxApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;

import java.util.Random;

public class GeneratorTabController {

    @FXML public CheckBox eulerGraph;
    @FXML CheckBox randomSeed;
	@FXML TextField seed;
	@FXML TextField nodeCount;
	@FXML TextField edgeCount;
	@FXML CheckBox directed;
	@FXML TextField max_weight_edges;
	@FXML TextField max_weight_nodes;
	@FXML Button generate;

	@FXML
	public void initialize() {
		generate.setOnAction(e -> generate());
	}
	
	private void generate() {
		AsyncAlgorithmManager.getInstance().removeAll();
		Graph graph = FxApplication.getInstance().getGraph();
		FxApplication.getInstance().clearGraph();

		try {
			int nodeCount = Integer.valueOf(this.nodeCount.getText());
			int edgeCount = Integer.valueOf(this.edgeCount.getText());
			int seed = Integer.valueOf(this.seed.getText());
			boolean directed = this.directed.isSelected();
			int max_weight_edges = Integer.valueOf(this.max_weight_edges.getText());
			int max_weight_nodes = Integer.valueOf(this.max_weight_nodes.getText());

			if(randomSeed.isSelected()) {
				seed = new Random().nextInt(9000000);
			}

			GraphStreamGraphReceiver graphReceiver = new GraphStreamGraphReceiver(graph);
			if(eulerGraph.isSelected()) {
				EulergraphGenerator eulergraphGenerator = new EulergraphGenerator(graphReceiver);
				eulergraphGenerator.eulerGenerate(edgeCount, seed, max_weight_edges, max_weight_nodes, directed);
			} else {
				GraphGenerator graphGenerator = new GraphGenerator(graphReceiver);
				graphGenerator.generate(nodeCount, edgeCount, seed, directed, max_weight_edges, max_weight_nodes);
			}
		} catch(RuntimeException e) {
			e.printStackTrace();
			Alert errorAlert = new Alert(AlertType.ERROR);
			errorAlert.setHeaderText(e.getMessage());
			errorAlert.setContentText(e.getMessage());
			errorAlert.showAndWait();
		}
	}
}
