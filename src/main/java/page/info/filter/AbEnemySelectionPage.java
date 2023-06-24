package page.info.filter;

import common.pack.PackData.UserPack;
import common.util.unit.AbEnemy;
import main.Opts;
import page.Page;
import page.SupPage;
import page.info.edit.StageEditTable;

public class AbEnemySelectionPage extends EnemyFindPage implements SupPage<AbEnemy> {

    private static final long serialVersionUID = 1L;

    private final StageEditTable table;
    public final int index;

    public AbEnemySelectionPage(Page p, StageEditTable table, int index) {
        super(p, true);

        this.table = table;
        this.index = index;
    }

    public AbEnemySelectionPage(Page p, StageEditTable table, int index, UserPack pack) {
        super(p, true, pack);

        this.table = table;
        this.index = index;
    }

    @Override
    public AbEnemy getSelected() {
        int sel = elt.getSelectedRow();
        if (sel < 0)
            return null;
        return elt.list.get(sel);

    }

    protected void addListeners() {
        back.addActionListener(arg0 -> {
            String content;

            if(getSelected() != null)
                content = "Will you set this enemy to "+getSelected()+"?";
            else
                content = "Will you set this enemy to no enemy?";

            boolean res = Opts.conf(content);

            if(res) {
                table.updateAbEnemy(this);
            }

            changePanel(getFront());
        });

        show.addActionListener(arg0 -> {
            if (show.isSelected())
                add(efb);
            else
                remove(efb);
        });

        seabt.setLnr((b) -> {
            if (efb != null) {
                efb.name = seatf.getText();

                efb.callBack(null);
            }
        });

        seatf.addActionListener(e -> {
            if (efb != null) {
                efb.name = seatf.getText();

                efb.callBack(null);
            }
        });
    }

    protected void ini() {
        add(back);
        add(show);
        add(efb);
        add(jsp);
        add(seatf);
        add(seabt);
        show.setSelected(true);
        addListeners();
    }
}
