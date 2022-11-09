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
import androidx.cardview.widget.CardView;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vip.android.viptechnician.beans.CategoryBean;
import com.vip.android.viptechnician.util.MonserattTextViewRegular;
import com.vip.android.viptechnician.util.MonserattTextview;
import com.vip.android.viptechnician.util.VIPConstants;
import com.vip.android.viptechnician.util.VIPFunctions;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {

    /*RecyclerView recyclerCategory;
    CategoryRecyclerAdapter recyclerAdapter;
    List<CategoryBean> categoryBeanList= new ArrayList<>();*/
    CardView ticket_cardview;
    MonserattTextViewRegular buttonPlanner;
    MonserattTextview reports;
    ProgressDialog progressDialog;
    SharedPreferences vipSharedPreferences;
    SharedPreferences.Editor editor;
    String userId, userName, categoryId = "2";
    MonserattTextview textTicketCount, textTicketAmount, textCloseCount, textUserName;
    MonserattTextview text_ticket_count_unsold;
    ;
    public static final String URL_CATEGORY = VIPConstants.SERVER_URL + "/RepairPointApi/GetCategoryRepair";

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
        setContentView(R.layout.activity_categories);

        // recyclerCategory= (RecyclerView) findViewById(R.id.category_recyclerview);
        textTicketCount = (MonserattTextview) findViewById(R.id.text_ticket_count);
        textTicketAmount = (MonserattTextview) findViewById(R.id.text_ticket_amount);
        textCloseCount = (MonserattTextview) findViewById(R.id.text_closed_ticket_count);
        textUserName = (MonserattTextview) findViewById(R.id.user_name);
        reports = (MonserattTextview) findViewById(R.id.reports);
        ticket_cardview = (CardView) findViewById(R.id.ticket_cardview);
        text_ticket_count_unsold = findViewById(R.id.text_ticket_count_unsold);
        // buttonPlanner= (MonserattTextViewRegular) findViewById(R.id.button_planner);

        prgDialog = new ProgressDialog(this);
        /*categoryBeanList.clear();
        CategoryBean categoryBean= new CategoryBean();
        categoryBean.setCategory_name("Repairing");
        categoryBeanList.add(categoryBean);

        CategoryBean categoryBean1= new CategoryBean();
        categoryBean1.setCategory_name("Unsold Product");
        categoryBeanList.add(categoryBean1);*/
        vipSharedPreferences = getSharedPreferences(VIPConstants.PREFS_NAME, 0);
        editor = vipSharedPreferences.edit();

        userId = vipSharedPreferences.getString(VIPConstants.USER_ID, "");
        userName = vipSharedPreferences.getString(VIPConstants.USER_NAME, "");


        textUserName.setText(userName);
        /*buttonPlanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(getApplicationContext(),PlannarActivity.class);
                startActivity(intent);
            }
        });*/
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName + "";

            System.out.println("version code : " + version);
            System.out.println("getPackageName : " + getApplicationContext().getPackageName());

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CategoriesActivity.this
                        , ReportsActivity.class));
            }
        });

        ticket_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productEdit = new Intent(CategoriesActivity.this,
                        CPListActivity.class);
                startActivity(productEdit);
            }
        });

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

                if (mainObj.getInt("statuscode") == 6) {
                    AlertDialog.Builder loginAlertDialogBuilder = new AlertDialog.Builder(CategoriesActivity.this);

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
                                       // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));

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
                    getTicketCount();
                }

                // getCategories();
               // getTicketCount();

            } catch (Exception e) {
                e.printStackTrace();
            }


        }


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
            new CheckVersion().execute(VIPConstants.SERVER_URL+
                    "RepairPointApi/VersionUpdateAPI?AppID="+"3"+"&AppVersion="+version);

            //getCategories();
        } else {
            Toast toast = Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void getTicketCount() {

        showProgress();
        //System.out.println("ticketno="+ticketNo+"&categoryid="+categoryId);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                VIPConstants.SERVER_URL + "/RepairPointApi/GetTicketCount?repairpointid=" + userId + "&categoryid=" + categoryId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (VIPFunctions.isJsonValid(response)) {

                            //Toast.makeText(ViewMore.this,response,Toast.LENGTH_LONG).show();
                            JSONObject jsonObj;
                            JSONArray jsonArray;
                            System.out.println("response of GetTicketCount" + response);
                            progressDialog.dismiss();
                            try {
                                jsonObj = new JSONObject(response);
                                int statusCode = jsonObj.getInt("statuscode");
                                String statusMessage = jsonObj.getString("statusmessage");
                                JSONObject object;
                                if (statusCode == 0) {

                                    String ticketCount = jsonObj.getString("ticketcount");
                                    String amount = jsonObj.getString("amount");
                                    String closeCount = jsonObj.getString("closedticketcount");
                                    String ticketCountUnsold= jsonObj.getString("ticketcountunsold");
                                    textTicketCount.setText("Open Ticket Count Sold :" + " " + ticketCount);
                                    textTicketAmount.setText("Ticket Amount :" + " " + amount);
                                    textCloseCount.setText("Closed Ticket Count :" + " " + closeCount);

                                    text_ticket_count_unsold.setText("Open Ticket Count Unsold : "+ticketCountUnsold);

                                    //jsonArray = jsonObj.getJSONArray("RepairList");


                                } else {
                                    Toast.makeText(getApplicationContext(), statusMessage, Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_out_attendance) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //Setting message manually and performing action on button click
            builder.setMessage("Do you really want to Log out ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            editor.putString(VIPConstants.USER_ID, "");
                            editor.commit();

                            startActivity(new Intent(CategoriesActivity.this, MainActivity.class));
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
                            dialog.cancel();
                        }
                    });

            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Message");
            alert.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getCategories() {

        showProgress();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_CATEGORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (VIPFunctions.isJsonValid(response)) {

                            // Toast.makeText(ViewMore.this,response,Toast.LENGTH_LONG).show();
                            JSONObject jsonObj;
                            JSONArray jsonArray;
                            System.out.println("response of loginServer" + response);
                            progressDialog.dismiss();
                            //  categoryBeanList.clear();
                            try {
                                jsonObj = new JSONObject(response);
                                int statusCode = jsonObj.getInt("statuscode");
                                String statusMessage = jsonObj.getString("statusmessage");
                                JSONObject object;
                                if (statusCode == 0) {
                                    jsonArray = jsonObj.getJSONArray("categorylist");
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        object = jsonArray.getJSONObject(i);
                                        CategoryBean categoryBean = new CategoryBean();
                                        categoryBean.setCategory_id(object.getString("id"));
                                        categoryBean.setCategory_name(object.getString("name"));
                                        //  categoryBeanList.add(categoryBean);
                                    }

                                   /* recyclerAdapter = new CategoryRecyclerAdapter(getApplicationContext(),categoryBeanList);
                                    int numberOfcolumn = 2;
                                    recyclerCategory.setLayoutManager(new GridLayoutManager(getApplicationContext(), numberOfcolumn, GridLayoutManager.VERTICAL, false));
                                    recyclerCategory.setItemAnimator(new DefaultItemAnimator());
                                    recyclerCategory.setNestedScrollingEnabled(false);
                                    recyclerCategory.setAdapter(recyclerAdapter);*/

                                } else {
                                    Toast.makeText(getApplicationContext(), statusMessage, Toast.LENGTH_LONG).show();
                                }

                                getTicketCount();

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

        progressDialog = new ProgressDialog(CategoriesActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

}
