package page;

import javax.swing.*;

public class LoadPage extends Page {

    private static final long serialVersionUID = 1L;

    public static LoadPage lp;

    public static void prog(double i) {
        lp.jpb.setValue((int) (i * 1000));
    }

    public static void prog(String str) {
        lp.set(str);
    }

    public static void packProg(String str) {
        lp.jl.setText(str);
    }

    private final JLabel jl = new JLabel();
    private final JProgressBar jpb = new JProgressBar();

    private String temp;

    protected LoadPage() {
        super(null);
        lp = this;

        add(jl);
        add(jpb);
        lp.jpb.setMaximum(1000);
        resized(true);
    }

    public void accept(double dl) {
        jpb.setValue((int) (dl * 1000));
    }

    @Override
    public JButton getBackButton() {
        return null;
    }

    @Override
    protected void resized(int x, int y) {
        setBounds(0, 0, x, y);
        set(jl, x, y, 100, 500, 2000, 50);
        set(jpb, x, y, 100, 600, 2100, 50);
    }

    @Override
    public void timer(int t) {
        if (temp != null) {
            jl.setText(temp);
            temp = null;
            jpb.setValue(0);
        }
    }

    private void set(String str) {
        temp = str;
    }

}