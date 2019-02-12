package com.google.codelabs.mdc.java.shrine;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.support.design.button.MaterialButton;
//import android.support.v4.app.Fragment;
import android.app.Fragment;

import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.codelabs.mdc.java.shrine.Model.IReceivePicture;
import com.google.codelabs.mdc.java.shrine.Model.ITakePicture;
import com.google.codelabs.mdc.java.shrine.Model.VirtualTable;
import com.google.codelabs.mdc.java.shrine.Parse.VirtualTableDAO;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.android.volley.VolleyLog.TAG;


public class GenerarMesaFragment extends Fragment implements IReceivePicture, View.OnClickListener, VirtualTableDAO.IVirtualTablePersistanceResult, VirtualTableDAO.IVirtualTableRetrievingResult {

    private CircleImageView generate_virtual_table_image;
    private MaterialButton take_picture_button;
    private MaterialButton select_picture_button;
    private TextView title_vt_field_text;
    private TextView description_vt_field_text;
    private TextView price_vt_field_text;
    private TextView quantity_vt_field_text;
    private Spinner pay_method_vt_spinner;
    private SwitchCompat auto_accept_switch;
    private MaterialButton generate_vt_button;
    private MaterialButton cancel_vt_button;
    private TextView delivery_datetime_vt_field_text;
    private TextView reservation_datetime_vt_field_text;

    private Bitmap photoBitmap;
    private DatePickerDialog.OnDateSetListener onDateDeliverySetListener;
    private TimePickerDialog.OnTimeSetListener onTimeDeliverySetListener;

    private Calendar delivery;
    private Calendar reservation;
    private String fecha_delivery;
    private String hora_delivery;
    private String fecha_reservation;
    private String hora_reservation;
    private DatePickerDialog.OnDateSetListener onDateReservationSetListener;
    private TimePickerDialog.OnTimeSetListener onTimeReservationSetListener;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Location currentLocation;
    private Boolean receivedLocation = false;
    private Boolean compressingImage = false;
    public static GenerarMesaFragment currentFragment;

