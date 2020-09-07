package editor.object;

public class LongNote extends Note {
    public int endTime, type = 128;

    public LongNote(int x, int t, int hs, int _t, String sfx){
        super(x, t, hs, sfx);
        endTime = _t;
    }
}
