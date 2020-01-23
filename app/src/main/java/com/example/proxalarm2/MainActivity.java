package com.example.proxalarm2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    
    private MapView mMapView;
    
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    
    LocationManager locationManager;
    LocationListener locationListener;
    
    Button savedMarker;
    Button newMarker;
    Button back;
    GoogleMap mMap;
    LatLng latLng;
    Marker newCircle;
    
    
    //Creates the project
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        
        savedMarker = findViewById(R.id.saved_button);
        newMarker = findViewById(R.id.new_button);
        back = findViewById(R.id.back_button);
        
        initGoogleMap(savedInstanceState);
        
        
        //On_click actions for buttons
        savedMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                //intent to change from current to new activity
                Intent intent = new Intent(MainActivity.this, SavedDataActivity.class);
                startActivity(intent);
                
            }
        });
        
        
    }
    
    //setup for mapview
    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(mapViewBundle);
        
        mMapView.getMapAsync(this);
        
    }
    
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == 1) {
            
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }
    
    
    //setup for map view
    @Override
    public void onSaveInstanceState(Bundle outState) {
        
        
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        
        
        mMapView.onSaveInstanceState(mapViewBundle);
        
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }
    
    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }
    
    //when the map is ready to use
    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;
        map.setMinZoomPreference(8.0f);
        map.setMaxZoomPreference(16.0f);
        map.setOnMapClickListener(null);
        
        // while permission not granted, ask.
        while (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            
        }
        
        //if permission granted, continue.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            
            map.setMyLocationEnabled(true);
            
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
        }
        
        
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout bottomLayout = findViewById(R.id.bottomLayout);
                LinearLayout bottomLayer2 = findViewById(R.id.bottomLayout2);
                bottomLayer2.setVisibility(View.GONE);
                bottomLayout.setVisibility(View.VISIBLE);
                map.setOnMapClickListener(null);
                
                
            }
        });
        
        
        //when new button is pressed switch layouts then allow placement of a marker
        newMarker.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                LinearLayout bottomLayout = findViewById(R.id.bottomLayout);
                LinearLayout bottomLayer2 = findViewById(R.id.bottomLayout2);
                bottomLayout.setVisibility(View.GONE);
                bottomLayer2.setVisibility(View.VISIBLE);
                
                
                //onClickListener for the map
                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(final LatLng circleR) {
                    
                       
                    
                       /* map.clear();
                        
                            newCircle = map.addMarker(new MarkerOptions().position(circleR).title("newMarker"));
                            newCircle.setPosition(circleR);
                            // Instantiates a new CircleOptions object and defines the center and radius
                            CircleOptions circleOptions = new CircleOptions()
                                    .center(circleR)
                                    .radius(100); // In meters
                            
                            circle = map.addCircle(circleOptions);
                           */
                        
                        // allows use of the seekbar.
                        SeekBar progress = findViewById(R.id.seekBarIncreaseRadius);
                        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                // circle.setRadius(progress);
                            }
                            
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                            
                            }
                            
                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                            
                            }
                        });
                        // Animating to the touched position
                        map.animateCamera(CameraUpdateFactory.newLatLng(circleR));
                        
                        
                    }
                });
            }
            
            
        });
        
        map.setOnMapClickListener(null);
        
        locationListener = new LocationListener() {
            
            @Override
            public void onLocationChanged(Location location) {
                
                
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
                map.animateCamera(cameraUpdate);
                
                
                //get users Address
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> listAdress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (listAdress != null && listAdress.size() > 0) {
                        Log.i("address", listAdress.get(0).toString());
                    }
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                
            }
            
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            
            @Override
            public void onProviderEnabled(String provider) {
            
            }
            
            @Override
            public void onProviderDisabled(String provider) {
            }
            
        };
        
        
    }
    
    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }
    
    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }
    
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    
    
}
