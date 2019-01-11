package com.wrms.spraymonitor;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wrms.spraymonitor.app.Constents;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.background.Lat_Lon_CellID;
import com.wrms.spraymonitor.dataobject.Credential;
import com.wrms.spraymonitor.dataobject.FarmerData;
import com.wrms.spraymonitor.dataobject.ImageData;
import com.wrms.spraymonitor.dataobject.ProductData;
import com.wrms.spraymonitor.dataobject.SprayMonitorData;
import com.wrms.spraymonitor.utils.FolderManager;
import com.wrms.spraymonitor.utils.ResizeBitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

//    private Toolbar toolbar;
    AutoCompleteTextView machineId;
    TextView farmerDetail,farmerRegisteredStatus;
    AutoCompleteTextView farmerContact;

    ImageButton machineImgButton;
    ImageButton machineGalButton;
    ImageView machineImageView;
    ImageButton farmerWithProductImg;
    ImageButton farmerWithProductGal;
    ImageView farmerWithProductImageView;

    Spinner crop;
    EditText totalAcre;

    EditText acreCovered;
    EditText totalAcreOfCrop;


    ArrayList<ProductData> productArray = new ArrayList<>();

    DBAdapter db;

    SprayMonitorData data;
    FarmerData farmerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_main);
/*

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
*/

        db = new DBAdapter(DetailActivity.this);
        db.open();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent intent = new Intent(DetailActivity.this, RegisterFarmerActivity.class);
                intent.putExtra(Constents.DATA, data);
                startActivity(intent);
            }
        });


        machineId = (AutoCompleteTextView) findViewById(R.id.machineIdACT);
        farmerDetail = (TextView) findViewById(R.id.farmerDetail);
        farmerContact = (AutoCompleteTextView) findViewById(R.id.farmerContact);
        crop = (Spinner) findViewById(R.id.crop);
        totalAcre = (EditText) findViewById(R.id.totalAcre);
        acreCovered = (EditText) findViewById(R.id.acreCovered);

        machineImgButton = (ImageButton) findViewById(R.id.machineImgButton);
        machineGalButton = (ImageButton) findViewById(R.id.machineGalButton);
        machineImageView = (ImageView)findViewById(R.id.machineImageView);

        farmerWithProductImg = (ImageButton) findViewById(R.id.farmerWithProductImgButton);
        farmerWithProductGal = (ImageButton) findViewById(R.id.farmerWithProductGalButton);
        farmerWithProductImageView = (ImageView) findViewById(R.id.farmerWithProductImageView);

        farmerRegisteredStatus = (TextView)findViewById(R.id.farmerRegisteredStatus);
        totalAcreOfCrop = (EditText)findViewById(R.id.totalAcreOfCrop);

        machineImgButton.setOnClickListener(machineImagListner);
        machineGalButton.setOnClickListener(machineImageGalOnClickListner);

        farmerWithProductImg.setOnClickListener(farmerProductImagListner);
        farmerWithProductGal.setOnClickListener(farmerProductGalOnClickListner);

        data = getIntent().getParcelableExtra(Constents.DATA);
        productArray = getIntent().getParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA);
        farmerData = getIntent().getParcelableExtra(Constents.FARMER_DATA);

        if (data == null) {
            Credential credential = new Credential();
            Cursor credentialCursor = db.getCredential();
            if (credentialCursor.getCount() > 0) {
                credentialCursor.moveToFirst();
                credential = new Credential(credentialCursor);
            }
            credentialCursor.close();
            data = new SprayMonitorData();
            String id = credential.getImei()+"_"+String.valueOf(System.currentTimeMillis());
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

        if (productArray == null) {
            productArray = new ArrayList<>();
        }

        //After instance farmer registration

        if (farmerData != null) {
            data.setFarmerName(farmerData.getFirstName() + " " + farmerData.getLastName());
            data.setFarmerId(farmerData.getFarmerId(), db);
            data.setFarmerContact(farmerData.getFarmerContact());
            data.setState(farmerData.getStateId());
            data.setDistrict(farmerData.getDistrictId());
            data.setTehsil(farmerData.getTehsilId());
            data.setVillage(farmerData.getVillageId(), db);
            data.setCrop(farmerData.getCropId());
            farmerRegisteredStatus.setText(farmerData.getSendingStatus());
            if(farmerData.getSendingStatus().equals(DBAdapter.SAVE)){
                farmerRegisteredStatus.setTextColor(getResources().getColor(R.color.fail_status));
                farmerRegisteredStatus.setText("UN-REGISTERED");
            }else if(farmerData.getSendingStatus().equals(DBAdapter.SUBMIT)){
                farmerRegisteredStatus.setTextColor(getResources().getColor(R.color.submit_status));
                farmerRegisteredStatus.setText("REGISTERED");
            }else{
                farmerRegisteredStatus.setTextColor(getResources().getColor(R.color.save_status));
                farmerRegisteredStatus.setText("REGISTERED");
            }
            setDefaultData(data);
        }


        final Cursor allMachine = db.getAllMachine();
        final int allMachineCount = allMachine.getCount();
        String[] machineStringArray = new String[allMachineCount + 1];
        machineStringArray[0] = "Select One";
        if (allMachineCount > 0) {
            allMachine.moveToFirst();
            for (int i = 1; i <= allMachineCount; i++) {
                machineStringArray[i] = allMachine.getString(allMachine.getColumnIndex(DBAdapter.MACHINE));
                allMachine.moveToNext();
            }
        }

        ArrayAdapter<String> machineArrayAdapter = new ArrayAdapter<String>(DetailActivity.this, android.R.layout.simple_spinner_item, machineStringArray); //selected item will look like a spinner set from XML
        machineArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        machineId.setAdapter(machineArrayAdapter);

        machineId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String machineId = "-1";
                System.out.println("Inside MachineId selection " + position);
                if (position > 0) {
                    allMachine.moveToPosition(position - 1);
                    machineId = allMachine.getString(allMachine.getColumnIndex(DBAdapter.MACHINE_ID));
                    String machineName = allMachine.getString(allMachine.getColumnIndex(DBAdapter.MACHINE));
                }
                data.setMachineId(machineId);
            }
        });
        machineId.setThreshold(1);


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
        ArrayAdapter<String> adp = new ArrayAdapter<String>(DetailActivity.this,
                android.R.layout.simple_dropdown_item_1line, farmerContactArray);
        farmerContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = farmerContactArray.indexOf(farmerContact.getText().toString());
                farmerListCursor.moveToPosition(index);
                String farmerId = farmerListCursor.getString(farmerListCursor.getColumnIndex(DBAdapter.FARMER_ID));
                String farmerContact = farmerListCursor.getString(farmerListCursor.getColumnIndex(DBAdapter.FARMER_CONTACT));
                String farmerName = farmerListCursor.getString(farmerListCursor.getColumnIndex(DBAdapter.FIRST_NAME));
                String stateId = "-1";
                String districtId = "-1";
                String tehsilId = "-1";
                String village_id = farmerListCursor.getString(farmerListCursor.getColumnIndex(DBAdapter.VILLAGE_ID));
                String cropId = farmerListCursor.getString(farmerListCursor.getColumnIndex(DBAdapter.CROP_ID));
                String totalAcre = farmerListCursor.getString(farmerListCursor.getColumnIndex(DBAdapter.TOTAL_ACRA));
                String farmerSubmitStatus =  farmerListCursor.getString(farmerListCursor.getColumnIndex(DBAdapter.SENDING_STATUS));


                Cursor tehsilIdCursor = db.getVillageById(village_id);
                if (tehsilIdCursor.moveToFirst()) {
                    tehsilId = tehsilIdCursor.getString(tehsilIdCursor.getColumnIndex(DBAdapter.TEHSIL_ID));
                    Cursor districtIdCursor = db.getTehsilById(tehsilId);
                    if (districtIdCursor.moveToFirst()) {
                        districtId = districtIdCursor.getString(districtIdCursor.getColumnIndex(DBAdapter.DISTRICT_ID));
                        Cursor stateIdCursor = db.getDistrictById(districtId);
                        if (stateIdCursor.moveToFirst()) {
                            stateId = stateIdCursor.getString(stateIdCursor.getColumnIndex(DBAdapter.STATE_ID));
                        }
                        stateIdCursor.close();
                    }
                    districtIdCursor.close();
                }
                tehsilIdCursor.close();

                System.out.println("Blank farmer detail has been called");

                data.setFarmerName(farmerName);
                data.setFarmerId(farmerId, db);
                data.setFarmerContact(farmerContact);
                data.setState(stateId);
                data.setDistrict(districtId);
                data.setTehsil(tehsilId);
                data.setVillage(village_id, db);
