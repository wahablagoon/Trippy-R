package bl.taxi.rider;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
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
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import bl.taxi.rider.utils.InternetUtils;
import bl.taxi.rider.utils.PermissionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnMyLocationButtonClickListener {

    @BindView(R.id.drawer_menu)
    ImageView drawerMenu;

    @BindView(R.id.map_toolbar)
    Toolbar mapToolbar;

    @BindView(R.id.map_fragment_container)
    FrameLayout mapFragmentContainer;

    // Material Drawer
    Drawer drawer;
    AccountHeader headerResult;

    private static final int GOOGLE_DEFAULT_ZOOM = 15;
    /**
     * Flag indicating whether a permission is already requested or not
     */
    private static boolean mPermissionRequested = false;
    Location mCurrentLocation;
    int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(
                        new ProfileDrawerItem().withName("Mike Penz").withTextColor(getResources().getColor(R.color.colorPrimary)).withIcon(getResources().getDrawable(R.drawable.ic_account_circle))
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
                        new SecondaryDrawerItem().withName(R.string.nav_book_ride).withIdentifier(1).withIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_local_taxi_black_24dp,null)),
                        new SecondaryDrawerItem().withName(R.string.nav_your_rides).withIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_access_time_black_24dp,null)),
                        new SecondaryDrawerItem().withName(R.string.nav_rate_card).withIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_local_ratecard,null)),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.nav_wallet).withIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_local_wallet_24dp,null)),
                        new SecondaryDrawerItem().withName(R.string.nav_payment).withIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_credit_card_black_24dp,null)),
                        new SecondaryDrawerItem().withName(R.string.nav_offers).withIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_local_offer,null)),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.nav_emergency).withIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_folder_shared_black_24dp,null)),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.nav_support).withIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_chat_bubble_outline_black_24dp,null))
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return true;

        }
        return false;
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

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

    @OnClick(R.id.drawer_menu)
    public void drawerMenu(View view) {
        if (!drawer.isDrawerOpen())
            drawer.openDrawer();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}