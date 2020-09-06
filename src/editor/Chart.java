package editor;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Chart {
    private String audio, bg;
    private String title, titleUnicode, artist, artistUnicode, creator, version, source, tags, bID, sID;
    private int hp, cs, od, ar;

    public final int GENERAL = 1;
    public final int EDITOR = 2;
    public final int METADATA = 3;
    public final int DIFFICULTY = 4;
    public final int EVENTS = 5;
    public final int TIMING_POINTS = 6;
    public final int HIT_OBJECTS = 7;
    public Chart(File f){
        int state = 0;
        try {
            List<String> contents = FileUtils.readLines(f, "UTF-8");

            // Iterate the result to print each line of the file.
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
                        switch(state){
                            case GENERAL:
                                if(line.startsWith("AudioFilename: ")) audio = line.replace("AudioFilename: ", "");
                                break;
                            case METADATA:
                                if(line.startsWith("Title:")) title = line.replace("Title:", "");
                                if(line.startsWith("TitleUnicode:")) titleUnicode = line.replace("TitleUnicode:", "");
                                if(line.startsWith("Artist:")) artist = line.replace("Artist:", "");
                                if(line.startsWith("ArtistUnicode:")) artistUnicode = line.replace("ArtistUnicode:", "");
                                if(line.startsWith("Version:")) version = line.replace("Version:", "");
                                break;
                            case DIFFICULTY:
                                if(line.startsWith("H")) hp = Integer.parseInt(line.replace("HPDrainRate:", ""));
                                if(line.startsWith("C")) cs = Integer.parseInt(line.replace("CircleSize:", ""));
                                if(line.startsWith("O")) od = Integer.parseInt(line.replace("OverallDifficulty:", ""));
                                if(line.startsWith("A")) ar = Integer.parseInt(line.replace("ApproachRate:", ""));
                                break;
                            case EVENTS:
                                if(line.startsWith("0,0,\"") && line.endsWith("\",0,0")) bg = line.split("\"")[1];
                                break;
                            case TIMING_POINTS: /* TODO */ break;
                            case HIT_OBJECTS: /* TODO */ break;
                        }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getName(){ return version; }
}
