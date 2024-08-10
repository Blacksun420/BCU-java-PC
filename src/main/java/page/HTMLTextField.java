package page;

import main.Opts;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;

public class HTMLTextField extends JEditorPane implements LocComp {

    private static final long serialVersionUID = 1L;

    private final LocSubComp lsc;
    private final HTMLEditorKit kit = new HTMLEditorKit();

    public HTMLTextField() {
        lsc = new LocSubComp(this);
        setContentType("text/html");
        setEditorKit(kit);
        setEditable(false);

        addHyperlinkListener(e -> {
            if (Desktop.isDesktopSupported() && e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (Exception exc) {
                    Opts.pop("Invalid URL: " + e.getURL(), "ERROR");
                    exc.printStackTrace();
                }
            }
        });
    }

    public HTMLTextField(String str) {
        this();
        setText(str);
    }

    public void setText(String str) {
        str = str.replace("\n", "<br>");
        Document doc = getDocument();
        try {
            doc.remove(0, doc.getLength());
            kit.insertHTML((HTMLDocument) doc, doc.getLength(), str, 0, 0, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stylize(String rules) {
        StyleSheet css = kit.getStyleSheet();
        css.addRule(rules);
    }

    @Override
    public LocSubComp getLSC() {
        return lsc;
    }
}
