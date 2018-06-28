package com.michaelwilliamjones.dynamicfragmenttest;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;

/**
 * Created by mikejones on 6/28/18.
 */

public class WebsocketConnectionAlertDialogFragment extends AppCompatDialogFragment {
    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Connection successful.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // close the fragment.
                    }
                });

        return builder.create();
    }
}
