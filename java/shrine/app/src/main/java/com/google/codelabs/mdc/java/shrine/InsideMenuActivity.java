package com.google.codelabs.mdc.java.shrine;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.codelabs.mdc.java.shrine.Model.IReceivePicture;
import com.google.codelabs.mdc.java.shrine.Model.ITakePicture;
import com.google.codelabs.mdc.java.shrine.Model.User;
import com.google.codelabs.mdc.java.shrine.Parse.UserDAO;
import com.parse.ParseException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class InsideMenuActivity extends AppCompatActivity implements UserDAO.IUserModelReceiver,ITakePicture {
    private NavigationView navview;
    private DrawerLayout mDrawerLayout;
    public User usuario;
    static IReceivePicture pictureReceiver;
    String pathPhoto;

    private boolean userReceived = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shr_inside_menu_activity);

        SeleccionarPlatoFragment fr = new SeleccionarPlatoFragment();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fr)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();

        final Toolbar myToolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(myToolbar);

        navview = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        UserDAO.getMyUserModel(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


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
                    case R.id.item_menu_generate_vt:
                        myToolbar.setTitle(getString(R.string.shr_button_generate_vt));
                        Bundle b1 = new Bundle();
                        b1.putBoolean("modify",false);
                        fragment = new GenerarMesaFragment();
                        fragment.setArguments(b1);
                        break;
                    case R.id.item_menu_edit_vt:
                        myToolbar.setTitle(getString(R.string.shr_button_generate_vt));
                        Bundle b2 = new Bundle();
                        b2.putBoolean("modify",true);
                        fragment = new GenerarMesaFragment();
                        fragment.setArguments(b2);
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

    @Override
    public void receiveUserSuccesfully(User user) {
        usuario = user;
        CircleImageView roundImage = findViewById(R.id.sidebar_profile_image);
        roundImage.setImageBitmap(user.image);
        ((TextView)findViewById(R.id.sidebar_username)).setText(user.name + " " + user.surname);
    }

    @Override
    public void receiveUserFailed(ParseException e) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Toast toast = Toast.makeText(getApplicationContext(), "Recuperacion de imagen ha fallado", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //BEGIN codigo para sacar fotos (Extraido del laboratorio 6)
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_";
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg",dir);

        pathPhoto = image.getAbsolutePath();
        return image;
    }
    @Override
    public void takePicture(IReceivePicture receiver) {
        if(receiver != null)
            pictureReceiver = receiver;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) { }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1337);
            }
        }
    }
    @Override
    protected void onActivityResult(int reqCode,int resCode, Intent data) {
        super.onActivityResult(reqCode,resCode, data);
        if (reqCode == 1337 && resCode == RESULT_OK) {

            if(pictureReceiver == null)
                pictureReceiver = (IReceivePicture)getSupportFragmentManager().findFragmentById(R.id.container);
            pictureReceiver.receivePicture(pathPhoto);
        }
    }
    //END codigo para sacar fotos (Extraido del laboratorio 6)
}
