package com.google.codelabs.mdc.java.shrine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.codelabs.mdc.java.shrine.Model.User;
import com.google.codelabs.mdc.java.shrine.Parse.UserDAO;
import com.parse.ParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginFragment extends Fragment implements UserDAO.IUserLogin {
    private MaterialButton nextButton;
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.shr_login_fragment, container, false);

        final TextInputLayout passwordTextInput = view.findViewById(R.id.password_text_input);
        final TextInputEditText username = view.findViewById(R.id.login_email_text);
        final TextInputEditText passwordEditText = view.findViewById(R.id.password_edit_text);
        final UserDAO.IUserLogin thisLoginFragment = this;
        nextButton = view.findViewById(R.id.login_button);
        MaterialButton registerButton = view.findViewById(R.id.register_button);



        // Set an error if the password is less than 8 characters.
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPasswordValid(passwordEditText.getText())) {
                    passwordTextInput.setError(getString(R.string.shr_error_password));
                } else {
                    passwordTextInput.setError(null); // Clear the error
                    nextButton.setClickable(false);
                    UserDAO.loginUser(username.getText().toString(),passwordEditText.getText().toString(),thisLoginFragment);
                }
            }


        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((NavigationHost) getActivity()).navigateTo(new RegisterFragment(), true); // Navigate to the next Fragment
            }
        });

        // Limpiar el mensaje de error
        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (isPasswordValid(passwordEditText.getText())) {
                    passwordTextInput.setError(null); //Clear the error
                }
                return false;
            }
        });

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus == false){
                    if (isPasswordValid(passwordEditText.getText())) {
                        passwordTextInput.setError(null); //Clear the error
                    }else{
                        passwordTextInput.setError(getString(R.string.shr_error_password));
                    }
                }

            }
        });
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

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
    public void loginSuccesful(User user) {
        nextButton.setClickable(true);
        Intent intent = new Intent(getActivity(), InsideMenuActivity.class);
        intent.putExtra("userModel",user);
        startActivity(intent);
    }

    @Override
    public void loginFailed(ParseException e) {
        nextButton.setClickable(true);
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                final Toast toast = Toast.makeText(getActivity().getApplicationContext(), "No ha podido ingresar, verifique su usuario y contraseña", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }
}
