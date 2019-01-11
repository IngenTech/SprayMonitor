package com.wrms.spraymonitor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wrms.spraymonitor.app.Constents;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.dataobject.ImageData;
import com.wrms.spraymonitor.dataobject.VideoData;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by WRMS on 11-06-2015.
 */
public class GalleryActivity extends ActionBarActivity {

    DBAdapter db;
    ImageData data;
    String sprayMonitoringId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);

        /*toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);                    // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button videoGallary = (Button) findViewById(R.id.videoGallary);
        if (videoGallary != null) {
            videoGallary.setVisibility(View.INVISIBLE);
        }*/

        db = new DBAdapter(GalleryActivity.this);
        db.open();

        ArrayList<ImageData> images = getIntent().getParcelableArrayListExtra("IMAGE");
        sprayMonitoringId = getIntent().getStringExtra(Constents.SPRAY_MONITORING_ID);
        System.out.println("IMAGES LENGTH : " + images.size());

        final ImageView diplayImage = (ImageView) findViewById(R.id.displayImage);
        final LinearLayout myGallery = (LinearLayout) findViewById(R.id.mygallery);

//        set Initial image
        if (images.size() > 0) {
            data = images.get(0);
            File imageFile = new File(data.getImage_path());
            if (imageFile.exists()) {
                data = images.get(0);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                final Bitmap bitmap = BitmapFactory.decodeFile(data.getImage_path(), options);
                diplayImage.setImageBitmap(bitmap);
            }
        }


        for (final ImageData imageName : images) {
            File imageFile = new File(imageName.getImage_path());

            if (imageFile.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                final Bitmap bitmap = BitmapFactory.decodeFile(imageName.getImage_path(), options);

                ImageView imageView = new ImageView(getApplicationContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(100, 100);
                lp.setMargins(5, 10, 5, 10);
                imageView.setLayoutParams(lp);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageBitmap(bitmap);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        diplayImage.setImageBitmap(bitmap);
                        data = imageName;
                    }
                });

                myGallery.addView(imageView);
            }
        }

        diplayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data != null) {
                    String imageInformation = "";
                    imageInformation = imageInformation + "ImageName : " + data.getImage_name() + "\n";
                    imageInformation = imageInformation + "DateTime : " + data.getDateTime() + "\n";
                    imageInformation = imageInformation + "Latitude : " + data.getLat() + "\n";
                    imageInformation = imageInformation + "Longitude : " + data.getLon() + "\n";
                    imageInformation = imageInformation + "SendingStatus : " + data.getSendingStatus() + "\n";

                    System.out.println("image_path" + data.getImage_path());

                /*new AsyncTask<Void,Void,Void>(){
                    @Override
                    protected Void doInBackground(Void... voids) {
                        data.sendRegistrationRequest(db);
                        return null;
                    }
                }.execute();*/

                    final AlertDialog.Builder builder = new AlertDialog.Builder(GalleryActivity.this);
                    builder.setTitle("IMAGE INFORMATION")
                            .setMessage(imageInformation)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                    builder.show();
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_video: {
                if (sprayMonitoringId != null) {
                    Cursor cursor = db.getVideoByFormId(sprayMonitoringId);
                    ArrayList<VideoData> videos = new ArrayList<VideoData>();
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        do {
                            VideoData videoData = new VideoData(cursor);
                            videos.add(videoData);
                        } while (cursor.moveToNext());
                    }

                    Toast.makeText(GalleryActivity.this, "Video Gallary is clicked", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(GalleryActivity.this, VideoGalleryActivity.class);
                    intent.putParcelableArrayListExtra("VIDEO", videos);
                    intent.putExtra(Constents.SPRAY_MONITORING_ID, sprayMonitoringId);
                    GalleryActivity.this.startActivity(intent);

                }
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

}
