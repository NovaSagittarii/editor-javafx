package editor.object;

import editor.EditorApplication;
import editor.util.FileUtil;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static editor.object.Audio.SAMPLE_RATE;

public class Chart {
    public static final int GENERAL = 1, EDITOR = 2, METADATA = 3, DIFFICULTY = 4, EVENTS = 5, TIMING_POINTS = 6, HIT_OBJECTS = 7;
    public HashMap<String, String> metadata = new HashMap<>(); // String title, titleUnicode, artist, artistUnicode, creator, version, source, tags, bID, sID;
    public ArrayList<Note> notes = new ArrayList<>();
    public ArrayList<TimingPoint> timingPoints = new ArrayList<>();
    public float hp, cs, od, ar;
    private PImage bg, bgRaw;
    private Audio audio;
    private final String path;
    private String audioPath;
    private String bgPath;
    private final PApplet s;

    public Chart(PApplet sketch, File f) {
        s = sketch;
        int state = 0;
        path = f.getParentFile().getAbsolutePath();
        try {
            List<String> contents = FileUtil.readLines(f); // Iterate the result to print each line of the file.
            for (String line : contents) {
                System.out.println(line);
                switch(line){
                    case "[General]": state = GENERAL; break;
                    case "[Editor]": state = EDITOR; break;
                    case "[Metadata]": state = METADATA; break;
                    case "[Difficulty]": state = DIFFICULTY; break;
                    case "[Events]": state = EVENTS; break;
                    case "[TimingPoints]": state = TIMING_POINTS; break;
                    case "[HitObjects]": state = HIT_OBJECTS; break;
                    default:
                        switch (state) {
                            case GENERAL:
                                if (line.startsWith("AudioFilename: ")) {
                                    audioPath = line.replace("AudioFilename: ", "");
                                    audio = new Audio(new File(path + "/" + audioPath));
                                    audio.getClip().start();
                                }
                                break;
                            case METADATA:
                                metadata.put(line.replaceFirst(":.*$", ""), line.replaceFirst("[^:]+?:", ""));
                                break;
                            case DIFFICULTY:
                                if (line.startsWith("H")) hp = Float.parseFloat(line.replaceFirst("[^:]+?:", ""));
                                if (line.startsWith("C")) cs = Float.parseFloat(line.replaceFirst("[^:]+?:", ""));
                                if (line.startsWith("O")) od = Float.parseFloat(line.replaceFirst("[^:]+?:", ""));
                                if (line.startsWith("A")) ar = Float.parseFloat(line.replaceFirst("[^:]+?:", ""));
                                break;
                            case EVENTS:
                                if (line.startsWith("0,0,\"") && line.endsWith("\",0,0")) {
                                    bgPath = line.split("\"")[1];
                                    if (bgPath != null) bgRaw = s.loadImage(path + "/" + bgPath);
                                    cacheBackground();
                                }
                                break;
                            case TIMING_POINTS:
                                TimingPoint timingPoint = TimingPoint.fromString(line);
                                if (timingPoint != null) timingPoints.add(timingPoint);
                                break;
                            case HIT_OBJECTS:
                                Note note = Note.fromString(line);
                                if (note != null) notes.add(note);
                                break;
                        }
                }

                Collections.sort(this.notes);
                Collections.sort(this.timingPoints);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PImage getBackground() {
        if (bg == null || (bg.width != s.width || bg.height != s.height)) cacheBackground();
        return bg;
    }

    private void cacheBackground() {
        PGraphics g = s.createGraphics(s.width, s.height);
        g.beginDraw();
        g.imageMode(g.CENTER);
        g.background(0);
        if(bgRaw != null){
            g.image(bgRaw, s.width/2f, s.height/2f, (float) bgRaw.width*s.height/bgRaw.height, s.height);
            g.fill(0, 200);
            g.rect(0, 0, g.width, g.height);
        }
        g.endDraw();
        bg = g.get();
    }
    public void alignTimingPoints(){
        Collections.sort(this.timingPoints);

        UninheritedTimingPoint red = null;
        for(TimingPoint tp : timingPoints){
            if(tp instanceof UninheritedTimingPoint) red = (UninheritedTimingPoint) tp;
            else if(red != null) ((InheritedTimingPoint) tp).parent = red;
        }
    }
    public int getTime(){ return audio.getClip().getFramePosition() / (SAMPLE_RATE/1000); } // returns in MS
    public int getFramePosition(){ return audio.getClip().getFramePosition(); }
    public int getFrameLength(){ return audio.getClip().getFrameLength(); }
    public String getName(){ return metadata.get("Version"); }
    public String export(){
        return String.join(",", metadata.values()) + path + "|" + audioPath + "|" + bgPath;
    }

    public float[] getSamples() {
        return audio.getSamples();
    }

    public void draw() {
        PApplet sketch = EditorApplication.getInstance();
        sketch.background(this.getBackground());
        sketch.fill(0, 0, 0, 100);
        sketch.ellipse(sketch.mouseX, sketch.mouseY, 15, 15);
        sketch.fill(255);
        sketch.text(this.getTime(), 100, 200);
        int f = this.getFramePosition();
        float[] s = this.getSamples();
        //stroke(255);
        sketch.noStroke();
        sketch.fill(255);
        int sampledDuration = 10000, sampledFrames = sampledDuration * SAMPLE_RATE / 1000, sampledChunkResolution = 3, sampledChunks = sketch.height / sampledChunkResolution, sampledChunkSize = sampledFrames / sampledChunks;

        int k = 0;
        for(int i = f; i < Math.min(this.getFrameLength(), f + sampledFrames); i += sampledChunkSize){
            double sum = 0;
            for(int j = i; j < Math.min(this.getFrameLength(), i + sampledChunkSize); j ++) sum += Math.abs(s[j]);
            float pos = (float)sum/sampledChunkSize/32768.0f * 100 + 4;
            sketch.rect(200, k*sampledChunkResolution, pos, sampledChunkResolution);
            k ++;
        }
    }
}
