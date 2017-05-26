/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JWindow;

/**
 *
 * @author Itirium
 */
public class ChatDBUtils {
 
    public static void center(JFrame frame) {
    // get the size of the screen, on systems with multiple displays,
    // the primary display is used
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    // calculate the new location of the window
        int w = frame.getSize().width;
        int h = frame.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;
    // moves this component to a new location, the top-left corner of
    // the new location is specified by the x and y
    // parameters in the coordinate space of this component's parent
        frame.setLocation(x, y);
    }

    public static void center(JWindow window) {
    // get the size of the screen, on systems with multiple displays,
    // the primary display is used
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    // calculate the new location of the window
        int w = window.getSize().width;
        int h = window.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;
    // moves this component to a new location, the top-left corner of
    // the new location is specified by the x and y
    // parameters in the coordinate space of this component's parent
        window.setLocation(x, y);
    }
}
