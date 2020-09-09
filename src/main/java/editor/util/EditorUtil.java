package editor.util;

public class EditorUtil {
    public int[] x, columnBoundary;
    public int leftBorder, rightBorder, kc, leftTimingPoint, rightTimingPoint, leftLiveBorder, columnWidth, halfColumnWidth, hitObjectCenter;
    public int chartBottom, yo;
    public int w, h, sampledDuration;
    public EditorUtil(int cs, int w_, int h_){
        calibrate(cs, w_, h_);
    }
    public void calibrate(int cs, int w_, int h_) {
        if(cs < 1) throw new IllegalArgumentException("invalid key count");
        kc = cs; // keyCount = CircleSize
        w = w_;
        h = h_;
        yo = Math.max(h / 5, h - 150);
        chartBottom = h - 20;
        leftBorder = 50;
        rightBorder = w - 50;
        columnWidth = (rightBorder - leftBorder - 200) / (cs * 2 + 3);
        halfColumnWidth = columnWidth / 2;
        //                 ACTUAL SPACE        SV-Visual  RAW+LIVE + 3 TP columns
        leftTimingPoint = leftBorder + cs * columnWidth;
        rightTimingPoint = leftBorder + (cs + 3) * columnWidth;
        leftLiveBorder = rightBorder - cs * columnWidth;
        hitObjectCenter = (leftBorder + leftTimingPoint) / 2;

        x = new int[cs+3];
        for(int i = 0; i < cs+3; i ++) x[i] = leftBorder + halfColumnWidth + columnWidth*i;
        columnBoundary = new int[cs+4];
        for(int i = 0; i < cs+4; i ++) columnBoundary[i] = leftBorder + columnWidth*i;
    }
}
