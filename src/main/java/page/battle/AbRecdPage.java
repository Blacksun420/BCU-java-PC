package page.battle;

import common.CommonStatic;
import common.battle.BasisSet;
import common.pack.Identifier;
import common.pack.UserProfile;
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

	private final JBTN rply = new JBTN(0, "rply");
	private final JBTN recd = new JBTN(-1, "mp4");
	private final JBTN vsta = new JBTN(0, "vsta");
	private final JBTN jlu = new JBTN(0, "line");
	private final JTF seed = new JTF();
	private final JTG larg = new JTG(0, "larges");
	private final JBTN imgs = new JBTN(-1, "PNG");
	private final JBTN clu = new JBTN(0, "Copy Lineup");
	private final JLabel len = new JLabel();

	private final JL ista = new JL();
	private final JL imap = new JL();

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
		add(imap);
		add(ista);
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
				ista.setText(UserProfile.getPack(r.st.pack.substring(0, r.st.pack.indexOf('/'))) != null ? r.st.get().toString() : "unavailable (" + r.st.toString() + ")");
				imap.setText(r.st.getCont().toString());
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
		set(imap, x, y, 950, 100, 300, 50);
		set(ista, x, y, 1300, 100, 300, 50);
		set(recd, x, y, 600, 200, 300, 50);
		set(larg, x, y, 950, 200, 300, 50);
		set(imgs, x, y, 1300, 200, 300, 50);
		set(clu, x, y, 1300, 300, 300, 50);
		set(len, x, y, 600, 300, 300, 50);
		set(vsta, x, y, 600, 600, 300, 50);
		set(jlu, x, y, 950, 600, 300, 50);
		set(seed, x, y, 950, 300, 300, 50);
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
		ista.setText(!a ? "(unavailable)" : st.toString());
		imap.setText(!a ? "(unavailable)" : st.getCont().toString());
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

		jlu.setLnr(x -> {
			Identifier<Stage> st = getSelection().st;

			if (st == null || st.safeGet() == null)
				changePanel(bp = new BasisPage(getThis()));
			else
				changePanel(bp = new BasisPage(getThis(), st.get(), st.get().getLim(getSelection().star)));
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
