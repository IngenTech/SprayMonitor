package com.wrms.spraymonitor.fuelmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wrms.spraymonitor.GalleryActivity;
import com.wrms.spraymonitor.R;
import com.wrms.spraymonitor.TabedActivity;
import com.wrms.spraymonitor.app.AppController;
import com.wrms.spraymonitor.app.Constents;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.Synchronize;
import com.wrms.spraymonitor.background.Lat_Lon_CellID;
import com.wrms.spraymonitor.dataobject.Credential;
import com.wrms.spraymonitor.dataobject.FuelData;
import com.wrms.spraymonitor.dataobject.FuelImageData;

import com.wrms.spraymonitor.dataobject.ImageData;
import com.wrms.spraymonitor.dataobject.PaymentObject;
import com.wrms.spraymonitor.dataobject.SprayCropData;
import com.wrms.spraymonitor.dataobject.SprayMonitorData;
import com.wrms.spraymonitor.dataobject.VideoData;
import com.wrms.spraymonitor.utils.AppManager;
import com.wrms.spraymonitor.utils.FolderManager;
import com.wrms.spraymonitor.utils.ResizeBitmap;
import com.wrms.spraymonitor.utils.Utility;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 25-03-2017.
 */
public class MachinRunTime extends Fragment {


    private int REQUEST_CAMERA_START = 0, SELECT_FILE_START = 1;
    private int REQUEST_CAMERA_STOP = 2, SELECT_FILE_STOP = 3;
    String imageString_start, imageString_stop;


    private String userChoosenTask;


    ImageButton machineImgButton;
    ImageView machineImageView;

    ImageButton machineStopImgButton;
    ImageView machineStopImageView;

    public static final int REQUEST_START_IMAGE = 115;
    public static final int REQUEST_STOP_IMAGE = 116;


    double latitude;
    double longitude;
    String datetimestamp;
    String CellID;
    String lac;
    String mcc;
    String mnc;

    Button submitButton;
    Button saveButton;
    MachineRunTimeData runData;
    DBAdapter db;
    FuelImageData idSaveImage;
    AutoCompleteTextView machineIdSpinner;

    public static final String RUN_DATA = "run_data";
    public static final SimpleDateFormat FUEL_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    EditText dateTime;
    EditText machineStartHour, machineStopHour;
    String machineId;

