package com.example.ronaldfernandes.myminiproject;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.sip.SipAudioCall;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private GoogleMap mMap;
    public Location dest;
    public Location sou;
    public Ringtone r;
    public boolean Confirmed = false;
    private LocationManager locationManager;
    private LocationListener listener;
    private double latitude;
    private double longitude;
    public Button stop;
    public float RADIUS = 1000;
    private boolean firsttime = true;
    public TextView destination;
    public boolean searchclk = false;
    private String strAdd = "";
    public LatLng latLngDestiny;
    private Button confirm;


    private LatLng destiny;
    public LatLng latLngCurrent;
    private Vibrator v;

    Marker marker = null;
    private EditText searchview;
    SharedPreferences prefs = null;
    public Button changeradius;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        setContentView(R.layout.activity_maps);
        searchview = (EditText) findViewById(R.id.searchView1);
        destination = (TextView) findViewById(R.id.latlongLocation);
        destination.setText("Enter the desination in \nsearch location and confirm \n");
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        confirm = (Button) findViewById(R.id.button3);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        changeradius = (Button) findViewById(R.id.radius);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(Confirmed){

                    float distanceInMeters = dest.distanceTo(sou);
                    if(distanceInMeters<RADIUS){
                        stop = (Button) findViewById(R.id.stop);
                        stop.setVisibility(View.VISIBLE);
                        if(!r.isPlaying()) {

                            r.play();

                            long[] pattern = {0, 100, 1000};
                            v.vibrate(pattern, 0);

                        }

                        Toast.makeText(MapsActivity.this, "Wake Up Machha!", Toast.LENGTH_SHORT).show();
                    }
                }

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                if (firsttime) {
                    getCompleteAddressString(latitude, longitude);
                    AlertDialog alertDialog = new AlertDialog.Builder(
                            MapsActivity.this).create();

                    // Setting Dialog Title
                    alertDialog.setTitle("Your current location");

                    // Setting Dialog Message
                    alertDialog.setMessage(strAdd);

                    // Setting Icon to Dialog


                    // Setting OK Button
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            Toast.makeText(getApplicationContext(), "Source Set", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();
                    latLngCurrent = new LatLng(latitude, longitude);
                    sou = new Location("");
                    sou.setLatitude(latLngCurrent.latitude);
                    sou.setLongitude(latLngCurrent.longitude);
                    mMap.addMarker(new MarkerOptions().position(latLngCurrent));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngCurrent));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


                    firsttime = false;
                }


                //String s = ("\n " + location.getLongitude() + " " + location.getLatitude());
                //Toast.makeText(MapsActivity.this,s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();


    }
    public  void ChangeRadius(View v){
        RADIUS = 300;
        Toast.makeText(this, "Radius changed to "+RADIUS, Toast.LENGTH_SHORT).show();
    }

    public void onClick(View v) {
        searchclk = true;

        String g = searchview.getText().toString();

        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> addresses = null;

        try {
            // Getting a maximum of 3 Address that matches the input
            // text
            addresses = geocoder.getFromLocationName(g, 3);
            if (addresses != null && !addresses.equals(""))
                search(addresses);

        } catch (Exception e) {

        }

    }
    public void stop()
    {
        v.cancel();
        locationManager.removeUpdates(listener);
        locationManager = null;
    }
    public void WokeUp(View v)
    {
        r.stop();
        stop();




    }


    protected void search(List<Address> addresses) {
        changeradius.setVisibility(View.VISIBLE);

        Address address = (Address) addresses.get(0);
        longitude = address.getLongitude();
        latitude = address.getLatitude();
        latLngDestiny = new LatLng(address.getLatitude(), address.getLongitude());

        String addressText = String.format(
                "%s, %s",
                address.getMaxAddressLineIndex() > 0 ? address
                        .getAddressLine(0) : "", address.getCountryName());

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(latLngDestiny);
        markerOptions.title(addressText);

        mMap.clear();
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngDestiny));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


        getCompleteAddressString(address.getLatitude(), address.getLongitude());


    }


    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Toast.makeText(this, strReturnedAddress.toString(), Toast.LENGTH_SHORT).show();
                destination.setText(strAdd);
                if (searchclk) {
                    confirm.setVisibility(View.VISIBLE);
                }
            } else {
                Log.w("My Current loction", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current address", "Canont get Address!");
        }
        return strAdd;
    }


    void configure_button() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.

        locationManager.requestLocationUpdates("gps", 5000, 0, listener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng bengaluru = new LatLng(12.9716, 77.5946);
        mMap.addMarker(new MarkerOptions().position(bengaluru).title("Marker in Bengaluru"));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bengaluru));
    }

    @Override
    public void onMapClick(LatLng point) {
        marker = mMap.addMarker(new MarkerOptions().position(point));

/* This code will save your location coordinates in SharedPrefrence when you click on the map and later you use it  */
        prefs.edit().putString("Lat", String.valueOf(point.latitude)).commit();
        prefs.edit().putString("Lng", String.valueOf(point.longitude)).commit();
        Toast.makeText(this, "Map clicked [" + point.latitude + " / " + point.longitude + "]", Toast.LENGTH_SHORT).show();
        //Then pass LatLng to other activity
        getCompleteAddressString(point.latitude, point.longitude);

    }


    public void Confirm_Destination(View v) {
        Toast.makeText(this, "Sit back and relax!", Toast.LENGTH_LONG).show();
        destiny = latLngDestiny;
        dest = new Location("");
        dest.setLatitude(destiny.latitude);
        dest.setLongitude(destiny.longitude);
        Confirmed = true;

    }


}
