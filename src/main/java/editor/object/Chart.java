package editor.object;

import editor.util.FileUtil;

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
            List<String> contents = FileUtil.readLines(f);

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
                                if(line.startsWith("Title:")) title = line.replaceFirst("[^:]+?:", "");
                                if(line.startsWith("TitleUnicode:")) titleUnicode = line.replaceFirst("[^:]+?:", "");
                                if(line.startsWith("Artist:")) artist = line.replaceFirst("[^:]+?:", "");
                                if(line.startsWith("ArtistUnicode:")) artistUnicode = line.replaceFirst("[^:]+?:", "");
                                if(line.startsWith("Version:")) version = line.replaceFirst("[^:]+?:", "");
                                break;
                            case DIFFICULTY:
                                if(line.startsWith("H")) hp = Integer.parseInt(line.replaceFirst("[^:]+?:", ""));
                                if(line.startsWith("C")) cs = Integer.parseInt(line.replaceFirst("[^:]+?:", ""));
                                if(line.startsWith("O")) od = Integer.parseInt(line.replaceFirst("[^:]+?:", ""));
                                if(line.startsWith("A")) ar = Integer.parseInt(line.replaceFirst("[^:]+?:", ""));
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
