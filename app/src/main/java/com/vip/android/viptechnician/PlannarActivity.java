package com.vip.android.viptechnician;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vip.android.viptechnician.adapters.CPRecyclerAdapter;
import com.vip.android.viptechnician.adapters.CalendarAdapter;
import com.vip.android.viptechnician.beans.CPBean;
import com.vip.android.viptechnician.beans.CalendarCollection;
import com.vip.android.viptechnician.util.VIPConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlannarActivity extends AppCompatActivity {

    public GregorianCalendar cal_month, cal_month_copy;
    private CalendarAdapter cal_adapter;
    private TextView tv_month;
    List<String> eventDates = new ArrayList<String>();
    ProgressDialog progressDialog;
    GridView gridview;
    RecyclerView recyclerPlanner;
    ArrayList<CPBean> cpBeanArrayList = new ArrayList<>();
    ArrayList<CPBean> cpDateList = new ArrayList<>();
    CPRecyclerAdapter cpRecyclerAdapter;
    String categoryId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plannar);

        getSupportActionBar().setTitle("Planner");
       /* CalendarCollection.date_collection_arr=new ArrayList<CalendarCollection>();
        CalendarCollection.date_collection_arr.add(new CalendarCollection("2017-11-22","John Birthday"));
        CalendarCollection.date_collection_arr.add(new CalendarCollection("2017-11-24","Client Meeting at 5 p.m."));
        CalendarCollection.date_collection_arr.add(new CalendarCollection("2017-11-26","A Small Party at my office"));
        CalendarCollection.date_collection_arr.add(new CalendarCollection("2017-11-28","Marriage Anniversary"));
        CalendarCollection.date_collection_arr.add(new CalendarCollection("2017-11-29","Live Event and Concert of sonu"));*/

       /* eventDates.add("2017-11-22");
        eventDates.add("2017-11-24");
        eventDates.add("2017-11-26");
        eventDates.add("2017-11-28");
        eventDates.add("2017-11-29");*/

        getPlannerData();

        recyclerPlanner= (RecyclerView) findViewById(R.id.recycler_planner);
        cal_month = (GregorianCalendar) GregorianCalendar.getInstance();
        cal_month_copy = (GregorianCalendar) cal_month.clone();
        cal_adapter = new CalendarAdapter(PlannarActivity.this, cal_month, CalendarCollection.date_collection_arr);
        //gridview.setAdapter(cal_adapter);

        tv_month = (TextView) findViewById(R.id.tv_month);
        tv_month.setText(android.text.format.DateFormat.format("MMMM yyyy", cal_month));

        ImageButton previous = (ImageButton) findViewById(R.id.ib_prev);

        previous.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setPreviousMonth();
                refreshCalendar();
            }
        });

        ImageButton next = (ImageButton) findViewById(R.id.Ib_next);
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setNextMonth();
                refreshCalendar();

            }
        });

        gridview = (GridView) findViewById(R.id.gv_calendar);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                //((CalendarAdapter) parent.getAdapter()).setSelected(v,position);
                String selectedGridDate = CalendarAdapter.day_string
                        .get(position);

                String[] separatedTime = selectedGridDate.split("-");
                String gridvalueString = separatedTime[2].replaceFirst("^0*","");
                int gridvalue = Integer.parseInt(gridvalueString);

                if ((gridvalue > 10) && (position < 8)) {
                    setPreviousMonth();
                    refreshCalendar();
                } else if ((gridvalue < 7) && (position > 28)) {
                    setNextMonth();
                    refreshCalendar();
                }
                //((CalendarAdapter) parent.getAdapter()).setSelected(v,position);
               /* if(CalendarCollection.date_collection_arr.contains(selectedGridDate)){

                    ((CalendarAdapter) parent.getAdapter()).getPositionList(selectedGridDate, PlannarActivity.this,v,position);
                }else{

                    ((CalendarAdapter) parent.getAdapter()).setSelected(v,position);
                }
*/
                Set<String> set = new HashSet<String>(eventDates);
                if (set.contains(selectedGridDate))
                {
                    //((CalendarAdapter) parent.getAdapter()).getPositionList(selectedGridDate, PlannarActivity.this,v,position);

                    int len=CalendarCollection.date_collection_arr.size();
                    for (int i = 0; i < len; i++) {
                        CalendarCollection cal_collection=CalendarCollection.date_collection_arr.get(i);
                        String event_date=cal_collection.date;

                        String event_message=cal_collection.event_message;

                        if (selectedGridDate.equals(event_date)) {

                            //((CalendarAdapter) parent.getAdapter()).getPositionList(selectedGridDate, PlannarActivity.this,v,position);
                            cpDateList.clear();
                            for(CPBean cpBean : cpBeanArrayList){
                                if(cpBean.getDate() != null && cpBean.getDate().equals(selectedGridDate)) {
                                    //something here
                                    /*channelId= Integer.parseInt(channelNames1.getChannelId());
                                    System.out.println("channelId is"+channelId);*/
                                    CPBean bean= new CPBean();
                                    bean.setDate(cpBean.getDate());
                                    bean.setCp_id(cpBean.getCp_id());
                                    bean.setCp_name(cpBean.getCp_name());
                                    bean.setCp_phone(cpBean.getCp_phone());
                                    bean.setCp_addr(cpBean.getCp_addr());
                                    cpDateList.add(bean);

                                }
                            }

                            cpRecyclerAdapter = new CPRecyclerAdapter(getApplicationContext(),
                                    cpDateList/*, categoryId*/);
                            int numberOfcolumn = 1;
                            recyclerPlanner.setLayoutManager(new GridLayoutManager(getApplicationContext(), numberOfcolumn, GridLayoutManager.VERTICAL, false));
                            recyclerPlanner.setItemAnimator(new DefaultItemAnimator());
                            recyclerPlanner.setAdapter(cpRecyclerAdapter);
                            recyclerPlanner.setNestedScrollingEnabled(false);


                        }else{

                            //setSelected(v,position);
                            //((CalendarAdapter) parent.getAdapter()).setSelected(v,position);

                        }}

                }else{
                    ((CalendarAdapter) parent.getAdapter()).setSelected(v,position,selectedGridDate);

                }
               /* int len=CalendarCollection.date_collection_arr.size();
                for (int i = 0; i < len; i++) {
                    CalendarCollection cal_collection=CalendarCollection.date_collection_arr.get(i);
                    String event_date=cal_collection.date;

                    String event_message=cal_collection.event_message;

                    if (selectedGridDate.equals(event_date)) {

                        ((CalendarAdapter) parent.getAdapter()).getPositionList(selectedGridDate, PlannarActivity.this,v,position);

                    }else{

                        //setSelected(v,position);
                        ((CalendarAdapter) parent.getAdapter()).setSelected(v,position);

                    }}
*/
            }

        });

    }

    private void getPlannerData() {

        showProgress();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                VIPConstants.SERVER_URL+"/ServiceEnginerApi/GetMecPlanner?TechnicianId=1",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Toast.makeText(ViewMore.this,response,Toast.LENGTH_LONG).show();
                        JSONObject jsonObj;
                        JSONArray jsonArray;
                        System.out.println("response of getPlannerData"+response);
                        progressDialog.dismiss();
                        CalendarCollection.date_collection_arr=new ArrayList<CalendarCollection>();
                        //cpBeanArrayList.clear();
                        try {
                            jsonObj = new JSONObject(response);
                            int statusCode = jsonObj.getInt("statuscode");
                            String statusMessage= jsonObj.getString("statusmessage");
                            JSONObject object;
                            if(statusCode==0) {
                                jsonArray = jsonObj.getJSONArray("Cplist");
                                for(int i=0; i<jsonArray.length();i++) {

                                    object= jsonArray.getJSONObject(i);
                                    CPBean cpBean= new CPBean();
                                    CalendarCollection collection= new CalendarCollection();
                                    collection.setDate(object.getString("datetovisit"));
                                    cpBean.setDate(object.getString("datetovisit"));
                                    collection.setName(object.getString("name"));
                                    cpBean.setCp_name(object.getString("name"));
                                    cpBean.setCp_id(object.getString("id"));
                                    collection.setId(object.getString("id"));
                                    collection.setContactno(object.getString("contactno"));
                                    cpBean.setCp_phone(object.getString("contactno"));
                                    collection.setPincode(object.getString("pincode"));
                                    collection.setCity(object.getString("city"));
                                    collection.setAddress(object.getString("address"));
                                    cpBean.setCp_addr(object.getString("address"));
                                    collection.setState(object.getString("state"));
                                    collection.setCountry(object.getString("country"));
                                    CalendarCollection.date_collection_arr.add(collection);
                                    eventDates.add(object.getString("datetovisit"));
                                    cpBeanArrayList.add(cpBean);

                                }

                                gridview.setAdapter(cal_adapter);

                                cpRecyclerAdapter = new CPRecyclerAdapter(getApplicationContext(),
                                        cpBeanArrayList/*, categoryId*/);
                                int numberOfcolumn = 1;
                                recyclerPlanner.setLayoutManager(new GridLayoutManager(getApplicationContext(), numberOfcolumn, GridLayoutManager.VERTICAL, false));
                                recyclerPlanner.setItemAnimator(new DefaultItemAnimator());
                                recyclerPlanner.setAdapter(cpRecyclerAdapter);
                                recyclerPlanner.setNestedScrollingEnabled(false);

                            }else{
                                Toast.makeText(getApplicationContext(),statusMessage,Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }){

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

    private void showProgress(){

        progressDialog= new ProgressDialog(PlannarActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    protected void setNextMonth() {
        if (cal_month.get(GregorianCalendar.MONTH) == cal_month.getActualMaximum(GregorianCalendar.MONTH)) {
            cal_month.set((cal_month.get(GregorianCalendar.YEAR) + 1),
                    cal_month.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            cal_month.set(GregorianCalendar.MONTH,
                    cal_month.get(GregorianCalendar.MONTH) + 1);
        }

    }

    protected void setPreviousMonth() {
        if (cal_month.get(GregorianCalendar.MONTH) == cal_month.getActualMinimum(GregorianCalendar.MONTH)) {
            cal_month.set((cal_month.get(GregorianCalendar.YEAR) - 1),
                    cal_month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            cal_month.set(GregorianCalendar.MONTH,
                    cal_month.get(GregorianCalendar.MONTH) - 1);
        }

    }

    public void refreshCalendar() {
        cal_adapter.refreshDays();
        cal_adapter.notifyDataSetChanged();
        tv_month.setText(android.text.format.DateFormat.format("MMMM yyyy", cal_month));
    }

}
