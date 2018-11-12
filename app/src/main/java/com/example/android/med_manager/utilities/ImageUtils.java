package com.example.android.med_manager.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

/**
 * Created by SOLARIN O. OLUBAYODE on 30/07/18.
 */

public class ImageUtils {

    //create a bitmap from a file path
    public static Bitmap getBitmapFromPath(Context context, String imageFilePath){

        Bitmap image = null;
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        if(FileUtils.isImageFile(imageFilePath)) {

            try {
                image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(imageFilePath));
            }catch (IOException e){
                e.printStackTrace();
            }
            return image;
        }else {
            return null;
        }
    }

}