    public MachinRunTime() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.machine_run_time_activity, container, false);

        db = new DBAdapter(getActivity());
        db.open();

        setLocation();

        machineIdSpinner = (AutoCompleteTextView) view.findViewById(R.id.machine_feul);
        machineStartHour = (EditText) view.findViewById(R.id.machine_start_hours_Edt);
        machineStopHour = (EditText) view.findViewById(R.id.machine_stop_hours_Edt);

        saveButton = (Button) view.findViewById(R.id.saveButton);
        submitButton = (Button) view.findViewById(R.id.submitButton);

        dateTime = (EditText) view.findViewById(R.id.fuelDateEdt);
        machineImageView = (ImageView) view.findViewById(R.id.machine_ImageView);
        machineImgButton = (ImageButton) view.findViewById(R.id.machine_img_btn);

        machineStopImageView = (ImageView) view.findViewById(R.id.machine_stop_ImageView);
        machineStopImgButton = (ImageButton) view.findViewById(R.id.machine_stop_img_btn);

        machineImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLocation();

                selectImage();
            }
        });

        machineStopImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
                selectImage1();
            }
        });


        final Cursor allMachine = db.getAllMachine();
        final int allMachineCount = allMachine.getCount();
        final ArrayList<String> machineStringArray = new ArrayList<String>();

        if (allMachineCount > 0) {
            allMachine.moveToFirst();
            for (int i = 1; i <= allMachineCount; i++) {
                machineStringArray.add(allMachine.getString(allMachine.getColumnIndex(DBAdapter.MACHINE)));
                allMachine.moveToNext();
            }
        }

        ArrayAdapter<String> machineArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.auto_complete_textview_item, machineStringArray); //selected item will look like a spinner set from XML
        //  machineArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        machineIdSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int index = machineStringArray.indexOf(machineIdSpinner.getText().toString().trim());
                System.out.println("Inside MachineId selection " + index);

                if (index > 0) {
                    allMachine.moveToPosition(index);
                    machineId = allMachine.getString(allMachine.getColumnIndex(DBAdapter.MACHINE_ID));
                    String machineName = allMachine.getString(allMachine.getColumnIndex(DBAdapter.MACHINE));
                    Log.v("machinID", machineId + "//" + machineName);
                }
                //  fuelData.setMachineId(machineId);

            }


        });

        machineIdSpinner.setThreshold(1);
        machineIdSpinner.setAdapter(machineArrayAdapter);

        idSaveImage = new FuelImageData();

        runData = getActivity().getIntent().getParcelableExtra(RUN_DATA);

        System.out.println("RunData : " + runData);


        if (runData == null) {
            runData = new MachineRunTimeData();
            runData.setFuelId(MachineRunTimeData.createFuelId());

            System.out.println("RunHoursID Time Start : " + MachineRunTimeData.createFuelId());

            Date date = new Date();
            runData.setCreatedDateTime(Constents.sdf.format(date));
            runData.setSendingStatus(DBAdapter.SAVE);
        }/*else {


            String fid=runData.getFuelId();
            String cd=runData.getCreatedDateTime();

            System.out.println("Run Start : " + fid+"-"+cd);


            runData.setFuelId(fid);

            runData.setCreatedDateTime(cd);
            runData.setSendingStatus(DBAdapter.SAVE);
        }

*/
        dateTime.setEnabled(false);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Cursor getRunImageByFormId = db.getAllFuelImage();
                System.out.println("Image Cursor Length : " + getRunImageByFormId.getCount());

                if (isValid()) {

                    setRunData();

                    Date date = new Date();
                    String todayDate = FUEL_DATE_TIME_FORMAT.format(date);

                    System.out.println("fuelData: " + runData.getFuelId());
                    System.out.println("fuelData: " + runData.getFuelDate());
                    System.out.println("fuelData: " + runData.getMachineId());
                    System.out.println("fuelData: " + runData.getMachineEndHour());


                    System.out.println("fuelData.getFuelDate().equals(todayDate) : " + (runData.getFuelDate().equals(todayDate)));
                    if (runData.getFuelDate().equals(todayDate)) {
                        Cursor fuelCursor = db.getRunFuelByDate(todayDate);
                        if (fuelCursor.moveToLast() && fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.SENDING_STATUS)).equals(DBAdapter.SENT)) {

                            Toast.makeText(getActivity(), "Today's Machine Run Hour detail has been sent", Toast.LENGTH_LONG).show();

                        } else {

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            int hours = calendar.get(Calendar.HOUR_OF_DAY);
                            System.out.println("HOURSE OF THE DAY : " + hours);
                            if (hours < 17) {


                                //  Log.v("formSubmitted Status",idSaveImage.getSendingStatus());

                                runData.setSendingStatus(DBAdapter.SUBMIT);
                                idSaveImage.setSendingStatus(DBAdapter.SUBMIT);

                                Log.v("formSubmitted Status", idSaveImage.getSendingStatus());

                                Toast.makeText(getActivity(), "Form can't be submitted before 5PM ", Toast.LENGTH_LONG).show();

                            } else {


                                runData.setSendingStatus(DBAdapter.SUBMIT);
                                idSaveImage.setSendingStatus(DBAdapter.SUBMIT);

                                Log.v("njklkljkljkljlsk", "idSaveImage");

                                submitFuelDetail(runData);
                            }
                        }
                        fuelCursor.close();
                    } else {
                        submitFuelDetail(runData);
                    }
                }


            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRunData();

                Date date = new Date();
                String todayDate = FUEL_DATE_TIME_FORMAT.format(date);
                if (runData.getFuelDate().equalsIgnoreCase(todayDate)) {
                    System.out.println("RunDate check : " + runData.getFuelDate());

                    //    Log.v("formSubmitted Status",idSaveImage.getSendingStatus());

                    idSaveImage.setSendingStatus(DBAdapter.SAVE);

                    runData.setSendingStatus(DBAdapter.SAVE);
                    if (!runData.save(db)) {
                        System.out.println("RunDate : " + runData.getFuelDate());
                        Toast.makeText(getActivity(), "Today's Machine Run Hour detail has been sent", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Today's Machine Run Hour detail has been saved", Toast.LENGTH_LONG).show();
                    }

                }
                getActivity().finish();
            }


        });

        setDefaultData();

        AppManager.isGPSenabled(getActivity());

        return view;
    }


    private void setDefaultData() {

        System.out.println("Run Time Date : " + runData.getFuelDate());
        System.out.println("Run Time Start : " + runData.getMachineStartHour());

        if (runData.getFuelDate() != null && runData.getFuelDate().trim().length() > 0) {
            dateTime.setText(runData.getFuelDate());
        } else {
            Date date = new Date();
            String dateString = FUEL_DATE_TIME_FORMAT.format(date);
            dateTime.setText(dateString);
        }

        if (runData.getMachineId() != null && (!runData.getMachineId().equals("-1"))) {
            machineIdSpinner.setText(runData.getMachineId());
            machineIdSpinner.setSelection(runData.getMachineId().length());

            machineId = runData.getMachineId();
            System.out.println("Fuel Date : " + runData.getMachineId());
        }

        if (runData.getMachineStartHour() != null && runData.getMachineStartHour().trim().length() > 0) {
            machineStartHour.setText(runData.getMachineStartHour());
        }

        if (runData.getMachineEndHour() != null && runData.getMachineEndHour().trim().length() > 0) {
            machineStopHour.setText(runData.getMachineEndHour());
        }

        SharedPreferences preferences_start = getActivity().getSharedPreferences("Start_Image", getActivity().MODE_PRIVATE);
        String startImage = preferences_start.getString("start_image", null);

        if (startImage != null && startImage.length() > 10) {

            byte[] decodedString = Base64.decode(startImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            machineImageView.setImageBitmap(decodedByte);
        }

        SharedPreferences preferences_stop = getActivity().getSharedPreferences("Stop_Image", getActivity().MODE_PRIVATE);
        String stopImage = preferences_stop.getString("stop_image", null);

        if (stopImage != null && stopImage.length() > 10) {

            byte[] decodedString = Base64.decode(stopImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            machineStopImageView.setImageBitmap(decodedByte);
        }

    }


    private void getLocation() {
        latitude = Lat_Lon_CellID.lat;
        longitude = Lat_Lon_CellID.lon;
        datetimestamp = Lat_Lon_CellID.getDatetimestamp();
        CellID = Lat_Lon_CellID.getCELLID();
        lac = Lat_Lon_CellID.getLac();
        mcc = Lat_Lon_CellID.getMCC();
        mnc = Lat_Lon_CellID.getMNC();
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

    private boolean isValid() {
        boolean result = true;

        if (dateTime.getText() != null && dateTime.getText().toString().trim().length() > 0) {
            runData.setFuelDate(dateTime.getText().toString());
        } else {
            Toast.makeText(getActivity(), "Please Select Fuel Date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (machineStartHour.getText() != null && machineStartHour.getText().toString().trim().length() > 0) {
            runData.setMachineStartHour(machineStartHour.getText().toString());
        } else {
            Toast.makeText(getActivity(), "Please enter start hour", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (machineStopHour.getText() != null && machineStopHour.getText().toString().trim().length() > 0) {
            runData.setMachineEndHour(machineStopHour.getText().toString());
        } else {
            Toast.makeText(getActivity(), "Please enter stop hour", Toast.LENGTH_SHORT).show();
            return false;
        }


        return result;
    }


    ProgressDialog dialog;

    private void submitFuelDetail(final MachineRunTimeData data) {

        String methodeName = "machineRunHour";

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, Synchronize.URL + methodeName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String fuelSubmissionResponse) {
                        dialog.dismiss();
                        try {
                            System.out.println("Machine Run Response : " + fuelSubmissionResponse);
                            JSONObject jsonObject = new JSONObject(fuelSubmissionResponse);

                            if (jsonObject.has("status")) {
                                if (jsonObject.getString("status").equals("success") || fuelSubmissionResponse.contains("AlreadyExist")) {
                                    Toast.makeText(getActivity(), "Machine Run detail has been submitted", Toast.LENGTH_LONG).show();
                                    data.setSendingStatus(DBAdapter.SENT);
                                    data.save(db);

                                    SharedPreferences preferences_start = getActivity().getSharedPreferences("Start_Image", getActivity().MODE_PRIVATE);
                                    preferences_start.edit().clear().apply();

                                    SharedPreferences preferences_stop = getActivity().getSharedPreferences("Stop_Image", getActivity().MODE_PRIVATE);
                                    preferences_stop.edit().clear().apply();

                                    //submitImageStart();

                                    getActivity().finish();

                                } else {
                                    Toast.makeText(getActivity(), "Machine Run request has been refused", Toast.LENGTH_LONG).show();
                                    Date date = new Date();
                                    String todayDate = FUEL_DATE_TIME_FORMAT.format(date);
                                    if (runData.getFuelDate().equals(todayDate)) {
                                        data.setSendingStatus(DBAdapter.SAVE);
                                        data.save(db);
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Not able parse response", Toast.LENGTH_LONG).show();
                            notSubmittedAlert(data);
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                dialog.cancel();
                Toast.makeText(getActivity(), "Not able to connect with server", Toast.LENGTH_LONG).show();
                notSubmittedAlert(data);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = data.getParametersInMap(db);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        stringVarietyRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        dialog = ProgressDialog.show(getActivity(), "Submitting Detail",
                "Please wait...", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);
    }

    private void notSubmittedAlert(final MachineRunTimeData data) {
        Date date = new Date();
        String todayDate = FUEL_DATE_TIME_FORMAT.format(date);
        if (data.getFuelDate().equals(todayDate)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Not Submitted").
                    setMessage("Do you want to save the details for the day?").
                    setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            setRunData();
                            data.setSendingStatus(DBAdapter.SUBMIT);
                            data.save(db);
                            getActivity().finish();
                        }
                    }).
                    setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            getActivity().finish();
                        }
                    });
            builder.show();
        }
    }

    private void setRunData() {

        Log.v("machineee",machineId+"");

        getLocation();

        if (machineId != null && machineId.length() > 0) {
            runData.setMachineId(machineId);


            if (dateTime.getText() != null && dateTime.getText().toString().trim().length() > 0) {
                runData.setFuelDate(dateTime.getText().toString());
            }

            if (machineStartHour.getText() != null && machineStartHour.getText().toString().trim().length() > 0) {
                runData.setMachineStartHour(machineStartHour.getText().toString());
            }

            if (machineStopHour.getText() != null && machineStopHour.getText().toString().trim().length() > 0) {
                runData.setMachineEndHour(machineStopHour.getText().toString());
            }


            if (imageString_stop != null && imageString_stop.length() > 10) {
                SharedPreferences preferences_stop = getActivity().getSharedPreferences("Stop_Image", getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor_stop = preferences_stop.edit();
                editor_stop.putString("stop_image_name", "Stop_run_hour");
                editor_stop.putString("stop_image", imageString_stop);
                editor_stop.putString("stop_lat", String.valueOf(Lat_Lon_CellID.lat));
                editor_stop.putString("stop_lon", String.valueOf(Lat_Lon_CellID.lon));
                editor_stop.putString("stop_image_time", Lat_Lon_CellID.datetimestamp);

                editor_stop.putString("machine_code", runData.getMachineId());
                editor_stop.putString("run_hour_id", runData.getFuelId());

                editor_stop.commit();

                idSaveImage = new FuelImageData("Stop_run_hour", imageString_stop, "" + String.valueOf(Lat_Lon_CellID.lat), "" + String.valueOf(Lat_Lon_CellID.lon), Lat_Lon_CellID.datetimestamp, runData.getFuelId(), runData.getMachineId(), DBAdapter.SAVE);
                idSaveImage.save(db);

                imageString_stop = null;
            }

            if (imageString_start != null && imageString_start.length() > 10) {

                SharedPreferences preferences_start = getActivity().getSharedPreferences("Start_Image", getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences_start.edit();
                editor.putString("start_image_name", "Strat_run_hour");
                editor.putString("start_image", imageString_start);
                editor.putString("start_lat", String.valueOf(Lat_Lon_CellID.lat));
                editor.putString("start_lon", String.valueOf(Lat_Lon_CellID.lon));
                editor.putString("start_image_time", Lat_Lon_CellID.datetimestamp);

                editor.putString("machine_code", runData.getMachineId());
                editor.putString("run_hour_id", runData.getFuelId());

                editor.commit();

                idSaveImage = new FuelImageData("Start_run_hour", imageString_start, "" + String.valueOf(Lat_Lon_CellID.lat), "" + String.valueOf(Lat_Lon_CellID.lon), Lat_Lon_CellID.datetimestamp, runData.getFuelId(), runData.getMachineId(), DBAdapter.SAVE);
                idSaveImage.save(db);

                imageString_start = null;

            }
        } else {

            Toast.makeText(getActivity(), "Please select machine", Toast.LENGTH_SHORT).show();
        }
    }


    private void selectImage() {
        final CharSequence[] items = {"Take Photo",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //  builder.setTitle("Upload Document!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    boolean resultCam = Utility.checkPermissionCamera(getActivity());
                    if (resultCam) {
                        cameraIntent();
                    }

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE_START);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA_START);
    }

    private void selectImage1() {
        final CharSequence[] items = {"Take Photo",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setTitle("Upload Document!");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    boolean resultCam = Utility.checkPermissionCamera(getActivity());
                    if (resultCam) {
                        cameraIntent1();
                    }

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent1() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE_STOP);
    }

    private void cameraIntent1() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA_STOP);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA_START) {
                onCaptureImageResult(data);
            }  else if (requestCode == REQUEST_CAMERA_STOP) {
                onCaptureImageResult_Stop(data);
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        machineImageView.setImageBitmap(thumbnail);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        imageString_start = Base64.encodeToString(byteArray, Base64.DEFAULT);


    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        machineImageView.setImageBitmap(bm);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        try {
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            imageString_start = Base64.encodeToString(byteArray, Base64.DEFAULT);

        } catch (IllegalAccessError e) {
            e.printStackTrace();
        }


    }

    private void onCaptureImageResult_Stop(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        machineStopImageView.setImageBitmap(thumbnail);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        imageString_stop = Base64.encodeToString(byteArray, Base64.DEFAULT);

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult_Stop(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        machineStopImageView.setImageBitmap(bm);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        try {

            byte[] byteArray = byteArrayOutputStream.toByteArray();
            imageString_stop = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (IllegalAccessError e) {
            e.printStackTrace();
        }


    }


    ProgressDialog dialog1;

    private void submitImageStart() {


        String methodeName = "machineRunHourImage";

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, Synchronize.URL + methodeName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String formSubmitResponse) {
                        if (getActivity() instanceof Activity && dialog1 != null) {
                            dialog1.cancel();

                            SharedPreferences preferences_start = getActivity().getSharedPreferences("Start_Image", getActivity().MODE_PRIVATE);

                            preferences_start.edit().clear().apply();

                            submitImageStop();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();

                //   submitImageStart();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                Credential credential = new Credential();
                Cursor credentialCursor = db.getCredential();
                if (credentialCursor.getCount() > 0) {
                    credentialCursor.moveToFirst();
                    credential = new Credential(credentialCursor);
                }
                credentialCursor.close();

                SharedPreferences preferences_start = getActivity().getSharedPreferences("Start_Image", getActivity().MODE_PRIVATE);

                params.put("UserID", credential.getUserId());
                params.put("RunHourID", preferences_start.getString("run_hour_id", null));
                params.put("MachineCode", preferences_start.getString("machine_code", null));
                params.put("ImageName", preferences_start.getString("start_image_name", null));
                params.put("BaseString", preferences_start.getString("start_image", null));
                params.put("Latitude", preferences_start.getString("start_lat", null));
                params.put("Longitude", preferences_start.getString("start_lon", null));
                params.put("DeviceDateTime", preferences_start.getString("start_image_time", null));
                params.put("Imei", credential.getImei());


                for (Map.Entry<String, String> entry : params.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return params;
            }
        };

        stringVarietyRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        dialog1 = ProgressDialog.show(getActivity(), "Submitting Start image",
                "Please wait...", true);

        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }

    ProgressDialog dialog2;

    private void submitImageStop() {

        String methodeName = "machineRunHourImage";

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, Synchronize.URL + methodeName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String formSubmitResponse) {
                        if (getActivity() instanceof Activity && dialog2 != null) {
                            dialog2.cancel();
                        }

                        SharedPreferences preferences_start = getActivity().getSharedPreferences("Stop_Image", getActivity().MODE_PRIVATE);

                        preferences_start.edit().clear().apply();

                        getActivity().finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                //  submitImageStop();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                Credential credential = new Credential();
                Cursor credentialCursor = db.getCredential();
                if (credentialCursor.getCount() > 0) {
                    credentialCursor.moveToFirst();
                    credential = new Credential(credentialCursor);
                }
                credentialCursor.close();

                SharedPreferences preferences_stop = getActivity().getSharedPreferences("Stop_Image", getActivity().MODE_PRIVATE);


                params.put("UserID", credential.getUserId());
                params.put("RunHourID", preferences_stop.getString("run_hour_id", null));
                params.put("MachineCode", preferences_stop.getString("machine_code", null));
                params.put("ImageName", preferences_stop.getString("stop_image_name", null));
                params.put("BaseString", preferences_stop.getString("stop_image", null));
                params.put("Latitude", preferences_stop.getString("stop_lat", null));
                params.put("Longitude", preferences_stop.getString("stop_lon", null));
                params.put("DeviceDateTime", preferences_stop.getString("stop_image_time", null));
                params.put("Imei", credential.getImei());


                for (Map.Entry<String, String> entry : params.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return params;
            }
        };

        stringVarietyRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        dialog2 = ProgressDialog.show(getActivity(), "Submitting Stop image",
                "Please wait...", true);

        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }


}
