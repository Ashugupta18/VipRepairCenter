package com.vip.android.viptechnician;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vip.android.viptechnician.adapters.CPRecyclerAdapter;
import com.vip.android.viptechnician.beans.CPBean;
import com.vip.android.viptechnician.util.VIPConstants;
import com.vip.android.viptechnician.util.VIPFunctions;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CPListActivity extends AppCompatActivity {

    RecyclerView cp_list_recyclerview;
    ArrayList<CPBean> cpBeanArrayList = new ArrayList<>();
    CPRecyclerAdapter cpRecyclerAdapter;
    ProgressDialog progressDialog;

    String userId/*,categoryId=""*/;
    SharedPreferences vipSharedPreferences;
    SharedPreferences.Editor editor;

    ProgressDialog prgDialog;
    StringBuffer strBuff;
    URL url;
    BufferedReader in;
    List<NameValuePair> nameValuePairs;
    JSONObject mainObj, innerObj, custDetailsObj;
    JSONArray messageArray;
    String version = "";
    AlertDialog loginAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cplist);

        getSupportActionBar().setTitle("CP List");
        cp_list_recyclerview = (RecyclerView) findViewById(R.id.cp_list_recyclerview);

        vipSharedPreferences = getSharedPreferences(VIPConstants.PREFS_NAME, 0);
        editor = vipSharedPreferences.edit();

        prgDialog = new ProgressDialog(this);
        //  categoryId= getIntent().getStringExtra("categoryid");
        // userId= vipSharedPreferences.getString(VIPConstants.USER_ID,"");

        /*cpBeanArrayList.clear();
        CPBean cpBean= new CPBean();
        cpBean.setCp_id("Ca 123456789");
        cpBean.setCp_name("Abhiraj Shetye");
        cpBean.setCp_addr("Address line,address line,address line");
        cpBean.setCp_phone("022-1234567");
        cpBeanArrayList.add(cpBean);

        CPBean cpBean1= new CPBean();
        cpBean1.setCp_id("Ca 123456789");
        cpBean1.setCp_name("Abhiraj Shetye");
        cpBean1.setCp_addr("Address line,address line,address line");
        cpBean1.setCp_phone("022-1234567");
        cpBeanArrayList.add(cpBean1);

        CPBean cpBean2= new CPBean();
        cpBean2.setCp_id("Ca 123456789");
        cpBean2.setCp_name("Abhiraj Shetye");
        cpBean2.setCp_addr("Address line,address line,address line");
        cpBean2.setCp_phone("022-1234567");
        cpBeanArrayList.add(cpBean2);*/

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionCode + "";

            System.out.println("version code : " + version);
            System.out.println("getPackageName : " + getApplicationContext().getPackageName());

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        userId = vipSharedPreferences.getString(VIPConstants.USER_ID, "");


    }

    class CheckVersion extends AsyncTask<String, Void, String> {
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
            } catch (IOException e) {


            }


            return strBuff.toString();

        }


        @Override
        protected void onPostExecute(String jsonString) {

            prgDialog.dismiss();

            System.out.println("@#@#@#@#jsonString = " + jsonString);


            try {
                mainObj = new JSONObject(jsonString);

                if (mainObj.getInt("code") == 1) {
                    AlertDialog.Builder loginAlertDialogBuilder = new AlertDialog.Builder(CPListActivity.this);

                    // set title
                    loginAlertDialogBuilder.setTitle("New Update Available");

                    // set dialog message
                    loginAlertDialogBuilder
                            .setMessage("A new version of this app is available on playstore. Would you like to download it? ")
                            .setCancelable(false)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

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
                    loginAlertDialog = loginAlertDialogBuilder.create();

                    if (!loginAlertDialog.isShowing())// show it
                        loginAlertDialog.show();


                } else {
                    /*Toast toast = Toast.makeText(HomeNavDrawerActivity.this,
                            "No updates yet",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();*/
                }

                getCpList();

            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }

    private void getCpList() {

        showProgress();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                VIPConstants.SERVER_URL + "/RepairPointApi/GetCpList?repairid=" + userId + "&categoryid=1",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (VIPFunctions.isJsonValid(response)) {
                            // Toast.makeText(ViewMore.this,response,Toast.LENGTH_LONG).show();
                            JSONObject jsonObj;
                            JSONArray jsonArray;
                            System.out.println("response of loginServer" + response);
                            progressDialog.dismiss();
                            cpBeanArrayList.clear();
                            try {
                                jsonObj = new JSONObject(response);
                                int statusCode = jsonObj.getInt("statuscode");
                                String statusMessage = jsonObj.getString("statusmessage");
                                JSONObject object;
                                if (statusCode == 0) {
                                    jsonArray = jsonObj.getJSONArray("cplist");
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        object = jsonArray.getJSONObject(i);
                                        CPBean cpBean = new CPBean();
                                        cpBean.setCp_id(object.getString("id"));
                                        cpBean.setSap_code(object.getString("sapcode"));
                                        cpBean.setCp_name(object.getString("name"));
                                        cpBean.setCp_addr(object.getString("address"));
                                        cpBean.setCp_phone(object.getString("contactno"));
                                        cpBean.setTicketCountOpen(object.getString("ticketcount"));
                                        cpBeanArrayList.add(cpBean);

                                    }

                                    cpRecyclerAdapter = new CPRecyclerAdapter(getApplicationContext(), cpBeanArrayList/*,categoryId*/);
                                    int numberOfcolumn = 1;
                                    cp_list_recyclerview.setLayoutManager(new GridLayoutManager(getApplicationContext(), numberOfcolumn, GridLayoutManager.VERTICAL, false));
                                    cp_list_recyclerview.setItemAnimator(new DefaultItemAnimator());
                                    cp_list_recyclerview.setAdapter(cpRecyclerAdapter);
                                    cp_list_recyclerview.setNestedScrollingEnabled(false);

                                } else {
                                    Toast.makeText(getApplicationContext(), statusMessage, Toast.LENGTH_LONG).show();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    R.string.invalid_json_response,
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }) {

          /*  @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("technicianid",userId);
                params.put("categoryid",categoryId);
                return params;
            }*/

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        500000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
    }

    private void showProgress() {

        progressDialog = new ProgressDialog(CPListActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    @Override
    protected void onResume() {
        super.onResume();

        /*AppUpdater appUpdater = new AppUpdater(this);
        appUpdater.setDisplay(Display.DIALOG);
        appUpdater.setCancelable(false);
        appUpdater.setUpdateFrom(UpdateFrom.GOOGLE_PLAY);
        appUpdater.setButtonDoNotShowAgain(null);
        appUpdater.setContentOnUpdateAvailable("A new version of this app is available on play store! Would you like to update to it?");
        // appUpdater.showAppUpdated(true);
        appUpdater.start();*/

        if (VIPFunctions.isNetworkAvailable(this)) {
            getCpList();
        } else {
            Toast toast = Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }


    }


}
