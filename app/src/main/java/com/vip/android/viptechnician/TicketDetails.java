package com.vip.android.viptechnician;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.vip.android.viptechnician.adapters.SparePartsAdapter;
import com.vip.android.viptechnician.beans.ReasonBean;
import com.vip.android.viptechnician.beans.ReasonList;
import com.vip.android.viptechnician.beans.RepairBean;
import com.vip.android.viptechnician.beans.SoldTicketReasons;
import com.vip.android.viptechnician.beans.SpareParts;
import com.vip.android.viptechnician.beans.SparePartsResponse;
import com.vip.android.viptechnician.beans.StatusBean;
import com.vip.android.viptechnician.beans.TicketBean;
import com.vip.android.viptechnician.util.MonserattTextViewRegular;
import com.vip.android.viptechnician.util.MonserattTextview;
import com.vip.android.viptechnician.util.MonteserattEditText;
import com.vip.android.viptechnician.util.VIPConstants;
import com.vip.android.viptechnician.util.VIPFunctions;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import uk.co.senab.photoview.PhotoViewAttacher;

public class TicketDetails extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Geocoder geocoder;
    List<Address> addresses;
    String Latitude, Longitude;
    String TotalAddress;
    public static final int INTERVAL = 1000 * 1;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    double lat, lon;
    AlertDialog networkAlertDialog;

    MonserattTextViewRegular buttonStartWork, buttonWorkDone, buttonNotDone, buttonAssign, spare_quantity, requestType;
    MonserattTextview textName, textPhone, textAddress, textProductName, textDefect, textPickOtp,
            text_product_id_value_detail, spare_part_image, upload_warranty_image, upload_from_camera, upload_from_gallery,
            text_ticket_number_value, negative_btn, positive_btn, text_warranty_type_value, upload_bill_image, replacementValue,
            text_ticket_invoice_date_value_detail,text_ticket_category_value_detail;
    Dialog dialog;
    ImageView imageDefect;
    MonteserattEditText editOtp, editComments, spare_part_selection, spare_search;
    MonserattTextview buttonSubmitOtp;
    CheckBox verify_barcode;
    LinearLayout linearGallery, linearCamera, serviceChargeLayout;
    ImageView imageViewAttach, remove_quantity, add_quantity, spare_image;
    Dialog dialogAttach;
    Button buttonRemove, buttonOkAttach, markPaid;
    Bitmap bitmap;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    AlertDialog permissionAlertDialog;
    AlertDialog.Builder verifyotpBuilder;
    private int GALLERY = 1, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    TicketBean ticketBean;
    String ticketNo, categoryId = "", SubmitOTP, userId, encodedImage = "", statusId = "", reasonId = "", comments = "", billEncodedImage = "",
            ticketStatus = "", imageUrl = "", allowwork, pickUpOtp, verifyBarcodeFromIntent = "", spareEncodedImage = "",
            verifyBarcodeValue = "", quantity = "", spareRequest = "", replacementRequest = "", warrantyEncodedImage = "",
            handlingDamageRequest = "", selectedSpare = "";
    ProgressDialog progressDialog;
    private byte changedSpareQuantity = 1;
    Spinner spinnerStatus, spinnerReason, spinnerRepairList, spinner_sold_reason;
    String selectedStatus, selectedReason, otp;
    List<StatusBean> statusBeen = new ArrayList<>();
    List<ReasonBean> reasonBeen = new ArrayList<>();
    List<String> soldReasonString = new ArrayList<>();
    List<RepairBean> repairBeen = new ArrayList<>();
    List<String> statusString = new ArrayList<>();
    List<String> reasonString = new ArrayList<>();
    List<String> repairString = new ArrayList<>();
    ArrayList<ReasonList> soldReasonList;
    ArrayAdapter<String> statusAdapter;
    ArrayAdapter<String> reasonAdapter;
    ArrayAdapter<String> repairAdapter;
    Intent camintent;
    SharedPreferences vipSharedPreferences;
    private int REQUEST_CAMERA_WARRANTY = 0;
    private int REQUEST_GALLERY_WARRANTY = 6;
    private int REQUEST_CAMERA_BILL = 10;
    private int REQUEST_CAMERA_SPARE = 20;
    private int REQUEST_GALLERY_BILL = 16;
    private int REQUEST_GALLERY_SPARE = 26;
    SharedPreferences.Editor editor;
    LayoutInflater inflater;
    //public static final String URL_TICKET_OTP= VIPConstants.SERVER_URL+"/RepairPointApi/GetTicketOtpRep";
    public static final String URL_STATUS = VIPConstants.SERVER_URL + "/RepairPointApi/GetStatus";
    public static final String URL_REASON = VIPConstants.SERVER_URL + "/RepairPointApi/GetReason";
    public static final String URL_CLOSE_TICKET = VIPConstants.SERVER_URL + "/RepairPointApi/CloseTicketRepair";
    public static final String GET_SPARE_PARTS = VIPConstants.SERVER_URL + "/ServiceEnginerApi/GetSpairParts";
    public static final String SOLD_WORK_NOT_DONE_REASON = VIPConstants.SERVER_URL + "/RepairPointApi/NotDoneReasonSold";
    public static final String UPDATE_TICKET_STATUS = VIPConstants.SERVER_URL + "/RepairPointApi/HandleTeamRequestRepairPoint";
    AlertDialog uploadImageAlertDialog, sparesAlertDialog;
    List<SpareParts> sparePartsList = new ArrayList<>();
    List<SpareParts> tempSparePartsList = new ArrayList<>();
    PhotoViewAttacher photoViewAttacher;
    Bitmap warrantyBitmap, billBitmap, spareBitmap;
    AlertDialog.Builder dialogBuilder;
    RecyclerView spare_recyclerview;
    URL sourceUrl = null;
    String attachmentpath = "";
    Uri uri;
    File destination, warrantyFile, billFile, spareFile;
    ProgressDialog prgDialog;
    StringBuffer strBuff;
    ImageView warranty_image, bill_image;
    URL url;
    BufferedReader in;
    List<NameValuePair> nameValuePairs;
    JSONObject mainObj, innerObj, custDetailsObj;
    JSONArray messageArray;
    String version = "", masterId;
    EditText serviceCharge;
    MonteserattEditText serviceChargeEditComments;
    SparePartsAdapter sparePartsAdapter;
    AlertDialog loginAlertDialog;
    private LinearLayout replacementLayout, spares_quantity_lay;

    LinearLayout layout_warranty_type_detail,layout_ticket_address_detail,layout_ticket_category,layout_ticket_invoice_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        setContentView(R.layout.activity_ticket_details);

        getSupportActionBar().setTitle("Ticket Details");

        ticketBean = (TicketBean) getIntent().getSerializableExtra("ticketBean");
        verifyBarcodeFromIntent = getIntent().getStringExtra("verifiedbarcode");

        System.out.println("verifyBarcodeFromIntent = " + verifyBarcodeFromIntent);
        camintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ticketNo = ticketBean.getTicketNo();
        System.out.println("list" + ticketBean.getTicketNo());
        categoryId= getIntent().getStringExtra("categoryid");
       // categoryId = ticketBean.getRepair_replacement();
        System.out.println("ticket category = " + ticketBean.getRepair_replacement());
        ticketStatus = ticketBean.getTicketStatus();
        imageUrl = ticketBean.getDefectImage();
        System.out.println("categoryId" + categoryId);
        pickUpOtp = ticketBean.getPickupotp();
        allowwork = ticketBean.getAllowwork();

        prgDialog = new ProgressDialog(this);
        vipSharedPreferences = getSharedPreferences(VIPConstants.PREFS_NAME, 0);
        editor = vipSharedPreferences.edit();

        userId = vipSharedPreferences.getString(VIPConstants.USER_ID, "");
        masterId = vipSharedPreferences.getString(VIPConstants.MASTER_ID, "");
