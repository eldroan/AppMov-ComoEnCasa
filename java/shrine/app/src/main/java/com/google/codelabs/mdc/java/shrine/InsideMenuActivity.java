package com.google.codelabs.mdc.java.shrine;

import android.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class InsideMenuActivity extends AppCompatActivity {

    private NavigationView navview;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shr_inside_menu_activity);

        SeleccionarPlatoFragment fr = new SeleccionarPlatoFragment();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fr)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();

        final Toolbar myToolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(myToolbar);

        navview = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);


        navview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.item_menu_inicio:
                        myToolbar.setTitle(getString(R.string.shr_app_name));
                        fragment = new SeleccionarPlatoFragment();
                        break;
                    case R.id.item_menu_direcciones:
                        myToolbar.setTitle(getString(R.string.shr_direcciones));
                        fragment = new NoDisponibleFragment();
                        break;
                    case R.id.item_menu_mis_pedidos:
                        myToolbar.setTitle(getString(R.string.shr_mis_pedidos));
                        fragment = new NoDisponibleFragment();
                        break;
                    case R.id.item_menu_favoritos:
                        myToolbar.setTitle(getString(R.string.shr_favoritos));
                        fragment = new NoDisponibleFragment();
                        break;
                    case R.id.item_menu_mi_cuenta:
                        myToolbar.setTitle(getString(R.string.shr_mi_cuenta));
                        fragment = new NoDisponibleFragment();
                        break;
                    case R.id.item_menu_ayuda:
                        myToolbar.setTitle(getString(R.string.shr_ayuda));
                        fragment = new NoDisponibleFragment();
                        break;
                    case R.id.item_menu_cambiar_rol:
                        myToolbar.setTitle(getString(R.string.shr_app_name));
                        fragment = new NoDisponibleFragment();
                        break;
                }

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();

                //Esto es para que la navView se cierre luego de seleccionar un item
                mDrawerLayout.closeDrawers();

                return InsideMenuActivity.super.onOptionsItemSelected(menuItem);
                //return  true;
            }
        });
    }
}
