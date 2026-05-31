package page.info.edit;

import common.util.Data.Proc.Condition;
import page.DefaultPage;
import page.JL;
import page.JTA;
import page.Page;

import javax.swing.*;
import java.util.function.Consumer;

public class ConditionPage extends DefaultPage {

    public Consumer<Condition> exitter;
    private Condition condition;
    private final JL pre = new JL("Pre-ATK");
    private final JTA jpre = new JTA();
    private final JScrollPane spre = new JScrollPane(jpre);
    private final JL post = new JL("Post-ATK");
    private final JTA jpost = new JTA();
    private final JScrollPane spost = new JScrollPane(jpost);

    public ConditionPage(Page pg) {
        super(pg);
        ini();
    }

    @Override
    public void exit() {
        if (exitter != null)
            exitter.accept(condition);
    }

    public void setData(Condition c) {
        condition = c;
        jpre.setText(condition.pre);
        jpost.setText(condition.post);
    }

    private void ini() {
        add(pre);
        add(spre);
        add(post);
        add(spost);
        addListeners();
    }

    private void addListeners() {
        jpre.setLnr(e -> condition.pre = jpre.getText());
        jpost.setLnr(e -> condition.post = jpost.getText());
    }

    @Override
    public void callBack(Object o) {
        fireDimensionChanged();
    }

    @Override
    public void resized(int x, int y) {
        super.resized(x, y);
        set(pre, x, y, 50, 100, 2200, 50);
        set(spre, x, y, 50, 150, 2200, 500);
        set(post, x, y, 50, 650, 2200, 50);
        set(spost, x, y, 50, 700, 2200, 500);
    }
}