//        getStatusList();
        buttonStartWork = (MonserattTextViewRegular) findViewById(R.id.button_start_work);
        buttonWorkDone = (MonserattTextViewRegular) findViewById(R.id.button_work_done);
        buttonNotDone = (MonserattTextViewRegular) findViewById(R.id.button_not_done);
        text_product_id_value_detail = (MonserattTextview) findViewById(R.id.text_product_id_value_detail);
        // buttonAssign= (MonserattTextViewRegular) findViewById(R.id.button_assign_work);
        imageDefect = (ImageView) findViewById(R.id.image_bag_defect);
        text_ticket_number_value = (MonserattTextview) findViewById(R.id.text_ticket_number_value);
        text_ticket_invoice_date_value_detail = (MonserattTextview) findViewById(R.id.text_ticket_invoice_date_value_detail);
        text_ticket_category_value_detail = (MonserattTextview) findViewById(R.id.text_ticket_category_value_detail);
        layout_warranty_type_detail =  findViewById(R.id.layout_warranty_type_detail);
        layout_ticket_category =  findViewById(R.id.layout_ticket_category);
        layout_ticket_invoice_date =  findViewById(R.id.layout_ticket_invoice_date);
        layout_ticket_address_detail =  findViewById(R.id.layout_ticket_address_detail);

        bill_image = (ImageView) findViewById(R.id.bill_image);
        spare_part_selection = (MonteserattEditText) findViewById(R.id.spare_part_selection);
        spinnerStatus = (Spinner) findViewById(R.id.spinner_status);
        spinnerReason = (Spinner) findViewById(R.id.spinner_reason);
        spinnerRepairList = (Spinner) findViewById(R.id.spinner_repair_list);
        spinner_sold_reason = (Spinner) findViewById(R.id.spinner_sold_reason);
        verify_barcode = (CheckBox) findViewById(R.id.verify_barcode);
        text_warranty_type_value = (MonserattTextview) findViewById(R.id.text_warranty_type_value);
        replacementLayout = findViewById(R.id.layout_replacement);
        requestType = findViewById(R.id.replacement_request_type);
        replacementValue = findViewById(R.id.replacement_value);
        editComments = findViewById(R.id.edit_comments);
        markPaid = findViewById(R.id.mark_paid);
        serviceCharge = findViewById(R.id.service_charge);
        serviceChargeEditComments = findViewById(R.id.service_charge_edit_comments);
        serviceChargeLayout = findViewById(R.id.service_charge_layout);
        spares_quantity_lay = findViewById(R.id.spares_quantity_lay);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);


        remove_quantity = (ImageView) findViewById(R.id.remove_quantity);
        spare_quantity = (MonserattTextViewRegular) findViewById(R.id.spare_quantity);
        add_quantity = (ImageView) findViewById(R.id.add_quantity);
        spare_part_image = (MonserattTextview) findViewById(R.id.spare_part_image);
        spare_image = (ImageView) findViewById(R.id.spare_image);
        upload_bill_image = (MonserattTextview) findViewById(R.id.upload_bill_image);
        warranty_image = (ImageView) findViewById(R.id.warranty_image);
        upload_warranty_image = (MonserattTextview) findViewById(R.id.upload_warranty_image);

        textPickOtp = (MonserattTextview) findViewById(R.id.text_otp_value_detail);
        textPickOtp.setText(pickUpOtp);

        destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        /*if(VIPFunctions.isNetworkAvailable(this))
        {
            getStatusList();
        }
        else
        {
            Toast toast = Toast.makeText(this,"Please check your internet connection",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }*/


        textName = (MonserattTextview) findViewById(R.id.text_ticket_name_value_detail);
        textPhone = (MonserattTextview) findViewById(R.id.text_ticket_phone_value_detail);
        textAddress = (MonserattTextview) findViewById(R.id.text_ticket_address_value_detail);
        textProductName = (MonserattTextview) findViewById(R.id.text_product_name_value_detail);
        textDefect = (MonserattTextview) findViewById(R.id.text_issue_value_detail);

        //getReasonList();
        text_ticket_number_value.setText(ticketBean.getTicketNo());
        textName.setText(ticketBean.getName());
        textPhone.setText(ticketBean.getPhone());
        textAddress.setText(ticketBean.getAddress());
        textProductName.setText(ticketBean.getProductName());
        textDefect.setText(ticketBean.getDefect());
        //incompled
        text_ticket_invoice_date_value_detail.setText(ticketBean.getInvoice_date());
        text_ticket_category_value_detail.setText(ticketBean.getCategory_Type());

        if(categoryId.equalsIgnoreCase("1")){
            layout_ticket_invoice_date.setVisibility(View.VISIBLE);
            layout_ticket_category.setVisibility(View.VISIBLE);
            layout_ticket_address_detail.setVisibility(View.GONE);
            layout_warranty_type_detail.setVisibility(View.GONE);

        }
        else {
            layout_ticket_invoice_date.setVisibility(View.GONE);
            layout_ticket_category.setVisibility(View.GONE);
            layout_ticket_address_detail.setVisibility(View.VISIBLE);
            layout_warranty_type_detail.setVisibility(View.VISIBLE);
        }

        remove_quantity.setEnabled(false);

        add_quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = "";
                quantity = spare_quantity.getText().toString();

                if (quantity.length() > 0) {
                    changedSpareQuantity = Byte.parseByte(quantity);

                    changedSpareQuantity += 1;
                    spare_quantity.setText(changedSpareQuantity + "");
                    // Glide.with(TicketDetails.this).load(R.drawable.ic_minus).into(remove_quantity);
                    remove_quantity.setEnabled(true);
                   /* convenientShoppingFragment.addRemoveCartQuantity(changedSpareQuantity,
                            productDetailsList.get(position).getProduct_id());*/
                } else {
                    remove_quantity.setEnabled(false);
                    // Glide.with(TicketDetails.this).load(R.drawable.ic_minus_light_blue).into(remove_quantity);
                }

            }
        });

        remove_quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = "";
                quantity = spare_quantity.getText().toString();


                System.out.println("remove_quantity called");

                if (quantity.length() > 0) {
                    changedSpareQuantity = Byte.parseByte(quantity);
                    if (changedSpareQuantity > 1) {
                        changedSpareQuantity -= 1;
                        spare_quantity.setText(changedSpareQuantity + "");

                        if (changedSpareQuantity > 1) {
                            remove_quantity.setEnabled(true);
                            // Glide.with(TicketDetails.this).load(R.drawable.ic_minus).into(remove_quantity);
                            /*convenientShoppingFragment.addRemoveCartQuantity(changedSpareQuantity,
                                    productDetailsList.get(position).getProduct_id());*/
                        } else {
                            remove_quantity.setEnabled(false);
                            // Glide.with(TicketDetails.this).load(R.drawable.ic_minus_light_blue).into(remove_quantity);
                            /*convenientShoppingFragment.addRemoveCartQuantity(changedSpareQuantity,
                                    productDetailsList.get(position).getProduct_id());*/
                        }

                    } else {
                        remove_quantity.setEnabled(false);
                        changedSpareQuantity = 1;
                        spare_quantity.setText(changedSpareQuantity + "");
                        // Glide.with(TicketDetails.this).load(R.drawable.ic_minus_light_blue).into(remove_quantity);
                    }

                }

            }
        });

        markPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markPaid.setEnabled(false);
                showProgress();
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        VIPConstants.SERVER_URL + "/ServiceEnginerApi/ChangeWarrantyStatusSold", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!TicketDetails.this.isFinishing()) {

                            progressDialog.dismiss();
                        }
                        markPaid.setEnabled(true);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("statuscode") == 0) {
                                markPaid.setVisibility(View.GONE);
                                text_warranty_type_value.setText("Paid");
                                ticketBean.setWarranty_type("Paid");
                                onResume();
                            }
                            Toast.makeText(TicketDetails.this, jsonObject.getString("statusmessage"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (!TicketDetails.this.isFinishing()) {

                            progressDialog.dismiss();
                        }
                        markPaid.setEnabled(true);
                        Log.v("VOLLEY_ERROR_", error.toString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("ticketno", ticketNo);
                        params.put("user_type", "RP");
                        params.put("warranty_type", "Paid");
                        params.put("master_id", masterId);
                        params.put("user_id", userId);
                        params.put("comment", "");
                        System.out.println("params" + params);
                        return params;
                    }

                };
                RequestQueue requestQueue = Volley.newRequestQueue(TicketDetails.this);
                requestQueue.add(stringRequest);
                stringRequest.setRetryPolicy(
                        new DefaultRetryPolicy(
                                500000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        )
                );
            }
        });

        if (ticketBean.getBarcode() != null) {
            text_product_id_value_detail.setText(ticketBean.getBarcode());
        }

        if (ticketBean.getWarranty_type().length() <= 0) {
            text_warranty_type_value.setVisibility(View.GONE);
        } else {
            text_warranty_type_value.setVisibility(View.VISIBLE);
            text_warranty_type_value.setText(ticketBean.getWarranty_type());
            if (ticketBean.getWarranty_type().equalsIgnoreCase("unpaid")
                    && categoryId.equals("2"))
                markPaid.setVisibility(View.VISIBLE);
        }

        verify_barcode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == true) {
                    verifyotpBuilder = null;
                    verifyotpBuilder = new AlertDialog.Builder(TicketDetails.this);

                    LayoutInflater inflater = TicketDetails.this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.accept_reject_dialog, null);
                    verifyotpBuilder.setView(dialogView);

                    negative_btn = (MonserattTextview) dialogView.findViewById(R.id.negative_btn);
                    positive_btn = (MonserattTextview) dialogView.findViewById(R.id.positive_btn);

                    if (VIPFunctions.isNetworkAvailable(TicketDetails.this)) {

                    }

                    final AlertDialog alertDialog = verifyotpBuilder.create();
                    alertDialog.show();

                    negative_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (categoryId.compareTo("2") == 0) {
                                alertDialog.dismiss();
                                verifyBarcodeValue = "0";
                                new VerifyBarcode().execute(VIPConstants.SERVER_URL +
                                        "/TransactionApi/VerifyBarcode");
                            } else if (categoryId.compareTo("1") == 0) {
                                alertDialog.dismiss();
                                verifyBarcodeValue = "0";
                                new VerifyUnsoldBarcode().execute(VIPConstants.SERVER_URL +
                                        "/TransactionApi/VerifyUnsoldBarcode");
                            }

                        }
                    });

                    positive_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (categoryId.compareTo("2") == 0) {
                                alertDialog.dismiss();
                                verifyBarcodeValue = "0";
                                new VerifyBarcode().execute(VIPConstants.SERVER_URL +
                                        "/TransactionApi/VerifyBarcode");
                            } else if (categoryId.compareTo("1") == 0) {
                                alertDialog.dismiss();
                                verifyBarcodeValue = "0";
                                new VerifyUnsoldBarcode().execute(VIPConstants.SERVER_URL +
                                        "/TransactionApi/VerifyUnsoldBarcode");
                            }
                        }
                    });


                } else {
                    buttonStartWork.setVisibility(View.GONE);
                    verify_barcode.setVisibility(View.VISIBLE);
                }
            }
        });

        if (ticketStatus.compareTo("startedworking") == 0) {
            //getCloseOtp();

            if (verifyBarcodeFromIntent.length() <= 0) {
                System.out.println("length zero");
                verify_barcode.setVisibility(View.VISIBLE);
                buttonStartWork.setVisibility(View.GONE);
                buttonNotDone.setVisibility(View.GONE);
                spinnerStatus.setVisibility(View.GONE);
            } else if (verifyBarcodeFromIntent.compareTo("1") == 0 && verifyBarcodeFromIntent.compareTo("0") == 0) {
                System.out.println("!@!@!its 1 ticketBean.getVerified_barcode() = " + verifyBarcodeFromIntent);
                verify_barcode.setVisibility(View.GONE);
                buttonStartWork.setVisibility(View.VISIBLE);
                buttonNotDone.setVisibility(View.GONE);
                spinnerStatus.setVisibility(View.GONE);
            }
        } else if (allowwork.compareTo("no") == 0) {
            verify_barcode.setVisibility(View.GONE);
            buttonStartWork.setVisibility(View.GONE);
            buttonNotDone.setVisibility(View.GONE);
            spinnerStatus.setVisibility(View.GONE);
            if ((ticketBean.getSpareRequest().compareToIgnoreCase("Sent") == 0) ||
                    (ticketBean.getReplacementRequest().compareToIgnoreCase("Sent") == 0) ||
                    (ticketBean.getReplacementRequest().compareToIgnoreCase("Accepted") == 0) ||
                    (ticketBean.getHandlingDamageRequest().compareToIgnoreCase("Sent") == 0)) {

                if (ticketBean.getSpareRequest().length() > 0) {
                    replacementLayout.setVisibility(View.VISIBLE);
                    requestType.setText("Spare Request:");
                    replacementValue.setText(ticketBean.getSpareRequest());
                } else if (ticketBean.getReplacementRequest().length() > 0) {
                    replacementLayout.setVisibility(View.VISIBLE);
                    requestType.setText("Replacement Request:");
                    replacementValue.setText(ticketBean.getReplacementRequest());
                } else if (ticketBean.getHandlingDamageRequest().length() > 0) {
                    replacementLayout.setVisibility(View.VISIBLE);
                    requestType.setText("Handling Damage Request:");
                    replacementValue.setText(ticketBean.getHandlingDamageRequest());
                }

            } else {
//                buttonNotDone.setVisibility(View.VISIBLE);
//                spinnerStatus.setVisibility(View.VISIBLE);

                if (ticketBean.getSpareRequest().length() > 0) {
                    replacementLayout.setVisibility(View.VISIBLE);
                    requestType.setText("Spare Request:");
                    replacementValue.setText(ticketBean.getSpareRequest());
                } else if (ticketBean.getReplacementRequest().length() > 0) {
                    replacementLayout.setVisibility(View.VISIBLE);
                    requestType.setText("Replacement Request:");
                    replacementValue.setText(ticketBean.getReplacementRequest());
                } else if (ticketBean.getHandlingDamageRequest().length() > 0) {
                    replacementLayout.setVisibility(View.VISIBLE);
                    requestType.setText("Handling Damage Request:");
                    replacementValue.setText(ticketBean.getHandlingDamageRequest());
                }
            }
        } else {
            if (verifyBarcodeFromIntent.length() <= 0) {
                System.out.println("length zero");
                verify_barcode.setVisibility(View.VISIBLE);
                buttonStartWork.setVisibility(View.GONE);
            } else if (verifyBarcodeFromIntent.compareTo("1") == 0 || verifyBarcodeFromIntent.compareTo("0") == 0) {
                System.out.println("!@!@!its 1 ticketBean.getVerified_barcode() = " + verifyBarcodeFromIntent);
                verify_barcode.setVisibility(View.GONE);
                buttonStartWork.setVisibility(View.VISIBLE);
            }

        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        System.out.println("All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        System.out.println("Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(TicketDetails.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            System.out.println("PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        System.out.println("Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });


        if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        }

        spare_part_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TicketDetails.this);


                View dialogView = inflater.inflate(R.layout.spares_selection_dialog, null);
                dialogBuilder.setView(dialogView);


                spare_search = dialogView.findViewById(R.id.spare_search);
                spare_recyclerview = dialogView.findViewById(R.id.spare_recyclerview);

                sparesAlertDialog = dialogBuilder.create();
                sparesAlertDialog.setCancelable(true);

                sparesAlertDialog.show();

                sparePartsList.clear();
                sparePartsList.addAll(tempSparePartsList);

                sparePartsAdapter = new SparePartsAdapter(TicketDetails.this,
                        TicketDetails.this, sparePartsList);
                spare_recyclerview.setAdapter(sparePartsAdapter);
                spare_recyclerview.setLayoutManager(new LinearLayoutManager(TicketDetails.this,
                        LinearLayoutManager.VERTICAL, false));

                spare_search.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (spare_search.getText().length() > 0) {
                            sparePartsList.clear();

                            System.out.println("text value = " + spare_search.getText().toString());

                            for (SpareParts spare : tempSparePartsList) {
                                if (spare.getText().toLowerCase().contains(spare_search.getText().toString().toLowerCase())) {
                                    sparePartsList.add(spare);
                                }

                            }

                            sparePartsAdapter.notifyDataSetChanged();
                        } else {
                            sparePartsList.clear();
                            sparePartsList.addAll(tempSparePartsList);
                            sparePartsAdapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

            }
        });

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedStatus = "";
                selectedStatus = parent.getItemAtPosition(position).toString();
                System.out.println("selectedStatus" + selectedStatus);

                if (selectedStatus.compareTo("Work Not Done") == 0) {
                    serviceChargeLayout.setVisibility(View.GONE);
                    serviceChargeEditComments.setText("");
                    serviceCharge.setText("");
                    if (categoryId.compareToIgnoreCase("1") == 0) {
                        spinnerReason.setVisibility(View.VISIBLE);
                        spinnerRepairList.setVisibility(View.GONE);
                        spinner_sold_reason.setVisibility(View.GONE);
                        upload_warranty_image.setVisibility(View.GONE);
                        warranty_image.setVisibility(View.GONE);
                        upload_bill_image.setVisibility(View.GONE);
                        bill_image.setVisibility(View.GONE);
                    } else {
                        spinnerReason.setVisibility(View.GONE);
                        spinnerRepairList.setVisibility(View.GONE);
                        spinner_sold_reason.setVisibility(View.VISIBLE);
                        spinner_sold_reason.setSelection(0);
                        editComments.setVisibility(View.VISIBLE);

                        upload_warranty_image.setVisibility(View.VISIBLE);
                        warranty_image.setVisibility(View.VISIBLE);
                        upload_bill_image.setVisibility(View.VISIBLE);
                        bill_image.setVisibility(View.VISIBLE);
                    }
                } else if (selectedStatus.compareTo("Work Done") == 0) {
                    spinnerReason.setVisibility(View.GONE);
                    spinner_sold_reason.setVisibility(View.GONE);
                    editComments.setVisibility(View.GONE);
                    spinnerRepairList.setVisibility(View.GONE);
                    upload_warranty_image.setVisibility(View.GONE);
                    warranty_image.setVisibility(View.GONE);
                    upload_bill_image.setVisibility(View.GONE);
                    bill_image.setVisibility(View.GONE);
                    spare_part_selection.setVisibility(View.GONE);
                    spares_quantity_lay.setVisibility(View.GONE);
                    spare_part_image.setVisibility(View.GONE);
                    spare_image.setVisibility(View.GONE);
                    if (ticketBean.getWarranty_type().equalsIgnoreCase("unpaid")
                            && categoryId.equals("2")) {
                        serviceChargeLayout.setVisibility(View.GONE);
                    } else {
                        serviceChargeLayout.setVisibility(View.VISIBLE);
                    }
                    //showPictureDialog();
                } else if (selectedStatus.compareTo("Assign") == 0) {
                    spinnerReason.setVisibility(View.GONE);
                    spinner_sold_reason.setVisibility(View.GONE);
                    spinnerRepairList.setVisibility(View.VISIBLE);
                    upload_warranty_image.setVisibility(View.GONE);
                    warranty_image.setVisibility(View.GONE);
                    upload_bill_image.setVisibility(View.GONE);
                    bill_image.setVisibility(View.GONE);
                    spare_part_selection.setVisibility(View.GONE);
                    spares_quantity_lay.setVisibility(View.GONE);
                    spare_part_image.setVisibility(View.GONE);
                    spare_image.setVisibility(View.GONE);
                    serviceChargeLayout.setVisibility(View.GONE);
                    serviceChargeEditComments.setText("");
                    serviceCharge.setText("");
                    getRepairList();
                    //showPictureDialog();
                } else if (selectedStatus.compareTo("Select Status") == 0) {
                    spinnerReason.setVisibility(View.GONE);
                    spinner_sold_reason.setVisibility(View.GONE);
                    editComments.setVisibility(View.GONE);
                    spinnerRepairList.setVisibility(View.GONE);
                    upload_warranty_image.setVisibility(View.GONE);
                    warranty_image.setVisibility(View.GONE);
                    upload_bill_image.setVisibility(View.GONE);
                    bill_image.setVisibility(View.GONE);
                    spare_part_selection.setVisibility(View.GONE);
                    spares_quantity_lay.setVisibility(View.GONE);
                    spare_part_image.setVisibility(View.GONE);
                    spare_image.setVisibility(View.GONE);
                    serviceChargeLayout.setVisibility(View.GONE);
                    serviceChargeEditComments.setText("");
                    serviceCharge.setText("");
                }


                for (StatusBean statusBean : statusBeen) {
                    if (statusBean.getStatusName() != null && statusBean.getStatusName().equals(selectedStatus)) {
                        //something here
                        statusId = statusBean.getStatusId();
                        System.out.println("statusId is" + statusId);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        System.out.println("url before removing spaces" + imageUrl);

        if (imageUrl.compareTo("") != 0) {
            //Picasso.with(TicketDetails.this).load(imageUrl).fit().into(imageDefect);
            imageUrl = imageUrl.replaceAll(" ", "%20");

            System.out.println("url after removing spaces" + imageUrl);

            try {
                sourceUrl = new URL(imageUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            // System.out.println("url after removing spaces"+imageUrl);

            if (VIPFunctions.isNetworkAvailable(this)) {
                Glide.with(TicketDetails.this)
                        .load(String.valueOf(sourceUrl))
                        .thumbnail(Glide.with(TicketDetails.this).load(R.drawable.loadergif))
                        .error(R.drawable.noimagefound)
                        //.crossFade()
                        .fitCenter()
                        .into(imageDefect);
            }

            //  Picasso.with(TicketDetails.this).load(String.valueOf(sourceUrl)).fit().into(imageDefect);
        } else {

            Glide.with(TicketDetails.this)
                    .load(R.drawable.noimagefound)
                    .into(imageDefect);
        }

        spinner_sold_reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedReason = parent.getItemAtPosition(position).toString();
                System.out.println("selectedReason" + selectedReason);
                if (soldReasonList == null) return;
                for (ReasonList reasonBean : soldReasonList) {
                    if ((reasonBean.getText() != null) &&
                            (reasonBean.getText().compareToIgnoreCase(selectedReason) == 0)) {
                        //something here
                        reasonId = reasonBean.getId() + "";
                        System.out.println("reasonId is" + reasonId);

                        // id 2109 is for spare request
                        if (reasonBean.getId() == 2109) {
                            spare_part_selection.setVisibility(View.VISIBLE);
                            spares_quantity_lay.setVisibility(View.VISIBLE);
                            spare_part_image.setVisibility(View.VISIBLE);
                            spare_image.setVisibility(View.VISIBLE);
                        } else {
                            spare_part_selection.setVisibility(View.GONE);
                            spares_quantity_lay.setVisibility(View.GONE);
                            spare_part_image.setVisibility(View.GONE);
                            spare_image.setVisibility(View.GONE);

                            spareEncodedImage = "";
                            spare_image.setImageBitmap(null);
                            spareBitmap = null;
                        }

                        editComments.setVisibility(View.VISIBLE);
                        upload_warranty_image.setVisibility(View.VISIBLE);
                        warranty_image.setVisibility(View.VISIBLE);
                        upload_bill_image.setVisibility(View.VISIBLE);
                        bill_image.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        upload_warranty_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TicketDetails.this);


                View dialogView = inflater.inflate(R.layout.camera_gallery_dialog, null);
                dialogBuilder.setView(dialogView);


                upload_from_camera = dialogView.findViewById(R.id.upload_from_camera);
                upload_from_gallery = dialogView.findViewById(R.id.upload_from_gallery);

                uploadImageAlertDialog = dialogBuilder.create();
                uploadImageAlertDialog.setCancelable(true);

                uploadImageAlertDialog.show();

                warrantyFile = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");

                upload_from_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        uploadImageAlertDialog.dismiss();

                        if ((ContextCompat.checkSelfPermission(TicketDetails.this,
                                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                                (ContextCompat.checkSelfPermission(TicketDetails.this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                            startActivityForResult(camintent, REQUEST_CAMERA_WARRANTY);
                        } else {
                            checkAndRequestPermissions();
                        }


                    }
                });

                upload_from_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadImageAlertDialog.dismiss();

                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, REQUEST_GALLERY_BILL);

                    }
                });
            }
        });

        upload_bill_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TicketDetails.this);


                View dialogView = inflater.inflate(R.layout.camera_gallery_dialog, null);
                dialogBuilder.setView(dialogView);


                upload_from_camera = dialogView.findViewById(R.id.upload_from_camera);
                upload_from_gallery = dialogView.findViewById(R.id.upload_from_gallery);

                uploadImageAlertDialog = dialogBuilder.create();
                uploadImageAlertDialog.setCancelable(true);

                uploadImageAlertDialog.show();

                billFile = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");

                upload_from_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        uploadImageAlertDialog.dismiss();

                        if ((ContextCompat.checkSelfPermission(TicketDetails.this,
                                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                                (ContextCompat.checkSelfPermission(TicketDetails.this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                            startActivityForResult(camintent, REQUEST_CAMERA_BILL);
                        } else {
                            checkAndRequestPermissions();
                        }


                    }
                });


                upload_from_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadImageAlertDialog.dismiss();

                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, REQUEST_GALLERY_BILL);

                    }
                });
            }
        });

        spare_part_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TicketDetails.this);


                View dialogView = inflater.inflate(R.layout.camera_gallery_dialog, null);
                dialogBuilder.setView(dialogView);


                upload_from_camera = dialogView.findViewById(R.id.upload_from_camera);
                upload_from_gallery = dialogView.findViewById(R.id.upload_from_gallery);

                uploadImageAlertDialog = dialogBuilder.create();
                uploadImageAlertDialog.setCancelable(true);

                uploadImageAlertDialog.show();

                spareFile = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");

                upload_from_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadImageAlertDialog.dismiss();

                        if ((ContextCompat.checkSelfPermission(TicketDetails.this,
                                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                                (ContextCompat.checkSelfPermission(TicketDetails.this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                            startActivityForResult(camintent, REQUEST_CAMERA_SPARE);
                        } else {
                            checkAndRequestPermissions();
                        }
                    }
                });


                upload_from_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadImageAlertDialog.dismiss();

                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, REQUEST_GALLERY_SPARE);
                    }

                });
            }
        });

        imageDefect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBuilder = new AlertDialog.Builder(TicketDetails.this);

                LayoutInflater inflater = TicketDetails.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.full_screen_image_popup, null);
                dialogBuilder.setView(dialogView);

                ImageView guideimage = (ImageView) dialogView.findViewById(R.id.fullscreenimage);

                if (VIPFunctions.isNetworkAvailable(TicketDetails.this)) {
                    Glide.with(TicketDetails.this)
                            .load(String.valueOf(sourceUrl))
                            .thumbnail(Glide.with(TicketDetails.this).load(R.drawable.loadergif))
                            .error(R.drawable.noimagefound)
                            //.crossFade()
                            .fitCenter()
                            .into(guideimage);
                }

                photoViewAttacher = new PhotoViewAttacher(guideimage);

                photoViewAttacher.update();

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

            }
        });

        spinnerReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedReason = parent.getItemAtPosition(position).toString();
                System.out.println("selectedReason" + selectedReason);

                for (ReasonBean reasonBean : reasonBeen) {
                    if (reasonBean.getReasonName() != null && reasonBean.getReasonName().equals(selectedReason)) {
                        //something here
                        reasonId = reasonBean.getReasonId();
                        System.out.println("reasonId is" + reasonId);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerRepairList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buttonStartWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // buttonWorkDone.setVisibility(View.VISIBLE);
                // spinnerStatus.setVisibility(View.VISIBLE);
                if (TotalAddress != null) {
                    if (VIPFunctions.isNetworkAvailable(getApplicationContext())) {
                        //getOtp();
                        buttonNotDone.setVisibility(View.VISIBLE);
                        verify_barcode.setVisibility(View.GONE);
                        buttonStartWork.setVisibility(View.GONE);
                        spinnerStatus.setVisibility(View.VISIBLE);
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Please check your internet connection", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }

                } else {

                    Toast.makeText(getApplicationContext(), "Please Wait While Fetching your Location", Toast.LENGTH_LONG).show();
                }
                //showOtpDialog();
            }
        });

        /*buttonWorkDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPictureDialog();
            }
        });*/

        buttonNotDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("selectedStatus = " + selectedStatus);
                System.out.println("categoryId = " + categoryId);


                if (selectedStatus.compareToIgnoreCase("Select Status") == 0) {
                    Toast.makeText(getApplicationContext(), "Please select status", Toast.LENGTH_LONG).show();

                } else if (selectedStatus.compareToIgnoreCase("Work Done") == 0) {

                    if (ticketBean.getWarranty_type().equalsIgnoreCase("unpaid")
                            && categoryId.equals("2")) {
                        showPictureDialog();
                    } else {
                        if (!TextUtils.isEmpty(serviceCharge.getText().toString())) {
                            try {
                                if (Double.parseDouble(serviceCharge.getText().toString()) == 0) {
                                    if (serviceChargeEditComments.getText().length() > 0) {
                                        showPictureDialog();
                                    } else {
                                        Toast.makeText(TicketDetails.this, "Please add your comments", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    showPictureDialog();
                                }
                            } catch (NumberFormatException n) {
                                Toast.makeText(TicketDetails.this, "Please enter a valid service charge.", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(TicketDetails.this, "Enter service charge.", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else if (selectedStatus.compareToIgnoreCase("Work Not Done") == 0) {
                    // flow for work not done is different for sold and unsold tickets
                    // hence here we need to check the category type
                    if (categoryId.compareToIgnoreCase("1") == 0) {
                        int reasonVisible = spinnerReason.getVisibility();
                        System.out.println("reasonVisible" + reasonVisible);

                        if (reasonVisible == 0 && selectedReason.compareTo("Select Reason") == 0) {
                            Toast.makeText(getApplicationContext(), "Please select reason", Toast.LENGTH_LONG).show();
                        } else {
                            //rest of the flow for unsold tickets work not done
                            showPictureDialog();

                            //closeTicket();
                        }
                    } else {
                        int reasonVisible = spinner_sold_reason.getVisibility();
                        System.out.println("reasonVisible" + reasonVisible);

                        if (reasonVisible == 0 && selectedReason.compareTo("Select Reason") == 0) {
                            Toast.makeText(getApplicationContext(), "Please select reason", Toast.LENGTH_LONG).show();
                        } else {
                            //rest of the flow for sold tickets work not done

                            System.out.println("reasonId = " + reasonId);

                            if (Integer.parseInt(reasonId) == 2109) {
                                if ((selectedSpare.length() > 0) && (changedSpareQuantity > 0)
                                        && (spareEncodedImage.length() > 0) && (editComments.getText().length() > 0)) {
                                    spareRequest = "Sent";
                                    replacementRequest = "";
                                    handlingDamageRequest = "";
                                    comments = editComments.getText().toString();

                                    updateSoldTicketStatus();
                                } else {
                                    if (selectedSpare.length() <= 0) {
                                        Toast.makeText(getApplicationContext(), "Please select a spare part", Toast.LENGTH_LONG).show();
                                    } else if (changedSpareQuantity <= 0) {
                                        Toast.makeText(getApplicationContext(), "Please select quantity of spare part", Toast.LENGTH_LONG).show();
                                    } else if (editComments.getText().length() <= 0) {
                                        Toast.makeText(getApplicationContext(), "Please add your comments", Toast.LENGTH_LONG).show();

                                    } else if (spareEncodedImage.length() <= 0) {
                                        Toast.makeText(getApplicationContext(), "Please upload spare part image", Toast.LENGTH_LONG).show();
                                    }
                                }
                            } else if (Integer.parseInt(reasonId) == 2110) {
                                if (editComments.getText().length() > 0) {
                                    spareRequest = "";
                                    replacementRequest = "Sent";
                                    handlingDamageRequest = "";
                                    comments = editComments.getText().toString();

                                    updateSoldTicketStatus();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Please add your comments", Toast.LENGTH_LONG).show();

                                }
                            } else if (Integer.parseInt(reasonId) == 2111) {
                                if (editComments.getText().length() > 0) {
                                    spareRequest = "";
                                    replacementRequest = "";
                                    handlingDamageRequest = "Sent";
                                    comments = editComments.getText().toString();

                                    updateSoldTicketStatus();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Please add your comments", Toast.LENGTH_LONG).show();

                                }
                            }

                        }
                    }
                }


            }
        });

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionCode + "";

            System.out.println("version code : " + version);
            System.out.println("getPackageName : " + getApplicationContext().getPackageName());

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        serviceCharge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    try {
                        if (Double.parseDouble(s.toString()) == 0) {
                            serviceChargeEditComments.setVisibility(View.VISIBLE);
                        } else {
                            serviceChargeEditComments.setText("");
                            serviceChargeEditComments.setVisibility(View.GONE);
                        }
                    } catch (NumberFormatException n) {
                        Toast.makeText(TicketDetails.this, "Please enter a valid service charge.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    serviceChargeEditComments.setVisibility(View.GONE);
                    serviceChargeEditComments.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    void updateSoldTicketStatus() {
        showProgress();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_TICKET_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!TicketDetails.this.isFinishing()) {

                            progressDialog.dismiss();
                        }

                        if (VIPFunctions.isJsonValid(response)) {
                            // Toast.makeText(ViewMore.this,response,Toast.LENGTH_LONG).show();
                            JSONObject jsonObj, jsonObjectRP;
                            System.out.println("response of URL_CLOSE_TICKET" + response);


                            try {
                                jsonObj = new JSONObject(response);
                                int statusCode = jsonObj.getInt("statuscode");
                                String statusMessage = jsonObj.getString("statusmessage");
                                if (statusCode == 0) {

                                    finish();
                                } else {

                                    Toast.makeText(getApplicationContext(), statusMessage, Toast.LENGTH_LONG).show();
                                }


                            } catch (JSONException e) {
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
                        if (!TicketDetails.this.isFinishing()) {

                            progressDialog.dismiss();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("master_id", userId);
                params.put("user_id", vipSharedPreferences.getString(VIPConstants.MASTER_ID, ""));
                params.put("ticket_no", ticketNo);
                params.put("SpairPart", selectedSpare);
                params.put("Qty", "" + changedSpareQuantity);
                params.put("Part", "");
                params.put("SpareRequests", spareRequest);
                params.put("ReplacementRequest", replacementRequest);
                params.put("Warranty_Se_Rp", warrantyEncodedImage);
                params.put("Bill_Se_Rp", billEncodedImage);
                params.put("HandlingDamageRequest", handlingDamageRequest);
                params.put("Comment", comments);
                params.put("Spare_Part_Img", spareEncodedImage);
                params.put("reasonid", reasonId);
                params.put("Work_Header_Status", statusId);
                if (categoryId.equals("2") && selectedStatus.equals("Work Done"))
                    params.put("Paid_Amount", serviceCharge.getText().toString().trim());
                params.put("msg", serviceChargeEditComments.getText().toString().trim());
                System.out.println("params" + params);
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
                    AlertDialog.Builder loginAlertDialogBuilder = new AlertDialog.Builder(TicketDetails.this);

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

    public void getSelectedSpare(SpareParts spareParts, int adapterPosition) {
        selectedSpare = spareParts.getId() + "";
        spare_part_selection.setText(spareParts.getText());
        System.out.println("selected = " + spareParts.getText());

        if (sparesAlertDialog != null && sparesAlertDialog.isShowing()) {
            sparesAlertDialog.dismiss();
        }
    }

    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!VIPFunctions.isNetworkAvailable(TicketDetails.this)) {
            AlertDialog.Builder networkAlertDialogBuilder = new AlertDialog.Builder(TicketDetails.this);

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

            getStatusList();
        }
//        else
//        {
//            Toast toast = Toast.makeText(this,"Please check your internet connection",Toast.LENGTH_LONG);
//            toast.setGravity(Gravity.CENTER,0,0);
//            toast.show();
//        }

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d("VIPCP", "Location update resumed .....................");


        }

    }


    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d("VIPCP LOC UPDATES", "Location update started ..............: ");
    }

    private boolean checkAndRequestPermissions() {

        int permissionCamera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);

        int writestoragePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int finelocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (finelocationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }


        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (writestoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);

            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        System.out.println("Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("camera and storage permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        System.out.println("Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.CAMERA) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                            AlertDialog.Builder permissionAlertDialogBuilder = new AlertDialog.Builder(this);

                            // set title
                            permissionAlertDialogBuilder.setTitle("Permissions Needed");

                            // set dialog message
                            permissionAlertDialogBuilder
                                    .setMessage("Camera and storage permission required for this app")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            checkAndRequestPermissions();
                                            dialog.dismiss();

                                        }
                                    }).
                                    setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
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
                        else {
                            Toast toast = Toast.makeText(TicketDetails.this,
                                    "Go to settings and enable permissions", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();

                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }

                    if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("permission", "location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d("permission", "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {

                            AlertDialog.Builder permissionAlertDialogBuilder = new AlertDialog.Builder(this);

                            // set title
                            permissionAlertDialogBuilder.setTitle("Permissions Needed");

                            // set dialog message
                            permissionAlertDialogBuilder
                                    .setMessage("Location permission required to process")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            checkAndRequestPermissions();
                                            dialog.cancel();

                                        }
                                    }).
                                    setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });


                            // create alert dialog
                            permissionAlertDialog = permissionAlertDialogBuilder.create();

                            if (!permissionAlertDialog.isShowing())// show it
                                permissionAlertDialog.show();


                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }

                }
            }
        }


    }

        private void getRepairList() {

        showProgress();
        System.out.println("ticketno=" + ticketNo + "&categoryid=" + categoryId);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                VIPConstants.SERVER_URL + "/ServiceEnginerApi/GetRepairPointList?ticketno=" + ticketNo + "&technicianid=" + userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(ViewMore.this,response,Toast.LENGTH_LONG).show();
                        JSONObject jsonObj;
                        JSONArray jsonArray;
                        System.out.println("response of GetRepairPointList" + response);
                        progressDialog.dismiss();
                        repairBeen.clear();
                        try {
                            jsonObj = new JSONObject(response);
                            int statusCode = jsonObj.getInt("statuscode");
                            String statusMessage = jsonObj.getString("statusmessage");
                            JSONObject object;
                            if (statusCode == 0) {

                                jsonArray = jsonObj.getJSONArray("RepairList");
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    object = jsonArray.getJSONObject(i);
                                    RepairBean repairBean = new RepairBean();
                                    repairBean.setId(object.getString("id"));
                                    repairBean.setName(object.getString("name"));
                                    repairBeen.add(repairBean);
                                    repairString.add(object.getString("name"));

                                }

                                repairAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                        R.layout.simple_item, repairString);
                                repairAdapter.setDropDownViewResource(R.layout.simple_item_dropdown);
                                spinnerRepairList.setAdapter(repairAdapter);

                            } else {
                                Toast.makeText(getApplicationContext(), statusMessage, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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


    private void getReasonList() {

        showProgress();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_REASON,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (VIPFunctions.isJsonValid(response)) {
                            // Toast.makeText(ViewMore.this,response,Toast.LENGTH_LONG).show();
                            JSONObject jsonObj;
                            JSONArray jsonArray;
                            System.out.println("response of URL_REASON" + response);
                            progressDialog.dismiss();
                            reasonString.clear();
                            reasonBeen.clear();
                            try {
                                jsonObj = new JSONObject(response);
                                int statusCode = jsonObj.getInt("statuscode");
                                String statusMessage = jsonObj.getString("statusmessage");
                                JSONObject object;
                                reasonString.add("Select Reason");

                                if (statusCode == 0) {
                                    jsonArray = jsonObj.getJSONArray("reasonlist");
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        object = jsonArray.getJSONObject(i);
                                        ReasonBean reasonBean = new ReasonBean();
                                        reasonBean.setReasonId(object.getString("id"));
                                        reasonBean.setReasonName(object.getString("name"));
                                        reasonBeen.add(reasonBean);
                                        reasonString.add(object.getString("name"));

                                    }

                                    reasonAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                            R.layout.simple_item, reasonString);
                                    reasonAdapter.setDropDownViewResource(R.layout.simple_item_dropdown);
                                    spinnerReason.setAdapter(reasonAdapter);


                                } else {
                                    Toast.makeText(getApplicationContext(), statusMessage, Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
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

    private void getStatusList() {

        showProgress();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (VIPFunctions.isJsonValid(response)) {

                            // Toast.makeText(ViewMore.this,response,Toast.LENGTH_LONG).show();
                            JSONObject jsonObj;
                            JSONArray jsonArray;
                            System.out.println("response of URL_STATUS" + response);
                            progressDialog.dismiss();
                            statusBeen.clear();
                            statusString.clear();
                            if (categoryId.compareToIgnoreCase("1") == 0) {
                                getReasonList();
                            } else {
                                getSoldTicketsWorkNotDoneReasons();
                            }
                            try {
                                jsonObj = new JSONObject(response);
                                int statusCode = jsonObj.getInt("statuscode");
                                String statusMessage = jsonObj.getString("statusmessage");
                                JSONObject object;
                                statusString.add("Select Status");

                                if (statusCode == 0) {
                                    jsonArray = jsonObj.getJSONArray("statuslist");
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        object = jsonArray.getJSONObject(i);
                                        StatusBean statusBean = new StatusBean();
                                        statusBean.setStatusId(object.getString("id"));
                                        statusBean.setStatusName(object.getString("name"));
                                        statusBeen.add(statusBean);
                                        statusString.add(object.getString("name"));
                                    }

                                    statusAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                            R.layout.simple_item, statusString);
                                    statusAdapter.setDropDownViewResource(R.layout.simple_item_dropdown);
                                    spinnerStatus.setAdapter(statusAdapter);


                                } else {
                                    Toast.makeText(getApplicationContext(), statusMessage, Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
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
                        getReasonList();
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

    private void showPictureDialog() {

        dialogAttach = new Dialog(TicketDetails.this, R.style.cust_dialog);
        dialogAttach.setContentView(R.layout.custom_dialog_attach);
        dialogAttach.setTitle("Attach Product Image");

        buttonRemove = (Button) dialogAttach.findViewById(R.id.button_remove);
        buttonOkAttach = (Button) dialogAttach.findViewById(R.id.button_ok_attach);
        linearCamera = (LinearLayout) dialogAttach.findViewById(R.id.linear_camera);
        linearGallery = (LinearLayout) dialogAttach.findViewById(R.id.linear_gallery);
        imageViewAttach = (ImageView) dialogAttach.findViewById(R.id.imageView_attach);


        dialogAttach.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                System.out.println("dialogAttach dismissed");
                bitmap = null;
            }
        });

        linearCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((ContextCompat.checkSelfPermission(TicketDetails.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                        (ContextCompat.checkSelfPermission(TicketDetails.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {

                    /*if(ticketStatus.compareTo("startedworking")==0){
                        getCloseOtp();
                    }*/
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA);
                    // startActivityForResult(camintent, REQUEST_CAMERA);
                } else {
                    checkAndRequestPermissions();
                }
            }
        });

        linearGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                choosePhotoFromGallary();

            }
        });

        dialogAttach.show();

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogAttach.dismiss();
                bitmap = null;
            }
        });

        buttonOkAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bitmap != null) {

                    //Toast.makeText(getApplicationContext(),"Product Image Attached Successfully",Toast.LENGTH_LONG).show();
                    if (ticketStatus.compareTo("startedworking") == 0) {
                        if (VIPFunctions.isNetworkAvailable(getApplicationContext())) {

                            //getCloseOtp();
                            closeTicket();
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Please check your internet connection", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                    } else {
                        closeTicket();
                    }
                    //dialogAttach.dismiss();

                } else {
                    Toast.makeText(getApplicationContext(), "Please Attach Invoice", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void closeTicket() {

        /*if(!TicketDetails.this.isFinishing()){

            showProgress();
        }*/
        showProgress();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CLOSE_TICKET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (VIPFunctions.isJsonValid(response)) {
                            // Toast.makeText(ViewMore.this,response,Toast.LENGTH_LONG).show();
                            JSONObject jsonObj, jsonObjectRP;
                            System.out.println("response of URL_CLOSE_TICKET" + response);
                            if (!TicketDetails.this.isFinishing()) {

                                progressDialog.dismiss();
                            }

                            try {
                                jsonObj = new JSONObject(response);
                                int statusCode = jsonObj.getInt("statuscode");
                                String statusMessage = jsonObj.getString("statusmessage");
                                if (statusCode == 0) {
                                    //jsonObjectRP = jsonObj.getJSONObject("RP");

                                    Toast.makeText(getApplicationContext(), statusMessage, Toast.LENGTH_LONG).show();
                                    bitmap = null;
                                    dialogAttach.dismiss();
                                    finish();
                                } else {

                                    Toast.makeText(getApplicationContext(), statusMessage, Toast.LENGTH_LONG).show();
                                }


                            } catch (JSONException e) {
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
                        if (!TicketDetails.this.isFinishing()) {

                            progressDialog.dismiss();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", userId);
                params.put("ticketno", ticketNo);
                params.put("categoryid", categoryId);
                params.put("statusid", statusId);
                params.put("reasonid", reasonId);
                params.put("otp", "");
                params.put("invoice", encodedImage);
                params.put("address", TotalAddress);
                params.put("Paid_Amount", serviceCharge.getText().toString().trim());
                System.out.println("params" + params);
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

    private void showOtpDialog() {

        dialog = new Dialog(TicketDetails.this, R.style.cust_dialog);
        dialog.setContentView(R.layout.custom_dialog_otp);
        dialog.setTitle("Enter OTP");

        editOtp = (MonteserattEditText) dialog.findViewById(R.id.edit_otp);
        buttonSubmitOtp = (MonserattTextview) dialog.findViewById(R.id.text_submit_otp);

        buttonSubmitOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SubmitOTP = editOtp.getText().toString();
                if (VIPFunctions.isNetworkAvailable(getApplicationContext())) {
                    if (editOtp.getText().toString().length() > 0) {
                        submitOtp();
                    } else {

                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Please Enter Otp",
                                Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Please check your internet connection", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

            }
        });

        dialog.setCancelable(true);
        dialog.show();

      /*  dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    //getFragmentManager().popBackStackImmediate();
                }
                return true;
            }
        });*/

    }

    private void submitOtp() {

        showProgress();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                VIPConstants.SERVER_URL + "/RepairPointApi/ValidateTicketOtprep?ticketno=" + ticketNo + "&categoryid=" + categoryId + "&otp=" + SubmitOTP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (VIPFunctions.isJsonValid(response)) {
                            // Toast.makeText(ViewMore.this,response,Toast.LENGTH_LONG).show();
                            JSONObject jsonObj;
                            JSONArray jsonArray;
                            System.out.println("response of URL_SUBMIT_OTP" + response);
                            progressDialog.dismiss();

                            try {
                                jsonObj = new JSONObject(response);
                                int statusCode = jsonObj.getInt("statuscode");
                                String statusMessage = jsonObj.getString("statusmessage");
                                JSONObject object;
                                if (statusCode == 0) {

                                    Toast.makeText(getApplicationContext(), statusMessage, Toast.LENGTH_LONG).show();
                                    // buttonWorkDone.setVisibility(View.VISIBLE);
                                    buttonNotDone.setVisibility(View.VISIBLE);
                                    verify_barcode.setVisibility(View.GONE);
                                    buttonStartWork.setVisibility(View.GONE);
                                    spinnerStatus.setVisibility(View.VISIBLE);
                                    // buttonAssign.setVisibility(View.VISIBLE);
                                    dialog.dismiss();

                                } else {
                                    Toast.makeText(getApplicationContext(), statusMessage, Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
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

    private void getCloseOtp() {

        //showProgress();
        System.out.println("ticketNo of URL_TICKET_OTP" + ticketNo);
        System.out.println("URL of URL_TICKET_OTP" + VIPConstants.SERVER_URL + "/RepairPointApi/GetTicketOtpRep?ticketno=" + ticketNo + "&userid=" + userId + "&categoryid=" + categoryId
                + "&address=" + TotalAddress);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                VIPConstants.SERVER_URL + "/RepairPointApi/GetTicketOtpRep",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (VIPFunctions.isJsonValid(response)) {
                            // Toast.makeText(ViewMore.this,response,Toast.LENGTH_LONG).show();
                            JSONObject jsonObj;
                            JSONArray jsonArray;
                            System.out.println("response of URL_TICKET_OTP" + response);
                            //progressDialog.dismiss();
                            //categoryBeanList.clear();
                            try {
                                jsonObj = new JSONObject(response);
                                int statusCode = jsonObj.getInt("statuscode");
                                String statusMessage = jsonObj.getString("statusmessage");
                                JSONObject object;
                                if (statusCode == 0) {

                                    otp = jsonObj.getString("otp");
                                    System.out.println("OTP" + otp);
                                    closeTicket();
                                    //progressDialog.dismiss();
                                    //showOtpDialog();

                                } else {
                                    Toast.makeText(getApplicationContext(), statusMessage, Toast.LENGTH_LONG).show();
                                }


                            } catch (JSONException e) {
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
                        //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                        // progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ticketno", ticketNo);
                params.put("userid", userId);
                params.put("categoryid", categoryId);
                params.put("address", TotalAddress);
                params.put("start", "0");
                System.out.println("params" + params);
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

    private void getOtp() {

        showProgress();
        System.out.println("ticketNo of URL_TICKET_OTP" + ticketNo);
        System.out.println("URL of URL_TICKET_OTP" + VIPConstants.SERVER_URL + "/RepairPointApi/GetTicketOtpRep?" +
                "ticketno=" + ticketNo + "&userid=" + userId + "&categoryid=" + categoryId
                + "&address=" + TotalAddress);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                VIPConstants.SERVER_URL + "/RepairPointApi/GetTicketOtpRep",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (VIPFunctions.isJsonValid(response)) {
                            // Toast.makeText(ViewMore.this,response,Toast.LENGTH_LONG).show();
                            JSONObject jsonObj;
                            JSONArray jsonArray;
                            System.out.println("response of URL_TICKET_OTP" + response);
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
                                    showOtpDialog();

                                } else {
                                    Toast.makeText(getApplicationContext(), statusMessage, Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
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
                        //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ticketno", ticketNo);
                params.put("userid", userId);
                params.put("categoryid", categoryId);
                params.put("address", TotalAddress);
                params.put("start", "1");
                System.out.println("params" + params);
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

        progressDialog = new ProgressDialog(TicketDetails.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    void getSoldTicketsWorkNotDoneReasons() {
        showProgress();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, SOLD_WORK_NOT_DONE_REASON,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        if (VIPFunctions.isJsonValid(response)) {
                            // Toast.makeText(ViewMore.this,response,Toast.LENGTH_LONG).show();
                            JSONObject jsonObj;
                            JSONArray jsonArray;
                            SoldTicketReasons soldTicketReasons;
                            try {
                                jsonObj = new JSONObject(response);
                                soldTicketReasons = new SoldTicketReasons();

                                int statusCode = jsonObj.getInt("statuscode");
                                String statusMessage = jsonObj.getString("statusmessage");

                                soldTicketReasons.setStatuscode(statusCode);
                                soldTicketReasons.setStatusmessage(statusMessage);

                                JSONObject object;

                                if (statusCode == 0) {
                                    jsonArray = jsonObj.getJSONArray("GeneralList");
                                    soldReasonList = new ArrayList<>();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        object = jsonArray.getJSONObject(i);
                                        ReasonList reason = new ReasonList();
                                        reason.setId(object.getInt("id"));
                                        reason.setText(object.getString("text"));
                                        soldReasonList.add(reason);
                                    }
                                    soldTicketReasons.setReasonList(soldReasonList);

                                    if (soldTicketReasons.getReasonList().size() > 0) {
                                        soldReasonString.clear();
                                        soldReasonString.add("Select Reason");

                                        for (int j = 0; j < soldTicketReasons.getReasonList().size(); j++) {
                                            soldReasonString.add(soldTicketReasons.getReasonList().get(j).getText());
                                        }

                                        reasonAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                                R.layout.simple_item, soldReasonString);
                                        reasonAdapter.setDropDownViewResource(R.layout.simple_item_dropdown);
                                        spinner_sold_reason.setAdapter(reasonAdapter);

                                    }


                                } else {
                                    Toast.makeText(getApplicationContext(), statusMessage, Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "There is some technical issue at server\nSorry for the inconvenience",
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                        getSpareParts();
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

    void getSpareParts() {
        showProgress();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_SPARE_PARTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!isFinishing())
                            progressDialog.dismiss();

                        if (VIPFunctions.isJsonValid(response)) {
                            // Toast.makeText(ViewMore.this,response,Toast.LENGTH_LONG).show();
                            JSONObject jsonObj;
                            JSONArray jsonArray;
                            SparePartsResponse sparePartsResponse;
                            try {
                                jsonObj = new JSONObject(response);
                                sparePartsResponse = new SparePartsResponse();

                                int statusCode = jsonObj.getInt("statuscode");
                                String statusMessage = jsonObj.getString("statusmessage");

                                sparePartsResponse.setStatuscode(statusCode);
                                sparePartsResponse.setStatusmessage(statusMessage);

                                JSONObject object;

                                if (statusCode == 0) {
                                    jsonArray = jsonObj.getJSONArray("GeneralListSpair");
                                    ArrayList<SpareParts> sparePartsArrayList = new ArrayList<>();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        object = jsonArray.getJSONObject(i);
                                        SpareParts spareParts = new SpareParts();
                                        spareParts.setId(object.getInt("id"));
                                        spareParts.setText(object.getString("text"));
                                        sparePartsArrayList.add(spareParts);
                                    }
                                    sparePartsResponse.setSpareList(sparePartsArrayList);

                                    if (sparePartsResponse.getSpareList().size() > 0) {
                                        sparePartsList = sparePartsResponse.getSpareList();
                                        tempSparePartsList.addAll(sparePartsList);
                                    }


                                } else {
                                    Toast.makeText(getApplicationContext(), statusMessage, Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
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

    public void choosePhotoFromGallary() {

       /* if(ticketStatus.compareTo("startedworking")==0){
            getCloseOtp();
        }*/
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                if (contentURI != null) {
                    try {

                        final InputStream imageStream = getContentResolver().openInputStream(contentURI);
                        bitmap = BitmapFactory.decodeStream(imageStream);

                        attachmentpath = VIPFunctions.getRealPathFromUri(TicketDetails.this, contentURI);

                        System.out.println("new path::" + data.getData());
                        System.out.println("attachmentpath::" + attachmentpath);

                        if (VIPFunctions.checkIfSizeIsGreater(attachmentpath)) {
                            Toast toast = Toast.makeText(TicketDetails.this,
                                    "Please upload image with size less than 500 kb", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                            attachmentpath = "";
                            bitmap = null;
                        } else {
                            imageViewAttach.setVisibility(View.VISIBLE);
                            imageViewAttach.setImageBitmap(bitmap);

                            encodedImage = "";
                            encodedImage = getEncoded64ImageString(bitmap);
                            System.out.println("encodedImage" + encodedImage);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        } else if (requestCode == CAMERA) {
            try {
                if (data.getExtras().get("data") != null) {
                    bitmap = (Bitmap) data.getExtras().get("data");

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                    uri = null;
                    uri = Uri.parse(destination.getAbsolutePath());

                    attachmentpath = "";
                    attachmentpath = destination.getAbsolutePath();

                    System.out.println("new path::" + destination.getAbsolutePath());

                    if (VIPFunctions.checkIfSizeIsGreater(attachmentpath)) {
                        Toast toast = Toast.makeText(TicketDetails.this,
                                "Please upload image with size less than 500 kb", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                        attachmentpath = "";
                        bitmap = null;
                    } else {
                        FileOutputStream fo;
                        try {
                            destination.createNewFile();
                            fo = new FileOutputStream(destination);
                            fo.write(bytes.toByteArray());
                            fo.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        imageViewAttach.setVisibility(View.VISIBLE);
                        imageViewAttach.setImageBitmap(bitmap);

                        encodedImage = "";
                        encodedImage = getEncoded64ImageString(bitmap);
                        System.out.println("encodedImage" + encodedImage);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Toast.makeText(getApplicationContext(),"Image Saved!", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_CAMERA_WARRANTY) {

            try {
                if (data.getExtras().get("data") != null) {
                    warrantyBitmap = (Bitmap) data.getExtras().get("data");

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    warrantyBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                    attachmentpath = "";
                    attachmentpath = warrantyFile.getAbsolutePath();

                    System.out.println("new path::" + warrantyFile.getAbsolutePath());

                    if (VIPFunctions.checkIfSizeIsGreater(attachmentpath)) {
                        Toast toast = Toast.makeText(TicketDetails.this,
                                "Please upload image with size less than 500 kb", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                        attachmentpath = "";
                        warrantyBitmap = null;
                    } else {
                        FileOutputStream fo;
                        try {
                            warrantyFile.createNewFile();
                            fo = new FileOutputStream(warrantyFile);
                            fo.write(bytes.toByteArray());
                            fo.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        warranty_image.setVisibility(View.VISIBLE);
                        warranty_image.setImageBitmap(warrantyBitmap);

                        warrantyEncodedImage = "";
                        warrantyEncodedImage = getEncoded64ImageString(warrantyBitmap);
                        System.out.println("warrantyEncodedImage" + warrantyEncodedImage);
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_GALLERY_WARRANTY) {
            if (data != null) {
                Uri contentURI = data.getData();
                if (contentURI != null) {
                    try {
                        final InputStream imageStream = getContentResolver().openInputStream(contentURI);
                        warrantyBitmap = BitmapFactory.decodeStream(imageStream);

                        attachmentpath = VIPFunctions.getRealPathFromUri(TicketDetails.this, contentURI);

                        System.out.println("new path::" + data.getData());
                        System.out.println("attachmentpath::" + attachmentpath);

                        if (VIPFunctions.checkIfSizeIsGreater(attachmentpath)) {
                            Toast toast = Toast.makeText(TicketDetails.this,
                                    "Please upload image with size less than 500 kb", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                            attachmentpath = "";
                            warrantyBitmap = null;
                        } else {
                            warranty_image.setVisibility(View.VISIBLE);
                            warranty_image.setImageBitmap(warrantyBitmap);

                            warrantyEncodedImage = "";
                            warrantyEncodedImage = getEncoded64ImageString(warrantyBitmap);
                            System.out.println("warrantyEncodedImage" + warrantyEncodedImage);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        } else if (requestCode == REQUEST_CAMERA_BILL) {

            try {
                if (data.getExtras().get("data") != null) {
                    billBitmap = (Bitmap) data.getExtras().get("data");

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    billBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                    attachmentpath = "";
                    attachmentpath = billFile.getAbsolutePath();

                    System.out.println("new path::" + billFile.getAbsolutePath());

                    if (VIPFunctions.checkIfSizeIsGreater(attachmentpath)) {
                        Toast toast = Toast.makeText(TicketDetails.this,
                                "Please upload image with size less than 500 kb", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                        attachmentpath = "";
                        billBitmap = null;
                    } else {
                        FileOutputStream fo;
                        try {
                            billFile.createNewFile();
                            fo = new FileOutputStream(billFile);
                            fo.write(bytes.toByteArray());
                            fo.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        bill_image.setVisibility(View.VISIBLE);
                        bill_image.setImageBitmap(billBitmap);

                        billEncodedImage = "";
                        billEncodedImage = getEncoded64ImageString(billBitmap);
                        System.out.println("billEncodedImage" + billEncodedImage);
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_GALLERY_BILL) {
            if (data != null) {
                Uri contentURI = data.getData();
                if (contentURI != null) {
                    try {
                        final InputStream imageStream = getContentResolver().openInputStream(contentURI);
                        billBitmap = BitmapFactory.decodeStream(imageStream);

                        attachmentpath = VIPFunctions.getRealPathFromUri(TicketDetails.this, contentURI);

                        System.out.println("new path::" + data.getData());
                        System.out.println("attachmentpath::" + attachmentpath);

                        if (VIPFunctions.checkIfSizeIsGreater(attachmentpath)) {
                            Toast toast = Toast.makeText(TicketDetails.this,
                                    "Please upload image with size less than 500 kb", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                            attachmentpath = "";
                            billBitmap = null;
                        } else {
                            bill_image.setVisibility(View.VISIBLE);
                            bill_image.setImageBitmap(billBitmap);

                            billEncodedImage = "";
                            billEncodedImage = getEncoded64ImageString(billBitmap);
                            System.out.println("billEncodedImage" + billEncodedImage);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        } else if (requestCode == REQUEST_CAMERA_SPARE) {

            try {
                if (data.getExtras().get("data") != null) {
                    spareBitmap = (Bitmap) data.getExtras().get("data");

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    spareBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                    attachmentpath = "";
                    attachmentpath = spareFile.getAbsolutePath();

                    System.out.println("new path::" + spareFile.getAbsolutePath());

                    if (VIPFunctions.checkIfSizeIsGreater(attachmentpath)) {
                        Toast toast = Toast.makeText(TicketDetails.this,
                                "Please upload image with size less than 500 kb", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                        attachmentpath = "";
                        spareBitmap = null;
                    } else {
                        FileOutputStream fo;
                        try {
                            spareFile.createNewFile();
                            fo = new FileOutputStream(spareFile);
                            fo.write(bytes.toByteArray());
                            fo.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        spare_image.setVisibility(View.VISIBLE);
                        spare_image.setImageBitmap(spareBitmap);

                        spareEncodedImage = "";
                        spareEncodedImage = getEncoded64ImageString(spareBitmap);
                        System.out.println("spareEncodedImage" + spareEncodedImage);
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_GALLERY_SPARE) {
            if (data != null) {
                Uri contentURI = data.getData();
                if (contentURI != null) {
                    try {
                        final InputStream imageStream = getContentResolver().openInputStream(contentURI);
                        spareBitmap = BitmapFactory.decodeStream(imageStream);

                        attachmentpath = VIPFunctions.getRealPathFromUri(TicketDetails.this, contentURI);

                        System.out.println("new path::" + data.getData());
                        System.out.println("attachmentpath::" + attachmentpath);

                        if (VIPFunctions.checkIfSizeIsGreater(attachmentpath)) {
                            Toast toast = Toast.makeText(TicketDetails.this,
                                    "Please upload image with size less than 500 kb", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                            attachmentpath = "";
                            spareBitmap = null;
                        } else {
                            spare_image.setVisibility(View.VISIBLE);
                            spare_image.setImageBitmap(spareBitmap);

                            spareEncodedImage = "";
                            spareEncodedImage = getEncoded64ImageString(spareBitmap);
                            System.out.println("spareEncodedImage" + spareEncodedImage);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }
    }

    public String getEncoded64ImageString(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(getApplicationContext(),
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }


    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("VIPCP onStart CALLED");
        mGoogleApiClient.connect();
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d("VIPCP", "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d("VIPCP STOPPED", "isConnected ...............: " + mGoogleApiClient.isConnected());
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        System.out.println("VIPCP onLocationChanged CALLED");

        if (location.getAccuracy() < 100) {
            if ((location.getLatitude() != 0) && (location.getLongitude() != 0)) {
                Latitude = String.valueOf(location.getLatitude());
                Longitude = String.valueOf(location.getLongitude());
                lat = location.getLatitude();
                lon = location.getLongitude();
                updateLocation();
                System.out.println("Latitude" + Latitude);
                System.out.println("Longitude" + Longitude);
                // waitforlocation.setVisibility(TextView.GONE);

            }
        }

    }

    private void updateLocation() {

        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses != null && addresses.size() > 0) {

                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                 String country = addresses.get(0).getCountryName();
                //String postalCode = addresses.get(0).getPostalCode();
                //String knownName = addresses.get(0).getFeatureName();
                TotalAddress = address + " , " + '\n' + city + " , " + '\n' + state + " . ";

                TotalAddress = TotalAddress.replaceAll(" ", "%20");

                //textFullAddress.setText(TotalAddress);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    class VerifyBarcode extends AsyncTask<String, Void, String> {

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

            strBuff = null;
            strBuff = new StringBuffer();
            try {
                // Create a URL for the desired page
                url = null;
                url = new URL(f_url[0]);

                // Read all the text returned by the server
                in = null;
                in = new BufferedReader(new InputStreamReader(url.openStream()));
                String str;
                while ((str = in.readLine()) != null) {
                    strBuff.append(str);
                }
                in.close();
            } catch (MalformedURLException e) {
            } catch (IOException e) {
            }


            HttpClient httpclient = new DefaultHttpClient();
            // specify the URL you want to post to

            HttpPost httppost = new HttpPost(f_url[0]);
            try {
                // create a list to store HTTP variables and their values

                nameValuePairs = new ArrayList<NameValuePair>();
                // add an HTTP variable and value pair
                nameValuePairs.clear();

                nameValuePairs.add(new BasicNameValuePair("ticketno", ticketBean.getTicketNo()));
                System.out.println("ticketno = " + ticketBean.getTicketNo());

                nameValuePairs.add(new BasicNameValuePair("verify_barcode", verifyBarcodeValue));
                System.out.println("verify_barcode = " + verifyBarcodeValue);


                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // send the variable and value, in other words post, to the URL
                HttpResponse response = httpclient.execute(httppost);

                return EntityUtils.toString(response.getEntity());

            } catch (ClientProtocolException e) {
                // process execption
            } catch (IOException e) {
                // process execption
            }

            return null;

        }


        @Override
        protected void onPostExecute(String jsonString) {

            prgDialog.dismiss();

            System.out.println("@#@#@#@#jsonString = " + jsonString);

            JSONObject jsonObject;

            try {
                if (jsonString == null) {
                    Toast toast = Toast.makeText(TicketDetails.this,
                            "Internet or server problem", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    mainObj = new JSONObject(jsonString);

                    int statuscode = mainObj.getInt("statuscode");
                    String statusmessage = mainObj.getString("statusmessage");
                    if (statuscode == 0) {
                        verify_barcode.setVisibility(View.GONE);

                        if ((categoryId.compareTo("2") == 0)) {
                            buttonStartWork.setVisibility(View.VISIBLE);
                        } else if ((categoryId.compareTo("1") == 0)) {
                            buttonStartWork.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(TicketDetails.this, statusmessage + "", Toast.LENGTH_SHORT).show();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    class VerifyUnsoldBarcode extends AsyncTask<String, Void, String> {

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

            strBuff = null;
            strBuff = new StringBuffer();
            try {
                // Create a URL for the desired page
                url = null;
                url = new URL(f_url[0]);

                System.out.println("f_url[0] = " + f_url[0]);
                // Read all the text returned by the server
                in = null;
                in = new BufferedReader(new InputStreamReader(url.openStream()));
                String str;
                while ((str = in.readLine()) != null) {
                    strBuff.append(str);
                }
                in.close();
            } catch (MalformedURLException e) {
            } catch (IOException e) {
            }


            HttpClient httpclient = new DefaultHttpClient();
            // specify the URL you want to post to

            HttpPost httppost = new HttpPost(f_url[0]);
            try {
                // create a list to store HTTP variables and their values

                nameValuePairs = new ArrayList<NameValuePair>();
                // add an HTTP variable and value pair
                nameValuePairs.clear();

                nameValuePairs.add(new BasicNameValuePair("ticketno", ticketBean.getTicketNo()));
                System.out.println("ticketno = " + ticketBean.getTicketNo());

                nameValuePairs.add(new BasicNameValuePair("verify_barcode", verifyBarcodeValue));
                System.out.println("verify_barcode = " + verifyBarcodeValue);


                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // send the variable and value, in other words post, to the URL
                HttpResponse response = httpclient.execute(httppost);

                return EntityUtils.toString(response.getEntity());

            } catch (ClientProtocolException e) {
                // process execption
            } catch (IOException e) {
                // process execption
            }

            return null;

        }


        @Override
        protected void onPostExecute(String jsonString) {

            prgDialog.dismiss();

            System.out.println("@#@#@#@#jsonString = " + jsonString);

            JSONObject jsonObject;

            try {
                if (jsonString == null) {
                    Toast toast = Toast.makeText(TicketDetails.this,
                            "Internet or server problem", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    mainObj = new JSONObject(jsonString);

                    int statuscode = mainObj.getInt("statuscode");
                    String statusmessage = mainObj.getString("statusmessage");
                    if (statuscode == 0) {
                        buttonStartWork.setVisibility(View.VISIBLE);
                        verify_barcode.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(TicketDetails.this, statusmessage + "", Toast.LENGTH_SHORT).show();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


}
