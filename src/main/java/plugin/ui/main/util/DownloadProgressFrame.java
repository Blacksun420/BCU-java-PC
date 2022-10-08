package plugin.ui.main.util;

import page.MainFrame;
import plugin.ui.main.page.comp.BCULabel;

import javax.swing.*;
import java.awt.*;

public class DownloadProgressFrame extends JFrame {
    public JProgressBar progress;
    public BCULabel text_above;
    public BCULabel text_below;

    public DownloadProgressFrame(String text_above, String text_below) {
        super("DOWNLOAD");
        if (MainFrame.F != null) {
            int w = MainFrame.F.getRootPane().getWidth();
            Rectangle r = MainFrame.F.getBounds();
            setBounds(r.x + (r.width >> 2), r.y + (r.height >> 2), w >> 1, w >> 3);
        }else{
            setBounds(200, 200, 800, 200);
        }
        init(text_above, text_below);
    }

    private void init(String text_above, String text_below) {
        this.text_above = new BCULabel();
        this.text_below = new BCULabel();

        this.text_above.setHtmlText(text_above);
        this.text_below.setHtmlText(text_below);

        progress = new JProgressBar(0, 100);
        progress.setStringPainted(true);

//        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        add(this.text_above, BorderLayout.NORTH);
        add(progress, BorderLayout.CENTER);
        add(this.text_below, BorderLayout.SOUTH);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }

    public void setProgress(double value){
        progress.setValue((int) (100 * value));
    }

}