    //Estos valores son para la funcionalidad de modificar virtual table
    private boolean  modifyingVirtualTable= false;
    private boolean retrievedVirtualTable = false;
    private VirtualTable editableVT = null;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.shr_fragment_generar_mesa, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.shr_button_generate_vt));

        generate_virtual_table_image = (CircleImageView) view.findViewById(R.id.generate_virtual_table_image);
        take_picture_button = (MaterialButton) view.findViewById(R.id.take_picture_button);
        select_picture_button = (MaterialButton) view.findViewById(R.id.select_picture_button);
        title_vt_field_text = (TextInputEditText) view.findViewById(R.id.title_vt_field_text);
        description_vt_field_text  = (TextInputEditText) view.findViewById(R.id.description_vt_field_text);
        price_vt_field_text = (TextInputEditText) view.findViewById(R.id.price_vt_field_text);
        quantity_vt_field_text = (TextInputEditText) view.findViewById(R.id.quantity_vt_field_text);
        pay_method_vt_spinner = (Spinner) view.findViewById(R.id.pay_method_vt_spinner);
        auto_accept_switch = (SwitchCompat) view.findViewById(R.id.auto_accept_switch);
        generate_vt_button  = (MaterialButton) view.findViewById(R.id.generate_vt_button);
        cancel_vt_button = (MaterialButton) view.findViewById(R.id.cancel_vt_button);
        delivery_datetime_vt_field_text = (TextInputEditText) view.findViewById(R.id.datetime_vt_field_text);
        reservation_datetime_vt_field_text =(TextInputEditText) view.findViewById(R.id.reservation_datetime_vt_field_text);
        delivery_datetime_vt_field_text.setFocusable(false);
        reservation_datetime_vt_field_text.setFocusable(false);
        currentFragment = this;


        String[] pay_method_items = new String[] { "Efectivo", "Mercadopago"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, pay_method_items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pay_method_vt_spinner.setAdapter(adapter);


        //Esto abre unos dialogos para setear fecha_delivery y hora_delivery del plato
        delivery_datetime_vt_field_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar rightNow = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                            getActivity(),
                            R.style.Theme_MaterialComponents_Light_Dialog_MinWidth_ComoEnCasa,
                        onDateDeliverySetListener,
                            rightNow.get(Calendar.YEAR),rightNow.get(Calendar.MONTH),rightNow.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    datePickerDialog.show();
            }
        });

        reservation_datetime_vt_field_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar rightNow = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(),
                        R.style.Theme_MaterialComponents_Light_Dialog_MinWidth_ComoEnCasa,
                        onDateReservationSetListener,
                        rightNow.get(Calendar.YEAR),rightNow.get(Calendar.MONTH),rightNow.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                datePickerDialog.show();
            }
        });

        //Esto abre el dialogo del calendario
        onDateDeliverySetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                Calendar rightNow = Calendar.getInstance();
                month = month + 1;
                fecha_delivery = month + "/" + dayOfMonth + "/" + year;

                //Aca voy a llenar el calendario de la fecha de delivery para poder reconstruirla despues
                delivery = Calendar.getInstance();
                delivery.set(year,month,dayOfMonth);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        R.style.Theme_MaterialComponents_Light_Dialog_MinWidth_ComoEnCasa,
                        //android.R.style.Theme_Material_Dialog,
                        onTimeDeliverySetListener,
                        rightNow.get(Calendar.HOUR_OF_DAY),rightNow.get(Calendar.MINUTE),true);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                timePickerDialog.show();
            }
        };

        onDateReservationSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                Calendar rightNow = Calendar.getInstance();
                month = month + 1;
                fecha_reservation = month + "/" + dayOfMonth + "/" + year;

                //Aca voy a llenar el calendario de la fecha de reservation para poder reconstruirla despues
                reservation = Calendar.getInstance();
                reservation.set(year,month,dayOfMonth);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        R.style.Theme_MaterialComponents_Light_Dialog_MinWidth_ComoEnCasa,
                        //android.R.style.Theme_Material_Dialog,
                        onTimeReservationSetListener,
                        rightNow.get(Calendar.HOUR_OF_DAY),rightNow.get(Calendar.MINUTE),true);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                timePickerDialog.show();
            }
        };

        //Esto abre el dialogo de la hora_delivery y setea el textfield
        onTimeDeliverySetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hora_delivery = hourOfDay + ":" + minute;
                delivery.set(delivery.get(Calendar.YEAR), delivery.get(Calendar.MONTH), delivery.get(Calendar.DAY_OF_MONTH), hourOfDay,minute); // Seteo la hora de deliuvy
                if(fecha_delivery != null && hora_delivery != null)
                {
                    delivery_datetime_vt_field_text.setText(fecha_delivery +" a las "+ hora_delivery);
                }
                else
                {
                    delivery_datetime_vt_field_text.setText("");
                }
            }
        };
        onTimeReservationSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hora_reservation = hourOfDay + ":" + minute;
                reservation.set(reservation.get(Calendar.YEAR), reservation.get(Calendar.MONTH), reservation.get(Calendar.DAY_OF_MONTH), hourOfDay,minute);
                if(fecha_reservation != null && hora_reservation != null)
                {
                    reservation_datetime_vt_field_text.setText(fecha_reservation +" a las "+ hora_reservation);
                }
                else
                {
                    reservation_datetime_vt_field_text.setText("");
                }
            }
        };


        take_picture_button.setOnClickListener(this);
        generate_vt_button.setOnClickListener(this);
        cancel_vt_button.setOnClickListener(this);
        select_picture_button.setOnClickListener(this);

        getLocationPermission(savedInstanceState);

        Bundle b = getArguments();

        if(b != null){
            modifyingVirtualTable = b.getBoolean("modify", false);
            if(modifyingVirtualTable){
                ParseUser user = ParseUser.getCurrentUser();
                if(user != null){

//                    reservation_datetime_vt_field_text.setFocusable(false);
//                    delivery_datetime_vt_field_text.setFocusable(false);
//                    cancel_vt_button.setClickable(false);
//                    take_picture_button.setClickable(false);
//                    select_picture_button.setClickable(false);
//                    title_vt_field_text.setFocusable(false);
//                    description_vt_field_text.setFocusable(false);
//                    price_vt_field_text.setFocusable(false);
//                    quantity_vt_field_text.setFocusable(false);
//                    pay_method_vt_spinner.setClickable(false);
//                    pay_method_vt_spinner.setFocusable(false);
//                    generate_vt_button.setClickable(false);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.shr_button_modify_vt));
                            generate_vt_button.setText(getActivity().getResources().getString(R.string.shr_button_modify_vt));
                        }
                    });
