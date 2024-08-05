package page.support;

import javax.imageio.ImageIO;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.image.BufferedImage;
import java.io.File;

public abstract class DropParser extends DropTarget {

    private File f;

    @Override
    public synchronized void drop(DropTargetDropEvent evt) {
        try {
            evt.acceptDrop(DnDConstants.ACTION_COPY);
            f = ((java.util.List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor)).get(0);
            evt.dropComplete(process(f));
        } catch (Exception e) {
            e.printStackTrace();
            evt.dropComplete(false);
        }
    }
    public abstract boolean process(File f);

    public BufferedImage getImg() {
        try {
            return ImageIO.read(f);
        } catch (Exception ignored) {
            return null;
        }
    }
}