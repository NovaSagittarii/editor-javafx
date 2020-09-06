package editor.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class DirectoryParser {
    final private ArrayList<File> files = new ArrayList<>();
    final private ArrayList<String> charts = new ArrayList<>();
    public DirectoryParser(String PATH){
        File repo = new File(PATH);
        if(!repo.isDirectory()) throw new IllegalArgumentException("That's not a directory!");
        for (File f : repo.listFiles()) {
            System.out.println(f);
            files.add(f);
            if(f.getName().endsWith(".osu")){
                charts.add(f.getName());
            }
        }
    }
    public ArrayList<String> getCharts(){
        return charts;
    }
}