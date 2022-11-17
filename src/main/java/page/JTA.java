package page;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class JTA extends JTextArea implements CustomComp {

    private static final long serialVersionUID = 1L;

    public JTA() {
        this("");
    }

    public JTA(String tos) {
        super(tos);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    append("\n");
                }
            }
        });

    }

    public void setLnr(Consumer<FocusEvent> c) {
        addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                c.accept(e);
            }

        });
    }
}
