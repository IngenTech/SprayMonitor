package com.wrms.spraymonitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wrms.spraymonitor.app.Constents;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.dataobject.VideoData;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by WRMS on 30-10-2015.
 */
public class VideoGalleryActivity extends Activity {

    DBAdapter db;
    VideoData data = new VideoData();
    ArrayList<VideoData> video = new ArrayList<>();
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_gallery_layout);

        /*Button videoGallary = (Button) findViewById(R.id.videoGallary);
        videoGallary.setText("PHOTO");
        if (videoGallary != null) {
            videoGallary.setVisibility(View.INVISIBLE);
        }*/

        final String sprayMonitoringId = getIntent().getStringExtra(Constents.SPRAY_MONITORING_ID);

        db = new DBAdapter(VideoGalleryActivity.this);
        db.open();
        video = getIntent().getParcelableArrayListExtra("VIDEO");
        System.out.println("IMAGES LENGTH : " + video.size());

        final ImageView diplayImage = (ImageView) findViewById(R.id.displayImage);
        final ImageView playVideo = (ImageView)findViewById(R.id.playVideo);
        final LinearLayout myGallery = (LinearLayout) findViewById(R.id.mygallery);

//        set Initial image
        if(video.size()>0){

            data = video.get(0);
            File videoFile = new File(data.getVideo_path());
            if (videoFile.exists()) {
                data = video.get(0);
                Bitmap bmThumbnail;
                bmThumbnail = ThumbnailUtils.createVideoThumbnail(data.getVideo_path(), Thumbnails.MICRO_KIND);
                diplayImage.setImageBitmap(bmThumbnail);
            }
        }


        for (final VideoData videoName : video) {
            File videoFile = new File(videoName.getVideo_path());

            if (videoFile.exists()) {
                final Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(videoName.getVideo_path(), Thumbnails.MICRO_KIND);

                ImageView imageView = new ImageView(getApplicationContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(100, 100);
                lp.setMargins(5, 10, 5, 10);
                imageView.setLayoutParams(lp);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageBitmap(bmThumbnail);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        diplayImage.setImageBitmap(bmThumbnail);
                        data = videoName;
                    }
                });

                myGallery.addView(imageView);
            }else{
                Toast.makeText(VideoGalleryActivity.this,"Video is not transcoded",Toast.LENGTH_SHORT).show();
            }
        }

        playVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(VideoGalleryActivity.this,PlayVideoActivity.class);
                intent.putExtra("data",data);
                startActivity(intent);
            }
        });

        diplayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data!=null) {
                    String imageInformation = "";
                    if(data.getVideo_name()!=null){
                        if(data.getVideo_name().contains(Constents.TANK_FILLING_VIDEO)||data.getVideo_name().contains(Constents.SPRAY_VIDEO)){
                            imageInformation = imageInformation + "VideoName : " + data.getVideo_name()+"_"+data.getFormType()+ "\n";
                        }else{
                            imageInformation = imageInformation + "VideoName : " + data.getVideo_name() + "\n";
                        }
                    }

                    imageInformation = imageInformation + "DateTime : " + data.getDateTime() + "\n";
                    imageInformation = imageInformation + "Latitude : " + data.getLat() + "\n";
                    imageInformation = imageInformation + "Longitude : " + data.getLon() + "\n";
                    imageInformation = imageInformation + "SendingStatus : " + data.getSendingStatus() + "\n";

                    System.out.println("image_path" + data.getVideo_path());

//                data.saveToXML();

                    final AlertDialog.Builder builder = new AlertDialog.Builder(VideoGalleryActivity.this);
                    builder.setTitle("VIDEO INFORMATION")
                            .setMessage(imageInformation)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                    builder.show();
                }

                /*new AsyncTask<Void,Void,Void>(){


                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        data.post();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                    }
                }.execute();*/
            }
        });

        /*if (sprayMonitoringId != null) {
            videoGallary.setVisibility(View.VISIBLE);
            videoGallary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VideoGalleryActivity.this.finish();
                }
            });
        }*/


    }

}