package com.filestack.android.demo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.filestack.Config;
import com.filestack.android.FsActivity;
import com.filestack.android.FsConstants;
import com.filestack.android.Selection;
import com.filestack.android.demo.models.Action;
import com.filestack.android.demo.models.User;
import com.filestack.android.demo.utility.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_FILESTACK = RESULT_FIRST_USER;
    private static final String TAG = "MainActivity";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView maction_name;
    public String action_name;
    public String action_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            IntentFilter intentFilter = new IntentFilter(FsConstants.BROADCAST_UPLOAD);
            UploadStatusReceiver receiver = new UploadStatusReceiver();
            LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
        }

        Log.d(TAG, "onCreate: started.");
        maction_name = (TextView) findViewById(R.id.maction_name);

        setupFirebaseAuth();

        initImageLoader();

        getActionData();
    }


    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                // ...
            }
        };
    }

    private void initImageLoader(){
      //  UniversalImageLoader imageLoader = new UniversalImageLoader(SignedInActivity.this);
        // ImageLoader.getInstance().init(imageLoader.getConfig());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Locale locale = Locale.getDefault();

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FILESTACK && resultCode == RESULT_OK) {
            Log.i(TAG, "received filestack selections");
            String key = FsConstants.EXTRA_SELECTION_LIST;
            ArrayList<Selection> selections = data.getParcelableArrayListExtra(key);
            for (int i = 0; i < selections.size(); i++) {
                Selection selection = selections.get(i);
                String msg = String.format(locale, "selection %d: %s", i, selection.getName());
                Log.i(TAG, msg);
            }
        }
    }

    public void openFilestack(View view) {
        Intent intent = new Intent(this, FsActivity.class);
        Config config = new Config("AJMXMSUQuJ89CLYEt6wjxz");
        intent.putExtra(FsConstants.EXTRA_CONFIG, config);
        intent.putExtra(FsConstants.EXTRA_AUTO_UPLOAD, true);
        startActivityForResult(intent, REQUEST_FILESTACK);
    }

    public void openFileBrowser(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 0);
    }
    public void openQuizProject(View view) {


        Intent intent = new Intent(MainActivity.this, Navigation_Activity.class);
        startActivity(intent);


    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    private void getActionData(){
        Log.d(TAG, "getActionsData: getting the actions information");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

//        Query query2 = reference.child(getString(R.string.dbnode_users))
//                .orderByChild(getString(R.string.field_user_id))
//                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());


        Query query7 = reference.child(getString(R.string.dbnode_actions));
        query7.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //this loop will return a single result
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: (QUERY METHOD 1) found action: "
                            + singleSnapshot.getValue(Action.class).toString());
                    Action action = singleSnapshot.getValue(Action.class);
                    maction_name.setText("Next Action : "+ action.getAction_name());
                    action_name = action.getAction_name();
                    action_id = action.getAction_id();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "action read failed");
            }
        });
    }
}






/**
 * Use the Location Layer plugin to easily add a device location "puck" to a Mapbox map.
 */
