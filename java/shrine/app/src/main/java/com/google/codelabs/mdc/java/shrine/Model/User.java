package com.google.codelabs.mdc.java.shrine.Model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    public String name;
    public String surname;
    public String email;
    public double score;
    public Bitmap image;

    public User() {}
    protected User(Parcel in) {
        name = in.readString();
        surname = in.readString();
        email = in.readString();
        score = in.readDouble();
        image = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(surname);
        dest.writeString(email);
        dest.writeDouble(score);
        dest.writeParcelable(image, flags);
    }
}
