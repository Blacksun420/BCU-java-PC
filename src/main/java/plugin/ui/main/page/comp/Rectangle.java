package plugin.ui.main.page.comp;

import common.system.fake.FakeGraphics;
import plugin.ui.common.config.StaticConfig;
import plugin.ui.main.UIPlugin;

import java.awt.*;

public class Rectangle {
    private static final UIPlugin P = UIPlugin.getInstance();

    public static double ratioX = P.getRatioX();
    public static double ratioY = P.getRatioY();

    // unit of padding
    public final static double UNIT_X = 0.001;
    public final static double UNIT_Y = 0.003;

    public int x;
    public int y;
    public int w;
    public int h;
    public Color c;

    private Rectangle(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public static Rectangle getInstance(int x, int y, int w, int h, boolean isBackdrop) {
        Rectangle r = new Rectangle(x, y, w, h);
        setColor(r, isBackdrop);
        return r;
    }

    public static Rectangle getInstance(int x, int y, int w, int h) {
        Rectangle r = new Rectangle(x, y, w, h);
        r.c = P.getColor(StaticConfig.COLOR_CD_LOCK);

        if (r.c == null) {
            r.c = new Color(0, 240, 0);
        }
        return r;
    }

    private static void setColor(Rectangle r, boolean isBackdrop) {
        Color c;
        if (isBackdrop) {
            c = P.getColor(StaticConfig.COLOR_CD_LINE_BACKDROP);
            if (c == null) {
                c = Color.BLACK;
            }
        } else {
            c = P.getColor(StaticConfig.COLOR_CD_LINE_LOADING);
            if (c == null) {
                c = new Color(2, 255, 254);
            }

        }
        r.c = c;
    }

    /**
     * [0,100] -> [0,0.5]
     *
     * @param x value between [0, 100]
     */
    public static void setRatioX(double x) {
        Rectangle.ratioX = x * UNIT_X;
    }

    public void setWidth(double ratio) {
        w = (int) (w * ratio);
    }

    /**
     * [0,100] -> [0, 0.3]
     *
     * @param y value between [0, 100]
     */
    public static void setRatioY(double y) {
        Rectangle.ratioY = y * UNIT_Y;
    }

    public void draw(FakeGraphics g, int alpha) {
        g.colRect(x, y, w, h, c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    public Rectangle getPadding() {
        int pad_x = (int) (w * ratioX);
        int pad_y = (int) (h * ratioY);
        return getInstance(x + pad_x, y + pad_y, w - (pad_x << 1), h - (pad_y << 1), false);
    }

    @Override
    public String toString() {
        return "Rectangle{" +
                "x=" + x +
                ", y=" + y +
                ", w=" + w +
                ", h=" + h +
                '}';
    }
}

