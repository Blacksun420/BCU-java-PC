package page.view;

import common.CommonStatic;
import common.pack.UserProfile;
import common.util.anim.AnimI;
import page.Page;

import javax.swing.*;
import java.util.Collections;
import java.util.Vector;
import java.util.stream.Collectors;

public class EffectViewPage extends AbViewPage {

	private static final long serialVersionUID = 1L;

	private final JList<AnimI<?, ?>> jlu = new JList<>();
	private final JScrollPane jspu = new JScrollPane(jlu);

	public EffectViewPage(Page p) {
		super(p);

		Vector<AnimI<?, ?>> va = new Vector<>();
		Collections.addAll(va, CommonStatic.getBCAssets().effas.values());
		Collections.addAll(va, CommonStatic.getBCAssets().atks);
		va.addAll(UserProfile.getBCData().souls.getList().stream().map(s -> s.anim).collect(Collectors.toList()));
		va.addAll(CommonStatic.getBCAssets().demonSouls.stream().map(s -> s.anim).collect(Collectors.toList()));
		jlu.setListData(va);
		ini();
		resized(true);
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		if (larges.isSelected())
			set(jspu, x, y, 50, 800, 300, 400);
		else
			set(jspu, x, y, 50, 100, 300, 1100);
	}

	@Override
	protected void updateChoice() {
		AnimI<?, ?> u = jlu.getSelectedValue();
		if (u == null)
			return;
		setAnim(u);
	}

	private void addListeners() {
		defCopyListener();

		jlu.addListSelectionListener(arg0 -> {
			if (arg0.getValueIsAdjusting())
				return;
			updateChoice();
		});
	}

	private void ini() {
		preini();
		add(jspu);
		addListeners();

	}

}
