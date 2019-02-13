package com.google.codelabs.mdc.java.shrine;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
public class SeleccionarPlatoFragment extends Fragment implements VirtualTableDAO.IVirtualTableRetrievingResult, VirtualTableDAO.ISuscribeToTables{

    private Button filter_button;
    private ListView listViewVirtualTables;
    private VirtualTableAdapter virtualTableAdapter;
    List<VirtualTable> virtualTables;
    public Context myContext;
    private DialogInterface.OnClickListener dialogClickListener;
    private SeleccionarPlatoFragment thisFragment;
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.shr_fragment_seleccionar_plato, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.shr_app_name));
        thisFragment=this;

        filter_button = (Button) view.findViewById(R.id.filter_button);
        listViewVirtualTables = (ListView) view.findViewById(R.id.listViewVirtualTables);
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        virtualTables = new ArrayList<>();

        Bundle b = getArguments();
        if(b != null){

            Boolean shouldRetrieveAll = b.getBoolean("all",false);
            if(shouldRetrieveAll){
                VirtualTableDAO.retrieveAllOpenVirtualTables(this);
                b.putBoolean("all",false); //Esto se hace asi porque sino cada vez que se volvia del filtro el fragmente le pedia al backend todos las mesas y pisaba el resultado filtrado
                setArguments(b);
            }

        }

        //VirtualTableDAO.retrieveAllOpenVirtualTables(this);



        filter_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                FilterVirtualTableFragment fvt = new FilterVirtualTableFragment();
                fvt.callbackReceiver = thisFragment;
                fragmentTransaction.replace(R.id.content_frame, fvt)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
            }
        });

        listViewVirtualTables.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final VirtualTable vt = (VirtualTable) parent.getItemAtPosition(position);

                if(vt.currentlyEating<vt.maxEating)
                {
                    dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    VirtualTableDAO.addEatingPersonToVirtualTable(vt.objectId,getActivity(),thisFragment);
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast t = Toast.makeText(getActivity().getApplicationContext(),"Vos te lo perdiste",Toast.LENGTH_SHORT);
                                            t.show();
                                        }
                                    });
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.Theme_MaterialComponents_Light_Dialog_MinWidth_ComoEnCasa));
                    builder.setMessage("¿Desea unirse a esta mesa "+vt.title +" de "+ vt.chef.name+" " + vt.chef.surname + "?" + "\nCosto: $ "+ vt.price).setPositiveButton("Si", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
                else
                {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast t = Toast.makeText(getActivity().getApplicationContext(),"La mesa a la cual quiere entrar está llena",Toast.LENGTH_LONG);
                            t.show();
                        }
                    });
                }
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

    @Override
    public void subscribedSuccesfully() {
        SeleccionarPlatoFragment fr = new SeleccionarPlatoFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fr)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

}
