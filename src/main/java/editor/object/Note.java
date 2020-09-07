package editor.object;

import java.util.Arrays;
import java.util.Comparator;

public class Note {
    public int column, time, hitsounds, type = 1;
    public String extras;

    public Note(int x, int t, int hs, String sfx) {
        column = x;
        time = t;
        hitsounds = hs;
        extras = sfx;
    }

    public static Note fromString(String line) {
        String[] l = line.split(",");
        int[] s = Arrays.stream(Arrays.copyOfRange(l, 0, l.length - 1)).mapToInt(Integer::parseInt).toArray();
        if (s.length < 7) return null;
        // x, y, t, type, hs, _t, sfx
        if (s[3] > 100) return new LongNote(s[0], s[2], s[4], s[5], l[6]);
        else return new Note(s[0], s[2], s[4], l[5]);
    }

    static Comparator<Note> compareByTime() {
        return Comparator.comparingInt(o -> o.time);
    }

    public String export() {
        //x,y,time,type,hitSound,objectParams,hitSample
        return column + ",192," + time + ",0," + hitsounds + "," + extras;
    }
}
