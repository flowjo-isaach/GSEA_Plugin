package main;

import javax.swing.*;

/***********************************************************************************************************************
 * Author: Isaac Harries
 * Date: 07/03/2017
 * Contact: isaach@flowjo.com
 * Description: Prevents a user from selecting items within a list. This is used in the main window for the Selected
 * Gene Sets preview box. When a user selects gene sets in the Gene Set Selector, the list in the main menu is auto
 * populated. In the main window, there is no reason for the user to select any gene sets which is the reason for
 * using this class. If they were selectable, it may mislead the user into thinking they can select them for a reason.
 **********************************************************************************************************************/
public class NoSelectionModel extends  DefaultListSelectionModel{

    @Override
    public void setAnchorSelectionIndex(final int anchorIndex) {}

    @Override
    public void setLeadAnchorNotificationEnabled(final boolean flag) {}

    @Override
    public void setLeadSelectionIndex(final int leadIndex) {}

    @Override
    public void setSelectionInterval(final int index0, final int index1) {}
}

