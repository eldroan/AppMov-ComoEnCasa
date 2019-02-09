package com.google.codelabs.mdc.java.shrine;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.codelabs.mdc.java.shrine.Model.VirtualTable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class VirtualTableAdapter extends ArrayAdapter<VirtualTable> {



    private final Context ctx;
    private List<VirtualTable> virtualTableList;

    public VirtualTableAdapter(Context context, List<VirtualTable> vtList) {
        super(context, 0, vtList);
        this.ctx = context;
        this.virtualTableList = vtList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        VirtualTableHolder holderVirtualTable;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(this.ctx);
            convertView = inflater.inflate(R.layout.shr_virtual_table_row,parent,false);

            holderVirtualTable = new VirtualTableHolder();

            holderVirtualTable.virtual_table_image = (ImageView) convertView.findViewById(R.id.virtual_table_image);
            holderVirtualTable.virtual_table_title = (TextView) convertView.findViewById(R.id.virtual_table_title);
            holderVirtualTable.virtual_table_chef = (TextView) convertView.findViewById(R.id.virtual_table_chef);
            holderVirtualTable.virtual_table_data = (TextView) convertView.findViewById(R.id.virtual_table_data);
            holderVirtualTable.virtual_table_pay_method = (TextView) convertView.findViewById(R.id.virtual_table_pay_method);
            holderVirtualTable.virtual_table_price = (TextView) convertView.findViewById(R.id.virtual_table_price);
            holderVirtualTable.virtual_table_stars = (TextView) convertView.findViewById(R.id.virtual_table_stars);


            convertView.setTag(holderVirtualTable);
        }

        else {
            holderVirtualTable = (VirtualTableHolder) convertView.getTag();
        }

        VirtualTable virtualTable = virtualTableList.get(position);
        holderVirtualTable.virtual_table_image.setImageBitmap(virtualTable.image);
        holderVirtualTable.virtual_table_title.setText(virtualTable.title);
        holderVirtualTable.virtual_table_chef.setText(virtualTable.chef.name + " " +virtualTable.chef.surname);
        holderVirtualTable.virtual_table_data.setText(virtualTable.deliveryTime.toString());
        holderVirtualTable.virtual_table_pay_method.setText("sadsa");
        holderVirtualTable.virtual_table_price.setText("$ "+String.format("%.2f", virtualTable.price));
        holderVirtualTable.virtual_table_stars.setText(String.format("%.1f", virtualTable.chef.score));

        return convertView;
    }

    private static class VirtualTableHolder{
        public ImageView virtual_table_image;
        public TextView virtual_table_title;
        public TextView virtual_table_chef;
        public TextView virtual_table_data;
        public TextView virtual_table_pay_method;
        public TextView virtual_table_price;
        public TextView virtual_table_stars;

    }


}
