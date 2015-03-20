package solution.views;

import solution.helpers.ColourTintHelper;
import solution.helpers.SoundHelper;

import javax.imageio.ImageIO;
import javax.swing.*;
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

/**
 * Created by benallen on 17/03/15.
 */
public class IntroView extends JPanel {
    private BufferedImage mLogoImg;
    private BufferedImage mPlayImg;
    private BufferedImage mLoadImg;
    private Rectangle mPlayBtn;
    private Rectangle mLoadBtn;
    private boolean mShowPlayOverlay = false;
    private boolean mShowLoadOverlay = false;
    private IntroViewListener mListener;

    public IntroView(){
        setOpaque(false);
        URL resource1 = getClass().getClassLoader().getResource("ui" + File.separator + "logo.png");
        URL resource2 = getClass().getClassLoader().getResource("ui" + File.separator + "playbtn.png");
        URL resource3 = getClass().getClassLoader().getResource("ui" + File.separator + "loadbtn.png");
        try {
            mLogoImg = ImageIO.read(new File(resource1.toURI()));
            mPlayImg = ImageIO.read(new File(resource2.toURI()));
            mLoadImg = ImageIO.read(new File(resource3.toURI()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        int w = getWidth();
        int h = getHeight();
        mPlayBtn = new Rectangle(0,0,0,0);
        mLoadBtn = new Rectangle(0,0,0,0);

        addMouseMotionListener(new LocalMouseAdapter());
        addMouseListener(new LocalMouseAdapter());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        int w = getWidth();
        int h = getHeight();
        g2d.drawImage(mLogoImg, null, (w / 2) - (mLogoImg.getWidth() / 2), (h / 4) - (mLogoImg.getHeight() / 2));
        g2d.drawImage(mPlayImg, null, (w / 2) - (mPlayImg.getWidth() / 2), ((2*h) / 4) - (mPlayImg.getHeight() / 2));
        g2d.drawImage(mLoadImg, null, (w / 2) - (mLoadImg.getWidth() / 2), ((3*h) / 4) - (mLoadImg.getHeight() / 2));

        mPlayBtn = new Rectangle(
                (w / 2) - (mPlayImg.getWidth() / 2),
                ((2*h) / 4) - (mPlayImg.getHeight() / 2),
                mPlayImg.getWidth(),
                mPlayImg.getHeight()
        );
        mLoadBtn = new Rectangle(
                (w / 2) - (mLoadImg.getWidth() / 2),
                ((3*h) / 4) - (mLoadImg.getHeight() / 2),
                mLoadImg.getWidth(),
                mLoadImg.getHeight()
        );

        // Button Hover
        Color color = new Color(0, 0, 0, 0.1f);
        g2d.setPaint(color);
        if(mShowPlayOverlay){
            g2d.fill(mPlayBtn);
        }
        if(mShowLoadOverlay){
            g2d.fill(mLoadBtn);
        }
    }
    public interface IntroViewListener {
        public void onPlayBtnPress();
        public void onLoadBtnPress();
    }
    public void setListener(IntroViewListener listener){
        mListener = listener;
    }

    class LocalMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            if (mPlayBtn.contains(e.getX(), e.getY()) ) {
                SoundHelper.itemClick();
                mListener.onPlayBtnPress();
                repaint();
            }
            if (mLoadBtn.contains(e.getX(), e.getY()) ) {
                SoundHelper.itemClick();
                mListener.onLoadBtnPress();
                repaint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);
            if (mPlayBtn.contains(e.getX(), e.getY()) ) {
                mShowPlayOverlay = true;
                mShowLoadOverlay = false;
                SoundHelper.itemHover();
                repaint();
            }else if (mLoadBtn.contains(e.getX(), e.getY()) ) {
                mShowLoadOverlay = true;
                mShowPlayOverlay = false;
                SoundHelper.itemHover();
                repaint();
            } else {
                mShowLoadOverlay = false;
                mShowPlayOverlay = false;
                SoundHelper.itemDeHover();
                repaint();
            }
        }
    }
}
