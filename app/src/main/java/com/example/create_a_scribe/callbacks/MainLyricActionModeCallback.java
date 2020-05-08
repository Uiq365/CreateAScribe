package com.example.create_a_scribe.callbacks;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.example.create_a_scribe.R;

/**
 * This method is used when the lyrics are long pressed and it goes into a multiSelect mode.
 * also works with the menu bar that appears and disappear on use of this class.
 * the shareItem and countItem are checked and if pressed the action is called
 */
public abstract class MainLyricActionModeCallback implements ActionMode.Callback {
    private ActionMode action;
    private MenuItem countItem;
    private MenuItem shareItem;

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        actionMode.getMenuInflater().inflate(R.menu.main__lyric_action_mode, menu);
        this.action = actionMode;
        this.countItem = menu.findItem(R.id.action_checked_count);
        this.shareItem = menu.findItem(R.id.action_share_lyric);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {

    }

    public void setCount(String checkedCount) {
        if (countItem != null)
            this.countItem.setTitle(checkedCount);
    }

    /**
     * if checked count > 1 hide shareItem else show it
     *
     * @param b :visible
     */
    public void changeShareItemVisible(boolean b) {
        shareItem.setVisible(b);
    }

    public ActionMode getAction() {
        return action;
    }
}
