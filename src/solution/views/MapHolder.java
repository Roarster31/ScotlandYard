package solution.views;

import solution.Models.GraphData;
import solution.interfaces.GameControllerInterface;
import solution.views.map.MapView;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by benallen on 19/03/15.
 */
@Deprecated
public class MapHolder extends JPanel {
    private final MapView mapView;
    private BufferedImage mMapImage;
    MapHolder(GameControllerInterface controllerInterface){
        setOpaque(false);

        mapView = new MapView(controllerInterface, "custom_map.png", new GraphData("custom.txt", GraphData.DataFormat.CUSTOM));

        URL resource = getClass().getClassLoader().getResource("ui" + File.separator + "mapbg.png");
        try {
            mMapImage  = ImageIO.read(new File(resource.toURI()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g2d.drawImage(mMapImage, null, 0, 0);

    }
}
