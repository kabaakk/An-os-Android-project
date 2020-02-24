package com.res.anos;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class utils {


    public static AlertDialog getAlertDialog(Activity activity, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_for_pd,null,false);
        TextView messageTv = view.findViewById(R.id.message);
        messageTv.setText(message);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        return dialog;

    }
}
