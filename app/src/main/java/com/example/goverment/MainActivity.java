package com.example.goverment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {


    private final List<Official> officialList = new ArrayList<>();  // Main content is here
    private RecyclerView recyclerView; // Layout's recyclerview
    private Official official;
    private static final String TAG = "MainActivity";
    private static final int OTHER_CODE = 123;
    private OfficialAdapter nAdapter;
    private int pos;
    private SwipeRefreshLayout swiper;

    private static int MY_LOCATION_REQUEST_CODE_ID = 111;
    private LocationManager locationManager;
    private Criteria criteria;
    private String adminarea;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);

        // Data to recyclerview adapter
        nAdapter = new OfficialAdapter(officialList, this);
        recyclerView.setAdapter(nAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        //Make some data - not always needed - just used to fill list
/*
        for (int i = 0; i < 2; i++) {
            officialList.add(new Official("official" + i, "nothing to do"));
        }*/




        //clearFile();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        criteria = new Criteria();

        // use gps for location
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        // use network for location
        //criteria.setPowerRequirement(Criteria.POWER_LOW);
        //criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);

        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MY_LOCATION_REQUEST_CODE_ID);
        } else {
            try {
                setLocation();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }





    }

    private void setLocation() throws IOException {

        if(doNetCheck()){
            String bestProvider = locationManager.getBestProvider(criteria, true);

            Location currentLocation = null;
            if (bestProvider != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                currentLocation = locationManager.getLastKnownLocation(bestProvider);
            }
            if (currentLocation != null) {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());

                List<Address> addresses;
                double lat = currentLocation.getLatitude();
                double lon = currentLocation.getLongitude();
                addresses = geocoder.getFromLocation(lat, lon, 10);
                displayAddresses(addresses);

            } else {
                ((TextView) findViewById(R.id.locText)).setText("no_locs");
            }
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("");
            builder.setTitle("No Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            ((TextView) findViewById(R.id.locText)).setText("no_locs");

        }


    }


    private void displayAddresses(List<Address> addresses) {
        StringBuilder sb = new StringBuilder();
        if (addresses.size() == 0) {
            ((TextView) findViewById(R.id.locText)).setText("nothing_found");
            return;
        }

        Address ad = addresses.get(0);
        String a = String.format("%s, %s, %s, %s",
                (ad.getLocality() == null ? "" : ad.getLocality()),
                (ad.getAdminArea() == null ? "" : ad.getAdminArea()),
                (ad.getPostalCode() == null ? "" : ad.getPostalCode()),
                (ad.getCountryName() == null ? "" : ad.getCountryCode()));


        sb.append(a);
        if(ad.getPostalCode()!=null){
            adminarea=ad.getPostalCode();
        }else{
            adminarea=ad.getLocality();
        }

        doDownload(adminarea);

        ((TextView) findViewById(R.id.locText)).setText(sb.toString());

        nAdapter.notifyDataSetChanged();
    }

    private void doDownload(String adminarea){
        OfficialDataDownloadRunnable loaderTaskRunnable = new OfficialDataDownloadRunnable(this, adminarea);
        new Thread(loaderTaskRunnable).start();

    }


    public void clearOfficial(){
        officialList.clear();
    }
    public void addOfficial(Official official){
        officialList.add(official);
        nAdapter.notifyDataSetChanged();
    }
    public void recheckLocation(View v) throws IOException {
        Toast.makeText(this, "Rechecking Location", Toast.LENGTH_SHORT).show();
        setLocation();
    }



    @Override
    public void onClick(View v) {  // click listener called by ViewHolder clicks
        pos = recyclerView.getChildLayoutPosition(v);
        official = officialList.get(pos);
        Intent intent = new Intent(this, OfficialActivity.class);
        intent.putExtra("name", official);
        //intent.putExtra("pos",pos);
        startActivity(intent);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.aboutitem:
                Intent intent1 = new Intent(this, About.class);
                startActivity(intent1);
                return true;
            case R.id.addnote:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                // Create an edittext and set it to be the builder's view
                final EditText et = new EditText(this);
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                et.setGravity(Gravity.CENTER_HORIZONTAL);
                builder.setView(et);

                builder.setIcon(R.drawable.icon1);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        try {
                            List<Address> ad=geocoder.getFromLocationName(String.valueOf(et.getText()),10);
                            displayAddresses(ad);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                });
                builder.setNegativeButton("Cancel" , new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show();
                        //tv1.setText(getString(R.string.no_way));
                    }
                });

                builder.setMessage("Enter a City, State or a zipcode:");


                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private boolean doNetCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


}