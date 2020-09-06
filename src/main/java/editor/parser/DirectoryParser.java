package editor.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DirectoryParser {
    final private ArrayList<File> files = new ArrayList<>();
    final private Map<String, String> charts = new HashMap<String, String>();
    public DirectoryParser(String PATH){
        File repo = new File(PATH);
        if(!repo.isDirectory()) throw new IllegalArgumentException("That's not a directory!");
        for (File f : repo.listFiles()) {
            System.out.println(f);
            files.add(f);
            if(f.getName().endsWith(".osu")){
                charts.put(f.getName(), f.getAbsolutePath());
            }
        }
    }
    public Map<String, String> getCharts(){
        return charts;
    }
}