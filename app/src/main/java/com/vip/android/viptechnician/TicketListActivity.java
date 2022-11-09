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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vip.android.viptechnician.adapters.TicketAdapter;
import com.vip.android.viptechnician.beans.TicketBean;
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

public class TicketListActivity extends AppCompatActivity {

    RecyclerView recyclerTicket;
    ArrayList<TicketBean> ticketBeanArrayList = new ArrayList<>();
    TicketAdapter ticketAdapter;
    ProgressDialog progressDialog;
    String userId/*,categoryId=""*/;
    SharedPreferences vipSharedPreferences;
    SharedPreferences.Editor editor;
    String cpid;
    LinearLayout ticketabsentlay;
    public static final String URL_TICKET_LIST = VIPConstants.SERVER_URL + "/RepairPointApi/GetTicketRepairPoint";

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
        setContentView(R.layout.activity_ticket_list);

        getSupportActionBar().setTitle("Tickets List");
        recyclerTicket = (RecyclerView) findViewById(R.id.ticket_list_recyclerview);

        cpid = getIntent().getStringExtra("cpid");
        // categoryId= getIntent().getStringExtra("categoryid");
        ticketabsentlay = (LinearLayout) findViewById(R.id.ticketabsentlay);
        vipSharedPreferences = getSharedPreferences(VIPConstants.PREFS_NAME, 0);
        editor = vipSharedPreferences.edit();

        prgDialog = new ProgressDialog(this);
        userId = vipSharedPreferences.getString(VIPConstants.USER_ID, "");
       /* ticketBeanArrayList.clear();
        TicketBean ticketBean= new TicketBean();
        ticketBean.setName("Abhiraj Shetye");
        ticketBean.setAddress("Address line,address line,address line");
        ticketBean.setPhone("022-1234567");
        ticketBeanArrayList.add(ticketBean);

        TicketBean ticketBean1= new TicketBean();
        ticketBean1.setName("Abhiraj Shetye");
        ticketBean1.setAddress("Address line,address line,address line");
        ticketBean1.setPhone("022-1234567");
        ticketBeanArrayList.add(ticketBean1);

        TicketBean ticketBean2= new TicketBean();
        ticketBean2.setName("Abhiraj Shetye");
        ticketBean2.setAddress("Address line,address line,address line");
        ticketBean2.setPhone("022-1234567");
        ticketBeanArrayList.add(ticketBean2);*/
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionCode + "";

            System.out.println("version code : " + version);
            System.out.println("getPackageName : " + getApplicationContext().getPackageName());

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (VIPFunctions.isNetworkAvailable(this)) {
            //getTicketList();

            getReplacementTicketList();
           /* AppUpdater appUpdater = new AppUpdater(this);
            appUpdater.setDisplay(Display.DIALOG);
            appUpdater.setCancelable(false);
            appUpdater.setUpdateFrom(UpdateFrom.GOOGLE_PLAY);
            appUpdater.setButtonDoNotShowAgain(null);
            appUpdater.setContentOnUpdateAvailable("A new version of this app is available on play store! Would you like to update to it?");
            // appUpdater.showAppUpdated(true);
            appUpdater.start();*/
        } else {
            Toast toast = Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }



