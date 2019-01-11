package com.wrms.spraymonitor;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wrms.spraymonitor.app.AppController;
import com.wrms.spraymonitor.app.Constents;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.Synchronize;
import com.wrms.spraymonitor.dataobject.Credential;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DayWisePerformanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayWisePerformanceFragment extends Fragment {

    public DayWisePerformanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DayWisePerformanceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DayWisePerformanceFragment newInstance() {
        DayWisePerformanceFragment fragment = new DayWisePerformanceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    Button fromDateButton;
    Button endDateButton;
    Button showButton;
    LinearLayout dataContainer;
    LayoutInflater innerInflater;
    LinearLayout textDurationLayout;
    TextView fromDateTxt;
    TextView endDateTxt;
    TextView toTxt;

    String startDateString = null;
    String endDateString = null;
    DBAdapter db;
    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day_wise_performance, container, false);

        fromDateButton = (Button) view.findViewById(R.id.fromDateButton);
        endDateButton = (Button) view.findViewById(R.id.endDateButton);
        showButton = (Button) view.findViewById(R.id.showButton);
        dataContainer = (LinearLayout) view.findViewById(R.id.dataContainer);
        innerInflater = LayoutInflater.from(getActivity());
        textDurationLayout = (LinearLayout) view.findViewById(R.id.textDurationLayout);
        fromDateTxt = (TextView) view.findViewById(R.id.fromDateTxt);
        endDateTxt = (TextView) view.findViewById(R.id.endDateTxt);
        toTxt = (TextView) view.findViewById(R.id.toTxt);

        db = new DBAdapter(getActivity());
        db.open();

        fromDateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                final View view = LayoutInflater.from(getActivity()).inflate(R.layout.date_picker, null);
                adb.setView(view);
                final Dialog dialog;
                adb.setPositiveButton("Add", new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                        DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker1);
                        java.util.Date date = null;
                        Calendar cal = GregorianCalendar.getInstance();
                        cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                        date = cal.getTime();
                        String selectedDate = sdf.format(date);
                        //make it from the date night 12 am
                        startDateString = selectedDate+" 00:00:00";

                        fromDateTxt.setText(selectedDate);
                        toTxt.setText("To");
                    }
                });
                dialog = adb.create();
                dialog.show();
            }
        });

        endDateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                final View view = LayoutInflater.from(getActivity()).inflate(R.layout.date_picker, null);
                adb.setView(view);
                final Dialog dialog;
                adb.setPositiveButton("Add", new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                        DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker1);
                        java.util.Date date = null;
                        Calendar cal = GregorianCalendar.getInstance();
                        cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                        date = cal.getTime();
                        String selectedDate = Constents.sdf.format(date);
                        endDateString = selectedDate;

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String selectedDateString = sdf.format(date);

                        Date currentDate = new Date();
                        String currentDateString = sdf.format(currentDate);
                        if(!(currentDateString.equals(selectedDateString))){
                            if(date.compareTo(currentDate)<0){
                                endDateString = selectedDateString+" 23:59:59";
                            }
                        }

                        endDateTxt.setText(sdf.format(date));
                    }
                });
                dialog = adb.create();
                dialog.show();
            }
        });

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()) {
                    performanceRequest();
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    private boolean isValid(){
        boolean isValid = true;

        if(startDateString == null || startDateString.isEmpty()){
            Toast.makeText(getActivity(),"Please Select Start Date",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(endDateString == null || endDateString.isEmpty()){
            Toast.makeText(getActivity(),"Please Select End Date",Toast.LENGTH_SHORT).show();
            return false;
        }

        return isValid;
    }


    ProgressDialog dialog;

    private void performanceRequest() {

        String methodeName = "datePerformance";
        dialog = ProgressDialog.show(getActivity(), null,
                "Please wait...", true);

        StringRequest performanceRequest = new StringRequest(Request.Method.POST, Synchronize.URL + methodeName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String performanceResponse) {
                        dialog.dismiss();
                        try {
                            System.out.println("Performance Response : " + performanceResponse);
                            JSONObject jsonObject = new JSONObject(performanceResponse);
//{{"status":"success","performance":[{"date":"2016-06-15","village_code":"648986","acreCovered":1}]}
                            if (jsonObject.has("status")) {
                                if (jsonObject.getString("status").equals("success")) {
                                    JSONArray performanceArray = jsonObject.getJSONArray("performance");
                                    if (performanceArray.length() > 0) {

                                        dataContainer.removeAllViews();

                                        final View headerView = innerInflater.inflate(R.layout.performance_list_item, null, false);
                                        TextView snoTxt = (TextView) headerView.findViewById(R.id.snoTxt);
                                        snoTxt.setTypeface(Typeface.DEFAULT_BOLD);
                                        snoTxt.setTextColor(getResources().getColor(R.color.color_primary_dark));

                                        TextView dateTxt = (TextView) headerView.findViewById(R.id.dateTxt);
                                        dateTxt.setTypeface(Typeface.DEFAULT_BOLD);
                                        dateTxt.setTextColor(getResources().getColor(R.color.color_primary_dark));

                                        TextView villageTxt = (TextView) headerView.findViewById(R.id.villageTxt);
                                        villageTxt.setTypeface(Typeface.DEFAULT_BOLD);
                                        villageTxt.setTextColor(getResources().getColor(R.color.color_primary_dark));

                                        TextView acreTxt = (TextView) headerView.findViewById(R.id.acreTxt);
                                        acreTxt.setTypeface(Typeface.DEFAULT_BOLD);
                                        acreTxt.setTextColor(getResources().getColor(R.color.color_primary_dark));

                                        dataContainer.addView(headerView);

                                        for (int i = 0; i < performanceArray.length(); i++) {
                                            JSONObject performanceObject = performanceArray.getJSONObject(i);
                                            String dateString = performanceObject.getString("date");
                                            String villageString = performanceObject.getString("village");
                                            String acreCoveredString = performanceObject.getString("acreCovered");

                                            final View confirmedProduct = innerInflater.inflate(R.layout.performance_list_item, null, false);
                                            TextView snoTxt1 = (TextView) confirmedProduct.findViewById(R.id.snoTxt);
                                            snoTxt1.setText(String.valueOf(i + 1));

                                            TextView dateTxt1 = (TextView) confirmedProduct.findViewById(R.id.dateTxt);
                                            dateTxt1.setText(dateString);

                                            TextView villageTxt1 = (TextView) confirmedProduct.findViewById(R.id.villageTxt);
                                            villageTxt1.setText(villageString);

                                            TextView acreTxt1 = (TextView) confirmedProduct.findViewById(R.id.acreTxt);
                                            acreTxt1.setText(acreCoveredString);

                                            dataContainer.addView(confirmedProduct);

                                        }


                                    }

                                } else {
                                    if (jsonObject.has("message")) {
                                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Request has been refused", Toast.LENGTH_LONG).show();
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                dialog.cancel();
                Toast.makeText(getActivity(), "Not able to connect with server", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Credential credential = new Credential();
                Cursor credentialCursor = db.getCredential();
                if (credentialCursor.getCount() > 0) {
                    credentialCursor.moveToFirst();
                    credential = new Credential(credentialCursor);
                }
                credentialCursor.close();

                Map<String, String> map = new HashMap<>();
                map.put("start_date", startDateString);
                map.put("end_date", endDateString);
                map.put("fo_code", credential.getFoCode());
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        performanceRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(performanceRequest);
    }


}
