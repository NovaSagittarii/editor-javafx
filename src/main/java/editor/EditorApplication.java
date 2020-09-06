package editor;

import editor.object.Chart;
import editor.parser.DirectoryParser;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;

import processing.core.PApplet;
import processing.javafx.PSurfaceFX;

import java.io.File;

/* public class EditorApplication extends Application {
    public static PSurfaceFX surface;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Canvas canvas = (Canvas) surface.getNative(); // boilerplate
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D(); // boilerplate
        surface. = graphicsContext; // boilerplate

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("editor.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
} */ // old JavaFX + processing implementation

public class EditorApplication extends PApplet {
    private final int AWAIT = 0, SELECT = 1, EDIT = 2;
    private DirectoryParser dp;
    private Chart chart;
    private int state = AWAIT;
    private boolean mp;

    public void settings(){
        size(500, 500);
    }

    public void setup(){
        folderSelected(null);
        noLoop();
    }

    public void folderSelected(File selectedDirectory){
        if(selectedDirectory != null) {
            System.out.println(selectedDirectory.getAbsolutePath());
            dp = new DirectoryParser(selectedDirectory.getAbsolutePath());
            loop();
            state = SELECT;
        }else selectFolder("Select a folder to process:", "folderSelected");
    }

    public void draw(){
        switch(state){
            case SELECT:
                background(200);
                fill(0);
                int i = 0;
                for(String s : dp.getCharts().keySet()){
                    text(s, 25, 15+20*++i);
                    if(mouseY < 15+20*i && mouseY > 5+20*i){
                        rect(5, 5+20*i, 10, 10);
                        if(mp){
                            chart = new Chart(new File(dp.getCharts().get(s)));
                            System.out.println(chart.export());
                        }
                    }
                }
            break;
        }
        mp = false;
    }

    public void mousePressed(){
        mp = true;
    }

    public static void main(String[] args){
        String[] processingArgs = {"EditorApplication"};
        EditorApplication sketch = new EditorApplication();
        PApplet.runSketch(processingArgs, sketch);
    }
}
