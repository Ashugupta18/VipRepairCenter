package com.vip.android.viptechnician;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AttendanceActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {

    Button buttonPresent,buttonWeekOff;
    private static GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Geocoder geocoder;
    List<Address> addresses;
    String Latitude, Longitude;
    String TotalAddress;
    Location mLastLocation;
    String userId,DateTime;
    String inOutFlag="1";
    String ipAddresss= "10.236.125.14";
    SharedPreferences StarSharedPreference;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    public static final int INTERVAL = 1000 * 1;
    private static final long FASTEST_INTERVAL= 1000 * 1;
    double lat, lon;
    AlertDialog networkAlertDialog;
    ConnectivityManager connectivityManager;
    NetworkInfo activeNetworkInfo;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    String isPunchOut,week_off="";
    String version="",id="",version_type="";
   /* private static String SOAP_ACTION = "http://tempuri.org/setAttendance";

    private static String NAMESPACE = "http://tempuri.org/";*/

    private static String SOAP_ACTION = "http://microsoft.com/webservices/setAttendance";

    private static String NAMESPACE = "http://microsoft.com/webservices";

    private static String METHOD_NAME = "setAttendance";

    private static String SOAP_ACTION_VERSION = "http://microsoft.com/webservices/getversionDetails";

    private static String METHOD_NAME_VERSION = "getversionDetails";

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    String user_ip_address="127.0.0.1";
    String user_name,user_password;
    //private static String URL = "http://14.142.218.42/LMS/service.asmx";
    //private static String URL = "http://14.142.218.45/lms/service.asmx";
    String URL;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    AlertDialog permissionAlertDialog;
    TextView waitforlocation;

    AlertDialog  loginAlertDialog;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        setContentView(R.layout.activity_attendance);

        buttonPresent= (Button) findViewById(R.id.button_present);
        waitforlocation= (TextView) findViewById(R.id.waitforlocation);

        buttonWeekOff= (Button) findViewById(R.id.button_week_off);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>()
        {
            @Override
            public void onResult(LocationSettingsResult result)
            {
                final Status status = result.getStatus();
                switch (status.getStatusCode())
                {
                    case LocationSettingsStatusCodes.SUCCESS:
                        System.out.println( "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        System.out.println("Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try
                        {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(AttendanceActivity.this, REQUEST_CHECK_SETTINGS);
                        }
                        catch (IntentSender.SendIntentException e)
                        {
                            System.out.println("PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        System.out.println("Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });

        if(checkAndRequestPermissions())
        {
            // carry on the normal flow, as the case of  permissions  granted.
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isNetworkAvailable())
        {
            showNetworkDialogue();
        }

        if (mGoogleApiClient.isConnected())
        {
            startLocationUpdates();
            Log.d("VIPSE", "Location update resumed .....................");
        }

        if((Latitude==null) && (Longitude==null))
        {
            waitforlocation.setVisibility(TextView.VISIBLE);
        }
        else
        {
            waitforlocation.setVisibility(TextView.GONE);
        }

    }


    private boolean isNetworkAvailable()
    {
        connectivityManager = (ConnectivityManager)getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showNetworkDialogue(){

        AlertDialog.Builder networkAlertDialogBuilder = new AlertDialog.Builder(
                AttendanceActivity.this);

        // set title
        networkAlertDialogBuilder.setTitle("Network Status");

        // set dialog message
        networkAlertDialogBuilder
                .setMessage("Please check your internet connection")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });


        // create alert dialog
        networkAlertDialog = networkAlertDialogBuilder.create();

        if (!networkAlertDialog.isShowing())// show it
            networkAlertDialog.show();
    }
    

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("VIPSE onStart CALLED");
        active=true;
        mGoogleApiClient.connect();
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d("VIPSE", "onStop fired ..............");
        active=false;
        mGoogleApiClient.disconnect();
        Log.d("VIPSE STOPPED", "isConnected ...............: " + mGoogleApiClient.isConnected());
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        System.out.println("VIPSE onLocationChanged CALLED");

        /*if(location.getAccuracy()<100)
        {*/
        if((location.getLatitude()!=0) && (location.getLongitude()!=0))
        {
            Latitude = String.valueOf(location.getLatitude());
            Longitude = String.valueOf(location.getLongitude());
            lat = location.getLatitude();
            lon = location.getLongitude();
            updateLocation();
            System.out.println("Latitude"+ Latitude);
            System.out.println("Longitude"+ Longitude);

            waitforlocation.setVisibility(TextView.GONE);

        }
        /*}*/
    }

    private  boolean checkAndRequestPermissions()
    {

        int finelocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);


        List<String> listPermissionsNeeded = new ArrayList<>();

        if (finelocationPermission != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);

            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("permission", "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions

                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0)
                {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("permission", "location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d("permission", "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if ( ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.ACCESS_FINE_LOCATION))
                        {

                            AlertDialog.Builder permissionAlertDialogBuilder = new AlertDialog.Builder(this);

                            // set title
                            permissionAlertDialogBuilder.setTitle("Permissions Needed");

                            // set dialog message
                            permissionAlertDialogBuilder
                                    .setMessage("Location permission required to mark attendance")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            checkAndRequestPermissions();
                                            dialog.cancel();

                                        }
                                    }).
                                    setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            finish();
                                        }
                                    });


                            // create alert dialog
                            permissionAlertDialog = permissionAlertDialogBuilder.create();

                            if (!permissionAlertDialog.isShowing())// show it
                                permissionAlertDialog.show();


                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }


    }


    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d("VIPSE LOC UPDATES", "Location update started ..............: ");
    }

    private void updateLocation() {

        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses != null && addresses.size() > 0) {

                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                // String country = addresses.get(0).getCountryName();
                //String postalCode = addresses.get(0).getPostalCode();
                //String knownName = addresses.get(0).getFeatureName();
                TotalAddress = address + " , " + '\n' + city + " , " + '\n' + state + " . ";
                //textFullAddress.setText(TotalAddress);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
