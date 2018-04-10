package com.filestack.android.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.filestack.android.demo.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import com.filestack.android.demo.utility.UniversalImageLoader;

public class SignedInActivity extends AppCompatActivity {

    private static final String TAG = "SignedInActivity";

    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView mScore;
    private TextView mLevelname;
    private TextView mLevel;
    // widgets and UI References



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signedin);
        Log.d(TAG, "onCreate: started.");
        mScore = (TextView) findViewById(R.id.score);
        mLevelname = (TextView) findViewById(R.id.level_circle_text2);
        mLevel = (TextView) findViewById(R.id.progress_circle_text1);

        setupFirebaseAuth();

        initImageLoader();

        getUserScoreData();
    }


    private void init() {
        getUserScoreData();
    }

    /**
     * init universal image loader
     */
    private void initImageLoader(){
        UniversalImageLoader imageLoader = new UniversalImageLoader(SignedInActivity.this);
        ImageLoader.getInstance().init(imageLoader.getConfig());
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        checkAuthenticationState();
    }

    private void checkAuthenticationState(){
        Log.d(TAG, "checkAuthenticationState: checking authentication state.");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            Log.d(TAG, "checkAuthenticationState: user is null, navigating back to login screen.");

            Intent intent = new Intent(SignedInActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else{
            Log.d(TAG, "checkAuthenticationState: user is authenticated.");
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.optionSignOut:
                signOut();
                return true;
            case R.id.optionAccountSettings:
                intent = new Intent(SignedInActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.optionChat:
                intent = new Intent(SignedInActivity.this, ChatActivity.class);
                startActivity(intent);
                return true;

            case R.id.optionOnCampus:
                intent = new Intent(SignedInActivity.this, OnCampActivity.class);
                startActivity(intent);
                return true;

            case R.id.optionYourLocation:
                intent = new Intent(SignedInActivity.this, MapActivity.class);
                startActivity(intent);
                return true;

            case R.id.optionOffCampus:
                intent = new Intent(SignedInActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Sign out the current user
     */
    private void signOut(){
        Log.d(TAG, "signOut: signing out");
        FirebaseAuth.getInstance().signOut();
    }

    /*
            ----------------------------- Firebase setup ---------------------------------
         */
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
                    Intent intent = new Intent(SignedInActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    private void getUserScoreData() {
        Log.d(TAG, "getUserScoreData: getting the user's Score information");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query1 = reference.child(getString(R.string.dbnode_users))
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
                    mScore.setText("Your Score : "+user.getScore());
                     int Remaingpoints;
                    Remaingpoints = 500 - Integer.parseInt(user.getScore()) ;
                    if (Integer.parseInt(user.getScore())  <= 500 )    {
                        mLevelname.setText("Reach "+Remaingpoints+ " points to go to level 2");
                        mLevel.setText("1 of 10");
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    public void offcampusbuttonclick(View view) {
        Intent intent;
        Toast.makeText(this, "off campus", Toast.LENGTH_LONG).show();
        intent = new Intent(SignedInActivity.this, MainActivity.class);
        startActivity(intent);
       // return true;
    }

    public void oncampusbuttonclick(View view) {
        Intent intent;
        Toast.makeText(this, "on campus", Toast.LENGTH_LONG).show();
        intent = new Intent(SignedInActivity.this, OnCampusActivity.class);
        startActivity(intent);
        // return true;
    }


}
