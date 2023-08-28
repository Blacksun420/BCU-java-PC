package page.anim;

import page.DefaultPage;
import page.JL;
import page.Page;
import utilpc.Algorithm;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class SpriteEditPage extends DefaultPage {

	private static final long serialVersionUID = 1L;

	private final JL disp = new JL();
	private final JScrollPane jsp = new JScrollPane(disp);
	private final JL jlh = new JL();
	private final JL jls = new JL();
	private final JL jlb = new JL();
	private final JSlider jslh = new JSlider(-180, 180, 0);
	private final JSlider jsls = new JSlider(-100, 100, 0);
	private final JSlider jslb = new JSlider(-100, 100, 0);

	private final BufferedImage base;

	private BufferedImage curr;

	private int h, s, b;

	protected SpriteEditPage(Page p, BufferedImage bimg) {
		super(p);
		curr = base = bimg;
		ini();
		resized(true);
	}

	protected BufferedImage getEdit() {
		return curr;
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(jsp, x, y, 0, 50, 2000, 1000);
		set(jslh, x, y, 0, 1100, 300, 100);
		set(jsls, x, y, 300, 1100, 300, 100);
		set(jslb, x, y, 600, 1100, 300, 100);
		set(jlh, x, y, 0, 1050, 300, 100);
		set(jls, x, y, 300, 1050, 300, 100);
		set(jlb, x, y, 600, 1050, 300, 100);
	}

	private void addListeners() {
		jslh.addChangeListener(e -> setColor(jslh.getValue(), s, b));
		jsls.addChangeListener(e -> setColor(h, jsls.getValue(), b));
		jslb.addChangeListener(e -> setColor(h, s, jslb.getValue()));
	}

	private void ini() {
		add(jsp);
		add(jslh);
		add(jsls);
		add(jslb);
		add(jlh);
		add(jls);
		add(jlb);
		disp.setBorder(BorderFactory.createEtchedBorder());
		jslh.setMajorTickSpacing(30);
		jslh.setMinorTickSpacing(10);
		jslh.setPaintTicks(true);
		jsls.setMajorTickSpacing(20);
		jsls.setMinorTickSpacing(5);
		jsls.setPaintTicks(true);
		jslb.setMajorTickSpacing(20);
		jslb.setMinorTickSpacing(5);
		jslb.setPaintTicks(true);
		setColor(h, s, b);
		addListeners();
	}

	private void setColor(int hval, int sval, int bval) {
		h = hval;
		s = sval;
		b = bval;
		curr = Algorithm.shift(base, h / 360f, s / 100f, b / 100f);
		disp.setIcon(new ImageIcon(curr));
		jlh.setText("hue: " + h);
		jls.setText("saturation:" + s);
		jlb.setText("brightness:" + b);
	}

}
