//MUST HAVE KEYBOARD AND MOUSE INTERACTIVITY. DON'T DO TIME-BASED ANIMATIONS
//Steven Lynch 3/15/2021
//CP2 Seward
//Binary counter

//AWT Advanced Windowing Toolkit
import java.awt.Dimension;
//          ^^^package
//				^^^class
//~ import java.awt.Font;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Gui { // Class name must be same as file name
    public static void main(String[] args) {
        JFrame frame = new JFrame("Binary Adder"); // Frame = Overall Window
        Image framePic = (new ImageIcon("Swords 64x64.png")).getImage();
        frame.setIconImage(framePic);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE/* JFrame.EXIT_ON_CLOSE */);

        frame.addWindowListener(new WindowAdapter() { // ????????????????????????
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            } // Exit with code0 (success)
        });

        // ~ JPanel panel = new JPanel(); // Tab/Subsection of frame
        // ~ JPanel panel = new CustomPanel();
        CustomPanel panel = new CustomPanel(); // Extended functionality - more than JPanel
        panel.addMouseMotionListener(panel); // Listens to itself.
        // ^^^Sends panel info from this object to ^^^
        panel.addMouseListener(panel); // Listens to itself.

        frame.setContentPane(panel);/* same as frame.add(panel); */ // Links frame with contents
        panel.setPreferredSize(new Dimension(600, 200)); // needs Dimension imported (width,height)
        // Preferred bc how do you fit 200x200 into a 100x100 without scroll bars
        frame.pack(); // Organizes elements how it wants
        frame.setResizable(true); // Allows user to resize the frame, or not
        frame.addKeyListener(panel);// add to panel (bc no control over frame)
        // ~ frame.addMouseMotionListener(panel);

        frame.setVisible(true);
    }
}