package com.wrms.spraymonitor;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wrms.spraymonitor.app.AppController;
import com.wrms.spraymonitor.app.Constents;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.Synchronize;
import com.wrms.spraymonitor.background.UploadSpray;
import com.wrms.spraymonitor.dataobject.PaymentObject;
import com.wrms.spraymonitor.dataobject.SprayMonitorData;
import com.wrms.spraymonitor.utils.Uploadable;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {


    TextView farmerNameTxt;
    TextView farmerContactTxt;
    TextView areaOfCropTxt;
    TextView actualAcreCoveredTxt;
    TextView balanceAmountTxt;

    TextView titleTagText;
    TextView farmerNameTagText;
    TextView farmerContactTagText;
    TextView areaOfCropTagText;
    TextView receivableAmountTagText;
    TextView actualAcreCoveredTagText;
    TextView balanceAmountTagText;
    TextView collectedAmountTagText;
    TextView collectedDateTagText;
    TextView receivingAmountTagText;

    LinearLayout collectedPaymentLinearLayout;
    LayoutInflater innerInflater;

    TextView receivableAmountTxt;
    EditText receivingAmountEdt;
    Button payNowButton;

    SprayMonitorData data;
    PaymentObject paymentData = new PaymentObject();
    DBAdapter db;
    Cursor paymentCursor;
    double balanceAmount = 0.0;

    public static Uploadable sUploadable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        farmerNameTxt = (TextView) findViewById(R.id.farmerNameTxt);
        farmerContactTxt = (TextView) findViewById(R.id.farmerContactTxt);
        areaOfCropTxt = (TextView) findViewById(R.id.areaOfCropTxt);
        collectedPaymentLinearLayout = (LinearLayout) findViewById(R.id.collectedPaymentLinearLayout);
        innerInflater = LayoutInflater.from(this);
        actualAcreCoveredTxt = (TextView) findViewById(R.id.actualAcreCoveredTxt);
        receivableAmountTxt = (TextView) findViewById(R.id.receivableAmountTxt);

        titleTagText = (TextView) findViewById(R.id.titleTagText);
        farmerNameTagText = (TextView) findViewById(R.id.farmerNameTagText);
        farmerContactTagText = (TextView) findViewById(R.id.farmerContactTagText);
        areaOfCropTagText = (TextView) findViewById(R.id.areaOfCropTagText);
        receivableAmountTagText = (TextView) findViewById(R.id.receivableAmountTagText);
        actualAcreCoveredTagText = (TextView) findViewById(R.id.actualAcreCoveredTagText);
        balanceAmountTagText = (TextView) findViewById(R.id.balanceAmountTagText);
        collectedAmountTagText = (TextView) findViewById(R.id.collectedAmountTagText);
        collectedDateTagText = (TextView) findViewById(R.id.collectedDateTagText);
        receivingAmountTagText = (TextView) findViewById(R.id.receivingAmountTagText);

        receivingAmountEdt = (EditText) findViewById(R.id.receivingAmountEdt);
        payNowButton = (Button) findViewById(R.id.payNowButton);
        balanceAmountTxt = (TextView)findViewById(R.id.balanceAmountTxt);

        setSubtitleLanguage();

        data = getIntent().getParcelableExtra(Constents.DATA);

        db = new DBAdapter(PaymentActivity.this);
        db.open();



            farmerNameTxt.setText(data.getFarmerName());
            farmerContactTxt.setText(data.getFarmerContact());
            areaOfCropTxt.setText(data.getTotalAcrage() + " acre");

            actualAcreCoveredTxt.setText(data.getActualAcreCovered() + " acre");
            receivableAmountTxt.setText(data.getAmountReceivable() + " -/");

            paymentCursor = db.getSprayPayment(data.getSprayMonitorId());
            if (paymentCursor.moveToFirst()) {
                do {
                    final View collectedPaymentItem = innerInflater.inflate(R.layout.collected_payment_item, null, false);
                    TextView collectedAmountTxt = (TextView) collectedPaymentItem.findViewById(R.id.collectedAmountTxt);
                    collectedAmountTxt.setText(paymentCursor.getString(paymentCursor.getColumnIndex(DBAdapter.AMOUNT_COLLECTED)));
                    TextView collectionDateTxt = (TextView) collectedPaymentItem.findViewById(R.id.CollectionDateTxt);
                    collectionDateTxt.setText(paymentCursor.getString(paymentCursor.getColumnIndex(DBAdapter.CREATED_DATE_TIME)));
                    collectedPaymentLinearLayout.addView(collectedPaymentItem);
                } while (paymentCursor.moveToNext());

                paymentCursor.moveToLast();
                try {
                    balanceAmount = Double.parseDouble(paymentCursor.getString(paymentCursor.getColumnIndex(DBAdapter.BALANCE_AMOUNT)));
                }catch (NumberFormatException nfe){nfe.printStackTrace();}
                receivingAmountEdt.setText(paymentCursor.getString(paymentCursor.getColumnIndex(DBAdapter.BALANCE_AMOUNT)));
                balanceAmountTxt.setText(paymentCursor.getString(paymentCursor.getColumnIndex(DBAdapter.BALANCE_AMOUNT)));

            }

        receivingAmountEdt.addTextChangedListener(receivingAmountTextWatcher);

        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()) {
                    try {
                        double receivableAmountD = Double.parseDouble(data.getAmountReceivable());
                        paymentCursor.moveToLast();
                        double balanceAmountD = Double.parseDouble(paymentCursor.getString(paymentCursor.getColumnIndex(DBAdapter.BALANCE_AMOUNT)));
                        double collectedAmountD = Double.parseDouble(receivingAmountEdt.getText().toString());
                        paymentData.setSprayMonitoringId(data.getSprayMonitorId());
                        paymentData.setFarmerId(data.getFarmerId());
                        Date date = new Date();
                        String dateString = Constents.sdf.format(date);
                        paymentData.setDateTime(dateString);
                        paymentData.setReceivableAmount(receivableAmountD);
                        paymentData.setCollectedAmount(collectedAmountD);
                        paymentData.setBalanceAmount(balanceAmountD);
                        paymentData.setPaymentId(PaymentObject.createPaymentId());

//                        savePayment(DBAdapter.SUBMIT);
                        paymentRequest();

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(PaymentActivity.this, "Not able to parse amount properly", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });


    }

    private void setSubtitleLanguage(){
        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(this);
        String languagePreference = myPreference.getString(getResources().getString(R.string.language_pref_key), "1");
        int languageConstant = Integer.parseInt(languagePreference);

        System.out.println("language Constant : "+languageConstant);
        switch (languageConstant){
            case 1 :
                setEnglishText();
                break;
            case 2 :
                setHindiText();
                break;
            default:
                setEnglishText();
        }
    }

    private void setEnglishText(){
        Typeface tf = Typeface.DEFAULT;

        titleTagText.setTypeface(tf);
        titleTagText.setText(getResources().getString(R.string.payment_detail_txt));

        farmerNameTagText.setTypeface(tf);
        farmerNameTagText.setText(getResources().getString(R.string.farmer_first_name_txt));

        farmerContactTagText.setTypeface(tf);
        farmerContactTagText.setText(getResources().getString(R.string.farmer_contact_txt));

        areaOfCropTagText.setTypeface(tf);
        areaOfCropTagText.setText(getResources().getString(R.string.total_area_txt));

        actualAcreCoveredTagText.setTypeface(tf);
        actualAcreCoveredTagText.setText(getResources().getString(R.string.actual_acre_covered_txt));

        receivableAmountTagText.setTypeface(tf);
        receivableAmountTagText.setText(getResources().getString(R.string.receivable_amount_txt));

        balanceAmountTagText.setTypeface(tf);
        balanceAmountTagText.setText(getResources().getString(R.string.balance_amount_txt));

        collectedAmountTagText.setTypeface(tf);
        collectedAmountTagText.setText(getResources().getString(R.string.collected_amount_txt));

        collectedDateTagText.setTypeface(tf);
        collectedDateTagText.setText(getResources().getString(R.string.colection_date_txt));

        receivingAmountTagText.setTypeface(tf);
        receivingAmountTagText.setText(getResources().getString(R.string.receiving_amount_txt));

    }

    private void setHindiText(){
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/krdv011.ttf");

        titleTagText.setTypeface(tf);
        titleTagText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        titleTagText.setText("Hkqxrku fooj.k");

        farmerNameTagText.setTypeface(tf);
        farmerNameTagText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        farmerNameTagText.setText("fdlku dk uke");

        farmerContactTagText.setTypeface(tf);
        farmerContactTagText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        farmerContactTagText.setText("fdlku dk e¨cby uÛ");

        areaOfCropTagText.setTypeface(tf);
        areaOfCropTagText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        areaOfCropTagText.setText("dqy ,dM+");

        actualAcreCoveredTagText.setTypeface(tf);
        actualAcreCoveredTagText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        actualAcreCoveredTagText.setText("okLrfod ,dM+");

        receivableAmountTagText.setTypeface(tf);
        receivableAmountTagText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        receivableAmountTagText.setText("dqy jkf'k");

        balanceAmountTagText.setTypeface(tf);
        balanceAmountTagText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        balanceAmountTagText.setText("'ks\"k jkf'k");

        collectedAmountTagText.setTypeface(tf);
        collectedAmountTagText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        collectedAmountTagText.setText("jkf'k ,d=");

        collectedDateTagText.setTypeface(tf);
        collectedDateTagText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        collectedDateTagText.setText("rkjh[k");

        receivingAmountTagText.setTypeface(tf);
        receivingAmountTagText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        receivingAmountTagText.setText(",d= jkf'k");

    }


    private boolean isValid(){
        boolean isValid = true;
        double balanceAmountD = 0.0;
        if(receivingAmountEdt.getText()!=null){
            try{
                balanceAmountD = Double.parseDouble(receivingAmountEdt.getText().toString());
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        if(balanceAmountD == 0.0){
            Toast.makeText(PaymentActivity.this,"Please enter valid balance amount",Toast.LENGTH_SHORT).show();
            return false;
        }


        return isValid;
    }

    ProgressDialog dialog;

    private void paymentRequest() {

        String methodeName = "payments";
        dialog = ProgressDialog.show(PaymentActivity.this, null,
                "Please wait...", true);
        StringRequest paymentRequest = new StringRequest(Request.Method.POST, Synchronize.URL + methodeName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String paymentResponse) {
                        dialog.dismiss();
                        try {
                            System.out.println("Payment Response : " + paymentResponse);
                            JSONObject jsonObject = new JSONObject(paymentResponse);

                            if (jsonObject.has("status")) {
                                if (jsonObject.getString("status").equals("success")) {
                                    savePayment(DBAdapter.SENT);
                                    Toast.makeText(PaymentActivity.this, "Payment Submitted Successfully", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(PaymentActivity.this, TabedActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(PaymentActivity.this, "Registration request has been refused", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(PaymentActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(PaymentActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                        PaymentActivity.this.finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                dialog.dismiss();
                Toast.makeText(PaymentActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
                savePayment(DBAdapter.SUBMIT);
                PaymentActivity.this.finish();
                Intent intent = new Intent(PaymentActivity.this, TabedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("payment_id", paymentData.getPaymentId());
                map.put("farmer_code", paymentData.getFarmerId());
                map.put("spray_monitoring_id", paymentData.getSprayMonitoringId());
                map.put("date_time", paymentData.getDateTime());
                map.put("collected_amount", String.valueOf(paymentData.getCollectedAmount()));

                double nextBalanceAmountD = paymentData.getBalanceAmount() - paymentData.getCollectedAmount();
                if (nextBalanceAmountD == 0.0) {
                    data.setPaymentStatus(DBAdapter.ON_SPOT);
                } else {
                    data.setPaymentStatus(DBAdapter.LATER);
                }
                data.setBalanceAmount(String.valueOf(nextBalanceAmountD));

                map.put("balance_amount", String.valueOf(nextBalanceAmountD));
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        paymentRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(paymentRequest);
    }


    private boolean savePayment(String sendingStatus) {

        double sprayCollectedAmountD = 0.0;
        try {
            sprayCollectedAmountD = Double.parseDouble(data.getAmountCollected());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        System.out.println("sprayCollectedAmountD : "+sprayCollectedAmountD);
        System.out.println("paymentData.getCollectedAmount() : "+paymentData.getCollectedAmount());

        double totalCollectedAmountD = paymentData.getCollectedAmount() + sprayCollectedAmountD;
        System.out.println("totalCollectedAmountD : "+totalCollectedAmountD);
        data.setAmountCollected(String.valueOf(totalCollectedAmountD));

        ContentValues values = new ContentValues();
        values.put(DBAdapter.PAYMENT_ID, paymentData.getPaymentId());
        values.put(DBAdapter.FARMER_ID, data.getFarmerId());
        values.put(DBAdapter.SPRAY_MONITORING_ID, data.getSprayMonitorId());
        values.put(DBAdapter.CREATED_DATE_TIME, paymentData.getDateTime());
        values.put(DBAdapter.SENDING_STATUS, sendingStatus);
        values.put(DBAdapter.AMOUNT_COLLECTED, String.valueOf(paymentData.getCollectedAmount()));
        double nextBalanceAmountD = paymentData.getBalanceAmount() - paymentData.getCollectedAmount();
        values.put(DBAdapter.BALANCE_AMOUNT, String.valueOf(nextBalanceAmountD));

        paymentData.setSendingStatus(sendingStatus);
        paymentData.saveToXML();

        data.setBalanceAmount(String.valueOf(nextBalanceAmountD));
        data.save(db);

        long k = db.db.insert(DBAdapter.TABLE_PAYMENT, null, values);
        if (k != -1) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        paymentCursor.close();
    }

    TextWatcher receivingAmountTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start,int count, int after) { }

        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {

            try {
                double enteredAmount = Double.parseDouble(s.toString());
                if(enteredAmount<=balanceAmount) {
                    paymentData.setCollectedAmount(enteredAmount);
                }else{
                    String valueString = s.toString().substring(0, s.toString().length() - 1);
                    receivingAmountEdt.setText(valueString);
                    Toast.makeText(PaymentActivity.this, "Receiving amount can not be greater than Balance amount", Toast.LENGTH_LONG).show();
                }
            }catch (NumberFormatException e){
                e.printStackTrace();
                if(s.length()>0) {
                    String valueString = s.toString().substring(0, s.toString().length() - 1);
                    receivingAmountEdt.setText(valueString);
                    Toast.makeText(PaymentActivity.this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home: {
                finish();
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}
