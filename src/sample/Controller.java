package sample;

import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class Controller {
    public Canvas canvas;
    public Menu list;
    private Stage primaryStage;
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    public void open(ActionEvent actionEvent) {
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if(selectedDirectory != null) {
            System.out.println(selectedDirectory.getAbsolutePath());
            DirectoryParser d = new DirectoryParser(selectedDirectory.getAbsolutePath());
            for(String s : d.getCharts().keySet()){
                list.getItems().add(new MenuItem(s));
            }
        }
    }
    public void exit(ActionEvent actionEvent) {

    }
    public void setStage(Stage stage) {
        primaryStage = stage;
    }
}
