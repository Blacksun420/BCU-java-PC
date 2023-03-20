package plugin.ui.main.page.comp;

import javax.swing.*;
import java.awt.*;

public class BCULabel extends JLabel {
    private static final long serialVersionUID = 1L;

    public BCULabel() {
        super();
        setBackground(Color.black);
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.BOTTOM);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Font defaultFont = UIManager.getFont("defaultFont");
        if (defaultFont != null) {
            g.setFont(defaultFont.deriveFont((float) 22));
        }
        super.paintComponent(g);
    }

    public void setHtmlText(String text) {
        setText("<html><h3>" + text + "</html></h3>");
    }
}
