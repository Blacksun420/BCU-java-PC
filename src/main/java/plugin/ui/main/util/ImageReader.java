package plugin.ui.main.util;

import org.jetbrains.annotations.NotNull;
import plugin.ui.common.config.StaticConfig;
import plugin.ui.main.UIPlugin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class ImageReader {
    private static final Map<String, BufferedImage> IMAGE_CACHE_MAP = new HashMap<>();
    private static final String PREFIX = StaticConfig.SRC_IMAGE_PREFIX;
    private static int defaultAlpha = UIPlugin.getInstance().getOpaque();

    public static BufferedImage getImage(String key) {
        return key == null ? null : IMAGE_CACHE_MAP.get(key);
    }

    public static BufferedImage getImage(File file, String key, int alpha) {
        BufferedImage image = IMAGE_CACHE_MAP.get(key);
        if (image == null) {
            image = readImage(file, key, alpha);
        }
        return image;
    }

    public static void readImage(File file, String key) {
        readImage(file, key, defaultAlpha);
    }

    public static BufferedImage readImage(@NotNull File file, String key, int alpha) {
        BufferedImage srcImage = null;

        try {
            srcImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (srcImage != null) {
            BufferedImage transparencyImage = alpha == 100 ? srcImage : transparencyImage(srcImage, alpha);
            put(key, srcImage, transparencyImage);
        }

        return srcImage;
    }

    public static BufferedImage transparencyImage(@NotNull BufferedImage srcImage, int alpha) {

        BufferedImage targetImage = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = targetImage.createGraphics();

        for (int y = srcImage.getMinY(); y < srcImage.getHeight(); y++) {
            for (int x = srcImage.getMinX(); x < srcImage.getWidth(); x++) {
                int rgb = srcImage.getRGB(x, y);
                rgb = ((alpha * 255 / StaticConfig.OPAQUE_MAX) << 24) | (rgb & 0x00ffffff);
                targetImage.setRGB(x, y, rgb);
            }
        }

        g.drawImage(targetImage, 0, 0, targetImage.getWidth(), targetImage.getHeight(), null);
        g.dispose();
        /*OPAQUE_CACHE_MAP.put(alpha, targetImage);*/
        return targetImage;
    }

    public static void reTransparency(String key, int alpha) {
        BufferedImage src = ImageReader.IMAGE_CACHE_MAP.get(PREFIX + key);
        if (src != null) {
            BufferedImage transparencyImage = transparencyImage(src, alpha);
            put(key, src, transparencyImage);
        }
    }

    public static void reTransparency(String key) {
        reTransparency(key, defaultAlpha);
    }

    public static void put(String key, @NotNull BufferedImage image, BufferedImage transparencyImage) {
        IMAGE_CACHE_MAP.put(key, transparencyImage);
        IMAGE_CACHE_MAP.put(PREFIX + key, image);
    }

    public static void setAlpha(int alpha) {
        ImageReader.defaultAlpha = alpha;
    }

    public static void clearImage() {
        IMAGE_CACHE_MAP.clear();
        System.gc();
    }

    public static int cacheSize() {
        return IMAGE_CACHE_MAP.size();
    }
}
