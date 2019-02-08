package com.google.codelabs.mdc.java.shrine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class InsideMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shr_inside_menu_activity);
        SeleccionarPlatoFragment fr = new SeleccionarPlatoFragment();
        fr.myContext = this.getApplicationContext();
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.drawer_layout, fr)
                    .commit();
        }
        Toolbar myToolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(myToolbar);

    }
}
