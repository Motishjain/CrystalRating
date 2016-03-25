package com.example.admin.tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.admin.constants.AppConstants;
import com.example.admin.database.DBHelper;
import com.example.admin.database.Reward;
import com.example.admin.util.ImageUtility;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

/**
 * Created by Admin on 3/23/2016.
 */
public class FetchRewardImageTask extends AsyncTask<byte[],Void,Bitmap>{

    ImageView imageView;
    String imageUrl;
    Reward reward;

    public FetchRewardImageTask(ImageView imageView) {
        this.imageView = imageView;
    }

    public FetchRewardImageTask(ImageView imageView, Reward reward) {
        this.imageView = imageView;
        this.imageUrl = reward.getImageUrl();
        this.reward = reward;
    }

    @Override
    protected Bitmap doInBackground(byte[]... imageBytes) {
        //If image bytes are not set, fetch the image and set the bytes
        if(imageBytes[0] == null && imageUrl!=null) {
            imageBytes[0] = ImageUtility.getImageBytes(AppConstants.BASE_URL+imageUrl);
            reward.setImage(imageBytes[0]);
        }
        //Check if still null
        if(imageBytes[0]!=null)
        {
            return ImageUtility.getImageBitmap(imageBytes[0]);
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        Log.i("Iame download- pre", "onPreExecute Called");
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        Log.i("Async-Example", "onPostExecute Called");
        if(result!=null)
        {
            imageView.setImageBitmap(result);
        }
    }


}
