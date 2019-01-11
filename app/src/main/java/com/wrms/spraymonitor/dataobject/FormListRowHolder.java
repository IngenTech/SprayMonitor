package com.wrms.spraymonitor.dataobject;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wrms.spraymonitor.R;

/**
 * Created by WRMS on 03-11-2015.
 */
public class FormListRowHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

    public TextView title;
    public TextView text1;
    public TextView text2;
    public TextView text3;
//    public TextView text4;
    public TextView status;
    public View view;
    public ImageView moreImageView;

    public FormListRowHolder(View view) {
        super(view);
//        this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        this.title = (TextView) view.findViewById(R.id.title);
        this.text1 = (TextView) view.findViewById(R.id.txtName);
        this.text2 = (TextView) view.findViewById(R.id.txtSurname);
        this.text3 = (TextView) view.findViewById(R.id.txtEmail);
//        this.text4 = (TextView) view.findViewById(R.id.createdDateTime);
        this.status = (TextView)view.findViewById(R.id.status);
        this.moreImageView = (ImageView) view.findViewById(R.id.moreImageView);
        view.setOnCreateContextMenuListener(this);
        this.view = view;

    }

    public void bind(SprayMonitorData model) {
        title.setText(model.getFarmerName());
        text1.setText(model.getFarmerContact());
        text2.setText(model.getVillage());
        text3.setText(model.getPaymentStatus());
//        text4.setText(model.getDeviceDateTime());
        status.setText(model.getSendingStatus());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        //menuInfo is null
        menu.add(Menu.NONE, R.id.action_delete,
                Menu.NONE, R.string.action_delete);
    }
}
