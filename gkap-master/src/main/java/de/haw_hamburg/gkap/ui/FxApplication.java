package de.haw_hamburg.gkap.ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javafx.scene.control.ScrollPane;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;

import de.haw_hamburg.gkap.algorithm.AsyncAlgorithmManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FxApplication extends Application {

	private Graph graph;
	private Stage stage;
	private FxViewPanel graphViewPanel;
	private ScrollPane controlsBox;
	private SplitPane mainPane;
	private static final String fxmlFolderPath = "fxml/";
	private static final String[] fxmlFiles = new String[] {"SettingsTab.fxml", "FileTab.fxml", "AlgorithmsTab.fxml", "GeneratorTab.fxml"};
	private static FxApplication INSTANCE;

	public static void main(String[] args) {
		FxApplication.launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		INSTANCE = this;
		AsyncAlgorithmManager.init();
		this.controlsBox = new ScrollPane(getControlsBox());
        this.mainPane = new SplitPane();
        setDisplayedGraph(new MultiGraph("HAW-Graph"));

        BorderPane mainPaneWithMenu = new BorderPane(mainPane);
        Scene scene = new Scene(mainPaneWithMenu, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
	}
	
	public void setDisplayedGraph(Graph graph) {
		this.graph = graph;
        FxViewer viewer = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        FxViewPanel panel = (FxViewPanel) viewer.addDefaultView(false, new FxGraphRenderer());
        panel.setMouseManager(new CustomMouseManager());
        graph.setAttribute("ui.stylesheet", GraphStylesheet.STYLESHEET);
        this.graphViewPanel = panel;
        updateMainPane();
	}
	
	private void updateMainPane() {
		mainPane.getItems().clear();
		mainPane.getItems().add(graphViewPanel);
		mainPane.getItems().add(controlsBox);
		mainPane.setDividerPositions(0.7, 0.3);
	}
	
	private VBox getControlsBox() throws IOException {
		VBox box = new VBox();
        ClassLoader classLoader = new FxApplication().getClass().getClassLoader();
        for (String fxmlFile : fxmlFiles) {
            box.getChildren().add(FXMLLoader.load(classLoader.getResource(fxmlFolderPath + fxmlFile)));
        }
		return box;
	}

	public void clearGraph() {
		AsyncAlgorithmManager.init();
		graph.clear();
		graph.setAttribute("ui.stylesheet", GraphStylesheet.STYLESHEET);
	}
    
	public Graph getGraph() {
		return graph;
	}
	
	public Stage getStage() {
		return  stage;
	}
	
	public static FxApplication getInstance() {
		return INSTANCE;
	}

	public FxViewPanel getGraphViewPanel() {
		return graphViewPanel;
	}
}
