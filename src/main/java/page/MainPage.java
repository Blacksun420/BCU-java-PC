package page;

import common.pack.PackData;
import common.pack.UserProfile;
import common.util.stage.MapColc;
import main.MainBCU;
import main.Opts;
import page.anim.DIYViewPage;
import page.anim.ImgCutEditPage;
import page.anim.MaAnimEditPage;
import page.anim.MaModelEditPage;
import page.basis.BasisPage;
import page.battle.BattleInfoPage;
import page.battle.RecdManagePage;
import page.info.ComparePage;
import page.info.StageViewPage;
import page.info.filter.EnemyFindPage;
import page.info.filter.UnitFindPage;
import page.pack.PackEditPage;
import page.pack.ResourcePage;
import page.view.*;
import plugin.ui.main.UIPlugin;
import plugin.ui.main.page.UIPage;

import javax.swing.*;

public class MainPage extends Page {

	private static final long serialVersionUID = 1L;

	private final JLabel memo = new JLabel();
	private final JLabel sgifau = new JLabel("Author of GIF exporter: Kevin Weiner, FM Software");
	private final JLabel welcome = new JLabel("Welcome " + MainBCU.author + "!");
	private final JBTN vuni = new JBTN(MainLocale.PAGE, "vuni");
	private final JBTN vene = new JBTN(MainLocale.PAGE, "vene");
	private final JBTN vsta = new JBTN(MainLocale.PAGE, "vsta");
	private final JBTN vdiy = new JBTN(MainLocale.PAGE, "vdiy");
	private final JBTN conf = new JBTN(MainLocale.PAGE, "conf");
	private final JBTN veff = new JBTN(MainLocale.PAGE, "veff");
	private final JBTN vcas = new JBTN(MainLocale.PAGE, "vcas");
	private final JBTN vbgr = new JBTN(MainLocale.PAGE, "vbgr");
	private final JBTN veif = new JBTN(MainLocale.PAGE, "veif");
	private final JBTN vuif = new JBTN(MainLocale.PAGE, "vuif");
	private final JBTN vmsc = new JBTN(MainLocale.PAGE, "vmsc");
	private final JBTN bass = new JBTN(MainLocale.PAGE, "bass");
	private final JBTN curr = new JBTN(MainLocale.PAGE, "curr");
	private final JBTN pcus = new JBTN(MainLocale.PAGE, "pcus");
	private final JBTN rply = new JBTN(MainLocale.PAGE, "rply");
	private final JBTN caic = new JBTN(MainLocale.PAGE, "caic");
	private final JBTN camm = new JBTN(MainLocale.PAGE, "camm");
	private final JBTN cama = new JBTN(MainLocale.PAGE, "cama");
	private final JBTN save = new JBTN(MainLocale.PAGE, "save");
	private final JBTN allf = new JBTN(MainLocale.PAGE, "allfile");
	private final JBTN auth = new JBTN(MainLocale.PAGE, "author");
	private final JBTN comp = new JBTN(MainLocale.PAGE, "compare");
	private final JBTN bckp = new JBTN(MainLocale.PAGE, "backup");
	private final JBTN logs = new JBTN(MainLocale.PAGE, "logs");

	private final JBTN refr = new JBTN(MainLocale.PAGE, "refrtips");
	private final JLabel tips = new JLabel();

	public MainPage() {
		super(null);

		ini();
	}

	@Override
    public JButton getBackButton() {
		return null;
	}

	@Override
	protected void renew() {
		Runtime.getRuntime().gc();
		curr.setEnabled(BattleInfoPage.current != null);
		refrTips();
		setMemo();
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(memo, x, y, 50, 30, 500, 50);
		set(sgifau, x, y, 50, 60, 800, 50);
		set(welcome, x, y, 50, 90, 500, 50);

		set(vuni, x, y, 600, 200, 200, 50);
		set(vene, x, y, 600, 300, 200, 50);
		set(veff, x, y, 600, 400, 200, 50);
		set(vcas, x, y, 600, 500, 200, 50);
		set(vbgr, x, y, 600, 600, 200, 50);
		set(vmsc, x, y, 600, 700, 200, 50);
		set(allf, x, y, 600, 800, 200, 50);
		set(tips, x, y, 600, 1050, 1200, 200);

		set(conf, x, y, 900, 200, 200, 50);
		set(save, x, y, 900, 300, 200, 50);
		set(bass, x, y, 900, 400, 200, 50);
		set(bckp, x, y, 900, 500, 200, 50);
		set(curr, x, y, 900, 600, 200, 50);
		set(auth, x, y, 900, 700, 200, 50);
		set(refr, x, y, 900, 800, 200, 50);

		set(vsta, x, y, 1200, 200, 200, 50);
		set(veif, x, y, 1200, 300, 200, 50);
		set(vuif, x, y, 1200, 400, 200, 50);
		set(comp, x, y, 1200, 500, 200, 50);
		set(pcus, x, y, 1200, 600, 200, 50);
		set(rply, x, y, 1200, 700, 200, 50);
		set(logs, x, y, 1200, 800, 200, 50);

		set(vdiy, x, y, 1500, 200, 200, 50);
		set(caic, x, y, 1500, 300, 200, 50);
		set(camm, x, y, 1500, 400, 200, 50);
		set(cama, x, y, 1500, 500, 200, 50);
		set(ui, x, y, 1500, 600, 200, 50);

	}

