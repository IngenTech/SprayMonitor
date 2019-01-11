package com.wrms.spraymonitor;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.wrms.spraymonitor.app.Constents;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.formContentRecyclerAdapter;
import com.wrms.spraymonitor.background.AuthenticateService;
import com.wrms.spraymonitor.dataobject.ContentData;
import com.wrms.spraymonitor.dataobject.ImageData;
import com.wrms.spraymonitor.dataobject.PaymentObject;
import com.wrms.spraymonitor.dataobject.SprayMonitorData;
import com.wrms.spraymonitor.dataobject.VideoData;

import java.util.ArrayList;
import java.util.List;

public class ShowFormDetailsActivity extends AppCompatActivity {

    private List<ContentData> sprayMonitoringItemList = new ArrayList<ContentData>();

    private RecyclerView mRecyclerView;

    private formContentRecyclerAdapter adapter;

    DBAdapter db;
    private Toolbar toolbar;
    ProgressDialog pDialog;
    SprayMonitorData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_form_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        db = new DBAdapter(ShowFormDetailsActivity.this);
        db.open();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                ArrayList<ImageData> images = new ArrayList<>();

                Cursor imageCursor = db.getImageByFormId(data.getSprayMonitorId());
                if(imageCursor.getCount()>0){
                    imageCursor.moveToFirst();
                    do{
                        ImageData imageData = new ImageData(imageCursor);
                        images.add(imageData);
                    }while (imageCursor.moveToNext());
                }

                Toast.makeText(ShowFormDetailsActivity.this, "Gallary is clicked", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ShowFormDetailsActivity.this, GalleryActivity.class);
                intent.putParcelableArrayListExtra("IMAGE", images);
                intent.putExtra(Constents.SPRAY_MONITORING_ID, data.getSprayMonitorId());
                ShowFormDetailsActivity.this.startActivity(intent);
            }
        });

        data = getIntent().getParcelableExtra(Constents.DATA);

        if(data!=null){
            sprayMonitoringItemList = data.getListContentData(db);
        }else{
            sprayMonitoringItemList = getIntent().getParcelableArrayListExtra(Constents.CONTENT_DATAS);
            fab.setVisibility(View.GONE);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.form_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        adapter = new formContentRecyclerAdapter(ShowFormDetailsActivity.this, sprayMonitoringItemList);
        mRecyclerView.setAdapter(adapter);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void sendRequest() {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                pDialog = ProgressDialog.show(ShowFormDetailsActivity.this, "Sending", "Please wait while sending..", true);
            }

            @Override
            protected String doInBackground(Void... voids) {

                ArrayList<ImageData> images = data.getImages(db);
                ArrayList<VideoData> videos= data.getVideos(db);
                ArrayList<PaymentObject> paymentObjects= data.getPayments(db);

                AuthenticateService.sendSprayMonitoringData(data,images,paymentObjects,videos,db,ShowFormDetailsActivity.this);

                return String.valueOf(AuthenticateService.errorCounter);
            }

            @Override
            protected void onPostExecute(String result) {
                pDialog.cancel();
                System.out.println("Result :" + result);
                if (result!=null && (!result.equals(0))) {
                    String message = "We have found "+result+" errors while sending";
                    showDialogBuilder(message);
                } else {
                    showDialogBuilder("Sent successfully");
                }

            }
        }.execute();

    }

    private void showDialogBuilder(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowFormDetailsActivity.this);
        builder.setTitle("Sending Status").
                setMessage(message).
                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(ShowFormDetailsActivity.this, TabedActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home: {

                finish();

                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }

    }

}
