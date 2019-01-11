package com.wrms.spraymonitor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.wrms.spraymonitor.app.ContentDataArrayAdapter;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.Synchronize;
import com.wrms.spraymonitor.dataobject.ContentData;
import com.wrms.spraymonitor.dataobject.FuelData;
import com.wrms.spraymonitor.utils.AppManager;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FuelManagerActivity extends AppCompatActivity {

    public static final String FUEL_DATA = "fuel_data";
    public static final SimpleDateFormat FUEL_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    EditText fuelDateEdt;
    AutoCompleteTextView machineIdSpinner;
    EditText morningFuelLevelEdt;
    EditText eveningFuelLevelEdt;
    EditText fuelConsumedEdt;
    RadioGroup dieselFilledRadioGroup;
    RadioButton dieselFilledRadioButton;
    RadioButton noDieselFilledRadioButton;
    LinearLayout dieselFilledLayout;
    EditText billNoEdt;
    EditText billAmountEdt;
    EditText filledDieselEdt;
    RadioGroup isTractorLeasedRadioGroup;
    RadioButton tractorLeasedRadioButton;
    RadioButton tractorNotLeasedRadioButton;
    LinearLayout costPerDayLayout;
    EditText costPerDayEdt;
    EditText remarkEdt;
    LinearLayout confirmedFuelLayout;
    Button addFuelDetailButton;
    Button addFuelButton;

    Button submitButton;
    Button saveButton;
    LayoutInflater layoutInflater;


    TextView dateTxt;
    TextView machineIdTxt;
    TextView dieselLevelInMorning;
    TextView dieselLevelInEvening;
    TextView fuelConsumedTxt;
    TextView dieselFilledTxt;
    TextView fuelFillInstructionTxt;
    TextView billNoTxt;
    TextView billAmountTxt;
    TextView totalDieselFilledTxt;
    TextView isTractorLeasedTxt;
    TextView costPerDayTxt;
    TextView remarkTxt;

    DBAdapter db;
    FuelData fuelData;
    private View mLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feul_manager);

        mLayout = findViewById(R.id.fuel_manager_activity_layout);
        db = new DBAdapter(this);
        db.open();




        fuelDateEdt = (EditText) findViewById(R.id.fuelDateEdt);
        machineIdSpinner = (AutoCompleteTextView) findViewById(R.id.machine_feul);
        morningFuelLevelEdt = (EditText) findViewById(R.id.morningFuelLevelEdt);
        eveningFuelLevelEdt = (EditText) findViewById(R.id.eveningFuelLevelEdt);
        fuelConsumedEdt = (EditText) findViewById(R.id.fuelConsumedEdt);
        fuelConsumedEdt.setEnabled(false);
        dieselFilledRadioGroup = (RadioGroup) findViewById(R.id.dieselFilledRadioGroup);
        dieselFilledRadioButton = (RadioButton) findViewById(R.id.dieselFilledRadioButton);
        noDieselFilledRadioButton = (RadioButton) findViewById(R.id.noDieselFilledRadioButton);
        dieselFilledLayout = (LinearLayout) findViewById(R.id.dieselFilledLayout);
        addFuelDetailButton = (Button) findViewById(R.id.addFuelDetailButton);
        confirmedFuelLayout = (LinearLayout) findViewById(R.id.confirmedFuelLayout);
        billNoEdt = (EditText) findViewById(R.id.billNoEdt);
        billAmountEdt = (EditText) findViewById(R.id.billAmountEdt);
        filledDieselEdt = (EditText) findViewById(R.id.filledDieselEdt);
        addFuelButton = (Button) findViewById(R.id.addFuelButton);
        isTractorLeasedRadioGroup = (RadioGroup) findViewById(R.id.isTractorLeasedRadioGroup);
        tractorLeasedRadioButton = (RadioButton) findViewById(R.id.tractorLeasedRadioButton);
        tractorNotLeasedRadioButton = (RadioButton) findViewById(R.id.tractorNotLeasedRadioButton);
        costPerDayLayout = (LinearLayout) findViewById(R.id.costPerDayLayout);
        costPerDayEdt = (EditText) findViewById(R.id.costPerDayEdt);
        remarkEdt = (EditText) findViewById(R.id.remarkEdt);
        submitButton = (Button) findViewById(R.id.submitButton);
        saveButton = (Button) findViewById(R.id.saveButton);

       // fuelDetailTxt = (TextView) findViewById(R.id.fuelDetailTxt);
        dateTxt = (TextView) findViewById(R.id.dateTxt);
        machineIdTxt = (TextView) findViewById(R.id.machineIdTxt);
        dieselLevelInMorning = (TextView) findViewById(R.id.dieselLevelInMorning);
        dieselLevelInEvening = (TextView) findViewById(R.id.dieselLevelInEvening);
        fuelConsumedTxt = (TextView) findViewById(R.id.fuelConsumedTxt);
        dieselFilledTxt = (TextView) findViewById(R.id.dieselFilledTxt);
        fuelFillInstructionTxt = (TextView) findViewById(R.id.fuelFillInstructionTxt);
        billNoTxt = (TextView) findViewById(R.id.billNoTxt);
        billAmountTxt = (TextView) findViewById(R.id.billAmountTxt);
        totalDieselFilledTxt = (TextView) findViewById(R.id.totalDieselFilledTxt);
        isTractorLeasedTxt = (TextView) findViewById(R.id.isTractorLeasedTxt);
        costPerDayTxt = (TextView) findViewById(R.id.costPerDayTxt);
        remarkTxt = (TextView) findViewById(R.id.remarkTxt);

        layoutInflater = LayoutInflater.from(this);

        setSubtitleLanguage();

        fuelData = getIntent().getParcelableExtra(FUEL_DATA);


        if (fuelData == null) {
            fuelData = new FuelData();
            fuelData.setFuelId(FuelData.createFuelId());
            Date date = new Date();
            fuelData.setCreatedDateTime(Constents.sdf.format(date));
            fuelData.setSendingStatus(DBAdapter.SAVE);
        }

        fuelDateEdt.setEnabled(false);
