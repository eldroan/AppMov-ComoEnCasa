package com.google.codelabs.mdc.java.shrine.Parse;

import com.google.codelabs.mdc.java.shrine.Model.VirtualTable;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class VirtualTableDAO {
    public void createObject(VirtualTable vt) {
        ParseObject entity = new ParseObject("VirtualTable");

        entity.put("name", "A string");

        // Saves the new object.
        // Notice that the SaveCallback is totally optional!
        entity.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                // Here you can handle errors, if thrown. Otherwise, "e" should be null
            }
        });
    }
}
