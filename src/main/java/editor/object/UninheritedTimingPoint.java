package editor.object;

public class UninheritedTimingPoint extends TimingPoint {
    public double bpm;

    public UninheritedTimingPoint(double t) {
        super(t);
        mspb = 500.0; // 60000.0 / 120
        bpm = 120.0;
    }

    // time,beatLength,meter,sampleSet,sampleIndex,volume,uninherited,effects
    public UninheritedTimingPoint(double t, double _mspb, int meter, int sampleSet, int sampleIndex, int volume, int kiai) {
        super(t, meter, sampleSet, sampleIndex, volume, kiai);
        mspb = _mspb;
        bpm = 60000 / mspb;
        column = 0;
    }
}
