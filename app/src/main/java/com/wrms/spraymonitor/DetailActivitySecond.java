package com.wrms.spraymonitor;

import android.Manifest;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wrms.spraymonitor.app.Constents;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.background.AuthenticateService;
import com.wrms.spraymonitor.background.Lat_Lon_CellID;
import com.wrms.spraymonitor.dataobject.ImageData;
import com.wrms.spraymonitor.dataobject.PaymentObject;
import com.wrms.spraymonitor.dataobject.ProductData;
import com.wrms.spraymonitor.dataobject.SprayMonitorData;
import com.wrms.spraymonitor.dataobject.VideoData;
import com.wrms.spraymonitor.utils.FolderManager;

import net.ypresto.androidtranscoder.MediaTranscoder;
import net.ypresto.androidtranscoder.format.MediaFormatStrategyPresets;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;

public class DetailActivitySecond extends AppCompatActivity {

    public static final int REQUEST_TANK_FILING_VID = 10;
    public static final int REQUEST_SPRAY_VID = 11;
    public static final int REQUEST_CROP_IMG_AFTER_SPRAY = 12;
    public static final int REQUEST_MIX_PREP_VID = 114;

    EditText amtCollected;
    Spinner amountCollectedBy;
    EditText remark;
    ImageButton mixturePreprationVid;
    ImageButton mixturePreprationGal;
    TextView amountCollectedTag;

    private Toolbar toolbar;
    SprayMonitorData data;
    DBAdapter db;

    String lowImagePath;
    String highImagePath;

    String lowVideoPath;
    String highVideoPath;

    Spinner productName;
    EditText productQty;
    Spinner productUOM;
    Button addProduct;
    Button addProductButton;
    LayoutInflater innerInflater;
    LinearLayout confirmedProductLayout;
    LinearLayout addProductLayout;
    ArrayList<ProductData> productArray = new ArrayList<>();
    ProductData productData = new ProductData();


    TextView totalAcre;
    TextView totalAcreOfCrop;
    TextView acreCovered;
    EditText actualAcreCovered;

    EditText balanceEdt;
    EditText amountReceivableEdt;

    String tankfilingDateTime;
    String paymentIdString;

    ProgressDialog pDialog;


    public static boolean isCaptured = false; //to check that we have returned from camera intent not just orientation has changed

