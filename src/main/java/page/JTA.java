package page;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.function.Consumer;

public class JTA extends JEditorPane implements CustomComp {

    private static final long serialVersionUID = 1L;
    private String hint;
    //private Stack<String> htmls = new Stack<>();
    //private static final String[] stackable = {"a", "h1", "h2", "h3", "h4", "h5", "h6", "p"}; //TODO: img
    //private static final String[] emptyValid = {"br"};

    public JTA() {
        super();
    }

    public String getRawText() {
        String text = getText().replace("\n", "");
        int sta = -1;
        while ((sta = text.indexOf('<', sta + 1)) != -1) {
            int oldLen = text.length();
            text = text.substring(0, sta) + text.substring(text.indexOf('>') + 1);
            sta -= oldLen - text.length();
        }
        return text;
    }

    @SuppressWarnings("all")
    public String assignSplitText(int lim) {
        if (lim == -1 || getRawText().length() <= lim)
            return getText();
        String tex = getText();
        if (tex.lastIndexOf('>') == -1 || tex.indexOf('<') > lim)
            return tex.substring(0, lim);
        StringBuilder ans = new StringBuilder();
        String last = "";
        int plusLen = 0;

        int sta = tex.indexOf('<'), col = tex.indexOf('>'), clen = 0;
        while (sta != -1 && col != -1 && sta + plusLen < lim) {
            plusLen += col - sta + 1;
            sta = tex.indexOf('<', sta+1);
            col = tex.indexOf('>', col+1);
        }
        if (sta != -1 && col != -1 && sta + plusLen >= lim)
            return tex.substring(0, lim + plusLen) + tex.substring(sta, col + 1);
        return tex.substring(0, lim + plusLen);
    }

    /*private int validHTML(String text) {
        if (text.startsWith("/")) {
            String last = htmls.pop();
            if (last != null && text.substring(1).equals(last.contains(" ") ? last.substring(0, last.indexOf(" ")) : last))
                return 2;
            return -1;
        } //TODO
        return -1;
    }*/

    public void setLnr(Consumer<FocusEvent> c) {
        addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                c.accept(e);
            }

        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (getText().isEmpty() && hint != null && !isFocusOwner()) {
            int h = getHeight();
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Insets ins = getInsets();
            FontMetrics fm = g.getFontMetrics();
            int c0 = getBackground().getRGB();
            int c1 = getForeground().getRGB();
            int m = 0xfefefefe;
            int c2 = ((c0 & m) >>> 1) + ((c1 & m) >>> 1);
            g.setColor(new Color(c2, true));
            g.drawString(hint, ins.left, h / 2 + fm.getAscent() / 2 - 2);
        }
    }

    public void setHintText(String t) {
        hint = t;
    }
}
