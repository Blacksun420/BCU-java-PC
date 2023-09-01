package page.info.edit;

import common.CommonStatic;
import common.battle.data.AtkDataModel;
import common.pack.PackData.UserPack;
import common.pack.UserProfile;
import common.util.stage.Music;
import main.MainBCU;
import page.JL;
import page.JTF;
import page.JTG;
import page.Page;
import page.info.filter.TraitList;
import page.support.ListJtfPolicy;
import utilpc.Interpret;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Vector;

import static common.util.Data.TRAIT_EVA;
import static common.util.Data.TRAIT_RED;

class AtkEditTable extends Page {

	private static final long serialVersionUID = 1L;

	private final JL latk = new JL(1, "atk");
	private final JL lpre = new JL(1, "preaa");
	private final JL lp0 = new JL(1, "p0");
	private final JL lp1 = new JL(1, "p1");
	private final JL ltp = new JL(1, "type");
	private final JL ldr = new JL(1, "dire");
	private final JL lct = new JL(1, "count");
	private final JL lab = new JL(1, "ability");
	private final JL lmv = new JL(1, "move");
	private final JTF fatk = new JTF();
	private final JTF fpre = new JTF();
	private final JTF fp0 = new JTF();
	private final JTF fp1 = new JTF();
	private final JTF ftp = new JTF();
	private final JTF fdr = new JTF();
	private final JTF fct = new JTF();
	private final JTF fab = new JTF();
	private final JTF fmv = new JTF();
	private final JTG isr = new JTG(1, "isr");
	private final TraitList atktr = new TraitList(false);
	private final JScrollPane scrtr = new JScrollPane(atktr);
	private final JComboBox<Music> aud = new JComboBox<>();
	private final JComboBox<Music> aud1 = new JComboBox<>();

	private final ListJtfPolicy ljp = new ListJtfPolicy();
	private final boolean editable;

	private double mul;
	private double lvMul;
	private boolean changing = false;

	protected AtkDataModel adm;
	protected ProcTable.AtkProcTable apt;

	protected AtkEditTable(Page p, UserPack pack) {
		super(p);
		editable = pack.editable;

		pini(pack);
		ini();
	}

	@Override
    public JButton getBackButton() {
		return null;
	}

	@Override
	public void callBack(Object o) {
		getFront().callBack(o);
	}

	@Override
	protected void resized(int x, int y) {
		set(latk, x, y, 0, 0, 200, 50);
		set(lpre, x, y, 0, 50, 200, 50);
		set(lp0, x, y, 0, 100, 200, 50);
		set(lp1, x, y, 0, 150, 200, 50);
		set(ltp, x, y, 0, 200, 200, 50);
		set(ldr, x, y, 0, 250, 200, 50);
		set(lct, x, y, 0, 300, 200, 50);
		set(lab, x, y, 0, 350, 200, 50);
		set(lmv, x, y, 0, 400, 200, 50);
		set(isr, x, y, 0, 450, 400, 50);
		set(aud, x, y, 0, 500, 200, 50);
		set(scrtr, x, y, 0, 550, 400, 350);
		set(fatk, x, y, 200, 0, 200, 50);
		set(fpre, x, y, 200, 50, 200, 50);
		set(fp0, x, y, 200, 100, 200, 50);
		set(fp1, x, y, 200, 150, 200, 50);
		set(ftp, x, y, 200, 200, 200, 50);
		set(fdr, x, y, 200, 250, 200, 50);
		set(fct, x, y, 200, 300, 200, 50);
		set(fab, x, y, 200, 350, 200, 50);
		set(fmv, x, y, 200, 400, 200, 50);
		set(aud1, x, y, 200, 500, 200, 50);
	}

	protected void setData(AtkDataModel data, double multi, double lvMulti) {
		changing = true;
		adm = data;
		mul = multi;
		lvMul = lvMulti;

		fatk.setText(String.valueOf((int) (Math.round(adm.atk * lvMul) * mul)));
		fpre.setText(MainBCU.convertTime(adm.pre));
		aud.setSelectedItem(adm.audio == null ? null : adm.audio.get());
		aud1.setSelectedItem(adm.audio1 == null ? null : adm.audio1.get());
		fp0.setText(String.valueOf(adm.ld0));
		fp1.setText(String.valueOf(adm.ld1));
		ftp.setText(String.valueOf(adm.targ));
		fdr.setText(String.valueOf(adm.dire));
		fct.setText(String.valueOf(adm.count));
		fmv.setText(String.valueOf(adm.move));
		apt.setData(adm.ce.common ? adm.ce.rep.proc : adm.proc);
		int alt = adm.getAltAbi();
		int i = 0;
		StringBuilder str = new StringBuilder("{");
		while (alt > 0) {
			if ((alt & 1) == 1) {
				if (str.length() > 1)
					str.append(",");
				str.append(i);
			}
			alt >>= 1;
			i++;
		}
		fab.setText(str + "}");
		isr.setSelected(adm.range);

		for (int k = 0; k < atktr.list.size(); k++)
			if (adm.traits.contains(atktr.list.get(k)))
				atktr.addSelectionInterval(k, k);
			else
				atktr.removeSelectionInterval(k, k);
		changing = false;
	}

