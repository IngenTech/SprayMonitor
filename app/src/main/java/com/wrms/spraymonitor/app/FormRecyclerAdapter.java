package com.wrms.spraymonitor.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wrms.spraymonitor.R;
import com.wrms.spraymonitor.dataobject.FormListRowHolder;
import com.wrms.spraymonitor.dataobject.SprayMonitorData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WRMS on 03-11-2015.
 */
public class FormRecyclerAdapter  extends RecyclerView.Adapter<FormListRowHolder> {


    private List<SprayMonitorData> feedItemList;

    private Context mContext;

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public FormRecyclerAdapter(Context context, List<SprayMonitorData> feedItemList) {
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
    public void onBindViewHolder(FormListRowHolder feedListRowHolder, final int i) {
        SprayMonitorData sprayData = feedItemList.get(i);
        feedListRowHolder.title.setText(Html.fromHtml(sprayData.getFarmerContact()));
        feedListRowHolder.text1.setText(Html.fromHtml(sprayData.getFarmerName()));
        /*String villageName = "";
        if(sprayData.getVillageName()!=null ){
            villageName = sprayData.getVillageName();
        }else{
            villageName = sprayData.getVillage();
        }
        feedListRowHolder.text2.setText(villageName);*/
        feedListRowHolder.text2.setText(Html.fromHtml(sprayData.getDeviceDateTime()));

        try{
            double amountReceivable = 0.0;
            if(sprayData.getAmountReceivable()!= null && (!sprayData.getAmountReceivable().isEmpty())){
                amountReceivable = Double.parseDouble(sprayData.getAmountReceivable());
            }
            double amountCollected = 0.0;
            if(sprayData.getAmountCollected()!= null && (!sprayData.getAmountCollected().isEmpty())){
                amountCollected = Double.parseDouble(sprayData.getAmountCollected());
            }

//            System.out.println("amountReceivable : "+amountReceivable+" amountCollected : "+amountCollected);

            if(amountReceivable!=0.0) {
                if(sprayData.getCollectedBy().contains("GOV/UNI")|| sprayData.getCollectedBy().toLowerCase().contains("demo")){
                    feedListRowHolder.text3.setTextColor(mContext.getResources().getColor(R.color.color_primary));
                    feedListRowHolder.text3.setText(Html.fromHtml("Payment Received"));
                }else {
                    feedListRowHolder.text3.setVisibility(View.VISIBLE);
                    if (amountCollected < amountReceivable) {
                        feedListRowHolder.text3.setTextColor(mContext.getResources().getColor(R.color.fail_status));
                        feedListRowHolder.text3.setText(Html.fromHtml("Payment Pending"));
                    } else {
                        feedListRowHolder.text3.setTextColor(mContext.getResources().getColor(R.color.color_primary));
                        feedListRowHolder.text3.setText(Html.fromHtml("Payment Received"));
                    }
                }
            }else{
                feedListRowHolder.text3.setText(Html.fromHtml(""));
            }

        }catch (NumberFormatException e){
            e.printStackTrace();
        }


        /*if(feedItem.getFarmerSendingStatus().equals(DBAdapter.SUBMIT)){
            feedListRowHolder.title.setTextColor(mContext.getResources().getColor(R.color.submit_status));
        }else{
            feedListRowHolder.title.setTextColor(mContext.getResources().getColor(R.color.color_primary));
        }*/

        if(sprayData.getSendingStatus().equals(DBAdapter.SENT)){
            feedListRowHolder.status.setTextColor(mContext.getResources().getColor(R.color.fail_status));
            feedListRowHolder.status.setText(Html.fromHtml("Sent To Server"));
        }else if(sprayData.getSendingStatus().equals(DBAdapter.SUBMIT)){
            feedListRowHolder.status.setTextColor(mContext.getResources().getColor(R.color.submit_status));
            feedListRowHolder.status.setText(Html.fromHtml(sprayData.getSendingStatus()));
        }else{
            feedListRowHolder.status.setTextColor(mContext.getResources().getColor(R.color.color_primary));
            feedListRowHolder.status.setText(Html.fromHtml(sprayData.getSendingStatus()));
        }
//        final FormListRowHolder holder = feedListRowHolder;
        feedListRowHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(i);
                return false;
            }
        });
    }

    @Override
    public void onViewRecycled(FormListRowHolder holder) {
        holder.moreImageView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public void setModels(List<SprayMonitorData> models) {
        feedItemList = new ArrayList<>(models);
    }

    public SprayMonitorData removeItem(int position) {
        final SprayMonitorData model = feedItemList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, SprayMonitorData model) {
        feedItemList.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final SprayMonitorData model = feedItemList.remove(fromPosition);
        feedItemList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void animateTo(List<SprayMonitorData> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<SprayMonitorData> newModels) {
        for (int i = feedItemList.size() - 1; i >= 0; i--) {
            final SprayMonitorData model = feedItemList.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }
    private void applyAndAnimateAdditions(List<SprayMonitorData> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final SprayMonitorData model = newModels.get(i);
            if (!feedItemList.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<SprayMonitorData> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final SprayMonitorData model = newModels.get(toPosition);
            final int fromPosition = feedItemList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

}
