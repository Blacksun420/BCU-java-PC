package page;

import common.util.lang.LocaleCenter.Binder;
import io.BCMusic;
import main.MainBCU;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JBTN extends JButton implements LocComp {

    private static final long serialVersionUID = 1L;

    private final LocSubComp lsc;

    public JBTN() {
        lsc = new LocSubComp(this);
    }

    public JBTN(Binder binder) {
        this();
        lsc.init(binder);
    }

    public JBTN(int i, String str) {
        this();
        lsc.init(i, str);
    }

    public JBTN(String str) {
        this(-1, str);
    }

    @Override
    public LocSubComp getLSC() {
        return lsc;
    }

    public void setLnr(Consumer<ActionEvent> c) {
        addActionListener(e -> {
            if(MainBCU.buttonSound)
                BCMusic.doSound(11);
            c.accept(e);
        });
    }

    public void setLnr(Supplier<Page> s) {
        setLnr((e) -> MainFrame.changePanel(s.get()));
    }
}