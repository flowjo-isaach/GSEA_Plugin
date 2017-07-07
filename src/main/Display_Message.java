package main;

import javax.swing.*;

/**
 * Created by Isaac on 7/3/2017.
 */
public class Display_Message {

    private String[] options = {"Continue", "Cancel"};
    private int response = -1;

    Display_Message(String type, String message) {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();

        type = type.toLowerCase();

        if(type.equals("error"))
            JOptionPane.showMessageDialog(panel, message, "Error", JOptionPane.ERROR_MESSAGE);
        else if(type.equals("disclaimer"))
            JOptionPane.showMessageDialog(panel, message, "Disclaimer", JOptionPane.INFORMATION_MESSAGE);
        else
            response = JOptionPane.showOptionDialog(frame, message, "Prompt", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
    }

    int getResponse() {return response;}
}