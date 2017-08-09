package main;

import javax.swing.*;
import java.util.*;

/***********************************************************************************************************************
 * Author: Isaac Harries
 * Date: 07/03/2017
 * Contact: isaach@flowjo.com
 * Description: Inherits from the AbstractListModel class. This class sorts all items in a ListModel in alphabetical
 * order.
 **********************************************************************************************************************/
class SortedListModel extends AbstractListModel {
    private SortedSet model;

    /**
     * Method: Constructor
     * Description: Initialize local variables
     */
    SortedListModel() { model = new TreeSet(); }

    /**
     * Method: getSize
     * Description: returns the number of items in this model
     * @return number of items in this model
     */
    @Override
    public int getSize() { return model.size(); }

    /**
     * Method: getElementAt
     * Description: returns the element based on the provided index
     * @param index index of element
     * @return element or -1
     */
    @Override
    public Object getElementAt(int index) {
        if(model.size() > index) return model.toArray()[index];

        return -1;
    }

    /**
     * Method: add
     * Description: adds an element to the model
     * @param element item to add
     */
    void add(Object element) {
        if (model.add(element)) {
            fireContentsChanged(this, 0, getSize());
        }
    }

    /**
     * Method: addAll
     * Description: add all elements from array
     * @param elements array of elements to add
     */
    void addAll(Object elements[]) {
        Collection c = Arrays.asList(elements);
        model.addAll(c);
        fireContentsChanged(this, 0, getSize());
    }

    /**
     * Method: clear
     * Description: clears the modal of all items
     */
    void clear() {
        model.clear();
        fireContentsChanged(this, 0, getSize());
    }

    /**
     * Method: contains
     * Description: returns true if element exists in model
     * @param element item to check
     * @return true if model contains element
     */
    boolean contains(Object element) { return model.contains(element); }

    /**
     * Method: firstElement
     * Description: returns the first element of the model
     * @return first element
     */
    Object firstElement() { return model.first(); }

    /**
     * Method: iterator
     * Description: returns an iterator of the model
     * @return iterator of model
     */
    Iterator iterator() { return model.iterator(); }

    /**
     * Method: lastElement
     * Description: returns last element of model
     * @return last element of model
     */
    Object lastElement() { return model.last(); }

    /**
     * Method: removeElement
     * Description: removes specified element
     * @param element item to remove
     * @return true if element is removed successfully
     */
    boolean removeElement(Object element) {
        boolean removed = model.remove(element);
        if (removed)
            fireContentsChanged(this, 0, getSize());

        return removed;
    }
}