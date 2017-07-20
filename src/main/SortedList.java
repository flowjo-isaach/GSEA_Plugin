package main;

import javax.swing.*;
import java.util.*;

/**
 * Created by Isaac on 7/3/2017.
 */
class SortedListModel extends AbstractListModel {

    SortedSet model;

    public SortedListModel() {
        model = new TreeSet();
    }

    public int getSize() {
        return model.size();
    }

    public Object getElementAt(int index) {
        if(model.size() > index) return model.toArray()[index];

        System.out.println("index: ".concat(Integer.toString(index)));
        System.out.println("out of bounds");
        return -1;
    }

    public void add(Object element) {
        if (model.add(element)) {
            fireContentsChanged(this, 0, getSize());
        }
    }

    public void addAll(Object elements[]) {
        Collection c = Arrays.asList(elements);
        model.addAll(c);
        fireContentsChanged(this, 0, getSize());
    }

    public void clear() {
        model.clear();
        fireContentsChanged(this, 0, getSize());
    }

    public boolean contains(Object element) {
        return model.contains(element);
    }

    public Object firstElement() {
        return model.first();
    }

    public Iterator iterator() {
        return model.iterator();
    }

    public Object lastElement() {
        return model.last();
    }

    public boolean removeElement(Object element) {
        boolean removed = model.remove(element);
        if (removed) {
            fireContentsChanged(this, 0, getSize());
        }
        return removed;
    }
}