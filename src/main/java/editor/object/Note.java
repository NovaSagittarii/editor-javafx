package editor.object;

import java.util.Arrays;

public class Note {
    public int column, time, hitsounds, extras, type = 1;
    public Note(int x, int t, int hs, int sfx){
        column = x;
        time = t;
        hitsounds = hs;
        extras = sfx;
    }

    public Note fromString(String line){
        int[] s = Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray();
        // x, y, t, type, hs, _t, sfx
        if(s[3] > 100) return new LongNote(s[0], s[2], s[4], s[5], s[6]);
        else           return new Note(s[0], s[2], s[4], s[6]);
    }
}
