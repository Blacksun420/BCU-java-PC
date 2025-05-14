package page.info.edit;

import common.CommonStatic;
import common.pack.Context.ErrType;
import common.pack.Identifier;
import common.pack.IndexContainer;
import common.pack.SortedPackSet;
import common.util.Data;
import common.util.lang.Editors;
import common.util.lang.Editors.EditControl;
import common.util.lang.Editors.Editor;
import common.util.lang.Editors.EditorGroup;
import common.util.lang.Editors.EditorSupplier;
import common.util.lang.Formatter;
import common.util.lang.ProcLang;
import common.util.unit.EneRand;
import common.util.unit.Trait;
import page.*;
import page.info.filter.TraitList;
import utilpc.UtilPC;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.util.function.Consumer;

public abstract class SwingEditor extends Editor {

	public static class BoolEditor extends SwingEditor {

		public final JTG input;

		public BoolEditor(EditorGroup eg, Editors.EdiField field, String f, boolean edit) {
			super(eg, field, f, edit);
			input = new JTG(ProcLang.get().get(eg.proc).get(f));
			input.setLnr(this::edit);
		}

		@Override
		public void setVisible(boolean res) {
			input.setVisible(res);
		}

		@Override
		public boolean isInvisible() {
			return !input.isVisible();
		}

		@Override
		public void resize(int x, int y, int x0, int y0, int w0, int h0) {
			Page.set(input, x, y, x0, y0, w0, h0);
		}

		@Override
		public void setData() {
			field.setData(par.obj);
			input.setSelected(field.obj != null && field.getBoolean());
			input.setEnabled(edit && field.obj != null);
		}

		@Override
		public void add(Consumer<JComponent> con) {
			con.accept(input);
		}

		private void edit(ActionEvent fe) {
			field.set(input.isSelected());
			update();
		}
	}

	public static class EditCtrl implements EditorSupplier {

		private final boolean isEnemy;
		private final EntityEditPage table;

		public EditCtrl(boolean isEnemy, EntityEditPage table) {
			this.isEnemy = isEnemy;
			this.table = table;
		}

		@Override
		public Editor getEditor(EditControl<?> ctrl, EditorGroup group, String f, boolean edit) {
			try {
				Editors.EdiField field = ctrl.getField(f);
				Class<?> fc = field.getType();
				if (fc == int.class) {
					if (field.getRaw().getAnnotation(Data.Proc.ProcItem.BitMasked.class) != null && ProcLang.get().get(group.proc).get(f).getOptionValues() != null)
						return new BitMaskEditor(group, field, f, edit);
					return new IntEditor(group, field, f, edit);
				}
				if (fc == float.class || fc == double.class)
					return new DoubleEditor(group, field, f, edit);
				if (fc == boolean.class)
					return new BoolEditor(group, field, f, edit);
				if (fc == Identifier.class) {
					switch (group.proc) {
						case "THEME":
							if (f.equals("id"))
								return new IdEditor<>(group, field, f, table::getBGSup, edit);
							else
								return new IdEditor<>(group, field, f, table::getMusicSup, edit);
						case "SUMMON":
							return new IdEditor<>(group, field, f, table::getEntitySup, edit);
						case "SPIRIT":
							return new IdEditor<>(group, field, f, table::getUnitSup, edit);
					}
				}
				if (fc == Data.Proc.ProcID.class)
					return new PIDEditor(group, field, f, edit);
				if (Enum.class.isAssignableFrom(fc))
					return new EnumEditor(group, field, f, edit);
				if (fc == Data.Proc.class)
					return new ProcEditor(group, field, f, edit, this);
				if (fc == SortedPackSet.class)
					return new TraitEditor(group, field, f, edit, this);
				throw new Exception("unexpected class " + fc);
			} catch (Exception e) {
				CommonStatic.ctx.noticeErr(e, ErrType.ERROR, "failed to generate editor for " + group.proc + ":" + f);
			}
			return null;
		}

		@Override
		public void setEditorVisibility(Editor e, boolean b) {
			SwingEditor edi = (SwingEditor) e;
			edi.setVisible(b);
		}

		@Override
		public boolean isEnemy() {
			return isEnemy;
		}

	}

	public static class IdEditor<T extends IndexContainer.Indexable<?, T>> extends SwingEditor {

		private final PageSup<T> page;

		public final JBTN input;
		public final JL jl;

