package solution.views;

import scotlandyard.Colour;
import solution.Models.ScotlandYardModel;
import solution.helpers.ColourHelper;
import solution.helpers.SoundHelper;
import solution.interfaces.GameControllerInterface;
import solution.interfaces.adapters.GameUIAdapter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * Created by benallen on 10/03/15.
 */
public class PlayerInfoBar extends JPanel {
    private GameControllerInterface mGameControllerInterface;
    private BufferedImage menuOptionsImg;
    private BufferedImage mPlayerHolderAImg;
    private BufferedImage mPlayerHolderBImg;
    private BufferedImage mPlayerColourImg;
    private Rectangle mSaveBtn;
    private Rectangle mMenuBtn;
    private boolean mSaveBtnOverlay = false;
    private boolean mMenuBtnOverlay = false;
    private PlayerInfoBarListener mListener;

    public PlayerInfoBar(GameControllerInterface controllerInterface) {
        mGameControllerInterface = controllerInterface;
        controllerInterface.addUpdateListener(new GameAdapter());

        URL resource = getClass().getClassLoader().getResource("ui" + File.separator + "options.png");
        try {
            menuOptionsImg = ImageIO.read(new File(resource.toURI()));
            resource = getClass().getClassLoader().getResource("ui" + File.separator + "playerholderA.png");
            mPlayerHolderAImg = ImageIO.read(new File(resource.toURI()));
            resource = getClass().getClassLoader().getResource("ui" + File.separator + "playerholderB.png");
            mPlayerHolderBImg = ImageIO.read(new File(resource.toURI()));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        createBar();
        addMouseListener(new LocalMouseAdapter());
        addMouseMotionListener(new LocalMouseAdapter());


    }
    public interface PlayerInfoBarListener {
        public void onMenuBtnPress();
        public void onSaveBtnPress();
    }
    public void setListener(PlayerInfoBarListener listener){
        mListener = listener;
    }
    private void createBar() {
        List<Colour> allPlayers = mGameControllerInterface.getPlayerList();
        setMinimumSize(new Dimension(800, 200));
        setPreferredSize(new Dimension(800, 200));
        Box horzView = Box.createHorizontalBox();

        PlayerInfoStats[] playerColumns = new PlayerInfoStats[allPlayers.size()];

        for(int i = 0; i < allPlayers.size(); i++){
            Colour currentPlayer = ColourHelper.getColour(i);
            playerColumns[i] = new PlayerInfoStats(currentPlayer, mGameControllerInterface, i);
            playerColumns[i].setOpaque(false);
            horzView.add(playerColumns[i]);
        }
        add(horzView);
        setOpaque(false);
    }
    class LocalMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            if (mMenuBtn.contains(e.getX(), e.getY()) ) {
                SoundHelper.itemClick();
                mListener.onMenuBtnPress();
                repaint();
            }
            if (mSaveBtn.contains(e.getX(), e.getY()) ) {
                SoundHelper.itemClick();
                mListener.onSaveBtnPress();
                repaint();
            }

        }

        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);
            if (mSaveBtn.contains(e.getX(), e.getY()) ) {
                mSaveBtnOverlay = true;
                mMenuBtnOverlay = false;
                SoundHelper.itemHover();
                repaint();
            }else if (mMenuBtn.contains(e.getX(), e.getY()) ) {
                mMenuBtnOverlay = true;
                mSaveBtnOverlay = false;
                SoundHelper.itemHover();
                repaint();
            } else {
                mMenuBtnOverlay = false;
                mSaveBtnOverlay = false;
                SoundHelper.itemDeHover();
                repaint();
            }
        }
    }

    class GameAdapter extends GameUIAdapter {
        @Override
        public void onGameModelUpdated(ScotlandYardModel model) {
            removeAll();
            createBar();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g2d.drawImage(
                menuOptionsImg,
                0,
                0,
                menuOptionsImg.getWidth(),
                menuOptionsImg.getHeight(),
                this);
        mMenuBtn = new Rectangle(
                0,
                0,
                menuOptionsImg.getWidth(),
                menuOptionsImg.getHeight() / 2);
        mSaveBtn = new Rectangle(
                0,
                menuOptionsImg.getHeight() / 2,
                menuOptionsImg.getWidth(),
                menuOptionsImg.getHeight() / 2
        );

        // Button Hover
        Color color = new Color(0, 0, 0, 0.1f);
        g2d.setPaint(color);
        if(mSaveBtnOverlay){
            g2d.fill(mSaveBtn);
        }
        if(mMenuBtnOverlay){
            g2d.fill(mMenuBtn);
        }
    }
}
