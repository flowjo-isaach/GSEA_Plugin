package main;

import javax.swing.*;

/***********************************************************************************************************************
 * Author: Isaac Harries
 * Date: 07/03/2017
 * Contact: isaach@flowjo.com
 * Description: Displays an error, disclaimer or prompt window based on the given parameters.
 **********************************************************************************************************************/
class DisplayMessage {
    private int response = -1;

    /**
     * Method: Constructor
     * Description: Displays a message
     * @param type type of message (either 'error', 'disclaimer', or 'prompt'
     * @param message message to display
     */
    DisplayMessage(String type, String message) {
        JFrame frame = new JFrame();
        String[] options = {"Continue", "Cancel"};

        type = type.toLowerCase();

        switch (type) {
            case "error":
                JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
                break;
            case "disclaimer":
                JOptionPane.showMessageDialog(frame, message, null, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "Prompt":
                response = JOptionPane.showOptionDialog(frame, message, null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                break;
            default:
                break;
        }
    }

    /**
     * Method: getResponse
     * Description: Returns the type of response if user was given a Prompt
     * @return response Integer value e.g. JOptionPane.OK_OPTION, JOptionPane.NO_OPTION
     */
    int getResponse() {return response;}
}