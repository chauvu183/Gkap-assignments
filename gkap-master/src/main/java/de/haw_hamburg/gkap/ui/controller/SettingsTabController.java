package de.haw_hamburg.gkap.ui.controller;

import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;

import de.haw_hamburg.gkap.algorithm.AsyncAlgorithmManager;
import de.haw_hamburg.gkap.ui.FxApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;

public class SettingsTabController {
	/*
	@FXML
	Slider layoutForce;
	@FXML
	Slider layoutQuality;
	@FXML
	Slider layoutStabilization;
	@FXML
	Slider layoutGravity;
	*/
	@FXML
	Button resetViewButton;
	@FXML
	CheckBox autoLayout;
	@FXML
	Button clearGraph;

	@FXML
	public void initialize() {
		resetViewButton.setOnAction(e -> resetView());
		clearGraph.setOnAction(e -> clearGraph());
		autoLayout.addEventFilter(ActionEvent.ACTION, e -> setAutoLayout(autoLayout.isSelected()));

		/*
		layoutForce.valueProperty().addListener((observableValue, number, t1) -> {
			setGraphAttribute("layout.force", number.doubleValue());
		});
		layoutQuality.valueProperty().addListener((observableValue, number, t1) -> {
			setGraphAttribute("layout.quality", number.doubleValue());
		});
		layoutGravity.valueProperty().addListener((observableValue, number, t1) -> {
			setGraphAttribute("layout.gravity", number.doubleValue());
		});
		layoutStabilization.valueProperty().addListener((observableValue, number, t1) -> {
			setGraphAttribute("layout.stabilization-limit", number.doubleValue());
		});
		*/
	}

	public void clearGraph() {
		FxApplication.getInstance().clearGraph();
		/*
		layoutForce.setValue(1);
		layoutQuality.setValue(3);
		layoutGravity.setValue(0);
		layoutStabilization.setValue(0.9);
		*/
		resetView();
	}

	private void setGraphAttribute(String attribute, Object value) {
		FxApplication.getInstance().getGraph().setAttribute(attribute, value);
	}

	private void setAutoLayout(boolean doLayout) {
		Viewer viewer = FxApplication.getInstance().getGraphViewPanel().getViewer();
		if (doLayout) {
			viewer.enableAutoLayout();
		} else {
			viewer.disableAutoLayout();
		}
	}

	private void resetView() {
		FxApplication.getInstance().getGraphViewPanel().getCamera().resetView();
	}
}
