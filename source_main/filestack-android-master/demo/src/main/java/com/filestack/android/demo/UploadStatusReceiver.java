package com.filestack.android.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.filestack.FileLink;
import com.filestack.android.FsConstants;
import com.filestack.android.Selection;
import com.filestack.android.demo.models.Action;
import com.filestack.android.demo.models.User;
import com.filestack.android.demo.models.UserActions;
import com.filestack.android.internal.UploadService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class UploadStatusReceiver extends BroadcastReceiver {
    private static final String TAG = "UploadStatusReceiver";
        public String action_name;
        public String action_id;
        public Integer score;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Locale locale = Locale.getDefault();
        String status = intent.getStringExtra(FsConstants.EXTRA_STATUS);
        Selection selection = intent.getParcelableExtra(FsConstants.EXTRA_SELECTION);
        FileLink fileLink = (FileLink) intent.getSerializableExtra(FsConstants.EXTRA_FILE_LINK);

        String name = selection.getName();
        String handle = fileLink != null ? fileLink.getHandle() : "n/a";
        String finalurl = "https://cdn.filestackcontent.com/" + handle;
        String msg = String.format(locale, "upload %s: %s %s (%s) ((%s))", status, fileLink, name, handle ,finalurl);
        Log.i(TAG, msg);



                /*
                ------ Change URL -----
                 */
        setupFirebaseAuth();
     //   getActionData();


        Log.d(TAG, "getActionsData: getting the actions information");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


        Query query = reference.child("actions");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //this loop will return a single result
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: (QUERY METHOD 1) found action: "
                            + singleSnapshot.getValue(Action.class).toString());
                    Action action = singleSnapshot.getValue(Action.class);
                    //  maction_name.setText("Next Action : "+ action.getAction_name());
                    action_name = action.getAction_name();
                    action_id = action.getAction_id();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "action read failed");
            }
        });





        //        String actionid = reference
//                .child("actions")
//                .push().getKey();
        UserActions newaction = new UserActions();
        newaction.setAction_id(action_id);
        newaction.setAction_name(action_name);
        newaction.setImg_url(finalurl);
        newaction.setTimestamp(getTimestamp());

        reference
                .child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("UserActions")
                .child("action2")
                .setValue(newaction);

       // getUserScoreData();







        Log.d(TAG, "getUserScoreData: getting the user's Score information");
     //   DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query1 = reference.child("users")
                .orderByKey()
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());



        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //this loop will return a single result
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: (QUERY METHOD 1) found user: "
                            + singleSnapshot.getValue(User.class).toString());
                    User user = singleSnapshot.getValue(User.class);
                    //    mScore.setText("Your Score : "+user.getScore());
                    int Remaingpoints;
                    score = Integer.parseInt(user.getScore());
                    Remaingpoints = 500 - Integer.parseInt(user.getScore()) ;
                    if (Integer.parseInt(user.getScore())  <= 500 )    {
                        //  mLevelname.setText("Reach "+Remaingpoints+ "more points to go to level 2");
                        // mLevel.setText("1 of 10");
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







        score = score + 50;

        reference
                .child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("score")
                .setValue(score);



    }


    private void getUserScoreData() {
        Log.d(TAG, "getUserScoreData: getting the user's Score information");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query1 = reference.child("users")
                .orderByKey()
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());



        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //this loop will return a single result
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: (QUERY METHOD 1) found user: "
                            + singleSnapshot.getValue(User.class).toString());
                    User user = singleSnapshot.getValue(User.class);
                //    mScore.setText("Your Score : "+user.getScore());
                    int Remaingpoints;
                    score = Integer.parseInt(user.getScore());
                    Remaingpoints = 500 - Integer.parseInt(user.getScore()) ;
                    if (Integer.parseInt(user.getScore())  <= 500 )    {
                      //  mLevelname.setText("Reach "+Remaingpoints+ "more points to go to level 2");
                       // mLevel.setText("1 of 10");
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }

    private void getActionData(){
        Log.d(TAG, "getActionsData: getting the actions information");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

//        Query query2 = reference.child(getString(R.string.dbnode_users))
//                .orderByChild(getString(R.string.field_user_id))
//                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());


        Query query7 = reference.child("actions");
        query7.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //this loop will return a single result
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: (QUERY METHOD 1) found action: "
                            + singleSnapshot.getValue(Action.class).toString());
                    Action action = singleSnapshot.getValue(Action.class);
                  //  maction_name.setText("Next Action : "+ action.getAction_name());
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
//                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    finish();
                }
                // ...
            }
        };
    }


}
