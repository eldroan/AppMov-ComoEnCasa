package com.google.codelabs.mdc.java.shrine;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class FilterVirtualTableFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mymap;
    Integer mapType;
    private MapView mapView;
    SupportMapFragment mapFragment;

    public FilterVirtualTableFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.shr_fragment_filter_virtual_table, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle args = getArguments();


        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);//when you already implement OnMapReadyCallback

        return view;
    }

    private void uptdateMap() {

        // Here, thisActivity is the current activity
        Activity thisActivity = getActivity();
        if (ActivityCompat.checkSelfPermission(thisActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 9999);
            return;
        }else{
            mymap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Toast.makeText(getActivity(), "Permission graanted", Toast.LENGTH_SHORT).show();
                    uptdateMap();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                    uptdateMap();
                }
                return;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mymap = map;
        mymap.setMinZoomPreference(12);
        LatLng ny = new LatLng(40.7143528, -74.0059731);
        mymap.moveCamera(CameraUpdateFactory.newLatLng(ny));

    }

}
