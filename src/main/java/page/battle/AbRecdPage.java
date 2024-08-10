package page.battle;

import common.CommonStatic;
import common.battle.BasisSet;
import common.pack.Identifier;
import common.util.stage.MapColc;
import common.util.stage.Replay;
import common.util.stage.Stage;
import main.MainBCU;
import main.Opts;
import page.*;
import page.basis.BasisPage;
import page.info.StageViewPage;

import javax.swing.*;

public abstract class AbRecdPage extends DefaultPage {

	private static final long serialVersionUID = 1L;

	private final JBTN rply = new JBTN(MainLocale.PAGE, "rply");
	private final JBTN recd = new JBTN(MainLocale.PAGE, "expmp4");
	private final JBTN vsta = new JBTN(MainLocale.PAGE, "csta");
	private final JBTN jlu = new JBTN(MainLocale.PAGE, "line");
	private final JTF seed = new JTF();
	private final JTG larg = new JTG(MainLocale.PAGE, "larges");
	private final JBTN imgs = new JBTN(MainLocale.PAGE, "png");
	private final JBTN clu = new JBTN(MainLocale.PAGE, "Copy Lineup");
	private final JLabel len = new JLabel();

	private final JBTN ista = new JBTN();

	private StageViewPage svp;
	private BasisPage bp;

	protected final boolean editable;

	public AbRecdPage(Page p, boolean edit) {
		super(p);
		editable = edit;
	}

	public abstract Replay getSelection();

	protected void preini() {
		add(rply);
		add(len);
		add(recd);
		add(larg);
		add(imgs);
		add(clu);
		add(vsta);
		add(seed);
		add(jlu);
		add(ista);
		ista.setHorizontalAlignment(SwingConstants.LEFT);
		len.setBorder(BorderFactory.createEtchedBorder());
		addListeners();
	}

	@Override
	protected void renew() {
		setList();
		if (editable && svp != null) {
			Replay r = getSelection();
			Stage ns = svp.getStage();
			if (r != null && ns != null) {
				if (r.st != null && r.st.safeGet() != null) {
					if (ns != r.st.get() && Opts.conf("are you sure to change stage?")) {
						r.st = ns.id;
						r.unsaved = true;
					}
				} else {
					r.st = ns.id;
					r.unsaved = true;
				}
				setRecd(r);
			}
		}
		if (editable && bp != null) {
			Replay r = getSelection();
			if (r != null && !r.lu.sameAs(BasisSet.current().sele) && Opts.conf("are you sure to change lineup?")) {
				r.lu = BasisSet.current().sele.copy();
				r.unsaved = true;
			}
		}
		bp = null;
		svp = null;
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(rply, x, y, 600, 100, 300, 50);
		set(ista, x, y, 950, 100, 650, 50);
		set(recd, x, y, 600, 200, 300, 50);
		set(larg, x, y, 950, 200, 300, 50);
		set(imgs, x, y, 1300, 200, 300, 50);
		set(len, x, y, 950, 300, 300, 50);
		set(seed, x, y, 1300, 300, 300, 50);
		set(vsta, x, y, 1300, 400, 300, 50);
		set(clu, x, y, 600, 500, 300, 50);
		set(jlu, x, y, 950, 500, 300, 50);
	}

	protected abstract void setList();

	protected void setRecd(Replay r) {
		seed.setEditable(editable && r != null);
		vsta.setEnabled(r != null);
		Stage st = r == null || r.st == null ? null : r.st.safeGet();
		boolean a = st != null && st.id.toString().equals(r.st.toString());
		rply.setEnabled(a);
		recd.setEnabled(a);
		imgs.setEnabled(a);
		ista.setText(!a ? "(unavailable" + (r != null ? r.st.toString() : "") + ")" : st.getCont() + ":  " + st);
		ista.setEnabled(a);
		jlu.setEnabled(r != null);
		if (r == null) {
			len.setText("");
			seed.setText("");
		} else {
			seed.setText("seed: " + r.seed);
			len.setText("length: " + MainBCU.convertTime(r.getLen()));
		}
	}

	private void addListeners() {
		vsta.setLnr(x -> {
			Stage rStage = getSelection().st == null ? null : getSelection().st.safeGet();
			changePanel(svp = new StageViewPage(getThis(), MapColc.values(), rStage));
		});

		ista.setLnr(x -> changePanel(new StageViewPage(getThis(), MapColc.values(), getSelection().st.safeGet()))); //Pinpoints without change

		jlu.setLnr(x -> {
			Identifier<Stage> st = getSelection().st;

			if (st == null || st.safeGet() == null)
				changePanel(bp = new BasisPage(getThis()));
			else
				changePanel(bp = new BasisPage(getThis(), st.get(), getSelection().star, getSelection().save == 2));
		});

		rply.setLnr(x -> changePanel(new BattleInfoPage(getThis(), getSelection(), 0)));

		recd.addActionListener(arg0 -> {
			Replay r = getSelection();
			int conf = 1;
			if (larg.isSelected())
				conf |= 2;
			changePanel(new BattleInfoPage(getThis(), r, conf));
		});

		imgs.addActionListener(arg0 -> {
			Replay r = getSelection();
			int conf = 5;
			if (larg.isSelected())
				conf |= 2;
			changePanel(new BattleInfoPage(getThis(), r, conf));
		});

		seed.setLnr(x -> {
			if (isAdj())
				return;
			Replay r = getSelection();
			if (r == null)
				return;
			r.seed = CommonStatic.parseLongN(seed.getText());
			r.unsaved = true;
			setRecd(r);
		});

		clu.setLnr(x -> BasisSet.current().lb.add(getSelection().lu.copy()));
	}

}