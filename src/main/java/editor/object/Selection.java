package editor.object;

import java.util.TreeSet;

public class Selection extends TreeSet<Selectable<?>> {
    public Selectable<?> focus = null;
    public Selection(){
        super();
    }
    public void focus(Selectable<?> that){
        focus = that;
    }
    public boolean focusedOn(Selectable<?> that){
        return focus != null && focus == that;
    }
    public boolean select(Selectable<?> that){
        focus(that);
        return add(that);
    }
    public boolean deselect(Selectable<?> that){
        if(focus == that) focus = null;
        return remove(that);
    }
    public void deselectAll(){
        focus = null;
        clear();
    }
}
