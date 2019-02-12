package com.google.codelabs.mdc.java.shrine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.card.MaterialCardView;
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
import android.widget.Toast;

import com.google.codelabs.mdc.java.shrine.Model.IReceivePicture;
import com.google.codelabs.mdc.java.shrine.Model.ITakePicture;
import com.google.codelabs.mdc.java.shrine.Model.User;
import com.google.codelabs.mdc.java.shrine.Parse.UserDAO;
import com.parse.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class RegisterFragment extends Fragment implements UserDAO.IUserCreation, IReceivePicture, View.OnClickListener {
    private Bitmap photoBitmap;
    private CircleImageView roundImage;
    private TextInputEditText passwordEditText;
    private TextInputEditText passwordEditTextConfirmation;
    private TextInputEditText nameEditText;
    private TextInputEditText surnameEditText;
    private TextInputEditText emailEditText;
    private boolean compressingImage = false;

    MaterialButton createUserButton;
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.shr_fragment_register, container, false);


        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        roundImage = view.findViewById(R.id.profile_image);
        final TextInputLayout passwordTextInput = view.findViewById(R.id.password_text_input);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        final TextInputLayout passwordTextInputConfirmation = view.findViewById(R.id.password_text_input_confirmation);
        passwordEditTextConfirmation = view.findViewById(R.id.password_edit_text_confirmation);
        final TextInputLayout nameEditLayout = view.findViewById(R.id.name_field_layout);
        nameEditText = view.findViewById(R.id.name_field_text);
        final TextInputLayout surnameEditLayout = view.findViewById(R.id.surname_field_layout);
        surnameEditText = view.findViewById(R.id.surname_field_text);
        final TextInputLayout emailEditLayout = view.findViewById(R.id.email_field_layout);
        emailEditText = view.findViewById(R.id.email_field_text);
        MaterialButton takePicture = view.findViewById(R.id.take_picture_button);
        MaterialButton selectPicture = view.findViewById(R.id.select_picture_button);
        createUserButton = view.findViewById(R.id.register_user_button);

        takePicture.setOnClickListener(this);
        createUserButton.setOnClickListener(this);
        selectPicture.setOnClickListener(this);

        //Seteo los onfocuschange listener de todos los edit text
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus == false){
                    if(isPasswordValid(passwordEditText.getText())){
                        passwordTextInput.setError(null);
                    }else{
                        passwordTextInput.setError(getResources().getString(R.string.shr_error_password));
                    }
                }
            }
        });
        passwordEditTextConfirmation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus == false){
                    if(passwordEditTextConfirmation.getText().toString().equals(passwordEditText.getText().toString())){
                        passwordTextInputConfirmation.setError(null);
                    }else{
                        passwordTextInputConfirmation.setError(getResources().getString(R.string.shr_error_password_confirmation));

                    }
                }
            }
        });
        nameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus == false){
                    if(nameEditText.getText().toString().isEmpty() == false){
                        nameEditLayout.setError(null);
                    }else{
                        nameEditLayout.setError(getResources().getString(R.string.shr_error_name));
                    }
                }
            }
        });
        surnameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus == false){
                    if(surnameEditText.getText().toString().isEmpty() == false){
                        surnameEditLayout.setError(null);
                    }else{
                        surnameEditLayout.setError(getResources().getString(R.string.shr_error_surname));
                    }
                }
            }
        });
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus == false){
                    if(emailEditText.getText().toString().isEmpty() == false){
                        emailEditLayout.setError(null);
                    }else{
                        emailEditLayout.setError(getResources().getString(R.string.shr_error_email));
                    }
                }
            }
        });




        return view;
    }


    private boolean validateUserFields(User modelUser){

        return modelUser.surname.isEmpty() == false && modelUser.name.isEmpty() == false && modelUser.email.isEmpty() == false;
    }

    private boolean isCompressingImage(){
        if(compressingImage){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast t = Toast.makeText(getActivity().getApplicationContext(),"Espere por favor mientras se comprime la imagen",Toast.LENGTH_SHORT);
                    t.show();
                }
            });
            return true;
        }else{
            return false;
        }
    }

    private boolean isPasswordValid(@Nullable Editable text) {

        //Las contraseñas deben ser mayor a 8 caracteres, contener mayusculas, contener minusculas y contener al menos un numero (requisito de Parse)

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
        createUserButton.setClickable(true);
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                final Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Fallo la conexion", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

    @Override
    public void creationSucceded() {
        createUserButton.setClickable(true);
        //Toast.makeText(getActivity().getApplicationContext(),"Creacion de usuario exitosa",Toast.LENGTH_SHORT);
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                final Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Creacion de usuario exitosa", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        try {
            getFragmentManager().popBackStackImmediate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void emailTaken() {
        createUserButton.setClickable(true);
        //Toast.makeText(getContext(),"Email en uso",Toast.LENGTH_SHORT);
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                final Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Email en uso", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    public void creationFailed(final ParseException e) {
        createUserButton.setClickable(true);
        //Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT);
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                final Toast toast = Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });

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
            photoBitmap = imageBitmap;
            comprimirImagen(photoBitmap,true);

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
            case R.id.select_picture_button:
                //To - Do
                    pickImage();
                break;
            case R.id.register_user_button:
                if(isCompressingImage() == false && isPasswordValid(passwordEditText.getText()) && photoBitmap != null){
                    String password = passwordEditText.getText().toString();
                    if(password.equals(passwordEditTextConfirmation.getText().toString())){
                        User newUser = new User();
                        newUser.email = emailEditText.getText().toString();
                        newUser.name = nameEditText.getText().toString();
                        newUser.surname = surnameEditText.getText().toString();
                        newUser.image = photoBitmap;

                        if(validateUserFields(newUser)){
                            createUserButton.setClickable(false);
                            UserDAO.createUser(newUser,password,this);
                        }
                    }
                }
                break;
            default:
            break;
        }
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
                roundImage.setImageBitmap(photoBitmap);
                comprimirImagen(photoBitmap,false);
            } catch (IOException e) {
                e.printStackTrace();
            }


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
                        roundImage.setImageBitmap(photoBitmap);
                    }
                });
                compressingImage =false;
            }
        });
        t.start();
    }

}
