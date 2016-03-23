package com.example.admin.tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.admin.util.ImageUtility;

/**
 * Created by Admin on 3/23/2016.
 */
public class ImageConversionTask extends AsyncTask<byte[],Void,Bitmap>{

    ImageView imageView;

    public ImageConversionTask(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(byte[]... bytes) {
        // TODO Auto-generated method stub
        return ImageUtility.getImageBitmap(bytes[0]);
    }

    @Override
    protected void onPreExecute() {
        Log.i("Iame download- pre", "onPreExecute Called");
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        Log.i("Async-Example", "onPostExecute Called");
        imageView.setImageBitmap(result);
    }

}
