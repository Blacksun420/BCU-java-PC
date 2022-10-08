package plugin.ui.main.util.api;

import com.sun.imageio.plugins.gif.GIFImageMetadata;
import plugin.ui.common.util.UIException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.*;

public class GifComponent extends JComponent {

    public static GifComponent gif;
    private static final long serialVersionUID = 1L;
    private GifBean[] gifBeans;
    private final Map<Integer, Integer[]> gifBeanMap = new HashMap<>();
    private int index = 0;
    private int delayFactor;
    private Timer timer;
    public File file;
    public boolean resize = false;


    /**
     * @param delayFactor 显示gif每帧图片的时间因子
     */
    public GifComponent(File gifFile, int delayFactor) {
        gif = this;
        setDelayFactor(delayFactor);
        setGifFile(gifFile);
    }

    public void close() {
        timer.cancel();
    }

    /**
     * 设置Gif文件
     */
    public void setGifFile(File gifFile) {
        if (gifFile == null || !gifFile.exists()) {
            throw new UIException("file isn't exist:" + gifFile);
        }

        file = gifFile;
        ImageReader reader = null;
        try {
            ImageInputStream imageIn = ImageIO.createImageInputStream(gifFile);
            Iterator<ImageReader> iter = ImageIO
                    .getImageReadersByFormatName("gif");
            if (iter.hasNext()) {
                reader = iter.next();
            }
            reader.setInput(imageIn, false);
            gifBeanMap.clear();
            gifBeans = new GifBean[reader.getNumImages(true)];
            GIFImageMetadata meta;
            for (int i = 0; i < gifBeans.length; i++) {
                meta = (GIFImageMetadata) reader.getImageMetadata(i);
                gifBeans[i] = new GifBean();
                gifBeans[i].image = reader.read(i);
                gifBeans[i].x = meta.imageLeftPosition;
                gifBeans[i].y = meta.imageTopPosition;
                gifBeans[i].width = meta.imageWidth;
                gifBeans[i].height = meta.imageHeight;
                gifBeans[i].disposalMethod = meta.disposalMethod;
                gifBeans[i].delayTime = meta.delayTime == 0 ? 1
                        : meta.delayTime;
            }
            for (int i = 1; i < gifBeans.length; i++) {
                if (gifBeans[i].disposalMethod == 2) {
                    gifBeanMap.put(i, new Integer[]{i});
                    continue;
                }
                int firstIndex = getFirstIndex(i);
                List<Integer> list = new ArrayList<>();
                for (int j = firstIndex; j <= i; j++) {
                    list.add(j);
                }
                gifBeanMap.put(i, list.toArray(new Integer[]{}));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        setTimer();
    }

    public void doSchedule() {
        repaint();
        try {
            Thread.sleep((long) gifBeans[index].delayTime * delayFactor);
        } catch (InterruptedException ignored) {
        }
        index++;
        if (index >= gifBeans.length) {
            index = 0;
        }
    }

    @Override
    public String toString() {
        return String.format("{%s, %d}", file.getName(), delayFactor);
    }

    public synchronized void setTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer("show gif");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                doSchedule();
            }

        }, 0, 1);

    }

    /**
     * 设置时间因子
     */
    public void setDelayFactor(int delayFactor) {
        this.delayFactor = delayFactor;
    }


    public int x;
    public int y;
    public int w;
    public int h;

    public void setBound(Rectangle rectangle) {
        x = rectangle.x;
        y = rectangle.y;
        w = rectangle.width;
        h = rectangle.height;
    }

    public void paintComponent(Graphics g, JComponent p) {
        super.paintComponent(g);
        setBound(p.getBounds());
        if (resize) {
            paintAsResize(g);
        } else {
            paintAsImageSize(g);
        }
    }

    private void paintAsResize(Graphics g) {
        g.drawImage(gifBeans[0].image, x, y, w, h, this);
        if (index > 0) {
            Integer[] array = gifBeanMap.get(index);
            for (Integer i : array) {
                g.drawImage(gifBeans[i].image, x, y, w, h, this);
            }
        }
    }

    private void paintAsImageSize(Graphics g) {
        g.drawImage(gifBeans[0].image, gifBeans[0].x, gifBeans[0].y, this);
        if (index > 0) {
            Integer[] array = gifBeanMap.get(index);
            for (Integer i : array) {
                g.drawImage(gifBeans[i].image, gifBeans[i].x, gifBeans[i].y, this);
            }
        }
    }

    private int getFirstIndex(int index) {
        int tempIndex = index;
        while (tempIndex > 1) {
            if (gifBeans[tempIndex - 1].disposalMethod == 2) {
                return index;
            }
            tempIndex--;
        }
        return tempIndex;
    }


    /**
     * 用于保持gif每帧图片的信息
     */
    public class GifBean {
        public BufferedImage image;
        public int x;
        public int y;
        public int width;
        public int height;
        public int disposalMethod;
        public int delayTime;
    }

}