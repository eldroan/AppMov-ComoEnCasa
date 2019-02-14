package com.google.codelabs.mdc.java.shrine;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.codelabs.mdc.java.shrine.Model.VirtualTable;

import java.text.SimpleDateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VirtualTableAdapter extends ArrayAdapter<VirtualTable> {



    private final Context ctx;
    private List<VirtualTable> virtualTableList;
    private Activity thisActivity;

    public VirtualTableAdapter(Context context, List<VirtualTable> vtList, Activity activity) {
        super(context, 0, vtList);
        this.ctx = context;
        this.virtualTableList = vtList;
        this.thisActivity = activity;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        VirtualTableHolder holderVirtualTable;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(this.ctx);
            convertView = inflater.inflate(R.layout.shr_virtual_table_row,parent,false);

            holderVirtualTable = new VirtualTableHolder();

            holderVirtualTable.virtual_table_image = (CircleImageView) convertView.findViewById(R.id.virtual_table_image);
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

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String dateString = dateFormat.format(virtualTable.deliveryTime) + " a las " + timeFormat.format(virtualTable.deliveryTime);
        holderVirtualTable.virtual_table_image.setImageBitmap(virtualTable.image);
        holderVirtualTable.virtual_table_title.setText(virtualTable.title);
        holderVirtualTable.virtual_table_chef.setText(virtualTable.chef.name + " " +virtualTable.chef.surname);
        holderVirtualTable.virtual_table_data.setText(dateString);
        if (virtualTable.paymentMethod == 0)
        {
            holderVirtualTable.virtual_table_pay_method.setText("Efectivo");
        }
        else
        {
            holderVirtualTable.virtual_table_pay_method.setText("Mercadopago");
        }
        holderVirtualTable.virtual_table_price.setText("$ "+String.format("%.2f", virtualTable.price));
        holderVirtualTable.virtual_table_stars.setText(String.format("%.1f", virtualTable.chef.score));


        final Bitmap finalBitmap = Bitmap.createScaledBitmap(virtualTable.image, 300, 300, false);
        final String finalDescription = virtualTable.description;
        holderVirtualTable.virtual_table_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View dialogImageView = LayoutInflater.from(getContext()).inflate(R.layout.image_dialog, null);
                ImageView imgView = dialogImageView.findViewById(R.id.image_dialog_image);
                TextView textView = dialogImageView.findViewById(R.id.description_dialog_image_textview);
                imgView.setImageBitmap(finalBitmap);
                textView.setText(finalDescription);

                Animation anim = new ScaleAnimation(
                        0f, 1f, // Start and end values for the X axis scaling
                        0f, 1f, // Start and end values for the Y axis scaling
                        Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                        Animation.RELATIVE_TO_SELF, 0.1f); // Pivot point of Y scaling
                anim.setFillAfter(true); // Needed to keep the result of the animation
                anim.setDuration(120);
                dialogImageView.startAnimation(anim);

                thisActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Dialog settingsDialog = new Dialog(getContext());
                        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        settingsDialog.setContentView(dialogImageView);
                        settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        settingsDialog.show();
                    }
                });
/*
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.Theme_MaterialComponents_Light_Dialog_MinWidth_ComoEnCasa));
                    builder.setMessage("¿Desea unirse a esta mesa "+vt.title +" de "+ vt.chef.name+" " + vt.chef.surname + "?" + "\nCosto: $ "+ vt.price).setPositiveButton("Si", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();*/
            }
        });


        return convertView;
    }

    private static class VirtualTableHolder{
        public CircleImageView virtual_table_image;
        public TextView virtual_table_title;
        public TextView virtual_table_chef;
        public TextView virtual_table_data;
        public TextView virtual_table_pay_method;
        public TextView virtual_table_price;
        public TextView virtual_table_stars;

    }


}
