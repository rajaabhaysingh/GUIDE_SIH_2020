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
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
    private Set<LatLng> verifiedLatLng;
    private List<Marker> currMarkers;

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

        verifiedLatLng = new HashSet<>();
        currMarkers = new ArrayList<>();

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

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(currMarkers.size() > 0){
                    currMarkers.get(0).remove();
                    currMarkers.clear();
                    return;
                }
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                Marker marker = mMap.addMarker(options);
                currMarkers.add(marker);
                LatLng origin = currMarkers.get(0).getPosition();
                LatLng dest = latLng;

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
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
                mMap.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: " + ds);
                    try {
                        HashMap<String, Double> obj = (HashMap<String, Double>) ds.getValue();
                        double latitude = obj.get("lat");
                        double longitude = obj.get("lng");
                        LatLng latLng = new LatLng(latitude, longitude);
                        verifiedLatLng.add(latLng);
                        Marker marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(latLng).title("Custom Added"));
//                        currMarkers.add(marker);
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

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }

    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null)
                mMap.addPolyline(lineOptions);
            else
                Toast.makeText(MainActivity.this, "No Route Found", Toast.LENGTH_SHORT).show();
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=walking";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

}