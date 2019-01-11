package com.wrms.spraymonitor.app;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wrms.spraymonitor.R;
import com.wrms.spraymonitor.dataobject.FuelData;

import java.util.ArrayList;

/**
 * Created by WRMS on 21-06-2016.
 */
public class FuelListViewAdapter extends BaseAdapter {
    ArrayList<FuelData> fuelDataArrayList;
    Context context;
    Holder holder;
    private static LayoutInflater inflater=null;
    public FuelListViewAdapter(Context context, ArrayList<FuelData> fuelDataArrayList) {
        // TODO Auto-generated constructor stub
        this.fuelDataArrayList = fuelDataArrayList;
        this.context=context;
        if (context != null) {
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return fuelDataArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView txtDate;
        TextView txtConsumedFuel;
        TextView txtRemark;
        TextView txtSendingStatus;

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.saved_fuel_list_item, null);
        holder.txtDate =(TextView) rowView.findViewById(R.id.dateTxt);
        holder.txtConsumedFuel =(TextView) rowView.findViewById(R.id.fuelConsumedTxt);
        holder.txtRemark =(TextView) rowView.findViewById(R.id.remarkTxt);
        holder.txtSendingStatus =(TextView) rowView.findViewById(R.id.sendingStatusTxt);


        holder.txtDate.setText(fuelDataArrayList.get(position).getFuelDate().toString());
        if(fuelDataArrayList.get(position).getConsumedFuel()!=null && fuelDataArrayList.get(position).getConsumedFuel().trim().length()>0) {
            holder.txtConsumedFuel.setText("Consumed Fuel : "+ fuelDataArrayList.get(position).getConsumedFuel().toString()+" Litre");
        }else{
            holder.txtConsumedFuel.setText("");
        }
        holder.txtRemark.setText(fuelDataArrayList.get(position).getRemark());
        holder.txtSendingStatus.setText(fuelDataArrayList.get(position).getSendingStatus());
        if(fuelDataArrayList.get(position).getSendingStatus().equalsIgnoreCase(DBAdapter.SAVE)){
            holder.txtSendingStatus.setTextColor(Color.parseColor("#509B28"));
        }else if(fuelDataArrayList.get(position).getSendingStatus().equalsIgnoreCase(DBAdapter.SUBMIT)){
            holder.txtSendingStatus.setTextColor(Color.parseColor("#F9A011"));
        }else{
            holder.txtSendingStatus.setTextColor(Color.parseColor("#B51E0A"));
        }

        return rowView;
    }

}
