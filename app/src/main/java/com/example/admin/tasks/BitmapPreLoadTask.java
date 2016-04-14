package com.example.admin.tasks;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.admin.util.ImageUtility;

import java.util.Map;

public class BitmapPreLoadTask extends AsyncTask<Integer, Void, Void> {

    private Map<Integer,Bitmap> imageMap;
    private Context context;
    OnTaskCompleted onTaskCompleted;


    public BitmapPreLoadTask(Context context, Map<Integer,Bitmap> imageMap, OnTaskCompleted onTaskCompleted) {
        this.imageMap = imageMap;
        this.context = context;
        this.onTaskCompleted = onTaskCompleted;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Integer... resId) {
        imageMap.put(resId[0],(ImageUtility.decodeSampledBitmapFromResource(context.getResources(),resId[0])));
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        onTaskCompleted.onTaskCompleted();
    }

    public interface OnTaskCompleted {
        void onTaskCompleted();
    }


}