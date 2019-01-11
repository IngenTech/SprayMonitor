package com.wrms.spraymonitor;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.wrms.spraymonitor.app.Constents;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.background.Lat_Lon_CellID;
import com.wrms.spraymonitor.dataobject.ProductData;
import com.wrms.spraymonitor.dataobject.SprayCropData;
import com.wrms.spraymonitor.dataobject.SprayMonitorData;
import com.wrms.spraymonitor.dataobject.TankFillingData;
import com.wrms.spraymonitor.dataobject.VideoData;
import com.wrms.spraymonitor.utils.AppManager;
import com.wrms.spraymonitor.utils.FolderManager;

import net.ypresto.androidtranscoder.MediaTranscoder;
import net.ypresto.androidtranscoder.format.MediaFormatStrategyPresets;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

public class SprayDetailTwoActivity extends AppCompatActivity {

    public static final int REQUEST_CROP_IMG_AFTER_SPRAY = 12;
    public static final int REQUEST_MIX_PREP_VID = 114;

    LinearLayout addedTankFillingDetailLinLay;
    ImageButton mixturePreparationVidImgBut;
    ImageButton mixturePreparationGalImgBut;
    AutoCompleteTextView cropSpinner;
    EditText cropAcreEdt;
    String acreText;

    LinearLayout confirmedProductLinearLayout;
    LinearLayout addProductLayout;
    Button addProductLayoutButton;
    AutoCompleteTextView productSpinner;
    EditText productQtyEdt;
    Spinner productUomSpinner;
    Button addProductConfirmationButton;
    Button addTankDetailConfirmationButton;

    EditText actualAcreCovered;
    EditText machineStartTime;
    EditText machineStopTime;

    TextView actualAcreCoveredTxt;
    TextView actualAcreCoveredTxt1;
    TextView mixturePreparationTxt;
    TextView mixturePrepInstructionTxt;
    TextView cropTxt;
    TextView coveredAcreTxt;
    TextView coveredAcreTxt1;
    TextView machineStartTimeTxt;
    TextView machineStopTimeTxt;
    TextView productInstructionTxt;
    TextView productNameTxt;
    TextView productQtyTxt;
    TextView productUomTxt;

    LayoutInflater innerInflater;

    SprayMonitorData data;
    ArrayList<ProductData> productDataArrayList = new ArrayList<>();
    ArrayList<SprayCropData> sprayCropDataArrayList = new ArrayList<>();
    ArrayList<TankFillingData> tankFillingDataArrayList = new ArrayList<>();
    DBAdapter db;

    TankFillingData tankFillingData;
    ProductData productData = new ProductData();

    String lowVideoPath;
    String highVideoPath;
    public static boolean isCaptured = false; //to check that we have returned from camera intent not just orientation has changed

    private View mLayout;
    public static final String TAG = SprayDetailTwoActivity.class.getSimpleName();
    private static final int REQUEST_MICRO_PHONE = 6;

    String timeFormatString = "yyyy-MM-dd HH:mm:ss";
    SimpleDateFormat timeFormatter = new SimpleDateFormat(timeFormatString);

    Button backBTN, nextBTN;
    String str_acre_check;

