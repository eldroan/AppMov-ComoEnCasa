package com.google.codelabs.mdc.java.shrine.Parse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.codelabs.mdc.java.shrine.Model.User;
import com.google.codelabs.mdc.java.shrine.Model.VirtualTable;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class VirtualTableDAO {

    public interface IVirtualTablePersistanceResult{
        void persistanceSucceded();
        void persistanceFailed(ParseException error);
    }

    public interface IVirtualTableRetrievingResult{
        void retrievingFailed(ParseException error);
        void retrievingSucceded(List<VirtualTable> results);
    }

    public static void saveVirtualTable(final VirtualTable virtualTableModel, final IVirtualTablePersistanceResult callbackReceiver) {
        if(virtualTableModel.objectId == null || virtualTableModel.objectId.isEmpty()){
            //Es una virtualtable nueva
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            virtualTableModel.image.compress(Bitmap.CompressFormat.PNG,100,stream);
            byte[] image = stream.toByteArray();
            final ParseFile parseImage = new ParseFile(virtualTableModel.title+(new Double(Math.random() * 10001)).toString()+".png", image);

            parseImage.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if(e== null){

                        ParseObject virtualTable = new ParseObject("VirtualTable");
                        virtualTable.put("title",virtualTableModel.title);
                        virtualTable.put("description", virtualTableModel.description);
                        virtualTable.put("price",virtualTableModel.price);
                        virtualTable.put("image",parseImage);
                        virtualTable.put("currentlyEating",0);
                        virtualTable.put("maxEating",virtualTableModel.maxEating);
                        virtualTable.put("paymentMethod",virtualTableModel.paymentMethod);
                        //virtualTable.put("deadlineForEntering",(new Long(virtualTableModel.deadlineForEntering.getTime())).doubleValue());
                        //virtualTable.put("deliveryTime",(new Long(virtualTableModel.deliveryTime.getTime())).doubleValue());
                        virtualTable.put("deadlineForEntering",(virtualTableModel.deadlineForEntering));
                        virtualTable.put("deliveryTime",(virtualTableModel.deliveryTime));
//                        StringBuilder stbr = new StringBuilder();

                        //Guardo todos lo tags como separados por coma para no tener que buscar la data en otra query
//                        for(int i = 0; i<virtualTableModel.tags.length;i++){
//                            if(i!=0){
//                                //A todos menos el primer elemento le meto la , adelante
//                                stbr.append(",");
//                            }
//                            stbr.append(virtualTableModel.tags[i]);
//                        }
                        virtualTable.put("tags",virtualTableModel.tags);
                        ParseGeoPoint geoPoint = new ParseGeoPoint(virtualTableModel.latitude,virtualTableModel.longitude);
                        virtualTable.put("latlon",geoPoint);
                        virtualTable.put("chef",virtualTableModel.chef.email);

                        virtualTable.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null){
                                    callbackReceiver.persistanceSucceded();
                                }else{
                                    callbackReceiver.persistanceFailed(e);
                                }
                            }
                        });

                    }else{
                        callbackReceiver.persistanceFailed(e);
                    }
                }
            });
        }else{
            //Es una virtualtable que ya existia y hay que modificarla
            ParseQuery<ParseObject> query = ParseQuery.getQuery("VirtualTable");
            query.getInBackground(virtualTableModel.objectId, new GetCallback<ParseObject>() {
                @Override
                public void done(final ParseObject virtualTable, ParseException e) {
                    if(e == null && virtualTable != null){
                        //Vuelvo a subir la imagen por si cambio
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        virtualTableModel.image.compress(Bitmap.CompressFormat.PNG,100,stream);
                        byte[] image = stream.toByteArray();
                        final ParseFile parseImage = new ParseFile(virtualTable.getParseFile("image").getName(), image); //Guardamos la imagen pisando la anterior

                        parseImage.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null){
                                    virtualTable.put("title",virtualTableModel.title);
                                    virtualTable.put("description", virtualTableModel.description);
                                    virtualTable.put("price",virtualTableModel.price);
                                    virtualTable.put("image",parseImage);
                                    virtualTable.put("currentlyEating",virtualTableModel.currentlyEating);
                                    virtualTable.put("maxEating",virtualTableModel.maxEating);
                                    virtualTable.put("paymentMethod",virtualTableModel.paymentMethod);
                                    //virtualTable.put("deadlineForEntering",(new Long(virtualTableModel.deadlineForEntering.getTime())).doubleValue());
                                    //virtualTable.put("deliveryTime",(new Long(virtualTableModel.deliveryTime.getTime())).doubleValue());
                                    virtualTable.put("deadlineForEntering",(virtualTableModel.deadlineForEntering));
                                    virtualTable.put("deliveryTime",(virtualTableModel.deliveryTime));
//                                    StringBuilder stbr = new StringBuilder();

                                    //Guardo todos lo tags como separados por coma para no tener que buscar la data en otra query
//                                    for(int i = 0; i<virtualTableModel.tags.length;i++){
//                                        if(i!=0){
//                                            //A todos menos el primer elemento le meto la , adelante
//                                            stbr.append(",");
//                                        }
//                                        stbr.append(virtualTableModel.tags[i]);
//                                    }
                                    virtualTable.put("tags",virtualTableModel.tags);
                                    ParseGeoPoint geoPoint = new ParseGeoPoint(virtualTableModel.latitude,virtualTableModel.longitude);
                                    virtualTable.put("latlon",geoPoint);
                                    virtualTable.put("chef",virtualTableModel.chef.email);
                                    virtualTable.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(e == null){
                                                callbackReceiver.persistanceSucceded();
                                            }else{
                                                callbackReceiver.persistanceFailed(e);
                                            }
                                        }
                                    });
                                }else{
                                    callbackReceiver.persistanceFailed(e);

                                }
                            }
                        });
                    }else{
                        callbackReceiver.persistanceFailed(e);
                    }
                }
            });
        }
    }
    public static void retrieveMyLastVirtualTable(String username, final IVirtualTableRetrievingResult callbackReceiver){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("VirtualTable").whereGreaterThan("deadlineForEntering",new Date()).whereEqualTo("chef",username).whereEqualTo("currentlyEating",0).addDescendingOrder("createdAt");


        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done( List<ParseObject> objects, ParseException e) {
                if(e == null){
                    treatVirtualTableQueryResult(objects, callbackReceiver);

                }else{
                    callbackReceiver.retrievingFailed(e);
                }
            }
        });

    }

    public static void retrieveAllOpenVirtualTables(final IVirtualTableRetrievingResult callbackReceiver){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("VirtualTable").whereGreaterThan("deadlineForEntering",new Date());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done( List<ParseObject> objects, ParseException e) {
                if(e == null){
                    treatVirtualTableQueryResult(objects, callbackReceiver);

                }else{
                    callbackReceiver.retrievingFailed(e);
                }
            }
        });

    }

    public static void retrieveAllWhithinRange(Double latitude, Double longitude, Double kilometers, final IVirtualTableRetrievingResult callbackReceiver){
        ParseGeoPoint geoPoint = new ParseGeoPoint(latitude,longitude);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("VirtualTable").whereGreaterThan("deadlineForEntering",new Date()).whereWithinKilometers("latlon",geoPoint,kilometers );

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done( List<ParseObject> objects, ParseException e) {
                if(e == null){
                    treatVirtualTableQueryResult(objects, callbackReceiver);

                }else{
                    callbackReceiver.retrievingFailed(e);
                }
            }
        });

    }

    private static void treatVirtualTableQueryResult(List<ParseObject> objects, IVirtualTableRetrievingResult callbackReceiver) {
//        ArrayList<String> usersEmails = new ArrayList<String>();
        if(objects == null) {
            //No encontro ninguna table
            callbackReceiver.retrievingSucceded(new ArrayList<VirtualTable>());
            return;
        }

        ListIterator<ParseObject> objectsli = objects.listIterator();
        ArrayList<VirtualTable>  virtualTables = new ArrayList<VirtualTable>();

        while(objectsli.hasNext()){
            ParseObject virtualTablePO = objectsli.next();
            String email = virtualTablePO.getString("chef");

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("email",email);
            try {
                ParseUser usuarioParse = query.getFirst();

                User modelUser = new User();
                modelUser.email = usuarioParse.getEmail();
                modelUser.name = usuarioParse.getString("name");
                modelUser.surname = usuarioParse.getString("surname");
                modelUser.score = usuarioParse.getDouble("score");

                VirtualTable vt = buildModelVirtualTable(virtualTablePO,modelUser,callbackReceiver);

                if(vt != null){
                    virtualTables.add(vt);
                }else{
                    //Si fallo una vez no sigo tratando de conseguir las cosas
                    break;
                }

            } catch (ParseException e1) {
                callbackReceiver.retrievingFailed(e1);
            }

        }

        callbackReceiver.retrievingSucceded(virtualTables);
    }


    private static VirtualTable buildModelVirtualTable(ParseObject virtualObject,User usuario,IVirtualTableRetrievingResult callbackReceiver){
        VirtualTable vt = new VirtualTable();
        try {
            vt.objectId= virtualObject.getObjectId();
            vt.title= virtualObject.getString("title");
            vt.description= virtualObject.getString("description");
            byte[] image = virtualObject.getParseFile("image").getData();
            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
            vt.image = bmp.copy(Bitmap.Config.ARGB_8888, true);
            vt.price= virtualObject.getDouble("price");
            vt.currentlyEating= virtualObject.getInt("currentlyEating");
            vt.maxEating= virtualObject.getInt("maxEating");
            vt.paymentMethod= virtualObject.getInt("paymentMethod");
            vt.deadlineForEntering= virtualObject.getDate("deadlineForEntering");
            vt.deliveryTime= virtualObject.getDate("deliveryTime");
            vt.tags= virtualObject.getList("tags");
            ParseGeoPoint parseGeoPoint = virtualObject.getParseGeoPoint("latlon");
            vt.latitude= parseGeoPoint.getLatitude();
            vt.longitude= parseGeoPoint.getLongitude();
            vt.chef= usuario;
        } catch (ParseException e) {
            vt = null;
            callbackReceiver.retrievingFailed(e);
        }
        return vt;
    }




}
