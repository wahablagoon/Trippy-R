package bl.taxi.rider;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import bl.taxi.rider.utils.InternetUtils;
import bl.taxi.rider.utils.LogUtils;
import bl.taxi.rider.utils.PermissionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int GOOGLE_DEFAULT_ZOOM = 15;
    /**
     * Constant used in the location settings dialog.
     */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    // Play services request code
    private static final int REQUEST_RESOLVE_ERROR = 33;
    /**
     * Flag indicating whether a permission is already requested or not
     */
    private static boolean mPermissionRequested = false;
    private static boolean mPermissionGranted = false;
    private static boolean mPlayServicesConnected = false;
    @BindView(R.id.drawer_menu)
    ImageView drawerMenu;
    @BindView(R.id.map_toolbar)
    Toolbar mapToolbar;
    @BindView(R.id.map_fragment_container)
    FrameLayout mapFragmentContainer;
    @BindView(R.id.myLocationButton)
    RelativeLayout myLocationButton;
    // Material Drawer
    Drawer drawer;
    AccountHeader headerResult;
    Location mCurrentLocation;
    private int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private boolean mResolvingError = false;
    private String TAG = "MapsActivity";
    private SettingsClient mSettingsClient;
    private boolean mSettingsAvailable = false;
    private boolean mExecutedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        mSettingsClient = LocationServices.getSettingsClient(this);

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                /*.withTextColor(ResourcesCompat.getColor(getResources(),R.color.otp_text,null))*/
                .withHeaderBackground(ResourcesCompat.getDrawable(getResources(), R.color.colorPrimary, null))
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(
                        new ProfileDrawerItem().withName("STR").withEmail("msg2thirumalai@gmail.com").withIcon(getResources().getDrawable(R.drawable.ic_account_circle))
                )
                /*.withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })*/
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withSelectedItem(-1)
                .addDrawerItems(
                        new SecondaryDrawerItem().withName(R.string.nav_book_ride).withIdentifier(1).withIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_local_taxi_black_24dp, null)),
                        new SecondaryDrawerItem().withName(R.string.nav_your_rides).withIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_access_time_black_24dp, null)),
                        new SecondaryDrawerItem().withName(R.string.nav_rate_card).withIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_local_ratecard, null)),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.nav_wallet).withIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_local_wallet_24dp, null)),
                        new SecondaryDrawerItem().withName(R.string.nav_payment).withIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_credit_card_black_24dp, null)),
                        new SecondaryDrawerItem().withName(R.string.nav_offers).withIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_local_offer, null)),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.nav_emergency).withIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_folder_shared_black_24dp, null)),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.nav_support).withIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_chat_bubble_outline_black_24dp, null))
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        return false;
                    }
                })
                .build();

        //Connect Googleclient Location API
        googleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();

        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlayServicesConnected = true;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            Log.d("PlayServiceresolving", connectionResult.getErrorMessage());
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                googleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(),
                    MapsActivity.this, REQUEST_RESOLVE_ERROR);
            //Toast.makeText(MapsActivity.this, R.string.google_api_connection_fail, Toast.LENGTH_LONG).show();
            mResolvingError = true;
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

        checkRequestPermission();
    }

    private void checkRequestPermission () {

        if (InternetUtils.isOnline(getApplicationContext())) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission to access the location is missing.
                if (!mPermissionRequested) {
                    mPermissionRequested = true;
                    PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                            Manifest.permission.ACCESS_FINE_LOCATION, getString(R.string.permission_rationale_location), true);
                }
            } else {
                mPermissionGranted = true;
                checkLocationSettings();
            }
        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Access to the location has been granted to the app.

            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            if (mLastLocation != null && mMap != null) {
                mCurrentLocation = mLastLocation;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(),
                                mLastLocation.getLongitude()),
                        GOOGLE_DEFAULT_ZOOM));
            }

            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000); //10 seconds
            mLocationRequest.setFastestInterval(5000); //5 seconds
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            // Avoid Duplicate Listeners
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION) && googleApiClient.isConnected()) {
            // Enable the my location layer if the permission has been granted.
            mPermissionGranted = true;
            checkLocationSettings();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            showMissingPermissionError();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case FragmentActivity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        mSettingsAvailable = true;
                        if (!mExecutedOnce) {
                            enableMyLocation();
                        }
                        mExecutedOnce = true;
                        break;
                    case FragmentActivity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        mSettingsAvailable = false;
                        break;
                }
                break;
            case REQUEST_RESOLVE_ERROR:
                switch (resultCode) {
                    case RESULT_OK:
                        googleApiClient.connect();
                }
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(getString(R.string.location_permission_denied), true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!InternetUtils.isOnline(getApplicationContext())) {

        }
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private void checkLocationSettings() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000); //10 seconds
        mLocationRequest.setFastestInterval(5000); //5 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest mLocationSettingsRequest = builder.build();

        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission
                        mSettingsAvailable = true;
                        if (!mExecutedOnce) {
                            enableMyLocation();
                        }
                        mExecutedOnce = true;
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(MapsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                mSettingsAvailable = false;
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Disconnect Googleclient Location API and remove Listeners for this Activity
        if (googleApiClient != null) {
            googleApiClient.unregisterConnectionCallbacks(this);
            googleApiClient.unregisterConnectionFailedListener(this);
        }

        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.drawer_menu)
    public void drawerMenu(View view) {
        if (!drawer.isDrawerOpen())
            drawer.openDrawer();
    }

    @OnClick(R.id.myLocationButton)
    public void onMyLocation(View view) {

        if (mCurrentLocation != null && mMap != null) {

            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)                              // Sets the center of the map to current location
                    .zoom(GOOGLE_DEFAULT_ZOOM)
                    .tilt(0)                                     // Sets the tilt of the camera to 0 degrees
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}