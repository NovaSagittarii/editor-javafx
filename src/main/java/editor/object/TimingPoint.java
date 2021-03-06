package editor.object;

import java.util.Arrays;
import java.util.Comparator;

public class TimingPoint implements Selectable<TimingPoint> {
    public double time, mspb;
    public int meter, sampleSet, sampleIndex, volume, kiai, column = 0;

    public TimingPoint(double t) {
        time = t;
    }

    public TimingPoint(double t, int m, int ss, int si, int v, int k) {
        time = t;
        meter = m;
        sampleSet = ss;
        sampleIndex = si;
        volume = v;
        kiai = k;
    }

    public static TimingPoint fromString(String line) {
        String[] l = line.split(",");
        if (l.length != 8) return null;
        int[] i = Arrays.stream(Arrays.copyOfRange(l, 2, l.length)).mapToInt(Integer::parseInt).toArray();
        double[] d = Arrays.stream(l).mapToDouble(Double::parseDouble).toArray();
        // time,beatLength,meter,sampleSet,sampleIndex,volume,uninherited,effects

        if (i[4] == 1) return new UninheritedTimingPoint(d[0], d[1], i[0], i[1], i[2], i[3], i[5]);
        else return new InheritedTimingPoint(d[0], d[1], i[0], i[1], i[2], i[3], i[5]);
    }

    static Comparator<TimingPoint> compareByTime() {
        return Comparator.comparingDouble(o -> o.time);
    }

    public int compareTo(TimingPoint o){
        return (time == o.time) ? (this instanceof UninheritedTimingPoint ? (o instanceof UninheritedTimingPoint ? 0 : 1) : (o instanceof UninheritedTimingPoint ? -1 : 0)) : (time > o.time ? 1 : -1);
    }
}
