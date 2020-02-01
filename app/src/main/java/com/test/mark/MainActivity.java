package com.test.mark;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private static String TAG = "mainActivityDebug";

    FrameLayout toolbar;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;

    private RippleBackground rippleBackground;
    private ImageView imageView;

    private ImageButton currentLocationButton, compassButton, submitPlaceButton;

    private Location mLastKnownLocation;
    private LocationCallback locationCallback;

    private MaterialSearchBar materialSearchBar;
    private View mapView;

    private RecyclerView categoryRecyclerView, trendingRecyclerView;
    private HorizontalCategoryScrollAdapter horizontalCategoryScrollAdapter;
    private HorizontalScrollTrendingAdapter horizontalScrollTrendingAdapter;

    //demo find button
    private ImageButton btnFind;

    private final float DEFAULT_ZOOM = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        // Create/Set toolbar as actionbar
        toolbar = (FrameLayout) findViewById(R.id.toolbar_container);

        //making status bar transparent 25B4B4B4
        // Check if the version of Android is Lollipop or higher
        if (Build.VERSION.SDK_INT >= 21) {

            // Set the status bar to dark-semi-transparent
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // Set paddingTop of toolbar to height of status bar.
            // Fixes status bar covers toolbar issue
            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        }


        //map buttons
        currentLocationButton = findViewById(R.id.current_location_image_button);
        compassButton = findViewById(R.id.compass_image_button);
        submitPlaceButton = findViewById(R.id.submit_place_image_buttom);

        compassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rippleBackground.startRippleAnimation();
            }
        });

        rippleBackground=(RippleBackground)findViewById(R.id.content);
        imageView=(ImageView)findViewById(R.id.imageView2);

        //navigation drawer
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(this);     //calling side nav bar navigation on-click listener
        navigationView.getMenu().getItem(0).setChecked(true);


        materialSearchBar = findViewById(R.id.searchBar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapView = mapFragment.getView();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        Places.initialize(MainActivity.this, getString(R.string.google_map_api));
        placesClient = Places.createClient(this);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString(), true, null, true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION)
                {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
                else if (buttonCode == MaterialSearchBar.BUTTON_BACK)
                {
                    materialSearchBar.clearSuggestions();
                    materialSearchBar.disableSearch();
                }
                else if (buttonCode == MaterialSearchBar.BUTTON_SPEECH)
                {
                    //openVoiceRecognizer();
                }
            }
        });

        //searching for places and inflating predictions
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setCountry("in")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();
                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if (task.isSuccessful())
                        {
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                            if (predictionsResponse != null)
                            {
                                predictionList = predictionsResponse.getAutocompletePredictions();
                                List<String> suggestionList = new ArrayList<>();
                                for (int i=0; i<predictionList.size(); i++)
                                {
                                    AutocompletePrediction prediction = predictionList.get(i);
                                    suggestionList.add(prediction.getFullText(null).toString());
                                }
                                materialSearchBar.updateLastSuggestions(suggestionList);
                                if (!materialSearchBar.isSuggestionsVisible())
                                {
                                    materialSearchBar.showSuggestionsList();
                                }
                            }
                        }else {
                            Toast.makeText(MainActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                            Log.d("Prediction", "prediction fetching unsuccessful");
                        }
                    }
                });
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                //use placeID to get Lat and Long and use that Lat/Long to go to selected location
                if (position >= predictionList.size())
                {
                    return;
                }

                AutocompletePrediction selectedPrediction = predictionList.get(position);
                String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
                materialSearchBar.setText(suggestion);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialSearchBar.clearSuggestions();
                    }
                }, 1000);

                //closing keyboard
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (inputMethodManager != null)
                {
                    inputMethodManager.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                }

                String placeId = selectedPrediction.getPlaceId();
                List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);

                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();
                        Log.d("place found", "place was found : " + place.getName());
                        LatLng latLng = place.getLatLng();
                        if (latLng != null)
                        {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                            mMap.setMapType(mMap.MAP_TYPE_HYBRID);
                        }
                        materialSearchBar.clearSuggestions();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException)
                        {
                            ApiException apiException = (ApiException) e;
                            apiException.printStackTrace();
                            int statusCode = apiException.getStatusCode();
                            Toast.makeText(MainActivity.this, "place not found", Toast.LENGTH_SHORT).show();
                            Log.d("Place not found", "Place not found" + e.getMessage() + " status code : " + statusCode);
                        }
                    }
                });
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {
                materialSearchBar.clearSuggestions();
            }
        });

        //INFLATING BOTH THE RECYCLER VIEWS


        //Category Recycler view
        categoryRecyclerView = findViewById(R.id.categories_recyclerView);

        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(this);
        categoryLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        categoryRecyclerView.setLayoutManager(categoryLayoutManager);

        final List<HorizontalCategoryScrollModel> horizontalCategoryScrollModelList = new ArrayList<HorizontalCategoryScrollModel>();
        horizontalCategoryScrollAdapter =  new HorizontalCategoryScrollAdapter(horizontalCategoryScrollModelList);

        horizontalCategoryScrollModelList.add(new HorizontalCategoryScrollModel(R.drawable.placeholder_png, "Temples"));
        horizontalCategoryScrollModelList.add(new HorizontalCategoryScrollModel(R.drawable.placeholder_png, "Rivers"));
        horizontalCategoryScrollModelList.add(new HorizontalCategoryScrollModel(R.drawable.placeholder_png, "Shops"));
        horizontalCategoryScrollModelList.add(new HorizontalCategoryScrollModel(R.drawable.placeholder_png, "Malls"));
        horizontalCategoryScrollModelList.add(new HorizontalCategoryScrollModel(R.drawable.placeholder_png, "Mountains"));
        horizontalCategoryScrollModelList.add(new HorizontalCategoryScrollModel(R.drawable.placeholder_png, "Statues"));
        horizontalCategoryScrollModelList.add(new HorizontalCategoryScrollModel(R.drawable.placeholder_png, "Parks"));
        horizontalCategoryScrollModelList.add(new HorizontalCategoryScrollModel(R.drawable.placeholder_png, "Beaches"));

        categoryRecyclerView.setAdapter(horizontalCategoryScrollAdapter);
        horizontalCategoryScrollAdapter.notifyDataSetChanged();

        //Trending Recycler view
        trendingRecyclerView = findViewById(R.id.trending_recyclerView);

        LinearLayoutManager trendingLayoutManager = new LinearLayoutManager(this);
        trendingLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        trendingRecyclerView.setLayoutManager(trendingLayoutManager);

        final List<HorizontalScrollTrendingModel> horizontalScrollTrendingModelList = new ArrayList<HorizontalScrollTrendingModel>();
        horizontalScrollTrendingAdapter =  new HorizontalScrollTrendingAdapter(horizontalScrollTrendingModelList);

        horizontalScrollTrendingModelList.add(new HorizontalScrollTrendingModel(R.drawable.placeholder_png, "Illuminati", "So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error. So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error.", "200 m"));
        horizontalScrollTrendingModelList.add(new HorizontalScrollTrendingModel(R.drawable.placeholder_png, "Illuminati", "So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error. So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error.", "200 m"));
        horizontalScrollTrendingModelList.add(new HorizontalScrollTrendingModel(R.drawable.placeholder_png, "Illuminati", "So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error. So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error.", "200 m"));
        horizontalScrollTrendingModelList.add(new HorizontalScrollTrendingModel(R.drawable.placeholder_png, "Illuminati", "So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error. So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error.", "200 m"));
        horizontalScrollTrendingModelList.add(new HorizontalScrollTrendingModel(R.drawable.placeholder_png, "Illuminati", "So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error. So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error.", "200 m"));
        horizontalScrollTrendingModelList.add(new HorizontalScrollTrendingModel(R.drawable.placeholder_png, "Illuminati", "So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error. So, I am a beginner into Android and Java. I just began learning. While I was experimenting with Intent today, I incurred an error.", "200 m"));

        trendingRecyclerView.setAdapter(horizontalScrollTrendingAdapter);
        horizontalScrollTrendingAdapter.notifyDataSetChanged();

        addMarkers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
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
            int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, r.getDisplayMetrics());

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

        SettingsClient settingsClient = LocationServices.getSettingsClient(MainActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(MainActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException)
                {
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    try {
                        resolvableApiException.startResolutionForResult(MainActivity.this, 100);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (materialSearchBar.isSuggestionsVisible())
                {
                    materialSearchBar.clearSuggestions();
                    materialSearchBar.disableSearch();
                }

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
                            Toast.makeText(MainActivity.this, "Unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        switch (id) {
            case R.id.home:
                Intent homeIntent = new Intent(MainActivity.this, MainActivity.class);
                homeIntent.putExtra("key", "main_activity"); //Optional parameters
                MainActivity.this.startActivity(homeIntent);
                break;

            case R.id.calender:
                Intent calenderIntent = new Intent(MainActivity.this, CalenderActivity.class);
                calenderIntent.putExtra("key", "calender_activity"); //Optional parameters
                MainActivity.this.startActivity(calenderIntent);
                break;

            case R.id.trending:
                Intent trendingIntent = new Intent(MainActivity.this, CurrentActivity.class);
                trendingIntent.putExtra("key", "trending_activity"); //Optional parameters
                MainActivity.this.startActivity(trendingIntent);
                break;

            case R.id.account:
                Intent accountIntent = new Intent(MainActivity.this, AccountActivity.class);
                accountIntent.putExtra("key", "account_activity"); //Optional parameters
                MainActivity.this.startActivity(accountIntent);
                break;

            case R.id.help:
                Intent helpIntent = new Intent(MainActivity.this, HelpActivity.class);
                helpIntent.putExtra("key", "help_activity"); //Optional parameters
                MainActivity.this.startActivity(helpIntent);
                break;

            default:
                return false;
        }

        //TODO: continue it...

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //side bar and app doesn't quit at same time on pressing back

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    public void addMarkers(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("locations");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int size = (int) dataSnapshot.getChildrenCount();
                Marker[] allMarkers = new Marker[size];
                mMap.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: " + ds);
                    try {
                        HashMap<String, Double> obj = (HashMap<String, Double>) ds.getValue();
                        double latitude = obj.get("lat");
                        double longitude = obj.get("lng");
                        LatLng latLng = new LatLng(latitude, longitude);

                        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(latLng).title("Custom Added"));
                    }
                    catch (Exception e){
                        Log.d(TAG, "onDataChange: Error " + e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}