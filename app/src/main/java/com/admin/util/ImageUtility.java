package com.admin.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Button;

import com.admin.freddyspeaks.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Admin on 3/13/2016.
 */
public class ImageUtility {

    static HashMap<Integer,Bitmap> imageBitmapCollection = new HashMap<>();

    public static byte[] getImageBytes(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            Bitmap bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return baos.toByteArray();
        }
        catch(IOException e){
            Log.e("ImageUtility","Unable to load image byte data");
        }
        return null;
    }

    public static Bitmap getImageBitmap(byte[] imageBytes) {
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    public static Bitmap getImageBitmap(int resId) {
        return imageBitmapCollection.get(resId);
    }

    public static HashMap<Integer, Bitmap> getImageBitmapCollection() {
        return imageBitmapCollection;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inJustDecodeBounds = true;
        //BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        //options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static void setButtonLook(Context context, boolean enabled, Button item) {
        item.setEnabled(enabled);
        Drawable originalIcon = context.getResources().getDrawable(R.drawable.rating_card_button_shape);
        Drawable icon = enabled ? originalIcon : convertDrawableToGrayScale(originalIcon,item,context);
        item.setBackgroundDrawable(icon);
    }

    /**
     *
     * Mutates and applies a filter that converts the given drawable to a Gray/corresponding shaded
     * image.
     * @return a mutated version of the given drawable with a color filter applied.
     */

    public static Drawable convertDrawableToGrayScale(Drawable drawable, Button button,Context c) {
        if (drawable == null)
            return null;

        Drawable res = drawable.mutate();
        res.setColorFilter(Color.parseColor("#ad9390"),PorterDuff.Mode.SRC_IN);
        button.setTextColor(c.getResources().getColor(R.color.disabled_button_color));
        return res;
    }

}
