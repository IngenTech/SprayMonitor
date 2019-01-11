package com.wrms.spraymonitor;

import android.Manifest;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.Date;
import java.util.Map;

public class DetailActivityThird extends AppCompatActivity {


    EditText amtCollected;
    Spinner amountCollectedBy;
    EditText remark;
    TextView amountCollectedTag;

//    private Toolbar toolbar;
    SprayMonitorData data;
    DBAdapter db;

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
        setContentView(R.layout.activity_detail_third);
        /*toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);                    // Setting toolbar as the ActionBar with setSupportActionBar() call*/
        mLayout = findViewById(R.id.detail_activity_second);


        amtCollected = (EditText) findViewById(R.id.amtCollectedEdt);
        amountCollectedBy = (Spinner) findViewById(R.id.amountCollectedBy);
        remark = (EditText) findViewById(R.id.remark);

        amountCollectedTag = (TextView) findViewById(R.id.amountCollectedTxt);
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
                    Intent intent = new Intent(DetailActivityThird.this, DetailActivity.class);
                    intent.putExtra(Constents.DATA, data);
                    intent.putParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA, productArray);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
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

                            /*SendingThread thread = SendingThread.getInstance();
                            thread.sendDataRequest(data, images, videos, db, DetailActivityThird.this);*/
//                                sendSubmittedData(data, images,paymentObjects, videos, db);

                                AuthenticateService.sendSprayMonitoringData(data, images,paymentObjects, videos, db, DetailActivityThird.this);

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivityThird.this);
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
                            Toast.makeText(DetailActivityThird.this, "Machine Id not selected", Toast.LENGTH_LONG).show();
                        }
                    }
                }
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
                            Toast.makeText(DetailActivityThird.this, "Actual Acre covered can not be greater than Acre to be Covered", Toast.LENGTH_SHORT).show();
                        }

                    } catch (ParseException e) {
                        Toast.makeText(DetailActivityThird.this, "Please Enter Valid Acre", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(DetailActivityThird.this, "Collected amount can not be greater than Receivable amount", Toast.LENGTH_SHORT).show();
                        }

                    } catch (ParseException e) {
                        Toast.makeText(DetailActivityThird.this, "Please Enter Valid Amount", Toast.LENGTH_SHORT).show();
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

        ArrayAdapter<String> collectorArrayAdapter = new ArrayAdapter<String>(DetailActivityThird.this, android.R.layout.simple_spinner_item, collectorStringArray); //selected item will look like a spinner set from XML
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
                            ActivityCompat.requestPermissions(DetailActivityThird.this,
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
                dialog = ProgressDialog.show(DetailActivityThird.this, "Registering",
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

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivityThird.this);
                builder.setTitle("Submit").
                        setMessage("Detail has been submitted").
                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(DetailActivityThird.this, TabedActivity.class);
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
                Intent intent = new Intent(DetailActivityThird.this, DetailActivity.class);
                intent.putExtra(Constents.DATA, data);
                intent.putParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA, productArray);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
            }
        }
    }

    private boolean isSubmitValid() {
        boolean isValid = true;

        boolean collectedAmount = (data.getAmountCollected() != null && data.getAmountCollected().trim().length() > 0);
        if (!collectedAmount) {
            Toast.makeText(DetailActivityThird.this, "Please enter collected amount", Toast.LENGTH_LONG).show();
            return false;
        }

       /* boolean collectedBy = (data.getCollectedBy() != null && data.getCollectedBy().trim().length() > 0);
        if (!collectedBy) {
            Toast.makeText(DetailActivityThird.this, "Please select, who collected the amount", Toast.LENGTH_LONG).show();
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
            Toast.makeText(DetailActivityThird.this, "Actual acre can not be greater than Acre to be covered", Toast.LENGTH_LONG).show();
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
                Toast.makeText(DetailActivityThird.this, "Please select To Be Collected By", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if (productArray.size() == 0) {
            Toast.makeText(DetailActivityThird.this, "Please add at least one product", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(DetailActivityThird.this, "Detail has been saved", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(DetailActivityThird.this, "Could not save the form", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(DetailActivityThird.this, "Farmer Name Required", Toast.LENGTH_LONG).show();
                    }
                }
                return true;
            }
            case android.R.id.home: {
                if (isValid()) {
                    Intent intent = new Intent(DetailActivityThird.this, DetailActivitySecond.class);
                    intent.putExtra(Constents.DATA, data);
                    intent.putParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA, productArray);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                }
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }



}

