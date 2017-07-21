package main;

import javax.swing.*;
import java.util.*;

/**
 * Created by Isaac on 7/3/2017.
 */
class SortedListModel extends AbstractListModel {

    private SortedSet model;

    SortedListModel() { model = new TreeSet(); }

    public int getSize() { return model.size(); }

    public Object getElementAt(int index) {
        if(model.size() > index) return model.toArray()[index];

        return -1;
    }

    void add(Object element) {
        if (model.add(element)) {
            fireContentsChanged(this, 0, getSize());
        }
    }

    void addAll(Object elements[]) {
        Collection c = Arrays.asList(elements);
        model.addAll(c);
        fireContentsChanged(this, 0, getSize());
    }

    void clear() {
        model.clear();
        fireContentsChanged(this, 0, getSize());
    }

    boolean contains(Object element) { return model.contains(element); }

    Object firstElement() { return model.first(); }

    Iterator iterator() { return model.iterator(); }

    Object lastElement() { return model.last(); }

    boolean removeElement(Object element) {
        boolean removed = model.remove(element);
        if (removed)
            fireContentsChanged(this, 0, getSize());

        return removed;
    }
}