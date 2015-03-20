package solution.views;

import scotlandyard.Colour;
import solution.Constants;
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
    private Rectangle mSaveBtn;
    private Rectangle mMenuBtn;
    private boolean mSaveBtnOverlay = false;
    private boolean mMenuBtnOverlay = false;
    private PlayerInfoBarListener mListener;
    private PlayerInfoImg[] playerColumns = new PlayerInfoImg[Constants.MAX_PLAYERS];

    public PlayerInfoBar(GameControllerInterface controllerInterface) {
        setMinimumSize(new Dimension(800, 200));
        setPreferredSize(new Dimension(800, 200));
        setOpaque(false);
        mGameControllerInterface = controllerInterface;
        controllerInterface.addUpdateListener(new GameAdapter());

        URL resource = getClass().getClassLoader().getResource("ui" + File.separator + "options.png");
        try {
            menuOptionsImg = ImageIO.read(new File(resource.toURI()));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        updatePlayerInfoImgs();
        addMouseListener(new LocalMouseAdapter());
        addMouseMotionListener(new LocalMouseAdapter());


    }
    public void updatePlayerInfoImgs(){
        // get players info
        List<Colour> allPlayers = mGameControllerInterface.getPlayerList();
        for(int i = 0; i < allPlayers.size(); i++){
            Colour currentPlayer = ColourHelper.getColour(i);
            playerColumns[i] = new PlayerInfoImg(currentPlayer, mGameControllerInterface, i);
        }
    }
    public interface PlayerInfoBarListener {
        public void onMenuBtnPress();
        public void onSaveBtnPress();
    }
    public void setListener(PlayerInfoBarListener listener){
        mListener = listener;
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
            updatePlayerInfoImgs();
            repaint();
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


        g2d.scale(1.2f,1.2f);
        for(int i = 0; i < mGameControllerInterface.getPlayerList().size(); i++) {
            playerColumns[i].draw(g2d, i * 160 + 130);
        }
        g2d.scale(0.8f,0.8f);
    }
}
