package page.support;

import common.util.pack.Background;
import common.util.stage.Music;
import page.SupPage;
import page.info.edit.SwingEditor;

public interface EntSupInt {
    SupPage<Music> getMusicSup(SwingEditor.IdEditor<Music> edi);
    SupPage<Background> getBGSup(SwingEditor.IdEditor<Background> edi);
    SupPage<?> getEntitySup(SwingEditor.IdEditor<?> edi);
}
