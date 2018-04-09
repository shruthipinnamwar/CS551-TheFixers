package com.filestack.android.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.filestack.FileLink;
import com.filestack.android.FsConstants;
import com.filestack.android.Selection;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class UploadStatusReceiver extends BroadcastReceiver {
    private static final String TAG = "UploadStatusReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Locale locale = Locale.getDefault();
        String status = intent.getStringExtra(FsConstants.EXTRA_STATUS);
        Selection selection = intent.getParcelableExtra(FsConstants.EXTRA_SELECTION);
        FileLink fileLink = (FileLink) intent.getSerializableExtra(FsConstants.EXTRA_FILE_LINK);

        String name = selection.getName();
        String handle = fileLink != null ? fileLink.getHandle() : "n/a";
        String finalurl = "https://cdn.filestackcontent.com/" + handle;
        String msg = String.format(locale, "upload %s: %s (%s) ((%s))", status, name, handle ,finalurl);
        Log.i(TAG, msg);

                /*
                ------ Change URL -----
                 */
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

//            reference.child(getString(R.string.dbnode_users))
//                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                    .child(getString(R.string.field_name))
//                    .setValue(finalurl);


    }


}
