package solution.helpers;

import com.jhlabs.image.GrayscaleFilter;
import com.jhlabs.image.HSBAdjustFilter;
import com.jhlabs.image.RGBAdjustFilter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by benallen on 19/03/15.
 * Functions used in here were written by a third party and not myself.
 * It uses the filters lib
 */
public class ColourTintHelper {


    public static BufferedImage setRGB(BufferedImage source, Color c) {
        RGBAdjustFilter rgb = new RGBAdjustFilter(c.getRed(), c.getGreen(), c.getBlue());
        BufferedImage destination = rgb.createCompatibleDestImage(source, null);

        BufferedImage result = rgb.filter(source, destination);

        return result;
    }
    public static BufferedImage setBlack(BufferedImage source) {
        GrayscaleFilter gray = new GrayscaleFilter();
        BufferedImage destination = gray.createCompatibleDestImage(source, null);

        BufferedImage result = gray.filter(source, destination);

        return result;
    }

}
