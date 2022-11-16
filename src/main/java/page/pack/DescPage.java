package page.pack;

import common.pack.PackData;
import common.pack.PackData.UserPack;
import page.Page;

public class DescPage extends Page {

    private static final long serialVersionUID = 1L;

    private final PackData.PackDesc desc;
    private final boolean editable;

    public DescPage(Page p, UserPack up) {
        super(p);
        desc = up.desc;
        editable = up.editable;
    }

    @Override
    protected void resized(int x, int y) {

    }
}
