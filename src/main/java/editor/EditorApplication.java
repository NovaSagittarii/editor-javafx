package editor;

import editor.object.Chart;
import editor.parser.DirectoryParser;
/* import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas; */

import processing.core.PApplet;

import java.io.File;
import java.util.Arrays;

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
    static final int SAMPLE_RATE = 44100;
    private final int AWAIT = 0, SELECT = 1, COMPOSE = 2;
    private DirectoryParser dp;
    private Chart chart;
    private int state = AWAIT;
    private boolean mp;

    public void settings(){
        noSmooth();
        size(500, 500);
    }

    public void setup(){
        folderSelected(null);
        frameRate(240);
        imageMode(CENTER);
        strokeCap(SQUARE);
        surface.setResizable(true);
        rectMode(CENTER);
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
                int y = 0;
                for(String s : dp.getCharts().keySet()){
                    fill(0);
                    ++ y;
                    if(mouseY < 15+20*y && mouseY > 5+20*y){
                        rect(10, 10+20*y, 10, 10);
                        fill(20);
                        if(mp){
                            chart = new Chart(this, new File(dp.getCharts().get(s)));
                            System.out.println(chart.export());
                            // System.out.println(Arrays.toString(chart.getSamples()));
                            state = COMPOSE;
                            textAlign(CENTER, CENTER);
                        }
                    }
                    text(s, 25, 15+20*y);
                }
            break;
            case COMPOSE:
                background(chart.getBackground());
                fill(0, 0, 0, 100);
                ellipse(mouseX, mouseY, 15, 15);
                fill(255);
                text(chart.getTime(), 100, 200);
                int f = chart.getFramePosition();
                float[] s = chart.getSamples();
                //stroke(255);
                noStroke();
                fill(255);
                int sampledDuration = 10000;
                int sampledFrames = sampledDuration*SAMPLE_RATE/1000;
                int sampledChunkResolution = 3;
                int sampledChunks = height/sampledChunkResolution;
                int sampledChunkSize = sampledFrames/sampledChunks;
                int k = 0;
                for(int i = f; i < Math.min(chart.getFrameLength(), f + sampledFrames); i += sampledChunkSize){
                    double sum = 0;
                    for(int j = i; j < Math.min(chart.getFrameLength(), i + sampledChunkSize); j ++) sum += Math.abs(s[j]);
                    float pos = (float)sum/sampledChunkSize/32768.0f * 100 + 4;
                    rect(200, k*sampledChunkResolution, pos, sampledChunkResolution);
                    k ++;
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
