package plugin.ui.common.util.Fio;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;


public abstract class AbstractFileChooser<T> implements FileChooser<T> {
    private File target;

    public File getTarget() {
        return target;
    }

    @Override
    public File selectFile() {
        return JFileChooserSelect();
    }


    @SuppressWarnings("unchecked")
    @Override
    public T open() {
        JFileChooserSelect();
        if (target == null) {
            return null;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(target))) {
            T result = (T) in.readObject();
            handleWhenSuccess();
            return result;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public File save(T o) {
        JFileChooserSelect();
        if (target == null) {
            return null;
        }
        if (target.exists()) {
            if (!handleWhenSaveExists()) {
                return null;
            }
        }

        // save
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(target))) {
            out.writeObject(o);
            handleWhenSuccess();
            return target;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File JFileChooserSelect() {
        // target = null;
        JFileChooser chooser = new JFileChooser(getDefaultDirectory());
        chooser.setDialogTitle(setTitle());
        chooser.setSelectedFile(getDefaultFile());
        FileNameExtensionFilter filter = getFileNameExtensionFilter();
        chooser.addChoosableFileFilter(filter);

        while (chooser.showDialog(getParentFrame(), "select") != JFileChooser.CANCEL_OPTION) {
            target = chooser.getSelectedFile();

            if (filter == null) {
                return target;
            } else {
                if (filter.accept(target)) {
                    target = chooser.getSelectedFile();
                    return target;
                } else {
                    boolean isBreak = handleWhenUnFitted(chooser);
                    if (isBreak) {
                        break;
                    }
                }
            }
        }

        return null;
    }


    private boolean handleWhenUnFitted(JFileChooser chooser) {
        String msg = "Please select " + getFileNameExtensionFilter().getDescription();
        JOptionPane.showMessageDialog(getParentFrame(), msg, "confirm", JOptionPane.ERROR_MESSAGE);
        chooser.setSelectedFile(null);
        return false;
    }

    private void handleWhenSuccess() {
    }

    protected File getDefaultDirectory() {
        return new File("./");
    }

    protected String setTitle() {
        return this.getClass().getSimpleName();
    }

    protected abstract boolean handleWhenSaveExists();

    protected abstract FileNameExtensionFilter getFileNameExtensionFilter();

    protected File getDefaultFile() {
        return null;
    }

    protected abstract Component getParentFrame();

}