		public IdEditor(EditorGroup par, Editors.EdiField field, String f, PageSup<T> page, boolean edit) {
			super(par, field, f, edit);
			this.page = page;
			input = new JBTN(ProcLang.get().get(par.proc).get(f));
			jl = new JL("");
			input.setLnr(this::edit);
		}

		public final void callback(Identifier<T> id) {
			field.set(id);
			update();
		}

		@Override
		public void setVisible(boolean res) {
			input.setVisible(res);
			jl.setVisible(res);
		}

		@Override
		public boolean isInvisible() {
			return !input.isVisible();
		}

		@Override
		public void resize(int x, int y, int x0, int y0, int w0, int h0) {
			Page.set(input, x, y, x0, y0, 150, h0);
			Page.set(jl, x, y, x0 + 150, y0, w0 - 150, h0);
		}

		@Override
		public void add(Consumer<JComponent> con) {
			con.accept(input);
			con.accept(jl);
		}

		@Override
		protected void setData() {
			field.setData(par.obj);

			Object obj = field.get();

			if(obj instanceof Identifier<?>)
				if(((Identifier<?>) obj).cls == EneRand.class)
					jl.setText(((Identifier<?>) obj).get() + " [Random]");
				else
					jl.setText(String.valueOf(((Identifier<?>) obj).get()));
			else
				jl.setText(String.valueOf(field.get()));

			input.setEnabled(edit && field.obj != null);
		}

		private void edit(ActionEvent fe) {
			MainFrame.changePanel(page.get(this).getThisPage());
		}
	}

	public static class IntEditor extends SwingEditor {
		public final JL label;
		public final JTF input = new JTF();

		public IntEditor(EditorGroup eg, Editors.EdiField field, String f, boolean edit) {
			super(eg, field, f, edit);
			label = new JL(ProcLang.get().get(eg.proc).get(f));
			input.setLnr(this::edit);
		}

		@Override
		public void setVisible(boolean res) {
			label.setVisible(res);
			input.setVisible(res);
		}

		@Override
		public boolean isInvisible() {
			return !label.isVisible();
		}

		@Override
		public void resize(int x, int y, int x0, int y0, int w0, int h0) {
			Page.set(label, x, y, x0, y0, 150, h0);
			Page.set(input, x, y, x0 + 150, y0, w0 - 150, h0);
		}

		@Override
		public void setData() {
			field.setData(par.obj);
			if (field.obj == null)
				input.setText("");
			else
				input.setText(String.valueOf(field.getInt()));
			input.setEnabled(edit && field.obj != null);
		}

		@Override
		public void add(Consumer<JComponent> con) {
			con.accept(label);
			con.accept(input);
		}

		@SuppressWarnings("ConstantConditions")
		protected void edit(FocusEvent fe) {
			field.setInt(Data.ignore(() -> CommonStatic.parseIntN(input.getText())));
			update();
		}
	}

	public static class BitMaskEditor extends SwingEditor {

		public final JL label;
		public final JCB[] checks;
		private boolean setting = false;

		public BitMaskEditor(EditorGroup eg, Editors.EdiField field, String f, boolean edit) {
			super(eg, field, f, edit);
			label = new JL(ProcLang.get().get(eg.proc).get(f));
			String[] op = ProcLang.get().get(eg.proc).get(f).getOptionValues();
			checks = new JCB[op.length];
			for (int i = 0; i < checks.length; i++) {
				int I = i;
				checks[I] = new JCB(op[I]);
				checks[I].addActionListener(a -> {
					if (setting)
						return;
					field.set(field.getInt() ^ (1 << I));
					update();
				});
			}
		}

		@Override
		public void setVisible(boolean res) {
			label.setVisible(res);
			for (JCB check : checks)
				check.setVisible(res);
		}

		@Override
		public boolean isInvisible() {
			return !label.isVisible();
		}

		@Override
		public void resize(int x, int y, int x0, int y0, int w0, int h0) {
			int rh = h0 / ((int)Math.ceil(checks.length / 2.0) + 1);
			Page.set(label, x, y, x0, y0, w0, rh);
			for (int i = 0; i < checks.length; i++)
				Page.set(checks[i], x, y, x0 + ((i % 2) * (w0 / 2)), y0 + 50 * ((i/2)+1), i + 1 == checks.length && i % 2 == 0 ? w0 : w0 / 2, rh);
		}

