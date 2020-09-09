package editor;

import editor.object.Chart;
import editor.object.LongNote;
import editor.object.Note;
import editor.parser.DirectoryParser;
import editor.util.EditorUtil;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import processing.core.PApplet;

import java.io.File;
import java.util.TreeSet;

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
    private EditorUtil e;
    private final TreeSet<Integer> keys = new TreeSet<>();
    private int state = AWAIT;
    private float zoom = 0.2f;
    private boolean mp;

    public void settings(){
        noSmooth();
        size(1280, 720);
    }

    public void setup() {
        folderSelected(null);
        e = new EditorUtil(4, width, height);
        frameRate(90);
        imageMode(CENTER);
        strokeCap(SQUARE);
        surface.setResizable(true);
        rectMode(CENTER);
        noLoop();
    }

    public void folderSelected(File selectedDirectory) {
        if (selectedDirectory != null) {
            System.out.println(selectedDirectory.getAbsolutePath());
            dp = new DirectoryParser(selectedDirectory.getAbsolutePath());
            loop();
            state = SELECT;
        } else selectFolder("Select a folder to process:", "folderSelected");
    }

    public void draw() {
        if(chart != null && (width != e.w || height != e.h)) e.calibrate((int)chart.cs, width, height);
        switch (state) {
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
                            e.calibrate((int)chart.cs, width, height);
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
                text((int)(Math.floor(chart.getTime())), 100, 200);
                text((int)frameRate + "fps", 100, 250);
                int chartBottom = height - 20;
                double f = Chart.timeToFrames(chart.getTime() - ((float)chartBottom-e.yo)/zoom);
                float[] s = chart.getSamples();
                //stroke(255);
                noStroke();
                fill(100);
                int sampledDuration = (int)(chartBottom/zoom);
                int sampledFrames = sampledDuration*SAMPLE_RATE/1000;
                int sampledChunkResolution = 1;
                int sampledChunks = chartBottom/sampledChunkResolution;
                int sampledChunkSize = sampledFrames/sampledChunks;
                int k = 0;
                for(double i = f; i < Math.min(chart.getFrameLength(), f + sampledFrames); i += sampledChunkSize){
                    if(i < 0){ k ++; continue; }
                    double sum = 0;
                    for(double j = i; j < Math.min(chart.getFrameLength(), i + sampledChunkSize); j ++) sum += Math.abs(s[(int) j]);
                    float pos = (float)sum/sampledChunkSize/32768.0f * 100 + 1;
                    rect(e.hitObjectCenter, (chartBottom-k)*sampledChunkResolution, pos, sampledChunkResolution);
                    k ++;
                }
                fill(50);
                rect(e.hitObjectCenter, (float)height/2, 1, height);
                ellipse(mouseX, mouseY, mousePressed ? 15 : 10, 10);
                stroke(255);
                line(300, 200, (float) (300+100*Math.cos((double)frameCount/50)), (float) (200+100*Math.sin((double)frameCount/50)));
                line(e.leftBorder, e.yo, e.rightTimingPoint, e.yo);
                line(e.leftLiveBorder, e.yo, e.rightBorder, e.yo);
                line(e.leftTimingPoint, e.yo-5, e.leftTimingPoint, e.yo+5);

                final int NoteHeight = 25; // to be changed with var when doing skinning
                for(Note n : chart.notes){
                    final int YPOS = (int)((chart.getTime() - n.time)*zoom + e.yo);
                    if(YPOS < 0) break;
                    fill(0, 100);
                    if(mouseX > e.columnBoundary[n.column] && mouseX <= e.columnBoundary[n.column+1]){
                        if(mouseY > YPOS-NoteHeight && mouseY < YPOS){
                            fill(255, 100);
                        }
                    }
                    if(n instanceof LongNote){
                        final int YEND = (int)((chart.getTime() - ((LongNote) n).endTime)*zoom + e.yo);
                        if(mouseX > e.columnBoundary[n.column] && mouseX <= e.columnBoundary[n.column+1]){
                            if(mouseY > YEND-NoteHeight && mouseY < YPOS){
                                fill(255, 100);
                            }
                        }
                        rect(e.x[n.column], (YPOS+YEND)/2f - NoteHeight/2f, e.columnWidth-15, YPOS-YEND-NoteHeight);
                        if(YEND > height) continue;
                        rect(e.x[n.column], YEND-NoteHeight/2f, e.columnWidth-5, NoteHeight);
                    }else if(YPOS > height) continue;
                    rect(e.x[n.column], YPOS-NoteHeight/2f, e.columnWidth-5, NoteHeight);
                }

                noStroke();
                fill(255, 80);
                rect(width/2f, height-10, width, 20);
                stroke(255);
                line(0, height-10, width, height-10);
                line(chart.progressRatio() * width, height-20, chart.progressRatio() * width, height);
                break;
        }
        mp = false;
    }

    public void mousePressed() {
        // if(chart != null) chart.getAudio().getClip().start();
        if(chart != null){
            if(mouseY > height-20) seek(mouseXDuration());
        }
        mp = true;
    }

    public void mouseDragged(){
        if(chart != null){
            if(mouseY > height-20) seek(mouseXDuration());
        }
    }

    private void seek(Duration d){
        if(chart != null){
            if (chart.getAudioPlayer().getStatus() == MediaPlayer.Status.PLAYING) chart.getAudioPlayer().pause();
            chart.getAudioPlayer().seek(d);
        }
    }

    private Duration mouseXDuration(){
        return new Duration((double) mouseX / width * chart.getAudioPlayer().getTotalDuration().toMillis());
    }

    public void mouseReleased() {
        // if(chart != null) chart.getAudio().getClip().stop();
    }

    public void keyPressed() {
        keys.add(keyCode);
        if(keyCode == 32 && chart.getAudioPlayer().getStatus() == MediaPlayer.Status.PLAYING){
            chart.getAudioPlayer().pause();
            chart.getAudioPlayer().seek(chart.getAudioPlayer().getCurrentTime());
        }
        else chart.getAudioPlayer().play();
    }

    public void keyReleased() {
        keys.remove(keyCode);
    }

    public static void main(String[] args){
        String[] processingArgs = {"EditorApplication"};
        EditorApplication sketch = new EditorApplication();
        PApplet.runSketch(processingArgs, sketch);
    }
}
