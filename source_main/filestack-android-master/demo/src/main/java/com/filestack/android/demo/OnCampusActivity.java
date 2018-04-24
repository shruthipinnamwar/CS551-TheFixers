package com.filestack.android.demo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import android.os.Bundle;
import com.filestack.android.demo.R;

public class OnCampusActivity extends AppCompatActivity{
//public class OnCampusActivity {
 static final int REQUEST_LOCATION = 1;
 LocationManager locationManager;


    protected void OnCreate(Bundle savedInstanceState){
     super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_oncampus);
     locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
     getLocation();

 }

    public void getlocations(View view) {
        Intent intent;
       // Toast.makeText(this, "on campus", Toast.LENGTH_LONG).show();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        getLocation();
        // return true;
    }
    public void getLocation(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }else{
            Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            if(location != null){
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Toast.makeText(this, " Latitude :  "+latitude, Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, " latitude not found ", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_LOCATION :
                getLocation();
                break;
        }
    }

}