    ArrayAdapter<String> cropArrayAdapter;
    Cursor getAllCrop;
    ArrayList<String> cropStringArray;
    LinearLayout videoLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spray_detail_two);

        mLayout = findViewById(R.id.SprayDetailTwoActivity);

        videoLayout = (LinearLayout)findViewById(R.id.video_layout);



        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // only for gingerbread and newer versions
            videoLayout.setVisibility(View.VISIBLE);
        }else {

            videoLayout.setVisibility(View.GONE);
        }

        backBTN = (Button) findViewById(R.id.saveBTN);
        nextBTN = (Button) findViewById(R.id.nextBTN);

        addedTankFillingDetailLinLay = (LinearLayout) findViewById(R.id.addedTankFillingDetailLinLay);
        mixturePreparationVidImgBut = (ImageButton) findViewById(R.id.mixturePreparationVidImgBut);
        mixturePreparationGalImgBut = (ImageButton) findViewById(R.id.mixturePreparationGalImgBut);
        cropSpinner = (AutoCompleteTextView) findViewById(R.id.cropSpinner);
        cropAcreEdt = (EditText) findViewById(R.id.cropAcreEdt);

        confirmedProductLinearLayout = (LinearLayout) findViewById(R.id.confirmedProductLinearLayout);
        addProductLayout = (LinearLayout) findViewById(R.id.addProductLayout);
        addProductLayoutButton = (Button) findViewById(R.id.addProductLayoutButton);
        productSpinner = (AutoCompleteTextView) findViewById(R.id.productSpinner);
        productQtyEdt = (EditText) findViewById(R.id.productQtyEdt);
        productUomSpinner = (Spinner) findViewById(R.id.productUomSpinner);
        addProductConfirmationButton = (Button) findViewById(R.id.addProductConfirmationButton);
        addTankDetailConfirmationButton = (Button) findViewById(R.id.addTankDetailConfirmationButton);
        actualAcreCovered = (EditText) findViewById(R.id.actualAcreCovered);
        machineStopTime = (EditText) findViewById(R.id.machineStopTime);
        machineStartTime = (EditText) findViewById(R.id.machineStartTime);
        actualAcreCovered.setEnabled(false);

        actualAcreCoveredTxt = (TextView) findViewById(R.id.actualAcreCoveredTxt);
        actualAcreCoveredTxt1 = (TextView) findViewById(R.id.actualAcreCoveredTxt2);

        mixturePreparationTxt = (TextView) findViewById(R.id.mixturePreparationTxt);
        mixturePrepInstructionTxt = (TextView) findViewById(R.id.mixturePrepInstructionTxt);
        cropTxt = (TextView) findViewById(R.id.cropTxt);
        coveredAcreTxt = (TextView) findViewById(R.id.coveredAcreTxt);
        coveredAcreTxt1 = (TextView) findViewById(R.id.coveredAcreTxt2);

        machineStartTimeTxt = (TextView) findViewById(R.id.machineStartTimeTxt);
        machineStopTimeTxt = (TextView) findViewById(R.id.machineStopTimeTxt);


        productInstructionTxt = (TextView) findViewById(R.id.productInstructionTxt);
        productNameTxt = (TextView) findViewById(R.id.productNameTxt);
        productQtyTxt = (TextView) findViewById(R.id.productQtyTxt);
        productUomTxt = (TextView) findViewById(R.id.productUomTxt);


        innerInflater = LayoutInflater.from(this);
        setSubtitleLanguage();

        getMicroPhone(mLayout);

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

        if (tankFillingData == null) {
            tankFillingData = initializeTankFillingData();
        }

        db = new DBAdapter(getApplicationContext());
        db.open();

        addProductLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                productSpinner.setText("");

                if (cropSpinner.getText().toString()==null || cropSpinner.getText().length() < 1) {

                    Toast.makeText(getApplicationContext(), "please select crop", Toast.LENGTH_SHORT).show();
                } else {

                    int productCount = 0;
                    for (ProductData pData : productDataArrayList) {
                        if (pData.getTankFillingId().equals(tankFillingData.getTankFillingId())) {
                            productCount++;
                        }
                    }

                    if (productCount < 5) {
                        addProductLayout.setVisibility(View.VISIBLE);
                        addProductLayoutButton.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(SprayDetailTwoActivity.this, "You have added five products", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

        final Cursor getAllProduct = db.getAllProduct();
        final int allProductCount = getAllProduct.getCount();
        final ArrayList<String> productStringArray = new ArrayList<String>();

        if (allProductCount > 0) {
            getAllProduct.moveToFirst();
            for (int i = 1; i <= allProductCount; i++) {
                productStringArray.add(getAllProduct.getString(getAllProduct.getColumnIndex(DBAdapter.PRODUCT)));
                getAllProduct.moveToNext();
            }
        }


        final ArrayAdapter<String> productArrayAdapter = new ArrayAdapter<String>(SprayDetailTwoActivity.this,R.layout.auto_complete_textview_item, productStringArray); //selected item will look like a spinner set from XML
       // productArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSpinner.setThreshold(1);

        productSpinner.setAdapter(productArrayAdapter);
        productSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String productId = "0";
                System.out.println("Inside Product selection " + position);
                String productUomString = null;
                if (position >= 0) {

                    int index = productStringArray.indexOf(productSpinner.getText().toString());
                    getAllProduct.moveToPosition(index);
                    productId = getAllProduct.getString(getAllProduct.getColumnIndex(DBAdapter.PRODUCT_ID));
                    String productName = getAllProduct.getString(getAllProduct.getColumnIndex(DBAdapter.PRODUCT));
                    productUomString = getAllProduct.getString(getAllProduct.getColumnIndex(DBAdapter.MEASURING_UNIT_ID));
                    productData.setProductId(productId);
                    productData.setProductName(productName);
                }
                data.setProductName(productId);
//                setUom(productId);

                if (productUomString != null && productUomString.trim().length() > 0) {


                    final String[] uoms = productUomString.split(",");
                    final ArrayList<String> uomNameArray = new ArrayList<>();
                    for (String uomId : uoms) {
                        uomNameArray.add(db.getUomNameById(uomId));
                    }
                /*System.out.println("uomCursor.getPosition() : " + uomCursor.getPosition());
                if (uomCursor.moveToFirst()) {
                    do {
                        String materialString = uomCursor.getString(uomCursor.getColumnIndex(DBAdapter.MEASURING_UNIT));
                        uomNameArray.add(materiaglStrin);
                    } while (uomCursor.moveToNext());
                }*/
                    ArrayAdapter<String> material_adapter = new ArrayAdapter<String>(SprayDetailTwoActivity.this, android.R.layout.simple_spinner_item, uomNameArray);

                    material_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    productUomSpinner.setAdapter(material_adapter);
                    productUomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            System.out.println("OnItemSelected called with position : " + position);
                            if (position < uoms.length) {
                            /*uomCursor.moveToPosition(position);
                            String uomId = uomCursor.getString(uomCursor.getColumnIndex(DBAdapter.MEASURING_UNIT_ID));
                            String uomName = uomCursor.getString(uomCursor.getColumnIndex(DBAdapter.MEASURING_UNIT));*/
                                String uomId = uoms[position];
                                String uomName = uomNameArray.get(position);
                                productData.setUomId(uomId);
                                productData.setUomName(uomName);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }

            }


        });


        addProductConfirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidProduct()) {
                    int productCount = 0;
                    for (ProductData pData : productDataArrayList) {
                        if (pData.getTankFillingId().equals(tankFillingData.getTankFillingId())) {
                            productCount++;
                        }
                    }

                    if (productCount < 5) {

                        final String productDataId = String.valueOf(System.currentTimeMillis());
                        productData.setProductDataId(productDataId);
                        System.out.println("tank filling id with product : " + tankFillingData.getTankFillingId());
                        productData.setTankFillingId(tankFillingData.getTankFillingId());
                        productData.save(db);
                        productDataArrayList.add(productData);
                        addProductInConfirmLayout(productData, productCount);

                        String productUomName = productData.getUomName();
                        String productUomId = productData.getUomId();

                        productData = new ProductData();
                        productData.setUomName(productUomName);
                        productData.setUomId(productUomId);

                    } else {
                        Toast.makeText(SprayDetailTwoActivity.this, "Five products has been added", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mixturePreparationVidImgBut.setOnClickListener(mixturePreparationListner);
        mixturePreparationGalImgBut.setOnClickListener(mixtureGalOnClickListner);

       /* final ArrayList<String> sprayCrops = new ArrayList<>();

        sprayCrops.add("Select Crop");

        for (SprayCropData sprayCropData : sprayCropDataArrayList) {
            sprayCrops.add(sprayCropData.getCropName());
        }
        ArrayAdapter<String> sprayCropAdapter = new ArrayAdapter<String>(SprayDetailTwoActivity.this, android.R.layout.simple_spinner_item, sprayCrops);
        sprayCropAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        cropSpinner.setAdapter(sprayCropAdapter);
        cropSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    int pos = position - 1;
                    if (sprayCropDataArrayList.size() > pos) {
                        SprayCropData sprayCropData = sprayCropDataArrayList.get(pos);
                        tankFillingData.setTankFillingCropId(sprayCropData.getCropId());

                        acreText = sprayCropData.getCropAcre();

                        str_acre_check = sprayCropData.getCropAcre();


                        if (data.getCrop_type()!=null && data.getCrop_type().equalsIgnoreCase("2")){
                            cropAcreEdt.setText("");
                        }else {

                            cropAcreEdt.setText(sprayCropData.getCropAcre());
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/
        final SharedPreferences sharedPreferences = getSharedPreferences("crop_type", MODE_PRIVATE);
        String cropType = sharedPreferences.getString("crop_type",null);

        getAllCrop = db.getAllCropByType(cropType);
        cropStringArray = new ArrayList<>();
        cropStringArray.clear();
        System.out.println("cropLenght:" + getAllCrop.getCount());

        if (getAllCrop.moveToFirst()) {
            do {
                cropStringArray.add(getAllCrop.getString(getAllCrop.getColumnIndex(DBAdapter.CROP)));
            } while (getAllCrop.moveToNext());
        }

        cropArrayAdapter = new ArrayAdapter<String>(SprayDetailTwoActivity.this, R.layout.auto_complete_textview_item, cropStringArray); //selected item will look like a spinner set from XML
        cropSpinner.setThreshold(1);
        cropSpinner.setAdapter(cropArrayAdapter);


        cropSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (cropStringArray.size() > 0) {
                    try {

                        int index = cropStringArray.indexOf(cropSpinner.getText().toString());
                        getAllCrop.moveToPosition(index);
                        String cropId = getAllCrop.getString(getAllCrop.getColumnIndex(DBAdapter.CROP_ID));
                        String cropName = getAllCrop.getString(getAllCrop.getColumnIndex(DBAdapter.CROP));
                       /* sprayCropData.setCropId(cropId);
                        sprayCropData.setCropName(cropName);
*/
                        tankFillingData.setTankFillingCropId(cropId);

                        Log.d("cropID", cropId + "index" + index);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


        addTankDetailConfirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tankFillingDataArrayList.size() < 10) {
                    if (isValidTankFilling()) {
                        if (tankFillingData.save(db)) {
                            Toast.makeText(SprayDetailTwoActivity.this, "Tank filling detail is saved", Toast.LENGTH_SHORT).show();
                        }
                        tankFillingDataArrayList.add(tankFillingData);

                        double actualAcreDouble = 0.0;
                        try {
                            actualAcreDouble = Double.parseDouble(actualAcreCovered.getText().toString());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                        double acreCoveredDouble = 0.0;
                        try {
                            acreCoveredDouble = Double.parseDouble(tankFillingData.getAcreCoveredByTank());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                        actualAcreDouble = actualAcreDouble + acreCoveredDouble;
                        double amountReceivableDouble = actualAcreDouble * 75;
                        actualAcreCovered.setText(String.valueOf(actualAcreDouble));
                        data.setActualAcreCovered(String.valueOf(actualAcreDouble));
                        data.setAmountReceivable(String.valueOf(amountReceivableDouble));
                        data.save(db);

                        addTankDetailInConfirmLayout(tankFillingData);

                        tankFillingData = initializeTankFillingData();
                    }
                } else {
                    Toast.makeText(SprayDetailTwoActivity.this, "10 tank filling detail has been added", Toast.LENGTH_SHORT).show();
                }
            }
        });


        String cropTYPE = data.getCrop_type();

        if (cropTYPE!=null && cropTYPE.equalsIgnoreCase("2")){

        }else {

            cropAcreEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {


                    if (str_acre_check != null && str_acre_check.length() > 0) {
                        int ac = Integer.parseInt(str_acre_check);
                        String strrr = s.toString();
                        if (strrr.length() > 0) {

                            int st = Integer.parseInt(strrr);

                            if (st > ac) {

                                cropAcreEdt.setText("");
                                Toast.makeText(getApplicationContext(), "Please enter valid acres", Toast.LENGTH_SHORT).show();

                            }
                        }

                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }

        setDefaultData();

        AppManager.isGPSenabled(this);

        nextBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SprayDetailTwoActivity.this, SprayDetailThreeActivity.class);
                intent.putExtra(Constents.DATA, data);
                intent.putParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA, productDataArrayList);
                intent.putParcelableArrayListExtra(Constents.SPRAY_CROP_DATA, sprayCropDataArrayList);
                intent.putParcelableArrayListExtra(Constents.SPRAY_TANK_FILLINF_DATA, tankFillingDataArrayList);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                finish();
            }
        });

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SprayDetailTwoActivity.this, SprayDetailOneActivity.class);
                intent.putExtra(Constents.DATA, data);
                intent.putParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA, productDataArrayList);
                intent.putParcelableArrayListExtra(Constents.SPRAY_CROP_DATA, sprayCropDataArrayList);
                intent.putParcelableArrayListExtra(Constents.SPRAY_TANK_FILLINF_DATA, tankFillingDataArrayList);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                finish();
            }
        });

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

        actualAcreCoveredTxt.setTypeface(tf);
        actualAcreCoveredTxt.setText(getResources().getString(R.string.actual_acre_covered_txt));

        mixturePreparationTxt.setTypeface(tf);
        mixturePreparationTxt.setText(getResources().getString(R.string.mixture_preparation_txt));

        mixturePrepInstructionTxt.setTypeface(tf);
        mixturePrepInstructionTxt.setText(getResources().getString(R.string.mixture_preparation_instruction_txt));

        cropTxt.setTypeface(tf);
        cropTxt.setText(getResources().getString(R.string.crop_txt));

        coveredAcreTxt.setTypeface(tf);
        coveredAcreTxt.setText(getResources().getString(R.string.covered_acre_txt));

        machineStartTimeTxt.setTypeface(tf);
        machineStartTimeTxt.setText(getResources().getString(R.string.machine_start_time));

        machineStopTimeTxt.setTypeface(tf);
        machineStopTimeTxt.setText(getResources().getString(R.string.machine_stop_time));

        productInstructionTxt.setTypeface(tf);
        productInstructionTxt.setText(getResources().getString(R.string.product_instruction));

        productNameTxt.setTypeface(tf);
        productNameTxt.setText(getResources().getString(R.string.product_name_txt));

        productQtyTxt.setTypeface(tf);
        productQtyTxt.setText(getResources().getString(R.string.product_qty_txt));

        productUomTxt.setTypeface(tf);
        productUomTxt.setText(getResources().getString(R.string.product_uom_txt));

    }

    private void setHindiText() {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/krdv011.ttf");

      //  Typeface tf1 = Typeface.createFromAsset(getAssets(), "fonts/DroidHindi.ttf");

        actualAcreCoveredTxt.setTypeface(tf);
        actualAcreCoveredTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        actualAcreCoveredTxt.setText("okLrfod ,dM+ doj");

        actualAcreCoveredTxt1.setTypeface(tf);
        actualAcreCoveredTxt1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        actualAcreCoveredTxt1.setText("खपत मात्रा");

       // actualAcreCoveredTxt.setText(getResources().getString(R.string.actual_acre_covered_txt));

        mixturePreparationTxt.setTypeface(tf);
        mixturePreparationTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        mixturePreparationTxt.setText("feJ.k rS;kjh");

        mixturePrepInstructionTxt.setTypeface(tf);
        mixturePrepInstructionTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        mixturePrepInstructionTxt.setText("d`i;k 10 ldsUM ls T;knk dk fofM;¨ uk cuk;s ");

        cropTxt.setTypeface(tf);
        cropTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        cropTxt.setText("Qly");

        coveredAcreTxt.setTypeface(tf);
        coveredAcreTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        coveredAcreTxt.setText("lfEefyr ,dM+");

        coveredAcreTxt1.setTypeface(tf);
        coveredAcreTxt1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        coveredAcreTxt1.setText("पानी की मात्रा");

       // coveredAcreTxt.setText(getResources().getString(R.string.covered_acre_txt_hindi));

        machineStartTimeTxt.setTypeface(tf);
        machineStartTimeTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        machineStartTimeTxt.setText("e'khu dc pkyq dh x;h");

        machineStopTimeTxt.setTypeface(tf);
        machineStopTimeTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        machineStopTimeTxt.setText("e'khu dc cUn dh x;h");

        productInstructionTxt.setTypeface(tf);
        productInstructionTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        productInstructionTxt.setText("vki 5 mRikn rd t¨M ldrs gS");

        productNameTxt.setTypeface(tf);
        productNameTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        productNameTxt.setText("mRikn dk uke");

        productQtyTxt.setTypeface(tf);
        productQtyTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        productQtyTxt.setText("mRikn ek=k");

        productUomTxt.setTypeface(tf);
        productUomTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        productUomTxt.setText("mRikn ekud");
    }

    private void addDynamicLable(TextView title) {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/krdv011.ttf");

        if (title != null) {
            title.setTypeface(tf);
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            title.setText("Võd es Òjh ek=k dk fooju");
        }
    }

    private void addDynamicLable(TextView tankFillingCountTagTxt,
                                 TextView machineStartTimeTxt,
                                 TextView machineStopTimeTxt,
                                 TextView tankFillingCropTagTxt,
                                 TextView tankFillingAcreTagTxt,
                                 TextView tankFillingProductNameTxt,
                                 TextView tankFillingUomTxt,
                                 TextView tankFillingQuantityTxt) {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/krdv011.ttf");

        tankFillingCountTagTxt.setTypeface(tf);
        tankFillingCountTagTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tankFillingCountTagTxt.setText("Võd dk fooju");

        machineStartTimeTxt.setTypeface(tf);
        machineStartTimeTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        machineStartTimeTxt.setText("e'khu dc pkyq dh x;h");

        machineStopTimeTxt.setTypeface(tf);
        machineStopTimeTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        machineStopTimeTxt.setText("e'khu dc cUn dh x;h");

        tankFillingCropTagTxt.setTypeface(tf);
        tankFillingCropTagTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tankFillingCropTagTxt.setText("Qly");

        tankFillingAcreTagTxt.setTypeface(tf);
        tankFillingAcreTagTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tankFillingAcreTagTxt.setText("lfEefyr ,dM+");

        tankFillingProductNameTxt.setTypeface(tf);
        tankFillingProductNameTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tankFillingProductNameTxt.setText("mRikn dk uke");

        tankFillingUomTxt.setTypeface(tf);
        tankFillingUomTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tankFillingUomTxt.setText("mRikn ekud");

        tankFillingQuantityTxt.setTypeface(tf);
        tankFillingQuantityTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tankFillingQuantityTxt.setText("mRikn ek=k");

       // setSubtitleLanguage();



    }

    private void addProductDynamicLabel(TextView titleTxt,
                                        TextView productNameTxt,
                                        TextView productUomTxt,
                                        TextView productQuantityTxt) {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/krdv011.ttf");

        if (titleTxt != null) {
            titleTxt.setTypeface(tf);
            titleTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            titleTxt.setText("mRikn");
        }

        productNameTxt.setTypeface(tf);
        productNameTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        productNameTxt.setText("mRikn dk uke");

        productUomTxt.setTypeface(tf);
        productUomTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        productUomTxt.setText("mRikn ekud");

        productQuantityTxt.setTypeface(tf);
        productQuantityTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        productQuantityTxt.setText("mRikn ek=k");

    }


    private TankFillingData initializeTankFillingData() {
        TankFillingData tankFillingData = new TankFillingData();
        tankFillingData.setTankFillingId(String.valueOf(System.currentTimeMillis()));
        tankFillingData.setSprayMonitoringId(data.getSprayMonitorId());
        if (sprayCropDataArrayList != null && sprayCropDataArrayList.size() > 0) {
            SprayCropData sprayCropData = sprayCropDataArrayList.get(0);
            tankFillingData.setTankFillingCropId(sprayCropData.getCropId());
        }
        return tankFillingData;
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void setDefaultData() {

        String stringCropType = data.getCrop_type();

        if (stringCropType!=null && stringCropType.equalsIgnoreCase("2")){

            actualAcreCoveredTxt1.setVisibility(View.VISIBLE);
            coveredAcreTxt1.setVisibility(View.VISIBLE);

            actualAcreCoveredTxt.setVisibility(View.GONE);
            coveredAcreTxt.setVisibility(View.GONE);

        }else {
            actualAcreCoveredTxt1.setVisibility(View.GONE);
            coveredAcreTxt1.setVisibility(View.GONE);

            actualAcreCoveredTxt.setVisibility(View.VISIBLE);
            coveredAcreTxt.setVisibility(View.VISIBLE);
        }

        setSubtitleLanguage();

        actualAcreCovered.setText(data.getActualAcreCovered());
        double addedTotalAcre = 0.0;
        for (SprayCropData sprayCropData : sprayCropDataArrayList) {
            double cropAcre = 0.0;
            try {
                cropAcre = Double.parseDouble(sprayCropData.getCropAcre());
            } catch (Exception e) {
                e.printStackTrace();
            }
            addedTotalAcre = addedTotalAcre + cropAcre;
        }
        data.setTotalAcrage(String.valueOf(addedTotalAcre));

        if (tankFillingDataArrayList != null) {
            for (TankFillingData tankFillingData : tankFillingDataArrayList) {
                addTankDetailInConfirmLayout(tankFillingData);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SprayDetailTwoActivity.this, SprayDetailOneActivity.class);
        intent.putExtra(Constents.DATA, data);
        intent.putParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA, productDataArrayList);
        intent.putParcelableArrayListExtra(Constents.SPRAY_CROP_DATA, sprayCropDataArrayList);
        intent.putParcelableArrayListExtra(Constents.SPRAY_TANK_FILLINF_DATA, tankFillingDataArrayList);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
        finish();
    }

    @Override
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

            }
            case R.id.action_next: {

                Intent intent = new Intent(SprayDetailTwoActivity.this, SprayDetailThreeActivity.class);
                intent.putExtra(Constents.DATA, data);
                intent.putParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA, productDataArrayList);
                intent.putParcelableArrayListExtra(Constents.SPRAY_CROP_DATA, sprayCropDataArrayList);
                intent.putParcelableArrayListExtra(Constents.SPRAY_TANK_FILLINF_DATA, tankFillingDataArrayList);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                finish();

                return true;
            }
            case android.R.id.home: {
                Intent intent = new Intent(SprayDetailTwoActivity.this, SprayDetailOneActivity.class);
                intent.putExtra(Constents.DATA, data);
                intent.putParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA, productDataArrayList);
                intent.putParcelableArrayListExtra(Constents.SPRAY_CROP_DATA, sprayCropDataArrayList);
                intent.putParcelableArrayListExtra(Constents.SPRAY_TANK_FILLINF_DATA, tankFillingDataArrayList);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                finish();

                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }

    }


    private void addTankDetailInConfirmLayout(TankFillingData tankFillingData) {
        if (addedTankFillingDetailLinLay.getChildCount() == 0) {

            final View confirmedProduct0 = innerInflater.inflate(R.layout.confirmed_product_item, null, false);
            TextView innerProductName0 = (TextView) confirmedProduct0.findViewById(R.id.productName);
            innerProductName0.setText("Added Tank filling Details");
            innerProductName0.setTypeface(Typeface.DEFAULT_BOLD);
            innerProductName0.setTextColor(getResources().getColor(R.color.color_primary_dark));
            TextView productUOM0 = (TextView) confirmedProduct0.findViewById(R.id.productUOM);
            productUOM0.setVisibility(View.GONE);
            TextView productQty0 = (TextView) confirmedProduct0.findViewById(R.id.productQty);
            productQty0.setVisibility(View.GONE);
            ImageButton remove0 = (ImageButton) confirmedProduct0.findViewById(R.id.remove);
            remove0.setVisibility(View.GONE);

            addedTankFillingDetailLinLay.addView(confirmedProduct0);
            addedTankFillingDetailLinLay.setVisibility(View.VISIBLE);
            String languagePrefrance = AppManager.getLanguagePrefs(SprayDetailTwoActivity.this);
            if (languagePrefrance.equals("2")) {
                addDynamicLable(innerProductName0);
            }

        }

//        Toast.makeText(SprayDetailTwoActivity.this, "Product Added", Toast.LENGTH_SHORT).show();

        final View tankFillingView = innerInflater.inflate(R.layout.tank_filling_detail_item, null, false);

        TextView tankFillingAcreTagTxt1 = (TextView) tankFillingView.findViewById(R.id.tankFillingAcreTagTxt);
        String cropType = data.getCrop_type();

        if (cropType!=null && cropType.equalsIgnoreCase("2")){
            tankFillingAcreTagTxt1.setText("Volume Consumed By Tank");

        }else {

            tankFillingAcreTagTxt1.setText("Acre Covered By Tank");
        }



        String languagePrefrance = AppManager.getLanguagePrefs(SprayDetailTwoActivity.this);
        if (languagePrefrance.equals("2")) {
            TextView tankFillingCountTagTxt = (TextView) tankFillingView.findViewById(R.id.tankFillingCountTagTxt);
            TextView machineStartTimeTxt = (TextView) tankFillingView.findViewById(R.id.machineStartTimeTxt);
            TextView machineStopTimeTxt = (TextView) tankFillingView.findViewById(R.id.machineStopTimeTxt);
            TextView tankFillingCropTagTxt = (TextView) tankFillingView.findViewById(R.id.tankFillingCropTagTxt);
            TextView tankFillingAcreTagTxt = (TextView) tankFillingView.findViewById(R.id.tankFillingAcreTagTxt);
            TextView tankFillingProductNameTxt = (TextView) tankFillingView.findViewById(R.id.tankFillingProductNameTxt);
            TextView tankFillingUomTxt = (TextView) tankFillingView.findViewById(R.id.tankFillingUomTxt);
            TextView tankFillingQuantityTxt = (TextView) tankFillingView.findViewById(R.id.tankFillingQuantityTxt);
            addDynamicLable(tankFillingCountTagTxt, machineStartTimeTxt, machineStopTimeTxt, tankFillingCropTagTxt, tankFillingAcreTagTxt, tankFillingProductNameTxt, tankFillingUomTxt, tankFillingQuantityTxt);
        }




        TextView tankFillingCountTxt = (TextView) tankFillingView.findViewById(R.id.tankFillingCountTxt);
        tankFillingCountTxt.setText(tankFillingData.getTankFillingCount());
        TextView tankFillingCropTxt = (TextView) tankFillingView.findViewById(R.id.tankFillingCropTxt);
        tankFillingCropTxt.setText(db.getCropNameById(tankFillingData.getTankFillingCropId()));
        TextView tankFillingAcreTxt = (TextView) tankFillingView.findViewById(R.id.tankFillingAcreTxt);
        tankFillingAcreTxt.setText(String.valueOf(tankFillingData.getAcreCoveredByTank()));

        TextView machineStartTimeTagTxt = (TextView) tankFillingView.findViewById(R.id.machineStartTimeTagTxt);
        machineStartTimeTagTxt.setText(String.valueOf(tankFillingData.getStartTime()));
        TextView machineStopTimeTagTxt = (TextView) tankFillingView.findViewById(R.id.machineStopTimeTagTxt);
        machineStopTimeTagTxt.setText(String.valueOf(tankFillingData.getEndTime()));

        System.out.println("tank filling id on view creation : " + tankFillingData.getTankFillingId());
        Cursor productCursor = db.getSprayedProduct(tankFillingData.getTankFillingId());

        System.out.println("(productCursor.moveToFirst()) : " + (productCursor.moveToFirst()));

        if (productCursor.moveToFirst()) {
            int i = 1;
            do {
                LinearLayout productDetailHeadingLayout = (LinearLayout) tankFillingView.findViewById(R.id.productDetailHeadingLayout);
                productDetailHeadingLayout.setVisibility(View.VISIBLE);

                String productDataId = productCursor.getString(productCursor.getColumnIndex(DBAdapter.PRODUCT_DATA_ID));
                ProductData productData = new ProductData(productDataId, db);
                if (i == 1) {
                    LinearLayout productOneLayout = (LinearLayout) tankFillingView.findViewById(R.id.productOneLayout);
                    productOneLayout.setVisibility(View.VISIBLE);
                    TextView productNameOneTxt = (TextView) tankFillingView.findViewById(R.id.productNameOneTxt);
                    productNameOneTxt.setText(productData.getProductName());
                    TextView uomNameOneTxt = (TextView) tankFillingView.findViewById(R.id.uomNameOneTxt);
                    uomNameOneTxt.setText((productData.getUomName()));
                    TextView quantityOneTxt = (TextView) tankFillingView.findViewById(R.id.quantityOneTxt);
                    quantityOneTxt.setText(String.valueOf(productData.getProductQuantity()));
                }
                if (i == 2) {
                    LinearLayout productTwoLayout = (LinearLayout) tankFillingView.findViewById(R.id.productTwoLayout);
                    productTwoLayout.setVisibility(View.VISIBLE);
                    TextView productNameTwoTxt = (TextView) tankFillingView.findViewById(R.id.productNameTwoTxt);
                    productNameTwoTxt.setText(productData.getProductName());
                    TextView uomNameTwoTxt = (TextView) tankFillingView.findViewById(R.id.uomNameTwoTxt);
                    uomNameTwoTxt.setText((productData.getUomName()));
                    TextView quantityTwoTxt = (TextView) tankFillingView.findViewById(R.id.quantityTwoTxt);
                    quantityTwoTxt.setText(String.valueOf(productData.getProductQuantity()));
                }
                if (i == 3) {
                    LinearLayout productThreeLayout = (LinearLayout) tankFillingView.findViewById(R.id.productThreeLayout);
                    productThreeLayout.setVisibility(View.VISIBLE);
                    TextView productNameThreeTxt = (TextView) tankFillingView.findViewById(R.id.productNameThreeTxt);
                    productNameThreeTxt.setText(productData.getProductName());
                    TextView uomNameThreeTxt = (TextView) tankFillingView.findViewById(R.id.uomNameThreeTxt);
                    uomNameThreeTxt.setText((productData.getUomName()));
                    TextView quantityThreeTxt = (TextView) tankFillingView.findViewById(R.id.quantityThreeTxt);
                    quantityThreeTxt.setText(String.valueOf(productData.getProductQuantity()));
                }
                if (i == 4) {
                    LinearLayout productThreeLayout = (LinearLayout) tankFillingView.findViewById(R.id.productFourLayout);
                    productThreeLayout.setVisibility(View.VISIBLE);
                    TextView productNameThreeTxt = (TextView) tankFillingView.findViewById(R.id.productNameFourTxt);
                    productNameThreeTxt.setText(productData.getProductName());
                    TextView uomNameThreeTxt = (TextView) tankFillingView.findViewById(R.id.uomNameFourTxt);
                    uomNameThreeTxt.setText((productData.getUomName()));
                    TextView quantityThreeTxt = (TextView) tankFillingView.findViewById(R.id.quantityFourTxt);
                    quantityThreeTxt.setText(String.valueOf(productData.getProductQuantity()));
                }
                if (i == 5) {
                    LinearLayout productThreeLayout = (LinearLayout) tankFillingView.findViewById(R.id.productFiveLayout);
                    productThreeLayout.setVisibility(View.VISIBLE);
                    TextView productNameThreeTxt = (TextView) tankFillingView.findViewById(R.id.productNameFiveTxt);
                    productNameThreeTxt.setText(productData.getProductName());
                    TextView uomNameThreeTxt = (TextView) tankFillingView.findViewById(R.id.uomNameFiveTxt);
                    uomNameThreeTxt.setText((productData.getUomName()));
                    TextView quantityThreeTxt = (TextView) tankFillingView.findViewById(R.id.quantityFiveTxt);
                    quantityThreeTxt.setText(String.valueOf(productData.getProductQuantity()));
                }
                i++;
            } while (productCursor.moveToNext());

        }
        productCursor.close();

        addedTankFillingDetailLinLay.addView(tankFillingView);

       cropSpinner.setText("");
        cropAcreEdt.setText("");
        machineStartTime.setText("");
        machineStopTime.setText("");

        addProductLayout.setVisibility(View.GONE);
        addProductLayoutButton.setVisibility(View.VISIBLE);
        confirmedProductLinearLayout.removeAllViews();
        confirmedProductLinearLayout.setVisibility(View.GONE);

        setSubtitleLanguage();

    }


    private boolean isValidTankFilling() {

        boolean isValid = true;

        if (!(tankFillingData.getTankFillingCropId() != null && tankFillingData.getTankFillingCropId().trim().length() > 0)) {
            Toast.makeText(SprayDetailTwoActivity.this, "Crop doesn't exist for tank filling", Toast.LENGTH_LONG).show();
            return false;
        }

      /*  if (!(tankFillingData.getStartTime() != null && tankFillingData.getStartTime().trim().length() > 0)) {
            Toast.makeText(SprayDetailTwoActivity.this,"Please select machine start time", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!(tankFillingData.getEndTime() != null && tankFillingData.getEndTime().trim().length() > 0)) {
            Toast.makeText(SprayDetailTwoActivity.this,"Please select machine stop time", Toast.LENGTH_LONG).show();
            return false;
        }*/

        if (cropAcreEdt.getText() != null && cropAcreEdt.getText().toString().trim().length() > 0) {
            tankFillingData.setAcreCoveredByTank(cropAcreEdt.getText().toString());
        } else {
            Toast.makeText(SprayDetailTwoActivity.this, "Acre covered by tank is not exist", Toast.LENGTH_LONG).show();
            return false;
        }

        Cursor productCursor = db.getSprayedProduct(tankFillingData.getTankFillingId());
        if (!productCursor.moveToFirst()) {
            Toast.makeText(SprayDetailTwoActivity.this, "Please add at least one product", Toast.LENGTH_LONG).show();
            return false;
        }
        productCursor.close();

        tankFillingData.setTankFillingCount(String.valueOf(tankFillingDataArrayList.size() + 1));

        return isValid;
    }

    private void addProductInConfirmLayout(ProductData productData, final int productCount) {
        if (confirmedProductLinearLayout.getChildCount() == 0) {

            final View confirmedProduct0 = innerInflater.inflate(R.layout.confirmed_product_item, null, false);
            TextView innerProductName0 = (TextView) confirmedProduct0.findViewById(R.id.productName);
            innerProductName0.setText("Added Products");
            innerProductName0.setTypeface(Typeface.DEFAULT_BOLD);
            innerProductName0.setTextColor(getResources().getColor(R.color.color_primary_dark));
            TextView productUOM0 = (TextView) confirmedProduct0.findViewById(R.id.productUOM);
            productUOM0.setVisibility(View.GONE);
            TextView productQty0 = (TextView) confirmedProduct0.findViewById(R.id.productQty);
            productQty0.setVisibility(View.GONE);
            ImageButton remove0 = (ImageButton) confirmedProduct0.findViewById(R.id.remove);
            remove0.setVisibility(View.GONE);

            confirmedProductLinearLayout.addView(confirmedProduct0);

            final View confirmedProduct = innerInflater.inflate(R.layout.confirmed_product_item, null, false);
            TextView innerProductName = (TextView) confirmedProduct.findViewById(R.id.productName);
            innerProductName.setText("Product Name");
            innerProductName.setTypeface(Typeface.DEFAULT_BOLD);
            TextView innerProductUOM = (TextView) confirmedProduct.findViewById(R.id.productUOM);
            innerProductUOM.setText("UOM");
            innerProductUOM.setTypeface(Typeface.DEFAULT_BOLD);
            TextView innerProductQty = (TextView) confirmedProduct.findViewById(R.id.productQty);
            innerProductQty.setText("Quantity");
            innerProductQty.setTypeface(Typeface.DEFAULT_BOLD);
            ImageButton remove = (ImageButton) confirmedProduct.findViewById(R.id.remove);
            remove.setVisibility(View.INVISIBLE);

            confirmedProductLinearLayout.addView(confirmedProduct);
            confirmedProductLinearLayout.setVisibility(View.VISIBLE);

            String languagePrefrance = AppManager.getLanguagePrefs(SprayDetailTwoActivity.this);
            if (languagePrefrance.equals("2")) {
                addProductDynamicLabel(innerProductName0, innerProductName, innerProductUOM, innerProductQty);
            }

        }

//        Toast.makeText(SprayDetailTwoActivity.this, "Product Added", Toast.LENGTH_SHORT).show();

        final String productDataId = productData.getProductDataId();

        final View confirmedProduct = innerInflater.inflate(R.layout.confirmed_product_item, null, false);
        TextView innerProductName = (TextView) confirmedProduct.findViewById(R.id.productName);
        innerProductName.setText(productData.getProductName());
        TextView innerProductUOM = (TextView) confirmedProduct.findViewById(R.id.productUOM);
        innerProductUOM.setText(productData.getUomName());
        TextView innerProductQty = (TextView) confirmedProduct.findViewById(R.id.productQty);
        innerProductQty.setText(String.valueOf(productData.getProductQuantity()));

        ImageButton remove = (ImageButton) confirmedProduct.findViewById(R.id.remove);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ViewGroup) confirmedProduct.getParent()).removeView(confirmedProduct);
                for (ProductData pData : productDataArrayList) {
                    if (pData.getProductDataId().equals(productDataId)) {
                        if (pData.delete(db)) {
                            productDataArrayList.remove(pData);
                            break;
                        }
                    }
                }
                System.out.println("Product size : " + productDataArrayList.size());
                if (productCount < 5) {
                    addProductLayoutButton.setVisibility(View.VISIBLE);
                }

            }
        });
        String productUomName = productData.getUomName();
        String productUomId = productData.getUomId();
        confirmedProductLinearLayout.addView(confirmedProduct);
        productData = new ProductData();

        System.out.println("new Product Data is created");

        productData.setUomName(productUomName);
        productData.setUomId(productUomId);
      //  productSpinner.setSelection(0, true);