		@Override
		public int getH() {
			return ((int)Math.ceil(checks.length / 2.0) + 1) * 50;
		}

		@Override
		public void setData() {
			setting = true;
			field.setData(par.obj);
			for (int i = 0; i < checks.length; i++) {
				checks[i].setSelected(field.obj != null && (field.getInt() & (1 << i)) != 0);
				checks[i].setEnabled(edit && field.obj != null);
			}
			setting = false;
		}

		@Override
		public void add(Consumer<JComponent> con) {
			con.accept(label);
            for (JCB check : checks)
				con.accept(check);
		}
	}

	public static class DoubleEditor extends SwingEditor {
		public final JL label;
		public final JTF input = new JTF();

		public DoubleEditor(EditorGroup eg, Editors.EdiField field, String f, boolean edit) {
			super(eg, field, f, edit);
			label = new JL(ProcLang.get().get(eg.proc).get(f));
			input.setLnr(this::edit);
		}

		@Override
		public void setVisible(boolean res) {
			label.setVisible(res);
			input.setVisible(res);
		}

		@Override
		public boolean isInvisible() {
			return !label.isVisible();
		}

		@Override
		public void resize(int x, int y, int x0, int y0, int w0, int h0) {
			Page.set(label, x, y, x0, y0, 150, h0);
			Page.set(input, x, y, x0 + 150, y0, w0 - 150, h0);
		}

		@Override
		public void setData() {
			field.setData(par.obj);
			if (field.obj == null)
				input.setText("");
			else if (field.getType() == float.class)
				input.setText(String.valueOf(field.getFloat()));
			else
				input.setText(String.valueOf(field.getDouble()));
			input.setEnabled(edit && field.obj != null);
		}

		@Override
		public void add(Consumer<JComponent> con) {
			con.accept(label);
			con.accept(input);
		}

		private void edit(FocusEvent fe) {
			if (field.getType() == float.class) {
				field.set(Data.ignore(() -> CommonStatic.parseFloatN(input.getText())));
			} else
				field.set(Data.ignore(() -> CommonStatic.parseDoubleN(input.getText())));
			update();
		}
	}

	public static class EnumEditor extends SwingEditor {

		protected final JL label;
		protected final JComboBox<Object> opts;
		private boolean setting = false;

		public EnumEditor(EditorGroup eg, Editors.EdiField field, String f, boolean edit) {
			super(eg, field, f, edit);
			label = new JL(ProcLang.get().get(eg.proc).get(f));
			String[] op = ProcLang.get().get(eg.proc).get(f).getOptionValues();
			Object[] consts = field.getType().getEnumConstants();
			opts = new JComboBox<>(op != null ? op : consts);
			opts.addActionListener(l -> {
				if (setting || opts.getSelectedIndex() == -1)
					return;
				field.set(consts[opts.getSelectedIndex()]);
				update();
			});
		}

		@Override
		public void setVisible(boolean res) {
			opts.setVisible(res);
			label.setVisible(res);
		}

		@Override
		public boolean isInvisible() {
			return !opts.isVisible();
		}

		@Override
		public void resize(int x, int y, int x0, int y0, int w0, int h0) {
			Page.set(label, x, y, x0, y0, 150, h0);
			Page.set(opts, x, y, x0 + 150, y0, w0 - 150, h0);
		}

		@Override
		public void setData() {
			setting = true;
			field.setData(par.obj);
			opts.setEnabled(edit && field.obj != null);
			if (field.get() != null)
				opts.setSelectedIndex(((Enum<?>)field.get()).ordinal());
			setting = false;
		}

		@Override
		public void add(Consumer<JComponent> con) {
			con.accept(label);
			con.accept(opts);
		}
	}

	public static class ProcEditor extends SwingEditor {

		private final EditCtrl ed;
		public final JBTN btn;
		private BlessPage edi;

		public ProcEditor(EditorGroup eg, Editors.EdiField field, String f, boolean edit, EditCtrl ec) {
			super(eg, field, f, edit);
			ed = ec;
			btn = new JBTN(ProcLang.get().get(eg.proc).get(f));
			btn.setLnr(e -> {
				if (edi == null) {
					edi = new BlessPage(ed.table, ed.isEnemy);
					edi.exitter = field::set;
				}
				Editors.def = false;
				MainFrame.changePanel(edi);
				edi.setData(field.get() == null ? Data.Proc.blank() : (Data.Proc)field.get());
			});
		}

