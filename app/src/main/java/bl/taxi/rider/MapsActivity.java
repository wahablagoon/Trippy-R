package bl.taxi.rider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import bl.taxi.rider.utils.InternetUtils;
import bl.taxi.rider.utils.PermissionUtils;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnMyLocationButtonClickListener {

    private static final int GOOGLE_DEFAULT_ZOOM = 15;
    /**
     * Flag indicating whether a permission is already requested or not
     */
    private static boolean mPermissionRequested = false;
    Location mCurrentLocation;
    int LOCATION_PERMISSION_REQUEST_CODE = 1;
    ImageView drawer_menu;
    Drawer drawer;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.mipmap.splash_booking)
                .addProfiles(
                        new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com").withIcon(getResources().getDrawable(R.drawable.ic_account_circle))
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
                /*.addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(1).withName(R.string.drawer_item_home);,
                        new DividerDrawerItem(),
                        item2,
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                    }
                })*/
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
        Toast.makeText(MapsActivity.this, R.string.google_api_connection_fail, Toast.LENGTH_LONG).show();
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

        if (!mMap.getUiSettings().isMyLocationButtonEnabled() & (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            enableMyLocation();
        }

        mMap.setOnMyLocationButtonClickListener(this);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            System.out.print("hello granted");
            // Access to the location has been granted to the app.
            if (mMap != null) {

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

                if (mLastLocation != null) {
                    mCurrentLocation = mLastLocation;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(),
                                    mLastLocation.getLongitude()),
                            GOOGLE_DEFAULT_ZOOM));
                }

                LocationRequest mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(5000); //5 seconds
                mLocationRequest.setFastestInterval(3000); //3 seconds
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

                // Avoid Duplicate Listeners
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
            }

        } else {

            // Permission to access the location is missing.
            if (!mPermissionRequested) {
                mPermissionRequested = true;
                PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                        Manifest.permission.ACCESS_FINE_LOCATION, getString(R.string.permission_rationale_location), true);
            }
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
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            showMissingPermissionError();
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

        if (InternetUtils.isOnline(getApplicationContext())) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission to access the location is missing.
                if (!mPermissionRequested) {
                    mPermissionRequested = true;
                    PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                            Manifest.permission.ACCESS_FINE_LOCATION, getString(R.string.permission_rationale_location), true);
                } else if (InternetUtils.isOnline(getApplicationContext())) {
                    mMap.setMyLocationEnabled(false);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Disconnect Googleclient Location API and remove Listeners for this Activity
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

        if (googleApiClient != null && googleApiClient.isConnected())
            googleApiClient.disconnect();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    public void drawerMenu(View view) {
        if (!drawer.isDrawerOpen())
            drawer.openDrawer();
    }
}