    private void getReplacementTicketList() {

        showProgress();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                VIPConstants.SERVER_URL + "/RepairPointApi/GetTicketRepairPoint?repairid="
                        + userId + "&categoryid=1" + "&cpid=" + cpid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (VIPFunctions.isJsonValid(response)) {

                            JSONObject jsonObj;
                            JSONArray jsonArray;
                            System.out.println("response of loginServer" + response);
                            progressDialog.dismiss();
                            ticketBeanArrayList.clear();
                            try {
                                jsonObj = new JSONObject(response);
                                int statusCode = jsonObj.getInt("statuscode");
                                String statusMessage = jsonObj.getString("statusmessage");
                                JSONObject object;
                                if (statusCode == 0) {
                                    jsonArray = jsonObj.getJSONArray("ticketlist");

                                    if (jsonArray.length() > 0) {
                                        ticketabsentlay.setVisibility(LinearLayout.GONE);
                                        recyclerTicket.setVisibility(RecyclerView.VISIBLE);

                                        for (int i = 0; i < jsonArray.length(); i++) {

                                            object = jsonArray.getJSONObject(i);
                                            TicketBean ticketBean = new TicketBean();
                                            ticketBean.setTicketNo(object.getString("ticketno"));
                                            ticketBean.setBarcode(object.getString("barcode"));
                                            ticketBean.setDefect(object.getString("defect"));
                                            ticketBean.setDefectImage(object.getString("defectimage1"));
                                            ticketBean.setName(object.getString("CustomerName"));
                                            ticketBean.setPhone(object.getString("CustomerPhone"));
                                            ticketBean.setAddress(object.getString("CustomerAddress"));
                                            ticketBean.setProductName(object.getString("ProductName"));
                                            ticketBean.setModelName(object.getString("ModelName"));
                                            ticketBean.setDefect(object.getString("defect"));
                                            ticketBean.setTicketStatus(object.getString("Is_Open"));
                                            ticketBean.setPickupotp(object.getString("pickupotp"));
                                            ticketBean.setAllowwork(object.getString("allowwork"));
                                            ticketBean.setRepair_replacement("1"); // 1 for replacement
                                            ticketBean.setSl_category(object.getString("uCategory")); // check only if category is unsold

                                            ticketBean.setSpareRequest(object.getString("SpairRequest"));
                                            ticketBean.setReplacementRequest(object.getString("ReplacementRequest"));
                                            ticketBean.setWarrantyImage(object.getString("Warranty_Se_Rp"));
                                            ticketBean.setBillImage(object.getString("Bill_Se_Rp"));
                                            ticketBean.setHandlingDamageRequest(object.getString("HandlingDamageRequest"));

                                            if (object.isNull("Verify_Barcode")) {
                                                System.out.println("check null ticket no = " + object.getString("ticketno"));
                                                System.out.println("check null Verify_Barcode = " + object.getString("Verify_Barcode"));
                                                ticketBean.setVerified_barcode("");
                                            } else {
                                                ticketBean.setVerified_barcode(object.getInt("Verify_Barcode") + "");
                                            }

                                            if (object.isNull("Warranty_Type")) {
                                                ticketBean.setWarranty_type("");
                                            } else {
                                                ticketBean.setWarranty_type(object.getString("Warranty_Type"));
                                            }
                                            if(object.isNull("category"))
                                            {
                                                ticketBean.setCategory_Type("");
                                            }
                                            else
                                            {
                                                ticketBean.setCategory_Type(object.getString("category"));
                                            }

                                            if(object.isNull("invoice_date"))
                                            {
                                                ticketBean.setInvoice_date("");
                                            }
                                            else
                                            {
                                                ticketBean.setInvoice_date(object.getString("invoice_date"));
                                            }


                                            ticketBeanArrayList.add(ticketBean);
                                        }

                                        ticketAdapter = new TicketAdapter(getApplicationContext(), ticketBeanArrayList,"1");
                                        int numberOfcolumn = 1;
                                        recyclerTicket.setLayoutManager(new GridLayoutManager(getApplicationContext(),
                                                numberOfcolumn, GridLayoutManager.VERTICAL, false));
                                        recyclerTicket.setItemAnimator(new DefaultItemAnimator());
                                        recyclerTicket.setAdapter(ticketAdapter);
                                        recyclerTicket.setNestedScrollingEnabled(false);

                                    } else {
                                        if (ticketBeanArrayList.size() <= 0) {
                                            ticketabsentlay.setVisibility(LinearLayout.VISIBLE);
                                            recyclerTicket.setVisibility(RecyclerView.GONE);
                                        }
                                      /*  ticketabsentlay.setVisibility(LinearLayout.VISIBLE);
                                        recyclerTicket.setVisibility(RecyclerView.GONE);*/
                                    }


                                } else {
                                    Toast.makeText(getApplicationContext(), statusMessage, Toast.LENGTH_LONG).show();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "There is some technical issue at server\nSorry for the inconvenience",
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                        getRepairTicketList();

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

    private void getRepairTicketList() {

        showProgress();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                VIPConstants.SERVER_URL + "/RepairPointApi/GetTicketRepairPoint?repairid="
                        + userId + "&categoryid=2" + "&cpid=" + cpid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (VIPFunctions.isJsonValid(response)) {

                            JSONObject jsonObj;
                            JSONArray jsonArray;
                            System.out.println("response of loginServer" + response);
                            progressDialog.dismiss();
                            // ticketBeanArrayList.clear();
                            try {
                                jsonObj = new JSONObject(response);
                                int statusCode = jsonObj.getInt("statuscode");
                                String statusMessage = jsonObj.getString("statusmessage");
                                JSONObject object;
                                if (statusCode == 0) {
                                    jsonArray = jsonObj.getJSONArray("ticketlist");

                                    if (jsonArray.length() > 0) {
                                        ticketabsentlay.setVisibility(LinearLayout.GONE);
                                        recyclerTicket.setVisibility(RecyclerView.VISIBLE);

                                        for (int i = 0; i < jsonArray.length(); i++) {

                                            object = jsonArray.getJSONObject(i);
                                            TicketBean ticketBean = new TicketBean();
                                            ticketBean.setTicketNo(object.getString("ticketno"));
                                            ticketBean.setBarcode(object.getString("barcode"));
                                            ticketBean.setDefect(object.getString("defect"));
                                            ticketBean.setDefectImage(object.getString("defectimage1"));
                                            ticketBean.setName(object.getString("CustomerName"));
                                            ticketBean.setPhone(object.getString("CustomerPhone"));
                                            ticketBean.setAddress(object.getString("CustomerAddress"));
                                            ticketBean.setProductName(object.getString("ProductName"));
                                            ticketBean.setModelName(object.getString("ModelName"));
                                            ticketBean.setDefect(object.getString("defect"));
                                            ticketBean.setTicketStatus(object.getString("Is_Open"));
                                            ticketBean.setPickupotp(object.getString("pickupotp"));
                                            ticketBean.setAllowwork(object.getString("allowwork"));
                                            ticketBean.setRepair_replacement("2"); // 2 for repair

                                            ticketBean.setSpareRequest(object.getString("SpairRequest"));
                                            ticketBean.setReplacementRequest(object.getString("ReplacementRequest"));
                                            ticketBean.setWarrantyImage(object.getString("Warranty_Se_Rp"));
                                            ticketBean.setBillImage(object.getString("Bill_Se_Rp"));
                                            ticketBean.setHandlingDamageRequest(object.getString("HandlingDamageRequest"));

                                            ticketBean.setSl_category(object.getString("uCategory"));

                                            if (object.isNull("Verify_Barcode")) {
                                                System.out.println("check null ticket no = " + object.getString("ticketno"));
                                                System.out.println("check null Verify_Barcode = " + object.getString("Verify_Barcode"));
                                                ticketBean.setVerified_barcode("");
                                            } else {
                                                ticketBean.setVerified_barcode(object.getInt("Verify_Barcode") + "");
                                            }

                                            if (object.isNull("Warranty_Type")) {
                                                ticketBean.setWarranty_type("");
                                            } else {
                                                ticketBean.setWarranty_type(object.getString("Warranty_Type"));
                                            }
                                            if(object.isNull("category"))
                                            {
                                                ticketBean.setCategory_Type("");
                                            }
                                            else
                                            {
                                                ticketBean.setCategory_Type(object.getString("category"));
                                            }

                                            if(object.isNull("invoice_date"))
                                            {
                                                ticketBean.setInvoice_date("");
                                            }
                                            else
                                            {
                                                ticketBean.setInvoice_date(object.getString("invoice_date"));
                                            }


                                            ticketBeanArrayList.add(ticketBean);
                                        }

                                        if (ticketAdapter == null) {
                                            ticketAdapter = new TicketAdapter(getApplicationContext(), ticketBeanArrayList,"2");
                                            int numberOfcolumn = 1;
                                            recyclerTicket.setLayoutManager(new GridLayoutManager(getApplicationContext(),
                                                    numberOfcolumn, GridLayoutManager.VERTICAL, false));
                                            recyclerTicket.setItemAnimator(new DefaultItemAnimator());
                                            recyclerTicket.setAdapter(ticketAdapter);
                                            recyclerTicket.setNestedScrollingEnabled(false);
                                        } else {
                                            ticketAdapter.notifyDataSetChanged();
                                        }


                                       /* ticketAdapter = new TicketAdapter(getApplicationContext(),ticketBeanArrayList,categoryId);
                                        int numberOfcolumn = 1;
                                        recyclerTicket.setLayoutManager(new GridLayoutManager(getApplicationContext(),
                                                numberOfcolumn, GridLayoutManager.VERTICAL, false));
                                        recyclerTicket.setItemAnimator(new DefaultItemAnimator());
                                        recyclerTicket.setAdapter(ticketAdapter);
                                        recyclerTicket.setNestedScrollingEnabled(false);*/

                                    } else {
                                        if (ticketBeanArrayList.size() <= 0) {
                                            ticketabsentlay.setVisibility(LinearLayout.VISIBLE);
                                            recyclerTicket.setVisibility(RecyclerView.GONE);
                                        }
                                      /*  ticketabsentlay.setVisibility(LinearLayout.VISIBLE);
                                        recyclerTicket.setVisibility(RecyclerView.GONE);*/
                                    }


                                } else {
                                    Toast.makeText(getApplicationContext(), statusMessage, Toast.LENGTH_LONG).show();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "There is some technical issue at server\nSorry for the inconvenience",
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

        progressDialog = new ProgressDialog(TicketListActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

}
