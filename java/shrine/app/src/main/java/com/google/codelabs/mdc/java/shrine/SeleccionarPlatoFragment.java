package com.google.codelabs.mdc.java.shrine;


import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.codelabs.mdc.java.shrine.Model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SeleccionarPlatoFragment extends Fragment {

    private Button filter_button;
    private ListView listViewVirtualTables;
    private VirtualTableAdapter virtualTableAdapter;
    public Context myContext;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.shr_fragment_seleccionar_plato, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.shr_app_name));



        filter_button = (Button) view.findViewById(R.id.filter_button);
        listViewVirtualTables = (ListView) view.findViewById(R.id.listViewVirtualTables);
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

        List<VirtualTable> vt = new ArrayList<>();

        for (int i = 0; i < 25; i++) {
            VirtualTable vtx = new VirtualTable();
            User us = new User();

            us.name = "Usuario" +i;
            us.surname =  "Surename" +i;
            us.score = i + 0.88f;

            vtx.chef = us;
            vtx.currentlyEating = 2+i;
            vtx.image = bitmap;
            vtx.paymentMethod = 1;
            vtx.title = "COMIDA "+i;
            vtx.price = i + 100.69f;
            vtx.deliveryTime = new Date();

            vt.add(vtx);
        }

        virtualTableAdapter = new VirtualTableAdapter(this.getActivity(), vt);
        listViewVirtualTables.setAdapter(virtualTableAdapter);

        filter_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, new FilterVirtualTableFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }



}
