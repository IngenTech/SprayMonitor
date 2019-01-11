package com.wrms.spraymonitor.dataobject;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.wrms.spraymonitor.R;

/**
 * Created by WRMS on 19-11-2015.
 */
public class FormContentRowHolder extends RecyclerView.ViewHolder  {

    public TextView title;
    public TextView value;

    public FormContentRowHolder(View view) {
        super(view);
        this.title = (TextView) view.findViewById(R.id.contentTitle);
        this.value = (TextView) view.findViewById(R.id.value);
    }

}
