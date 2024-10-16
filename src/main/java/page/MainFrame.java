package page;

import common.system.P;
import main.Opts;
import main.Printer;
import plugin.ui.main.UIPlugin;
import plugin.ui.main.util.MenuBarHandler;
import utilpc.PP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ConcurrentModificationException;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	public static int x = 0, y = 0;
	public static Rectangle rect = null, crect = null;
	public static Font font = null;
	public static boolean closeClicked = false;

	public static String fontType = "Dialog";
	public static int fontStyle = Font.PLAIN;
	public static final int fontSize = 24;
	public static MainFrame F;

	private static Page mainPanel = null;

	public static void changePanel(Page p) {
		F.FchangePanel(p);

		JMenuItem back = MenuBarHandler.getFileMenu("Back");
		if(back != null && p != null)
			back.setEnabled(p.getBackButton() != null);
	}

	public static void callback(Object obj) {
		if (mainPanel != null)
			mainPanel.callBack(obj);
	}

	public static void exitAll() {
		if (mainPanel != null)
			mainPanel.exitAll();
	}

	public static Page getPanel() {
		return mainPanel;
	}

	public static void timer(int t) {
		if (mainPanel != null && !F.changingPanel)
			mainPanel.timer(t);
	}

	protected static void resized() {
		F.Fresized();
	}

	private static void setFonts() {
		font = UIPlugin.getFont();
	}

	private boolean settingsize = false, changingPanel = false;

	public MainFrame(String ver) {
		super(Page.get(MainLocale.PAGE, "title") + " Ver " + ver);
		F = this;
		setLayout(null);
		addListener();
		sizer();
	}

	public void initialize() {
		changePanel(new LoadPage());
		Fresized();
	}

	public void sizer() {
		if (crect == null) {
			PP screen = new PP(Toolkit.getDefaultToolkit().getScreenSize());
			rect = new PP(0, 0).toRectangle(new P(screen.x, screen.y));
			setBounds(rect);
			int nx = rect.width - getRootPane().getWidth();
			int ny = rect.height - getRootPane().getHeight();
			crect = rect;
			if (nx != x || ny != y) {
				x = nx;
				y = ny;
			}
		} else {
			rect = crect;
			setBounds(rect);
		}
	}

	private void addListener() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent arg0) {
				if (mainPanel != null)
					mainPanel.windowActivated();
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				JMenuItem menu = MenuBarHandler.getFileMenu("Save");
				if (menu != null)
					menu.setEnabled(false);

				if (!closeClicked) {
					boolean[] conf = Opts.confirmSave();

					if (conf != null)
						changePanel(new SavePage(conf));
					else if (menu != null)
						menu.setEnabled(true);
				} else {
					Opts.warnPop("Saving progress...\nPlease wait!", "Saving");
				}
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				if (mainPanel != null)
					mainPanel.windowDeactivated();
			}
		});

		Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
			if (event.getID() == KeyEvent.KEY_PRESSED && mainPanel != null)
				mainPanel.keyPressed((KeyEvent) event);
			if (event.getID() == KeyEvent.KEY_RELEASED && mainPanel != null)
				mainPanel.keyReleased((KeyEvent) event);
			if (event.getID() == KeyEvent.KEY_TYPED && mainPanel != null)
				mainPanel.keyTyped((KeyEvent) event);
		}, AWTEvent.KEY_EVENT_MASK);

		Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
			if (event.getID() == MouseEvent.MOUSE_PRESSED && mainPanel != null)
				mainPanel.mousePressed((MouseEvent) event);
			if (event.getID() == MouseEvent.MOUSE_RELEASED && mainPanel != null)
				mainPanel.mouseReleased((MouseEvent) event);
			if (event.getID() == MouseEvent.MOUSE_CLICKED && mainPanel != null)
				mainPanel.mouseClicked((MouseEvent) event);
		}, AWTEvent.MOUSE_EVENT_MASK);

		Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
			if (event.getID() == MouseEvent.MOUSE_MOVED && mainPanel != null)
				mainPanel.mouseMoved((MouseEvent) event);
			if (event.getID() == MouseEvent.MOUSE_DRAGGED && mainPanel != null)
				mainPanel.mouseDragged((MouseEvent) event);
		}, AWTEvent.MOUSE_MOTION_EVENT_MASK);

		Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
			if (event.getID() == MouseEvent.MOUSE_WHEEL && mainPanel != null)
				mainPanel.mouseWheel((MouseEvent) event);
		}, AWTEvent.MOUSE_WHEEL_EVENT_MASK);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent arg0) {
				setCrect();
			}

			@Override
			public void componentResized(ComponentEvent arg0) {
				Fresized();
				setCrect();
			}
		});
	}

	private void FchangePanel(Page p) {
		if (p == null)
			return;
		changingPanel = true;
		if (mainPanel != null)
			if (p.getFront() == mainPanel)
				mainPanel.leave();
			else
				mainPanel.exit();
		add(p);
		if (mainPanel != null)
			remove(mainPanel);
		mainPanel = p;
		validate();
		p.renew();
		p.resized();
		repaint();
		changingPanel = false;
	}

	private void Fresized() {
		if (settingsize)
			return;
		settingsize = true;
		int w = getRootPane().getWidth();
		int h = getRootPane().getHeight();
		try {
			setFonts();
		} catch (ConcurrentModificationException cme) {
			Printer.p("MainFrame", 217, "Failed to set Font");
		}
		if (mainPanel != null)
			mainPanel.componentResized(w, h);
		settingsize = false;
	}

	private void setCrect() {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle r = getBounds();
		if ((r.x + r.width) >= 0 && r.y >= 0 && r.x < d.width && (r.y - r.height) < d.height)
			crect = r;
	}
}