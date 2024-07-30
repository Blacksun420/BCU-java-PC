package page.info.filter;

import common.pack.PackData;
import common.pack.UserProfile;
import common.util.lang.MultiLangCont;
import common.util.lang.ProcLang;
import common.util.unit.AbEnemy;
import common.util.unit.EneRand;
import common.util.unit.Enemy;
import main.MainBCU;
import page.Page;
import utilpc.UtilPC;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static utilpc.Interpret.*;

public class EnemyFilterBox extends EntityFilterBox {

	private static final long serialVersionUID = 1L;

	protected final JList<String> rare = new JList<>(ERARE);
	private final AttList abis = new AttList(-1, EABIIND.length);
	private final JScrollPane jr = new JScrollPane(rare);
	private final JScrollPane jab = new JScrollPane(abis);

	protected EnemyFilterBox(Page p, boolean rand, PackData.UserPack pack) {
		super(p, pack, rand);

		ini();
		confirm();
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(orop[3], x, y, 0, 0, 200, 50);

		if (multipacks) {
			set(pks, x, y, 0, 50, 200, 50);
			set(jr, x, y, 0, 100, 200, 200);
		} else {
			set(pks, x, y, 0, 50, 200, 0);
			set(jr, x, y, 0, 50, 200, 250);
		}
		set(jab, x, y, 250, 50, 200, 1100);
	}

	@Override
	protected void confirm() {
		minDiff = MainBCU.searchTolerance;
		List<AbEnemy> ans = new ArrayList<>();
		for(PackData p : UserProfile.getAllPacks())
			if ((pks.getSelectedItem() == null || processOperator(3, p.equals(pks.getSelectedItem()))) && validatePack(p)) {
				List<Enemy> el = p.getEnemies(p instanceof PackData.UserPack && ((PackData.UserPack)p).editable);
				for (Enemy e : el)
					if (validateEnemy(e))
						ans.add(e);
				if (rand)
					for(EneRand rand : p.randEnemies.getList()) {
						int diff = UtilPC.damerauLevenshteinDistance(rand.name.toLowerCase(), name.toLowerCase());
						if(diff <= minDiff)
							for (Enemy en : rand.getPossible())
								if (ans.contains(en)) {
									ans.add(rand);
									minDiff = diff;
									break;
								}
					}
			}

		for (int i = 0; i < ans.size(); i++)
			if (ans.get(i) instanceof Enemy) {
				Enemy e = (Enemy)ans.get(i);
				String ename = MultiLangCont.getStatic().ENAME.getCont(e);
				if (ename == null)
					ename = e.names.toString();
				if (UtilPC.damerauLevenshteinDistance(ename.toLowerCase(), name.toLowerCase()) > minDiff)
					ans.remove(i--);
			} else if (UtilPC.damerauLevenshteinDistance(((EneRand) ans.get(i)).name.toLowerCase(), name.toLowerCase()) > minDiff)
				ans.remove(i--);

		getFront().callBack(ans);
	}

	protected boolean validateEnemy(Enemy e) {
		if (e.filter == 3)
			return false;
		String fname = MultiLangCont.getStatic().ENAME.getCont(e);
		if (fname == null)
			fname = e.names.toString();
		int diff = UtilPC.damerauLevenshteinDistance(fname.toLowerCase(), name.toLowerCase());
		if (diff > minDiff)
			return false;

		int a = e.de.getAbi();

		boolean b0 = unchangeable(3);
		for (int r : rare.getSelectedIndices()) {
			b0 = processOperator(3, isER(e, r));
			if (b0 != unchangeable(3))
				break;
		}
		boolean b1 = unchangeable(0);
		for (int i : trait.getSelectedIndices()) {
			b1 = processOperator(0, e.de.getTraits().contains(trait.list.get(i)));
			if (b1 != unchangeable(0))
				break;
		}
		boolean b2 = unchangeable(1);
		int len = EABIIND.length;
		for (int i : abis.getSelectedIndices()) {
			b2 = processOperator(1, i < len ? ((a >> EABIIND[i]) & 1) == 1 : e.de.getAllProc().getArr(EPROCIND[i - len]).exists());
			if (b2 != unchangeable(1))
				break;
		}
		if (b2 == unchangeable(1) && ((EnemyFindPage)getFront()).adv != null && !((EnemyFindPage)getFront()).adv.isBlank) {
			b2 = processOperator(1, ((EnemyFindPage)getFront()).adv.compare(e.de.getAllProc()));
			if (b2 != unchangeable(1) && !b2)
				return false;
		}

		boolean b3 = unchangeable(2);
		for (int i : atkt.getSelectedIndices()) {
			b3 = processOperator(2, isType(e.de, i));
			if (b3 != unchangeable(2))
				break;
		}

		b0 |= rare.getSelectedIndex() == -1;
		b1 |= trait.getSelectedIndex() == -1;
		b2 |= abis.getSelectedIndex() == -1;
		b3 |= atkt.getSelectedIndex() == -1;
		if (!b0 | !b1 | !b2 | !b3)
			return false; //Return early to not affect minDiff

		minDiff = diff;
		return true;
	}

	@Override
	protected void ini() {
		super.ini();

		va.addAll(Arrays.asList(EABI).subList(0, EABIIND.length));
		ProcLang proclang = ProcLang.get();
		for (byte pr : EPROCIND)
			va.add(proclang.get(pr).abbr_name);
		abis.setListData(va);
		abis.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		set(rare);
		set(abis);
		add(jr);
		add(jab);
		Vector<PackData> pkv = new Vector<>(UserProfile.getAllPacks().size() + 1);
		pkv.add(null);
		if (pack == null) {
			for (PackData p : UserProfile.getAllPacks())
				if (p.enemies.size() > 0 || (rand && p.randEnemies.size() > 0))
					pkv.add(p);
		} else {
			pkv.add(UserProfile.getBCData());
			if (pack.enemies.size() > 0 || (rand && pack.randEnemies.size() > 0))
				pkv.add(pack);

			for (String s : pack.desc.dependency) {
				PackData p = UserProfile.getUserPack(s);
				if (p.enemies.size() > 0 || (rand && p.randEnemies.size() > 0))
					pkv.add(UserProfile.getUserPack(s));
			}
		}
		multipacks = pkv.size() > 2;
		pks.setModel(new DefaultComboBoxModel<>(pkv));
		postIni();
	}
}