//                    delivery_datetime_vt_field_text.setClickable(false);
//                    reservation_datetime_vt_field_text.setClickable(false);

                    VirtualTableDAO.retrieveMyLastVirtualTable(user.getEmail(),this);
                }else{
                    getActivity().onBackPressed();
                }

            }

        }

        return view;
    }

    @Override
    public void receivePicture(String path) {
        File file = new File(path);
        Bitmap imageBitmap = null;
        try {
            imageBitmap = MediaStore.Images.Media
                    .getBitmap(getActivity().getApplicationContext().getContentResolver(),
                            Uri.fromFile(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (imageBitmap != null) {
            generate_virtual_table_image.setImageBitmap(imageBitmap);
            comprimirImagen(imageBitmap,true);

        }else{
            Log.d("Generate V T","El bitmap fue null");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_picture_button:
                ((ITakePicture)this.getActivity()).takePicture(this);
                break;
            case R.id.select_picture_button:
                pickImage();
                break;
            case R.id.cancel_vt_button:
                getActivity().onBackPressed();
                break;
            case R.id.generate_vt_button:
                if(modifyingVirtualTable){
                    if(retrievedVirtualTable){
                        editableVT.title = title_vt_field_text.getText().toString();
                        editableVT.description = description_vt_field_text.getText().toString();
                        editableVT.image = photoBitmap;
                        editableVT.price = new Double(price_vt_field_text.getText().toString());
                        editableVT.maxEating = new Integer(quantity_vt_field_text.getText().toString());
                        editableVT.paymentMethod = ((String)pay_method_vt_spinner.getSelectedItem()).equals("Efectivo") ? 0 : 1; //Harcodeada cosmica, no anda con mas de 2
                        editableVT.deadlineForEntering = reservation.getTime();
                        editableVT.deliveryTime = delivery.getTime();
                        editableVT.tags = new ArrayList<String>();
                        editableVT.latitude = currentLocation.getLatitude();
                        editableVT.longitude = currentLocation.getLongitude();
                        generate_vt_button.setClickable(false);
                        VirtualTableDAO.saveVirtualTable(editableVT,this);
                    }else{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast t = Toast.makeText(getActivity().getApplicationContext(),"Por favor espere mientras recuperamos la mesa a editar",Toast.LENGTH_SHORT);
                                t.show();

                            }
                        });
                    }
                }else{
                    final String validationResult = validateFields();
                    if(validationResult != null && validationResult.isEmpty()){
                        VirtualTable tempVT = new VirtualTable();
                        tempVT.title = title_vt_field_text.getText().toString();
                        tempVT.description = description_vt_field_text.getText().toString();
                        tempVT.image = photoBitmap;
                        tempVT.price = new Double(price_vt_field_text.getText().toString());
                        tempVT.currentlyEating = 0;
                        tempVT.maxEating = new Integer(quantity_vt_field_text.getText().toString());
                        tempVT.paymentMethod = ((String)pay_method_vt_spinner.getSelectedItem()).equals("Efectivo") ? 0 : 1; //Harcodeada cosmica, no anda con mas de 2
                        tempVT.deadlineForEntering = reservation.getTime();
                        tempVT.deliveryTime = delivery.getTime();
                        tempVT.tags = new ArrayList<String>();
                        tempVT.latitude = currentLocation.getLatitude();
                        tempVT.longitude = currentLocation.getLongitude();
                        tempVT.chef = ((InsideMenuActivity)getActivity()).usuario ;

                        generate_vt_button.setClickable(false);
                        VirtualTableDAO.saveVirtualTable(tempVT,this);

                    }else{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast t = Toast.makeText(getActivity().getApplicationContext(),validationResult,Toast.LENGTH_SHORT);
                                t.show();

                            }
                        });
                    }
                }
                break;
            default:
                break;
        }
    }

    private String validateFields(){

        if(photoBitmap == null){
            return "Debe colocar una imagen para su plato";
        }
        if(receivedLocation == false){
            return "No se consiguio la ubicación del usuario";
        }
        if(title_vt_field_text.getText().toString().isEmpty()){
            return "No puede crearse una mesa sin nombre de plato";
        }
        if(description_vt_field_text.getText().toString().isEmpty()){
            return "No puede crearse una mesa sin descripción";
        }

        try{
            Double price = Double.parseDouble(price_vt_field_text.getText().toString());
            if(price < 0){
                return "El precio no puede ser negativo";
            }
        }catch (NumberFormatException e){
            return "El precio debe ser un número";
        }
        try{
            Double cantidad = Double.parseDouble(quantity_vt_field_text.getText().toString());
            if(cantidad < 0){
                return "La cantidad no puede ser negativa";
            }
        }catch (NumberFormatException e){
            return "La cantidad debe ser un número";
        }

        if(delivery_datetime_vt_field_text.getText().toString().isEmpty()){
            return "Debe elegir una fecha de entrega";
        }else{
            if(reservation_datetime_vt_field_text.getText().toString().isEmpty()){
                return "Debe elegir una fecha de limite para reserva";
            }else{

                if(reservation != null && delivery != null){
                    if(delivery.getTime().before(reservation.getTime())){
                        return "Debe elegir una fecha de entrega posterior al limite para reservas";
                    }
                }
            }
        }

        if(((InsideMenuActivity)getActivity()).usuario == null){
            return "El usuario conectado fue nulo, vuelva a conectarse por favor";
        }

        if(compressingImage){
            return "Por favor espere un poco mientras se comprime su imagen";
        }

        return  "";
    }

    private void getDeviceLocation(){
        Log.d("MapaParaVT", "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d("MapaParaVT", "onComplete: found location!");
                            currentLocation = (Location) task.getResult();
                            receivedLocation = true;
                            Log.d("MapaParaVT", "onComplete: current location is null");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast t = Toast.makeText(getActivity().getApplicationContext(),"Estas en " + currentLocation.getLatitude() + " " + currentLocation.getLongitude(),Toast.LENGTH_SHORT);
                                    t.show();

                                }
                            });

                        }else{
                            Log.d("MapaParaVT", "onComplete: current location is null");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast t = Toast.makeText(getActivity().getApplicationContext(),"unable to get current location",Toast.LENGTH_SHORT);
                                    t.show();

                                }
                            });
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e("MapaParaVT", "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
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

                getDeviceLocation(); //Si tengo permisos busca mi ubicacion
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast t = Toast.makeText(getActivity().getApplicationContext(),"Permission graanted",Toast.LENGTH_SHORT);
                            t.show();

                        }
                    });
                    getDeviceLocation();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }
        }
    }

    @Override
    public void persistanceSucceded() {
        generate_vt_button.setClickable(true);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast t;
                if(modifyingVirtualTable){
                    t = Toast.makeText(getActivity().getApplicationContext(),"La mesa se modifico exitosamente",Toast.LENGTH_SHORT);

                }else{
                    t = Toast.makeText(getActivity().getApplicationContext(),"La mesa se creo exitosamente",Toast.LENGTH_SHORT);
                }
                t.show();

            }
        });

        try {
            getFragmentManager().popBackStackImmediate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void persistanceFailed(ParseException error) {
        generate_vt_button.setClickable(true);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast t;
                if(modifyingVirtualTable){
                    t = Toast.makeText(getActivity().getApplicationContext(),"La mesa no pudo ser modificada :(",Toast.LENGTH_SHORT);

                }else{
                    t = Toast.makeText(getActivity().getApplicationContext(),"La mesa no pudo ser creada :(",Toast.LENGTH_SHORT);
                }
                t.show();

            }
        });
        try {
            getFragmentManager().popBackStackImmediate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void comprimirImagen(final Bitmap imagen, final boolean rotate){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                compressingImage =true;
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                Bitmap as = Bitmap.createScaledBitmap(imagen, 400, 300, false);
                as.compress(Bitmap.CompressFormat.PNG, 30, out);
                byte[] byteArray = out.toByteArray();
                Bitmap decoded = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                Matrix matrix = new Matrix();

                if(rotate)
                    matrix.postRotate(90);

                Bitmap rotatedBitmap = Bitmap.createBitmap(decoded, 0, 0, decoded.getWidth(), decoded.getHeight(), matrix, true);
                photoBitmap = rotatedBitmap;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        generate_virtual_table_image.setImageBitmap(photoBitmap);
                    }
                });
                compressingImage =false;
            }
        });
        t.start();
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent,"Seleccione la aplicación"),10);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 10) {

            Uri returnUri = data.getData();
            try {
                photoBitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), returnUri);
                generate_virtual_table_image.setImageBitmap(photoBitmap);
                comprimirImagen(photoBitmap,false);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void retrievingFailed(ParseException error) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                final Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Fallo la recuperacion de la mesa", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        getActivity().onBackPressed();
    }

    @Override
    public void retrievingSucceded(final List<VirtualTable> results) {
        if(results.isEmpty()){
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    final Toast toast = Toast.makeText(getActivity().getApplicationContext(), "No posee mesas para editar", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
            getActivity().onBackPressed();
        }else{
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    editableVT = results.get(0);
                    //Aca tengo que llenar los fields
                    delivery = Calendar.getInstance();
                    delivery.setTime(editableVT.deliveryTime);
                    //month + "/" + dayOfMonth + "/" + year
                    fecha_delivery = delivery.get(Calendar.MONTH) + "/" + delivery.get(Calendar.DAY_OF_MONTH)+"/"+delivery.get(Calendar.YEAR);
                    //hourOfDay + ":" + minute;
                    hora_delivery = delivery.get(Calendar.HOUR_OF_DAY)+":"+delivery.get(Calendar.MINUTE);
                    reservation = Calendar.getInstance();
                    reservation.setTime(editableVT.deadlineForEntering);
                    //month + "/" + dayOfMonth + "/" + year
                    fecha_reservation = reservation.get(Calendar.MONTH) + "/" + reservation.get(Calendar.DAY_OF_MONTH)+"/"+reservation.get(Calendar.YEAR);
                    //hourOfDay + ":" + minute;
                    hora_reservation = reservation.get(Calendar.HOUR_OF_DAY)+":"+reservation.get(Calendar.MINUTE);

                    reservation_datetime_vt_field_text.setText(fecha_reservation +" a las "+ hora_reservation);
                    delivery_datetime_vt_field_text.setText(fecha_delivery +" a las "+ hora_delivery);
                    title_vt_field_text.setText(editableVT.title);
                    description_vt_field_text.setText(editableVT.description);
                    price_vt_field_text.setText((new Double(editableVT.price).toString()));
                    quantity_vt_field_text.setText((new Integer(editableVT.maxEating)).toString());
                    pay_method_vt_spinner.setSelection(editableVT.paymentMethod);
                    generate_virtual_table_image.setImageBitmap(editableVT.image);
                    photoBitmap = editableVT.image;

                    //Aca tengo que volverle a activar las cosas para editar;
//                    reservation_datetime_vt_field_text.setFocusable(true);
//
//                    delivery_datetime_vt_field_text.setFocusable(true);
//                    cancel_vt_button.setClickable(true);
//                    take_picture_button.setClickable(true);
//                    select_picture_button.setClickable(true);
//                    title_vt_field_text.setFocusable(true);
//                    title_vt_field_text.setClickable(true);
//
//                    description_vt_field_text.setFocusable(true);
//                    price_vt_field_text.setFocusable(true);
//                    quantity_vt_field_text.setFocusable(true);
//                    pay_method_vt_spinner.setClickable(true);
//                    pay_method_vt_spinner.setFocusable(true);
//                    generate_vt_button.setClickable(true);
//                    delivery_datetime_vt_field_text.setClickable(true);
//                    reservation_datetime_vt_field_text.setClickable(true);

                    retrievedVirtualTable = true;
                }
            });
        }

    }
}
