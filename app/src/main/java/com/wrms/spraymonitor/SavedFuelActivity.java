package com.wrms.spraymonitor;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wrms.spraymonitor.app.Constents;
import com.wrms.spraymonitor.app.DBAdapter;
import com.wrms.spraymonitor.app.FuelListViewAdapter;
import com.wrms.spraymonitor.dataobject.ContentData;
import com.wrms.spraymonitor.dataobject.FuelData;
import com.wrms.spraymonitor.fuelmanager.FuelManager;
import com.wrms.spraymonitor.fuelmanager.MachineRunTimeData;

import java.util.ArrayList;

public class SavedFuelActivity extends AppCompatActivity {


    ListView listView;
    DBAdapter db;
    ArrayList<FuelData> fuelDatas = new ArrayList<>();

    ArrayList< MachineRunTimeData> runDatas = new ArrayList<MachineRunTimeData>();

    FuelListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_fuel);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);


        listView = (ListView)findViewById(R.id.listView);

        db = new DBAdapter(this);
        db.open();

        Cursor fuelDataCursor = db.getAllFuelDetail();
        if(fuelDataCursor.moveToFirst()){
            do{
                FuelData fuelData = new FuelData();
                fuelData.setId(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.ID)));

                fuelData.setFuelId(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.FUEL_ID)));
                fuelData.setFuelDate(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.FUEL_DATE)));
                fuelData.setMorningFuelLevel(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.MORNING_FUEL_LEVEL)));
                fuelData.setEveningFuelLevel(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.EVENING_FUEL_LEVEL)));

               /* fuelData.setFuelStartHour(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.MACHINE_START_HOUR)));
                fuelData.setFuelStopHour(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.MACHINE_STOP_HOUR)));*/

                fuelData.setConsumedFuel(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.CONSUMED_FUEL)));
                fuelData.setDieselFilled(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.DIESEL_FILLED)));
                fuelData.setBillNo(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.BILL_NO)));
                fuelData.setBillAmount(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.BILL_AMOUNT)));
                fuelData.setFilledDieselAmount(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.FILLED_DIESEL_AMOUNT)));

                fuelData.setBillNo1(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.BILL_NO1)));
                fuelData.setBillAmount1(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.BILL_AMOUNT1)));
                fuelData.setFilledDieselAmount1(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.FILLED_DIESEL_AMOUNT1)));

                fuelData.setBillNo2(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.BILL_NO2)));
                fuelData.setBillAmount2(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.BILL_AMOUNT2)));
                fuelData.setFilledDieselAmount2(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.FILLED_DIESEL_AMOUNT2)));

                fuelData.setTractorLeased(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.TRACTOR_LEASED)));
                fuelData.setCostPerDay(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.COST_PER_DAY)));
                fuelData.setRemark(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.REMARK)));
                fuelData.setCreatedDateTime(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.CREATED_DATE_TIME)));
                fuelData.setSendingStatus(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.SENDING_STATUS)));
                fuelData.setMachineId(fuelDataCursor.getString(fuelDataCursor.getColumnIndex(DBAdapter.MACHINE_ID)));


                fuelDatas.add(fuelData);

            }while (fuelDataCursor.moveToNext());
        }

        adapter = new FuelListViewAdapter(SavedFuelActivity.this,fuelDatas);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if(fuelDatas.size()>position){
                    FuelData fuelData = fuelDatas.get(position);

                  //  MachineRunTimeData runData = fuelDatas.get(position);

                    if(fuelData.getSendingStatus().equals(DBAdapter.SAVE)) {
                        Intent intent = new Intent(SavedFuelActivity.this, FuelManager.class);
                        intent.putExtra("fuel_data", fuelData);
                        startActivity(intent);
                    }else{
                        ArrayList<ContentData> contentDatas = fuelData.getListContentData();
                        Intent intent = new Intent(SavedFuelActivity.this, ShowFormDetailsActivity.class);
                        intent.putParcelableArrayListExtra(Constents.CONTENT_DATAS, contentDatas);
                        startActivity(intent);
                    }
                }

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home:{
                finish();
                return true;}
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}
