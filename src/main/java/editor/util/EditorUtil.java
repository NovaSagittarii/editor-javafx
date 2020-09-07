package editor.util;

public class EditorUtil {
    public int leftBorder, rightBorder, yo, kc, leftTimingPoint, columnWidth;
    private int w, h;
    public EditorUtil(int cs, int w_, int h_){
        kc = cs; // keyCount = CircleSize
        w = w_;
        h = h_;
        yo = Math.min(h/5, h - 250);
        leftBorder = 50;
        rightBorder = w - 50;
        columnWidth = (rightBorder - leftBorder - 200) / (cs*2 + 3);
        //                 ACTUAL SPACE        SV-Visual  RAW+LIVE + 3 TP columns
    }
    public void recalibrate(int cs, int w_, int h_) {
        kc = cs; // keyCount = CircleSize
        w = w_;
        h = h_;
        yo = Math.min(h / 5, h - 250);
        leftBorder = 50;
        rightBorder = w - 50;
        columnWidth = (rightBorder - leftBorder - 200) / (cs * 2 + 3);
    }
}
