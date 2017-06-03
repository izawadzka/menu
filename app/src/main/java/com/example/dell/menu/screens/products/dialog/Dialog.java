package com.example.dell.menu.screens.products.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.dell.menu.App;
import com.example.dell.menu.R;
import com.example.dell.menu.events.products.DeleteProductAnywayEvent;

/**
 * Created by Dell on 28.05.2017.
 */

public class Dialog extends DialogFragment{
    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_product_alert).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((App)getActivity().getApplication()).getBus().post(new DeleteProductAnywayEvent());
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setTitle("Warning");
        return builder.create();
    }
}