//        fuelDateEdt.setOnClickListener(selectDateClickListener);
        eveningFuelLevelEdt.addTextChangedListener(eveningFuelTextWatcher);
        morningFuelLevelEdt.addTextChangedListener(morningFuelTextWatcher);
        fuelConsumedEdt.addTextChangedListener(consumedFuelTextWatcher);
        filledDieselEdt.addTextChangedListener(filledFuelTextWatcher);

        dieselFilledRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                if (dieselFilledRadioButton.isChecked()) {

                    if (fuelData.getMorningFuelLevel() != null && fuelData.getMorningFuelLevel().trim().length() > 0) {
                        if (morningFuelLevelEdt.isEnabled()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(FuelManagerActivity.this);
                            builder.setCancelable(false);
                            builder.setMessage("Would you like to save the morning fuel Level?").
                                    setNegativeButton("EDIT", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            morningFuelLevelEdt.requestFocus();
                                            noDieselFilledRadioButton.setChecked(true);
                                            dieselFilledRadioButton.setChecked(false);
                                            dieselFilledLayout.setVisibility(View.GONE);
                                            addFuelDetailButton.setVisibility(View.GONE);
                                            billNoEdt.setText("");
                                            billAmountEdt.setText("");
                                            filledDieselEdt.setText("");
                                        }
                                    }).
                                    setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            morningFuelLevelEdt.setEnabled(false);


                                            Date date = new Date();
                                            String todayDate = FUEL_DATE_TIME_FORMAT.format(date);
                                            if (fuelData.getFuelDate().equals(todayDate)) {
                                                fuelData.setSendingStatus(DBAdapter.SAVE);
                                                if (!fuelData.save(db)) {
                                                    Toast.makeText(FuelManagerActivity.this, "Today's fuel detail has been sent", Toast.LENGTH_LONG).show();
                                                }

                                            }
                                            //                    dieselFilledLayout.setVisibility(View.VISIBLE);
                                            addFuelDetailButton.setVisibility(View.VISIBLE);
                                        }
                                    });
                            builder.show();
                        } else {
//                    dieselFilledLayout.setVisibility(View.VISIBLE);
                            addFuelDetailButton.setVisibility(View.VISIBLE);
                        }

                    } else {

                        Snackbar.make(mLayout, "Please Enter Morning Fuel Level", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        noDieselFilledRadioButton.setChecked(true);
                        dieselFilledRadioButton.setChecked(false);
                        dieselFilledLayout.setVisibility(View.GONE);
                        addFuelDetailButton.setVisibility(View.GONE);
                        billNoEdt.setText("");
                        billAmountEdt.setText("");
                        filledDieselEdt.setText("");

                    }
                } else {
                    dieselFilledLayout.setVisibility(View.GONE);
                    addFuelDetailButton.setVisibility(View.GONE);
                    billNoEdt.setText("");
                    billAmountEdt.setText("");
                    filledDieselEdt.setText("");
                }

            }
        });

        isTractorLeasedRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (tractorLeasedRadioButton.isChecked()) {
                    costPerDayLayout.setVisibility(View.VISIBLE);

                } else {
                    costPerDayLayout.setVisibility(View.GONE);
                    costPerDayEdt.setText("");
                }

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

        ArrayAdapter<String> machineArrayAdapter = new ArrayAdapter<String>(FuelManagerActivity.this, R.layout.auto_complete_textview_item, machineStringArray); //selected item will look like a spinner set from XML
        machineArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        machineIdSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String machineId = "";
                int index = machineStringArray.indexOf(machineIdSpinner.getText().toString());
                System.out.println("Inside MachineId selection " + index);

                if (index > 0) {
                    allMachine.moveToPosition(index);
                    machineId = allMachine.getString(allMachine.getColumnIndex(DBAdapter.MACHINE_ID));
                    String machineName = allMachine.getString(allMachine.getColumnIndex(DBAdapter.MACHINE));
                    Log.v("machinID", machineId + "//" + machineName);
                }
                fuelData.setMachineId(machineId);
            }


        });

        machineIdSpinner.setThreshold(1);
        machineIdSpinner.setAdapter(machineArrayAdapter);


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    Date date = new Date();
                    String todayDate = FUEL_DATE_TIME_FORMAT.format(date);
                    System.out.println("fuelData.getFuelDate().equals(todayDate) : " + (fuelData.getFuelDate().equals(todayDate)));
                    if (fuelData.getFuelDate().equals(todayDate)) {
                        Cursor fuelCursor = db.getFuelByDate(todayDate);
                        if (fuelCursor.moveToLast() && fuelCursor.getString(fuelCursor.getColumnIndex(DBAdapter.SENDING_STATUS)).equals(DBAdapter.SENT)) {
                            Snackbar.make(mLayout, "Today's fuel detail has been sent", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            int hours = calendar.get(Calendar.HOUR_OF_DAY);
                            System.out.println("HOURSE OF THE DAY : " + hours);
                            if (hours < 17) {
                                Snackbar.make(mLayout, "Form can be submitted before 5PM ", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            } else {
                                submitFuelDetail(fuelData);
                            }
                        }
                        fuelCursor.close();
                    } else {
                        submitFuelDetail(fuelData);
                    }
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFuelData();
                if (morningFuelLevelEdt.isEnabled()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FuelManagerActivity.this);
                    builder.setCancelable(false);
                    builder.setMessage("Would you like to save the morning fuel Level?").
                            setNegativeButton("EDIT", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    morningFuelLevelEdt.requestFocus();
                                }
                            }).
                            setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    morningFuelLevelEdt.setEnabled(false);
                                    Date date = new Date();
                                    String todayDate = FUEL_DATE_TIME_FORMAT.format(date);
                                    if (fuelData.getFuelDate().equals(todayDate)) {
                                        fuelData.setSendingStatus(DBAdapter.SAVE);
                                        if (!fuelData.save(db)) {
                                            Toast.makeText(FuelManagerActivity.this, "Today's fuel detail has been sent", Toast.LENGTH_LONG).show();
                                        }
                                        ;
                                    }
                                    FuelManagerActivity.this.finish();
                                }
                            });
                    builder.show();
                } else {
                    Date date = new Date();
                    String todayDate = FUEL_DATE_TIME_FORMAT.format(date);
                    if (fuelData.getFuelDate().equals(todayDate)) {
                        fuelData.setSendingStatus(DBAdapter.SAVE);
                        if (!fuelData.save(db)) {
                            Toast.makeText(FuelManagerActivity.this, "Today's fuel detail has been sent", Toast.LENGTH_LONG).show();
                        }
                        ;
                    }
                    FuelManagerActivity.this.finish();
                }

            }
        });

        addFuelDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dieselFilledLayout.setVisibility(View.VISIBLE);
                addFuelDetailButton.setVisibility(View.GONE);
            }
        });
        addFuelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int filledFuelCount = 0;
                if (fuelData.getBillNo() != null && fuelData.getBillNo().trim().length() > 0) {
                    if (fuelData.getBillNo1() != null && fuelData.getBillNo1().trim().length() > 0) {
                        if (fuelData.getBillNo2() != null && fuelData.getBillNo2().trim().length() > 0) {
                            Toast.makeText(FuelManagerActivity.this, "Fuel has been filled three times", Toast.LENGTH_SHORT).show();
                        } else {
                            filledFuelCount = 2;
                        }
                    } else {
                        filledFuelCount = 1;
                    }
                }
                final int finalFilledFuelCount = filledFuelCount;
                if (isValidFilledFuelDetail(finalFilledFuelCount)) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(FuelManagerActivity.this);
                    builder.setMessage("Would you like to save the filled fuel Detail?").
                            setNegativeButton("EDIT", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).
                            setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    addDynamicFilledFuelHeader();
                                    Toast.makeText(FuelManagerActivity.this, "Fuel Detail Added", Toast.LENGTH_SHORT).show();
                                    final View confirmedProduct = layoutInflater.inflate(R.layout.confirmed_product_item, null, false);
                                    TextView innerProductName = (TextView) confirmedProduct.findViewById(R.id.productName);
                                    innerProductName.setText(billNoEdt.getText().toString());
                                    TextView innerProductUOM = (TextView) confirmedProduct.findViewById(R.id.productUOM);
                                    innerProductUOM.setText(billAmountEdt.getText().toString());
                                    TextView innerProductQty = (TextView) confirmedProduct.findViewById(R.id.productQty);
                                    innerProductQty.setText(String.valueOf(filledDieselEdt.getText()));

                                    double morningFuel = 0.0;
                                    double filledFuel = 0.0;
                                    try {
                                        morningFuel = Double.parseDouble(fuelData.getMorningFuelLevel());
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        filledFuel = Double.parseDouble(filledDieselEdt.getText().toString());
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }

                                   // double totalMorningFuel = morningFuel + filledFuel;
                                    double totalMorningFuel = morningFuel;
                                    morningFuelLevelEdt.setText(String.valueOf(totalMorningFuel));
                                    fuelData.setMorningFuelLevel(String.valueOf(totalMorningFuel));

                                    double eveningFuel = 0.0;
                                    try {
                                        eveningFuel = Double.parseDouble(fuelData.getEveningFuelLevel());
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }

                                    double consumedFuel = totalMorningFuel - eveningFuel;
                                    fuelConsumedEdt.setText(String.valueOf(consumedFuel));
                                    fuelData.setConsumedFuel(String.valueOf(consumedFuel));

                                    ImageButton remove = (ImageButton) confirmedProduct.findViewById(R.id.remove);
                                    remove.setVisibility(View.GONE);

                                    confirmedFuelLayout.addView(confirmedProduct);
                                    billNoEdt.setText("");
                                    billAmountEdt.setText("");
                                    filledDieselEdt.setText("");
                                    if (finalFilledFuelCount < 2) {
                                        addFuelDetailButton.setVisibility(View.VISIBLE);
                                    }
                                    dieselFilledLayout.setVisibility(View.GONE);
                                    noDieselFilledRadioButton.setEnabled(false);

                                }
                            });
                    builder.show();


                }

            }
        });


        setDefaultData();

        AppManager.isGPSenabled(this);
    }

    private void setSubtitleLanguage() {
        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(this);
        String languagePreference = myPreference.getString(getResources().getString(R.string.language_pref_key), "1");
        int languageConstant = Integer.parseInt(languagePreference);

        System.out.println("language Constant : " + languageConstant);
        switch (languageConstant) {
            case 1:
                setEnglishText();
                break;
            case 2:
                setHindiText();
                break;
            default:
                setEnglishText();
        }
    }

    private void setEnglishText() {
        Typeface tf = Typeface.DEFAULT;

       /* fuelDetailTxt.setTypeface(tf);
        fuelDetailTxt.setText(getResources().getString(R.string.fuel_detail_txt));
*/
        dateTxt.setTypeface(tf);
        dateTxt.setText(getResources().getString(R.string.date_txt));

        machineIdTxt.setTypeface(tf);
        machineIdTxt.setText(getResources().getString(R.string.machine_id_txt));

        dieselLevelInMorning.setTypeface(tf);
        dieselLevelInMorning.setText(getResources().getString(R.string.diesel_level_in_morning_txt));

        dieselLevelInEvening.setTypeface(tf);
        dieselLevelInEvening.setText(getResources().getString(R.string.diesel_level_in_evening_txt));

        fuelConsumedTxt.setTypeface(tf);
        fuelConsumedTxt.setText(getResources().getString(R.string.fuel_consumed_txt));

        dieselFilledTxt.setTypeface(tf);
        dieselFilledTxt.setText(getResources().getString(R.string.diesel_filled_txt));

        fuelFillInstructionTxt.setTypeface(tf);
        fuelFillInstructionTxt.setText(getResources().getString(R.string.fuel_filled_instruction_txt));

        billNoTxt.setTypeface(tf);
        billNoTxt.setText(getResources().getString(R.string.bill_no_txt));

        billAmountTxt.setTypeface(tf);
        billAmountTxt.setText(getResources().getString(R.string.bill_amount_txt));

        totalDieselFilledTxt.setTypeface(tf);
        totalDieselFilledTxt.setText(getResources().getString(R.string.total_diesel_filled_txt));

        isTractorLeasedTxt.setTypeface(tf);
        isTractorLeasedTxt.setText(getResources().getString(R.string.is_tractor_leases_txt));

        costPerDayTxt.setTypeface(tf);
        costPerDayTxt.setText(getResources().getString(R.string.cost_per_day_txt));

        remarkTxt.setTypeface(tf);
        remarkTxt.setText(getResources().getString(R.string.remark_txt));

    }

    private void setHindiText() {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/krdv011.ttf");

        /*fuelDetailTxt.setTypeface(tf);
        fuelDetailTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        fuelDetailTxt.setText("ÃaaÄu dk fooju");*/

        dateTxt.setTypeface(tf);
        dateTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        dateTxt.setText("rkjh[k");

        machineIdTxt.setTypeface(tf);
        machineIdTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        machineIdTxt.setText("e'khu dk uke");

        dieselLevelInMorning.setTypeface(tf);
        dieselLevelInMorning.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        dieselLevelInMorning.setText("lqcg esa Mhty Lrj");

        dieselLevelInEvening.setTypeface(tf);
        dieselLevelInEvening.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        dieselLevelInEvening.setText("'kke dks Mhty Lrj");

        fuelConsumedTxt.setTypeface(tf);
        fuelConsumedTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        fuelConsumedTxt.setText("bZa/ku [kir ¼yhVj½");

        dieselFilledTxt.setTypeface(tf);
        dieselFilledTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        dieselFilledTxt.setText("Mhty Hkjk");

        fuelFillInstructionTxt.setTypeface(tf);
        fuelFillInstructionTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        fuelFillInstructionTxt.setText("ÃaaÄu 3 ckj rd Òjk tk ldrk gS");

        billNoTxt.setTypeface(tf);
        billNoTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        billNoTxt.setText("fcy la[;k");

        billAmountTxt.setTypeface(tf);
        billAmountTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        billAmountTxt.setText("fcy jkf'k¼:½");

        totalDieselFilledTxt.setTypeface(tf);
        totalDieselFilledTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        totalDieselFilledTxt.setText("dqy Mhty Òjk ¼yhVj½");

        isTractorLeasedTxt.setTypeface(tf);
        isTractorLeasedTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        isTractorLeasedTxt.setText("D;k VªSDVj fdjk;s ij gS");

        costPerDayTxt.setTypeface(tf);
        costPerDayTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        costPerDayTxt.setText("çfr fnu dh ykxr¼:½");

        remarkTxt.setTypeface(tf);
        remarkTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        remarkTxt.setText("fVIi.kh");


    }

    private void addDynamicLabel(TextView titleTxt,
                                 TextView billNoTxt,
                                 TextView billAmountTxt,
                                 TextView filledFuelTxt) {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/krdv011.ttf");

        if (titleTxt != null) {
            titleTxt.setTypeface(tf);
            titleTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            titleTxt.setText("Òjk ÃaaÄu");
        }

        billNoTxt.setTypeface(tf);
        billNoTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        billNoTxt.setText("fcy la[;k");

        billAmountTxt.setTypeface(tf);
        billAmountTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        billAmountTxt.setText("fcy jkf'k¼:½");

        filledFuelTxt.setTypeface(tf);
        filledFuelTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        filledFuelTxt.setText("Mhty ¼yhVj½");

    }


    private void setDefaultData() {

        System.out.println("Fuel Date : " + fuelData.getFuelDate());

        if (fuelData.getFuelDate() != null && fuelData.getFuelDate().trim().length() > 0) {
            fuelDateEdt.setText(fuelData.getFuelDate());
        } else {
            Date date = new Date();
            String dateString = FUEL_DATE_TIME_FORMAT.format(date);
            fuelDateEdt.setText(dateString);
        }

        /*if (fuelData.getMachineId() != "-1") {
            Cursor getAllMachine = db.getAllMachine();
            int machineCount = getAllMachine.getCount();
            System.out.println("machineId : " + fuelData.getMachineId() + " machineCount : " + machineCount);
            if (machineCount > 0 && (!fuelData.getMachineId().equals("-1"))) {
                getAllMachine.moveToFirst();
                for (int d = 1; d <= machineCount; d++) {
                    if (fuelData.getMachineId().trim().equals(getAllMachine.getString(getAllMachine.getColumnIndex(DBAdapter.MACHINE_ID)))) {
                        machineIdSpinner.setSelection(d, true);
                        break;
                    }
                    getAllMachine.moveToNext();
                }
            }
        }*/

        if (fuelData.getMachineId() != null && (!fuelData.getMachineId().equals("-1"))) {
            machineIdSpinner.setText(db.getMachineNameById(fuelData.getMachineId()));
            /*Cursor getAllMachine = db.getAllMachine();
            int machineCount = getAllMachine.getCount();
            System.out.println("machineId : " + data.getMachineId() + " machineCount : " + machineCount);
            if (machineCount > 0 && (!data.getMachineId().equals("-1"))) {
                getAllMachine.moveToFirst();
                for (int d = 1; d <= machineCount; d++) {
                    if (data.getMachineId().trim().equals(getAllMachine.getString(getAllMachine.getColumnIndex(DBAdapter.MACHINE_ID)))) {

                        break;
                    }
                    getAllMachine.moveToNext();
                }
            }*/
        }

        if (fuelData.getMorningFuelLevel() != null && fuelData.getMorningFuelLevel().trim().length() > 0) {
            morningFuelLevelEdt.setText(fuelData.getMorningFuelLevel());
            morningFuelLevelEdt.setEnabled(false);
        }
        eveningFuelLevelEdt.setText(fuelData.getEveningFuelLevel());
        fuelConsumedEdt.setText(fuelData.getConsumedFuel());


        if (fuelData.getDieselFilled() != null && fuelData.getDieselFilled().equals("1")) {
            dieselFilledRadioButton.setChecked(true);
            noDieselFilledRadioButton.setChecked(false);
//            dieselFilledLayout.setVisibility(View.VISIBLE);

            addDynamicFilledFuelHeader();

            if (fuelData.getBillNo() != null && fuelData.getBillNo().trim().length() > 0) {
                final View confirmedProduct = layoutInflater.inflate(R.layout.confirmed_product_item, null, false);
                TextView innerProductName = (TextView) confirmedProduct.findViewById(R.id.productName);
                innerProductName.setText(fuelData.getBillNo());
                TextView innerProductUOM = (TextView) confirmedProduct.findViewById(R.id.productUOM);
                innerProductUOM.setText(fuelData.getBillAmount());
                TextView innerProductQty = (TextView) confirmedProduct.findViewById(R.id.productQty);
                innerProductQty.setText(String.valueOf(fuelData.getFilledDieselAmount()));

                ImageButton remove = (ImageButton) confirmedProduct.findViewById(R.id.remove);
                remove.setVisibility(View.GONE);

                confirmedFuelLayout.addView(confirmedProduct);
            }

            if (fuelData.getBillNo1() != null && fuelData.getBillNo1().trim().length() > 0) {
                final View confirmedProduct = layoutInflater.inflate(R.layout.confirmed_product_item, null, false);
                TextView innerProductName = (TextView) confirmedProduct.findViewById(R.id.productName);
                innerProductName.setText(fuelData.getBillNo1());
                TextView innerProductUOM = (TextView) confirmedProduct.findViewById(R.id.productUOM);
                innerProductUOM.setText(fuelData.getBillAmount1());
                TextView innerProductQty = (TextView) confirmedProduct.findViewById(R.id.productQty);
                innerProductQty.setText(String.valueOf(fuelData.getFilledDieselAmount1()));

                ImageButton remove = (ImageButton) confirmedProduct.findViewById(R.id.remove);
                remove.setVisibility(View.GONE);

                confirmedFuelLayout.addView(confirmedProduct);
            }

            if (fuelData.getBillNo2() != null && fuelData.getBillNo2().trim().length() > 0) {
                addFuelDetailButton.setVisibility(View.GONE);

                final View confirmedProduct = layoutInflater.inflate(R.layout.confirmed_product_item, null, false);
                TextView innerProductName = (TextView) confirmedProduct.findViewById(R.id.productName);
                innerProductName.setText(fuelData.getBillNo2());
                TextView innerProductUOM = (TextView) confirmedProduct.findViewById(R.id.productUOM);
                innerProductUOM.setText(fuelData.getBillAmount2());
                TextView innerProductQty = (TextView) confirmedProduct.findViewById(R.id.productQty);
                innerProductQty.setText(String.valueOf(fuelData.getFilledDieselAmount2()));

                ImageButton remove = (ImageButton) confirmedProduct.findViewById(R.id.remove);
                remove.setVisibility(View.GONE);

                confirmedFuelLayout.addView(confirmedProduct);

            } else {
                addFuelDetailButton.setVisibility(View.VISIBLE);
            }


            billNoEdt.setText("");
            billAmountEdt.setText("");
            filledDieselEdt.setText("");

            dieselFilledLayout.setVisibility(View.GONE);
            noDieselFilledRadioButton.setEnabled(false);


            /*billNoEdt.setText(fuelData.getBillNo());
            billAmountEdt.setText(fuelData.getBillAmount());
            filledDieselEdt.setText(fuelData.getFilledDieselAmount());*/

        } else {
            dieselFilledRadioButton.setChecked(false);
            noDieselFilledRadioButton.setChecked(true);
//            dieselFilledLayout.setVisibility(View.GONE);
            addFuelDetailButton.setVisibility(View.GONE);
        }


        if (fuelData.getTractorLeased() != null && fuelData.getTractorLeased().equals("1")) {
            tractorLeasedRadioButton.setChecked(true);
            tractorNotLeasedRadioButton.setChecked(false);
            costPerDayLayout.setVisibility(View.VISIBLE);
            costPerDayEdt.setText(fuelData.getCostPerDay());
        } else {
            tractorLeasedRadioButton.setChecked(false);
            tractorNotLeasedRadioButton.setChecked(true);
            costPerDayLayout.setVisibility(View.GONE);
        }

        remarkEdt.setText(fuelData.getRemark());
    }

    ProgressDialog dialog;

    private void submitFuelDetail(final FuelData data) {

        String methodeName = "fuelDetails";

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, Synchronize.URL + methodeName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String fuelSubmissionResponse) {
                        dialog.dismiss();
                        try {
                            System.out.println("Fuel Submission Response : " + fuelSubmissionResponse);
                            JSONObject jsonObject = new JSONObject(fuelSubmissionResponse);

                            if (jsonObject.has("status")) {
                                if (jsonObject.getString("status").equals("success") || fuelSubmissionResponse.contains("AlreadyExist")) {
                                    Toast.makeText(FuelManagerActivity.this, "Fuel detail has been submitted", Toast.LENGTH_LONG).show();
                                    data.setSendingStatus(DBAdapter.SENT);
                                    data.save(db);
                                    FuelManagerActivity.this.finish();

                                } else {
                                    Toast.makeText(FuelManagerActivity.this, "Fuel detail submission request has been refused", Toast.LENGTH_LONG).show();
                                    Date date = new Date();
                                    String todayDate = FUEL_DATE_TIME_FORMAT.format(date);
                                    if (fuelData.getFuelDate().equals(todayDate)) {
                                        data.setSendingStatus(DBAdapter.SAVE);
                                        data.save(db);
                                    }
                                }
                            } else {
                                Toast.makeText(FuelManagerActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(FuelManagerActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();
                            notSubmittedAlert(data);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                dialog.cancel();
                Toast.makeText(FuelManagerActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
                notSubmittedAlert(data);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                System.out.println("Get Params has been called");
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

        dialog = ProgressDialog.show(FuelManagerActivity.this, "Submitting Detail",
                "Please wait...", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);
    }

    @Override
    public void onBackPressed() {
        setFuelData();
        Date date = new Date();
        String todayDate = FUEL_DATE_TIME_FORMAT.format(date);
        if (fuelData.getFuelDate().equals(todayDate)) {
            setFuelData();
            fuelData.save(db);
            FuelManagerActivity.this.finish();
        } else {
            super.onBackPressed();
        }
    }

    private void notSubmittedAlert(final FuelData data) {
        Date date = new Date();
        String todayDate = FUEL_DATE_TIME_FORMAT.format(date);
        if (data.getFuelDate().equals(todayDate)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(FuelManagerActivity.this);
            builder.setTitle("Not Submitted").
                    setMessage("Do you want to save the details for the day?").
                    setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            setFuelData();
                            data.setSendingStatus(DBAdapter.SUBMIT);
                            data.save(db);
                            FuelManagerActivity.this.finish();
                        }
                    }).
                    setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            FuelManagerActivity.this.finish();
                        }
                    });
            builder.show();
        }
    }

    private void setFuelData() {

        if (fuelDateEdt.getText() != null && fuelDateEdt.getText().toString().trim().length() > 0) {
            fuelData.setFuelDate(fuelDateEdt.getText().toString());
        }

        if (morningFuelLevelEdt.getText() != null && morningFuelLevelEdt.getText().toString().trim().length() > 0) {
            fuelData.setMorningFuelLevel(morningFuelLevelEdt.getText().toString());
        }

        if (eveningFuelLevelEdt.getText() != null && eveningFuelLevelEdt.getText().toString().trim().length() > 0) {
            fuelData.setEveningFuelLevel(eveningFuelLevelEdt.getText().toString());
        }

        if (fuelConsumedEdt.getText() != null && fuelConsumedEdt.getText().toString().trim().length() > 0) {
            fuelData.setConsumedFuel(fuelConsumedEdt.getText().toString());
        }

        if (dieselFilledRadioButton.isChecked()) {
            fuelData.setDieselFilled("1");
            /*if (billNoEdt.getText() != null && billNoEdt.getText().toString().trim().length() > 0) {
                fuelData.setBillNo(billNoEdt.getText().toString());
            }
            if (billAmountEdt.getText() != null && billAmountEdt.getText().toString().trim().length() > 0) {
                fuelData.setBillAmount(billAmountEdt.getText().toString());
            }
            if (filledDieselEdt.getText() != null && filledDieselEdt.getText().toString().trim().length() > 0) {
                fuelData.setFilledDieselAmount(filledDieselEdt.getText().toString());
            }*/
        } else {
            fuelData.setDieselFilled("0");
        }

        if (tractorLeasedRadioButton.isChecked()) {
            fuelData.setTractorLeased("1");

            if (costPerDayEdt.getText() != null && costPerDayEdt.getText().toString().trim().length() > 0) {
                fuelData.setCostPerDay(costPerDayEdt.getText().toString());
            }

        } else {
            fuelData.setTractorLeased("0");
        }

        fuelData.setRemark(remarkEdt.getText().toString());


    }

    private boolean isValidFilledFuelDetail(int filledFuelCount) {
        boolean result = true;

        if (dieselFilledRadioButton.isChecked()) {
            fuelData.setDieselFilled("1");

            if (billNoEdt.getText() != null && billNoEdt.getText().toString().trim().length() > 0) {

            } else {
                Toast.makeText(FuelManagerActivity.this, "Please enter bill number", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (billAmountEdt.getText() != null && billAmountEdt.getText().toString().trim().length() > 0) {

            } else {
                Toast.makeText(FuelManagerActivity.this, "Please enter bill amount", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (filledDieselEdt.getText() != null && filledDieselEdt.getText().toString().trim().length() > 0) {

            } else {
                Toast.makeText(FuelManagerActivity.this, "Please Filled Fuel Amount", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (filledFuelCount == 0) {
                fuelData.setBillNo(billNoEdt.getText().toString());
                fuelData.setBillAmount(billAmountEdt.getText().toString());
                fuelData.setFilledDieselAmount(filledDieselEdt.getText().toString());
            }
            if (filledFuelCount == 1) {
                fuelData.setBillNo1(billNoEdt.getText().toString());
                fuelData.setBillAmount1(billAmountEdt.getText().toString());
                fuelData.setFilledDieselAmount1(filledDieselEdt.getText().toString());
            }
            if (filledFuelCount == 2) {
                fuelData.setBillNo2(billNoEdt.getText().toString());
                fuelData.setBillAmount2(billAmountEdt.getText().toString());
                fuelData.setFilledDieselAmount2(filledDieselEdt.getText().toString());
            }

        } else {
            Snackbar.make(mLayout, "Is fuel filled is not selected", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            fuelData.setDieselFilled("0");
            return false;
        }

        return result;
    }

    private boolean isValid() {
        boolean result = true;

        if (fuelDateEdt.getText() != null && fuelDateEdt.getText().toString().trim().length() > 0) {
            fuelData.setFuelDate(fuelDateEdt.getText().toString());
        } else {
            Toast.makeText(FuelManagerActivity.this, "Please Select Fuel Date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (morningFuelLevelEdt.getText() != null && morningFuelLevelEdt.getText().toString().trim().length() > 0) {
            fuelData.setMorningFuelLevel(morningFuelLevelEdt.getText().toString());
        } else {
            Toast.makeText(FuelManagerActivity.this, "Please enter fuel level in the morning", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (eveningFuelLevelEdt.getText() != null && eveningFuelLevelEdt.getText().toString().trim().length() > 0) {
            fuelData.setEveningFuelLevel(eveningFuelLevelEdt.getText().toString());
        } else {
            Toast.makeText(FuelManagerActivity.this, "Please enter fuel level in the evening", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fuelConsumedEdt.getText() != null && fuelConsumedEdt.getText().toString().trim().length() > 0) {
            fuelData.setConsumedFuel(fuelConsumedEdt.getText().toString());
        } else {
            Toast.makeText(FuelManagerActivity.this, "Please enter consumed fuel", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fuelData.getMachineId() != null && fuelData.getMachineId().trim().length() > 0) {

        } else {
            Toast.makeText(FuelManagerActivity.this, "Please select machine", Toast.LENGTH_SHORT).show();
            return false;
        }

        double totalFuel = 0.0;
        double morningFuel = 0.0;
        double filledFuel = 0.0;
        double eveningFuel = 0.0;
        double consumedFuel = 0.0;

        if (dieselFilledRadioButton.isChecked()) {
            fuelData.setDieselFilled("1");

            if (!(fuelData.getBillNo() != null && fuelData.getBillNo().trim().length() > 0)) {
                Toast.makeText(FuelManagerActivity.this, "Please enter bill number", Toast.LENGTH_SHORT).show();
                return false;
            }


            if (!(fuelData.getBillAmount() != null && fuelData.getBillAmount().trim().length() > 0)) {
                Toast.makeText(FuelManagerActivity.this, "Please enter bill amount", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (!(fuelData.getFilledDieselAmount() != null && fuelData.getFilledDieselAmount().trim().length() > 0)) {
                Toast.makeText(FuelManagerActivity.this, "Please enter filled fuel amount", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                try {
                    System.out.println("filledFuel " + filledFuel + " Double.parseDouble(fuelData.getFilledDieselAmount()) " + Double.parseDouble(fuelData.getFilledDieselAmount()));
                    filledFuel = filledFuel + Double.parseDouble(fuelData.getFilledDieselAmount());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            if (fuelData.getFilledDieselAmount1() != null && fuelData.getFilledDieselAmount1().trim().length() > 0) {
                try {
                    filledFuel = filledFuel + Double.parseDouble(fuelData.getFilledDieselAmount1());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            if (fuelData.getFilledDieselAmount2() != null && fuelData.getFilledDieselAmount2().trim().length() > 0) {
                try {
                    filledFuel = filledFuel + Double.parseDouble(fuelData.getFilledDieselAmount2());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }


        } else {
            fuelData.setDieselFilled("0");
        }


        try {
            morningFuel = Double.parseDouble(fuelData.getMorningFuelLevel());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        System.out.println("morningFuel " + morningFuel + " filledFuel " + filledFuel);
        totalFuel = morningFuel + filledFuel;


        try {
            eveningFuel = Double.parseDouble(fuelData.getEveningFuelLevel());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            consumedFuel = Double.parseDouble(fuelData.getConsumedFuel());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (eveningFuel > morningFuel) {
            Snackbar.make(mLayout, "(Evening fuel level can not be greater than morning fuel level)", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            return false;
        }


        double total = eveningFuel + consumedFuel;

        System.out.println("total " + total + " totalFuel " + totalFuel + " (total != totalFuel) " + (total != totalFuel));

        if (total != morningFuel) {
//                    Toast.makeText(FuelManagerActivity.this,"Invalid Fuel Data",Toast.LENGTH_SHORT).show();

            Snackbar.make(mLayout, "Initial Fuel is not equal to (Consumed Fuel + Left Fuel)", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            return false;
        }

        if (tractorLeasedRadioButton.isChecked()) {
            fuelData.setTractorLeased("1");

            if (costPerDayEdt.getText() != null && costPerDayEdt.getText().toString().trim().length() > 0) {
                fuelData.setCostPerDay(costPerDayEdt.getText().toString());
            } else {
                Toast.makeText(FuelManagerActivity.this, "Please enter per day cost of tractor", Toast.LENGTH_SHORT).show();
                return false;
            }

        } else {
            fuelData.setTractorLeased("0");
        }



        fuelData.setRemark(remarkEdt.getText().toString());

        return result;
    }

    private void addDynamicFilledFuelHeader() {
        if (confirmedFuelLayout.getChildCount() == 0) {

            final View confirmedFuelHeading = layoutInflater.inflate(R.layout.confirmed_product_item, null, false);
            TextView titleTxt = (TextView) confirmedFuelHeading.findViewById(R.id.productName);
            titleTxt.setText("Added Fuel Details");
            titleTxt.setTypeface(Typeface.DEFAULT_BOLD);
            titleTxt.setTextColor(getResources().getColor(R.color.color_primary_dark));
            TextView productUOM0 = (TextView) confirmedFuelHeading.findViewById(R.id.productUOM);
            productUOM0.setVisibility(View.GONE);
            TextView productQty0 = (TextView) confirmedFuelHeading.findViewById(R.id.productQty);
            productQty0.setVisibility(View.GONE);
            ImageButton remove0 = (ImageButton) confirmedFuelHeading.findViewById(R.id.remove);
            remove0.setVisibility(View.GONE);

            confirmedFuelLayout.addView(confirmedFuelHeading);

            final View confirmedFuelLables = layoutInflater.inflate(R.layout.confirmed_product_item, null, false);
            TextView billNoTxt = (TextView) confirmedFuelLables.findViewById(R.id.productName);
            billNoTxt.setText("Bill No.");
            billNoTxt.setTypeface(Typeface.DEFAULT_BOLD);
            TextView billAmountTxt = (TextView) confirmedFuelLables.findViewById(R.id.productUOM);
            billAmountTxt.setText("Bill Amount");
            billAmountTxt.setTypeface(Typeface.DEFAULT_BOLD);
            TextView filledFuelTxt = (TextView) confirmedFuelLables.findViewById(R.id.productQty);
            filledFuelTxt.setText("Filled Fuel Quantity");
            filledFuelTxt.setTypeface(Typeface.DEFAULT_BOLD);
            ImageButton remove = (ImageButton) confirmedFuelLables.findViewById(R.id.remove);
            remove.setVisibility(View.GONE);

            confirmedFuelLayout.addView(confirmedFuelLables);

            addDynamicLabel(titleTxt, billNoTxt, billAmountTxt, filledFuelTxt);
        }

    }

    Button.OnClickListener selectDateClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View viewOnClick) {
            final AlertDialog.Builder adb = new AlertDialog.Builder(FuelManagerActivity.this);
            final View view = LayoutInflater.from(FuelManagerActivity.this).inflate(R.layout.date_picker, null);
            adb.setView(view);
            final Dialog dialog;
            adb.setPositiveButton("Add", new android.content.DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {

                    DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker1);
                    java.util.Date date = null;
                    Calendar cal = GregorianCalendar.getInstance();
                    cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                    date = cal.getTime();
                    String selectedDate = FUEL_DATE_TIME_FORMAT.format(date);

                    Date currentDate = new Date();
                    String currentDateString = FUEL_DATE_TIME_FORMAT.format(currentDate);

                    long diff = date.getTime() - currentDate.getTime();
                    System.out.println("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

                    if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) <= 0) {
                        fuelDateEdt.setText(selectedDate);
                        fuelData.setFuelDate(selectedDate);
                    } else {
                        fuelDateEdt.setText(currentDateString);
                        fuelData.setFuelDate(currentDateString);
                        Snackbar.make(mLayout, "Fuel Date can not be in future", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });
            dialog = adb.create();
            dialog.show();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.saved_fuel_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_saved: {
                this.finish();
                Intent intent = new Intent(FuelManagerActivity.this, SavedFuelActivity.class);
                startActivity(intent);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    TextWatcher eveningFuelTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(final CharSequence s, int start,
                                  int before, int count) {
            fuelData.setEveningFuelLevel(s.toString());
            if (s.length() > 0) {

                double morningFuel = 0.0;
                boolean isMorning = false;
                if (fuelData.getMorningFuelLevel() != null && fuelData.getMorningFuelLevel().trim().length() > 0) {
                    isMorning = true;
                    try {
                        morningFuel = Double.parseDouble(fuelData.getMorningFuelLevel());
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                    }
                }

                double eveningFuel = 0.0;
                try {
                    eveningFuel = Double.parseDouble(fuelData.getEveningFuelLevel());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(FuelManagerActivity.this, "Please enter valid fuel level", Toast.LENGTH_SHORT).show();
                }

                double consumedFuel = morningFuel - eveningFuel;
                fuelConsumedEdt.setText(String.valueOf(consumedFuel));
                fuelData.setConsumedFuel(String.valueOf(consumedFuel));

                if (!isMorning) {
                    String previousEntry = s.toString().substring(0, s.toString().length() - 1);
                    eveningFuelLevelEdt.setText(previousEntry);
                    Snackbar.make(mLayout, "Please Enter Morning Fuel Level", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                } else {
                    if (morningFuelLevelEdt.isEnabled()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(FuelManagerActivity.this);
                        builder.setCancelable(false);
                        builder.setMessage("Would you like to save the morning fuel Level?").
                                setNegativeButton("EDIT", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        String previousEntry = s.toString().substring(0, s.toString().length() - 1);
                                        eveningFuelLevelEdt.setText(previousEntry);
                                        morningFuelLevelEdt.requestFocus();
                                    }
                                }).
                                setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        morningFuelLevelEdt.setEnabled(false);
                                        Date date = new Date();
                                        String todayDate = FUEL_DATE_TIME_FORMAT.format(date);
                                        if (fuelData.getFuelDate().equals(todayDate)) {
                                            fuelData.setSendingStatus(DBAdapter.SAVE);
                                            if (!fuelData.save(db)) {
                                                Toast.makeText(FuelManagerActivity.this, "Today's fuel detail has been sent", Toast.LENGTH_LONG).show();
                                            }
                                            ;
                                        }
                                    }
                                });
                        builder.show();
                    } else {
                        if (eveningFuel > morningFuel) {
                            Snackbar.make(mLayout, "Left Fuel can not be greater than Initial Fuel", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                }

            } else {
                fuelConsumedEdt.setText(morningFuelLevelEdt.getText());
            }
        }
    };

    TextWatcher morningFuelTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {
            fuelData.setMorningFuelLevel(s.toString());
            /*if(s.length()>0) {
                fuelData.setMorningFuelLevel(s.toString());
            }*/
        }
    };

    TextWatcher consumedFuelTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {
//            fuelData.setConsumedFuel(s.toString());
            /*if (s.length() > 0) {

                double filledFuel = 0.0;
                boolean isFilled = false;
                if (fuelData.getFilledDieselAmount() != null && fuelData.getFilledDieselAmount().trim().length() > 0) {
                    isFilled = true;
                    try {
                        filledFuel = Double.parseDouble(fuelData.getFilledDieselAmount());
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                    }
                }


                double morningFuel = 0.0;
                boolean isMorning = false;
                if (fuelData.getMorningFuelLevel() != null && fuelData.getMorningFuelLevel().trim().length() > 0) {
                    isMorning = true;
                    try {
                        morningFuel = Double.parseDouble(fuelData.getMorningFuelLevel());
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                    }
                }

                double consumedFuel = 0.0;
                try {
                    consumedFuel = Double.parseDouble(fuelData.getConsumedFuel());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(FuelManagerActivity.this, "Please enter valid fuel level", Toast.LENGTH_SHORT).show();
                }

                if (!isMorning) {
                    String previousEntry = s.toString().substring(0, s.toString().length() - 1);
                    fuelConsumedEdt.setText(previousEntry);
                    Snackbar.make(mLayout, "Please Enter Morning Fuel Level", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                } else if (isMorning && isFilled) {
                    if (consumedFuel > morningFuel + filledFuel) {
                        String previousEntry = s.toString().substring(0, s.toString().length() - 1);
                        fuelConsumedEdt.setText(previousEntry);
                        Snackbar.make(mLayout, "Consumed Fuel can not be greater then Initial Fuel + Filled Fuel", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } else {
                    if (consumedFuel > morningFuel) {
                        String previousEntry = s.toString().substring(0, s.toString().length() - 1);
                        fuelConsumedEdt.setText(previousEntry);
                        Snackbar.make(mLayout, "Consumed Fuel can not be greater then Initial Fuel", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }

            }*/
        }
    };

    TextWatcher filledFuelTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {
//            fuelData.setFilledDieselAmount(s.toString());
        }
    };

}
