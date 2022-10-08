package plugin.ui.main.handler;

import plugin.ui.main.UIPlugin;
import plugin.ui.main.util.ImageReader;
import plugin.ui.main.util.api.GifComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


public abstract class UIPainter {
    private static final UIPlugin P = UIPlugin.P;
    public static boolean resize;
    private static String key;

    protected static void paintBG(Graphics g, JComponent page) {
        BufferedImage target = ImageReader.getImage(key);

        // paint target
        if (target != null) {
            if (resize) {
                g.drawImage(target, P.getPointX(), P.getPointY(), page.getWidth(), page.getHeight(), page);
            } else {
                g.drawImage(target, P.getPointX(), P.getPointY(), target.getWidth(null), target.getHeight(null), page);
            }
        }
    }

    protected static void paintGIF(Graphics g, JComponent page, GifComponent gif) {
        if (gif != null) {
            gif.paintComponent(g, page);
        }
    }

    protected static void setKey(String key) {
        UIPainter.key = key;
    }

    public static void init() {
        key = null;
        resize = P.getOptional("bgResize");
    }
}