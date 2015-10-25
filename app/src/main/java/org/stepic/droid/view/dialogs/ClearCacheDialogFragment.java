package org.stepic.droid.view.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import org.jetbrains.annotations.NotNull;
import org.stepic.droid.R;
import org.stepic.droid.base.MainApplication;
import org.stepic.droid.preferences.UserPreferences;
import org.stepic.droid.store.operations.DatabaseManager;
import org.stepic.droid.util.CleanerUtil;

import javax.inject.Inject;

public class ClearCacheDialogFragment extends DialogFragment {

    @Inject
    DatabaseManager mDatabaseManager;
    @Inject
    UserPreferences userPreferences;

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MainApplication.component().inject(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_clear_cache_dialog)
                .setMessage(R.string.clear_cache_dialog_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //// FIXME: 22.10.15 do it in background
                        CleanerUtil.CleanDirectory(userPreferences.getDownloadFolder());
                        mDatabaseManager.dropDatabase();
                    }
                })
                .setNegativeButton(R.string.no, null);

        return builder.create();
    }
}