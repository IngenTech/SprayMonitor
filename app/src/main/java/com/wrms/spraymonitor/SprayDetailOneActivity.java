package com.wrms.spraymonitor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wrms.spraymonitor.app.Constents;
import com.wrms.spraymonitor.app.ContentDataArrayAdapter;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.background.Lat_Lon_CellID;
import com.wrms.spraymonitor.background.LocationPollReceiver;
import com.wrms.spraymonitor.dataobject.ContentData;
import com.wrms.spraymonitor.dataobject.Credential;
import com.wrms.spraymonitor.dataobject.FarmerData;
import com.wrms.spraymonitor.dataobject.ImageData;
import com.wrms.spraymonitor.dataobject.ProductData;
import com.wrms.spraymonitor.dataobject.SprayCropData;
import com.wrms.spraymonitor.dataobject.SprayMonitorData;
import com.wrms.spraymonitor.dataobject.TankFillingData;
import com.wrms.spraymonitor.utils.AppManager;
import com.wrms.spraymonitor.utils.FolderManager;
import com.wrms.spraymonitor.utils.ResizeBitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SprayDetailOneActivity extends AppCompatActivity {

    AutoCompleteTextView farmerContactAct;
    TextView farmerDetailTxt;
    TextView farmerRegisteredStatusTxt;
    AutoCompleteTextView machineIdAct;
    ImageButton machineGalImgButton;
    ImageButton machineImgButton;
    ImageView machineImageView;
    LinearLayout addedCropLinLay;
    // AutoCompleteTextView cropSpinner;
    EditText cropAcreEdt;
    EditText totalAcreEdt;
    //  ImageButton addCropImgButton;
    LinearLayout farmerWithProductTag;
    ImageButton farmerWithProductGalButton;
    ImageButton farmerWithProductImgButton;
    ImageView farmerWithProductImageView;

    TextView farmerContactTxt;
    TextView machineNameTxt;

    LinearLayout fieldLayout;
    TextView cropNameTxt;
    TextView acreTxt;

    LinearLayout orchardLayout;
    TextView orchard_cropNameTxt;
    TextView orchard_volumeTxt;

    ArrayAdapter<String> cropArrayAdapter;
    Cursor getAllCrop;
    ArrayList<String> cropStringArray;
    String cropType = "1";
    RadioGroup cropTypeGroup;
    RadioButton fieldRadioButton;
    RadioButton orchardRadioButton;

    TextView farmerTotalAcrageTxt;
    TextView farmerWithProductTxt;

    LayoutInflater innerInflater;

    ArrayList<ProductData> productDataArrayList = new ArrayList<>();
    ArrayList<SprayCropData> sprayCropDataArrayList = new ArrayList<>();
    ArrayList<TankFillingData> tankFillingDataArrayList = new ArrayList<>();


    Cursor allMachine;
    int allMachineCount;
    ArrayList<String> machineStringArray;

    DBAdapter db;

    SprayMonitorData data;
    FarmerData farmerData;
    SprayCropData sprayCropData;

    ArrayAdapter<String> mContentDataArrayAdapter;

    Button saveBTN, nextBTN;
    LinearLayout newFarLay, oldFarLay;
    Button addFarmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spray_detail_one);

        db = new DBAdapter(SprayDetailOneActivity.this);
        db.open();

        newFarLay = (LinearLayout) findViewById(R.id.new_farmer_lay);
        oldFarLay = (LinearLayout) findViewById(R.id.old_farmer_lay);
        addFarmer = (Button) findViewById(R.id.newfarmer_BTN);

       /* newFarLay.setVisibility(View.VISIBLE);
        oldFarLay.setVisibility(View.GONE);*/

        Log.v("llllloooccaaatt", Lat_Lon_CellID.lat + "");

        saveBTN = (Button) findViewById(R.id.save_btn);
        nextBTN = (Button) findViewById(R.id.next_btn);

        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setDataIntoObject()) {
                    data.setSendingStatus(DBAdapter.SAVE);
                    if (data.getFarmerContact() != null && data.getFarmerContact().trim().length() > 0) {
                        if (data.save(db)) {
                            Toast.makeText(SprayDetailOneActivity.this, "Detail has been saved", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SprayDetailOneActivity.this, "Could not save the form", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(SprayDetailOneActivity.this, "Farmer Contact Required", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        nextBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (setDataIntoObject()) {
                    if (actionNextValid(data)) {
                        Intent intent = new Intent(SprayDetailOneActivity.this, SprayDetailTwoActivity.class);
                        intent.putExtra(Constents.DATA, data);
                        intent.putParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA, productDataArrayList);
                        intent.putParcelableArrayListExtra(Constents.SPRAY_CROP_DATA, sprayCropDataArrayList);
                        intent.putParcelableArrayListExtra(Constents.SPRAY_TANK_FILLINF_DATA, tankFillingDataArrayList);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                        finish();
                    }
                }
            }
        });

        cropTypeGroup = (RadioGroup) findViewById(R.id.cropRadioGroup);
        fieldRadioButton = (RadioButton) findViewById(R.id.field_cropRadioButton);
        orchardRadioButton = (RadioButton) findViewById(R.id.orchard_cropRadioButton);

        farmerContactAct = (AutoCompleteTextView) findViewById(R.id.farmerContactAct);
        machineIdAct = (AutoCompleteTextView) findViewById(R.id.machineIdAct);
        farmerDetailTxt = (TextView) findViewById(R.id.farmerDetailTxt);
        farmerRegisteredStatusTxt = (TextView) findViewById(R.id.farmerRegisteredStatusTxt);
        farmerWithProductTag = (LinearLayout) findViewById(R.id.farmerWithProductTxt);
        machineGalImgButton = (ImageButton) findViewById(R.id.machineGalImgButton);
        machineImgButton = (ImageButton) findViewById(R.id.machineImgButton);
        //   addCropImgButton = (ImageButton) findViewById(R.id.addCropImgButton);
        farmerWithProductGalButton = (ImageButton) findViewById(R.id.farmerWithProductGalButton);
        farmerWithProductImgButton = (ImageButton) findViewById(R.id.farmerWithProductImgButton);
        machineImageView = (ImageView) findViewById(R.id.machineImageView);
        farmerWithProductImageView = (ImageView) findViewById(R.id.farmerWithProductImageView);
        addedCropLinLay = (LinearLayout) findViewById(R.id.addedCropLinLay);
        //  cropSpinner = (AutoCompleteTextView) findViewById(R.id.cropSpinner);
        cropAcreEdt = (EditText) findViewById(R.id.cropAcreEdt);
        totalAcreEdt = (EditText) findViewById(R.id.totalAcreEdt);
        totalAcreEdt.setEnabled(false);

        farmerContactTxt = (TextView) findViewById(R.id.farmerContactTxt);
        machineNameTxt = (TextView) findViewById(R.id.machineNameTxt);

        fieldLayout = (LinearLayout) findViewById(R.id.field_layout);
        cropNameTxt = (TextView) findViewById(R.id.cropNameTxt);
        acreTxt = (TextView) findViewById(R.id.acreTxt);

        orchardLayout = (LinearLayout) findViewById(R.id.orchard_layout);
        orchard_cropNameTxt = (TextView) findViewById(R.id.orchard_cropNameTxt);
        orchard_volumeTxt = (TextView) findViewById(R.id.orchard_volumeTxt);

        farmerTotalAcrageTxt = (TextView) findViewById(R.id.farmerTotalAcrageTxt);
        farmerWithProductTxt = (TextView) findViewById(R.id.farmartext);

        innerInflater = LayoutInflater.from(this);

        setSubtitleLanguage();

        addFarmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (farmerContactAct.getText().toString().trim().length() > 9) {
                    Intent intent = new Intent(getApplicationContext(), RegisterFarmerActivity.class);
                    startActivity(intent);
                } else {

                    Toast.makeText(getApplicationContext(), "Please Enter Valid Mobile Number.", Toast.LENGTH_SHORT).show();
                }
            }
        });


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
        farmerData = getIntent().getParcelableExtra(Constents.FARMER_DATA);

        data = sprayDataInit(data);
        if (sprayCropData == null) {
            sprayCropData = new SprayCropData();
        }


        setFarmerDetail(farmerData);

        machineImgButton.setOnClickListener(machineImagListner);
        machineGalImgButton.setOnClickListener(machineImageGalOnClickListner);

        farmerWithProductImgButton.setOnClickListener(farmerProductImagListner);
        farmerWithProductGalButton.setOnClickListener(farmerProductGalOnClickListner);

       /* getAllCrop = db.getAllCropByType(cropType);
        cropStringArray = new ArrayList<>();
        System.out.println("cropLenght:" + getAllCrop.getCount());

        if (getAllCrop.moveToFirst()) {
            do {
                cropStringArray.add(getAllCrop.getString(getAllCrop.getColumnIndex(DBAdapter.CROP)));
            } while (getAllCrop.moveToNext());
        }

        cropArrayAdapter = new ArrayAdapter<String>(SprayDetailOneActivity.this, R.layout.auto_complete_textview_item, cropStringArray); //selected item will look like a spinner set from XML
        cropSpinner.setThreshold(1);
        cropSpinner.setAdapter(cropArrayAdapter);
*/

        allMachine = db.getAllMachineByCrop(cropType);
        allMachineCount = allMachine.getCount();
        machineStringArray = new ArrayList<>();
        machineStringArray.clear();

        if (allMachineCount > 0) {
            allMachine.moveToFirst();
            for (int i = 1; i <= allMachineCount; i++) {
                machineStringArray.add(allMachine.getString(allMachine.getColumnIndex(DBAdapter.MACHINE)));
                allMachine.moveToNext();
            }
        }

        mContentDataArrayAdapter = new ArrayAdapter<String>(SprayDetailOneActivity.this, R.layout.auto_complete_textview_item, machineStringArray); //selected item will look like a spinner set from XML
        machineIdAct.setThreshold(1);
        machineIdAct.setAdapter(mContentDataArrayAdapter);

        final Cursor farmerListCursor = db.getFarmerList();
        final ArrayList<String> farmerContactArray = new ArrayList<>();
        if (farmerListCursor.getCount() > 0) {
            farmerListCursor.moveToFirst();
            int i = 0;
            do {
                farmerContactArray.add(farmerListCursor.getString(farmerListCursor.getColumnIndex(DBAdapter.FARMER_CONTACT)));
                i++;
            } while (farmerListCursor.moveToNext());
        }
        ArrayAdapter<String> adp = new ArrayAdapter<String>(SprayDetailOneActivity.this, android.R.layout.simple_dropdown_item_1line, farmerContactArray);
        farmerContactAct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                newFarLay.setVisibility(View.GONE);
                oldFarLay.setVisibility(View.VISIBLE);

                System.out.println("farmer contact Array : " + farmerContactArray);
                int index = farmerContactArray.indexOf(farmerContactAct.getText().toString().trim());
                System.out.println("Farmer cursor index : " + index);
                farmerListCursor.moveToPosition(index);
                System.out.println("farmerListCursor.getColumnIndex(DBAdapter.FARMER_ID) : " + (farmerListCursor.getColumnIndex(DBAdapter.FARMER_ID)));
                String farmerId = farmerListCursor.getString(farmerListCursor.getColumnIndex(DBAdapter.FARMER_ID));
                FarmerData farmerData = new FarmerData(farmerId, db);
                setFarmerDetail(farmerData);

                farmerDetailTxt.setText("");

                if (data.getFarmerName() != null && data.getFarmerName().length() > 0) {
                    String farmerDetailString = data.getFarmerName();
                    farmerDetailTxt.setText(farmerDetailString);
                    farmerDetailTxt.setVisibility(View.VISIBLE);

                }

            }

        });

        farmerContactAct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                newFarLay.setVisibility(View.VISIBLE);
                oldFarLay.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        farmerContactAct.setThreshold(1);
        farmerContactAct.setAdapter(adp);

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setDataIntoObject()) {
                    if (actionNextValid(data)) {
                        Intent intent = new Intent(SprayDetailOneActivity.this, DetailActivitySecond.class);
                        intent.putExtra(Constents.DATA, data);
                        intent.putParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA, productDataArrayList);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                        finish();
                    }
                }
            }
        });


        //for crop type in rate list use shareprefrence

        final SharedPreferences sharedPreferences = getSharedPreferences("crop_type", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("crop_type", cropType);
        editor.commit();

        cropTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                if (fieldRadioButton.isChecked()) {

                    cropType = "1";

                    data.setCrop_type(cropType);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("crop_type", cropType);
                    editor.commit();

                   /* fieldLayout.setVisibility(View.VISIBLE);
                    orchardLayout.setVisibility(View.GONE);

                    getAllCrop = db.getAllCropByType(cropType);
                    cropStringArray = new ArrayList<>();
                    cropStringArray.clear();
                    System.out.println("cropLenght:" + getAllCrop.getCount());

                    if (getAllCrop.moveToFirst()) {
                        do {
                            cropStringArray.add(getAllCrop.getString(getAllCrop.getColumnIndex(DBAdapter.CROP)));
                        } while (getAllCrop.moveToNext());
                    }

                    cropArrayAdapter = new ArrayAdapter<String>(SprayDetailOneActivity.this, R.layout.auto_complete_textview_item, cropStringArray); //selected item will look like a spinner set from XML
                    cropSpinner.setThreshold(1);
                    cropSpinner.setAdapter(cropArrayAdapter);*/


                    allMachine = db.getAllMachineByCrop(cropType);
                    allMachineCount = allMachine.getCount();
                    machineStringArray = new ArrayList<>();
                    machineStringArray.clear();

                    if (allMachineCount > 0) {
                        allMachine.moveToFirst();
                        for (int i = 1; i <= allMachineCount; i++) {
                            machineStringArray.add(allMachine.getString(allMachine.getColumnIndex(DBAdapter.MACHINE)));
                            allMachine.moveToNext();
                        }
                    }

                    mContentDataArrayAdapter = new ArrayAdapter<String>(SprayDetailOneActivity.this, R.layout.auto_complete_textview_item, machineStringArray); //selected item will look like a spinner set from XML

                    machineIdAct.setThreshold(1);
                    machineIdAct.setAdapter(mContentDataArrayAdapter);

                } else {

                    cropType = "2";

                    data.setCrop_type(cropType);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("crop_type", cropType);
                    editor.commit();

                   /* fieldLayout.setVisibility(View.GONE);
                    orchardLayout.setVisibility(View.VISIBLE);

                    getAllCrop = db.getAllCropByType(cropType);
                    cropStringArray = new ArrayList<>();
                    cropStringArray.clear();

                    System.out.println("cropLenght:" + getAllCrop.getCount());

                    if (getAllCrop.moveToFirst()) {
                        do {
                            cropStringArray.add(getAllCrop.getString(getAllCrop.getColumnIndex(DBAdapter.CROP)));
                        } while (getAllCrop.moveToNext());
                    }*/


                }

              /*  cropArrayAdapter = new ArrayAdapter<String>(SprayDetailOneActivity.this, R.layout.auto_complete_textview_item, cropStringArray); //selected item will look like a spinner set from XML

                cropSpinner.setThreshold(1);
                cropSpinner.setAdapter(cropArrayAdapter);
*/

                allMachine = db.getAllMachineByCrop(cropType);
                allMachineCount = allMachine.getCount();
                machineStringArray = new ArrayList<>();
                machineStringArray.clear();

                if (allMachineCount > 0) {
                    allMachine.moveToFirst();
                    for (int i = 1; i <= allMachineCount; i++) {
                        machineStringArray.add(allMachine.getString(allMachine.getColumnIndex(DBAdapter.MACHINE)));
                        allMachine.moveToNext();
                    }
                }

                mContentDataArrayAdapter = new ArrayAdapter<String>(SprayDetailOneActivity.this, R.layout.auto_complete_textview_item, machineStringArray); //selected item will look like a spinner set from XML

                machineIdAct.setThreshold(1);
                machineIdAct.setAdapter(mContentDataArrayAdapter);

            }
        });


        machineIdAct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (machineStringArray.size() > 0) {
                    System.out.println("machineArray : " + machineStringArray);
                    int index = machineStringArray.indexOf(machineIdAct.getText().toString().trim());
                    System.out.println("Machine index : " + index);

                    allMachine.moveToPosition(index);
                    String machineId = allMachine.getString(allMachine.getColumnIndex(DBAdapter.MACHINE_ID));
                    String machineName = allMachine.getString(allMachine.getColumnIndex(DBAdapter.MACHINE));
                    data.setMachineId(machineId);
                }
            }
        });


       /* cropSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (cropStringArray.size() > 0) {
                    try {

                        int index = cropStringArray.indexOf(cropSpinner.getText().toString());
                        getAllCrop.moveToPosition(index);
                        String cropId = getAllCrop.getString(getAllCrop.getColumnIndex(DBAdapter.CROP_ID));
                        String cropName = getAllCrop.getString(getAllCrop.getColumnIndex(DBAdapter.CROP));
                        sprayCropData.setCropId(cropId);
                        sprayCropData.setCropName(cropName);

                        Log.d("cropID", cropId + "index" + index);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

            }
        });*/


       /* addCropImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sprayCropData.setCropAcre(cropAcreEdt.getText().toString());
                if (isValidCrop()) {
                    if (sprayCropDataArrayList.size() < 5) {
                        sprayCropData.setSprayId(data.getSprayMonitorId());
                        sprayCropData.setSprayCropId(String.valueOf(System.currentTimeMillis()));

                        double totalAcre = 0.0;
                        try {
                            totalAcre = Double.parseDouble(totalAcreEdt.getText().toString());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                        double addedCropAcre = 0.0;
                        try {
                            addedCropAcre = Double.parseDouble(cropAcreEdt.getText().toString());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                        totalAcreEdt.setText(String.valueOf(totalAcre + addedCropAcre));

                        if (data.isSprayExists(db)) {
                            data.setTotalAcrage(totalAcreEdt.getText().toString());
                            data.save(db);
                        }

                        sprayCropData.save(db);
                        sprayCropDataArrayList.add(sprayCropData);
                        setCropInConfirmLayout(sprayCropData);
                        sprayCropData = new SprayCropData();

                       *//* if (sprayCropDataArrayList.size()==0){

                            cropName1 = cropSpinner.getText().toString().trim();
                            acre1 = cropAcreEdt.getText().toString().trim();
                        }

                        if (sprayCropDataArrayList.size()==1){

                            cropName2 = cropSpinner.getText().toString().trim();
                            acre2 = cropAcreEdt.getText().toString().trim();
                        }

                        if (sprayCropDataArrayList.size()==2){

                            cropName3 = cropSpinner.getText().toString().trim();
                            acre3 = cropAcreEdt.getText().toString().trim();
                        }

                        if (sprayCropDataArrayList.size()==3){

                            cropName4 = cropSpinner.getText().toString().trim();
                            acre4 = cropAcreEdt.getText().toString().trim();
                        }

                        if (sprayCropDataArrayList.size()==4){

                            cropName5 = cropSpinner.getText().toString().trim();
                            acre5 = cropAcreEdt.getText().toString().trim();
                        }*//*

                    } else {
                        Toast.makeText(SprayDetailOneActivity.this, "5 Crops has been added", Toast.LENGTH_SHORT).show();
                    }

                    cropSpinner.setText("");
                }
            }
        });*/

        if (data != null) {
            setDefaultData(data);
        } else {

            data.setCrop_type(cropType);
        }

        String cropT = data.getCrop_type();

        if (cropT != null && cropT.equalsIgnoreCase("1")) {
            data.setCrop_type("1");
        } else if (cropT != null && cropT.equalsIgnoreCase("2")) {
            data.setCrop_type("2");
        } else {
            data.setCrop_type("1");
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

        farmerContactTxt.setTypeface(tf);
        farmerContactTxt.setText(getResources().getString(R.string.farmer_contact_txt));

        machineNameTxt.setTypeface(tf);
        machineNameTxt.setText(getResources().getString(R.string.machine_name_txt));

        cropNameTxt.setTypeface(tf);
        cropNameTxt.setText(getResources().getString(R.string.crop_txt));

        acreTxt.setTypeface(tf);
        acreTxt.setText(getResources().getString(R.string.acre_txt));

        orchard_cropNameTxt.setTypeface(tf);
        orchard_cropNameTxt.setText(getResources().getString(R.string.crop_txt));

        orchard_volumeTxt.setTypeface(tf);
        orchard_volumeTxt.setText(getResources().getString(R.string.acre_txt));

        farmerTotalAcrageTxt.setTypeface(tf);
        farmerTotalAcrageTxt.setText(getResources().getString(R.string.total_acre_of_farmer_txt));

        farmerWithProductTxt.setTypeface(tf);
        farmerWithProductTxt.setText(getResources().getString(R.string.farmer_with_product_txt));

    }

    private void setHindiText() {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/krdv011.ttf");

        farmerContactTxt.setTypeface(tf);
        farmerContactTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        farmerContactTxt.setText("fdlku dk e¨cby uÛ");

        machineNameTxt.setTypeface(tf);
        machineNameTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        machineNameTxt.setText("e'khu dk uke");

        cropNameTxt.setTypeface(tf);
        cropNameTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        cropNameTxt.setText("Qly");

        acreTxt.setTypeface(tf);
        acreTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        acreTxt.setText(",dM+");

        orchard_cropNameTxt.setTypeface(tf);
        orchard_cropNameTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        orchard_cropNameTxt.setText("Qly");

        orchard_volumeTxt.setTypeface(tf);
        orchard_volumeTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        orchard_volumeTxt.setText(",dM+");

        farmerTotalAcrageTxt.setTypeface(tf);
        farmerTotalAcrageTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        farmerTotalAcrageTxt.setText("fdlku dh dqy ,dM+");

        farmerWithProductTxt.setTypeface(tf);
        farmerWithProductTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        farmerWithProductTxt.setText("fdlku ds lkFk mRikn");
    }

    private void addDynamicLable(TextView cropTitle, TextView cropLable, TextView coveredAcre) {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/krdv011.ttf");

        cropTitle.setTypeface(tf);
        cropTitle.setTextColor(getResources().getColor(R.color.color_primary_dark));
        cropTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        cropTitle.setText("Qly");

        cropLable.setTypeface(tf);
        cropLable.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        cropLable.setText("Qly dk uke");

        coveredAcre.setTypeface(tf);
        coveredAcre.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        coveredAcre.setText(",dM+");
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        /*SprayDetailOneActivity.this.finish();
        Intent intent = new Intent(SprayDetailOneActivity.this, TabedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);*/

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_one, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_save: {
                if (setDataIntoObject()) {
                    data.setSendingStatus(DBAdapter.SAVE);
                    if (data.getFarmerContact() != null && data.getFarmerContact().trim().length() > 0) {
                        if (data.save(db)) {
                            Toast.makeText(SprayDetailOneActivity.this, "Detail has been saved", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SprayDetailOneActivity.this, "Could not save the form", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(SprayDetailOneActivity.this, "Farmer Contact Required", Toast.LENGTH_LONG).show();
                    }
                }
                return true;

            }
            case R.id.action_spraylog: {


                Intent intent = new Intent(SprayDetailOneActivity.this, TabedActivity.class);
                startActivity(intent);
                // finish();


                return true;
            }
            case android.R.id.home: {
                /*SprayDetailOneActivity.this.finish();
                Intent intent = new Intent(SprayDetailOneActivity.this, TabedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/

                finish();

                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }

    }


    boolean isValidCrop() {
        boolean isValid = true;

        if (!(sprayCropData.getCropId() != null && sprayCropData.getCropId().trim().length() > 0)) {
            Toast.makeText(SprayDetailOneActivity.this, "Select crop", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(sprayCropData.getCropAcre() != null && sprayCropData.getCropAcre().trim().length() > 0)) {
            Toast.makeText(SprayDetailOneActivity.this, "Enter crop acre", Toast.LENGTH_SHORT).show();
            return false;
        }

        return isValid;
    }

    public boolean actionNextValid(SprayMonitorData data) {
        boolean isValid = true;

        boolean farmerContact = (data.getFarmerContact() != null && data.getFarmerContact().trim().length() > 0);
        if (!farmerContact) {
            Toast.makeText(SprayDetailOneActivity.this, "Farmer Contact number Required", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        Cursor farmerC = db.getFarmerByContact(data.getFarmerContact());
        if (!(farmerC.getCount() > 0)) {
            Toast.makeText(SprayDetailOneActivity.this, "Farmer Contact does not exist.", Toast.LENGTH_LONG).show();
            farmerC.close();
            isValid = false;
        }
        farmerC.close();

        Cursor machineCursor = db.getMachineByName(machineIdAct.getText().toString().trim());
        if (machineCursor.moveToFirst()) {
            String machineId = machineCursor.getString(machineCursor.getColumnIndex(DBAdapter.MACHINE_ID));
            data.setMachineId(machineId);
        } else {

            Toast.makeText(SprayDetailOneActivity.this, "Machine name not exist.", Toast.LENGTH_LONG).show();

            machineCursor.close();
            isValid = false;
        }
        machineCursor.close();

      /*  if (sprayCropDataArrayList == null || sprayCropDataArrayList.size() == 0) {
            Toast.makeText(SprayDetailOneActivity.this, "Please add crop acre", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        boolean totalAcre = (data.getTotalAcrage() != null && data.getTotalAcrage().trim().length() > 0);
        if (!totalAcre) {
            Toast.makeText(SprayDetailOneActivity.this, "Please enter total acre", Toast.LENGTH_LONG).show();
            isValid = false;
        }
*/
        Cursor imageCursor = db.getImageByFormIdAndName(data.getSprayMonitorId(), Constents.FARMER_WITH_PRODUCT_DIR);
        if (imageCursor.getCount() <= 0) {
            Toast.makeText(SprayDetailOneActivity.this, "Please select farmer with product photo.", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        imageCursor.close();

        return isValid;
    }


    public boolean actionCropDelValid(SprayMonitorData data) {
        boolean isValid = true;

        boolean farmerContact = (data.getFarmerContact() != null && data.getFarmerContact().trim().length() > 0);
        if (!farmerContact) {
            //    Toast.makeText(SprayDetailOneActivity.this, "Farmer Contact number Required", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        Cursor farmerC = db.getFarmerByContact(data.getFarmerContact());
        if (!(farmerC.getCount() > 0)) {
            //    Toast.makeText(SprayDetailOneActivity.this, "Farmer Contact does not exist.", Toast.LENGTH_LONG).show();
            farmerC.close();
            isValid = false;
        }
        farmerC.close();

        Cursor machineCursor = db.getMachineByName(machineIdAct.getText().toString().trim());
        if (machineCursor.moveToFirst()) {
            String machineId = machineCursor.getString(machineCursor.getColumnIndex(DBAdapter.MACHINE_ID));
            data.setMachineId(machineId);
        } else {


            //    Toast.makeText(SprayDetailOneActivity.this, "Machine name not exist.", Toast.LENGTH_LONG).show();


            machineCursor.close();
            isValid = false;
        }
        machineCursor.close();

        if (sprayCropDataArrayList == null || sprayCropDataArrayList.size() == 0) {
            //   Toast.makeText(SprayDetailOneActivity.this, "Please add crop acre", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        boolean totalAcre = (data.getTotalAcrage() != null && data.getTotalAcrage().trim().length() > 0);
        if (!totalAcre) {
            //   Toast.makeText(SprayDetailOneActivity.this, "Please enter total acre", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        Cursor imageCursor = db.getImageByFormIdAndName(data.getSprayMonitorId(), Constents.FARMER_WITH_PRODUCT_DIR);
        if (imageCursor.getCount() <= 0) {
            //    Toast.makeText(SprayDetailOneActivity.this, "Please select farmer with product photo.", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        imageCursor.close();

        return isValid;
    }

    private boolean setDataIntoObject() {
        boolean isValid = true;

        data.setFarmerContact(farmerContactAct.getText().toString().trim());
        data.setTotalAcrage(totalAcreEdt.getText().toString());
        Cursor machineCursor = db.getMachineByName(machineIdAct.getText().toString().trim());
        if (machineCursor.moveToFirst()) {
            data.setMachineId(machineCursor.getString(machineCursor.getColumnIndex(DBAdapter.MACHINE_ID)));
        }
        machineCursor.close();

        System.out.println("data.getFarmerSendingStatus() : " + data.getFarmerSendingStatus());
        if (data.getFarmerContact() != null && data.getFarmerContact().trim().length() > 0) {
            if (data.getFarmerSendingStatus().equals(DBAdapter.SENT) || data.getFarmerSendingStatus().equals(DBAdapter.CONFIRM)) {
                isValid = true;
            } else if (data.getFarmerSendingStatus().equals(DBAdapter.SUBMIT) || data.getFarmerSendingStatus().equals(DBAdapter.SAVE)) {
                isValid = true;
            } else {
                Toast.makeText(SprayDetailOneActivity.this, "Farmer does not exist. Please add the farmer.", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
        } else {
            Toast.makeText(SprayDetailOneActivity.this, "Farmer contact is blank", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

       /* Cursor imageCursor = db.getImageByFormIdAndName(data.getSprayMonitorId(), Constents.FARMER_WITH_PRODUCT_DIR);
        if (imageCursor.getCount() <= 0) {
            Toast.makeText(SprayDetailOneActivity.this, "Please select farmer with product photo.", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        imageCursor.close();*/
//        data.save(db);
        return isValid;
    }

    private void setCropInConfirmLayout(final SprayCropData sprayCropData) {
        if (addedCropLinLay.getChildCount() == 0) {

            final View confirmedProduct0 = innerInflater.inflate(R.layout.confirmed_crop_item, null, false);
            TextView cropNameTxt = (TextView) confirmedProduct0.findViewById(R.id.cropNameTxt);
            cropNameTxt.setText("CROPS");
            cropNameTxt.setTypeface(Typeface.DEFAULT_BOLD);
            cropNameTxt.setTextColor(getResources().getColor(R.color.color_primary_dark));
            TextView cropAcreTxt = (TextView) confirmedProduct0.findViewById(R.id.cropAcreTxt);
            cropAcreTxt.setVisibility(View.GONE);
            ImageButton remove = (ImageButton) confirmedProduct0.findViewById(R.id.remove);
            remove.setVisibility(View.GONE);

            addedCropLinLay.addView(confirmedProduct0);

            final View confirmedProduct = innerInflater.inflate(R.layout.confirmed_crop_item, null, false);
            TextView cropNameTxtH = (TextView) confirmedProduct.findViewById(R.id.cropNameTxt);
            cropNameTxtH.setText("CROP NAME");
            cropNameTxtH.setTypeface(Typeface.DEFAULT_BOLD);
            TextView cropAcreTxtH = (TextView) confirmedProduct.findViewById(R.id.cropAcreTxt);

           /* String crop_type = data.getCrop_type();

            if (crop_type!=null){
                cropType = crop_type;
            }

            if (cropType.equalsIgnoreCase("1")) {
                cropAcreTxtH.setText("ACRE");
            } else {
                cropAcreTxtH.setText("VOLUME");
            }*/

            cropAcreTxtH.setText("ACRE");

            cropAcreTxtH.setTypeface(Typeface.DEFAULT_BOLD);
            ImageButton removeH = (ImageButton) confirmedProduct.findViewById(R.id.remove);
            removeH.setVisibility(View.INVISIBLE);

            addedCropLinLay.addView(confirmedProduct);
            addedCropLinLay.setVisibility(View.VISIBLE);

            String languagePrefrance = AppManager.getLanguagePrefs(SprayDetailOneActivity.this);
            if (languagePrefrance.equals("2")) {
                addDynamicLable(cropNameTxt, cropNameTxtH, cropAcreTxtH);
            }

        }
//        Toast.makeText(SprayDetailOneActivity.this, "Crop Added", Toast.LENGTH_SHORT).show();
        final View confirmedCropView = innerInflater.inflate(R.layout.confirmed_crop_item, null, false);
        TextView cropNameTxtV = (TextView) confirmedCropView.findViewById(R.id.cropNameTxt);
        cropNameTxtV.setText(sprayCropData.getCropName());
        TextView cropAcreTxtV = (TextView) confirmedCropView.findViewById(R.id.cropAcreTxt);
        cropAcreTxtV.setText(sprayCropData.getCropAcre());

        ImageButton remove = (ImageButton) confirmedCropView.findViewById(R.id.remove);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ViewGroup) confirmedCropView.getParent()).removeView(confirmedCropView);
                Iterator<SprayCropData> iter = sprayCropDataArrayList.iterator();
                while (iter.hasNext()) {
                    if (iter.next().getSprayCropId().equals(sprayCropData.getSprayCropId())) {
                        double acre = 0.0;
                        try {
                            acre = Double.parseDouble(sprayCropData.getCropAcre());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                        double totalAcre = 0.0;
                        try {
                            totalAcre = Double.parseDouble(totalAcreEdt.getText().toString());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                        if (totalAcre >= acre) {
                            totalAcre = totalAcre - acre;
                        }
                        totalAcreEdt.setText(String.valueOf(totalAcre));
                        data.setTotalAcrage(String.valueOf(totalAcre));
                        if (actionCropDelValid(data)) {
                            data.save(db);
                        }

                        Toast.makeText(SprayDetailOneActivity.this, "Crop has deleted.", Toast.LENGTH_LONG).show();

                        db.db.delete(DBAdapter.TABLE_SPRAY_CROPS, DBAdapter.SPRAY_CROP_ID + " = '" + sprayCropData.getSprayCropId() + "'", null);

                        iter.remove();
                    }
                }
                System.out.println("Crop size : " + sprayCropDataArrayList.size());
            }
        });

        addedCropLinLay.addView(confirmedCropView);

        cropAcreEdt.setText("");
    }

    private void setDefaultData(SprayMonitorData data) {
//        machineIdAct.setText(data.getMachineId());
        if (data.getMachineId() != null && (!data.getMachineId().equals("-1"))) {
            machineIdAct.setText(db.getMachineNameById(data.getMachineId()));
            machineIdAct.setSelection(data.getMachineId().length());

            /*Cursor getAllMachine = db.getAllMachine();
            int machineCount = getAllMachine.getCount();
            System.out.println("machineId : " + data.getMachineId() + " machineCount : " + machineCount);
            if (machineCount > 0 && (!data.getMachineId().equals("-1"))) {
                getAllMachine.moveToFirst();
                for (int d = 1; d <= machineCount; d++) {
                    if (data.getMachineId().trim().equals(getAllMachine.getString(getAllMachine.getColumnIndex(DBAdapter.MACHINE_ID)))) {

                        break;
                    }
                    getAllMachine.moveToNext();
                }
            }*/
        }
        if (data.getFarmerName() != null && data.getFarmerName().length() > 0) {
            String farmerDetailString = data.getFarmerName();
            farmerDetailTxt.setText(farmerDetailString);
            farmerDetailTxt.setVisibility(View.VISIBLE);

        }


        if (data.getFarmerContact() != null && data.getFarmerContact().length() > 0) {
            farmerContactAct.setText(data.getFarmerContact());
            farmerContactAct.setSelection(data.getFarmerContact().length());


            newFarLay.setVisibility(View.GONE);
            oldFarLay.setVisibility(View.VISIBLE);

        }

        if (data.getTotalAcrage() != null) {
            totalAcreEdt.setText(data.getTotalAcrage());
        }

        Cursor getFarmerWithProductImg = db.getImageByFormIdAndName(data.getSprayMonitorId(), Constents.FARMER_WITH_PRODUCT_DIR);
        if (getFarmerWithProductImg.getCount() > 0) {
            getFarmerWithProductImg.moveToLast();
            String imagePath = getFarmerWithProductImg.getString(getFarmerWithProductImg.getColumnIndex(DBAdapter.IMAGE_PATH));
            Bitmap bmp = BitmapFactory.decodeFile(imagePath);
            farmerWithProductImageView.setImageBitmap(bmp);
        }
        getFarmerWithProductImg.close();

        Cursor machineImageCuresor = db.getImageByFormIdAndName(data.getSprayMonitorId(), Constents.MACHINE_IMAGE_DIR);
        System.out.println("Spray id for machine : " + data.getSprayMonitorId() + " cursor count : " + machineImageCuresor.getCount());
        if (machineImageCuresor.getCount() > 0) {
            machineImageCuresor.moveToLast();
            String imagePath = machineImageCuresor.getString(machineImageCuresor.getColumnIndex(DBAdapter.IMAGE_PATH));
            Bitmap bmp = BitmapFactory.decodeFile(imagePath);
            machineImageView.setImageBitmap(bmp);
        }
        machineImageCuresor.close();

        System.out.println("sprayCropDataArrayList size : " + sprayCropDataArrayList.size());
        if (sprayCropDataArrayList != null) {
            for (SprayCropData sprayCropData : sprayCropDataArrayList) {
                setCropInConfirmLayout(sprayCropData);
            }
        }

        String cropType = data.getCrop_type();

        Log.v("kdkdkkopkpocks", cropType + "");

        if (cropType != null) {

            if (cropType.equalsIgnoreCase("2")) {

                fieldRadioButton.setChecked(false);
                orchardRadioButton.setChecked(true);

                fieldRadioButton.setEnabled(false);
                orchardRadioButton.setEnabled(true);

            } else {
                fieldRadioButton.setChecked(true);
                orchardRadioButton.setChecked(false);

                fieldRadioButton.setEnabled(true);
                orchardRadioButton.setEnabled(false);
            }

        } else {

            cropTypeGroup.setEnabled(true);
        }
    }

    private SprayMonitorData sprayDataInit(SprayMonitorData data) {
        if (data == null) {
            Credential credential = new Credential();
            Cursor credentialCursor = db.getCredential();
            if (credentialCursor.getCount() > 0) {
                credentialCursor.moveToFirst();
                credential = new Credential(credentialCursor);
            }
            credentialCursor.close();
            data = new SprayMonitorData();
            String id = credential.getImei() + "_" + String.valueOf(System.currentTimeMillis());
            data.setSprayMonitorId(id);
            data.setLatitude(String.valueOf(Lat_Lon_CellID.lat));
            data.setLongitude(String.valueOf(Lat_Lon_CellID.lon));
            Date date = new Date();
            String dateTime = Lat_Lon_CellID.sdf.format(date);
            data.setDeviceDateTime(dateTime);
            data.setLacId(String.valueOf(Lat_Lon_CellID.LAC));
            data.setCellId(String.valueOf(Lat_Lon_CellID.CellID));
            data.setMcc(String.valueOf(Lat_Lon_CellID.mcc));
            data.setMnc(String.valueOf(Lat_Lon_CellID.mnc));
            data.setSendingStatus(DBAdapter.SAVE);
        }
        return data;
    }

    private void setFarmerDetail(FarmerData farmerData) {
        if (farmerData != null) {
            data.setFarmerName(farmerData.getFirstName() + " " + farmerData.getLastName());
            data.setFarmerId(farmerData.getFarmerId(), db);
            data.setFarmerContact(farmerData.getFarmerContact());
            data.setState(farmerData.getStateId());
            data.setDistrict(farmerData.getDistrictId());
            data.setTehsil(farmerData.getTehsilId());
            data.setVillage(farmerData.getVillageId(), db);
            data.setCrop(farmerData.getCropId());
            farmerRegisteredStatusTxt.setText(farmerData.getSendingStatus());
            if (farmerData.getSendingStatus().equals(DBAdapter.SAVE)) {
                farmerRegisteredStatusTxt.setTextColor(getResources().getColor(R.color.fail_status));
                farmerRegisteredStatusTxt.setText("UN-REGISTERED");
            } else if (farmerData.getSendingStatus().equals(DBAdapter.SUBMIT)) {
                farmerRegisteredStatusTxt.setTextColor(getResources().getColor(R.color.submit_status));
                farmerRegisteredStatusTxt.setText("REGISTERED");
            } else {
                farmerRegisteredStatusTxt.setTextColor(getResources().getColor(R.color.save_status));
                farmerRegisteredStatusTxt.setText("REGISTERED");
            }
//            setDefaultData(data);
        }
    }

    public static final int REQUEST_MACHINE_IMAGE = 115;
    public static final int REQUEST_PRODUCT = 112;
    public static boolean isCaptured = false; //to check that we have returned from camera intent not just orientation has changed
    String lowImagePath;
    String highImagePath;

    Button.OnClickListener farmerProductGalOnClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            ArrayList<ImageData> images = new ArrayList<>();

            Cursor imageCursor = db.getImageByFormIdAndName(data.getSprayMonitorId(), Constents.FARMER_WITH_PRODUCT_DIR);
            if (imageCursor.getCount() > 0) {
                imageCursor.moveToFirst();
                do {
                    ImageData imageData = new ImageData(imageCursor);
                    images.add(imageData);
                } while (imageCursor.moveToNext());
            }

            Toast.makeText(SprayDetailOneActivity.this, "Gallary is clicked", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(SprayDetailOneActivity.this, GalleryActivity.class);
            intent.putParcelableArrayListExtra("IMAGE", images);
            SprayDetailOneActivity.this.startActivity(intent);
        }
    };

    View.OnClickListener farmerProductImagListner = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i("MakeMachine", "startCameraActivity()");
            if (AppManager.isGPSenabled(SprayDetailOneActivity.this)) {
                getLocation();
                isCaptured = true;
                String imgName = String.valueOf(System.currentTimeMillis()) + "_" + Constents.FARMER_WITH_PRODUCT_DIR;
                Map<String, String> pathMap = FolderManager.getImgDir(data.getSprayMonitorId(), Constents.FARMER_WITH_PRODUCT_DIR, imgName);

                highImagePath = pathMap.get(FolderManager.HIGH_RESOLUTION_DIRECTORY);
                lowImagePath = pathMap.get(FolderManager.LOW_RESOLUTION_DIRECTORY);

                System.out.println("imagePath " + highImagePath);

                File file = new File(highImagePath);
                //  Uri outputFileUri = Uri.fromFile(file);

                Uri outputFileUri = FileProvider.getUriForFile(SprayDetailOneActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    grantUriPermission(packageName, outputFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                if (outputFileUri != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(intent, REQUEST_PRODUCT);
                }
            }

        }
    };


    Button.OnClickListener machineImageGalOnClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            ArrayList<ImageData> images = new ArrayList<>();

            Cursor imageCursor = db.getImageByFormIdAndName(data.getSprayMonitorId(), Constents.MACHINE_IMAGE_DIR);
            if (imageCursor.getCount() > 0) {
                imageCursor.moveToFirst();
                do {
                    ImageData imageData = new ImageData(imageCursor);
                    images.add(imageData);
                } while (imageCursor.moveToNext());
            }

            Toast.makeText(SprayDetailOneActivity.this, "Gallary is clicked", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(SprayDetailOneActivity.this, GalleryActivity.class);
            intent.putParcelableArrayListExtra("IMAGE", images);
            SprayDetailOneActivity.this.startActivity(intent);
        }
    };

    View.OnClickListener machineImagListner = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i("MakeMachine", "startCameraActivity()");
            if (AppManager.isGPSenabled(SprayDetailOneActivity.this)) {
                getLocation();
                isCaptured = true;
                String imgName = String.valueOf(System.currentTimeMillis()) + "_" + Constents.MACHINE_IMAGE_DIR;
                Map<String, String> pathMap = FolderManager.getImgDir(data.getSprayMonitorId(), Constents.MACHINE_IMAGE_DIR, imgName);

                highImagePath = pathMap.get(FolderManager.HIGH_RESOLUTION_DIRECTORY);
                lowImagePath = pathMap.get(FolderManager.LOW_RESOLUTION_DIRECTORY);

                System.out.println("imagePath " + highImagePath);

                File file = new File(highImagePath);
                // Uri outputFileUri = Uri.fromFile(file);

                Uri outputFileUri = FileProvider.getUriForFile(SprayDetailOneActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    grantUriPermission(packageName, outputFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                Log.v("kskks", outputFileUri + "");

                if (outputFileUri != null) {

                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(intent, REQUEST_MACHINE_IMAGE);
                }
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);

        Log.v("abc", "" + requestCode);
        Log.v("def", "" + resultCode);
        Log.v("ijk", "" + intent);
        if (resultCode == RESULT_OK) {

            Log.i("MakeMachine", "resultCode: " + resultCode);
            switch (resultCode) {
                case 0:
                    Log.i("MakeMachine", "User cancelled");

                    if (requestCode == REQUEST_MACHINE_IMAGE) {
                        if (isCaptured) {
                            isCaptured = false;


                            //        data.setSite_picture(lowPath);
                            OutputStream stream = null;


                            ResizeBitmap rb = new ResizeBitmap();
                            Bitmap bmp = rb.decodeSampledBitmapFromUri(highImagePath, Constents.IMAGE_HIEGHT, Constents.IMAGE_WIDTH);
                            try {
                                stream = new FileOutputStream(lowImagePath);

                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            if (bmp != null) {

                                try {

                                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                    machineImageView.setImageBitmap(bmp);
                                    Cursor getImageByFormIdAndName = db.getImageByFormIdAndName(data.getSprayMonitorId(), Constents.MACHINE_IMAGE_DIR);
                                    int imageCount = getImageByFormIdAndName.getCount() + 1;
                                    ImageData idSaveImage = new ImageData(Constents.MACHINE_IMAGE_DIR, lowImagePath, "" + latitude, "" + longitude, datetimestamp, data.getSprayMonitorId(), String.valueOf(imageCount), DBAdapter.SAVE, CellID, lac, mcc, mnc);
                                    idSaveImage.save(db);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }

                    if (requestCode == REQUEST_PRODUCT) {
                        if (isCaptured) {
                            isCaptured = false;


                            //        data.setSite_picture(lowPath);
                            OutputStream stream = null;

                            ResizeBitmap rb = new ResizeBitmap();
                            Bitmap bmp = rb.decodeSampledBitmapFromUri(highImagePath, Constents.IMAGE_HIEGHT, Constents.IMAGE_WIDTH);

                            try {
                                stream = new FileOutputStream(lowImagePath);

                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            if (bmp != null) {

                                try {

                                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                    farmerWithProductImageView.setImageBitmap(bmp);
                                    Cursor getImageByFormIdAndName = db.getImageByFormIdAndName(data.getSprayMonitorId(), Constents.FARMER_WITH_PRODUCT_DIR);
                                    int imageCount = getImageByFormIdAndName.getCount() + 1;
                                    ImageData idSaveImage = new ImageData(Constents.FARMER_WITH_PRODUCT_DIR, lowImagePath, "" + latitude, "" + longitude, datetimestamp, data.getSprayMonitorId(), String.valueOf(imageCount), DBAdapter.SAVE, CellID, lac, mcc, mnc);
                                    idSaveImage.save(db);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }

                    break;
                case -1:
//                Match request code to get the image id
                    if (requestCode == REQUEST_MACHINE_IMAGE) {
                        if (isCaptured) {
                            isCaptured = false;

                            ResizeBitmap rb = new ResizeBitmap();
                            Bitmap bmp = rb.decodeSampledBitmapFromUri(highImagePath, Constents.IMAGE_HIEGHT, Constents.IMAGE_WIDTH);
                            //        data.setSite_picture(lowPath);
                            OutputStream stream = null;
                            try {
                                stream = new FileOutputStream(lowImagePath);
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }


                            if (stream != null && bmp != null) {

                                try {
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                    machineImageView.setImageBitmap(bmp);
                                    Cursor getImageByFormIdAndName = db.getImageByFormIdAndName(data.getSprayMonitorId(), Constents.MACHINE_IMAGE_DIR);
                                    int imageCount = getImageByFormIdAndName.getCount() + 1;
                                    ImageData idSaveImage = new ImageData(Constents.MACHINE_IMAGE_DIR, lowImagePath, "" + latitude, "" + longitude, datetimestamp, data.getSprayMonitorId(), String.valueOf(imageCount), DBAdapter.SAVE,
                                            CellID, lac, mcc, mnc);
                                    idSaveImage.save(db);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                        }
                    }

                    if (requestCode == REQUEST_PRODUCT) {
                        if (isCaptured) {
                            isCaptured = false;

                            ResizeBitmap rb = new ResizeBitmap();
                            Bitmap bmp = rb.decodeSampledBitmapFromUri(highImagePath, Constents.IMAGE_HIEGHT, Constents.IMAGE_WIDTH);
                            //        data.setSite_picture(lowPath);
                            OutputStream stream = null;
                            try {
                                stream = new FileOutputStream(lowImagePath);

                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }


                            if (bmp != null) {

                                try {

                                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                    farmerWithProductImageView.setImageBitmap(bmp);
                                    Cursor getImageByFormIdAndName = db.getImageByFormIdAndName(data.getSprayMonitorId(), Constents.FARMER_WITH_PRODUCT_DIR);
                                    int imageCount = getImageByFormIdAndName.getCount() + 1;
                                    ImageData idSaveImage = new ImageData(Constents.FARMER_WITH_PRODUCT_DIR, lowImagePath, "" + latitude, "" + longitude, datetimestamp, data.getSprayMonitorId(), String.valueOf(imageCount), DBAdapter.SAVE, CellID, lac, mcc, mnc);
                                    idSaveImage.save(db);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                    break;
            }
        } else {

            Log.v("cancel", "cancel");
        }
    }

    private static final String PRODUCT_DATA_ARRAY = "product_data_array";
    private static final String SPRAY_CROP_DATA_ARRAY = "spray_crop_data_array";
    private static final String TANK_DATA_ARRAY = "tank_data_array";

    private static final String SPRAY_DATA = "spray_data";
    private static final String FARMER_DATA = "farmer_data";
    private static final String SPRAY_CROP_DATA = "spray_crop_data";

    private static final String IS_CAPTURE = "is_captured";
    private static final String LOW_IMAGE_PATH = "low_image_path";
    private static final String HIGH_IMAGE_PATH = "high_image_path";

    private static final String FARMER_CONTACT_ACT = "farmer_contact_act";
    private static final String FARMER_DETAIL_TEXT = "farmer_detail_txt";
    private static final String FARMER_REGISTRATION_TXT = "farmer_registration_txt";
    private static final String MACHINE_ACT = "machine_act";
    private static final String MACHINE_IMAGE_PATH = "machine_image_path";
    private static final String ADDED_CROP_LAYOUT = "added_crop_layout";
    private static final String CROP_SPINNER = "crop_spinner";
    private static final String CROP_ACRE_EDT = "crop_acre_edt";
    private static final String TOTAL_ACRE_EDT = "total_acre_edt";
    private static final String IS_MIDDILE_INTENT = "is_middile_intent";
    private static final String FARMER_WITH_PRODUCT_IMAGE = "farmer_with_product";


    double latitude;
    double longitude;
    String datetimestamp;
    String CellID;
    String lac;
    String mcc;
    String mnc;


    private void getLocation() {


        latitude = Lat_Lon_CellID.lat;
        longitude = Lat_Lon_CellID.lon;
        datetimestamp = Lat_Lon_CellID.getDatetimestamp();
        CellID = Lat_Lon_CellID.getCELLID();
        lac = Lat_Lon_CellID.getLac();
        mcc = Lat_Lon_CellID.getMCC();
        mnc = Lat_Lon_CellID.getMNC();

        Log.v("latlong", latitude + "");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i("MakeMachine", "onRestoreInstanceState()");

        productDataArrayList = savedInstanceState.getParcelableArrayList(PRODUCT_DATA_ARRAY);

        sprayCropDataArrayList = savedInstanceState.getParcelableArrayList(SPRAY_CROP_DATA_ARRAY);

        if (sprayCropDataArrayList != null) {
            if (sprayCropDataArrayList.size() > 0) {
                addedCropLinLay.removeAllViews();
                for (SprayCropData sprayCropData : sprayCropDataArrayList) {
                    setCropInConfirmLayout(sprayCropData);
                }
            }
        }

        tankFillingDataArrayList = savedInstanceState.getParcelableArrayList(TANK_DATA_ARRAY);
        data = savedInstanceState.getParcelable(SPRAY_DATA);
        farmerData = savedInstanceState.getParcelable(FARMER_DATA);
        sprayCropData = savedInstanceState.getParcelable(SPRAY_CROP_DATA);
        isCaptured = savedInstanceState.getBoolean(IS_CAPTURE);
        lowImagePath = savedInstanceState.getString(LOW_IMAGE_PATH);
        highImagePath = savedInstanceState.getString(HIGH_IMAGE_PATH);

        farmerContactAct.setText(savedInstanceState.getString(FARMER_CONTACT_ACT));
        String ss = savedInstanceState.getString(FARMER_CONTACT_ACT);
        if (ss != null && ss.length() > 0) {

            farmerContactAct.setSelection(ss.length());
            newFarLay.setVisibility(View.GONE);
            oldFarLay.setVisibility(View.VISIBLE);
        }

        farmerDetailTxt.setText(savedInstanceState.getString(FARMER_DETAIL_TEXT));
        farmerRegisteredStatusTxt.setText(savedInstanceState.getString(FARMER_REGISTRATION_TXT));
        machineIdAct.setText(savedInstanceState.getString(MACHINE_ACT));

        switch (savedInstanceState.getInt(ADDED_CROP_LAYOUT)) {
            case View.VISIBLE:
                addedCropLinLay.setVisibility(View.VISIBLE);
                break;
            case View.GONE:
                addedCropLinLay.setVisibility(View.GONE);
                break;
            default:
                addedCropLinLay.setVisibility(View.GONE);
        }

        //   cropSpinner.setText(savedInstanceState.getString(CROP_SPINNER));
        cropAcreEdt.setText(savedInstanceState.getString(CROP_ACRE_EDT));
        totalAcreEdt.setText(savedInstanceState.getString(TOTAL_ACRE_EDT));

        latitude = savedInstanceState.getDouble(Lat_Lon_CellID.LATITUDE);
        longitude = savedInstanceState.getDouble(Lat_Lon_CellID.LONGITUDE);
        datetimestamp = savedInstanceState.getString(Lat_Lon_CellID.DATE_TIME_STAMP);
        CellID = savedInstanceState.getString(Lat_Lon_CellID.CELL_ID);
        lac = savedInstanceState.getString(Lat_Lon_CellID.LAC_ID);
        mcc = savedInstanceState.getString(Lat_Lon_CellID.MCC);
        mnc = savedInstanceState.getString(Lat_Lon_CellID.MNC);

        Cursor getFarmerWithProductImg = db.getImageByFormIdAndName(data.getSprayMonitorId(), Constents.FARMER_WITH_PRODUCT_DIR);
        if (getFarmerWithProductImg.getCount() > 0) {
            getFarmerWithProductImg.moveToLast();
            String imagePath = getFarmerWithProductImg.getString(getFarmerWithProductImg.getColumnIndex(DBAdapter.IMAGE_PATH));
            Bitmap bmp = BitmapFactory.decodeFile(imagePath);
            farmerWithProductImageView.setImageBitmap(bmp);
        }
        getFarmerWithProductImg.close();

        Cursor machineImageCuresor = db.getImageByFormIdAndName(data.getSprayMonitorId(), Constents.MACHINE_IMAGE_DIR);
        System.out.println("Spray id for machine : " + data.getSprayMonitorId() + " cursor count : " + machineImageCuresor.getCount());
        if (machineImageCuresor.getCount() > 0) {
            machineImageCuresor.moveToLast();
            String imagePath = machineImageCuresor.getString(machineImageCuresor.getColumnIndex(DBAdapter.IMAGE_PATH));
            Bitmap bmp = BitmapFactory.decodeFile(imagePath);
            machineImageView.setImageBitmap(bmp);
        }
        machineImageCuresor.close();


        setLocation();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(PRODUCT_DATA_ARRAY, productDataArrayList);
        outState.putParcelableArrayList(SPRAY_CROP_DATA_ARRAY, sprayCropDataArrayList);
        outState.putParcelableArrayList(TANK_DATA_ARRAY, tankFillingDataArrayList);
        outState.putParcelable(SPRAY_DATA, data);
        outState.putParcelable(FARMER_DATA, farmerData);
        outState.putParcelable(SPRAY_CROP_DATA, sprayCropData);
        outState.putBoolean(IS_CAPTURE, isCaptured);
        outState.putString(LOW_IMAGE_PATH, lowImagePath);
        outState.putString(HIGH_IMAGE_PATH, highImagePath);

        outState.putString(FARMER_CONTACT_ACT, farmerContactAct.getText().toString().trim());
        outState.putString(FARMER_DETAIL_TEXT, farmerDetailTxt.getText().toString());
        outState.putString(FARMER_REGISTRATION_TXT, farmerRegisteredStatusTxt.getText().toString());
        outState.putString(MACHINE_ACT, machineIdAct.getText().toString().trim());

        System.out.println("Layout on save : " + addedCropLinLay.getVisibility());
        outState.putInt(ADDED_CROP_LAYOUT, addedCropLinLay.getVisibility());
        //   outState.putString(CROP_SPINNER, cropSpinner.getText().toString());
        outState.putString(CROP_ACRE_EDT, cropAcreEdt.getText().toString());
        outState.putString(TOTAL_ACRE_EDT, totalAcreEdt.getText().toString());

        outState.putDouble(Lat_Lon_CellID.LATITUDE, latitude);
        outState.putDouble(Lat_Lon_CellID.LONGITUDE, longitude);
        outState.putString(Lat_Lon_CellID.DATE_TIME_STAMP, datetimestamp);
        outState.putString(Lat_Lon_CellID.CELL_ID, CellID);
        outState.putString(Lat_Lon_CellID.LAC_ID, lac);
        outState.putString(Lat_Lon_CellID.MCC, mcc);
        outState.putString(Lat_Lon_CellID.MNC, mnc);
    }

    private void setLocation() {
        Lat_Lon_CellID.lat = latitude;
        Lat_Lon_CellID.lon = longitude;
        Lat_Lon_CellID.datetimestamp = datetimestamp;
        Lat_Lon_CellID.CellID = CellID;
        Lat_Lon_CellID.LAC = lac;
        Lat_Lon_CellID.mcc = mcc;
        Lat_Lon_CellID.mnc = mnc;
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
