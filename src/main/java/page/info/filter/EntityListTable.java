package page.info.filter;

import page.Page;
import page.support.SortTable;

import javax.swing.*;
import java.awt.*;

public abstract class EntityListTable<T> extends SortTable<T> {
    private static final long serialVersionUID = 1L;

    protected final Page page;

    protected EntityListTable(Page p, String[] title) {
        super(title);
        page = p;
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    public abstract void clicked(Point p);
}
