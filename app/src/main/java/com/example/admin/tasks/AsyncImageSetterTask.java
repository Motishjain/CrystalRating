package com.example.admin.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

public class AsyncImageSetterTask extends AsyncTask<Integer, Void, Bitmap> {

    private ImageView img;
    private Context c;


    public AsyncImageSetterTask(Context c, ImageView img) {

        this.img = img;
        this.c = c;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Integer... resId) {

        Drawable drawable = c.getResources().getDrawable(resId[0]);
        return ((BitmapDrawable)drawable).getBitmap();
    }

    @Override
    protected void onPostExecute(Bitmap result) {

        try {
            img.setImageBitmap(result);
        } catch (Exception e) {

        }
        super.onPostExecute(result);
    }

}