package com.vip.android.viptechnician;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.messaging.FirebaseMessaging;
import com.vip.android.viptechnician.beans.UpdateTokenResponse;
import com.vip.android.viptechnician.util.ApiInterface;
import com.vip.android.viptechnician.util.MonserattTextViewRegular;
import com.vip.android.viptechnician.util.MonserattTextview;
import com.vip.android.viptechnician.util.MonteserattEditText;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    MonteserattEditText mobilenum, passwvalue;
    MonserattTextViewRegular loginusr, register;
    MonserattTextview forgot_password;
    private ProgressDialog prgDialog;
    StringBuffer strBuff;
    AlertDialog loginAlertDialog;
    URL url;
    BufferedReader in;
    List<NameValuePair> nameValuePairs;
    JSONObject mainObj, innerObj, custDetailsObj;
    JSONArray messageArray;
    ApiInterface apiInterface;
    SharedPreferences vipSharedPreferences;
    SharedPreferences.Editor editor;
    String mobileValue = "", enteredPassw = "", mobileNo = "", otp, newPassword = "";
    //private static LoginActivity activityinstance;
    AlertDialog networkAlertDialog;
    InputMethodManager inputManager;
    Dialog dialog;
    public static final String URL_LOGIN = VIPConstants.SERVER_URL + "RepairPointApi/Login";
    public static final String URL_PASS = VIPConstants.SERVER_URL + "/RepairPointApi/ForgotPassword";

    MonteserattEditText editMobileNo, editOtp, editNewPass, editConfirmPass;
    MonserattTextview buttonSubmitMobileNo, buttonSubmitOtp, buttonSubmitPass;
    LinearLayout linearMobileNo, linearOtpNo, linearNewPass;
    ProgressDialog progressDialog;

    String version = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        apiInterface = APIClient.getClient().create(ApiInterface.class);

        mobilenum = (MonteserattEditText) findViewById(R.id.mobilenum);
        passwvalue = (MonteserattEditText) findViewById(R.id.passwvalue);

        loginusr = (MonserattTextViewRegular) findViewById(R.id.loginusr);
        //register = (MonserattTextViewRegular) findViewById(R.id.register);
        forgot_password = (MonserattTextview) findViewById(R.id.forgot_password);

        prgDialog = new ProgressDialog(this);

        vipSharedPreferences = getSharedPreferences(VIPConstants.PREFS_NAME, 0);
        editor = vipSharedPreferences.edit();

        inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        if (vipSharedPreferences.getString(VIPConstants.USER_ID, "").length() > 0) {
          /*  inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);*/

            startActivity(new Intent(MainActivity.this, CategoriesActivity.class));
            finish();
           /* startActivity(new Intent(LoginActivity.this,ProductRegistrationActivity.class));
            finish();*/
        } else {
            if (VIPFunctions.isNetworkAvailable(MainActivity.this)) {

            } else {
                AlertDialog.Builder networkAlertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

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
        }


     /*   register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

            }
        });
*/

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                //showPassDialog();

                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://proasaw.jetair.co.in/Account/ForgotPassword"));
                if (browserIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(browserIntent);
                } else {
                    Toast toast = Toast.makeText(MainActivity.this,
                            "No browser found", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

            }
        });


        loginusr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                if (VIPFunctions.isNetworkAvailable(MainActivity.this)) {

                    if ((mobilenum.getText().length() > 0) && (passwvalue.getText().length() > 0)) {
                        mobileValue = mobilenum.getText().toString();
                        enteredPassw = passwvalue.getText().toString();

                        loginServer();
                        /*InputMethodManager inputMethodManager = (InputMethodManager)LoginActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(), 0);
                */
                     /*   startActivity(new Intent(LoginActivity.this,CategoriesActivity.class));
                        finish();*/

                        // new CheckLogin().execute(VIPConstants.SERVER_URL+"/Registration/LoginCp");

                    } else if (mobilenum.getText().length() <= 0) {
                        mobilenum.setError("Please enter your mobile number");
                        mobilenum.requestFocus();

                    } else if (passwvalue.getText().length() <= 0) {
                        passwvalue.setError("Please enter your password");
                        passwvalue.requestFocus();
                    }

                } else {
                    AlertDialog.Builder networkAlertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

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


            }
        });

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName + "";

            System.out.println("version code : " + version);
            System.out.println("getPackageName : " + getApplicationContext().getPackageName());

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //generateNotificationToken();
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
                    AlertDialog.Builder loginAlertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

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


            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }

    private void showPassDialog() {

        dialog = new Dialog(MainActivity.this, R.style.cust_dialog);
        dialog.setContentView(R.layout.custom_dialog_password);
        dialog.setTitle("Enter Mobile No.");

        editMobileNo = (MonteserattEditText) dialog.findViewById(R.id.edit_mobile_no);
        editOtp = (MonteserattEditText) dialog.findViewById(R.id.edit_otp_no);
        editNewPass = (MonteserattEditText) dialog.findViewById(R.id.edit_new_pass);
        editConfirmPass = (MonteserattEditText) dialog.findViewById(R.id.edit_confirm_pass);
        buttonSubmitMobileNo = (MonserattTextview) dialog.findViewById(R.id.text_submit_no);
        buttonSubmitOtp = (MonserattTextview) dialog.findViewById(R.id.text_submit_otp_no);
        buttonSubmitPass = (MonserattTextview) dialog.findViewById(R.id.text_submit_pass);
        linearMobileNo = (LinearLayout) dialog.findViewById(R.id.linear_mobile_no);
        linearNewPass = (LinearLayout) dialog.findViewById(R.id.linear_new_pass);
        linearOtpNo = (LinearLayout) dialog.findViewById(R.id.linear_otp_no);

        buttonSubmitMobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mobileNo = editMobileNo.getText().toString();
                editOtp.setFocusable(true);
                if (VIPFunctions.isNetworkAvailable(MainActivity.this)) {
                    sendOtp();
                } else {
                    Toast toast = Toast.makeText(MainActivity.this,
                            "Please check your internet connection", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

                //dialog.dismiss();
            }
        });

        buttonSubmitOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredOtp = editOtp.getText().toString();
                if (enteredOtp.compareTo(otp) == 0) {

                    linearOtpNo.setVisibility(View.GONE);
                    linearNewPass.setVisibility(View.VISIBLE);
                    dialog.setTitle("Enter New Password");

                } else {

                    Toast toast = Toast.makeText(MainActivity.this,
                            "Entered OTP is incorrect", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }
            }
        });

        buttonSubmitPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editNewPass.getText().toString().compareTo(editConfirmPass.getText().toString()) == 0) {

                    newPassword = editConfirmPass.getText().toString();
                    if (mobileNo.compareTo("") != 0 && newPassword.compareTo("") != 0) {
                        if (VIPFunctions.isNetworkAvailable(MainActivity.this)) {
                            updatePassword();
                        } else {
                            Toast toast = Toast.makeText(MainActivity.this,
                                    "Please check your internet connection", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                    } else {
                        Toast toast = Toast.makeText(MainActivity.this,
                                "Please enter new password", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                    }
                } else {
                    Toast toast = Toast.makeText(MainActivity.this,
                            "Password does not match", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }

            }
        });

        dialog.show();

    }

    private void updatePassword() {

        showProgress();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_PASS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (VIPFunctions.isJsonValid(response)) {

                            // Toast.makeText(ViewMore.this,response,Toast.LENGTH_LONG).show();
                            JSONObject jsonObj, jsonObjectRP;
                            System.out.println("response of forgot pass" + response);
                            progressDialog.dismiss();
                            try {
                                jsonObj = new JSONObject(response);
                                int statusCode = jsonObj.getInt("statuscode");
                                String statusMessage = jsonObj.getString("statusmessage");
                                if (statusCode == 0) {

                                    Toast toast = Toast.makeText(MainActivity.this,
                                            statusMessage, Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();

                                    dialog.dismiss();

                                } else {

                                    Toast toast = Toast.makeText(MainActivity.this,
                                            statusMessage, Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();

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
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("MobileNo", mobileNo);
                params.put("password", newPassword);
                System.out.println("response of params" + params);
                return params;
            }

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

    private void sendOtp() {

        showProgress();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                VIPConstants.SERVER_URL + "/RepairPointApi/SendOtp?MobileNo=" + mobileNo,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (VIPFunctions.isJsonValid(response)) {

                            // Toast.makeText(ViewMore.this,response,Toast.LENGTH_LONG).show();
                            JSONObject jsonObj;
                            JSONArray jsonArray;
                            System.out.println("response of send otp" + response);
                            progressDialog.dismiss();
                            //categoryBeanList.clear();
                            try {
                                jsonObj = new JSONObject(response);
                                int statusCode = jsonObj.getInt("statuscode");
                                String statusMessage = jsonObj.getString("statusmessage");
                                JSONObject object;
                                if (statusCode == 0) {

                                    otp = jsonObj.getString("otp");
                                    System.out.println("OTP" + otp);
                                    linearMobileNo.setVisibility(View.INVISIBLE);
                                    linearOtpNo.setVisibility(View.VISIBLE);
                                    dialog.setTitle("Enter Otp");

                                } else {
                                    Toast toast = Toast.makeText(MainActivity.this,
                                            statusMessage, Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();

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


    private void loginServer() {

        showProgress();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN+"?MobileNo="+mobileValue+"&AppVersion="+version+"&AppID="+"3"+"&Otp="+""+"&password="+enteredPassw,
                new Response.Listener<String>() {
                    JSONObject jsonObject;

                    @Override
                    public void onResponse(String response) {

                        if (VIPFunctions.isJsonValid(response)) {

                            // Toast.makeText(ViewMore.this,response,Toast.LENGTH_LONG).show();
                            JSONObject jsonObj, jsonObjectRP;
                            System.out.println("response of loginServer" + response);
                            progressDialog.dismiss();
                            try {
                                jsonObj = new JSONObject(response);
                                int statusCode = jsonObj.getInt("statuscode");
                                String statusMessage = jsonObj.getString("statusmessage");
                                editor.putString(VIPConstants.MASTER_ID, jsonObj.getString("user_id"));
                                if (statusCode == 0) {
                                    jsonObjectRP = jsonObj.getJSONObject("RP");
                                    editor.putString(VIPConstants.USER_ID, jsonObjectRP.getString("id"));
                                    //editor.putString(VIPConstants.STORE_NAME, jsonObjectRP.getString("storeName"));
                                    editor.putString(VIPConstants.USER_NAME, jsonObjectRP.getString("storeName"));
                                    editor.putString(VIPConstants.USER_CONTACT, jsonObjectRP.getString("ContactPerson"));
                                    //editor.putString(VIPConstants.USER_ALT_CONTACT, jsonObjectRP.getString("AltContNum"));
                                    editor.putString(VIPConstants.USER_EMAIL, jsonObjectRP.getString("Email"));
                                    // editor.putString(VIPConstants.USER_PINCODE, jsonObjectRP.getString("Pincode"));
                                    editor.putString(VIPConstants.USER_ZONE, jsonObjectRP.getString("Zone"));
                                    //editor.putString(VIPConstants.USER_CITY, jsonObjectRP.getString("City"));
                                    //editor.putString(VIPConstants.USER_STATE, jsonObjectRP.getString("State"));
                                    //editor.putString(VIPConstants.USER_COUNTRY, jsonObjectRP.getString("Country"));
                                    //editor.putString(VIPConstants.USER_SAPCODE, jsonObjectRP.getString("SapCode"));
                                    //editor.putString(VIPConstants.USER_GST, jsonObjectRP.getString("GST"));
                                    editor.putString(VIPConstants.USER_STATUS, jsonObjectRP.getString("Status"));
                                    editor.putString(VIPConstants.CREATE_DATE, jsonObjectRP.getString("CreateDate"));
                                    editor.putString(VIPConstants.USER_BRANCH, jsonObjectRP.getString("Branch"));
                                    //editor.putString(VIPConstants.USER_TOWN, jsonObjectRP.getString("Town"));
                                    editor.commit();
                                    generateNotificationToken(jsonObj.getString("user_id"));
                                    startActivity(new Intent(MainActivity.this, CategoriesActivity.class));
                                    finish();

                                } else if (statusCode == 10) {
                                    Update();
                                } else {
                                    Toast toast = Toast.makeText(MainActivity.this,
                                            statusMessage, Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();

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
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("MobileNo", mobileValue);
//                params.put("password", enteredPassw);
//                params.put("AppID", "3");
//                params.put("AppVersion", version);


                return params;
            }

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

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    @Override
    protected void onResume() {
        super.onResume();

//       if(VIPFunctions.isNetworkAvailable(this))
//        {
//            new CheckVersion().execute(VIPConstants.SERVER_URL+
//                    "RepairPointApi/VersionUpdateAPI?AppID="+"3"+"&AppVersion="+version);
//            //getCategories();
//
//        }
//        else
//        {
//            Toast toast = Toast.makeText(this,"Please check your internet connection",Toast.LENGTH_LONG);
//            toast.setGravity(Gravity.CENTER,0,0);
//            toast.show();
//        }
       /* AppUpdater appUpdater = new AppUpdater(this);
        appUpdater.setDisplay(Display.DIALOG);
        appUpdater.setCancelable(false);
        appUpdater.setUpdateFrom(UpdateFrom.GOOGLE_PLAY);
        appUpdater.setButtonDoNotShowAgain(null);
        appUpdater.setContentOnUpdateAvailable("A new version of this app is available on play store! Would you like to update to it?");
        // appUpdater.showAppUpdated(true);
        appUpdater.start();*/
    }

    //String fcmToken = "";
    void generateNotificationToken(String userid) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        storeRegIdInPref(token);
                        updateFirebaseToken(token, userid);
                        //  fcmToken = token;

                        // Log and toast

                        System.out.println(token);

                        Log.i("Your Device Token", token);
                    }
                });
    }

    public void updateFirebaseToken(String token, String userid) {


        // using retrofit to get user details
        Call<UpdateTokenResponse> call = apiInterface.updateToken(userid, token);
        call.enqueue(new retrofit2.Callback<UpdateTokenResponse>() {
            @Override
            public void onResponse(Call<UpdateTokenResponse> call, retrofit2.Response<UpdateTokenResponse> response) {
                UpdateTokenResponse statusResponse = response.body();

                Log.d("statusResponse code", statusResponse.getStatuscode() + "");
                Log.d("statusResponse", response.toString());


                if (statusResponse.getStatuscode() == 0) {
                    //finish();
                } else {
                    // Log.i("Token updated", token);
                    VIPFunctions.showToast(MainActivity.this,
                            statusResponse.getStatusmessage());
                }
            }

            @Override
            public void onFailure(Call<UpdateTokenResponse> call, Throwable t) {

            }
        });

    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(VIPConstants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(VIPConstants.FIREBASE_TOKEN, token);
        editor.commit();

        System.out.println("token = " + token);


    }

    public void Update() {


        AlertDialog.Builder loginAlertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

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


    }
}
