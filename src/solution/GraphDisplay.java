package solution;

import scotlandyard.Edge;
import scotlandyard.Graph;
import scotlandyard.GraphReader;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.image.*;
import java.io.*;
import java.text.*;
import java.util.*;

import javax.imageio.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class GraphDisplay {

    public static void main(String[] args) {

        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(1018, 809);
        
        JLabel lbl = new JLabel();
        lbl.setIcon(loadInImage());
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }
    private static ImageIcon loadInImage(){
        BufferedImage img = null;
        try
        {
            img = ImageIO.read(new File("resources/map.jpg"));
        }
        catch( IOException e )
        {
            System.out.printf("Cannot Find Picture Of Map");
            System.out.println(e);
            return null;
        }
        return new ImageIcon(img);
    }
}