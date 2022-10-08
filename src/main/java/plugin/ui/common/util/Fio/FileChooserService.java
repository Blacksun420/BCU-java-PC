package plugin.ui.common.util.Fio;

import page.MainFrame;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public abstract class FileChooserService {
    private static File LastSelectedFile = new File("./");

    public static File getFile() {
        return getFile("./");
    }

    public static File getFile(String defaultDirectoryPath) {
        StdFileChooser chooser = new StdFileChooser(MainFrame.F, LastSelectedFile);
        File defaultDirectory;
        if (defaultDirectoryPath != null && (defaultDirectory = new File(defaultDirectoryPath)).exists()) {
            chooser.setDefaultDirectory(defaultDirectory);
        }
        return saveUserSelect(chooser.selectFile());
    }

    public static File getFile(String path, String description, String... extensions) {
        StdFileChooser chooser = new StdFileChooser(MainFrame.F, LastSelectedFile,
                new FileNameExtensionFilter(description, extensions));

        chooser.setDefaultDirectory(new File(path));
        return saveUserSelect(chooser.selectFile());
    }

    private static File saveUserSelect(File file) {
        if (file != null) {
            LastSelectedFile = file;
        }
        return file;
    }

    public static void copyFile(File src, File dst) {
        FileOutputStream out = null;
        try {
            byte[] data = Files.readAllBytes(src.toPath());
            out = new FileOutputStream(dst);
            out.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