		@Override
		public void setVisible(boolean res) {
			btn.setVisible(res);
		}

		@Override
		public boolean isInvisible() {
			return !btn.isVisible();
		}

		@Override
		public void resize(int x, int y, int x0, int y0, int w0, int h0) {
			Page.set(btn, x, y, x0, y0, w0, h0);
		}

		@Override
		public void setData() {
			field.setData(par.obj);
		}

		@Override
		public void add(Consumer<JComponent> con) {
			con.accept(btn);
		}
	}

	/** For ProcID objects */
	public static class PIDEditor extends IntEditor {

		public PIDEditor(EditorGroup eg, Editors.EdiField field, String f, boolean edit) {
			super(eg, field, f, edit);
		}

		@Override
		public void setData() {
			field.setData(par.obj);
			if (field.obj == null)
				input.setText("");
			else
				input.setText(field.get().toString());
			input.setEnabled(edit && field.obj != null);
		}

		@Override
		@SuppressWarnings("ConstantConditions")
		protected void edit(FocusEvent fe) {
			((Data.Proc.ProcID)field.get()).setData(CommonStatic.parseIntsN(input.getText()));
			update();
		}
	}

	public static class TraitEditor extends SwingEditor {

		private final TraitList traitList;
		private final JScrollPane tpane;
		public final JL label;
		boolean setting = true;

		public TraitEditor(EditorGroup eg, Editors.EdiField field, String f, boolean edit, EditCtrl ec) {
			super(eg, field, f, edit);
			traitList = new TraitList(edit);
			tpane = new JScrollPane(traitList);
			label = new JL(ProcLang.get().get(eg.proc).get(f));
			traitList.setup(ec.table.pack, ec.isEnemy);
			traitList.addListSelectionListener(arg0 -> {
				if (setting)
					return;
				SortedPackSet<Trait> lt = (SortedPackSet<Trait>)field.get();
				for (int i = 0; i < traitList.list.size(); i++)
					if (traitList.isSelectedIndex(i)) {
						lt.add(traitList.list.get(i));
					} else
						lt.remove(traitList.list.get(i));
				field.set(lt);
				update();
			});
		}

		@Override
		public void setVisible(boolean res) {
			tpane.setVisible(res);
			label.setVisible(res);
		}

		@Override
		public boolean isInvisible() {
			return !tpane.isVisible();
		}

		@Override
		public void resize(int x, int y, int x0, int y0, int w0, int h0) {
			Page.set(label, x, y, x0, y0, w0, 50);
			Page.set(tpane, x, y, x0, y0 + 50, w0, h0 - 50);
		}

		@Override
		public int getH() {
			return 350;
		}

		@Override
		public void setData() {
			setting = true;
			field.setData(par.obj);
			SortedPackSet<Trait> lt = (SortedPackSet<Trait>)field.get();
			for (int k = 0; k < traitList.list.size(); k++)
				if (lt.contains(traitList.list.get(k)))
					traitList.addSelectionInterval(k, k);
				else
					traitList.removeSelectionInterval(k, k);
			setting = false;
		}

		@Override
		public void add(Consumer<JComponent> con) {
			con.accept(tpane);
			con.accept(label);
		}
	}

	public interface PageSup<T extends IndexContainer.Indexable<?, T>> {

		SupPage<T> get(IdEditor<T> editor);

	}

	public static class SwingEG extends EditorGroup {

		public final JL jlm;

		public SwingEG(int ind, boolean edit, Runnable cb, Formatter.Context ctx) {
			super(Data.Proc.getName(ind), edit, cb);
			jlm = new JL(getItem(ctx));
			ImageIcon icon = UtilPC.getIcon(1, ind);
			jlm.setIcon(icon == null ? null : UtilPC.getScaledIcon(icon, UtilPC.iconSize, UtilPC.iconSize));
		}

		@Override
		public void setData(Data.Proc.ProcItem obj) {
			super.setData(obj);
			jlm.getLSC().update();
		}
	}

	public boolean edit;

	public SwingEditor(EditorGroup par, Editors.EdiField field, String f, boolean edit) {
		super(par, field, f);
		this.edit = edit;
	}

	public abstract void setVisible(boolean res);

	public abstract boolean isInvisible();

	public abstract void resize(int x, int y, int x0, int y0, int w0, int h0);

	public int getH() {
		return 50;
	}

	public abstract void add(Consumer<JComponent> con);

}