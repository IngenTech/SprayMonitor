package com.wrms.spraymonitor;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ParseException;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wrms.spraymonitor.app.Constents;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.background.AuthenticateService;
import com.wrms.spraymonitor.background.UploadSprayMonitoringData;
import com.wrms.spraymonitor.dataobject.FarmerData;
import com.wrms.spraymonitor.dataobject.ImageData;
import com.wrms.spraymonitor.dataobject.PaymentObject;
import com.wrms.spraymonitor.dataobject.ProductData;
import com.wrms.spraymonitor.dataobject.SprayCropData;
import com.wrms.spraymonitor.dataobject.SprayMonitorData;
import com.wrms.spraymonitor.dataobject.TankFillingData;
import com.wrms.spraymonitor.dataobject.VideoData;
import com.wrms.spraymonitor.utils.AppManager;
import com.wrms.spraymonitor.utils.Uploadable;

import java.util.ArrayList;
import java.util.Date;

public class SprayDetailThreeActivity extends AppCompatActivity {
    EditText amtCollected;
    Spinner amountCollectedBy;
    EditText remark;
    TextView amountCollectedTag;

    SprayMonitorData data;
    DBAdapter db;

    String districtName;

    TextView totalAcre;
    EditText actualAcreCovered;

    EditText balanceEdt;
    EditText amountReceivableEdt;
    String paymentIdString;

    ArrayList<ProductData> productDataArrayList = new ArrayList<>();
    ArrayList<SprayCropData> sprayCropDataArrayList = new ArrayList<>();
    ArrayList<TankFillingData> tankFillingDataArrayList = new ArrayList<>();

    private View mLayout;
    public static final String TAG = "Detail activity tag";
    private static final int REQUEST_MICRO_PHONE = 6;

    public static Uploadable sUploadable;

    TextView totalAcreOfTheFarmerTxt;
    TextView acreCoveredOfActualCropTxt;
    TextView acreCoveredOfActualCropTxt1;
    TextView districtText;

    TextView paymentDetailTxt;
    TextView amountReceivableTxt;
    TextView amountCollectedTxt;
    TextView balanceTxt;
    TextView toBeCollectedByTxt;
    TextView remarkTxt;
    Spinner districtAotoComplete;
    String districtID;


