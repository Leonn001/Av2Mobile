package com.example.apptrail;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.apptrail.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker usuario;
    private Location loc;
    private ActivityMapsBinding binding;
    private static final int REQUEST_LOCATION_UPDATES = 1;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        startLocationUpdates();
        mMap = googleMap;

        MarkerOptions user_markerOptions=new MarkerOptions();
        if(loc!=null) {
            user_markerOptions.position(new LatLng(loc.getLatitude(), loc.getLongitude()));
        } else {
            user_markerOptions.position(new LatLng(0, 0));
        }
        user_markerOptions.title("Usuário");
        user_markerOptions.snippet("Estou aqui!");
        usuario=mMap.addMarker(user_markerOptions);
    }

    public void startLocationUpdates() {
        if  (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)==
                PackageManager.PERMISSION_GRANTED){
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            mLocationRequest = new LocationRequest.Builder(5*1000).build();
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location location = locationResult.getLastLocation();
                    if(location!=null) {
                        loc = location;
                        Log.d("Latitude", String.valueOf(loc.getLatitude()));
                        Log.d("Longitude", String.valueOf(loc.getLongitude()));

                        usuario.setPosition(new LatLng(loc.getLatitude(),loc.getLongitude()));
                        // centraliza a camera na posição do marcador mudando o zoom
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom
                                (new LatLng(loc.getLatitude(), loc.getLongitude()),15));
                    }
                }
            };
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,null);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_UPDATES);
        }
    }
}