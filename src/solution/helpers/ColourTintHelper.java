package solution.helpers;

import com.jhlabs.image.HSBAdjustFilter;
import com.jhlabs.image.RGBAdjustFilter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by benallen on 19/03/15.
 */
public class ColourTintHelper {
    public static BufferedImage setRGB(BufferedImage source, Color c) {
        RGBAdjustFilter rgb = new RGBAdjustFilter(c.getRed(), c.getGreen(), c.getBlue());
        BufferedImage destination = rgb.createCompatibleDestImage(source, null);

        BufferedImage result = rgb.filter(source, destination);

        return result;
    }

}
