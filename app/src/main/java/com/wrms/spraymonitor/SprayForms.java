package com.wrms.spraymonitor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wrms.spraymonitor.app.Constents;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.FormRecyclerAdapter;
import com.wrms.spraymonitor.app.RecyclerItemClickListener;
import com.wrms.spraymonitor.background.AuthenticateService;
import com.wrms.spraymonitor.background.UploadSprayMonitoringData;
import com.wrms.spraymonitor.dataobject.FarmerData;
import com.wrms.spraymonitor.dataobject.ImageData;
import com.wrms.spraymonitor.dataobject.PaymentObject;
import com.wrms.spraymonitor.dataobject.ProductData;
import com.wrms.spraymonitor.dataobject.SprayCropData;
import com.wrms.spraymonitor.dataobject.SprayMonitorData;
import com.wrms.spraymonitor.dataobject.TankFillingData;
import com.wrms.spraymonitor.dataobject.VideoData;
import com.wrms.spraymonitor.utils.Uploadable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SprayForms.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SprayForms#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SprayForms extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SPRAY_MONITOR_LIST = "sprayMonitorList";
    private static final String ARG_PARAM2 = "param2";

    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SprayForms() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sprayMonitoringItemList Parameter 1.
     * @param param2                  Parameter 2.
     * @return A new instance of fragment SprayForms.
     */
    // TODO: Rename and change types and number of parameters
    public static SprayForms newInstance(ArrayList<SprayMonitorData> sprayMonitoringItemList, String param2) {
        SprayForms fragment = new SprayForms();
        Bundle args = new Bundle();
        args.putParcelableArrayList(SPRAY_MONITOR_LIST, sprayMonitoringItemList);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sprayMonitoringItemList = getArguments().getParcelableArrayList(SPRAY_MONITOR_LIST);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.spray_forms, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        formAlert = (TextView) rootView.findViewById(R.id.formAlert);

        return rootView;
    }

    private static final String TAG = "RecyclerViewExample";

    private ArrayList<SprayMonitorData> sprayMonitoringItemList;

    private RecyclerView mRecyclerView;

    private FormRecyclerAdapter adapter;

    ProgressDialog pDialog;
    TextView formAlert;
    DBAdapter db;

    public static Uploadable sUploadable;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        if (sprayMonitoringItemList != null && sprayMonitoringItemList.size() > 0) {
            formAlert.setVisibility(View.INVISIBLE);
        } else {
            formAlert.setVisibility(View.VISIBLE);
            formAlert.setText("No Form");
        }

        db = new DBAdapter(getActivity());
        db.open();

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        SprayMonitorData data = sprayMonitoringItemList.get(position);
                        ArrayList<ProductData> productDataArrayList = new ArrayList<ProductData>();
                        ArrayList<SprayCropData> sprayCropDataArrayList = new ArrayList<SprayCropData>();
                        Cursor sprayCropCursor = db.getSprayCrop(data.getSprayMonitorId());
                        if (sprayCropCursor.moveToFirst()) {
                            do {

                                String sprayCropId = sprayCropCursor.getString(0);
                                SprayCropData sprayCropData = new SprayCropData(sprayCropId, db);
                                sprayCropDataArrayList.add(sprayCropData);

                            } while (sprayCropCursor.moveToNext());
                        }
                        sprayCropCursor.close();

                        ArrayList<TankFillingData> tankFillingDatas = new ArrayList<TankFillingData>();
                        Cursor tankFillingCursor = db.getTankFilling(data.getSprayMonitorId());
                        if (tankFillingCursor.moveToFirst()) {
                            do {

                                String tankFillingId = tankFillingCursor.getString(0);
                                TankFillingData tankFillingData = new TankFillingData(tankFillingId, db);
                                tankFillingDatas.add(tankFillingData);

                                Cursor productDataCursor = db.getSprayedProduct(tankFillingId);
                                if (productDataCursor.moveToFirst()) {
                                    do {
                                        String productDataId = productDataCursor.getString(0);
                                        ProductData productData = new ProductData(productDataId, db);
                                        productDataArrayList.add(productData);

                                    } while (productDataCursor.moveToNext());
                                }
                                productDataCursor.close();

                            } while (tankFillingCursor.moveToNext());
                        }
                        tankFillingCursor.close();

                        if (data.getSendingStatus().equals(DBAdapter.SAVE)) {
                            Intent intent = new Intent(getActivity(), SprayDetailOneActivity.class);
                            intent.putExtra(Constents.DATA, data);
                            intent.putParcelableArrayListExtra(Constents.SPRAY_PRODUCT_DATA, productDataArrayList);
                            intent.putParcelableArrayListExtra(Constents.SPRAY_CROP_DATA, sprayCropDataArrayList);
                            intent.putParcelableArrayListExtra(Constents.SPRAY_TANK_FILLINF_DATA, tankFillingDatas);
                            startActivity(intent);
//                            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                        } else {
//                            SprayMonitorData newData = new SprayMonitorData(data.getSprayMonitorId(), db);
                            Intent intent = new Intent(getActivity(), ShowFormDetailsActivity.class);
                            intent.putExtra(Constents.DATA, data);
                            startActivity(intent);
//                            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                        }
                    }
                })
        );

        System.out.println("(sprayMonitoringItemList!=null) : " + (sprayMonitoringItemList != null));
        if (sprayMonitoringItemList != null) {
            Collections.sort(sprayMonitoringItemList);
        }


        adapter = new FormRecyclerAdapter(getActivity(), sprayMonitoringItemList);
        mRecyclerView.setAdapter(adapter);
        registerForContextMenu(mRecyclerView);

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
//        menu.add(Menu.NONE, R.id.action_delete, Menu.NONE, "DELETE");
        menu.add(Menu.NONE, R.id.action_payment, Menu.NONE, "MAKE PAYMENT");
        menu.add(Menu.NONE, R.id.action_send, Menu.NONE, "SEND");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            int position = -1;
            try {
                position = adapter.getPosition();

            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage(), e);
                return super.onContextItemSelected(item);
            }
            switch (item.getItemId()) {
                case R.id.action_delete:
                    delete(sprayMonitoringItemList.get(position));
                    break;

                case R.id.action_send:
                    try {
                        if (sprayMonitoringItemList.get(position).getSendingStatus().equals(DBAdapter.SUBMIT)) {

                            SprayMonitorData data = sprayMonitoringItemList.get(position);

                            ArrayList<ImageData> images = data.getImages(db);
                            for (ImageData imageData : images) {
                                if (imageData.getSendingStatus().equals(DBAdapter.SENT)) {
                                    images.remove(imageData);
                                }
                            }
                            ArrayList<VideoData> videos = data.getVideos(db);
                            for (VideoData videoData : videos) {
                                if (videoData.getSendingStatus().equals(DBAdapter.SENT)) {
                                    videos.remove(videoData);
                                }
                            }

                            ArrayList<PaymentObject> paymentObjects = data.getPayments(db);
                            for (PaymentObject paymentObject : paymentObjects) {
                                if (paymentObject.getSendingStatus().equals(DBAdapter.SENT)) {
                                    paymentObjects.remove(paymentObject);
                                }
                            }

                        /*ArrayList<SprayMonitorData> sprayMonitorDatas = new ArrayList<SprayMonitorData>();
                        sprayMonitorDatas.add(data);

                        sUploadable = new Uploadable() {
                            @Override
                            public void onSprayFormUploadingSuccess(DBAdapter db, SprayMonitorData cceFormData) {
                                Toast.makeText(getActivity(),"Form is uploaded",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onImageUploadingSuccess(DBAdapter db, ImageData imageData) {
                                Toast.makeText(getActivity(),"Image is uploaded",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onVideoUploadingSuccess(DBAdapter db, VideoData videoData) {
                                Toast.makeText(getActivity(),"Video is uploaded",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPaymentUploadingSuccess(DBAdapter db, PaymentObject weighingData) {
                                Toast.makeText(getActivity(),"Payment is uploaded",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFarmerUploadingSuccess(DBAdapter db, FarmerData audioData) {

                            }

                            @Override
                            public void onFailed() {
                                Toast.makeText(getActivity(),"Could not upload",Toast.LENGTH_SHORT).show();
                            }
                        };

                        new UploadSprayMonitoringData(getActivity(),
                                db,
                                sprayMonitorDatas,
                                images,
                                videos,
                                paymentObjects);*/

                            AuthenticateService.sendSprayMonitoringData(data, images, paymentObjects, videos, db, getActivity());
                        } else {
                            Toast.makeText(getActivity(), "Form is " + sprayMonitoringItemList.get(position).getSendingStatus(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    break;

                case R.id.action_payment:
                    SprayMonitorData sprayMonitorData = sprayMonitoringItemList.get(position);
                    if (sprayMonitorData.getSendingStatus().equals(DBAdapter.SUBMIT) || sprayMonitorData.getSendingStatus().equals(DBAdapter.SENT)) {

                        try {
                            double amountReceivable = 0.0;
                            if (sprayMonitorData.getAmountReceivable() != null && (!sprayMonitorData.getAmountReceivable().isEmpty())) {
                                amountReceivable = Double.parseDouble(sprayMonitorData.getAmountReceivable());
                            }
                            double amountCollected = 0.0;
                            if (sprayMonitorData.getAmountCollected() != null && (!sprayMonitorData.getAmountCollected().isEmpty())) {
                                amountCollected = Double.parseDouble(sprayMonitorData.getAmountCollected());
                            }

                            if (amountReceivable != 0.0) {
                                if (sprayMonitorData.getCollectedBy().toLowerCase().contains("gov") || sprayMonitorData.getCollectedBy().toLowerCase().contains("demo")) {
                                    Toast.makeText(getActivity(), "Payment Received", Toast.LENGTH_SHORT).show();
                                } else if (amountCollected < amountReceivable) {
                                    Intent intent = new Intent(getActivity(), PaymentActivity.class);
                                    intent.putExtra(Constents.DATA, sprayMonitorData);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getActivity(), "Payment Received", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Receivable Amount is 0.0 ", Toast.LENGTH_SHORT).show();
                            }

                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Intent intent = new Intent(getActivity(), PaymentActivity.class);
                            intent.putExtra(Constents.DATA, sprayMonitorData);
                            startActivity(intent);
                        }

                    } else {
                        Toast.makeText(getActivity(), "Form is not submitted", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            return super.onContextItemSelected(item);
        }
        return false;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private boolean delete(final SprayMonitorData sprayData) {

        boolean isDeleted = false;
        String message = "";
        String title = "";
        if (sprayData.getSendingStatus().equals(DBAdapter.SENT) || sprayData.getSendingStatus().equals(DBAdapter.SUBMIT)) {
            title = sprayData.getSendingStatus();
            message = "Can not be deleted";
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title).
                    setMessage(message).
                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            builder.show();

        } else {
            title = "Delete";
            message = "Do you want to delete this spray?";

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title).
                    setMessage(message).
                    setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (sprayData.getSendingStatus().equals(DBAdapter.SUBMIT) || sprayData.getSendingStatus().equals(DBAdapter.SENT)) {
                                Toast.makeText(getActivity(), "Form has been submitted", Toast.LENGTH_SHORT).show();
                            }
                            if (sprayData.getSendingStatus().equals(DBAdapter.SAVE)) {
                                {

                                    String observationDir = Environment
                                            .getExternalStorageDirectory()
                                            + File.separator + Constents.APP_DIR + File.separator
                                            + sprayData.getSprayMonitorId();

                                    File dir0 = new File(observationDir);
                                    System.out.println(observationDir + " EXIST ? " + dir0.exists());
                                    if (dir0.exists()) {
                                        if (dir0.isDirectory()) {

                                            String[] dir1Array = dir0.list();
                                            for (int j = 0; j < dir1Array.length; j++) {

                                                String dir1Path = observationDir + "/" + dir1Array[j];
                                                File dir1 = new File(dir1Path);
                                                System.out.println(dir1Path + " DIR1 IS DIR ? " + dir1.isDirectory());

                                                if (dir1.isDirectory()) {

                                                    String[] dir2Array = dir1.list();
                                                    for (int dir2Count = 0; dir2Count < dir2Array.length; dir2Count++) {

                                                        String dir2Path = dir1Path + "/" + dir2Array[dir2Count];
                                                        File dir2 = new File(dir2Path);
                                                        //System.out.println(dir2Path+ " DIR2 IS DIR ? "+dir2.isDirectory());

                                                        if (dir2.isDirectory()) {

                                                            String[] dir3Array = dir2
                                                                    .list();
                                                            for (int dir3Count = 0; dir3Count < dir3Array.length; dir3Count++) {

                                                                String dir3Path = dir2Path + "/" + dir3Array[dir3Count];
                                                                File dir3 = new File(dir3Path);

                                                                System.out.println(dir3Path + " DIR3 PATH ? ");

                                                                if (dir3.isDirectory()) {

                                                                    String[] dir4Array = dir3
                                                                            .list();
                                                                    for (int dir4Count = 0; dir4Count < dir4Array.length; dir4Count++) {

                                                                        String dir4Path = dir3Path + "/" + dir4Array[dir4Count];
                                                                        File finalFile = new File(dir4Path);

                                                                        System.out.println(dir4Path + " DIR4 PATH ? ");

                                                                        boolean isDeletedInner = finalFile.delete();
                                                                        System.out.println("finalFile IS DELETED " + isDeletedInner);

                                                                    }

                                                                } else {
                                                                    File finalFile = new File(dir3Path);
                                                                    boolean isDeletedInnerSecond = finalFile.delete();
                                                                    System.out.println("finalFile IS DELETED " + isDeletedInnerSecond);
                                                                }

                                                                boolean isDeletedInner = dir3.delete();
                                                                System.out.println("finalFile IS DELETED " + isDeletedInner);

                                                            }

                                                        } else {
                                                            File finalFile = new File(dir2Path);
                                                            boolean isDeletedInnerSecond = finalFile.delete();
                                                            System.out.println("finalFile IS DELETED " + isDeletedInnerSecond);
                                                        }
                                                        dir2.delete();
                                                    }
                                                }
                                                dir1.delete();
                                            }

                                        }
                                        dir0.delete();
                                    }

                                    /*********
                                     * End of Directory deletion from
                                     * External storage
                                     **********/
                                    /***********
                                     * Delete Observation Detail from
                                     * Database
                                     ****************/

                                    db.db.execSQL("delete from "
                                            + DBAdapter.TABLE_SPRAY_MONITORING_FORM
                                            + " where "
                                            + DBAdapter.SPRAY_MONITORING_ID
                                            + " LIKE '%" + sprayData.getSprayMonitorId()
                                            + "%'");
                                    db.db.execSQL("delete from "
                                            + DBAdapter.TABLE_VIDEO
                                            + " where "
                                            + DBAdapter.SPRAY_MONITORING_ID
                                            + " LIKE '%" + sprayData.getSprayMonitorId()
                                            + "%'");
                                    db.db.execSQL("delete from "
                                            + DBAdapter.TABLE_IMAGE
                                            + " where "
                                            + DBAdapter.SPRAY_MONITORING_ID
                                            + " LIKE '%" + sprayData.getSprayMonitorId()
                                            + "%'");

                                    db.db.execSQL("delete from "
                                            + DBAdapter.TABLE_SPRAY_CROPS
                                            + " where "
                                            + DBAdapter.SPRAY_MONITORING_ID
                                            + " LIKE '%" + sprayData.getSprayMonitorId()
                                            + "%'");

                                    Cursor tankFillingCursor = db.getTankFilling(sprayData.getSprayMonitorId());
                                    if (tankFillingCursor.moveToFirst()) {
                                        String tankFillingId = tankFillingCursor.getString(tankFillingCursor.getColumnIndex(DBAdapter.TANK_FILLING_ID));
                                        db.db.execSQL("delete from "
                                                + DBAdapter.TABLE_SPRAYED_PRODUCT
                                                + " where "
                                                + DBAdapter.TANK_FILLING_ID
                                                + " LIKE '%" + tankFillingId
                                                + "%'");
                                    }
                                    tankFillingCursor.close();

                                    db.db.execSQL("delete from "
                                            + DBAdapter.TABLE_TANK_FILLING
                                            + " where "
                                            + DBAdapter.SPRAY_MONITORING_ID
                                            + " LIKE '%" + sprayData.getSprayMonitorId()
                                            + "%'");


                                    /***********
                                     * End of Deletion Observation Detail
                                     * from Database
                                     *******/

                                    sprayMonitoringItemList.remove(sprayData);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            dialogInterface.cancel();
                        }
                    }).
                    setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();

                        }
                    });
            builder.show();

        }

        return isDeleted;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
