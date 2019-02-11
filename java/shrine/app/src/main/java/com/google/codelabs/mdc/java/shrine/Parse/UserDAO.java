package com.google.codelabs.mdc.java.shrine.Parse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.codelabs.mdc.java.shrine.Model.User;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;


public  class UserDAO {

    public interface IUserCreation{
        void connectionFailed();
        void creationSucceded(); //
        void emailTaken();
        void creationFailed(ParseException e);
    }

    public interface IUserLogin{
        void loginSuccesful();
        void loginFailed(ParseException e);
    }

    public interface IUserModelReceiver{
        void receiveUserSuccesfully(User user);
        void receiveUserFailed(ParseException e);
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
                                //TODO Aca deberia borrar el usuario
                                callbackReceiver.creationFailed(e);
                            }
                        }
                    });

                }else{
                    //Se creo mal el usuario
                    if(e.getCode() == ParseException.ACCOUNT_ALREADY_LINKED || e.getCode() == ParseException.EMAIL_TAKEN || e.getCode() == ParseException.USERNAME_TAKEN)
                        callbackReceiver.emailTaken();
                    else if (e.getCode() == ParseException.CONNECTION_FAILED)
                        callbackReceiver.connectionFailed();
                    else
                        callbackReceiver.creationFailed(e);
                }
            }
        });
    }

    public static void loginUser(String username, String password, final IUserLogin callbackReceiver) {
        ParseUser.getCurrentUser().logOut();

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(final ParseUser user, ParseException e) {
                if (e == null && user != null) {
                    // Logueo con exito,
                    callbackReceiver.loginSuccesful();
                } else {
                    // fallo el login
                    callbackReceiver.loginFailed(e);
                }
            }
        });
    }

    public static void getMyUserModel(final IUserModelReceiver callbackReceiver)
    {
        final ParseUser user = ParseUser.getCurrentUser();
        getUserModel(callbackReceiver,user);
    }

    public static  void getUserModel(final IUserModelReceiver callbackReceiver, final ParseUser user){
        if(user == null)
            callbackReceiver.receiveUserFailed(null);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProfileImage");
        query.whereFullText("username",user.getEmail()).getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {

                if(e == null){
                    ParseFile pf = object.getParseFile("imagepath");
                    pf.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            if(e == null){
                                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                Bitmap mutableBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
                                User modelUser = new User();
                                modelUser.email = user.getEmail();
                                modelUser.name = user.getString("name");
                                modelUser.surname = user.getString("surname");
                                modelUser.score = user.getDouble("score");
                                modelUser.image = mutableBitmap;

                                callbackReceiver.receiveUserSuccesfully(modelUser);

                            }else{
                                callbackReceiver.receiveUserFailed(e);
                            }

                        }
                    });
                }else {
                    callbackReceiver.receiveUserFailed(e);
                }
            }
        });
    }
}
