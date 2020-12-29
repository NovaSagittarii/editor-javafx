package editor.object;

import java.util.TreeSet;

public class TimingPointTimeline extends TreeSet<TimingPoint> {
    public TimingPoint current;
    public TimingPointTimeline(){
        super();
    }
    public void update(double time){
        if(current == null){
            if(isEmpty()) throw new UnsupportedOperationException("add something first pls");
            current = first();
        }
        TimingPoint next = higher(current);
        while(next != null && next.time <= time){
            current = next;
            next = higher(current);
        }
        TimingPoint before = lower(current);
        while(before != null && current.time > time){
            current = before;
            before = lower(current);
        }
    }
}
