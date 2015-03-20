package solution.helpers;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by rory on 20/03/15.
 */
public class ImageHelper {

    //https://community.oracle.com/thread/1263994
    public static BufferedImage optimizeImage(BufferedImage img)
    {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        boolean istransparent = img.getColorModel().hasAlpha();

        BufferedImage img2 = gc.createCompatibleImage(img.getWidth(), img.getHeight(), istransparent ? Transparency.BITMASK : Transparency.OPAQUE);
        Graphics2D g = img2.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return img2;
    }

}
