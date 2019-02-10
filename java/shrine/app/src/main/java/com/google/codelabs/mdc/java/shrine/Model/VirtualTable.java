package com.google.codelabs.mdc.java.shrine.Model;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.List;

public class VirtualTable {

    public String objectId;
    public String title;
    public String description;
    public Bitmap image;
    public double price;
    public int currentlyEating;
    public int maxEating;
    public int paymentMethod;
    public Date deadlineForEntering;
    public Date deliveryTime;
    public String[] tags;
    public double latitude;
    public double longitude;
    public User chef;
}