//                        productUOM.setSelection(0, true);
        productQtyEdt.setText("");

        if (productCount == 5) {
            addProductLayout.setVisibility(View.GONE);
            addProductLayoutButton.setVisibility(View.GONE);
        } else {
            addProductLayout.setVisibility(View.GONE);
            addProductLayoutButton.setVisibility(View.VISIBLE);
        }
    }

    View.OnClickListener mixturePreparationListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (AppManager.isGPSenabled(SprayDetailTwoActivity.this)) {
                getLocation();

                Log.i("MakeMachine", "startCameraActivity()");
                isCaptured = true;

                String vidName = String.valueOf(System.currentTimeMillis()) + "_" + Constents.MIXTURE_PREPARATION;
//            String vidName = Constents.MIXTURE_PREPARATION;
                Map<String, String> pathMap = FolderManager.getVidDir(data.getSprayMonitorId(), Constents.MIXTURE_PREPARATION, vidName);

                highVideoPath = pathMap.get(FolderManager.HIGH_RESOLUTION_DIRECTORY);
                lowVideoPath = pathMap.get(FolderManager.LOW_RESOLUTION_DIRECTORY);

                System.out.println("videoPath " + highVideoPath);

                File file = new File(highVideoPath);
                // Uri outputFileUri = Uri.fromFile(file);

                Uri outputFileUri = FileProvider.getUriForFile(SprayDetailTwoActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        file);

                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    grantUriPermission(packageName, outputFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(intent, REQUEST_MIX_PREP_VID);
            }
        }
    };

    View.OnClickListener mixtureGalOnClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            ArrayList<VideoData> videos = new ArrayList<>();

            Cursor videoCursor = db.videoByIdAndName(data.getSprayMonitorId(), Constents.MIXTURE_PREPARATION);
            if (videoCursor.getCount() > 0) {
                videoCursor.moveToFirst();
                do {
                    VideoData videoData = new VideoData(videoCursor);
                    videos.add(videoData);
                } while (videoCursor.moveToNext());
            }
            Toast.makeText(SprayDetailTwoActivity.this, "Gallery is clicked", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(SprayDetailTwoActivity.this, VideoGalleryActivity.class);
            intent.putParcelableArrayListExtra("VIDEO", videos);
            SprayDetailTwoActivity.this.startActivity(intent);
        }
    };

    ProgressDialog pDialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;
            case -1:
