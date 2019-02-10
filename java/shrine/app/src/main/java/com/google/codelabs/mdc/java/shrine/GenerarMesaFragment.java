package com.google.codelabs.mdc.java.shrine;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.codelabs.mdc.java.shrine.Model.IReceivePicture;
import com.google.codelabs.mdc.java.shrine.Model.ITakePicture;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.time.Clock;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;


public class GenerarMesaFragment extends Fragment implements IReceivePicture {

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
    private TextView datetime_vt_field_text;
    private Bitmap photoBitmap;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;

    private String fecha;
    private String hora;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.shr_fragment_generar_mesa, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        generate_virtual_table_image = (CircleImageView) view.findViewById(R.id.generate_virtual_table_image);
        take_picture_button = (MaterialButton) view.findViewById(R.id.take_picture_button);
        select_picture_button = (MaterialButton) view.findViewById(R.id.select_picture_button);
        title_vt_field_text = (TextView) view.findViewById(R.id.title_vt_field_text);
        description_vt_field_text  = (TextView) view.findViewById(R.id.description_vt_field_text);
        price_vt_field_text = (TextView) view.findViewById(R.id.price_vt_field_text);
        quantity_vt_field_text = (TextView) view.findViewById(R.id.quantity_vt_field_text);
        pay_method_vt_spinner = (Spinner) view.findViewById(R.id.pay_method_vt_spinner);
        auto_accept_switch = (SwitchCompat) view.findViewById(R.id.auto_accept_switch);
        generate_vt_button  = (MaterialButton) view.findViewById(R.id.generate_vt_button);
        cancel_vt_button = (MaterialButton) view.findViewById(R.id.cancel_vt_button);
        datetime_vt_field_text = (TextView) view.findViewById(R.id.datetime_vt_field_text);
        datetime_vt_field_text.setFocusable(false);


        String[] pay_method_items = new String[] {
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, pay_method_items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pay_method_vt_spinner.setAdapter(adapter);


        //Esto abre unos dialogos para setear fecha y hora del plato
        datetime_vt_field_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar rightNow = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                            getActivity(),
                            R.style.Theme_MaterialComponents_Light_Dialog_MinWidth_ComoEnCasa,
                            onDateSetListener,
                            rightNow.get(Calendar.YEAR),rightNow.get(Calendar.MONTH),rightNow.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
                    datePickerDialog.show();
            }
        });

        //Esto abre el dialogo del calendario
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar rightNow = Calendar.getInstance();
                month = month + 1;
                fecha = month + "/" + dayOfMonth + "/" + year;
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        R.style.Theme_MaterialComponents_Light_Dialog_MinWidth_ComoEnCasa,
                        //android.R.style.Theme_Material_Dialog,
                        onTimeSetListener,
                        rightNow.get(Calendar.HOUR_OF_DAY),rightNow.get(Calendar.MINUTE),true);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
                timePickerDialog.show();
            }
        };

        //Esto abre el dialogo de la hora y setea el textfield
        onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hora = hourOfDay + ":" + minute;
                if(fecha != null && hora!= null)
                {
                    datetime_vt_field_text.setText(fecha +" a las "+hora);
                }
                else
                {
                    datetime_vt_field_text.setText("");
                }
            }
        };


        take_picture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ITakePicture) getActivity()).takePicture(GenerarMesaFragment.this);
            }
        });

        select_picture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ITakePicture) getActivity()).takePicture(GenerarMesaFragment.this);
            }
        });

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
            photoBitmap = imageBitmap;
        }else{
            Log.d("Generate V T","El bitmap fue null");
        }
    }
}
