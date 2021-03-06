package editor.object;

import editor.util.FileUtil;
import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaPlayer;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Chart {
    public static final int GENERAL = 1, EDITOR = 2, METADATA = 3, DIFFICULTY = 4, EVENTS = 5, TIMING_POINTS = 6, HIT_OBJECTS = 7;
    public HashMap<String, String> metadata = new HashMap<>(); // String title, titleUnicode, artist, artistUnicode, creator, version, source, tags, bID, sID;
    public TreeSet<Note> notes = new TreeSet<>();
    public TimingPointTimeline timingPoints = new TimingPointTimeline();
    public TimingPointTimeline activeTimingPoints = new TimingPointTimeline();
    public Selection selection = new Selection();
    public float hp, od, ar; // HP - OverallDifficulty - ApproachRate
    public int cs; // CircleSize | KeyCount
    public int selectionId;
    private PImage bg, bgRaw;
    private Audio audio;
    private final String path;
    private String audioPath;
    private String bgPath;
    private final PApplet s;
    /* TODO: make image cache of the waveform and redraw as necessary */

    public Chart(PApplet sketch, File f) {
        s = sketch;
        int state = 0;
        path = f.getParentFile().getAbsolutePath();
        s.getSurface().setTitle("EditorApplication | editing " + f.getName());
        int ColumnWidth = 128; // 512 / 4
        try {
            List<String> contents = FileUtil.readLines(f); // Iterate the result to print each line of the file.
            for (String line : contents) {
                // System.out.println(state + " | " + line);
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
                                    // audio.getClip().start();
                                }
                                break;
                            case METADATA:
                                metadata.put(line.replaceFirst(":.*$", ""), line.replaceFirst("[^:]+?:", ""));
                                break;
                            case DIFFICULTY:
                                if (line.startsWith("H")) hp = Float.parseFloat(line.replaceFirst("[^:]+?:", ""));
                                if (line.startsWith("C")){
                                    cs = Integer.parseInt(line.replaceFirst("[^:]+?:", ""));
                                    ColumnWidth = 512/cs;
                                }
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
                                if (timingPoint != null){
                                    addTimingPoint(timingPoint);
                                }
                                break;
                            case HIT_OBJECTS:
                                Note note = Note.fromString(line, ColumnWidth);
                                if (note != null) notes.add(note);
                                break;
                        }
                }
            }
            alignTimingPoints();
            updateCurrentTimingPoint();
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
        if(timingPoints.size() < 2) return;
        UninheritedTimingPoint red = null;
        for(TimingPoint tp : timingPoints){
            if(tp instanceof UninheritedTimingPoint) red = (UninheritedTimingPoint) tp;
            else if(red != null){
                tp.mspb = red.mspb;
                ((InheritedTimingPoint) tp).parent = red;
            }
        }
    }
    public void updateCurrentTimingPoint(){
        double now = getTime();
        timingPoints.update(now);
        activeTimingPoints.update(now);
    }
    public boolean select(Note n){
        n.selected = selectionId;
        return selection.select(n);
    }
    /* TODO: public void selectArea(int x1, int x2, int start, int end){} */
    public void addNote(Note n){
        notes.add(n);
    }
    public boolean removeNote(Note n){ return notes.remove(n); }
    public boolean deselect(Note n){
        n.selected = null;
        return selection.deselect(n);
    }
    public void deselectAll(){
        selectionId ++;
        selection.deselectAll();
    }
    public boolean hasSelected(Note n){ return n.selected != null && n.selected == selectionId; }

    public void addTimingPoint(TimingPoint tp){
        if(tp instanceof UninheritedTimingPoint) activeTimingPoints.add(tp);
        timingPoints.add(tp);
    }

    static public double timeToFrames(double millis){ return millis * ((double)Audio.SAMPLE_RATE/1000.0); }
    public double getTime(){ return getAudioPlayer().getCurrentTime().toMillis(); } // returns in MS
    public float progressRatio(){ return (float)(getTime() / getAudioPlayer().getTotalDuration().toMillis()); }
    public int getFramePosition(){ return (int)(getAudioPlayer().getCurrentTime().toMillis() * ((double)Audio.SAMPLE_RATE/1000.0)); }
    public int getFrameLength(){ return audio.getSamples().length; }
    public Audio getAudio(){ return audio; }
    public MediaPlayer getAudioPlayer(){ return audio.getMediaPlayer(); }
    public String getName(){ return metadata.get("Version"); }
    public String export(){
        return String.join(",", metadata.values()) + path + "|" + audioPath + "|" + bgPath;
    }

    public float[] getSamples() {
        return audio.getSamples();
    }
}
