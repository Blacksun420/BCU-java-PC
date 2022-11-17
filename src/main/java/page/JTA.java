package page;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.function.Consumer;

public class JTA extends JTextArea implements CustomComp {

    private static final long serialVersionUID = 1L;

    public JTA() {
        this("");
    }

    public JTA(String tos) {
        super(tos);
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
