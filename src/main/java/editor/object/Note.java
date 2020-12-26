package editor.object;

import java.util.Arrays;

public class Note implements Comparable<Note> {
    public int column, time, hitsounds, type = 1;
    public String extras;
    public Integer selected = null;

    public Note(int x, int t){
        column = x;
        time = t;
        hitsounds = 0;
        extras = null;
    }

    public Note(int x, int t, int hs, String sfx) {
        column = x;
        time = t;
        hitsounds = hs;
        extras = sfx; /* TODO: likely to use split/join (':') for extras part */
    }

    public static Note fromString(String line, int ColumnWidth) {
        String[] l = line.split(",");
        int[] s = Arrays.stream(Arrays.copyOfRange(l, 0, l.length - 1)).mapToInt(Integer::parseInt).toArray();
        if (s.length < 4) return null;
        // x, y, t, type, hs, _t, sfx
        if (s[3] > 100){
            return new LongNote(s[0] / ColumnWidth, s[2], s[4], Integer.parseInt(l[5].split(":")[0]), l[5].replaceFirst("\\d?:", ""));
        }
        else return new Note(s[0] / ColumnWidth, s[2], s[4], l[5]);
    }

    public String export() {
        //x,y,time,type,hitSound,objectParams,hitSample
        return column + ",192," + time + ",0," + hitsounds + "," + extras;
    }

    @Override
    public int compareTo(Note o) {
        return Integer.compare(this.time, o.time);
    }
}
