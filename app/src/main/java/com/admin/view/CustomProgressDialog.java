package com.admin.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.admin.freddyspeaks.R;

/**
 * Created by admin on 17-04-2016.
 */
public class CustomProgressDialog extends ProgressDialog {

    private AnimationDrawable animation;

    public static ProgressDialog createCustomProgressDialog(Context context) {

        CustomProgressDialog dialog = new CustomProgressDialog(context,R.style.CustomDialogTheme);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }

    public CustomProgressDialog(Context context) {
        super(context);
    }

    public CustomProgressDialog(Context context, int theme) {

        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_progress_dialog);

        ImageView animate = (ImageView) findViewById(R.id.animation);

        animate.setBackgroundResource(R.drawable.progress_dialog_animation_list);

        animation = (AnimationDrawable) animate.getBackground();
    }

    @Override
    public void show() {

        super.show();

        animation.start();
    }

    @Override
    public void dismiss() {

        super.dismiss();

        animation.stop();

    }
}

