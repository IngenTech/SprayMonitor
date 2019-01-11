package com.wrms.spraymonitor.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wrms.spraymonitor.R;
import com.wrms.spraymonitor.dataobject.FarmerData;
import com.wrms.spraymonitor.dataobject.FormListRowHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WRMS on 10-03-2016.
 */
public class FarmerRecyclerAdapter extends RecyclerView.Adapter<FormListRowHolder> {


    private List<FarmerData> feedItemList;

    private Context mContext;

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public FarmerRecyclerAdapter(Context context, List<FarmerData> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public FormListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);
        FormListRowHolder mh = new FormListRowHolder(v);

        return mh;
    }

    @Override
    public void onBindViewHolder(FormListRowHolder feedListRowHolder,final int i) {
        FarmerData feedItem = feedItemList.get(i);
        feedListRowHolder.title.setText(Html.fromHtml(feedItem.getFarmerContact()));
        feedListRowHolder.text1.setText(Html.fromHtml(feedItem.getFirstName()));
        /*String villageName = "";
        if(feedItem.getVillageName()!=null ){
            villageName = feedItem.getVillageName();
        }else{
            villageName = feedItem.getVillageId();
        }
        feedListRowHolder.text2.setText(villageName);*/
        feedListRowHolder.text3.setText(Html.fromHtml(feedItem.getSendingStatus()));
        feedListRowHolder.text2.setText(Html.fromHtml(feedItem.getCreatedDateTime()));
        /*if(feedItem.getFarmerSendingStatus().equals(DBAdapter.SUBMIT)){
            feedListRowHolder.title.setTextColor(mContext.getResources().getColor(R.color.submit_status));
        }else{
            feedListRowHolder.title.setTextColor(mContext.getResources().getColor(R.color.color_primary));
        }*/

        if (feedItem.getSendingStatus().equals(DBAdapter.SAVE)) {
            feedListRowHolder.text3.setTextColor(mContext.getResources().getColor(R.color.fail_status));
        } else if (feedItem.getSendingStatus().equals(DBAdapter.SUBMIT)) {
            feedListRowHolder.text3.setTextColor(mContext.getResources().getColor(R.color.submit_status));
        } else if (feedItem.getSendingStatus().equals(DBAdapter.SENT)) {
            feedListRowHolder.text3.setTextColor(mContext.getResources().getColor(R.color.save_status));
            feedListRowHolder.text3.setText("Registered");
        } else {
            feedListRowHolder.text3.setTextColor(mContext.getResources().getColor(R.color.fail_status));
        }

        feedListRowHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(i);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public void setModels(List<FarmerData> models) {
        feedItemList = new ArrayList<>(models);
    }

    public FarmerData removeItem(int position) {
        final FarmerData model = feedItemList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, FarmerData model) {
        feedItemList.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final FarmerData model = feedItemList.remove(fromPosition);
        feedItemList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void animateTo(List<FarmerData> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<FarmerData> newModels) {
        for (int i = feedItemList.size() - 1; i >= 0; i--) {
            final FarmerData model = feedItemList.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<FarmerData> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final FarmerData model = newModels.get(i);
            if (!feedItemList.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<FarmerData> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final FarmerData model = newModels.get(toPosition);
            final int fromPosition = feedItemList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

}
