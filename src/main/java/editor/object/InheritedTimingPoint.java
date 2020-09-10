package editor.object;

public class InheritedTimingPoint extends TimingPoint {
    public double sv;
    public UninheritedTimingPoint parent;
    public InheritedTimingPoint(double t){
        super(t);
        sv = 1.00;
    }

    // time,beatLength,meter,sampleSet,sampleIndex,volume,uninherited,effects
    public InheritedTimingPoint(double t, double _sv, int meter, int sampleSet, int sampleIndex, int volume, int kiai) {
        super(t, meter, sampleSet, sampleIndex, volume, kiai);
        sv = _sv;
        column = 1;
    }
}