    String rate = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spray_detail_three);

        mLayout = findViewById(R.id.SprayDetailThreeActivity);

        districtAotoComplete = (Spinner) findViewById(R.id.district_actv);

        amtCollected = (EditText) findViewById(R.id.amtCollectedEdt);
        amountCollectedBy = (Spinner) findViewById(R.id.amountCollectedBy);
        remark = (EditText) findViewById(R.id.remark);
        amountCollectedTag = (TextView) findViewById(R.id.amountCollectedTxt);
        totalAcre = (TextView) findViewById(R.id.totalAcre);
        actualAcreCovered = (EditText) findViewById(R.id.actualAcreCovered);
        actualAcreCovered.setEnabled(false);
        amountReceivableEdt = (EditText) findViewById(R.id.amountReceivableEdt);
        amountReceivableEdt.setEnabled(false);
        balanceEdt = (EditText) findViewById(R.id.balanceEdt);

        totalAcreOfTheFarmerTxt = (TextView) findViewById(R.id.totalAcreOfTheFarmerTxt);
        acreCoveredOfActualCropTxt = (TextView) findViewById(R.id.acreCoveredOfActualCropTxt);
        acreCoveredOfActualCropTxt1 = (TextView) findViewById(R.id.acreCoveredOfActualCropTxt2);
        districtText = (TextView) findViewById(R.id.districtTxt);

        paymentDetailTxt = (TextView) findViewById(R.id.paymentDetailTxt);
        amountReceivableTxt = (TextView) findViewById(R.id.amountReceivableTxt);
        amountCollectedTxt = (TextView) findViewById(R.id.amountCollectedTxt);
        balanceTxt = (TextView) findViewById(R.id.balanceTxt);
        toBeCollectedByTxt = (TextView) findViewById(R.id.toBeCollectedByTxt);
        remarkTxt = (TextView) findViewById(R.id.remarkTxt);

        setSubtitleLanguage();

        paymentIdString = PaymentObject.createPaymentId();

        getMicroPhone(mLayout);


        data = (SprayMonitorData) getIntent().getParcelableExtra(Constents.DATA);
        data = getIntent().getParcelableExtra(Constents.DATA);
        productDataArrayList = getIntent().getParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA);
        if (productDataArrayList == null) {
            productDataArrayList = new ArrayList<>();
        }
        sprayCropDataArrayList = getIntent().getParcelableArrayListExtra(Constents.SPRAY_CROP_DATA);
        if (sprayCropDataArrayList == null) {
            sprayCropDataArrayList = new ArrayList<>();
        }
        tankFillingDataArrayList = getIntent().getParcelableArrayListExtra(Constents.SPRAY_TANK_FILLINF_DATA);
        if (tankFillingDataArrayList == null) {
            tankFillingDataArrayList = new ArrayList<>();
        }

        db = new DBAdapter(getApplicationContext());
        db.open();

        findViewById(R.id.privious).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {

                    final SharedPreferences sp = getSharedPreferences("district_data", MODE_PRIVATE);
                    SharedPreferences.Editor spEditor = sp.edit();
                    spEditor.putString("district_name", districtName);
                    spEditor.putString("rate", rate);
                    spEditor.commit();

                    Intent intent = new Intent(SprayDetailThreeActivity.this, SprayDetailTwoActivity.class);
                    intent.putExtra(Constents.DATA, data);
                    intent.putParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA, productDataArrayList);
                    intent.putParcelableArrayListExtra(Constents.SPRAY_CROP_DATA, sprayCropDataArrayList);
                    intent.putParcelableArrayListExtra(Constents.SPRAY_TANK_FILLINF_DATA, tankFillingDataArrayList);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                    finish();


                }
            }
        });

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValid()) {
                    if (isSubmitValid()) {
                        if (data.getMachineId() != null && data.getMachineId().trim().length() > 0) {

                            data.setSendingStatus(DBAdapter.SUBMIT);

                            final SharedPreferences sp = getSharedPreferences("district_data", MODE_PRIVATE);
                            sp.edit().clear().apply();

                            if (data.save(db)) {

                                ContentValues values = new ContentValues();
                                values.put(DBAdapter.SPRAY_MONITORING_ID, data.getSprayMonitorId());
                                values.put(DBAdapter.FARMER_ID, data.getFarmerId());
                                values.put(DBAdapter.AMOUNT_COLLECTED, data.getAmountCollected());
                                values.put(DBAdapter.BALANCE_AMOUNT, data.getBalanceAmount());
                                Date date = new Date();
                                String dateString = Constents.sdf.format(date);
                                values.put(DBAdapter.CREATED_DATE_TIME, dateString);
                                values.put(DBAdapter.SENDING_STATUS, DBAdapter.SUBMIT);
                                values.put(DBAdapter.PAYMENT_ID, paymentIdString);
                                db.db.insert(DBAdapter.TABLE_PAYMENT, null, values);

                        /*Change corresponding video and images status in to submit*/
                                ArrayList<ImageData> images = data.getImages(db);
                                for (ImageData imageData : images) {
                                    imageData.setSendingStatus(DBAdapter.SUBMIT);
                                    imageData.save(db);
                                }
                                ArrayList<VideoData> videos = data.getVideos(db);
                                for (VideoData videoData : videos) {
                                    videoData.setSendingStatus(DBAdapter.SUBMIT);
                                    videoData.save(db);
                                }

                                ArrayList<PaymentObject> paymentObjects = data.getPayments(db);
                                for (PaymentObject paymentObject : paymentObjects) {
                                    paymentObject.setFarmerId(data.getFarmerId());
                                    paymentObject.setSendingStatus(DBAdapter.SUBMIT);
                                    paymentObject.save(db);
                                }
                                /*ArrayList<SprayMonitorData> sprayMonitorDatas = new ArrayList<SprayMonitorData>();
                                sprayMonitorDatas.add(data);

                                sUploadable = new Uploadable() {
                                    @Override
                                    public void onSprayFormUploadingSuccess(DBAdapter db, SprayMonitorData cceFormData) {
                                        Toast.makeText(SprayDetailThreeActivity.this,"Form is uploaded",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onImageUploadingSuccess(DBAdapter db, ImageData imageData) {
                                        Toast.makeText(SprayDetailThreeActivity.this,"Image is uploaded",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onVideoUploadingSuccess(DBAdapter db, VideoData videoData) {
                                        Toast.makeText(SprayDetailThreeActivity.this,"Video is uploaded",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onPaymentUploadingSuccess(DBAdapter db, PaymentObject weighingData) {
                                        Toast.makeText(SprayDetailThreeActivity.this,"Payment is uploaded",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFarmerUploadingSuccess(DBAdapter db, FarmerData audioData) {

                                    }

                                    @Override
                                    public void onFailed() {
                                        Toast.makeText(SprayDetailThreeActivity.this,"Could not upload",Toast.LENGTH_SHORT).show();
                                    }
                                };

                                new UploadSprayMonitoringData(SprayDetailThreeActivity.this,
                                        db,
                                        sprayMonitorDatas,
                                        images,
                                        videos,
                                        paymentObjects);*/

                                AuthenticateService.sendSprayMonitoringData(data, images, paymentObjects, videos, db, SprayDetailThreeActivity.this);

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SprayDetailThreeActivity.this);
                                builder.setTitle("Save").
                                        setMessage("Could not save the form").
                                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        }).show();
                            }
                        } else {
                            Toast.makeText(SprayDetailThreeActivity.this, "Machine Id not selected", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

        String cropType = data.getCrop_type();


        final Cursor allDistrict = db.getDistrictByCrop(cropType);
        final int allDistrictCount = allDistrict.getCount();
        final ArrayList<String> districtStringArray = new ArrayList<String>();

      //  districtStringArray.add("Select district");

        //  districtStringArray.add("Please Select District");

        System.out.println(" district count " + allDistrict.getCount());

        if (allDistrictCount > 0) {
            allDistrict.moveToFirst();
            for (int i = 1; i <= allDistrictCount; i++) {
                districtStringArray.add(allDistrict.getString(allDistrict.getColumnIndex(DBAdapter.DISTRICT)));
                allDistrict.moveToNext();
            }
        }


        ArrayAdapter<String> dist_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, districtStringArray);

        dist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtAotoComplete.setAdapter(dist_adapter);

        final SharedPreferences sp = getSharedPreferences("district_data", MODE_PRIVATE);

        String dist_name = sp.getString("district_name", null);

        if (dist_name != null) {
            int spinnerPosition = dist_adapter.getPosition(dist_name);
            districtAotoComplete.setSelection(spinnerPosition);

        }

        if (districtStringArray.size()>1){
            districtAotoComplete.setFocusable(true);
            districtAotoComplete.setFocusableInTouchMode(true);

        }else {
            districtAotoComplete.setFocusable(false);
            districtAotoComplete.setFocusableInTouchMode(false);
        }


        districtAotoComplete.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when a new item was selected (in the Spinner)
             */
            public void onItemSelected(AdapterView<?> parent, View view, int poss, long id) {
                String districtId = "";
                if (poss >= 0) {

                    int pos = poss;


                    allDistrict.moveToPosition(pos);
                    districtId = allDistrict.getString(allDistrict.getColumnIndex(DBAdapter.DISTRICT_CODE));
                    districtName = allDistrict.getString(allDistrict.getColumnIndex(DBAdapter.DISTRICT));
                    Log.v("DistrictID", districtId + "//" + districtName);

                    districtID = districtId;
                    if (districtId != null && districtId.length() > 0) {

                        //  final SharedPreferences sharedPreferences = getSharedPreferences("crop_type", MODE_PRIVATE);
                        String cropType = data.getCrop_type();
                        rate = db.getRateByDistCropType(districtID, cropType);

                        data.setDistrict(districtId);


                        if (rate.length() < 1) {
                            rate = "40";
                        }

                        System.out.println("rate:" + rate + "lenght" + rate.length());
                        System.out.println("Crop type:" + cropType + "");


                        if (data != null) {
                            setDefaultData(data);
                        }
                    }

                }

            }

            public void onNothingSelected(AdapterView parent) {
                // Do nothing.
            }
        });



       /* ArrayAdapter<String> districtArrayAdapter = new ArrayAdapter<String>(this, R.layout.auto_complete_textview_item, districtStringArray); //selected item will look like a spinner set from XML

        districtAotoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String districtId = "";
                int index = districtStringArray.indexOf(districtAotoComplete.getText().toString());
                System.out.println("Inside district selection " + index);

                if (index >= 0) {
                    allDistrict.moveToPosition(index);
                    districtId = allDistrict.getString(allDistrict.getColumnIndex(DBAdapter.DISTRICT_CODE));
                    String districtName = allDistrict.getString(allDistrict.getColumnIndex(DBAdapter.DISTRICT));
                    Log.v("machinID", districtId + "//" + districtName);

                    districtID = districtId;
                    if (districtId!=null && districtId.length()>0) {

                        final SharedPreferences sharedPreferences = getSharedPreferences("crop_type",MODE_PRIVATE);
                        String cropType = sharedPreferences.getString("crop_type",null);
                        rate = db.getRateByDistCropType(districtID, cropType);
                        System.out.println("rate:" + rate);

                        final SharedPreferences sp = getSharedPreferences("district_data",MODE_PRIVATE);
                        SharedPreferences.Editor spEditor = sp.edit();
                        spEditor.putString("district_name",districtName);
                        spEditor.putString("rate",rate);
                        spEditor.commit();

                        if (data != null) {
                            setDefaultData(data);
                        }
                    }

                }


            }


        });

        districtAotoComplete.setThreshold(1);
        districtAotoComplete.setAdapter(districtArrayAdapter);
*/

        amtCollected.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    try {


                        if (!s.toString().equals(".")) {
                            double amtCollectedDouble = Double.parseDouble(s.toString());

                            double amtReceivableDouble = 0.0;
                            if (data.getAmountReceivable() != null && (!data.getAmountReceivable().isEmpty())) {
                                amtReceivableDouble = Double.parseDouble(data.getAmountReceivable());
                            }
                       /* double collectedAmnt = 0.0;

                        if (data.getBalanceAmount() != null && (!data.getBalanceAmount().isEmpty())) {
                            collectedAmnt = Double.parseDouble(data.getBalanceAmount());
                        }
*/
                            if (amtCollectedDouble <= (amtReceivableDouble)) {

                                double balanceAmountDouble = amtReceivableDouble - amtCollectedDouble;
                                balanceEdt.setText(String.valueOf(balanceAmountDouble));
                                data.setBalanceAmount(String.valueOf(balanceAmountDouble));
                                if (balanceAmountDouble == 0.0) {
                                    data.setPaymentStatus(DBAdapter.ON_SPOT);
                                    amountCollectedBy.setSelection(0);
                                    amountCollectedBy.setEnabled(false);
                                } else {
                                    data.setPaymentStatus(DBAdapter.LATER);
                                    amountCollectedBy.setEnabled(true);
                                }

                            } else {

                                amtCollected.setText("");

                                Toast.makeText(SprayDetailThreeActivity.this, "Collected amount can not be greater than Receivable amount", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (ParseException e) {
                        Toast.makeText(SprayDetailThreeActivity.this, "Please Enter Valid Amount", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    double amtReceivableDouble = 0.0;
                    if (data.getAmountReceivable() != null && (!data.getAmountReceivable().isEmpty())) {
                        amtReceivableDouble = Double.parseDouble(data.getAmountReceivable());
                    }

                    double balanceAmountDouble = amtReceivableDouble;
                    balanceEdt.setText(String.valueOf(balanceAmountDouble));
                }
            }
        });

        final Cursor getFieldCollector = db.getFieldCollector();
        final int collectorCountCount = getFieldCollector.getCount();
        String[] collectorStringArray = new String[collectorCountCount + 1];
        collectorStringArray[0] = "Select One";
        if (collectorCountCount > 0) {
            getFieldCollector.moveToFirst();
            for (int i = 1; i <= collectorCountCount; i++) {
                collectorStringArray[i] = getFieldCollector.getString(getFieldCollector.getColumnIndex(DBAdapter.FIELD_COLLECTOR));
                getFieldCollector.moveToNext();
            }
        }

        ArrayAdapter<String> collectorArrayAdapter = new ArrayAdapter<String>(SprayDetailThreeActivity.this, android.R.layout.simple_spinner_item, collectorStringArray); //selected item will look like a spinner set from XML
        collectorArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        amountCollectedBy.setAdapter(collectorArrayAdapter);
        amountCollectedBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String collectedBy = "-1";
                String collectedByName = "";
                System.out.println("Inside colector selection " + position);
                if (position > 0) {
                    getFieldCollector.moveToPosition(position - 1);
                    collectedBy = getFieldCollector.getString(getFieldCollector.getColumnIndex(DBAdapter.FIELD_COLLECTOR_ID));
                    collectedByName = getFieldCollector.getString(getFieldCollector.getColumnIndex(DBAdapter.FIELD_COLLECTOR));
                }
                data.setCollectedBy(collectedByName);
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

    private void setSubtitleLanguage() {
        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(this);
        String languagePreference = myPreference.getString(getResources().getString(R.string.language_pref_key), "1");
        int languageConstant = Integer.parseInt(languagePreference);

        System.out.println("language Constant : " + languageConstant);
        switch (languageConstant) {
            case 1:
                setEnglishText();
                break;
            case 2:
                setHindiText();
                break;
            default:
                setEnglishText();
        }
    }

    private void setEnglishText() {
        Typeface tf = Typeface.DEFAULT;

        totalAcreOfTheFarmerTxt.setTypeface(tf);
        totalAcreOfTheFarmerTxt.setText(getResources().getString(R.string.total_acre_of_farmer_txt));

        acreCoveredOfActualCropTxt.setTypeface(tf);
        acreCoveredOfActualCropTxt.setText(getResources().getString(R.string.actual_acre_covered_of_crop_txt));

        paymentDetailTxt.setTypeface(tf);
        paymentDetailTxt.setText(getResources().getString(R.string.payment_detail_txt));

        amountReceivableTxt.setTypeface(tf);
        amountReceivableTxt.setText(getResources().getString(R.string.amount_receivable_txt));

        amountCollectedTxt.setTypeface(tf);
        amountCollectedTxt.setText(getResources().getString(R.string.amount_collected_txt));

        balanceTxt.setTypeface(tf);
        balanceTxt.setText(getResources().getString(R.string.balance_amount_txt));

        toBeCollectedByTxt.setTypeface(tf);
        toBeCollectedByTxt.setText(getResources().getString(R.string.to_be_collected_by_txt));

        remarkTxt.setTypeface(tf);
        remarkTxt.setText(getResources().getString(R.string.remark_txt));

    }

    private void setHindiText() {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/krdv011.ttf");
//        Typeface tf1 = Typeface.createFromAsset(getAssets(), "fonts/Kruti_Dev_010.ttf");
//
        totalAcreOfTheFarmerTxt.setTypeface(tf);
        totalAcreOfTheFarmerTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        totalAcreOfTheFarmerTxt.setText("fdlku dh dqy NséQy¼,dM+½");

        acreCoveredOfActualCropTxt.setTypeface(tf);
        acreCoveredOfActualCropTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        acreCoveredOfActualCropTxt.setText("p;fur lewg ds okLrfod ,dM+ ");

        acreCoveredOfActualCropTxt1.setTypeface(tf);
        acreCoveredOfActualCropTxt1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        acreCoveredOfActualCropTxt1.setText("कुल खपत मात्रा");

        districtText.setTypeface(tf);
        districtText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        districtText.setText("जिला");


        paymentDetailTxt.setTypeface(tf);
        paymentDetailTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        paymentDetailTxt.setText("Hkqxrku fooj.k");

        amountReceivableTxt.setTypeface(tf);
        amountReceivableTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        amountReceivableTxt.setText("dqy jkf'k ¼:½");

        amountCollectedTxt.setTypeface(tf);
        amountCollectedTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        amountCollectedTxt.setText("jkf'k ,d=¼:½");

        balanceTxt.setTypeface(tf);
        balanceTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        balanceTxt.setText("'ks\"k jkf'k");

        toBeCollectedByTxt.setTypeface(tf);
        toBeCollectedByTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        toBeCollectedByTxt.setText("jkf'k ,dédZrk");

        remarkTxt.setTypeface(tf);
        remarkTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        remarkTxt.setText("fVIi.kh");

    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isValid();
        if (data != null) {
            if (data.getFarmerContact() != null && data.getFarmerContact().trim().length() > 0) {
                data.save(db);
            }
        }

    }


    public void getMicroPhone(View view) {
        Log.i(TAG, "Show MicroPhone button pressed. Checking permission.");
        // BEGIN_INCLUDE(camera_permission)
        // Check if the MicroPhone permission is already available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // MicroPhone permission has not been granted.

            requestMicroPhonePermission();

        } else {

            // MicroPhone permissions is already available, show the camera preview.
            Log.i(TAG,
                    "Location permission has already been granted. Displaying camera preview.");
//            showCameraPreview();
        }
        // END_INCLUDE(camera_permission)

    }

    private void requestMicroPhonePermission() {
        Log.i(TAG, "Location permission has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(Location_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying fine location permission rationale to provide additional context.");
            Snackbar.make(mLayout, R.string.permission_Location_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(SprayDetailThreeActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_MICRO_PHONE);
                        }
                    })
                    .show();

        } else {

            // Location permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_MICRO_PHONE);
        }
        // END_INCLUDE(Location_permission_request)
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_MICRO_PHONE) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for GPS permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i(TAG, "Micro Phone permission has now been granted. Showing preview.");
                Snackbar.make(mLayout, R.string.permission_available_gps,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "Micro Phone permission was NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT).show();

            }
            // END_INCLUDE(permission_result)

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void setDefaultData(SprayMonitorData data) {

        final SharedPreferences sp = getSharedPreferences("district_data", MODE_PRIVATE);
        String rattt = sp.getString("rate", null);

        Log.v("aaaa","l"+"--"+rate);

        if (rattt != null && !rattt.equalsIgnoreCase("0")) {
            rate = rattt;
        }


        double actualAcreDouble = 0.0;

        for (TankFillingData tankFillingData : tankFillingDataArrayList) {
            double acreCoveredByTank = 0.0;
            try {
                acreCoveredByTank = Double.parseDouble(tankFillingData.getAcreCoveredByTank());

            } catch (Exception e) {
                e.printStackTrace();
            }
            actualAcreDouble = acreCoveredByTank + actualAcreDouble;
        }
        Log.v("aaaa",actualAcreDouble+"--"+rate);
        double amountReceivableDouble = actualAcreDouble * Double.parseDouble(rate);
        data.setActualAcreCovered(String.valueOf(actualAcreDouble));
        data.setAmountReceivable(String.valueOf(amountReceivableDouble));

        Log.v("ddjdjjd",amountReceivableDouble+"");

        if (data.getAmountReceivable() != null) {
            amountReceivableEdt.setText(data.getAmountReceivable());
        }

        if (data.getAmountCollected() != null) {
            amtCollected.setText(data.getAmountCollected());
        }

        if (data.getRemark() != null) {
            remark.setText(data.getRemark());
        }

        if (data.getCollectedBy() != null && data.getCollectedBy().trim().length() > 0) {
            Cursor getFieldCollector = db.getFieldCollector();
            int collectorCount = getFieldCollector.getCount();
            System.out.println("getCollectedBy : " + data.getCollectedBy() + " collected count : " + collectorCount);
            if (collectorCount > 0 && (data.getCollectedBy().trim().length() > 0)) {
                getFieldCollector.moveToFirst();
                for (int d = 1; d <= collectorCount; d++) {
                    if (data.getCollectedBy().trim().equals(getFieldCollector.getString(getFieldCollector.getColumnIndex(DBAdapter.FIELD_COLLECTOR)))) {
                        amountCollectedBy.setSelection(d, true);
                        break;
                    }
                    getFieldCollector.moveToNext();
                }
            }
        }

        if (data.getTotalAcrage() != null && (!data.getTotalAcrage().isEmpty())) {
            totalAcre.setText(data.getTotalAcrage());
        }

        String cropType = data.getCrop_type();
        if (cropType != null && cropType.equalsIgnoreCase("2")) {

            acreCoveredOfActualCropTxt1.setVisibility(View.VISIBLE);
            acreCoveredOfActualCropTxt.setVisibility(View.GONE);
        } else {
            acreCoveredOfActualCropTxt1.setVisibility(View.GONE);
            acreCoveredOfActualCropTxt.setVisibility(View.VISIBLE);
        }

        if (data.getActualAcreCovered() != null && (!data.getActualAcreCovered().isEmpty())) {
            actualAcreCovered.setText(data.getActualAcreCovered());
        }

        data.save(db);

    }


    ProgressDialog dialog;

    private void sendSubmittedData(final SprayMonitorData data, final ArrayList<ImageData> images, final ArrayList<PaymentObject> paymentObjects, final ArrayList<VideoData> videos, final DBAdapter db) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                dialog = ProgressDialog.show(SprayDetailThreeActivity.this, "Registering",
                        "Please wait...", true);
            }

            @Override
            protected String doInBackground(Void... voids) {
                AuthenticateService.sendSprayMonitoringData(data, images, paymentObjects, videos, db, getApplicationContext());
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                dialog.cancel();
//                Form details has been submitted.
//                It may take some time to send it to server.
                Toast.makeText(getApplicationContext(), "Detail has been submitted", Toast.LENGTH_LONG).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(SprayDetailThreeActivity.this);
                builder.setTitle("Submit").
                        setMessage("Detail has been submitted").
                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(SprayDetailThreeActivity.this, TabedActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }).show();

            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        if (isValid()) {
            if (isValid()) {
                Intent intent = new Intent(SprayDetailThreeActivity.this, SprayDetailTwoActivity.class);
                intent.putExtra(Constents.DATA, data);
                intent.putParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA, productDataArrayList);
                intent.putParcelableArrayListExtra(Constents.SPRAY_CROP_DATA, sprayCropDataArrayList);
                intent.putParcelableArrayListExtra(Constents.SPRAY_TANK_FILLINF_DATA, tankFillingDataArrayList);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                finish();

            }
        }
    }

    private boolean isSubmitValid() {
        boolean isValid = true;

        boolean collectedAmount = (data.getAmountCollected() != null && data.getAmountCollected().trim().length() > 0);
        if (!collectedAmount) {
            Toast.makeText(SprayDetailThreeActivity.this, "Please enter collected amount", Toast.LENGTH_LONG).show();
            return false;
        }

       /* boolean collectedBy = (data.getCollectedBy() != null && data.getCollectedBy().trim().length() > 0);
        if (!collectedBy) {
            Toast.makeText(SprayDetailThreeActivity.this, "Please select, who collected the amount", Toast.LENGTH_LONG).show();
            return false;
        }*/

        /*double actualAcraCovered = 0.0;
        double acreToBeCovered = 0.0;
        if(data.getActualAcreCovered() != null && data.getActualAcreCovered().trim().length()>0){
            try{
                actualAcraCovered = Double.parseDouble(data.getActualAcreCovered());
            }catch (NumberFormatException e){e.printStackTrace();}
        }

        if(data.getAcrageCovered() != null && data.getAcrageCovered().trim().length()>0){
            try{
                acreToBeCovered = Double.parseDouble(data.getAcrageCovered());
            }catch (NumberFormatException e){e.printStackTrace();}
        }

        if(actualAcraCovered>acreToBeCovered){
            Toast.makeText(SprayDetailThreeActivity.this, "Actual acre can not be greater than Acre to be covered", Toast.LENGTH_LONG).show();
            return false;
        }*/

        double balanceAmount = 0.0;
        if (data.getBalanceAmount() != null) {
            try {
                balanceAmount = Double.parseDouble(data.getBalanceAmount());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (balanceAmount > 0.0) {
            if (data.getCollectedBy() == null || data.getCollectedBy().trim().length() <= 0) {
                Toast.makeText(SprayDetailThreeActivity.this, "Please select To Be Collected By", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        /*if (productArray.size() == 0) {
            Toast.makeText(SprayDetailThreeActivity.this, "Please add at least one product", Toast.LENGTH_LONG).show();
            return false;
        }*/

        return isValid;
    }


    private boolean isValid() {
        boolean isValid = true;

      /*  String name = districtAotoComplete.getSelectedItem().toString();

        if (name != null && name.length() > 0) {


        } else {

            Toast.makeText(getApplicationContext(), "Please select district.", Toast.LENGTH_SHORT).show();
            return false;
        }*/

       /* if(acreCovered.getText().toString()!=null && acreCovered.getText().toString().trim().length()>0) {
            data.setAcrgaeCovered(acreCovered.getText().toString());
        }else{
            Toast.makeText(this,"Please Enter Acre Covered",Toast.LENGTH_SHORT).show();
            return false;
        }*/

        /*data.setAcrgaeCovered(acreCovered.getText().toString());
        data.setProductSprayedCount(sprayCount.getText().toString());
        data.setLastSprayedQty(qtySprayed.getText().toString());*/
        data.setAmountCollected(amtCollected.getText().toString());
        data.setRemark(remark.getText().toString());
        data.setActualAcreCovered(actualAcreCovered.getText().toString());

        return isValid;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;
            case -1:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_third, menu);
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
                    if (data.getFarmerName() != null && data.getFarmerName().trim().length() > 0) {
                        if (data.save(db)) {
                            Toast.makeText(SprayDetailThreeActivity.this, "Detail has been saved", Toast.LENGTH_LONG).show();

                            final SharedPreferences sp = getSharedPreferences("district_data", MODE_PRIVATE);
                            SharedPreferences.Editor spEditor = sp.edit();
                            spEditor.putString("district_name", districtName);
                            spEditor.putString("rate", rate);
                            spEditor.commit();

                        } else {
                            Toast.makeText(SprayDetailThreeActivity.this, "Could not save the form", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(SprayDetailThreeActivity.this, "Farmer Name Required", Toast.LENGTH_LONG).show();
                    }
                }
                return true;
            }
            case android.R.id.home: {
                if (isValid()) {
                    Intent intent = new Intent(SprayDetailThreeActivity.this, SprayDetailTwoActivity.class);
                    intent.putExtra(Constents.DATA, data);
                    intent.putParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA, productDataArrayList);
                    intent.putParcelableArrayListExtra(Constents.SPRAY_CROP_DATA, sprayCropDataArrayList);
                    intent.putParcelableArrayListExtra(Constents.SPRAY_TANK_FILLINF_DATA, tankFillingDataArrayList);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                    finish();
                }
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private static final String PRODUCT_DATA_ARRAY = "product_data_array";
    private static final String SPRAY_CROP_DATA_ARRAY = "spray_crop_data_array";
    private static final String TANK_DATA_ARRAY = "tank_data_array";
    private static final String SPRAY_DATA = "spray_data";

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i("MakeMachine", "onRestoreInstanceState()");
        productDataArrayList = savedInstanceState.getParcelableArrayList(PRODUCT_DATA_ARRAY);
        sprayCropDataArrayList = savedInstanceState.getParcelableArrayList(SPRAY_CROP_DATA_ARRAY);
        tankFillingDataArrayList = savedInstanceState.getParcelableArrayList(TANK_DATA_ARRAY);
        data = savedInstanceState.getParcelable(SPRAY_DATA);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(PRODUCT_DATA_ARRAY, productDataArrayList);
        outState.putParcelableArrayList(SPRAY_CROP_DATA_ARRAY, sprayCropDataArrayList);
        outState.putParcelableArrayList(TANK_DATA_ARRAY, tankFillingDataArrayList);
        outState.putParcelable(SPRAY_DATA, data);
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
                    || y < w.getTop() || y > w.getBottom())) {

                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }
        return ret;
    }

}