	private void pini(UserPack pack) {
		atktr.list.addAll(UserProfile.getBCData().traits.getList().subList(TRAIT_RED,TRAIT_EVA));
		Vector<Music> vs = new Vector<>();
		vs.add(null);
		vs.addAll(UserProfile.getBCData().musics.getList());

		if (pack != null) {
			atktr.list.addAll(pack.traits.getList());
			vs.addAll(pack.musics.getList());
			for (String dep : pack.desc.dependency) {
				UserPack pacc = UserProfile.getUserPack(dep);
				atktr.list.addAll(pacc.traits.getList());
				vs.addAll(pacc.musics.getList());
			}
		}
		aud.setModel(new DefaultComboBoxModel<>(vs));
		aud1.setModel(new DefaultComboBoxModel<>(vs));
		fireDimensionChanged();
	}

	private void ini() {
		set(latk);
		set(lpre);
		set(lp0);
		set(lp1);
		set(ltp);
		set(ldr);
		set(lct);
		set(lab);
		set(lmv);
		set(fatk);
		set(fpre);
		set(fp0);
		set(fp1);
		set(ftp);
		set(fdr);
		set(fct);
		set(fab);
		set(fmv);
		add(isr);
		add(scrtr);
		add(aud);
		add(aud1);

		ftp.setToolTipText(
				"<html>" + "+1 for normal attack<br>"
						+ "+2 to attack kb<br>"
						+ "+4 to attack underground<br>"
						+ "+8 to attack corpse<br>"
						+ "+16 to attack soul<br>"
						+ "+32 to attack ghost<br>"
						+ "+64 to attack entities that can revive others<br>"
						+ "+128 to attack enter animations</html>");
		fdr.setToolTipText("direction, 1 means attack enemies, 0 means not an attack, -1 means assist allies");

		fpre.setToolTipText(
				"<html>use 0 for random attack attaching to previous one.<br>pre=0 for first attack will invalidate it</html>");
		StringBuilder ttt = new StringBuilder("<html>enter ID of abilities separated by comma or space.<br>" + "it changes the ability state"
				+ "(has to not having, and viceversa)<br>"
				+ "it won't change back until you make another attack to change it<br>");

		for (int i = 0; i < Interpret.SABIS.length; i++)
			ttt.append(i).append(": ").append(Interpret.SABIS[i]).append("<br>");
		fab.setToolTipText(ttt + "</html>");

		isr.setEnabled(editable);
		atktr.setEnabled(editable);
		setFocusTraversalPolicy(ljp);
		setFocusCycleRoot(true);

		isr.setLnr(x -> adm.range = isr.isSelected());
		atktr.setListData();
		int m = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
		atktr.setSelectionMode(m);
		addListeners();
	}

	private void addListeners() {
		aud.addActionListener(x -> {
			if (changing)
				return;
			changing = true;
			Music m = (Music) aud.getSelectedItem();
			adm.audio = m != null ? m.getID() : null;
			changing = false;
		});
		aud1.addActionListener(x -> {
			if (changing)
				return;
			changing = true;
			Music m = (Music) aud.getSelectedItem();
			adm.audio1 = m != null ? m.getID() : null;
			changing = false;
		});

		atktr.addListSelectionListener(arg0 -> {
			if (!changing && !atktr.getValueIsAdjusting()) {
				for (int i = 0; i < atktr.list.size(); i++)
					if (atktr.isSelectedIndex(i)) {
						adm.traits.add(atktr.list.get(i));
					} else
						adm.traits.remove(atktr.list.get(i));
			}
		});
	}

	private void input(JTF jtf, String text) {
		if (text.length() > 0) {
			if (jtf == fab) {
				int[] ent = CommonStatic.parseIntsN(text);
				int ans = 0;
				for (int i : ent)
					if (i >= 0 && i < Interpret.ABIS.length)
						if (ans == -1)
							ans = 1 << i;
						else
							ans |= 1 << i;
				adm.alt = ans;
			}
			int v = CommonStatic.parseIntN(text);
			if (jtf == fatk) {
				adm.atk = findIdealAtkValue(v);
			} else if (jtf == fpre) {
				double w = CommonStatic.parseDoubleN(text);
				adm.pre = w < 0 ? 1 : convertPreTime(w);
			} else if (jtf == fp0) {
				adm.ld0 = v;
				if (adm.ld0 != 0 || adm.ld1 != 0)
					if (adm.ld1 <= v)
						adm.ld1 = v + 1;
			} else if (jtf == fp1) {
				adm.ld1 = v;
				if (adm.ld0 != 0 || adm.ld1 != 0)
					if (adm.ld0 >= v)
						adm.ld0 = v - 1;
			} else if (jtf == ftp) {
				if (v < 1)
					v = 1;
				adm.targ = v;
			} else if (jtf == fdr) {
				if (v < -1)
					v = -1;
				if (v > 1)
					v = 1;
				adm.dire = v;
			} else if (jtf == fct) {
				if (v < 0)
					v = -1;
				adm.count = v;
			} else if (jtf == fmv)
				adm.move = v;
		}
		callBack(null);
	}

	private void set(JLabel jl) {
		jl.setHorizontalAlignment(SwingConstants.CENTER);
		jl.setBorder(BorderFactory.createEtchedBorder());
		add(jl);
	}

	private void set(JTF jtf) {
		jtf.setEditable(editable);
		add(jtf);
		ljp.add(jtf);

		jtf.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent fe) {
				if (changing)
					return;
				input(jtf, jtf.getText());
				callBack(null);
			}

		});
	}

	private int findIdealAtkValue(int val) {
		double sign = Math.signum(val);
		double lvValue = Math.round(Math.abs(val) * 1.0 / mul);

		return (int) ((lvValue + 0.5) * sign / lvMul);
	}

	private int convertPreTime(double val) {
		if (!MainBCU.seconds)
			return (int) val;
		return (int) (val * 30);
	}

	protected void setProcTable(ProcTable.AtkProcTable a) {
		apt = a;
	}
}