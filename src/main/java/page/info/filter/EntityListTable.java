package page.info.filter;

import page.Page;
import page.support.SortTable;

import java.awt.*;

public abstract class EntityListTable<T> extends SortTable<T> {

    protected final Page page;

    protected EntityListTable(Page p, String[] title) {
        super(title);
        page = p;
    }

    public abstract void clicked(Point p);
}
