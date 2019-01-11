package com.wrms.spraymonitor.app;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wrms.spraymonitor.R;
import com.wrms.spraymonitor.dataobject.ContentData;
import com.wrms.spraymonitor.dataobject.FormContentRowHolder;

import java.util.List;

/**
 * Created by WRMS on 19-11-2015.
 */
public class formContentRecyclerAdapter extends RecyclerView.Adapter<FormContentRowHolder> {

    private List<ContentData> feedItemList;

    private Context mContext;

    public formContentRecyclerAdapter(Context context, List<ContentData> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public FormContentRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_list_row, null);
        FormContentRowHolder mh = new FormContentRowHolder(v);

        return mh;
    }

    @Override
    public void onBindViewHolder(FormContentRowHolder feedListRowHolder, int i) {
        ContentData feedItem = feedItemList.get(i);
        String value = "null";
        if(feedItem.getValue()!=null) {
            value = feedItem.getValue();
        }

        String st = String.valueOf(Html.fromHtml(feedItem.getTitle())).trim();

        Log.v("kdjksa",st+"");

        if (st.equalsIgnoreCase("Total Acre")){

        }else {

            feedListRowHolder.title.setText(st);
            feedListRowHolder.value.setText(Html.fromHtml(value));
        }

        if(feedItem.getTitle().equals("Saved Image")||feedItem.getTitle().equals("Saved Video")){
            feedListRowHolder.title.setTextColor(Color.RED);
            feedListRowHolder.value.setTextColor(Color.RED);
        }else{
            feedListRowHolder.title.setTextColor(Color.BLACK);
            feedListRowHolder.value.setTextColor(Color.GRAY);
        }

        if(feedItem.getTitle().equals("Sent Image")||feedItem.getTitle().equals("Sent Video")){
            feedListRowHolder.title.setTextColor(Color.GREEN);
            feedListRowHolder.value.setTextColor(Color.GREEN);
        }else{
            feedListRowHolder.title.setTextColor(Color.BLACK);
            feedListRowHolder.value.setTextColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

}
