package page.support;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Importer extends JFileChooser {

	private static final long serialVersionUID = 1L;

	public static final int IMP_MUS = 0, IMP_IMG = 1;
	public static final File[] curs = new File[2];

	public File file;

	public Importer(String str, int imp) {
		setDialogTitle(str);
		setCurrentDirectory(curs[imp]);

		if (imp == IMP_MUS) {
			FileNameExtensionFilter filter = new FileNameExtensionFilter("OGG Music", "ogg");
			setFileFilter(filter);
		} else {
			FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
			setFileFilter(filter);
			String[] fils = new String[]{"jpg", "jfif", "jpeg"};
			for (String fil : fils) {
				FileNameExtensionFilter fnef = new FileNameExtensionFilter(fil.toUpperCase() + " Images", fil);
				addChoosableFileFilter(fnef);//Five Nights Et Freddy's
			}
		}
		setDragEnabled(true);
		int returnVal = showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = getSelectedFile();
			curs[imp] = getCurrentDirectory();
		}
	}

	public File get() {
		return file;
	}

	public BufferedImage getImg() {
		if (file == null)
			return null;
		try {
			return ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
