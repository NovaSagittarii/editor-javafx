package editor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class DirectoryParser {
    final private ArrayList<File> files = new ArrayList<>();
    final private HashMap<String, Chart> charts = new HashMap<>();
    public DirectoryParser(String PATH){
        File repo = new File(PATH);
        if(!repo.isDirectory()) throw new IllegalArgumentException("That's not a directory!");
        for (File f : repo.listFiles()) {
            System.out.println(f);
            files.add(f);
            if(f.getName().endsWith(".osu")){
                Chart chart = new Chart(f);
                charts.put(chart.getName(), chart);
            }
        }
    }
    public HashMap<String, Chart> getCharts(){
        return charts;
    }
}