	private void addListeners() {
		vuni.setLnr(() -> new UnitViewPage(this));
		vene.setLnr(() -> new EnemyViewPage(this));
		vsta.setLnr(() -> new StageViewPage(this, MapColc.values()));
		vdiy.setLnr(() -> new DIYViewPage(this));
		conf.setLnr(() -> new ConfigPage(this));
		veff.setLnr(() -> new EffectViewPage(this));
		vcas.setLnr(() -> new CastleViewPage(this));
		vbgr.setLnr(() -> new BGViewPage(this));
		veif.setLnr(() -> new EnemyFindPage(this, true));
		vuif.setLnr(() -> new UnitFindPage(this, true));
		bass.setLnr(() -> new BasisPage(this));
		curr.setLnr(() -> BattleInfoPage.current);
		pcus.setLnr(() -> new PackEditPage(this));
		caic.setLnr(() -> new ImgCutEditPage(this));
		camm.setLnr(() -> new MaModelEditPage(this));
		cama.setLnr(() -> new MaAnimEditPage(this));
		save.setLnr((e) -> UIPlugin.P.writeData());
		vmsc.setLnr(() -> new MusicPage(this));
		rply.setLnr(() -> new RecdManagePage(this));
		allf.setLnr(() -> new ResourcePage(this));
		bckp.setLnr(() -> new BackupPage(this, true));
		logs.setLnr(() -> new LogPage(this));
		auth.setLnr(e -> {
			String author = Opts.read("Decide your author name");

			if (author == null)
				return;

			author = author.trim();

			for (PackData.UserPack p : UserProfile.getUserPacks()) {
				if (p.editable) {
					p.desc.author = author;
				}
			}

			MainBCU.author = author;

			if (!author.isEmpty()) {
				welcome.setVisible(true);
				welcome.setText("Welcome " + MainBCU.author + "!");
			} else {
				welcome.setVisible(false);
			}
		});
		comp.setLnr(() -> new ComparePage(this));
		refr.setLnr(c -> refrTips());
		ui.setLnr(()-> new UIPage(this));
	}

	private void ini() {
		add(vuni);
		vuni.setToolTipText(get(MainLocale.PAGE,"vunitip"));

		add(vene);
		vene.setToolTipText(get(MainLocale.PAGE,"venetip"));

		add(vsta);
		vsta.setToolTipText(get(MainLocale.PAGE,"vstatip"));

		add(vdiy);
		vdiy.setToolTipText(get(MainLocale.PAGE,"vdiytip"));

		add(conf);
		conf.setToolTipText(get(MainLocale.PAGE,"conftip"));

		add(veff);
		veff.setToolTipText(get(MainLocale.PAGE,"vefftip"));

		add(vcas);
		vcas.setToolTipText(get(MainLocale.PAGE,"vcastip"));

		add(vbgr);
		vbgr.setToolTipText(get(MainLocale.PAGE,"vbgrtip"));

		add(veif);
		veif.setToolTipText(get(MainLocale.PAGE,"veiftip"));

		add(vuif);
		vuif.setToolTipText(get(MainLocale.PAGE,"vuiftip"));

		add(vmsc);
		vmsc.setToolTipText(get(MainLocale.PAGE,"vmsctip"));

		add(bass);
		bass.setToolTipText(get(MainLocale.PAGE,"basstip"));

		add(memo);
		add(curr);
		curr.setToolTipText(get(MainLocale.PAGE,"currtip"));

		add(pcus);
		pcus.setToolTipText(get(MainLocale.PAGE,"pcustip"));

		add(caic);
		caic.setToolTipText(get(MainLocale.PAGE,"caictip"));

		add(camm);
		camm.setToolTipText(get(MainLocale.PAGE,"cammtip"));

		add(cama);
		cama.setToolTipText(get(MainLocale.PAGE,"camatip"));

		add(save);
		save.setToolTipText(get(MainLocale.PAGE,"savetip"));

		add(sgifau);
		add(welcome);
		add(rply);
		rply.setToolTipText(get(MainLocale.PAGE,"rplytip"));

		add(allf);
		allf.setToolTipText(get(MainLocale.PAGE,"allftip"));

		add(auth);
		auth.setToolTipText(get(MainLocale.PAGE,"authtip"));

		add(comp);
		comp.setToolTipText(get(MainLocale.PAGE,"comptip"));

		add(bckp);
		bckp.setToolTipText(get(MainLocale.PAGE,"bckptip"));

		add(logs);
		logs.setToolTipText(""); // TODO: Logs tool tip

		add(refr);
		add(tips);

		add(ui);
		ui.setToolTipText(get(MainLocale.PAGE,"ui-tip"));

		welcome.setVisible(!MainBCU.author.isEmpty());

		setMemo();
		refrTips();
		addListeners();
	}

	private void setMemo() {
		long f = Runtime.getRuntime().freeMemory();
		long t = Runtime.getRuntime().totalMemory();
		long m = Runtime.getRuntime().maxMemory();
		double per = 100.0 * (t - f) / m;
		memo.setText("memory used: " + (t - f >> 20) + " MB / " + (m >> 20) + " MB, " + (int) per + "%");
	}

	private void refrTips() {
		String[] ALLTIPS = get(MainLocale.PAGE, "tip", 19);
		tips.setText("<html>" + ALLTIPS[(int)(Math.random() * ALLTIPS.length)] + "</html>");
	}

	private final JBTN ui = new JBTN(0, "UI Plugin");
}