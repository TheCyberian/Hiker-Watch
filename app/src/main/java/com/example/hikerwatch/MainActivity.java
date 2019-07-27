package com.example.hikerwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager manager;
    LocationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            Location lastKnownLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null){
                updateLocationInfo(lastKnownLocation);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }

    protected void startListening(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        }
    }

    protected void updateLocationInfo(Location location){
        Log.i("Location: from function", location.toString());

        TextView latTextView = findViewById(R.id.LatTextView);
        TextView lngTextView = findViewById(R.id.LngTextView);
        TextView accuracyTextView = findViewById(R.id.AccuracyTextView);
        TextView altitudeTextView = findViewById(R.id.AltitudeTextView);
        TextView addresstextView = findViewById(R.id.AddressTextView);

        latTextView.setText("Latitude: "+location.getLatitude());
        lngTextView.setText("Longitude: "+location.getLongitude());
        accuracyTextView.setText("Accuracy: "+location.getAccuracy());
        altitudeTextView.setText("Altitude: "+location.getAltitude());

        String address = "Could not find the address. :/";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try{
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if(addressList != null && addressList.size() > 0){
                address = "Address:\n";
                if(addressList.get(0).getThoroughfare() != null){
                    address += addressList.get(0).getThoroughfare() + "\n";
                }
                if(addressList.get(0).getLocality() != null){
                    address += addressList.get(0).getLocality() + "\n";
                }
                if(addressList.get(0).getPostalCode() != null){
                    address += addressList.get(0).getPostalCode() + "\n";
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        addresstextView.setText(address);
    }
}