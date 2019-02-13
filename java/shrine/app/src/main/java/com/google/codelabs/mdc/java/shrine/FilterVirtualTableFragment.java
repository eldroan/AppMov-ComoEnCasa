package com.google.codelabs.mdc.java.shrine;
import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.inputmethodservice.Keyboard;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.codelabs.mdc.java.shrine.Parse.VirtualTableDAO;

import java.util.Calendar;
import java.util.Date;

import static com.android.volley.VolleyLog.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterVirtualTableFragment extends Fragment implements OnMapReadyCallback {
    public VirtualTableDAO.IVirtualTableRetrievingResult callbackReceiver;

    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float ZOOM_BASE = 15f;
    private GoogleMap mymap;
    private SeekBar radio_filter_vt_seekbar;
    private TextView radio_filter_vt_text;
    private TextInputEditText title_filter_vt_textl;
    private TextInputEditText max_price_filter_vt_text;
    private SeekBar min_score_vt_seekbar;
    private TextView min_score_vt_text;
    private TextInputEditText date_aprox_filter_vt_text;
    private RadioButton cash_radio_button;
    private RadioButton mercadopago_radio_button;
    private MaterialButton filter_vt_button;
    private ScrollView scrollViewFragmentFilter;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;
    private MapView mapView;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = false;
    private float zoomValue = ZOOM_BASE;
    Circle mapCircle;
    private String fecha;
    private String hora;
    private Calendar dateEat;
    private Location myCurrentLocation;

    public FilterVirtualTableFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.shr_fragment_filter_virtual_table, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        radio_filter_vt_seekbar = view.findViewById(R.id.radio_filter_vt_seekbar);
        radio_filter_vt_text = view.findViewById(R.id.radio_filter_vt_text);
        title_filter_vt_textl = view.findViewById(R.id.title_filter_vt_text);
        max_price_filter_vt_text = view.findViewById(R.id.max_price_filter_vt_text);
        min_score_vt_seekbar = view.findViewById(R.id.min_score_vt_seekbar);
        min_score_vt_text = view.findViewById(R.id.min_score_vt_text);
        date_aprox_filter_vt_text = view.findViewById(R.id.date_aprox_filter_vt_text);
        cash_radio_button = view.findViewById(R.id.cash_radio_button);
        mercadopago_radio_button = view.findViewById(R.id.mercadopago_radio_button);
        filter_vt_button = view.findViewById(R.id.filter_vt_button);
        //scrollViewFragmentFilter = view.findViewById(R.id.scrollViewFragmentFilter);
        mapView = view.findViewById(R.id.mapView);

        date_aprox_filter_vt_text.setFocusable(false);
        SetDataTimePickerDialogListener();

        radio_filter_vt_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                               @Override
                                                               public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                                   zoomValue = ZOOM_BASE-(0.3f*progress);
                                                                   getDeviceLocation();
                                                               }

                                                               @Override
                                                               public void onStartTrackingTouch(SeekBar seekBar) {

                                                               }

                                                               @Override
                                                               public void onStopTrackingTouch(SeekBar seekBar) {

                                                               }
                                                           }
        );

        min_score_vt_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                            @Override
                                                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                                Integer newScore = progress;
                                                                min_score_vt_text.setText(newScore.toString());
                                                            }

                                                            @Override
                                                            public void onStartTrackingTouch(SeekBar seekBar) {

                                                            }

                                                            @Override
                                                            public void onStopTrackingTouch(SeekBar seekBar) {

                                                            }
                                                        }
        );

        date_aprox_filter_vt_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar rightNow = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(),
                        R.style.Theme_MaterialComponents_Light_Dialog_MinWidth_ComoEnCasa,
                        onDateSetListener,
                        rightNow.get(Calendar.YEAR),rightNow.get(Calendar.MONTH),rightNow.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                datePickerDialog.show();
            }
        });

        filter_vt_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        filter_vt_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float radiusInMeters = (450f+(radio_filter_vt_seekbar.getProgress()*150f));
                Double radiusKM = new Double(radiusInMeters)  * 0.001d;
                String title;
                if(title_filter_vt_textl.getText().toString().isEmpty()){
                    title = null;
                }else{
                    title = title_filter_vt_textl.getText().toString();
                }
                Double price;
                if(max_price_filter_vt_text.getText().toString().isEmpty()){
                    price = null;
                }else{
                    price = Double.parseDouble(max_price_filter_vt_text.getText().toString());
                }
                Integer minScore = min_score_vt_seekbar.getProgress(); //No anda por ahora
                Date dateAprox = null;
                if(dateEat != null){
                    dateAprox = dateEat.getTime();
                }

                Integer payMethod = cash_radio_button.isChecked() ? 0 : 1; //Harcodeada cosmica, no anda con mas de 2

                VirtualTableDAO.retrieveWithFilters(myCurrentLocation.getLatitude(),myCurrentLocation.getLongitude(),radiusKM,price,dateAprox,payMethod,title,callbackReceiver);

                hideKeyboard(getActivity());
                getActivity().onBackPressed();

            }
        });



        getLocationPermission(savedInstanceState);
        initGoogleMap(savedInstanceState);


        return view;
    }

    private void SetDataTimePickerDialogListener() {
        //Esto abre el dialogo del calendario
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar rightNow = Calendar.getInstance();
                month = month + 1;
                fecha = month + "/" + dayOfMonth + "/" + year;

                dateEat = Calendar.getInstance();
                dateEat.set(year,month,dayOfMonth);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        R.style.Theme_MaterialComponents_Light_Dialog_MinWidth_ComoEnCasa,
                        //android.R.style.Theme_Material_Dialog,
                        onTimeSetListener,
                        rightNow.get(Calendar.HOUR_OF_DAY),rightNow.get(Calendar.MINUTE),true);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                timePickerDialog.show();
            }
        };

        //Esto abre el dialogo de la hora y setea el textfield
        onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hora = hourOfDay + ":" + minute;
                dateEat.set(dateEat.get(Calendar.YEAR), dateEat.get(Calendar.MONTH), dateEat.get(Calendar.DAY_OF_MONTH), hourOfDay,minute);
                if(fecha != null && hora!= null)
                {
                    date_aprox_filter_vt_text.setText(fecha +" a las "+hora);
                }
                else
                {
                    date_aprox_filter_vt_text.setText("");
                    dateEat = null;
                }
            }
        };
    }

    private void UpdateMapRadius(LatLng currentLocation){
        float newRadius;
        newRadius = (450f+(radio_filter_vt_seekbar.getProgress()*150f));
        if(mapCircle!=null)
        {
            mapCircle.remove();
        }
        CircleOptions co = new CircleOptions();
        co.radius(newRadius);
        radio_filter_vt_text.setText(((int) newRadius)+" mts.");
        co.center(currentLocation);
        co.fillColor(Color.argb(80 ,170,166,157));
        co.strokeColor(Color.argb(255,255,177,66));
        co.strokeWidth(5);
        mapCircle  = mymap.addCircle(co);

    }

    private void initGoogleMap(Bundle savedInstanceState){
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    private void getLocationPermission(Bundle savedInstanceState){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
            }else{
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    zoomValue);

//                            myCurrentLocation.setLatitude(currentLocation.getLatitude());
//                            myCurrentLocation.setLongitude(currentLocation.getLongitude());
                            myCurrentLocation = currentLocation;

                            UpdateMapRadius(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getActivity(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mymap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
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
                    updateMap();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                    updateMap();
                }
                return;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }

    private void updateMap()
    {
        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mymap.setMyLocationEnabled(true);
            mymap.getUiSettings().setMyLocationButtonEnabled(false);
            mymap.getUiSettings().setZoomGesturesEnabled(false);
            mymap.getUiSettings().setZoomControlsEnabled(false);

        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mymap = map;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        updateMap();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


}