    private View mLayout;
    public static final String TAG = "Detail activity tag";
    private static final int REQUEST_MICRO_PHONE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_second);
        /*toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);                    // Setting toolbar as the ActionBar with setSupportActionBar() call*/
        mLayout = findViewById(R.id.detail_activity_second);


        amtCollected = (EditText) findViewById(R.id.amtCollectedEdt);
        amountCollectedBy = (Spinner) findViewById(R.id.amountCollectedBy);
        remark = (EditText) findViewById(R.id.remark);

        mixturePreprationVid = (ImageButton) findViewById(R.id.mixturePreprationVid);
        mixturePreprationGal = (ImageButton) findViewById(R.id.mixturePreprationGal);
        amountCollectedTag = (TextView) findViewById(R.id.amountCollectedTxt);

        productName = (Spinner) findViewById(R.id.productName);
        productQty = (EditText) findViewById(R.id.productQty);
        productUOM = (Spinner) findViewById(R.id.productUOM);
        addProduct = (Button) findViewById(R.id.addProduct);
        addProductButton = (Button) findViewById(R.id.addProductButton);
        innerInflater = LayoutInflater.from(this);
        confirmedProductLayout = (LinearLayout) findViewById(R.id.confirmedProductLayout);
        addProductLayout = (LinearLayout) findViewById(R.id.addProductLayout);

        totalAcre = (TextView) findViewById(R.id.totalAcre);
        totalAcreOfCrop = (TextView) findViewById(R.id.totalAcreOfCrop);
        acreCovered = (TextView) findViewById(R.id.acreCovered);
        actualAcreCovered = (EditText) findViewById(R.id.actualAcreCovered);
        amountReceivableEdt = (EditText) findViewById(R.id.amountReceivableEdt);
        balanceEdt = (EditText) findViewById(R.id.balanceEdt);

        paymentIdString = PaymentObject.createPaymentId();

        getMicroPhone(mLayout);

        data = (SprayMonitorData) getIntent().getParcelableExtra(Constents.DATA);
        productArray = getIntent().getParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA);

        if (productArray == null) {
            productArray = new ArrayList<>();
        }

        db = new DBAdapter(getApplicationContext());
        db.open();


        findViewById(R.id.privious).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValid()) {
                    Intent intent = new Intent(DetailActivitySecond.this, DetailActivity.class);
                    intent.putExtra(Constents.DATA, data);
                    intent.putParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA, productArray);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                }
            }
        });

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValid()) {
                    Intent intent = new Intent(DetailActivitySecond.this, DetailActivityThird.class);
                    intent.putExtra(Constents.DATA, data);
                    intent.putParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA, productArray);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                }


                /*if (isValid()) {
                    if (isSubmitValid()) {
                        if (data.getMachineId() != null && data.getMachineId().trim().length() > 0) {

                            data.setSendingStatus(DBAdapter.SUBMIT);

                            if (data.save(db)) {

                                ContentValues values = new ContentValues();
                                values.put(DBAdapter.SPRAY_MONITORING_ID,data.getSprayMonitorId());
                                values.put(DBAdapter.AMOUNT_COLLECTED,data.getAmountCollected());
                                values.put(DBAdapter.BALANCE_AMOUNT,data.getBalanceAmount());
                                Date date = new Date();
                                String dateString = Constents.sdf.format(date);
                                values.put(DBAdapter.CREATED_DATE_TIME,dateString);
                                values.put(DBAdapter.SENDING_STATUS,DBAdapter.SUBMIT);
                                values.put(DBAdapter.PAYMENT_ID,paymentIdString);
                                db.db.insert(DBAdapter.TABLE_PAYMENT,null,values);

                        *//*Change corresponding video and images status in to submit*//*
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
                                    paymentObject.setSendingStatus(DBAdapter.SUBMIT);
                                    paymentObject.save(db);
                                }
                                AuthenticateService.sendSprayMonitoringData(data, images,paymentObjects, videos, db, DetailActivitySecond.this);

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivitySecond.this);
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
                            Toast.makeText(DetailActivitySecond.this, "Machine Id not selected", Toast.LENGTH_LONG).show();
                        }
                    }
                }*/
            }
        });


        actualAcreCovered.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    try {

                        double actualAcreDouble = Double.parseDouble(s.toString());
                        double acreCoveredDouble = 0.0;

                        if (data.getAcrageCovered() != null && (!data.getAcrageCovered().isEmpty())) {
                            acreCoveredDouble = Double.parseDouble(data.getAcrageCovered());
                        }

                        if (actualAcreDouble <= acreCoveredDouble) {

                            double amountReceivableDouble = actualAcreDouble * 75;
                            amountReceivableEdt.setText(String.valueOf(amountReceivableDouble));
                            data.setAmountReceivable(String.valueOf(amountReceivableDouble));

                        } else {
                            Toast.makeText(DetailActivitySecond.this, "Actual Acre covered can not be greater than Acre to be Covered", Toast.LENGTH_SHORT).show();
                        }

                    } catch (ParseException e) {
                        Toast.makeText(DetailActivitySecond.this, "Please Enter Valid Acre", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        amtCollected.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    try {

                        double amtCollectedDouble = Double.parseDouble(s.toString());

                        double amtReceivableDouble = 0.0;
                        if (data.getAmountReceivable() != null && (!data.getAmountReceivable().isEmpty())) {
                            amtReceivableDouble = Double.parseDouble(data.getAmountReceivable());
                        }

                        if (amtCollectedDouble <= amtReceivableDouble) {

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
                            Toast.makeText(DetailActivitySecond.this, "Collected amount can not be greater than Receivable amount", Toast.LENGTH_SHORT).show();
                        }

                    } catch (ParseException e) {
                        Toast.makeText(DetailActivitySecond.this, "Please Enter Valid Amount", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productArray.size() < 3) {
                    addProductLayout.setVisibility(View.VISIBLE);
                    addProductButton.setVisibility(View.GONE);
                }else{
                    Toast.makeText(DetailActivitySecond.this,"You have added three products",Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Cursor getAllProduct = db.getAllProduct();
        final int allProductCount = getAllProduct.getCount();
        String[] productStringArray = new String[allProductCount + 1];
        productStringArray[0] = "Select Product";
        if (allProductCount > 0) {
            getAllProduct.moveToFirst();
            for (int i = 1; i <= allProductCount; i++) {
                productStringArray[i] = getAllProduct.getString(getAllProduct.getColumnIndex(DBAdapter.PRODUCT));
                getAllProduct.moveToNext();
            }
        }

        ArrayAdapter<String> productArrayAdapter = new ArrayAdapter<String>(DetailActivitySecond.this, android.R.layout.simple_spinner_item, productStringArray); //selected item will look like a spinner set from XML
        productArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productName.setAdapter(productArrayAdapter);
        productName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String productId = "-1";
                System.out.println("Inside Product selection " + position);
                if (position > 0) {
                    getAllProduct.moveToPosition(position - 1);
                    productId = getAllProduct.getString(getAllProduct.getColumnIndex(DBAdapter.PRODUCT_ID));
                    String productName = getAllProduct.getString(getAllProduct.getColumnIndex(DBAdapter.PRODUCT));
                    productData.setProductId(productId);
                    productData.setProductName(productName);
                }
                data.setProductName(productId);
//                setUom(productId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Cursor uomCursor = db.getUom();
        System.out.println("uomCursor.getPosition() : " + uomCursor.getPosition());

        final ArrayList<String> uomNameArray = new ArrayList<>();
        if (uomCursor.moveToFirst()) {
            do {
                String materialString = uomCursor.getString(uomCursor.getColumnIndex(DBAdapter.MEASURING_UNIT));
                uomNameArray.add(materialString);
            } while (uomCursor.moveToNext());
        }
        ArrayAdapter<String> material_adapter = new ArrayAdapter<String>(DetailActivitySecond.this,
                android.R.layout.simple_spinner_item, uomNameArray);

        material_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productUOM.setAdapter(material_adapter);
        productUOM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("OnItemSelected called with position : " + position);
                uomCursor.moveToPosition(position);
                String uomId = uomCursor.getString(uomCursor.getColumnIndex(DBAdapter.MEASURING_UNIT_ID));
                String uomName = uomCursor.getString(uomCursor.getColumnIndex(DBAdapter.MEASURING_UNIT));
                productData.setUomId(uomId);
                productData.setUomName(uomName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidProduct()) {

                    if (productArray.size() < 3) {

                        if (confirmedProductLayout.getChildCount() == 0) {

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

                            confirmedProductLayout.addView(confirmedProduct0);

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

                            confirmedProductLayout.addView(confirmedProduct);
                        }

                        final String productDataId = String.valueOf(System.currentTimeMillis());
                        productData.setProductDataId(productDataId);
                        productData.setTankFillingId(data.getSprayMonitorId());
                        productData.save(db);
                        productArray.add(productData);

                        Toast.makeText(DetailActivitySecond.this, "Product Added", Toast.LENGTH_SHORT).show();

//                    sno, materialTypeId, materialAmount, remove

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
                                for (ProductData pData : productArray) {
                                    if (pData.getProductDataId().equals(productDataId)) {
                                        if (pData.delete(db)) {
                                            productArray.remove(pData);
                                            break;
                                        }
                                    }
                                }
                                System.out.println("Product size : "+productArray.size());
                                if(productArray.size()<3){
                                    addProductButton.setVisibility(View.VISIBLE);
                                }

                            }
                        });
                        String productUomName = productData.getUomName();
                        String productUomId = productData.getUomId();
                        confirmedProductLayout.addView(confirmedProduct);
                        productData = new ProductData();

                        System.out.println("new Product Data is created");

                        productData.setUomName(productUomName);
                        productData.setUomId(productUomId);
                        productName.setSelection(0, true);
//                        productUOM.setSelection(0, true);
                        productQty.setText("");

                        if(productArray.size()==3){
                            addProductLayout.setVisibility(View.GONE);
                            addProductButton.setVisibility(View.GONE);
                        }else{
                            addProductLayout.setVisibility(View.GONE);
                            addProductButton.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Toast.makeText(DetailActivitySecond.this, "Three products has been added", Toast.LENGTH_SHORT).show();
                    }
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

        ArrayAdapter<String> collectorArrayAdapter = new ArrayAdapter<String>(DetailActivitySecond.this, android.R.layout.simple_spinner_item, collectorStringArray); //selected item will look like a spinner set from XML
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

        mixturePreprationVid.setOnClickListener(mixturePreparationListner);
        mixturePreprationGal.setOnClickListener(mixtureGalOnClickListner);

        if (data != null) {
            setDefaultData(data);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isValid();
        if(data!=null){
            if(data.getFarmerContact()!=null && data.getFarmerContact().trim().length()>0) {
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

    boolean isValidProduct() {
        boolean isValid = true;

        if (!(productData.getProductId() != null && productData.getProductId().trim().length() > 0)) {
            Toast.makeText(DetailActivitySecond.this, "Please select product", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(productData.getUomId() != null && productData.getUomId().trim().length() > 0)) {
            Toast.makeText(DetailActivitySecond.this, "Please select product uom", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (productQty.getText() != null && productQty.getText().toString().trim().length() > 0) {
            productData.setProductQuantity(productQty.getText().toString());
        } else {
            Toast.makeText(DetailActivitySecond.this, "Please enter product quantity ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return isValid;
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
                            ActivityCompat.requestPermissions(DetailActivitySecond.this,
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

        if (data.getProductName() != "-1") {
            Cursor getAllProduct = db.getAllProduct();
            int productCount = getAllProduct.getCount();
            System.out.println("productId : " + data.getProductName() + " productCount : " + productCount);
            if (productCount > 0 && (!data.getCrop().equals("-1"))) {
                getAllProduct.moveToFirst();
                for (int d = 1; d <= productCount; d++) {
                    if (data.getProductName().trim().equals(getAllProduct.getString(getAllProduct.getColumnIndex(DBAdapter.PRODUCT_ID)))) {
                        productName.setSelection(d, true);
                        break;
                    }
                    getAllProduct.moveToNext();
                }
            }
        }

        if (data.getProductQty() != null && data.getProductQty().length() > 0) {
            productQty.setText(data.getProductQty());
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




        if (productArray != null && productArray.size() > 0) {
            if (confirmedProductLayout.getChildCount() == 0) {

                final View confirmedProduct0 = innerInflater.inflate(R.layout.confirmed_product_item, null, false);
                TextView ProductName0 = (TextView) confirmedProduct0.findViewById(R.id.productName);
                ProductName0.setText("Added Product");
                ProductName0.setTypeface(Typeface.DEFAULT_BOLD);
                ProductName0.setTextColor(getResources().getColor(R.color.color_primary_dark));
                TextView productUOM0 = (TextView) confirmedProduct0.findViewById(R.id.productUOM);
                productUOM0.setVisibility(View.GONE);
                TextView productQty0 = (TextView) confirmedProduct0.findViewById(R.id.productQty);
                productQty0.setVisibility(View.GONE);
                ImageButton remove0 = (ImageButton) confirmedProduct0.findViewById(R.id.remove);
                remove0.setVisibility(View.GONE);

                confirmedProductLayout.addView(confirmedProduct0);

                final View confirmedProduct = innerInflater.inflate(R.layout.confirmed_product_item, null, false);
                TextView productName = (TextView) confirmedProduct.findViewById(R.id.productName);
                productName.setText("Product Name");
                productName.setTypeface(Typeface.DEFAULT_BOLD);
                TextView productUOM = (TextView) confirmedProduct.findViewById(R.id.productUOM);
                productUOM.setText("UOM");
                productUOM.setTypeface(Typeface.DEFAULT_BOLD);
                TextView productQty = (TextView) confirmedProduct.findViewById(R.id.productQty);
                productQty.setText("Quantity");
                productQty.setTypeface(Typeface.DEFAULT_BOLD);
                ImageButton remove = (ImageButton) confirmedProduct.findViewById(R.id.remove);
                remove.setVisibility(View.INVISIBLE);

                confirmedProductLayout.addView(confirmedProduct);

                for (ProductData productData1 : productArray) {
                    final String productDataId = productData1.getProductDataId();

                    final View innerConfirmedProduct = innerInflater.inflate(R.layout.confirmed_product_item, null, false);
                    TextView innerProductName = (TextView) innerConfirmedProduct.findViewById(R.id.productName);
                    innerProductName.setText(productData1.getProductName());
                    TextView innerProductUOM = (TextView) innerConfirmedProduct.findViewById(R.id.productUOM);
                    innerProductUOM.setText(productData1.getUomName());
                    TextView innerProductQty = (TextView) innerConfirmedProduct.findViewById(R.id.productQty);
                    innerProductQty.setText(String.valueOf(productData1.getProductQuantity()));

                    ImageButton innerRemove = (ImageButton) innerConfirmedProduct.findViewById(R.id.remove);
                    innerRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((ViewGroup) innerConfirmedProduct.getParent()).removeView(innerConfirmedProduct);
                            for (ProductData pData : productArray) {
                                if (pData.getProductDataId().equals(productDataId)) {
                                    if (pData.delete(db)) {
                                        productArray.remove(pData);
                                        break;
                                    }
                                }
                                System.out.println("Product size : "+productArray.size());
                                if(productArray.size()<3){
                                    addProductButton.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
                    confirmedProductLayout.addView(innerConfirmedProduct);
                }
            }
        }

        if (data.getTotalAcrage() != null && (!data.getTotalAcrage().isEmpty())) {
            totalAcre.setText(data.getTotalAcrage());
        }
        if (data.getTotalAcreOfCrop() != null && (!data.getTotalAcreOfCrop().isEmpty())) {
            totalAcreOfCrop.setText(data.getTotalAcreOfCrop());
        }
        if (data.getAcrageCovered() != null && (!data.getAcrageCovered().isEmpty())) {
            acreCovered.setText(data.getAcrageCovered());
        }
        if (data.getActualAcreCovered() != null && (!data.getActualAcreCovered().isEmpty())) {
            actualAcreCovered.setText(data.getActualAcreCovered());
        }
    }


    ProgressDialog dialog;
    private void sendSubmittedData(final SprayMonitorData data, final ArrayList<ImageData> images,final ArrayList<PaymentObject> paymentObjects, final ArrayList<VideoData> videos, final DBAdapter db) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                dialog = ProgressDialog.show(DetailActivitySecond.this, "Registering",
                        "Please wait...", true);
            }

            @Override
            protected String doInBackground(Void... voids) {
                AuthenticateService.sendSprayMonitoringData(data, images,paymentObjects, videos, db, getApplicationContext());
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                dialog.cancel();
//                Form details has been submitted.
//                It may take some time to send it to server.
                Toast.makeText(getApplicationContext(), "Detail has been submitted", Toast.LENGTH_LONG).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivitySecond.this);
                builder.setTitle("Submit").
                        setMessage("Detail has been submitted").
                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(DetailActivitySecond.this, TabedActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }).show();

            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        if (isValid()) {
            if (isValid()) {
                Intent intent = new Intent(DetailActivitySecond.this, DetailActivity.class);
                intent.putExtra(Constents.DATA, data);
                intent.putParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA, productArray);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
            }
        }
    }

    private boolean isSubmitValid() {
        boolean isValid = true;

        data.setProductQty(productQty.getText().toString());

        boolean collectedAmount = (data.getAmountCollected() != null && data.getAmountCollected().trim().length() > 0);
        if (!collectedAmount) {
            Toast.makeText(DetailActivitySecond.this, "Please enter collected amount", Toast.LENGTH_LONG).show();
            return false;
        }

       /* boolean collectedBy = (data.getCollectedBy() != null && data.getCollectedBy().trim().length() > 0);
        if (!collectedBy) {
            Toast.makeText(DetailActivitySecond.this, "Please select, who collected the amount", Toast.LENGTH_LONG).show();
            return false;
        }*/

        double actualAcraCovered = 0.0;
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
            Toast.makeText(DetailActivitySecond.this, "Actual acre can not be greater than Acre to be covered", Toast.LENGTH_LONG).show();
            return false;
        }

        double balanceAmount = 0.0;
        if(data.getBalanceAmount()!=null){
            try{
                balanceAmount = Double.parseDouble(data.getBalanceAmount());
            }catch (NumberFormatException e){e.printStackTrace();}
        }

        if(balanceAmount>0.0){
            if(data.getCollectedBy() == null || data.getCollectedBy().trim().length()<=0){
                Toast.makeText(DetailActivitySecond.this, "Please select To Be Collected By", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if (productArray.size() == 0) {
            Toast.makeText(DetailActivitySecond.this, "Please add at least one product", Toast.LENGTH_LONG).show();
            return false;
        }

        return isValid;
    }

    private boolean isValid() {
        boolean isValid = true;
        /*if(totalAcre.getText().toString()!=null && totalAcre.getText().toString().trim().length()>0) {
            data.setTotalAcrage(totalAcre.getText().toString());
        }else{
            Toast.makeText(this,"Please Enter total Acre",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(acreCovered.getText().toString()!=null && acreCovered.getText().toString().trim().length()>0) {
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


    View.OnClickListener mixturePreparationListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Log.i("MakeMachine", "startCameraActivity()");
            isCaptured = true;

            String vidName = String.valueOf(System.currentTimeMillis()) + "_" + Constents.MIXTURE_PREPARATION;
//            String vidName = Constents.MIXTURE_PREPARATION;
            Map<String, String> pathMap = FolderManager.getVidDir(data.getSprayMonitorId(), Constents.MIXTURE_PREPARATION, vidName);

            highVideoPath = pathMap.get(FolderManager.HIGH_RESOLUTION_DIRECTORY);
            lowVideoPath = pathMap.get(FolderManager.LOW_RESOLUTION_DIRECTORY);

            System.out.println("videoPath " + highVideoPath);

            File file = new File(highVideoPath);
            Uri outputFileUri = Uri.fromFile(file);

            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, REQUEST_MIX_PREP_VID);


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
            Toast.makeText(DetailActivitySecond.this, "Gallery is clicked", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DetailActivitySecond.this, VideoGalleryActivity.class);
            intent.putParcelableArrayListExtra("VIDEO", videos);
            DetailActivitySecond.this.startActivity(intent);
        }
    };


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
                    pDialog = ProgressDialog.show(DetailActivitySecond.this, "Processing", "Please wait while processing video..", true);
                    MediaTranscoder.Listener listener = new MediaTranscoder.Listener() {

                        final int id = 100;
                        final NotificationManager mNotifyManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());

                        @Override
                        public void onTranscodeProgress(double progress) {
                            mixturePreprationGal.setEnabled(false);
                            mBuilder.setContentTitle("Process Video")
                                    .setContentText("Video Processing in progress")
                                    .setSmallIcon(R.drawable.ic_action_process);
                            mBuilder.setProgress(1000, (int) Math.round(progress * 1000), false);
                            mNotifyManager.notify(id, mBuilder.build());
                        }

                        @Override
                        public void onTranscodeCompleted() {
                            pDialog.cancel();
                            mixturePreprationGal.setEnabled(true);
                            mBuilder.setContentText("Video Processing complete")
                                    // Removes the progress bar
                                    .setProgress(0, 0, false);
                            mNotifyManager.notify(id, mBuilder.build());
                            Cursor videoByIdAndName = db.videoByIdAndName(data.getSprayMonitorId(), Constents.MIXTURE_PREPARATION);
                            int videoCount = videoByIdAndName.getCount() + 1;
                            VideoData idSaveVideo = new VideoData(Constents.MIXTURE_PREPARATION, lowVideoPath, "" + Lat_Lon_CellID.lat, "" + Lat_Lon_CellID.lon, Lat_Lon_CellID.datetimestamp, data.getSprayMonitorId(), String.valueOf(videoCount), DBAdapter.TRUE, DBAdapter.SAVE,
                                    Lat_Lon_CellID.CellID, Lat_Lon_CellID.LAC, Lat_Lon_CellID.mcc, Lat_Lon_CellID.mnc);
                            idSaveVideo.save(db);
                        }

                        @Override
                        public void onTranscodeFailed(Exception exception) {
                            pDialog.cancel();
                            mixturePreprationGal.setEnabled(false);
                            mBuilder.setContentText("Video Processing complete")
                                    // Removes the progress bar
                                    .setProgress(0, 0, false);
                            mNotifyManager.notify(id, mBuilder.build());
                            Cursor videoByIdAndName = db.videoByIdAndName(data.getSprayMonitorId(), Constents.MIXTURE_PREPARATION);
                            int videoCount = videoByIdAndName.getCount() + 1;
                            VideoData idSaveVideo = new VideoData(Constents.MIXTURE_PREPARATION, highVideoPath, "" + Lat_Lon_CellID.lat, "" + Lat_Lon_CellID.lon, Lat_Lon_CellID.datetimestamp, data.getSprayMonitorId(), String.valueOf(videoCount), DBAdapter.FALSE, DBAdapter.SAVE,
                                    Lat_Lon_CellID.CellID, Lat_Lon_CellID.LAC, Lat_Lon_CellID.mcc, Lat_Lon_CellID.mnc);
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
                        Toast.makeText(DetailActivitySecond.this, "File not found.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    final FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

                    MediaTranscoder.getInstance().transcodeVideo(fileDescriptor, lowVideoPath, MediaFormatStrategyPresets.createAndroid720pStrategy(), listener);

                }
                break;
        }
    }

    View.OnClickListener cropAfterSprayGalOnClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            ArrayList<ImageData> images = new ArrayList<>();

            Cursor imageCursor = db.getImageByFormIdAndName(data.getSprayMonitorId(), Constents.CROP_AFTER_SPRAY_DIR);
            if (imageCursor.getCount() > 0) {
                imageCursor.moveToFirst();
                do {
                    ImageData imageData = new ImageData(imageCursor);
                    images.add(imageData);
                } while (imageCursor.moveToNext());
            }

            Toast.makeText(DetailActivitySecond.this, "Gallary is clicked", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DetailActivitySecond.this, GalleryActivity.class);
            intent.putParcelableArrayListExtra("IMAGE", images);
            DetailActivitySecond.this.startActivity(intent);
        }
    };

    View.OnClickListener cropAfterSprayImgListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i("MakeMachine", "startCameraActivity()");
            isCaptured = true;
            String imgName = String.valueOf(System.currentTimeMillis()) + "_" + Constents.CROP_AFTER_SPRAY_DIR;
            Map<String, String> pathMap = FolderManager.getImgDir(data.getSprayMonitorId(), Constents.CROP_AFTER_SPRAY_DIR, imgName);

            highImagePath = pathMap.get(FolderManager.HIGH_RESOLUTION_DIRECTORY);
            lowImagePath = pathMap.get(FolderManager.LOW_RESOLUTION_DIRECTORY);

            System.out.println("imagePath " + highImagePath);

            File file = new File(highImagePath);
            Uri outputFileUri = Uri.fromFile(file);

            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, REQUEST_CROP_IMG_AFTER_SPRAY);

        }
    };

    private View.OnClickListener addTankFillingNoL = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //get the existing tank filling number
//            String fillingCount = tankFillingNo.getText().toString();
            String fillingCount = "1";

            //check wheather video has been created for existing number or not
            Cursor filingCountCursor = db.checkVideoForTankfilling(data.getSprayMonitorId(), fillingCount, Constents.TANK_FILLING_VIDEO);
            if (!(filingCountCursor.getCount() > 0)) {
                Toast.makeText(DetailActivitySecond.this, "Tank filing Video is not recorded for current Tank filing number", Toast.LENGTH_LONG).show();
                return;
            }
            Cursor sprayCountCursor = db.checkVideoForTankfilling(data.getSprayMonitorId(), fillingCount, Constents.SPRAY_VIDEO);
            if (!(sprayCountCursor.getCount() > 0)) {
                Toast.makeText(DetailActivitySecond.this, "Spray Video is not recorded for current Tank filing number", Toast.LENGTH_LONG).show();
                return;
            }

            int count = Integer.valueOf(fillingCount);

//            tankFillingNo.setText(String.valueOf(count + 1));
//            data.setTankFillingNo(String.valueOf(count + 1));
            /*data.setTankfillingDateTime(Constents.sdf.format(new Date()));*/
        }
    };

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
                if (isValid()) {
                    data.setSendingStatus(DBAdapter.SAVE);
                    if (data.getFarmerName() != null && data.getFarmerName().trim().length() > 0) {
                        if (data.save(db)) {
                            Toast.makeText(DetailActivitySecond.this, "Detail has been saved", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(DetailActivitySecond.this, "Could not save the form", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(DetailActivitySecond.this, "Farmer Name Required", Toast.LENGTH_LONG).show();
                    }
                }
                return true;
            }
            case android.R.id.home: {
                if (isValid()) {
                    Intent intent = new Intent(DetailActivitySecond.this, DetailActivity.class);
                    intent.putExtra(Constents.DATA, data);
                    intent.putParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA, productArray);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                }
                return true;
            }

            case R.id.action_next:{
                if (isValid()) {
                    Intent intent = new Intent(DetailActivitySecond.this, DetailActivityThird.class);
                    intent.putExtra(Constents.DATA, data);
                    intent.putParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA, productArray);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                }
                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }



}
