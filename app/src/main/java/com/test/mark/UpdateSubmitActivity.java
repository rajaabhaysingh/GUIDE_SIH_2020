package com.test.mark;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.List;

public class UpdateSubmitActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final float DEFAULT_ZOOM = 16;

    private FrameLayout toolbarContainer;
    private Toolbar toolbar;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    private View mapView;

    private ImageButton currentLocationButton, cameraButton;
    private Button updateAndSave;

    private RippleBackground rippleBackground;

    private RecyclerView uploadedPictureRecyclerView;

    private UpdateSubmitPictureAdapter placesPictureAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_submit);


        toolbarContainer = (FrameLayout) findViewById(R.id.toolbar_container);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        //making status bar transparent 25B4B4B4
        // Check if the version of Android is Lollipop or higher
        if (Build.VERSION.SDK_INT >= 21) {

            // Set the status bar to dark-semi-transparent
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // Set paddingTop of toolbar to height of status bar.
            // Fixes status bar covers toolbar issue
            toolbarContainer.setPadding(0, getStatusBarHeight(), 0, 0);
            //toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentLocationButton = findViewById(R.id.current_location_image_button);
        rippleBackground=(RippleBackground)findViewById(R.id.content);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapView = mapFragment.getView();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(UpdateSubmitActivity.this);
        Places.initialize(UpdateSubmitActivity.this, getString(R.string.google_map_api));

        //INFLATING RECYCLER VIEW

        //Picture Recycler view
        uploadedPictureRecyclerView = findViewById(R.id.update_submit_image_recycler_view);

        LinearLayoutManager picturesLayoutManager = new LinearLayoutManager(this);
        picturesLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        uploadedPictureRecyclerView.setLayoutManager(picturesLayoutManager);

        final List<PlacesPictureModel> placesPictureModelList = new ArrayList<PlacesPictureModel>();
        placesPictureAdapter =  new UpdateSubmitPictureAdapter(placesPictureModelList);

        placesPictureModelList.add(new PlacesPictureModel(R.drawable.placeholder_png));
        placesPictureModelList.add(new PlacesPictureModel(R.drawable.placeholder_png));
        placesPictureModelList.add(new PlacesPictureModel(R.drawable.placeholder_png));
        placesPictureModelList.add(new PlacesPictureModel(R.drawable.placeholder_png));
        placesPictureModelList.add(new PlacesPictureModel(R.drawable.placeholder_png));
        placesPictureModelList.add(new PlacesPictureModel(R.drawable.placeholder_png));
        placesPictureModelList.add(new PlacesPictureModel(R.drawable.placeholder_png));
        placesPictureModelList.add(new PlacesPictureModel(R.drawable.placeholder_png));

        uploadedPictureRecyclerView.setAdapter(placesPictureAdapter);
        placesPictureAdapter.notifyDataSetChanged();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        //changing the default position of current location button
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null)
        {
            final View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            Resources r = this.getResources();
            int bottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, r.getDisplayMetrics());

            layoutParams.setMargins(0, 0, 0, bottom);

            locationButton.setVisibility(View.GONE);

            currentLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mMap != null)
                    {
                        if(locationButton != null)
                            locationButton.callOnClick();

                    }
                }
            });
        }


        //check if GPS is enabled or not before moving user to his location on map
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(200);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(UpdateSubmitActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(UpdateSubmitActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(UpdateSubmitActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException)
                {
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    try {
                        resolvableApiException.startResolutionForResult(UpdateSubmitActivity.this, 100);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });



        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                rippleBackground.startRippleAnimation();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rippleBackground.stopRippleAnimation();
                    }
                }, 800);
                return false;
            }
        });


    }


    //here we are trying to know whether user enabled the location in above steps or denied
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100)
        {
            if (resultCode == RESULT_OK)
            {
                getDeviceLocation();
            }
        }
    }

    //function to access current device location
    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful())
                        {
                            mLastKnownLocation = task.getResult();

                            //but again it may return last known location as null
                            //so

                            if (mLastKnownLocation != null)
                            {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                mMap.setMapType(mMap.MAP_TYPE_HYBRID);
                            }
                            else {
                                LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(500);
                                locationRequest.setFastestInterval(250);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);

                                        if (locationResult == null)
                                        {
                                            return;
                                        }

                                        mLastKnownLocation = locationResult.getLastLocation();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                        mMap.setMapType(mMap.MAP_TYPE_HYBRID);
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };

                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        }
                        else {
                            Toast.makeText(UpdateSubmitActivity.this, "Unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