//                Match request code to get the image id

                if (requestCode == REQUEST_MIX_PREP_VID) {
                    pDialog = ProgressDialog.show(SprayDetailTwoActivity.this, "Processing", "Please wait while processing video..", true);
                    MediaTranscoder.Listener listener = new MediaTranscoder.Listener() {

                        final int id = 100;
                        final NotificationManager mNotifyManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());

                        @Override
                        public void onTranscodeProgress(double progress) {
                            mixturePreparationGalImgBut.setEnabled(false);
                            mBuilder.setContentTitle("Process Video")
                                    .setContentText("Video Processing in progress")
                                    .setSmallIcon(R.drawable.ic_action_process);
                            mBuilder.setProgress(1000, (int) Math.round(progress * 1000), false);
                            mNotifyManager.notify(id, mBuilder.build());
                        }

                        @Override
                        public void onTranscodeCompleted() {
                            pDialog.cancel();
                            mixturePreparationGalImgBut.setEnabled(true);
                            mBuilder.setContentText("Video Processing complete")
                                    // Removes the progress bar
                                    .setProgress(0, 0, false);
                            mNotifyManager.notify(id, mBuilder.build());
                            Cursor videoByIdAndName = db.videoByIdAndName(data.getSprayMonitorId(), Constents.MIXTURE_PREPARATION);
                            int videoCount = videoByIdAndName.getCount() + 1;
                            VideoData idSaveVideo = new VideoData(Constents.MIXTURE_PREPARATION, lowVideoPath, "" + latitude, "" + longitude, datetimestamp, data.getSprayMonitorId(), tankFillingData.getTankFillingId(), DBAdapter.TRUE, DBAdapter.SAVE,
                                    CellID, lac, mcc, mnc);
                            idSaveVideo.save(db);
                        }

                        @Override
                        public void onTranscodeFailed(Exception exception) {
                            pDialog.cancel();
                            mixturePreparationGalImgBut.setEnabled(false);
                            mBuilder.setContentText("Video Processing complete")
                                    // Removes the progress bar
                                    .setProgress(0, 0, false);
                            mNotifyManager.notify(id, mBuilder.build());
                            Cursor videoByIdAndName = db.videoByIdAndName(data.getSprayMonitorId(), Constents.MIXTURE_PREPARATION);
                            int videoCount = videoByIdAndName.getCount() + 1;
                            VideoData idSaveVideo = new VideoData(Constents.MIXTURE_PREPARATION, highVideoPath, "" + latitude, "" + longitude, datetimestamp, data.getSprayMonitorId(), String.valueOf(videoCount), DBAdapter.FALSE, DBAdapter.SAVE,
                                    CellID, lac, mcc, mnc);
                            idSaveVideo.save(db);
                        }
                    };

                    ContentResolver resolver = getContentResolver();
                    final ParcelFileDescriptor parcelFileDescriptor;
                    try {
                        File file = new File(highVideoPath);
                        Uri outputFileUri = Uri.fromFile(file);
                        parcelFileDescriptor = resolver.openFileDescriptor(outputFileUri, "r");
                    } catch (FileNotFoundException e) {
                        Log.w("Could not open '" + highVideoPath + "'", e);
                        Toast.makeText(SprayDetailTwoActivity.this, "File not found.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    final FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

                    MediaTranscoder.getInstance().transcodeVideo(fileDescriptor, lowVideoPath,
                            MediaFormatStrategyPresets.createAndroid720pStrategy(), listener);

                }
                break;
        }
    }

    boolean isValidProduct() {
        boolean isValid = true;

        if (!(productData.getProductId() != null && productData.getProductId().trim().length() > 0)) {
            Toast.makeText(SprayDetailTwoActivity.this, "Please select product", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(productData.getUomId() != null && productData.getUomId().trim().length() > 0)) {
            Toast.makeText(SprayDetailTwoActivity.this, "Please select product uom", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (productQtyEdt.getText() != null && productQtyEdt.getText().toString().trim().length() > 0) {
            productData.setProductQuantity(productQtyEdt.getText().toString());
        } else {
            Toast.makeText(SprayDetailTwoActivity.this, "Please enter product quantity ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return isValid;
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
                            ActivityCompat.requestPermissions(SprayDetailTwoActivity.this,
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
    }

    private static final String PRODUCT_DATA_ARRAY = "product_data_array";
    private static final String SPRAY_CROP_DATA_ARRAY = "spray_crop_data_array";
    private static final String TANK_DATA_ARRAY = "tank_data_array";
    private static final String SPRAY_DATA = "spray_data";
    private static final String TANK_FILLING_DATA = "tank_filling_data";
    private static final String PRODUCT_DATA = "product_data";
    private static final String IS_CAPTURE = "is_captured";
    private static final String LOW_VIDEO_PATH = "low_video_path";
    private static final String HIGH_VIDEO_PATH = "high_video_path";

    private static final String TANK_FILLING_LAYOUT = "tank_filling_layout";
    private static final String CROP_SPINNER = "crop_spinner";
    private static final String CROP_ACRE_EDT = "crop_acre_edt";
    private static final String CONFIRM_PRODUCT_LAYOUT = "confirm_product_layout";
    private static final String ADD_PRODUCT_LAYOUT = "add_product_layout";
 //   private static final String PRODUCT_SPINNER = "product_spinner";
    private static final String PRODUCT_UOM_SPINNER = "product_uom_spinner";
    private static final String PRODUCT_QUANTITY = "product_quantity";


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i("MakeMachine", "onRestoreInstanceState()");

        productDataArrayList = savedInstanceState.getParcelableArrayList(PRODUCT_DATA_ARRAY);

        sprayCropDataArrayList = savedInstanceState.getParcelableArrayList(SPRAY_CROP_DATA_ARRAY);
        tankFillingDataArrayList = savedInstanceState.getParcelableArrayList(TANK_DATA_ARRAY);
        data = savedInstanceState.getParcelable(SPRAY_DATA);
        tankFillingData = savedInstanceState.getParcelable(TANK_FILLING_DATA);
        productData = savedInstanceState.getParcelable(PRODUCT_DATA);
        isCaptured = savedInstanceState.getBoolean(IS_CAPTURE);
        lowVideoPath = savedInstanceState.getString(LOW_VIDEO_PATH);
        highVideoPath = savedInstanceState.getString(HIGH_VIDEO_PATH);

        switch (savedInstanceState.getInt(TANK_FILLING_LAYOUT)) {
            case View.VISIBLE:
                addedTankFillingDetailLinLay.setVisibility(View.VISIBLE);
                break;
            case View.GONE:
                addedTankFillingDetailLinLay.setVisibility(View.GONE);
                break;
            default:
                addedTankFillingDetailLinLay.setVisibility(View.GONE);
        }

        cropSpinner.setSelection(savedInstanceState.getInt(CROP_SPINNER));
     //   productSpinner.setSelection(savedInstanceState.getInt(PRODUCT_SPINNER));
        productUomSpinner.setSelection(savedInstanceState.getInt(PRODUCT_UOM_SPINNER));
        cropAcreEdt.setText(savedInstanceState.getString(CROP_ACRE_EDT));
        productQtyEdt.setText(savedInstanceState.getString(PRODUCT_QUANTITY));

        switch (savedInstanceState.getInt(ADD_PRODUCT_LAYOUT)) {
            case View.VISIBLE:
                addProductLayout.setVisibility(View.VISIBLE);
                break;
            case View.GONE:
                addProductLayout.setVisibility(View.GONE);
                break;
            default:
                addProductLayout.setVisibility(View.GONE);
        }

        switch (savedInstanceState.getInt(CONFIRM_PRODUCT_LAYOUT)) {
            case View.VISIBLE:
                confirmedProductLinearLayout.setVisibility(View.VISIBLE);
                break;
            case View.GONE:
                confirmedProductLinearLayout.setVisibility(View.GONE);
                break;
            default:
                confirmedProductLinearLayout.setVisibility(View.GONE);
                break;
        }

        latitude = savedInstanceState.getDouble(Lat_Lon_CellID.LATITUDE);
        longitude = savedInstanceState.getDouble(Lat_Lon_CellID.LONGITUDE);
        datetimestamp = savedInstanceState.getString(Lat_Lon_CellID.DATE_TIME_STAMP);
        CellID = savedInstanceState.getString(Lat_Lon_CellID.CELL_ID);
        lac = savedInstanceState.getString(Lat_Lon_CellID.LAC_ID);
        mcc = savedInstanceState.getString(Lat_Lon_CellID.MCC);
        mnc = savedInstanceState.getString(Lat_Lon_CellID.MNC);

        int productCount = 0;
        for (ProductData pData : productDataArrayList) {
            if (pData.getTankFillingId().equals(tankFillingData.getTankFillingId())) {
                productCount++;
            }
        }
        if (productDataArrayList != null) {
            if (productDataArrayList.size() > 0) {
                confirmedProductLinearLayout.removeAllViews();
                for (ProductData productData : productDataArrayList) {
                    addProductInConfirmLayout(productData, productCount);
                }
            }
        }

        if (tankFillingDataArrayList != null) {
            if (tankFillingDataArrayList.size() > 0) {
                addedTankFillingDetailLinLay.removeAllViews();
                for (TankFillingData tankFillingData : tankFillingDataArrayList) {
                    addTankDetailInConfirmLayout(tankFillingData);
                }
            }
        }

        setLocation();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(PRODUCT_DATA_ARRAY, productDataArrayList);
        outState.putParcelableArrayList(SPRAY_CROP_DATA_ARRAY, sprayCropDataArrayList);
        outState.putParcelableArrayList(TANK_DATA_ARRAY, tankFillingDataArrayList);
        outState.putParcelable(SPRAY_DATA, data);
        outState.putParcelable(TANK_FILLING_DATA, tankFillingData);
        outState.putParcelable(PRODUCT_DATA, productData);
        outState.putBoolean(IS_CAPTURE, isCaptured);
        outState.putString(LOW_VIDEO_PATH, lowVideoPath);
        outState.putString(HIGH_VIDEO_PATH, highVideoPath);

        outState.putInt(TANK_FILLING_LAYOUT, addedTankFillingDetailLinLay.getVisibility());
      //  outState.putInt(CROP_SPINNER, cropSpinner.po());
      //  outState.putInt(PRODUCT_SPINNER, productSpinner.getSelectedItemPosition());
        outState.putInt(PRODUCT_UOM_SPINNER, productUomSpinner.getSelectedItemPosition());
        outState.putString(CROP_ACRE_EDT, cropAcreEdt.getText().toString());
        outState.putInt(CONFIRM_PRODUCT_LAYOUT, confirmedProductLinearLayout.getVisibility());
        outState.putInt(ADD_PRODUCT_LAYOUT, addProductLayout.getVisibility());
        outState.putString(PRODUCT_QUANTITY, productQtyEdt.getText().toString());

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
