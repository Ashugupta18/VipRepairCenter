package com.vip.android.viptechnician;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.vip.android.viptechnician.adapters.ReportsListAdapter;
import com.vip.android.viptechnician.beans.ReportsBean;
import com.vip.android.viptechnician.util.VIPConstants;
import com.vip.android.viptechnician.util.VIPFunctions;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsActivity extends AppCompatActivity
{
    ListView reports_listview;
    ProgressDialog prgDialog;
    SharedPreferences vipSharedPreferences;
    String userId;
    StringBuffer strBuff;
    URL url;
    BufferedReader in;
    List<NameValuePair> nameValuePairs;
    JSONObject mainObj,innerObj, custDetailsObj;
    JSONArray reportsArray;
    String version="",filePath;
    int i=0;
    AlertDialog networkAlertDialog, updateAlertDialog;
    ArrayList<ReportsBean> reportsBeanArrayList = new ArrayList<>();
    ReportsListAdapter reportsListAdapter;
    LinearLayout listview_header;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    AlertDialog permissionAlertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        reports_listview = (ListView) findViewById(R.id.reports_listview);
        listview_header = (LinearLayout) findViewById(R.id.listview_header);
        prgDialog= new ProgressDialog(this);
        vipSharedPreferences = getSharedPreferences(VIPConstants.PREFS_NAME,0);


        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionCode+"";

            System.out.println("version code : "+version);
            System.out.println("getPackageName : "+getApplicationContext().getPackageName());

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        if(checkAndRequestPermissions())
        {
            // carry on the normal flow, as the case of  permissions  granted.
        }


    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!VIPFunctions.isNetworkAvailable(ReportsActivity.this)) {
            AlertDialog.Builder networkAlertDialogBuilder = new AlertDialog.Builder(ReportsActivity.this);

            // set title
            networkAlertDialogBuilder.setTitle("Network Status");

            // set dialog message
            networkAlertDialogBuilder
                    .setMessage("Please check your internet connection")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                        }
                    });


            // create alert dialog
            networkAlertDialog = networkAlertDialogBuilder.create();

            if (!networkAlertDialog.isShowing())// show it
                networkAlertDialog.show();

        }

        /*AppUpdater appUpdater = new AppUpdater(this);
        appUpdater.setDisplay(Display.DIALOG);
        appUpdater.setCancelable(false);
        appUpdater.setUpdateFrom(UpdateFrom.GOOGLE_PLAY);
        appUpdater.setButtonDoNotShowAgain(null);
        appUpdater.setContentOnUpdateAvailable("A new version of this app is available on play store! Would you like to update to it?");
        // appUpdater.showAppUpdated(true);
        appUpdater.start();*/

        if (VIPFunctions.isNetworkAvailable(this)) {
            new GetReportsData().execute(VIPConstants.SERVER_URL+
                    "/RepairPointApi/GetRPReport/?repairpointid="+
                    vipSharedPreferences.getString(VIPConstants.USER_ID,""));
            //getCategories();
        } else {
            Toast toast = Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }


    class CheckVersion extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgDialog= new ProgressDialog(ReportsActivity.this);

            prgDialog.setMessage("Please wait ...");
            prgDialog.setCancelable(false);

            if (!prgDialog.isShowing())
                prgDialog.show();

        }

        @Override
        protected String doInBackground(String... f_url) {

            StringBuffer strBuff = new StringBuffer();
            try {
                // Create a URL for the desired page
                URL url = new URL(f_url[0]);

                // Read all the text returned by the server
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String str;
                while ((str = in.readLine()) != null) {
                    strBuff.append(str);
                }
                in.close();
            }catch (IOException e) {


            }


            return strBuff.toString();

        }


        @Override
        protected void onPostExecute(String jsonString)
        {

            prgDialog.dismiss();

            System.out.println("@#@#@#@#jsonString = "+jsonString);


            try
            {
                mainObj = new JSONObject(jsonString);

                if(mainObj.getInt("code")==1)
                {
                    AlertDialog.Builder updateAlertDialogBuilder = new AlertDialog.Builder(ReportsActivity.this);

                    // set title
                    updateAlertDialogBuilder.setTitle("New Update Available");

                    // set dialog message
                    updateAlertDialogBuilder
                            .setMessage("A new version of this app is available on playstore. Would you like to download it? ")
                            .setCancelable(false)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {

                                    dialog.dismiss();

                                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }

                                    /*Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();*/

                                }
                            });


                    // create alert dialog
                    updateAlertDialog = updateAlertDialogBuilder.create();

                    if (!updateAlertDialog.isShowing())// show it
                        updateAlertDialog.show();


                }
                else
                {
                    /*Toast toast = Toast.makeText(HomeNavDrawerActivity.this,
                            "No updates yet",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();*/
                }

                System.out.println("VIPConstants.USER_ID = "+
                        vipSharedPreferences.getString(VIPConstants.USER_ID,""));

                new GetReportsData().execute(VIPConstants.SERVER_URL+
                        "/RepairPointApi/GetRPReport/?repairpointid="+
                        vipSharedPreferences.getString(VIPConstants.USER_ID,""));


            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }


    class GetReportsData extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            prgDialog.setMessage("Please wait ...");
            prgDialog.setCancelable(false);

            if (!prgDialog.isShowing())
                prgDialog.show();

        }

        @Override
        protected String doInBackground(String... f_url) {

            StringBuffer strBuff = new StringBuffer();
            try {
                // Create a URL for the desired page
                URL url = new URL(f_url[0]);

                // Read all the text returned by the server
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String str;
                while ((str = in.readLine()) != null) {
                    strBuff.append(str);
                }
                in.close();
            }catch (IOException e) {


            }


            return strBuff.toString();

        }


        @Override
        protected void onPostExecute(String jsonString)
        {

            prgDialog.dismiss();

            System.out.println("@#@#@#@#report data jsonString = "+jsonString);

            try
            {
                mainObj = new JSONObject(jsonString);

                if(mainObj.getInt("statuscode")==0)
                {
                    reportsArray = mainObj.getJSONArray("GetRPReport");

                    if(reportsArray.length()>0)
                    {
                        reportsBeanArrayList.clear();

                        for(i=0;i<reportsArray.length();i++)
                        {
                            innerObj = reportsArray.getJSONObject(i);

                            ReportsBean reportsBean = new ReportsBean();

                            reportsBean.setTicket_no(innerObj.getInt("Ticketno")+"");
                            reportsBean.setCp_name(innerObj.getString("CP"));
                            reportsBean.setIssue(innerObj.getString("issue"));
                            reportsBean.setPayment(innerObj.getString("Payment"));
                            reportsBean.setAmount(innerObj.getString("Amount"));
                            reportsBean.setTicket_status(innerObj.getString("Ticket_Status"));

                            reportsBeanArrayList.add(reportsBean);
                        }

                        reportsListAdapter = new ReportsListAdapter(ReportsActivity.this,
                                reportsBeanArrayList);
                        reports_listview.setAdapter(reportsListAdapter);

                    }
                    else
                    {
                        Toast toast = Toast.makeText(ReportsActivity.this,
                                "No reports found",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                }
                else
                {
                    Toast toast = Toast.makeText(ReportsActivity.this,
                            mainObj.getString("statusmessage"),Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report_download_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save_as_pdf)
        {
            if(checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED)
            {
                downloadImageAsPdf();
            }
            else
            {
                checkAndRequestPermissions();
            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void downloadImageAsPdf()
    {

        int allitemsheight = 0;
        java.util.List<Bitmap> bmps = new ArrayList<Bitmap>();

        View listviewHeaderView = listview_header;
        listviewHeaderView.measure(
                View.MeasureSpec.makeMeasureSpec(reports_listview.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        listviewHeaderView.layout(0, 0, listviewHeaderView.getMeasuredWidth(), listviewHeaderView.getMeasuredHeight());
        listviewHeaderView.setDrawingCacheEnabled(true);
        listviewHeaderView.buildDrawingCache();
        bmps.add(listviewHeaderView.getDrawingCache());
        allitemsheight += listviewHeaderView.getMeasuredHeight();

        for (i = 0; i < reportsBeanArrayList.size(); i++) {
            View childView = reportsListAdapter.getView(i, null, reports_listview);
            childView.measure(
                    View.MeasureSpec.makeMeasureSpec(reports_listview.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
            childView.setDrawingCacheEnabled(true);
            childView.buildDrawingCache();
            bmps.add(childView.getDrawingCache());
            allitemsheight += childView.getMeasuredHeight();
        }
        Bitmap bigbitmap = Bitmap.createBitmap(reports_listview.getMeasuredWidth(), allitemsheight,
                Bitmap.Config.ARGB_8888);
        Canvas bigcanvas = new Canvas(bigbitmap);
        Paint paint = new Paint();
        int iHeight = 0;
        i=0;
        for (i = 0; i < bmps.size(); i++) {
            Bitmap bmp = bmps.get(i);
            bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
            iHeight += bmp.getHeight();


            bmp = null;


        }

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        storeImage(bigbitmap, new SimpleDateFormat("dd MM yyyy HH:mm:ss" ).format(c.getTime()) +".jpg");

    }


    public boolean storeImage(Bitmap imageData, String filename)
    {
        // get path to external storage (SD card)
        File sdIconStorageDir = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/vip_repair_center/");
        // create storage directories, if they don't exist
        sdIconStorageDir.mkdirs();
        try {
            //filePath=null;
            filePath = sdIconStorageDir.toString() + File.separator + filename;
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            Toast.makeText(ReportsActivity.this, "Image Saved at----" + filePath, Toast.LENGTH_LONG).show();
            // choose another format if PNG doesn't suit you
            imageData.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();


            Document document=new Document();
            PdfWriter.getInstance(document,new FileOutputStream(filePath+".pdf"));
            document.open();
            Image image = Image.getInstance (filePath);
            float width = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
            float height = document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin();
            image.scaleToFit(width, height);

           /*
            float wscaler = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
            float hscaler = ((document.getPageSize().getHeight() - document.topMargin()
                    - document.bottomMargin() - 0) / image.getHeight()) * 100; // 0 means you have no indentation. If you have any, change it.

            image.scalePercent(wscaler,hscaler);*/


            image.setAlignment(Image.ALIGN_CENTER);
            document.add(image);
            document.close();
            System.out.println("sdcard : "+filePath+".pdf");



        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }



        return true;
    }



    private  boolean checkAndRequestPermissions()
    {

        int writestoragePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();


        if (writestoragePermission != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);

            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        System.out.println("Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                // perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if ( perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        System.out.println( "camera and storage permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        System.out.println( "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        {

                            AlertDialog.Builder permissionAlertDialogBuilder = new AlertDialog.Builder(this);

                            // set title
                            permissionAlertDialogBuilder.setTitle("Permissions Needed");

                            // set dialog message
                            permissionAlertDialogBuilder
                                    .setMessage("Storage permission required for this app")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            checkAndRequestPermissions();
                                            dialog.dismiss();

                                        }
                                    }).
                                    setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            dialog.dismiss();
                                            //finish();
                                        }
                                    });


                            // create alert dialog
                            permissionAlertDialog = permissionAlertDialogBuilder.create();

                            if (!permissionAlertDialog.isShowing())// show it
                                permissionAlertDialog.show();


                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else
                        {
                            Toast toast = Toast.makeText(ReportsActivity.this,
                                    "Go to settings and enable permissions", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER| Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();

                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }

                }
            }
        }


    }

}