//                data.setCrop(cropId);
//                data.setTotalAcrage(totalAcre);
                farmerRegisteredStatus.setText(farmerSubmitStatus);
                if(farmerSubmitStatus.equals(DBAdapter.SAVE)){
                    farmerRegisteredStatus.setTextColor(getResources().getColor(R.color.fail_status));
                    farmerRegisteredStatus.setText("UN-REGISTERED");
                }else if(farmerSubmitStatus.equals(DBAdapter.SUBMIT)){
                    farmerRegisteredStatus.setTextColor(getResources().getColor(R.color.submit_status));
                    farmerRegisteredStatus.setText("REGISTERED");
                }else{
                    farmerRegisteredStatus.setTextColor(getResources().getColor(R.color.save_status));
                    farmerRegisteredStatus.setText("REGISTERED");
                }
                setDefaultData(data);

            }
        });

        farmerContact.setThreshold(1);
        farmerContact.setAdapter(adp);

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    if (actionNextValid(data)) {
                        Intent intent = new Intent(DetailActivity.this, DetailActivitySecond.class);
                        intent.putExtra(Constents.DATA, data);
                        intent.putParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA, productArray);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                    }
                }
            }
        });


        final Cursor getAllCrop = db.getAllCrop();
        final int allCropCount = getAllCrop.getCount();
        String[] cropStringArray = new String[allCropCount + 1];
        cropStringArray[0] = "Select Crop";
        if (allCropCount > 0) {
            getAllCrop.moveToFirst();
            for (int i = 1; i <= allCropCount; i++) {
                cropStringArray[i] = getAllCrop.getString(getAllCrop.getColumnIndex(DBAdapter.CROP));
                getAllCrop.moveToNext();
            }
        }

        ArrayAdapter<String> cropArrayAdapter = new ArrayAdapter<String>(DetailActivity.this, android.R.layout.simple_spinner_item, cropStringArray); //selected item will look like a spinner set from XML
        cropArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        crop.setAdapter(cropArrayAdapter);
        crop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String Crop = "-1";
                System.out.println("Inside Crop selection " + position);
                if (position > 0) {
                    getAllCrop.moveToPosition(position - 1);
                    Crop = getAllCrop.getString(getAllCrop.getColumnIndex(DBAdapter.CROP_ID));
                    String cropName = getAllCrop.getString(getAllCrop.getColumnIndex(DBAdapter.CROP));
                }
                data.setCrop(Crop);
