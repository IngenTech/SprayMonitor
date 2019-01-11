package com.wrms.spraymonitor;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.Synchronize;
import com.wrms.spraymonitor.dataobject.Credential;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WRMS on 09-06-2016.
 */
public class TopTenPerformenceFragment extends Fragment {

    public TopTenPerformenceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MothWisePerformanceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TopTenPerformenceFragment newInstance() {
        TopTenPerformenceFragment fragment = new TopTenPerformenceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    LinearLayout dataContainer;
    LayoutInflater innerInflater;
    DBAdapter db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_moth_wise_performance, container, false);

        dataContainer = (LinearLayout) view.findViewById(R.id.dataContainer);
        innerInflater = LayoutInflater.from(getActivity());
        db = new DBAdapter(getActivity());
        db.open();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        performanceRequest();
    }

    ProgressDialog dialog;

    private void performanceRequest() {

        String methodeName = "topPerformance";
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

                            if (jsonObject.has("status")) {
                                if (jsonObject.getString("status").equals("success")) {

                                    JSONArray performanceArray = jsonObject.getJSONArray("performance");
                                    if(performanceArray.length()>0){
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

                                        for(int i = 0 ; i<performanceArray.length();i++){
                                            JSONObject performanceObject = performanceArray.getJSONObject(i);
                                            String dateString = performanceObject.getString("date");
                                            String villageString = performanceObject.getString("village");
                                            String acreCoveredString = performanceObject.getString("acreCovered");

                                            final View confirmedProduct = innerInflater.inflate(R.layout.performance_list_item, null, false);
                                            TextView snoTxt1 = (TextView) confirmedProduct.findViewById(R.id.snoTxt);
                                            snoTxt1.setText(String.valueOf(i+1));

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
                                    }else {
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
                getActivity().finish();            }
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

