package sandjentrance.com.sj.utils;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.edisonwang.ps.lib.PennStation;

import sandjentrance.com.sj.R;
import sandjentrance.com.sj.actions.ArchiveFileAction_.PsArchiveFileAction;
import sandjentrance.com.sj.actions.MoveFileAction_.PsMoveFileAction;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.ui.extras.FileClickInterface;

/**
 * Created by toidiu on 4/21/16.
 */
public class UtilsView {

    public static PopupMenu fileClickPopup(final View anchor, final FileObj fileObj, final FileClickInterface listener) {
        anchor.setSelected(true);
        PopupMenu menu = new PopupMenu(anchor.getContext(), anchor);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (listener != null) {

                    switch (menuItem.getItemId()) {
                        case R.id.share:
                            listener.shareClicked(fileObj);
                            return true;
                        case R.id.print:
                            listener.printClicked(fileObj);
                            return true;
                    }
                }
                return false;
            }
        });
        menu.inflate(R.menu.file_click_menu);
        menu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu popupMenu) {
                anchor.setSelected(false);
            }
        });
        menu.show();
        return menu;
    }

    public static PopupMenu fileLongClickPopup(final View anchor, final FileObj fileObj, final FileClickInterface listener) {
        anchor.setSelected(true);
        PopupMenu menu = new PopupMenu(anchor.getContext(), anchor);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (listener != null) {

                    switch (menuItem.getItemId()) {
                        case R.id.rename:
                            listener.renameLongClicked(fileObj);
                            return true;
                        case R.id.move:
                            listener.moveLongClicked(fileObj);
                            return true;
                    }
                }
                return false;
            }
        });
        menu.inflate(R.menu.file_long_click_menu);
        menu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu popupMenu) {
                anchor.setSelected(false);
            }
        });
        menu.show();
        return menu;
    }
}
