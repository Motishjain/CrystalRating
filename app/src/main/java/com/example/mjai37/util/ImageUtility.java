package com.example.mjai37.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Admin on 3/13/2016.
 */
public class ImageUtility {

    public Bitmap downloadImage() {
        URL myFileUrl = null;
        Bitmap bmImg = null;
        try {
            myFileUrl = new URL("");
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            Log.i("im connected", "Download");
            bmImg = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bmImg;
    }

    public void saveImage(Context context, Bitmap bmImg) {
        File filename;

        String imagePath = "/";
        try {
            //createDir(IMAGE_DIR);

            filename = new File(imagePath, "imageName");

            FileOutputStream out = new FileOutputStream(filename);

            bmImg.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            generateThumb(bmImg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public Bitmap generateThumb(Bitmap bitmap) {
        File filethumb;
        Bitmap thumb = null;

        String thumbPath = "/";
        try {
            //createDir(THUMB_DIR);

            filethumb = new File(thumbPath, "imageName");

            FileOutputStream out2 = new FileOutputStream(filethumb);

            thumb = Bitmap.createScaledBitmap(bitmap, 250, 340, false);
            thumb.compress(Bitmap.CompressFormat.JPEG, 90, out2);

            out2.flush();
            out2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return thumb;
    }
}
