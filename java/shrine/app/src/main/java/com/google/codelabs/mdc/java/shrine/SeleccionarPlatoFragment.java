package com.google.codelabs.mdc.java.shrine;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.codelabs.mdc.java.shrine.Model.VirtualTable;
import com.google.codelabs.mdc.java.shrine.Parse.VirtualTableDAO;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import static com.parse.Parse.getApplicationContext;

//import android.support.v4.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class SeleccionarPlatoFragment extends Fragment implements VirtualTableDAO.IVirtualTableRetrievingResult{

    private Button filter_button;
    private ListView listViewVirtualTables;
    private VirtualTableAdapter virtualTableAdapter;
    List<VirtualTable> virtualTables;
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

        virtualTables = new ArrayList<>();

        VirtualTableDAO.retrieveAllOpenVirtualTables(this);

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


    @Override
    public void retrievingFailed(ParseException error) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Toast toast = Toast.makeText(getApplicationContext(), "Falló al llenar la lista", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    public void retrievingSucceded(List<VirtualTable> results) {
        virtualTables = results;
        virtualTableAdapter = new VirtualTableAdapter(this.getActivity(), virtualTables);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listViewVirtualTables.setAdapter(virtualTableAdapter);
            }
        });
    }
}
