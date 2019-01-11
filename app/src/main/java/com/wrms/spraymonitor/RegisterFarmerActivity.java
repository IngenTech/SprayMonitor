package com.wrms.spraymonitor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.wrms.spraymonitor.app.CustomeAutoCompleteAdapter;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.FiltrableStringAdapter;
import com.wrms.spraymonitor.app.Synchronize;
import com.wrms.spraymonitor.dataobject.ContentData;
import com.wrms.spraymonitor.dataobject.Credential;
import com.wrms.spraymonitor.dataobject.FarmerData;
import com.wrms.spraymonitor.dataobject.SprayMonitorData;
import com.wrms.spraymonitor.utils.AppManager;
import com.wrms.spraymonitor.utils.Uploadable;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RegisterFarmerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    EditText firstName;
    EditText lastName;
    EditText addressEdtxt;
    AutoCompleteTextView farmerContact;
    Spinner state;
    Spinner district;
    Spinner tehsil;
    AutoCompleteTextView village;

    DBAdapter db;

    Cursor districtCursor;
    Cursor tehsilCursor;
    Cursor stateCursor;

    boolean isStateSelection = false;
    boolean isDistrictSelection = false;
    boolean isTehsilSelection = false;

    FarmerData data;
    SprayMonitorData sprayMonitorData;
    Credential credential;
    String id;


    TextView farmerContactTxt;
    TextView farmerFirstNameTxt;
    TextView farmerLastNameTxt;
    TextView stateTxt;
    TextView districtTxt;
    TextView tehsilTxt;
    TextView villageTxt;
    TextView addressTxt;

    Button saveBTN;

    public static Uploadable sUploadable;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menue_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_add: {

                if (isValid()) {
                    if(actionNextValid(data)) {
                        data.setSendingStatus(DBAdapter.SAVE);
                        data.save(db);
                        /*if (sprayMonitorData != null) {
                            sprayMonitorData.setFirstName(data.getFirstName());
                            sprayMonitorData.setFarmerId(data.getFarmerId(), db);
                            sprayMonitorData.setFarmerContact(data.getFarmerContact());
                            sprayMonitorData.setState(data.getStateId());
                            sprayMonitorData.setDistrict(data.getDistrictId());
                            sprayMonitorData.setTehsil(data.getTehsilId());
                            sprayMonitorData.setVillage(data.getVillageId());
                            sprayMonitorData.setCrop(data.getCropId());
                            sprayMonitorData.setTotalAcrage(data.getTotalAcre());
                            sprayMonitorData.save(db);
                        }*/

//                        Toast.makeText(RegisterFarmerActivity.this,"Make a Submit Action",Toast.LENGTH_SHORT).show();
                        registerRequest(data);
                    }
                }

                return true;

            }

            case android.R.id.home: {

                finish();
                return true;

            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_farmer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);


        db = new DBAdapter(this);
        db.open();

        sprayMonitorData = getIntent().getParcelableExtra(Constents.DATA);
        credential = new Credential();
        Cursor credentialCursor = db.getCredential();
        if (credentialCursor.getCount() > 0) {
            credentialCursor.moveToFirst();
            credential = new Credential(credentialCursor);
        }
        credentialCursor.close();
        if (data == null) {
            data = new FarmerData();
            id = credential.getImei()+"_"+String.valueOf(System.currentTimeMillis());;
            data.setFarmerId(id);
            data.setFarmerCode(data.getFarmerId());
        }
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        farmerContact = (AutoCompleteTextView) findViewById(R.id.farmerContact);
        state = (Spinner) findViewById(R.id.state);
        district = (Spinner) findViewById(R.id.district);
        tehsil = (Spinner) findViewById(R.id.tehsil);
        village = (AutoCompleteTextView) findViewById(R.id.village);
        addressEdtxt = (EditText)findViewById(R.id.address);

        farmerFirstNameTxt = (TextView) findViewById(R.id.farmerFirstNameTxt);
        farmerLastNameTxt = (TextView) findViewById(R.id.farmerLastNameTxt);
        farmerContactTxt = (TextView) findViewById(R.id.farmerContactTxt);
        stateTxt = (TextView) findViewById(R.id.stateTxt);
        districtTxt = (TextView) findViewById(R.id.districtTxt);
        tehsilTxt = (TextView) findViewById(R.id.tehsilTxt);
        villageTxt = (TextView) findViewById(R.id.villageTxt);
        addressTxt = (TextView) findViewById(R.id.addressTxt);

        saveBTN = (Button)findViewById(R.id.register_formar);
        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValid()) {
                    if(actionNextValid(data)) {
                        data.setSendingStatus(DBAdapter.SAVE);
                        data.save(db);
                        registerRequest(data);
                    }
                }

            }
        });

        setSubtitleLanguage();

        final Cursor farmerListCursor = db.getFarmerList();
        String[] farmerContactArray = new String[farmerListCursor.getCount()];
        if(farmerListCursor.getCount()>0){
            farmerListCursor.moveToFirst();
            int i =0;
            do{
                farmerContactArray[i] = farmerListCursor.getString(farmerListCursor.getColumnIndex(DBAdapter.FARMER_CONTACT));
                i++;
            }while(farmerListCursor.moveToNext());
        }
        ArrayAdapter<String> adp=new ArrayAdapter<String>(RegisterFarmerActivity.this,
                android.R.layout.simple_dropdown_item_1line,farmerContactArray);
        farmerContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(RegisterFarmerActivity.this);
                builder.setTitle("Already Registered");
                builder.setMessage("Farmer already registered please enter new number.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                farmerContact.setText("");
                                dialogInterface.dismiss();

                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                builder.show();

            }
        });

        farmerContact.setThreshold(1);
        farmerContact.setAdapter(adp);

        stateCursor = db.getAllState();
        final int stateCount = stateCursor.getCount();
        String[] stateStringArray = new String[stateCount + 1];
        stateStringArray[0] = "Select State";
        System.out.println("stateCount : "+stateCount);
        if (stateCount > 0) {
            stateCursor.moveToFirst();
            for (int i = 1; i <= stateCount; i++) {
                stateStringArray[i] = stateCursor.getString(stateCursor.getColumnIndex(DBAdapter.STATE));
                stateCursor.moveToNext();
            }
        }

        ArrayAdapter<String> stateArrayAdapter = new ArrayAdapter<String>(RegisterFarmerActivity.this, android.R.layout.simple_spinner_item, stateStringArray); //selected item will look like a spinner set from XML
        stateArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state.setAdapter(stateArrayAdapter);
        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String stateId = "-1";
                System.out.println("Inside State selection " + position);
                if (position > 0) {
                    stateCursor.moveToPosition(position - 1);
                    stateId = stateCursor.getString(stateCursor.getColumnIndex(DBAdapter.STATE_ID));
                    String stateName = stateCursor.getString(stateCursor.getColumnIndex(DBAdapter.STATE));
                }
                data.setStateId(stateId);
                setDistrict(stateId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (data != null) {
            setDefaultData(data);
        }

        AppManager.isGPSenabled(this);
    }

    private void setSubtitleLanguage(){
        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(this);
        String languagePreference = myPreference.getString(getResources().getString(R.string.language_pref_key), "1");
        int languageConstant = Integer.parseInt(languagePreference);

        System.out.println("language Constant : "+languageConstant);
        switch (languageConstant){
            case 1 :
                setEnglishText();
                break;
            case 2 :
                setHindiText();
                break;
            default:
                setEnglishText();
        }
    }

    private void setEnglishText(){
        Typeface tf = Typeface.DEFAULT;

        farmerContactTxt.setTypeface(tf);
        farmerContactTxt.setText(getResources().getString(R.string.farmer_contact_txt));

        farmerFirstNameTxt.setTypeface(tf);
        farmerFirstNameTxt.setText(getResources().getString(R.string.farmer_first_name_txt));

        farmerLastNameTxt.setTypeface(tf);
        farmerLastNameTxt.setText(getResources().getString(R.string.farmer_last_name_txt));

        stateTxt.setTypeface(tf);
        stateTxt.setText(getResources().getString(R.string.state_txt));

        districtTxt.setTypeface(tf);
        districtTxt.setText(getResources().getString(R.string.district_txt));

        tehsilTxt.setTypeface(tf);
        tehsilTxt.setText(getResources().getString(R.string.tehsil_txt));

        villageTxt.setTypeface(tf);
        villageTxt.setText(getResources().getString(R.string.village_txt));

        addressTxt.setTypeface(tf);
        addressTxt.setText(getResources().getString(R.string.address_txt));

    }

    private void setHindiText(){
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/krdv011.ttf");

        farmerContactTxt.setTypeface(tf);
        farmerContactTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        farmerContactTxt.setText("fdlku dk e¨cby uÛ");

        farmerFirstNameTxt.setTypeface(tf);
        farmerFirstNameTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        farmerFirstNameTxt.setText("fdlku dk igyk uke");

        farmerLastNameTxt.setTypeface(tf);
        farmerLastNameTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        farmerLastNameTxt.setText("fdlku dk vafre uke");

        stateTxt.setTypeface(tf);
        stateTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        stateTxt.setText("jkT;");

        districtTxt.setTypeface(tf);
        districtTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        districtTxt.setText("ftyk");

        tehsilTxt.setTypeface(tf);
        tehsilTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tehsilTxt.setText("rglhy");

        villageTxt.setTypeface(tf);
        villageTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        villageTxt.setText("xk¡o");

        addressTxt.setTypeface(tf);
        addressTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        addressTxt.setText("irk");

    }

    ProgressDialog dialog;
    private void registerRequest(final FarmerData data) {

        String methodeName = "farmerRegistration";
        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, Synchronize.URL+methodeName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String registrationResponse) {
                        dialog.cancel();
                        try {
                            System.out.println("Register Farmer Response : " + registrationResponse);
                            JSONObject jsonObject = new JSONObject(registrationResponse);

                            if(jsonObject.has("status")){
                                if(jsonObject.getString("status").equals("success")||jsonObject.getString("status").equals("ContactAlreadyExist")){

                                    if(jsonObject.has("FarmerId")){
                                        String farmerId = jsonObject.getString("FarmerId");
                                        data.setFarmerId(farmerId);
                                        data.setFarmerCode(farmerId);
                                        Toast.makeText(RegisterFarmerActivity.this, "Registration request has been submitted", Toast.LENGTH_LONG).show();
                                        data.setSendingStatus(DBAdapter.SENT);
                                        data.save(db);
                                    }else{
                                        Toast.makeText(RegisterFarmerActivity.this, "Registration request has been submitted", Toast.LENGTH_LONG).show();
                                        data.setFarmerCode(data.getFarmerId());
                                        data.setSendingStatus(DBAdapter.SUBMIT);
                                        data.save(db);
//                                        Toast.makeText(RegisterFarmerActivity.this, "Farmer Id not Found", Toast.LENGTH_LONG).show();
                                    }

                                    RegisterFarmerActivity.this.finish();

                                }else{
                                    Toast.makeText(RegisterFarmerActivity.this, "Registration request has been refused", Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(RegisterFarmerActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterFarmerActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();
                            notRegisteredAlert(data);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                dialog.cancel();
                Toast.makeText(RegisterFarmerActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
                notRegisteredAlert(data);
                /*Intent intent = new Intent(RegisterFarmerActivity.this, TabedActivity.class);
                startActivity(intent);*/
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                System.out.println("Get Params has been called");
                Map<String,String> map = data.getParametersInMap(db);
                for (Map.Entry<String, String> entry : map.entrySet())
                {
                    System.out.println(entry.getKey() + " : " + entry.getValue());

                }
                return map;
            }
        };

        stringVarietyRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        dialog = ProgressDialog.show(RegisterFarmerActivity.this, "Registering",
                "Please wait...", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);
    }

    private void notRegisteredAlert(final FarmerData data){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterFarmerActivity.this);
        builder.setTitle("Not Registered").
                setMessage("Do you still want to continue?").
                setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        RegisterFarmerActivity.this.finish();
                        /*Intent intent = new Intent(RegisterFarmerActivity.this, DetailActivity.class);
                        intent.putExtra(Constents.FARMER_DATA,data);
                        startActivity(intent);*/
                    }
                }).
                setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        data.delete(db);
                        RegisterFarmerActivity.this.finish();
                    }
                });
        builder.show();

    }


    public boolean actionNextValid(FarmerData data){
        boolean isValid = true;

        boolean farmerContact = (data.getFarmerContact() != null && data.getFarmerContact().trim().length() > 0);
        if (!farmerContact){
            Toast.makeText(RegisterFarmerActivity.this, "Farmer Contact number Required", Toast.LENGTH_LONG).show();
            return false;
        }

        boolean farmerName = (data.getFirstName() != null && data.getFirstName().trim().length() > 0);
        if (!farmerName) {
            Toast.makeText(RegisterFarmerActivity.this, "Farmer Name Required", Toast.LENGTH_LONG).show();
            return false;
        }

        /*boolean farmerLastName = (data.getLastName() != null && data.getLastName().trim().length() > 0);
        if (!farmerLastName) {
            Toast.makeText(RegisterFarmerActivity.this, "Farmer Last Name Required", Toast.LENGTH_LONG).show();
            return false;
        }

        if (data.getVillageId().equals("-1")) {
            Toast.makeText(RegisterFarmerActivity.this, "Please select Village", Toast.LENGTH_LONG).show();
            return false;
        }

        if (data.getTerritory().equals("-1")) {
            Toast.makeText(DetailActivity.this, "Territory Required", Toast.LENGTH_LONG).show();
            return false;
        }

        if (data.getDistrict().equals("-1")) {
            Toast.makeText(DetailActivity.this, "District Required", Toast.LENGTH_LONG).show();
            return false;
        }

        if (data.getTehsil().equals("-1")) {
            Toast.makeText(DetailActivity.this, "Tehsil Required", Toast.LENGTH_LONG).show();
            return false;
        }*/

        return isValid;
    }

    private boolean isValid() {
        boolean isValid = true;

        data.setFirstName(firstName.getText().toString());
        data.setLastName(lastName.getText().toString());
        String farmerContactString = farmerContact.getText().toString();

        if (farmerContactString==null || farmerContactString.length()<9){

            Toast.makeText(this,"Please enter valid mobile number",Toast.LENGTH_SHORT).show();
            return false;
        }

        Cursor getFarmerById = db.getFarmerById(farmerContactString);
        if(getFarmerById.moveToFirst()){
            Toast.makeText(this,"Contact No. Already exist",Toast.LENGTH_SHORT).show();
            return false;
        }
        data.setFarmerContact(farmerContactString);
        data.setVillageName(village.getText().toString());
        data.setAddressLine(addressEdtxt.getText().toString());

        Cursor farmerDetailCursor = db.getFarmerByContact(data.getFarmerContact());
        if(farmerDetailCursor.moveToFirst()){
            Toast.makeText(RegisterFarmerActivity.this, "Farmer Contact Already Exist", Toast.LENGTH_LONG).show();
            return false;
        }

        return isValid;
    }

    private void setDefaultData(FarmerData data) {

        String stateId = data.getStateId();
        String district = data.getDistrictId();
        String tehsil = data.getTehsilId();

        if (!district.equals("-1")) {
            isDistrictSelection = true;
        }
        if (!tehsil.equals("-1")) {
            isTehsilSelection = true;
        }


        int stateCount = stateCursor.getCount();
        System.out.println("stateId : " + data.getStateId() + " stateCount : " + stateCount);
        if (stateCount > 0 && (!data.getStateId().equals("-1"))) {
            stateCursor.moveToFirst();
            for (int d = 1; d <= stateCount; d++) {
                if (data.getStateId().trim().equals(stateCursor.getString(stateCursor.getColumnIndex(DBAdapter.STATE_ID)))) {
                    state.setSelection(d, true);
                    isStateSelection = false;
                    break;
                }
                stateCursor.moveToNext();
            }
        }

        if (data.getFirstName() != null && data.getFirstName().length() > 0) {
            firstName.setText(data.getFirstName());
        }

        if (data.getFarmerContact() != null && data.getFarmerContact().length() > 0) {
            farmerContact.setText(data.getFarmerContact());
        }

        if (data.getVillageId() != null && data.getVillageId().length() > 0) {
            Cursor villageCursor = db.getVillageById(data.getVillageId());
            String villageName = "";
            if(villageCursor.moveToFirst()){
                villageName = villageCursor.getString(villageCursor.getColumnIndex(DBAdapter.VILLAGE));
            }
            villageCursor.close();
            village.setText(villageName);
        }

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_save: {
                if (isValid()) {
                    data.setSendingStatus(DBAdapter.SAVE);
                    if (data.getFarmerContact() != null && data.getFarmerContact().trim().length() > 0) {
                        if (data.save(db)) {
                            Toast.makeText(RegisterFarmerActivity.this, "Detail has been saved", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegisterFarmerActivity.this, "Could not save the form", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(RegisterFarmerActivity.this, "Farmer Contact Required", Toast.LENGTH_LONG).show();
                    }
                }
                return true;

            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }

    }99910459475
*/

    private void setDistrict(final String stateId) {
        String newValue = null;
        String newValueID = null;

        districtCursor = db.getDistrictByState(stateId);

        System.out.println("territoryId inside district : " + stateId + " districtCursor.getCount() : " + districtCursor.getCount());

        final int districtCount = districtCursor.getCount();
       // String[] districtStringArray = new String[districtCount + 1];

        ArrayList<String> districtStringArray = new ArrayList<>();
        final ArrayList<String> districtIDArray = new ArrayList<>();

        districtStringArray.add("Select District");

        if (districtCount > 0) {
            districtCursor.moveToFirst();
            for (int i = 1; i <= districtCount; i++) {

                newValue = districtCursor.getString(districtCursor.getColumnIndex(DBAdapter.DISTRICT));
                newValueID = districtCursor.getString(districtCursor.getColumnIndex(DBAdapter.DISTRICT_ID));

                if (!districtStringArray.contains(newValue)) {
                    districtStringArray.add(newValue);
                }

                if (!districtIDArray.contains(newValueID)) {
                    districtIDArray.add(newValueID);
                }

                districtCursor.moveToNext();
            }

        }

        ArrayAdapter<String> districtArrayAdapter = new ArrayAdapter<String>(RegisterFarmerActivity.this, android.R.layout.simple_spinner_item, districtStringArray); //selected item will look like a spinner set from XML
        districtArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        district.setAdapter(districtArrayAdapter);
        district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String districtId = "-1";
                if (position > 0) {
                    districtCursor.moveToPosition(position - 1);
                    districtId = districtIDArray.get(position-1);
                    String blockName = districtCursor.getString(districtCursor.getColumnIndex(DBAdapter.DISTRICT));

                }
                data.setDistrictId(districtId);
                setTehsil(districtId);
               /* if (block_switch.isChecked()) {
                    setLocation(districtId);
                } else {
                    setLocation(districtId);
                }*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (isDistrictSelection) {
            int districtC = districtCursor.getCount();
            System.out.println("districtId : " + data.getDistrictId() + " districtCount : " + districtC);
            if (districtC > 0 && (!data.getDistrictId().equals("-1"))) {
                districtCursor.moveToFirst();
                for (int d = 1; d <= districtC; d++) {
                    if (data.getDistrictId().trim().equals(districtCursor.getString(districtCursor.getColumnIndex(DBAdapter.DISTRICT_ID)))) {
                        district.setSelection(d, true);
                        isDistrictSelection = false;
                        break;
                    }
                    districtCursor.moveToNext();
                }
            }
        }
    }

    private void setTehsil(String districtId) {
        System.out.println("districtId inside setTehsil : " + districtId);
        tehsilCursor = db.getTehsilByDistrict(districtId);

        final int tehsilCount = tehsilCursor.getCount();
        String[] tehsilStringArray = new String[tehsilCount + 1];
        tehsilStringArray[0] = "Select Tehsil";
        if (tehsilCount > 0) {
            tehsilCursor.moveToFirst();
            for (int i = 1; i <= tehsilCount; i++) {
                tehsilStringArray[i] = tehsilCursor.getString(tehsilCursor.getColumnIndex(DBAdapter.TEHSIL));
                tehsilCursor.moveToNext();
            }
        }

        ArrayAdapter<String> tehsilArrayAdapter = new ArrayAdapter<String>(RegisterFarmerActivity.this, android.R.layout.simple_spinner_item, tehsilStringArray); //selected item will look like a spinner set from XML
        tehsilArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tehsil.setAdapter(tehsilArrayAdapter);
        tehsil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    tehsilCursor.moveToPosition(position - 1);
                    String tehsilId = tehsilCursor.getString(tehsilCursor.getColumnIndex(DBAdapter.TEHSIL_ID));
                    String tehsilName = tehsilCursor.getString(tehsilCursor.getColumnIndex(DBAdapter.TEHSIL));
                    data.setTehsilId(tehsilId);
                    setVillage(tehsilId);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (isTehsilSelection) {
            int tehCount = tehsilCursor.getCount();
            System.out.println("tehsilID : " + data.getTehsilId() + " tehsilCount : " + tehCount);
            if (tehCount > 0 && (!data.getTehsilId().equals("-1"))) {
                tehsilCursor.moveToFirst();
                for (int d = 1; d <= tehCount; d++) {
                    if (data.getTehsilId().trim().equals(tehsilCursor.getString(tehsilCursor.getColumnIndex(DBAdapter.TEHSIL_ID)))) {
                        tehsil.setSelection(d, true);
                        isTehsilSelection = false;

                        break;
                    }
                    tehsilCursor.moveToNext();
                }
            }
        }

    }

    Cursor villageCursor;
    private boolean isVillageSelection = false;
    private void setVillage(String tehsilId) {
        System.out.println("tehsilId inside setVillage : " + tehsilId);
        villageCursor = db.getVillageByTehsilId(tehsilId);

        final int villageCount = villageCursor.getCount();
        ArrayList<ContentData> villageStringArray = new ArrayList<>();

        if (villageCount > 0) {
            villageCursor.moveToFirst();
            for (int i = 0; i < villageCount; i++) {
                ContentData contentData = new ContentData();
                contentData.setTitle(villageCursor.getString(villageCursor.getColumnIndex(DBAdapter.VILLAGE)));
                contentData.setValue(villageCursor.getString(villageCursor.getColumnIndex(DBAdapter.VILLAGE_ID)));
                villageStringArray.add(contentData);
                villageCursor.moveToNext();
            }
        }
        villageCursor.close();

        CustomeAutoCompleteAdapter villageArrayAdapter = new CustomeAutoCompleteAdapter(RegisterFarmerActivity.this,R.layout.auto_complete_textview_item, villageStringArray);
        village.setAdapter(villageArrayAdapter);
        village.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ContentData selected = (ContentData) adapterView.getAdapter().getItem(i);
                data.setVillageId(selected.getValue());
                data.setVillageName(selected.getTitle());
            }
        });
        village.setThreshold(1);

        if (isVillageSelection) {
            int villCount = villageCursor.getCount();
            System.out.println("villCount : " + data.getVillageId() + " villageCount : " + villCount);
            if (villCount > 0 && (!data.getVillageId().equals("-1"))) {
                villageCursor.moveToFirst();
                for (int d = 1; d <= villCount; d++) {
                    if (data.getVillageId().trim().equals(villageCursor.getString(villageCursor.getColumnIndex(DBAdapter.VILLAGE_ID)))) {
                        village.setSelection(d);
                        isVillageSelection = false;

                        break;
                    }
                    villageCursor.moveToNext();
                }
            }
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i("MakeMachine", "onRestoreInstanceState()");
        state.setSelection(savedInstanceState.getInt("state"));
        district.setSelection(savedInstanceState.getInt("district"));
        tehsil.setSelection(savedInstanceState.getInt("tehsil"));
        firstName.setText(savedInstanceState.getString("firstName"));
        farmerContact.setText(savedInstanceState.getString("farmerContact"));
        village.setText(savedInstanceState.getString("village"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("state", state.getSelectedItemPosition());
        outState.putInt("district", district.getSelectedItemPosition());
        outState.putInt("tehsil", tehsil.getSelectedItemPosition());
        outState.putString("firstName", firstName.getText().toString());
        outState.putString("farmerContact", farmerContact.getText().toString());
        outState.putString("village", village.getText().toString());

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom()) ) {

                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                }catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }
        return ret;
    }

}
