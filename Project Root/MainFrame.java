package ans;

import javax.swing.*;

public class MainFrame extends JFrame
{
    // constructor
    public MainFrame(String title)
    { 
        super(title);// set the title of the title bar

        setSize(1100, 680);// set the size of GUI

        setDefaultCloseOperation(EXIT_ON_CLOSE);// GUI ends process after closing

        setLocationRelativeTo(null);// load GUI in the center of the screen

        setResizable(true);// GUI changes size

        getContentPane().setBackground(CommonConstants.PRIMARY_COLOR); // change the background color of the gui

    }
}
