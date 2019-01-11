package com.wrms.spraymonitor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.wrms.spraymonitor.app.FarmerRecyclerAdapter;
import com.wrms.spraymonitor.app.RecyclerItemClickListener;
import com.wrms.spraymonitor.app.Synchronize;
import com.wrms.spraymonitor.dataobject.Credential;
import com.wrms.spraymonitor.dataobject.FarmerData;
import com.wrms.spraymonitor.utils.Uploadable;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FarmersList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FarmersList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FarmersList extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String FARMERS_LIST = "farmerList";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private ArrayList<FarmerData> farmesList;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FarmersList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param farmesList Parameter 1.
     * @param param2     Parameter 2.
     * @return A new instance of fragment FarmersList.
     */
    // TODO: Rename and change types and number of parameters
    public static FarmersList newInstance(ArrayList<FarmerData> farmesList, String param2) {
        FarmersList fragment = new FarmersList();
        Bundle args = new Bundle();
        args.putParcelableArrayList(FARMERS_LIST, farmesList);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            farmesList = getArguments().getParcelableArrayList(FARMERS_LIST);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private static final String TAG = "RecyclerViewExample";

    private RecyclerView mRecyclerView;

    private FarmerRecyclerAdapter adapter;

    TextView formAlert;
    DBAdapter db;

    public static Uploadable sUploadable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.farmers_forms, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.farmer_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        formAlert = (TextView) rootView.findViewById(R.id.farmerFormAlert);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        if (farmesList != null && farmesList.size() > 0) {
            formAlert.setVisibility(View.INVISIBLE);
        } else {
            formAlert.setVisibility(View.VISIBLE);
            formAlert.setText("No Farmer");
        }

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        FarmerData data = farmesList.get(position);
                        if (data.getSendingStatus().equals(DBAdapter.SAVE)) {
                            registerRequest(data);
                        } else if (data.getSendingStatus().equals(DBAdapter.SUBMIT)) {
                            confirmationRequest(data);
                        }
                    }
                })
        );

        System.out.println("(sprayMonitoringItemList!=null) : " + (farmesList != null));
        /*if(farmesList!=null) {
            Collections.sort(sprayMonitoringItemList);
        }*/
        if (farmesList != null) {
            Collections.sort(farmesList);
        }
        db = new DBAdapter(getActivity());
        db.open();
        adapter = new FarmerRecyclerAdapter(getActivity(), farmesList);
        mRecyclerView.setAdapter(adapter);
        registerForContextMenu(mRecyclerView);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            int position = -1;
            try {
                position = adapter.getPosition();
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage(), e);
                return super.onContextItemSelected(item);
            }
            switch (item.getItemId()) {
                case R.id.action_delete:
                    Toast.makeText(getActivity(), "Farmer Delete Action fire", Toast.LENGTH_SHORT).show();
                    deleteFarmer(farmesList.get(position));
                    break;
            }
            return super.onContextItemSelected(item);
        }
        return false;
    }


    private boolean deleteFarmer(final FarmerData farmerData) {
        boolean isDeleted = false;
        String message = "";
        String title = "";
        if (farmerData.getSendingStatus().equals(DBAdapter.SENT) || farmerData.getSendingStatus().equals(DBAdapter.SUBMIT)) {
            title = "Delete from the list";
            message = "Do you want to delete this farmer from this list?";
        } else {
            title = "Farmer is not added on server";
            message = "Do you want to delete this farmer?";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).
                setMessage(message).
                setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (farmerData.getSendingStatus().equals(DBAdapter.SUBMIT)) {
                            int j = db.db.delete(DBAdapter.TABLE_FARMER,DBAdapter.FARMER_CONTACT+" = '"+farmerData.getFarmerContact()+"'",null);
                            if(j!=-1){
                                farmesList.remove(farmerData);
                                adapter.notifyDataSetChanged();
                            }
                            System.out.println("Inside Submit with delete status : "+j);

                        }

                        if (farmerData.getSendingStatus().equals(DBAdapter.SENT)) {

                            ContentValues values = new ContentValues();
                            values.put(DBAdapter.FARMER_ID, farmerData.getFarmerId());
                            values.put(DBAdapter.FIRST_NAME, farmerData.getFirstName());
                            values.put(DBAdapter.LAST_NAME, farmerData.getLastName());
                            values.put(DBAdapter.FARMER_CONTACT, farmerData.getFarmerContact());
                            values.put(DBAdapter.VILLAGE_ID, farmerData.getVillageId());
                            values.put(DBAdapter.ADDRESS, farmerData.getAddressLine());
                            values.put(DBAdapter.SENDING_STATUS, farmerData.getSendingStatus());
                            values.put(DBAdapter.CROP_ID, farmerData.getCropId());
                            values.put(DBAdapter.CREATED_BY, DBAdapter.CREATED_BY_SERVER);
                            long isInserted = 0;
                            //update existing id
                            isInserted = db.db.update(DBAdapter.TABLE_FARMER, values, DBAdapter.FARMER_CONTACT + " = '" + farmerData.getFarmerContact() + "'", null);


                            if (isInserted != -1) {
                                farmesList.remove(farmerData);
                                adapter.notifyDataSetChanged();
                            }
                            System.out.println("Inside Sent with update status : "+isInserted);
                        }

                        if (farmerData.getSendingStatus().equals(DBAdapter.SAVE)) {
                            int j = db.db.delete(DBAdapter.TABLE_FARMER,DBAdapter.FARMER_CONTACT+" = '"+farmerData.getFarmerContact()+"'",null);
                            if(j!=-1){
                                farmesList.remove(farmerData);
                                adapter.notifyDataSetChanged();
                            }
                            System.out.println("Inside Save with delete status : "+j);
                        }
                        dialogInterface.cancel();
                    }
                }).
                setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                    }
                });
        builder.show();


        return isDeleted;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    ProgressDialog progressDialog;

    private void confirmationRequest(final FarmerData data) {

        String methodOnServer = "farmerStatus";

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, Synchronize.URL + methodOnServer,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String confirmationResponse) {
                        progressDialog.dismiss();
                        try {
                            System.out.println("Farmer Confirmation Response : " + confirmationResponse);
                            JSONObject jsonObject = new JSONObject(confirmationResponse);

                            if (jsonObject.has("status")) {
                                if (jsonObject.getString("status").equals("success") || jsonObject.getString("status").equals("ContactAlreadyExist")) {
                                    if (jsonObject.has("FarmerId")) {
                                        JSONObject farmerJsonObject = jsonObject.getJSONObject("FarmerDetail");

                                        data.setFarmerId(farmerJsonObject.getString("FarmerId"));
                                        data.setFarmerContact(farmerJsonObject.getString("FarmerContact"));
                                        data.setFirstName(farmerJsonObject.getString("FirstName"));
                                        data.setLastName(farmerJsonObject.getString("LastName"));
                                        data.setVillageId(farmerJsonObject.getString("VillageId"));
                                        data.setAddressLine(farmerJsonObject.getString("FarmerAddress"));
                                        data.setSendingStatus(DBAdapter.SENT);

                                        Toast.makeText(getActivity(), "Registration request has been submitted", Toast.LENGTH_LONG).show();

                                        data.save(db);
                                    } else {
                                        Toast.makeText(getActivity(), "Farmer Id not Found", Toast.LENGTH_LONG).show();
                                    }

                                } else if (jsonObject.getString("status").equals("fail")) {
                                    Toast.makeText(getActivity(), "Registration request has been refused", Toast.LENGTH_LONG).show();
                                    data.setSendingStatus(DBAdapter.REJECTED);
                                    data.save(db);
                                }
                                adapter.notifyDataSetChanged();
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
                progressDialog.dismiss();
                volleyError.printStackTrace();
                Toast.makeText(getActivity(), "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                Credential credential = new Credential();
                Cursor credentialCursor = db.getCredential();
                if (credentialCursor.getCount() > 0) {
                    credentialCursor.moveToFirst();
                    credential = new Credential(credentialCursor);
                }
                credentialCursor.close();
                map.put("AccountId", credential.getUserId());
                map.put("FarmerContact", data.getFarmerContact());
//                map.put("FarmerCode", data.getFarmerId());
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        stringVarietyRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        progressDialog = ProgressDialog.show(getActivity(), "",
                "Please Wait.....", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);
    }

    private void notRegisteredAlert(){

    }


    private void registerRequest(final FarmerData data) {

        String methodeName = "farmerRegistration";

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, Synchronize.URL + methodeName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String registrationResponse) {
                        progressDialog.dismiss();
                        try {
                            System.out.println("Register Farmer Response : " + registrationResponse);
                            JSONObject jsonObject = new JSONObject(registrationResponse);

                            if (jsonObject.has("status")) {
                                if (jsonObject.getString("status").equals("success") || jsonObject.getString("status").equals("ContactAlreadyExist")) {
                                    Toast.makeText(getActivity(), "Registration request has been submitted", Toast.LENGTH_LONG).show();
                                    data.setSendingStatus(DBAdapter.SUBMIT);
                                    data.save(db);
                                    adapter.notifyDataSetChanged();

                                } else {
                                    Toast.makeText(getActivity(), "Registration request has been refused", Toast.LENGTH_LONG).show();
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
                progressDialog.dismiss();
                volleyError.printStackTrace();
                Toast.makeText(getActivity(), "Not able to connect with server", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), TabedActivity.class);
                startActivity(intent);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                System.out.println("Get Params has been called");
                Map<String, String> map = data.getParametersInMap(db);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        stringVarietyRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        progressDialog = ProgressDialog.show(getActivity(), "",
                "Please Wait.....", true);

        AppController.getInstance().addToRequestQueue(stringVarietyRequest);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
