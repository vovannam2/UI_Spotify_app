package com.example.spotify_app.helpers;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.spotify_app.R;
import com.example.spotify_app.listeners.DialogClickListener;
import com.google.android.material.button.MaterialButton;

public class DialogHelper {
    private final Dialog dialog;

    public DialogHelper(Context context, DialogClickListener listener) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog);

        MaterialButton btnPositive = dialog.findViewById(R.id.btn_positive);
        MaterialButton btnNegative = dialog.findViewById(R.id.btn_negative);

        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPositiveButtonClick();
                dialog.dismiss();
            }
        });

        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNegativeButtonClick();
                dialog.dismiss();
            }
        });
    }

    public void setTitle(String title) {
        TextView tvTitle = dialog.findViewById(R.id.tv_dialog_title);
        tvTitle.setText(title);
    }

    public void setMessage(String message) {
        TextView tvMessage = dialog.findViewById(R.id.tv_dialog_message);
        tvMessage.setText(message);
    }

    public void show() {
        dialog.show();
    }

    public void setPositiveButtonContent(String text) {
        MaterialButton btnPositive = dialog.findViewById(R.id.btn_positive);
        btnPositive.setText(text);
    }

    public void setNegativeButtonContent(String text) {
        MaterialButton btnNegative = dialog.findViewById(R.id.btn_negative);
        btnNegative.setText(text);
    }

    public void setPositiveButtonColor(int resId) {
        MaterialButton btnPositive = dialog.findViewById(R.id.btn_positive);
        ColorStateList colorStateList = ColorStateList.valueOf(ContextCompat.getColor(dialog.getContext(), resId));
        btnPositive.setBackgroundTintList(colorStateList);
    }

}