//                setProduct(Crop);
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
    protected void onResume() {
        super.onResume();
        System.out.println("On Resume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("On Stop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        isValid();
        if(data!=null){
            if(data.getFarmerContact()!=null && data.getFarmerContact().trim().length()>0) {
                Cursor farmerCursor = db.getFarmerByContact(data.getFarmerContact());
                if(farmerCursor.moveToFirst()) {
                    data.save(db);
                }
                farmerCursor.close();
            }
        }

    }

    @Override
    public void onBackPressed() {
        System.out.println("On Back pressed");
//            data.save(db);
            DetailActivity.this.finish();
            Intent intent = new Intent(DetailActivity.this, TabedActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
//        }
    }

    public boolean actionNextValid(SprayMonitorData data) {
        boolean isValid = true;

        boolean farmerContact = (data.getFarmerContact() != null && data.getFarmerContact().trim().length() > 0);
        if (!farmerContact) {
            Toast.makeText(DetailActivity.this, "Farmer Contact number Required", Toast.LENGTH_LONG).show();
            return false;
        }

        Cursor farmerC = db.getFarmerByContact(data.getFarmerContact());
        if(!(farmerC.getCount()>0)){
            Toast.makeText(DetailActivity.this, "Farmer Contact does not exist.", Toast.LENGTH_LONG).show();
            farmerC.close();
            return false;
        }
        farmerC.close();

        /*boolean machineId = (data.getMachineId().equals("-1"));
        if (machineId) {
            Toast.makeText(DetailActivity.this, "Please select machine id", Toast.LENGTH_LONG).show();
            return false;
        }*/

        Cursor machineCursor = db.getMachineByName(machineId.getText().toString());
        if(machineCursor.moveToFirst()){
            String machineId = machineCursor.getString(machineCursor.getColumnIndex(DBAdapter.MACHINE_ID));
            data.setMachineId(machineId);
        }else{
            Toast.makeText(DetailActivity.this, "Machine name not exist.", Toast.LENGTH_LONG).show();
            machineCursor.close();
            return false;
        }
        machineCursor.close();


        boolean crop = (data.getCrop().equals("-1"));
        if (crop) {
            Toast.makeText(DetailActivity.this, "Please select crop", Toast.LENGTH_LONG).show();
            return false;
        }

        boolean totalAcre = (data.getTotalAcrage() != null && data.getTotalAcrage().trim().length() > 0);
        if (!totalAcre) {
            Toast.makeText(DetailActivity.this, "Please enter total acre", Toast.LENGTH_LONG).show();
            return false;
        }

        boolean acreCovered = (data.getAcrageCovered() != null && data.getAcrageCovered().trim().length() > 0);
        if (!acreCovered) {
            Toast.makeText(DetailActivity.this, "Please enter covered acre", Toast.LENGTH_LONG).show();
            return false;
        }

        boolean totalAcreOfCrop = (data.getTotalAcreOfCrop()!=null && (!data.getTotalAcreOfCrop().isEmpty()));
        if(!totalAcreOfCrop){
            Toast.makeText(DetailActivity.this,"Please Enter Total acre of crop",Toast.LENGTH_SHORT).show();
            return false;
        }

        try{
            double total = Double.valueOf(data.getTotalAcrage());
            double totalCropAcre  = Double.valueOf(data.getTotalAcreOfCrop());
            if(totalCropAcre>total){
                Toast.makeText(DetailActivity.this, "Crop Acre can not be greater than Total Acre", Toast.LENGTH_LONG).show();
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(DetailActivity.this, "Total Acre or Crop Acre entry is not correct", Toast.LENGTH_LONG).show();
            return false;
        }

        try{
            double total = Double.valueOf(data.getTotalAcreOfCrop());
            double covered  = Double.valueOf(data.getAcrageCovered());
            if(covered>total){
                Toast.makeText(DetailActivity.this, "Acre covered can not be greater than Crop Acre", Toast.LENGTH_LONG).show();
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(DetailActivity.this, "Crop Acre or Acre covered entry is not correct", Toast.LENGTH_LONG).show();
            return false;
        }




        /*boolean product = (data.getProductName().equals("-1"));
        if (product) {
            Toast.makeText(DetailActivity.this, "Please select product", Toast.LENGTH_LONG).show();
            return false;
        }

        boolean productQty = (data.getProductQty().equals("0"));
        if (productQty) {
            Toast.makeText(DetailActivity.this, "Please enter product quantity", Toast.LENGTH_LONG).show();
            return false;
        }*/

        return isValid;
    }

    private boolean isValid() {
        boolean isValid = true;

        data.setFarmerContact(farmerContact.getText().toString());
        data.setTotalAcrage(totalAcre.getText().toString());
        data.setAcrageCovered(acreCovered.getText().toString());
        data.setTotalAcreOfCrop(totalAcreOfCrop.getText().toString());

        System.out.println("data.getFarmerSendingStatus() : " + data.getFarmerSendingStatus());
        if(data.getFarmerContact()!=null && data.getFarmerContact().trim().length()>0 ) {
            if (data.getFarmerSendingStatus().equals(DBAdapter.SENT) || data.getFarmerSendingStatus().equals(DBAdapter.CONFIRM)) {
                return isValid;
            } else if (data.getFarmerSendingStatus().equals(DBAdapter.SUBMIT) || data.getFarmerSendingStatus().equals(DBAdapter.SAVE)) {
                return isValid;
            /*Toast.makeText(DetailActivity.this, "Please wait for farmer Confirmation", Toast.LENGTH_SHORT).show();
            return false;*/
            } else {
                Toast.makeText(DetailActivity.this, "Farmer does not exist. Please add the farmer.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(DetailActivity.this, "Farmer contact is blank", Toast.LENGTH_SHORT).show();
            return false;
        }

//        data.setFirstName(firstName.getText().toString());
//        data.setFarmerContact(farmerContact.getText().toString());

//        return isValid;
    }

    private void setDefaultData(SprayMonitorData data) {

        String stateId = data.getState();
        String district = data.getDistrict();
        String tehsil = data.getTehsil();

        if (data.getMachineId() != "-1") {
            Cursor getAllMachine = db.getAllMachine();
            int machineCount = getAllMachine.getCount();
            System.out.println("machineId : " + data.getMachineId() + " machineCount : " + machineCount);
            if (machineCount > 0 && (!data.getMachineId().equals("-1"))) {
                getAllMachine.moveToFirst();
                for (int d = 1; d <= machineCount; d++) {
                    if (data.getMachineId().trim().equals(getAllMachine.getString(getAllMachine.getColumnIndex(DBAdapter.MACHINE_ID)))) {
//                        machineId.setSelection(d, true);
                        machineId.setText(data.getMachineId());
                        break;
                    }
                    getAllMachine.moveToNext();
                }
            }
        }
        if (data.getFarmerName() != null && data.getFarmerName().length() > 0) {

            String districtName = db.getDistrictNameById(data.getDistrict());
            String stateName = db.getStateNameById(data.getState());
            String tehsilName = db.getTehsilNameById(data.getTehsil());


//            String farmerDetailString = data.getFarmerName() + " , " + tehsilName + ", " + districtName + ", " + stateName;
            String farmerDetailString = data.getFarmerName() + " , " + data.getVillageName();
            farmerDetail.setText(farmerDetailString);
            farmerDetail.setVisibility(View.VISIBLE);

        }


        if (data.getFarmerContact() != null && data.getFarmerContact().length() > 0) {
            farmerContact.setText(data.getFarmerContact());
        }

        if (data.getCrop() != "-1") {
            Cursor getAllCrop = db.getAllCrop();
            int cropCount = getAllCrop.getCount();
            System.out.println("cropId : " + data.getCrop() + " cropCount : " + cropCount);
            if (cropCount > 0 && (!data.getCrop().equals("-1"))) {
                getAllCrop.moveToFirst();
                for (int d = 1; d <= cropCount; d++) {
                    if (data.getCrop().trim().equals(getAllCrop.getString(getAllCrop.getColumnIndex(DBAdapter.CROP_ID)))) {
                        crop.setSelection(d, true);
                        break;
                    }
                    getAllCrop.moveToNext();
                }
            }
        }

        if (data.getTotalAcrage() != null) {
            totalAcre.setText(data.getTotalAcrage());
        }

        if (data.getAcrageCovered() != null) {
            acreCovered.setText(data.getAcrageCovered());
        }

        if(data.getTotalAcreOfCrop() != null){
            totalAcreOfCrop.setText(data.getTotalAcreOfCrop());
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
        if (machineImageCuresor.getCount() > 0) {
            machineImageCuresor.moveToLast();
            String imagePath = machineImageCuresor.getString(machineImageCuresor.getColumnIndex(DBAdapter.IMAGE_PATH));
            Bitmap bmp = BitmapFactory.decodeFile(imagePath);
            machineImageView.setImageBitmap(bmp);
        }
        machineImageCuresor.close();
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
                if (isValid()) {
                    data.setSendingStatus(DBAdapter.SAVE);
                    if (data.getFarmerContact() != null && data.getFarmerContact().trim().length() > 0) {
                        if (data.save(db)) {
                            Toast.makeText(DetailActivity.this, "Detail has been saved", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(DetailActivity.this, "Could not save the form", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(DetailActivity.this, "Farmer Contact Required", Toast.LENGTH_LONG).show();
                    }
                }
                return true;

            }
            case R.id.action_next:{
                if (isValid()) {
                    Intent intent = new Intent(DetailActivity.this, DetailActivitySecond.class);
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


    public static final int REQUEST_MACHINE_IMAGE = 115;
    public static final int REQUEST_PRODUCT = 112;
    public static boolean isCaptured = false; //to check that we have returned from camera intent not just orientation has changed
    String lowImagePath;
    String highImagePath;

    View.OnClickListener farmerProductGalOnClickListner = new View.OnClickListener() {
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

            Toast.makeText(DetailActivity.this, "Gallary is clicked", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DetailActivity.this, GalleryActivity.class);
            intent.putParcelableArrayListExtra("IMAGE", images);
            DetailActivity.this.startActivity(intent);
        }
    };

    Button.OnClickListener farmerProductImagListner = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i("MakeMachine", "startCameraActivity()");

            isCaptured = true;
            String imgName = String.valueOf(System.currentTimeMillis()) + "_" + Constents.FARMER_WITH_PRODUCT_DIR;
            Map<String, String> pathMap = FolderManager.getImgDir(data.getSprayMonitorId(), Constents.FARMER_WITH_PRODUCT_DIR, imgName);

            highImagePath = pathMap.get(FolderManager.HIGH_RESOLUTION_DIRECTORY);
            lowImagePath = pathMap.get(FolderManager.LOW_RESOLUTION_DIRECTORY);

            System.out.println("imagePath " + highImagePath);

            File file = new File(highImagePath);
            Uri outputFileUri = Uri.fromFile(file);

            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, REQUEST_PRODUCT);

        }
    };



    View.OnClickListener machineImageGalOnClickListner = new View.OnClickListener() {
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

            Toast.makeText(DetailActivity.this, "Gallary is clicked", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DetailActivity.this, GalleryActivity.class);
            intent.putParcelableArrayListExtra("IMAGE", images);
            DetailActivity.this.startActivity(intent);
        }
    };

    Button.OnClickListener machineImagListner = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i("MakeMachine", "startCameraActivity()");

            isCaptured = true;
            String imgName = String.valueOf(System.currentTimeMillis()) + "_" + Constents.MACHINE_IMAGE_DIR;
            Map<String, String> pathMap = FolderManager.getImgDir(data.getSprayMonitorId(), Constents.MACHINE_IMAGE_DIR, imgName);

            highImagePath = pathMap.get(FolderManager.HIGH_RESOLUTION_DIRECTORY);
            lowImagePath = pathMap.get(FolderManager.LOW_RESOLUTION_DIRECTORY);

            System.out.println("imagePath " + highImagePath);

            File file = new File(highImagePath);
            Uri outputFileUri = Uri.fromFile(file);

            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, REQUEST_MACHINE_IMAGE);

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

                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        machineImageView.setImageBitmap(bmp);
                        Cursor getImageByFormIdAndName = db.getImageByFormIdAndName(data.getSprayMonitorId(), Constents.MACHINE_IMAGE_DIR);
                        int imageCount = getImageByFormIdAndName.getCount() + 1;
                        ImageData idSaveImage = new ImageData(Constents.MACHINE_IMAGE_DIR, lowImagePath, "" + Lat_Lon_CellID.lat, "" + Lat_Lon_CellID.lon, Lat_Lon_CellID.datetimestamp, data.getSprayMonitorId(), String.valueOf(imageCount), DBAdapter.SAVE, Lat_Lon_CellID.CellID, Lat_Lon_CellID.LAC, Lat_Lon_CellID.mcc, Lat_Lon_CellID.mnc);
                        idSaveImage.save(db);
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

                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        farmerWithProductImageView.setImageBitmap(bmp);
                        Cursor getImageByFormIdAndName = db.getImageByFormIdAndName(data.getSprayMonitorId(), Constents.FARMER_WITH_PRODUCT_DIR);
                        int imageCount = getImageByFormIdAndName.getCount() + 1;
                        ImageData idSaveImage = new ImageData(Constents.FARMER_WITH_PRODUCT_DIR, lowImagePath, "" + Lat_Lon_CellID.lat, "" + Lat_Lon_CellID.lon, Lat_Lon_CellID.datetimestamp, data.getSprayMonitorId(), String.valueOf(imageCount), DBAdapter.SAVE,
                                Lat_Lon_CellID.CellID, Lat_Lon_CellID.LAC, Lat_Lon_CellID.mcc, Lat_Lon_CellID.mnc);
                        idSaveImage.save(db);
                    }
                }
                break;
        }
    }





    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i("MakeMachine", "onRestoreInstanceState()");
        machineId.setText(savedInstanceState.getString("machineId"));
        farmerContact.setText(savedInstanceState.getString("farmerContact"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("machineId", machineId.getText().toString());
        outState.putString("farmerContact", farmerContact.getText().toString());

    }

}
