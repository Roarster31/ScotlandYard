package solution;

import scotlandyard.Edge;
import scotlandyard.Graph;
import scotlandyard.GraphReader;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.text.*;
import java.util.*;

import javax.imageio.*;
import javax.swing.*;

public class GraphDisplay {

    public static void main(String[] args) {

//        JFrame frame = new JFrame();
//        frame.setLayout(new FlowLayout());
//        frame.setSize(1018, 809);
//        frame.setLayout(new GridLayout(3, 1));
//        JLabel lbl = new JLabel();
//        lbl.setIcon(loadInImage());
//        //frame.add(lbl);
//
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        JPanel panel = new JPanel();
//        panel.add(InterfaceInterrupts.endGame());
//
//        frame.add(panel);
//        frame.setVisible(true);
        GameLogic game = new GameLogic();
        game.main(args);
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