package com.google.codelabs.mdc.java.shrine;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.codelabs.mdc.java.shrine.Model.IReceivePicture;
import com.google.codelabs.mdc.java.shrine.Model.ITakePicture;
import com.google.codelabs.mdc.java.shrine.Model.User;
import com.google.codelabs.mdc.java.shrine.Parse.UserDAO;
import com.parse.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;


public class RegisterFragment extends Fragment implements UserDAO.IUserCreation, IReceivePicture, View.OnClickListener {
    private User newUser;
    private CircleImageView roundImage;
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.shr_fragment_register, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        roundImage = view.findViewById(R.id.profile_image);
        final TextInputLayout passwordTextInput = view.findViewById(R.id.password_text_input);
        final TextInputEditText passwordEditText = view.findViewById(R.id.password_edit_text);
        final TextInputLayout passwordTextInputConfirmation = view.findViewById(R.id.password_text_input_confirmation);
        final TextInputEditText passwordEditTextConfirmation = view.findViewById(R.id.password_edit_text_confirmation);
        final TextInputEditText nameEditText = view.findViewById(R.id.name_field_text);
        final TextInputEditText surnameEditText = view.findViewById(R.id.surname_field_text);
        final TextInputEditText emailEditText = view.findViewById(R.id.email_field_text);
        MaterialButton takePicture = view.findViewById(R.id.take_picture_button);

        takePicture.setOnClickListener(this);




        return view;
    }

    private boolean isPasswordValid(@Nullable Editable text) {

        //Las contraseñas deben ser mayor a 8 caracteres, contener mayusculas, contener minusculas y contener al menos un numero

        if(text == null)
            return false;

        String t = text.toString();

        if(t.length() < 9)
            return false;
        if(t.equals(t.toLowerCase())){
            //Si el texto es igual al resultado de to lowercase es porque no tiene mayusculas
            return false;
        }
        if(t.equals(t.toUpperCase())){
            //Si el texto es igual al resultado de to uppercase es porque no tiene minusculas
            return false;
        }

        //Esto busca si hay un numero dentro del string, gracias stackoverflow
        Pattern p = Pattern.compile( "[0-9]" );
        Matcher m = p.matcher( t );

        if(m.find() == false){
            // el matcher no encontro nada, no hay numeros en el string
            return false;
        }

        //Si llegaste hasta aca es porque esta todo bien
        return true;
    }

    @Override
    public void connectionFailed() {

    }

    @Override
    public void creationSucceded() {

    }

    @Override
    public void usernameTaken() {

    }

    @Override
    public void emailTaken() {

    }

    @Override
    public void creationFailed(ParseException e) {

    }

    @Override
    public void receivePicture(String path) {
        //Aca me llega el path de la imagen que se guardo en el almacenamiento externo
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
            //Hacer algo con este bitmap
            roundImage.setImageBitmap(imageBitmap);

        }else{
            Log.d("Register","El bitmap fue null");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.take_picture_button:
                ((ITakePicture)this.getActivity()).takePicture(this);
                break;
            default:
            break;
        }
    }
}
