package page.anim;

import common.CommonStatic.EditLink;
import common.util.anim.AnimCE;
import io.BCUWriter;
import page.JBTN;
import page.Page;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class EditHead extends Page implements EditLink {

	private static final long serialVersionUID = 1L;

	private final JBTN undo = new JBTN(0, "undo");
	private final JBTN redo = new JBTN(0, "redo");
	private final JBTN save = new JBTN(0, "save");
	private final JBTN view = new JBTN(0, "vdiy");
	private final JBTN icut = new JBTN(0, "caic");
	private final JBTN mmdl = new JBTN(0, "camm");
	private final JBTN manm = new JBTN(0, "cama");

	private DIYViewPage p0;
	private ImgCutEditPage p1;
	private MaModelEditPage p2;
	private MaAnimEditPage p3;
	private int val;
	private Page cur;
	private boolean changing = false;

	protected AnimCE anim;

	public EditHead(Page p, int v) {
		super(p.getFront());
		val = v;
		cur = p;
		if (v == 0)
			p0 = (DIYViewPage) p;
		else if (v == 1)
			p1 = (ImgCutEditPage) p;
		else if (v == 2)
			p2 = (MaModelEditPage) p;
		else if (v == 3)
			p3 = (MaAnimEditPage) p;
		ini();
	}

	@Override
    public JButton getBackButton() {
		return null;
	}

	@Override
	public void review() {
		undo.setEnabled(anim != null && anim.history.size() > 1);
		redo.setEnabled(anim != null && !anim.redo.empty());
		cur.callBack("review");
		if (anim != null) {
			undo.setToolTipText(anim.getUndo());
			redo.setToolTipText(anim.getRedo());
		}
	}

	public void setAnim(AnimCE da) {
		if (changing)
			return;
		anim = da;
		if (anim != null)
			anim.link = this;
		review();
	}

	@Override
	protected void resized(int x, int y) {
		set(undo, x, y, 0, 0, 135, 50);
		set(redo, x, y, 140, 0, 135, 50);
		set(save, x, y, 350, 0, 200, 50);
		set(view, x, y, 625, 0, 200, 50);
		set(icut, x, y, 850, 0, 200, 50);
		set(mmdl, x, y, 1075, 0, 200, 50);
		set(manm, x, y, 1300, 0, 200, 50);
	}

	private void addListeners() {
		view.addActionListener(arg0 -> {
			if (val == 0)
				return;
			changing = true;
			cur.remove(EditHead.this);
			if (p0 == null)
				p0 = new DIYViewPage(getFront(), EditHead.this);
			changePanel(cur = p0);
			cur.add(EditHead.this);
			((AbEditPage) cur).setSelection(anim);
			val = 0;
			changing = false;
		});

		icut.addActionListener(arg0 -> {
			if (val == 1)
				return;
			changing = true;
			cur.remove(EditHead.this);
			if (p1 == null)
				p1 = new ImgCutEditPage(getFront(), EditHead.this);
			changePanel(cur = p1);
			cur.add(EditHead.this);
			((AbEditPage) cur).setSelection(anim);
			val = 1;
			changing = false;
		});

		mmdl.addActionListener(arg0 -> {
			if (val == 2)
				return;
			changing = true;
			cur.remove(EditHead.this);
			if (p2 == null)
				p2 = new MaModelEditPage(getFront(), EditHead.this);
			changePanel(cur = p2);
			cur.add(EditHead.this);
			((AbEditPage) cur).setSelection(anim);
			val = 2;
			changing = false;
		});

		manm.addActionListener(arg0 -> {
			if (val == 3)
				return;
			changing = true;
			cur.remove(EditHead.this);
			if (p3 == null)
				p3 = new MaAnimEditPage(getFront(), EditHead.this);
			cur = p3;
			cur.add(EditHead.this);
			((AbEditPage) cur).setSelection(anim);
			changePanel(cur);
			val = 3;
			changing = false;
		});

		save.setLnr((e) -> BCUWriter.writeData(false));

		undo.addActionListener(arg0 -> {
			anim.undo();
			review();
			((AbEditPage) cur).setSelection(anim);
		});

		redo.addActionListener(arg0 -> {
			anim.redo();
			review();
			((AbEditPage) cur).setSelection(anim);
		});
	}

	protected void hotkey(KeyEvent e) {
		if (anim != null && (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) > 0)
			if (e.getKeyCode() == KeyEvent.VK_Z && anim.history.size() > 1) {
				anim.undo();
				review();
				((AbEditPage) cur).setSelection(anim);
			} else if (e.getKeyCode() == KeyEvent.VK_Y && !anim.redo.empty()) {
				anim.redo();
				review();
				((AbEditPage) cur).setSelection(anim);
			}
	}

	private void ini() {
		add(view);
		add(icut);
		add(mmdl);
		add(manm);
		add(save);
		add(undo);
		add(redo);
		addListeners();
	}

}