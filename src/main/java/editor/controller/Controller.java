package editor.controller;

import editor.parser.DirectoryParser;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class Controller {
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    public Canvas canvas;
    public Menu list;
    private Stage primaryStage;

    public void open(ActionEvent actionEvent) {
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory != null) {
            System.out.println(selectedDirectory.getAbsolutePath());
            DirectoryParser d = new DirectoryParser(selectedDirectory.getAbsolutePath());
            for (String s : d.getCharts().keySet()) {
                MenuItem menuItem = new MenuItem(s);
                menuItem.setOnAction(event -> {
                    String PATH = d.getCharts().get(((MenuItem) event.getSource()).getText());
                    System.out.println(PATH);
                    // Chart chart = new Chart(new File(PATH));
                    // System.out.println(chart.export());
                });
                list.getItems().add(menuItem);
            }
        }
    }

    public void exit(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void setStage(Stage stage) {
        primaryStage = stage;
    }
}