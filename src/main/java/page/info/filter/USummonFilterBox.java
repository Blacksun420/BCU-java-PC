package page.info.filter;

import common.pack.PackData;
import common.pack.UserProfile;
import page.Page;

import javax.swing.*;

public class USummonFilterBox extends UnitFilterBox {

    private static final long serialVersionUID = 1L;

    public USummonFilterBox(Page p, PackData.UserPack pack) {
        super(p, false, pack);
    }

    @Override
    protected void ini() {
        super.ini();
        pks.setModel(new DefaultComboBoxModel<>(new PackData[]{null, UserProfile.getBCData(), pack}));
        multipacks = true;

        postIni();
    }

    @Override
    protected void postIni() {
        trait.list.addAll(pack.traits.getList());
        trait.setListData();
    }

    @Override
    protected boolean validatePack(PackData p) {
        return p instanceof PackData.DefPack || p == pack;
    }
}