package editor.object;

import java.util.TreeSet;

public class TimingPointTimeline extends TreeSet<TimingPoint> {
    public TimingPoint current;
    public TimingPointTimeline(){
        super();
    }
    public void update(double time){
        if(current == null){
            if(this.isEmpty()) throw new UnsupportedOperationException("add something first pls");
            current = this.first();
        }
        TimingPoint next = this.higher(current);
        while(next != null && next.time <= time){
            current = next;
            next = this.higher(current);
        }
        TimingPoint before = this.lower(current);
        while(before != null && current.time > time){
            current = before;
            before = this.lower(current);
        }
    }
}
