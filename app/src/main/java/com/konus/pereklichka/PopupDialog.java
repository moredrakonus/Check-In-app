package com.konus.pereklichka;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PopupDialog {
    public interface PopupListener {
        void onTextEntered(String text);
    }

    public void showPopup(Context context, final PopupListener listener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.popup_layout, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        final EditText editTextPopup = dialogView.findViewById(R.id.editTextPopup);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editTextPopup.getText().toString().trim();
                listener.onTextEntered(text);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}
