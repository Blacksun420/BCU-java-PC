package page;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.function.Consumer;

public class JTA extends JEditorPane implements CustomComp {

    private static final long serialVersionUID = 1L;
    private String hint;

    public JTA() {
        super();
    }

    public String getRawText() {
        String text = getText().replace("\n", "");
        int sta = -1;
        while ((sta = text.indexOf('<', sta + 1)) != -1) {
            text = text.substring(0, sta) + text.substring(text.indexOf('>') + 1);
        }
        return text;
    }

    @SuppressWarnings("all")
    public String assignSplitText(int lim) {
        String str = getRawText();
        if (lim == -1 || str.length() <= lim)
            return getText();
        String tex = getText();
        if (tex.lastIndexOf('>') == -1 || tex.indexOf('<') > lim)
            return getText().substring(0, lim);
        str = str.substring(0, lim);
        StringBuilder ans = new StringBuilder();
        String last = "";

        int sta = tex.indexOf('<'), col = tex.indexOf('>'), clen = 0;
        while (col != -1) {
            String[] strs = tex.substring(col, col = tex.indexOf('>', col + 1)).split("<");
            ans.append(strs[0]);
            if (strs.length == 1 && sta == 0) {
                continue;
            }
            sta = tex.indexOf('<', sta + 1);
            last = strs[0];

            if (str.contains(strs[0])) {
                clen += last.length();
                ans.append(strs[1]);
            }
        }
        str = str.substring(clen);
        int enterCount = last.length() - str.length();
        ans.append(last, 0, str.length() + enterCount);

        return ans.toString();
    }

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
        if (getText().length() == 0 && hint != null && !isFocusOwner()) {
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
