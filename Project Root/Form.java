package ans;

import javax.swing.*;

public class Form extends JFrame
{
    // constructor
    public Form(String title)
    { 
        super(title);// set the title of the title bar

        setSize(520, 680);// set the size of GUI

        setDefaultCloseOperation(EXIT_ON_CLOSE);// GUI ends process after closing

        setLayout(null);// set Layout to null to disable layout management in order to use absolute postitioning
                                // to place the components whereever we want

        setLocationRelativeTo(null);// load GUI in the center of the screen

        setResizable(false);// prevent GUI from changing size

        getContentPane().setBackground(CommonConstants.PRIMARY_COLOR); // change the background color of the gui

    }
}
