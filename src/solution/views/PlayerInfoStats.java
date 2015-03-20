package solution.views;

import com.jhlabs.image.RGBAdjustFilter;
import scotlandyard.Colour;
import scotlandyard.Ticket;
import solution.Constants;
import solution.helpers.ColourHelper;
import solution.helpers.ColourTintHelper;
import solution.interfaces.GameControllerInterface;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

/**
 * Created by benallen on 16/03/15.
 */
public class PlayerInfoStats extends JPanel{
    private GameControllerInterface mControllerInterface;
    private BufferedImage bgImg;
    private BufferedImage colourImg;
    private AffineTransform transform;
    private Dimension mImgSize;
    private Colour mCurrentPlayer;
    private int mPlayerNumber;
    private Font mFont;
    private float xScale = 1.5f;
    private float yScale = 1.5f;
    public PlayerInfoStats(Colour currentPlayer, GameControllerInterface controllerInterface, int playerNumber){
        transform = new AffineTransform();
        mControllerInterface = controllerInterface;
        mPlayerNumber = playerNumber;
        mCurrentPlayer = currentPlayer;

        xScale = 1.5f;

        URL resource;
        if (currentPlayer == Constants.MR_X_COLOUR) {
            resource = getClass().getClassLoader().getResource("ui" + File.separator + "mrxholder.png");
        } else if(playerNumber % 2 == 0) {
            resource = getClass().getClassLoader().getResource("ui" + File.separator + "playerholderA.png");
        } else {
            resource = getClass().getClassLoader().getResource("ui" + File.separator + "playerholderB.png");
        }
        URL resource2 = getClass().getClassLoader().getResource("ui" + File.separator + "paper.png");
        try {
            bgImg = ImageIO.read(new File(resource.toURI()));
            colourImg = ImageIO.read(new File(resource2.toURI()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mImgSize = new Dimension((int)((bgImg.getWidth() / 2) * xScale),(int) ((bgImg.getHeight() / 2)* yScale));

        setPreferredSize(mImgSize);
        setMaximumSize(mImgSize);
        setMinimumSize(mImgSize);
        setSize(mImgSize);

        InputStream is = getClass().getClassLoader().getResourceAsStream("ui" + File.separator + "snellroundhand.ttf");
        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mFont = font;

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = getSize();
                transform.setToScale(size.getWidth() / mImgSize.getWidth(), size.getHeight() / mImgSize.getHeight());
                repaint();
            }

        });
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g2d.scale(xScale,yScale);
        //g2d.drawImage(ColourTintHelper.colourImage(bgImg, 125,125,125), 0, 0, (int) mImgSize.getWidth(), (int) mImgSize.getHeight(), this);
        g2d.drawImage(bgImg, 0, 0,  (bgImg.getWidth() / 2), (bgImg.getHeight() / 2), this);
        g2d.setFont(mFont.deriveFont(22f));
        g2d.setColor(Color.BLACK);
        String playerName;
        if(mCurrentPlayer == Constants.MR_X_COLOUR) {
            playerName = "";
        } else {
            playerName = "Player " + (mPlayerNumber);
        }
        FontMetrics fm = g.getFontMetrics();

        int tOffset;
        int lOffset;

        // Add in the player name
        int w = fm.stringWidth(playerName);
        int h = fm.getAscent();
        if(mCurrentPlayer == Constants.MR_X_COLOUR) {
            tOffset = 0;
            lOffset = 0;
        } else if(mPlayerNumber % 2 == 0) {
            tOffset = 28;
            lOffset = 42;
            g2d.rotate(0, lOffset + (w / 2), tOffset + (h / 2));
        } else {
            tOffset = 28;
            lOffset = 42;
            g2d.rotate(0, lOffset + (w / 2), tOffset + (h / 2));
        }

        g2d.drawString(playerName, lOffset, tOffset);

        Map<Ticket, Integer> playerTickets = mControllerInterface.getPlayerTickets(mCurrentPlayer);
        if(mCurrentPlayer == Constants.MR_X_COLOUR){
            g2d.setFont(mFont.deriveFont(22f));
        } else {
            g2d.setFont(mFont.deriveFont(28f));
        }


        // Add in the bus number
        String currentDrawText = String.valueOf(playerTickets.get(Ticket.Bus));
        w = fm.stringWidth(currentDrawText);
        h = fm.getAscent();
        if(mCurrentPlayer == Constants.MR_X_COLOUR) {
            lOffset = 55;
            tOffset = 52;
        } else if(mPlayerNumber % 2 == 0) {
            lOffset = 56;
            tOffset = 55;
            g2d.rotate(0, lOffset + (w / 2), tOffset + (h / 2));
        } else {
            lOffset = 56;
            tOffset = 55;
            g2d.rotate(0, lOffset + (w / 2), tOffset + (h / 2));
        }

        g2d.drawString(currentDrawText,lOffset, tOffset);

        // Add in the taxi number
        currentDrawText = String.valueOf(playerTickets.get(Ticket.Taxi));
        w = fm.stringWidth(currentDrawText);
        h = fm.getAscent();
        if(mCurrentPlayer == Constants.MR_X_COLOUR) {
            lOffset = 86;
            tOffset = 55;
        } else if(mPlayerNumber % 2 == 0) {
            lOffset = 80;
            tOffset = 53;
        } else {
            lOffset = 80;
            tOffset = 53;
        }

        g2d.drawString(currentDrawText,lOffset, tOffset);

        // Add in the underground number
        currentDrawText = String.valueOf(playerTickets.get(Ticket.Underground));
        w = fm.stringWidth(currentDrawText);
        h = fm.getAscent();
        if(mCurrentPlayer == Constants.MR_X_COLOUR) {
            lOffset = 58;
            tOffset = 75;
        } else if(mPlayerNumber % 2 == 0) {
            lOffset = 56;
            tOffset = 82;
        } else {
            lOffset = 56;
            tOffset = 82;
        }

        g2d.drawString(currentDrawText,lOffset, tOffset);

        // Mr X Extras
        if(mCurrentPlayer == Constants.MR_X_COLOUR) {
            currentDrawText = String.valueOf(playerTickets.get(Ticket.SecretMove));
            lOffset = 86;
            tOffset = 75;
            g2d.drawString(currentDrawText,lOffset, tOffset);
            currentDrawText = String.valueOf(playerTickets.get(Ticket.DoubleMove));
            lOffset = 70;
            tOffset = 85;
            g2d.drawString(currentDrawText,lOffset, tOffset);
        } else {
            Color c = ColourHelper.toColor(mCurrentPlayer);
            colourImg = ColourTintHelper.setRGB(colourImg, c);
            g2d.drawImage(
                    colourImg,
                    70,
                    50,
                    70,
                    70,
                    this);


        }

    }
}
