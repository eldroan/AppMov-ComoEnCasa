package com.google.codelabs.mdc.java.shrine.Parse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.codelabs.mdc.java.shrine.Model.User;
import com.parse.GetDataCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.util.logging.Logger;

public  class UserDAO {

    public interface IUserCreation{
        void connectionFailed();
        void creationSucceded(); //
        void usernameTaken();
        void emailTaken();
        void creationFailed(ParseException e);
    }

    public interface IUserLogin{
        void loginSuccesful(User user);
        void loginFailed(ParseException e);
    }

    public static void createUser(final User localUser, final String password, final IUserCreation callbackReceiver) {
        ParseUser.getCurrentUser().logOut();


        ParseUser user = new ParseUser();
        user.setUsername(localUser.email);
        user.setPassword(password);
        user.setEmail(localUser.email);
        user.put("name",localUser.name);
        user.put("surname" , localUser.surname);
        user.put("score",0d);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    //Se creo bien el usuario, ahora hay que guardar la imagen
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap newbitmap = Bitmap.createScaledBitmap(localUser.image, 200, 200, true);
                    stream = new ByteArrayOutputStream();
                    newbitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] image = stream.toByteArray();
                    final ParseFile parseImage = new ParseFile("image"+localUser.email.hashCode()+".png", image);
                    parseImage.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                //La imagen se subio bien
                                ParseObject profileImage = new ParseObject("ProfileImage");
                                profileImage.put("username",localUser.email);
                                profileImage.put("imagepath",parseImage);

                                profileImage.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null){
                                            callbackReceiver.creationSucceded();
                                        }else{
                                            callbackReceiver.creationFailed(e);
                                        }
                                    }
                                });


                            }else{
                                //La imagen se subio mal
                                callbackReceiver.creationFailed(e);
                            }
                        }
                    });

                }else{
                    //Se creo mal el usuario
                    callbackReceiver.creationFailed(e);
                }
            }
        });
    }

    public static void loginUser(String username, String password, final IUserLogin callbackReceiver) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(final ParseUser user, ParseException e) {
                if (user != null) {
                    // Logueo con exito, hay que construir el usuario del modelo
                    //Primero recuperamos la imagen del usuario;
                    ParseFile image = (ParseFile) user.getParseFile("imageFileName");

                    image.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            if(e == null){
                                //Encontro la imagen, ahora contruyo el bitmap
                                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                Bitmap mutableBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);

                                //Ahora puedo crear el usuario y setearle el bitmap
                                User modelUser = new User();
                                modelUser.email = user.getEmail();
                                modelUser.name = user.getString("name");
                                modelUser.surname = user.getString("surname");
                                modelUser.score = user.getDouble("score");
                                modelUser.image = mutableBitmap;

                                callbackReceiver.loginSuccesful(modelUser);
                            }else{
                                //No encontro la imagen, fallo el login
                                callbackReceiver.loginFailed(e);
                            }
                        }
                    });


                } else {
                    // fallo el login
                    callbackReceiver.loginFailed(e);
                }
            }
        });
    }
}
