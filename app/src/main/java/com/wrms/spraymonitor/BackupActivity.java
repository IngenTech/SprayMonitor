package com.wrms.spraymonitor;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.wrms.spraymonitor.app.Synchronize;
import com.wrms.spraymonitor.app.DBAdapter;

import net.ypresto.androidtranscoder.MediaTranscoder;
import net.ypresto.androidtranscoder.format.MediaFormatStrategyPresets;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by WRMS on 21-10-2015.
 */
public class BackupActivity extends AppCompatActivity {


    private static final String TAG = "TranscoderActivity";
    private static final int REQUEST_CODE_PICK = 1;
    private static final int SCAN_BARCODE = 2;

    String fileURI = "";

    private Toolbar toolbar;

    DBAdapter db;
    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        db = new DBAdapter(BackupActivity.this);
        db.open();

        findViewById(R.id.select_video_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* CompressMedia compressMedia = new CompressMedia();
                compressMedia.extractMediaFile();*/
                startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("video/*"), REQUEST_CODE_PICK);
            }
        });

        findViewById(R.id.scan_code).setOnClickListener(mScan);


        findViewById(R.id.upload_video).setOnClickListener(mUpload);


        findViewById(R.id.moveToNext).setOnClickListener(moveToNext);

    }


    public Button.OnClickListener moveToNext = new Button.OnClickListener() {
        public void onClick(View v) {

            Intent intent = new Intent(BackupActivity.this, DetailActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_PICK: {
                final File file;
                if (resultCode == RESULT_OK) {
                    try {
                        file = File.createTempFile("transcode_test", ".mp4", Environment.getExternalStorageDirectory());
                        fileURI = file.getAbsolutePath();
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to create temporary file.", e);
                        Toast.makeText(this, "Failed to create temporary file.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    ContentResolver resolver = getContentResolver();
                    final ParcelFileDescriptor parcelFileDescriptor;
                    try {
                        parcelFileDescriptor = resolver.openFileDescriptor(data.getData(), "r");
                    } catch (FileNotFoundException e) {
                        Log.w("Could not open '" + data.getDataString() + "'", e);
                        Toast.makeText(BackupActivity.this, "File not found.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    final FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
/*
// Start a lengthy operation in a background thread
                    new Thread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    int incr;
                                    // Do the "lengthy" operation 20 times
                                    for (incr = 0; incr <= 100; incr+=5) {
                                        // Sets the progress indicator to a max value, the
                                        // current completion percentage, and "determinate"
                                        // state
                                        mBuilder.setProgress(100, incr, false);
                                        // Displays the progress bar for the first time.
                                        mNotifyManager.notify(id, mBuilder.build());
                                        // Sleeps the thread, simulating an operation
                                        // that takes time
                                        try {
                                            // Sleep for 5 seconds
                                            Thread.sleep(5*1000);
                                        } catch (InterruptedException e) {
                                            Log.d(TAG, "sleep failure");
                                        }
                                    }
                                    // When the loop is finished, updates the notification
                                    mBuilder.setContentText("Download complete")
                                            // Removes the progress bar
                                            .setProgress(0,0,false);
                                    mNotifyManager.notify(id, mBuilder.build());
                                }
                            }
// Starts the thread by calling the run() method in its Runnable
                    ).start();

*/

                    final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
                    final int id = 100;
                    final NotificationManager mNotifyManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                    mBuilder.setContentTitle("Process Video")
                            .setContentText("Video Processing in progress")
                            .setSmallIcon(R.drawable.ic_action_new);
                    progressBar.setMax(1000);
                    final long startTime = SystemClock.uptimeMillis();
                    MediaTranscoder.Listener listener = new MediaTranscoder.Listener() {
                        @Override
                        public void onTranscodeProgress(double progress) {
                            if (progress < 0) {
                                progressBar.setIndeterminate(true);
                            } else {
                                progressBar.setIndeterminate(false);
                                progressBar.setProgress((int) Math.round(progress * 1000));
                                mBuilder.setProgress(1000, (int) Math.round(progress * 1000), false);
                                mNotifyManager.notify(id, mBuilder.build());
                            }
                        }

                        @Override
                        public void onTranscodeCompleted() {
                            Log.d(TAG, "transcoding took " + (SystemClock.uptimeMillis() - startTime) + "ms");
                            Toast.makeText(BackupActivity.this, "transcoded file placed on " + file, Toast.LENGTH_LONG).show();
                            findViewById(R.id.select_video_button).setEnabled(true);
                            progressBar.setIndeterminate(false);
                            progressBar.setProgress(1000);

                            mBuilder.setContentText("Processing complete")
                                    // Removes the progress bar
                                    .setProgress(0,0,false);
                            mNotifyManager.notify(id, mBuilder.build());
                            startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(file), "video/mp4"));
                            try {
                                parcelFileDescriptor.close();
                            } catch (IOException e) {
                                Log.w("Error while closing", e);
                            }
                        }

                        @Override
                        public void onTranscodeFailed(Exception exception) {
                            progressBar.setIndeterminate(false);
                            progressBar.setProgress(0);
                            findViewById(R.id.select_video_button).setEnabled(true);
                            Toast.makeText(BackupActivity.this, "Transcoder error occurred.", Toast.LENGTH_LONG).show();
                            try {
                                parcelFileDescriptor.close();
                            } catch (IOException e) {
                                Log.w("Error while closing", e);
                            }
                        }
                    };
                    Log.d(TAG, "transcoding into " + file);
                    MediaTranscoder.getInstance().transcodeVideo(fileDescriptor, file.getAbsolutePath(),
                            MediaFormatStrategyPresets.createAndroid720pStrategy(), listener);
                    findViewById(R.id.select_video_button).setEnabled(false);
                }
                break;
            }
            case SCAN_BARCODE : {
                System.out.println("resultCode : " + resultCode);
                if (resultCode == RESULT_OK) {
                    String contents = data.getStringExtra("SCAN_RESULT");
                    String format = data.getStringExtra("SCAN_RESULT_FORMAT");

                    System.out.println("contents + format : " + contents + " , " + format);
                    // Handle successful scan
                } else if (resultCode == RESULT_CANCELED) {
                    // Handle cancel
                }
            }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public Button.OnClickListener mScan = new Button.OnClickListener() {
        public void onClick(View v) {
//            Intent intent = new Intent(LoginActivity.this,CaptureActivity.class);
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(intent, SCAN_BARCODE);
        }
    };


    public Button.OnClickListener mUpload = new Button.OnClickListener() {
        public void onClick(View v) {
            new AsyncTask<Void,Void,Void>(){


                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    fileURI= "/storage/emulated/0/transcode_test1939990042.mp4";
                    System.out.println("File URI : "+fileURI);
                    upLoad2Server(fileURI);

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                }
            }.execute();
        }
    };

    public static int serverResponseCode = 0;
    public static int upLoad2Server(String sourceFileUri) {
        String upLoadServerUri = "http://ecodrivesolution.com/android/fileUploadCode.php";
        // String [] string = sourceFileUri;
        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        String responseFromServer = "";

        File sourceFile = new File(sourceFileUri);
        if (!sourceFile.isFile()) {
            Log.e("Huzza", "Source File Does not exist");
            return 0;
        }
        try { // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(upLoadServerUri);
            conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);
            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
            Log.i("Huzza", "Initial .available : " + bytesAvailable);

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // sendRegistrationRequest multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i("Upload file to server", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
            // close streams
            Log.i("Upload file to server", fileName + " File is written");
            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
//this block will give the response of upload link
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                    .getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                Log.i("Huzza", "RES Message: " + line);
            }
            rd.close();
        } catch (IOException ioex) {
            Log.e("Huzza", "error: " + ioex.getMessage(), ioex);
        }
        return serverResponseCode;  // like 200 (Ok)

    } // end upLoad2Server

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        //             switch (item.getItemId()) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                Toast.makeText(BackupActivity.this, "Device Not Authenticated", Toast.LENGTH_LONG).show();
                return true;
            }
           /* case R.id.action_refresh: {
                synchronize(Synchronize.syncList);
                return true;
            }*/
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

        class Background extends AsyncTask<Void,Void,String>{

            //            ProgressDialog pDialog  = new ProgressDialog(HomeActivity.this);
            String syncString;

            Background(String syncString){
                this.syncString = syncString;
            }

            @Override
            protected void onPreExecute() {
               /* pDialog.setMessage("Synchronize for "+syncString+".....");
                pDialog.show();*/
                pDialog =  ProgressDialog.show(BackupActivity.this, "",
                        "Synchronize for " + syncString + ".....", true);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = Synchronize.syncFor(syncString, db,BackupActivity.this);
                if((!Synchronize.isConnectedToServer)|| (!response.contains("Success"))){
                    syncResult = syncResult+"Could not sync for "+syncString+"\n";
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(BackupActivity.this);
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

  /*  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
