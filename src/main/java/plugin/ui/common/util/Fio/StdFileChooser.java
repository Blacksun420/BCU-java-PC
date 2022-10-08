package plugin.ui.common.util.Fio;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;


public class StdFileChooser extends AbstractFileChooser {
    private final Component parent;
    private FileNameExtensionFilter extensionFilter;
    private File defaultFile;
    private File defaultDirectory;

    public StdFileChooser(JFrame f, File defaultFile, FileNameExtensionFilter filter) {
        this(f, defaultFile);
        extensionFilter = filter;
    }

    public StdFileChooser(Component parent) {
        this.parent = parent;
    }

    public StdFileChooser(Component parent, File defaultFile) {
        this(parent);
        this.defaultFile = defaultFile;
        this.defaultDirectory = defaultFile == null ? null : defaultFile.getParentFile();
    }

    @Override
    protected String setTitle() {
        return "Choose File";
    }

    @Override
    public File getDefaultDirectory() {
        return defaultDirectory;
    }

    public void setDefaultDirectory(File defaultDirectory) {
        this.defaultDirectory = defaultDirectory;
    }

    @Override
    protected boolean handleWhenSaveExists() {
        return false;
    }

    @Override
    protected FileNameExtensionFilter getFileNameExtensionFilter() {
        return extensionFilter;
    }

    @Override
    protected Component getParentFrame() {
        return parent;
    }

    @Override
    protected File getDefaultFile() {
        return defaultFile == null ? getTarget() : defaultFile;
    }
}
