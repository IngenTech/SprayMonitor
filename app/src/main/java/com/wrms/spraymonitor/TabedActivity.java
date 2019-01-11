package com.wrms.spraymonitor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.wrms.spraymonitor.app.Constents;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.Synchronize;
import com.wrms.spraymonitor.dataobject.FarmerData;
import com.wrms.spraymonitor.dataobject.SprayMonitorData;
import com.wrms.spraymonitor.utils.AppManager;
import com.wrms.spraymonitor.utils.Uploadable;

import java.util.ArrayList;

public class TabedActivity extends AppCompatActivity
implements SprayForms.OnFragmentInteractionListener,
FarmersList.OnFragmentInteractionListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    DBAdapter db;
    ProgressDialog pDialog;
    private ArrayList<SprayMonitorData> sprayMonitoringItemList = new ArrayList<>();
    private ArrayList<FarmerData> farmersItemList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabed);

        System.out.println("On Create");
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);*/

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        db = new DBAdapter(TabedActivity.this);
        db.open();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TabedActivity.this, RegisterFarmerActivity.class);
                startActivity(intent);
            }
        });

        AppManager.isGPSenabled(this);
    }


    @Override
    protected void onStart() {
        super.onStart();

        sprayMonitoringItemList = new ArrayList<>();
        farmersItemList = new ArrayList<>();

        Cursor getFormID = db.getFormID();
        if(getFormID.getCount()>0){
            getFormID.moveToFirst();
            do{
                String sprayMonitoringId = getFormID.getString(getFormID.getColumnIndex(DBAdapter.SPRAY_MONITORING_ID));
                SprayMonitorData data = new SprayMonitorData(sprayMonitoringId,db);
                /*data.setSendingStatus(DBAdapter.SAVE);
                data.save(db);*/
                sprayMonitoringItemList.add(data);
            }while (getFormID.moveToNext());
        }

        Cursor farmerCursor = db.getFarmerCreatedByUser();
        System.out.println("Farmer Cursor Length : "+farmerCursor.getCount());
        if(farmerCursor.getCount()>0){
            farmerCursor.moveToFirst();
            do{
                String farmerId = farmerCursor.getString(farmerCursor.getColumnIndex(DBAdapter.FARMER_ID));
                FarmerData data = new FarmerData(farmerId,db);
                farmersItemList.add(data);
            }while (farmerCursor.moveToNext());
        }
        farmerCursor.close();

        /*Cursor getSavedFarmer = db.getSavedFarmer();
        if(getSavedFarmer.getCount()>0){
            getSavedFarmer.moveToFirst();
            do{
                String farmerId = getSavedFarmer.getString(getSavedFarmer.getColumnIndex(DBAdapter.FARMER_ID));
                FarmerData data = new FarmerData(farmerId,db);
                farmersItemList.add(data);
            }while (getFormID.moveToNext());
        }
        getSavedFarmer.close();

        Cursor getConfirmedFarmer = db.getSavedConfirmed();
        if(getConfirmedFarmer.getCount()>0){
            getConfirmedFarmer.moveToFirst();
            do{
                String farmerId = getConfirmedFarmer.getString(getConfirmedFarmer.getColumnIndex(DBAdapter.FARMER_ID));
                FarmerData data = new FarmerData(farmerId,db);
                farmersItemList.add(data);
            }while (getFormID.moveToNext());
        }
        getConfirmedFarmer.close();

        Cursor getRejectedFarmer = db.getSavedRejected();
        if(getRejectedFarmer.getCount()>0){
            getRejectedFarmer.moveToFirst();
            do{
                String farmerId = getRejectedFarmer.getString(getRejectedFarmer.getColumnIndex(DBAdapter.FARMER_ID));
                FarmerData data = new FarmerData(farmerId,db);
                farmersItemList.add(data);
            }while (getFormID.moveToNext());
        }
        getRejectedFarmer.close();*/

    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.existing_form, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_add: {
                Intent intent = new Intent(TabedActivity.this, SprayDetailOneActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                finish();
                return true;
            }
            case android.R.id.home: {
                finish();
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    static  int synchronizeCount = 0;
    String syncResult = "";
    String syncedElement = "";
    private void synchronize(final String[] syncName){

        final int arrayCount = syncName.length;
        synchronizeCount = 0;
        syncResult = "";
        syncedElement = "";

        class Background extends AsyncTask<Void,Void,String> {
            String syncString;

            Background(String syncString){
                this.syncString = syncString;
            }

            @Override
            protected void onPreExecute() {
                pDialog =  ProgressDialog.show(TabedActivity.this, "",
                        "Synchronize for " + syncString + ".....", true);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = Synchronize.syncFor(syncString, db,TabedActivity.this);
                if((!Synchronize.isConnectedToServer)){
                    syncResult = syncResult+"Could not sync for "+syncString+"\n";
                }else if(!response.contains("Success")){
                    syncResult = syncResult+response+"\n";
                }else{
                    syncedElement = syncedElement+","+syncString;
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if(pDialog!=null) {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                }
                synchronizeCount++;
                if(synchronizeCount<arrayCount){
                    new Background(syncName[synchronizeCount]).execute();
                }else{
                    String[] syncedArray = syncedElement.split(",");

                    AlertDialog.Builder builder = new AlertDialog.Builder(TabedActivity.this);
                    builder.setTitle("Syncing Status");
                    if(syncResult.trim().length()>0){
                        builder.setMessage(syncResult);
                    }else if(syncedArray.length != Synchronize.syncList.length){
                        builder.setMessage("Could Not Synchronize. Please Sync again");
                    }else{
                        builder.setMessage("Synchronize Successfully");
                    }
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            }
        }
        new Background(syncName[synchronizeCount]).execute();
    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
        
        
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position==0){
                return SprayForms.newInstance(sprayMonitoringItemList,"param2");
            }else {
                return FarmersList.newInstance(farmersItemList, "param2");
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SPRAY LOG";
                case 1:
                    return "REGISTRATION STATUS";
                case 2:
                    return "BOOKED";
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
       /* TabedActivity.this.finish();
        Intent intent = new Intent(TabedActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);*/

        finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO make intraction action
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
