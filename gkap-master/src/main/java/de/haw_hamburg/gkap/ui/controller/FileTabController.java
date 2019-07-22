package de.haw_hamburg.gkap.ui.controller;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javafx.scene.control.TextArea;
import org.graphstream.graph.Graph;

import de.haw_hamburg.gkap.persistence.loader.GraphLoader;
import de.haw_hamburg.gkap.persistence.loader.GraphStreamGraphReceiver;
import de.haw_hamburg.gkap.persistence.writer.GraphStreamGraphPersistor;
import de.haw_hamburg.gkap.persistence.writer.SimpleGraphWriter;
import de.haw_hamburg.gkap.ui.FxApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import org.graphstream.graph.implementations.MultiGraph;

public class FileTabController {

	@FXML
	public TextArea injectField;
	@FXML
	public Button injectButton;
	@FXML
	public Button printButton;
	@FXML
	Button loadFile;
	@FXML
	Button saveFile;

	@FXML
	public void initialize() {
		loadFile.setOnAction(e -> loadFileAction());
		saveFile.setOnAction(e -> saveFileAction());
		injectButton.setOnAction(e -> inject());
		printButton.setOnAction(e -> print());
	}

	private void loadFileAction() {
		Optional<List<File>> graphFiles = selectGraphFile();
		graphFiles.ifPresent(files -> files.forEach(file -> loadGraphFromFile(file)));
	}

	private void saveFileAction() {
		Optional<File> file = selectGraphFileForWrite();
		if (file.isPresent())
			saveGraphToFile(file.get());
	}

	private void print() {
		Graph graph = FxApplication.getInstance().getGraph();
		StringWriter stringWriter = new StringWriter();
		SimpleGraphWriter writer = new SimpleGraphWriter(stringWriter);
		GraphStreamGraphPersistor graphPersistor = new GraphStreamGraphPersistor(writer, graph);
		try {
			graphPersistor.persist();
			stringWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		injectField.setText(stringWriter.toString());
	}

	private void inject() {
		String text = injectField.getText();
		Graph graph = FxApplication.getInstance().getGraph();
		InputStream in = new ByteArrayInputStream(text.getBytes());
		GraphStreamGraphReceiver graphReceiver = new GraphStreamGraphReceiver(graph);
		GraphLoader loader = new GraphLoader(in, graphReceiver);

		try {
			loader.load(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		graphReceiver.end();
	}

	private Optional<List<File>> selectGraphFile() {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Graph File", "*.graph");
		fileChooser.getExtensionFilters().add(extFilter);

		return Optional.ofNullable(fileChooser.showOpenMultipleDialog(FxApplication.getInstance().getStage()));
	}

	private Optional<File> selectGraphFileForWrite() {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Graph File (*.graph)", "*.graph");
		fileChooser.getExtensionFilters().add(extFilter);

		return Optional.ofNullable(fileChooser.showSaveDialog(FxApplication.getInstance().getStage()));
	}

	private void saveGraphToFile(File file) {
		// try {
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
			SimpleGraphWriter writer = new SimpleGraphWriter(bufferedWriter);
			GraphStreamGraphPersistor graphPersistor = new GraphStreamGraphPersistor(writer,FxApplication.getInstance().getGraph());
			graphPersistor.persist();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadGraphFromFile(File file) {
		Graph graph = FxApplication.getInstance().getGraph();
		GraphStreamGraphReceiver graphSource = new GraphStreamGraphReceiver(graph);

		try (InputStream in = new FileInputStream(file)) {
			// create GraphLoader with Receiver and load
			GraphLoader loader = new GraphLoader(in, graphSource);
			// lade graph
			loader.load(false);
			// schließe GraphStreamGraphReceiver
			graphSource.end();
			// schließe InputStream
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
