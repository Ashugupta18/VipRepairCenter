package com.vip.android.viptechnician.util;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Android on 10/24/2017.
 */

public class VIPFunctions
{
    private static ConnectivityManager connectivityManager;
    private static NetworkInfo activeNetworkInfo;
    private static Toast toast;


    public static boolean isNetworkAvailable(Context context)
    {

        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static String getGalleryImageType(String filePath)
    {

        System.out.println("path ="+filePath.toString());

        String temp=null;

        temp = filePath.substring(filePath.lastIndexOf(".") + 1);

        return temp;


    }
    public static void showToast(Context context, String msg)
    {
        toast = Toast.makeText(context,msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }


    public static String getRealPathFromUri(Context context, Uri contentUri)
    {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public static String getImageType(Context context, Uri filePath)
    {

        System.out.println("path ="+filePath.toString());

        String strMimeType = null,temp=null;

        strMimeType = filePath.toString();

        temp = strMimeType.substring(strMimeType.lastIndexOf(".") + 1);

        return temp;


    }


    public static boolean isJsonValid(String stringToBeTested)
    {
        try
        {
            new JSONObject(stringToBeTested);

        }
        catch (JSONException ex)
        {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try
            {
                new JSONArray(stringToBeTested);

            }
            catch (JSONException ex1)
            {
                return false;
            }
        }

        return true;
    }


    // check if the file size is < 500kb
    public static boolean checkIfSizeIsGreater(String data)
    {
        File file = new File(data);
        long length = file.length();

        System.out.println("image size = "+ length);

        if((length/1024)<500)
        {
            return false;
        }
        else
        {
            return true;
        }

    }

}
