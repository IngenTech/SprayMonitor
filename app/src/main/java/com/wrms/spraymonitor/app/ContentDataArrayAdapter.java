package com.wrms.spraymonitor.app;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.wrms.spraymonitor.R;
import com.wrms.spraymonitor.dataobject.ContentData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 21-02-2017.
 */

public class ContentDataArrayAdapter extends ArrayAdapter<ContentData> {
    private final Context mContext;
    private final List<ContentData> mContentDatas;
    private final List<ContentData> mContentDatas_All;
    private final List<ContentData> mContentDatas_Suggestion;
    private final int mLayoutResourceId;

    public ContentDataArrayAdapter(Context context, int resource, List<ContentData> ContentDatas) {
        super(context, resource, ContentDatas);
        this.mContext = context;
        this.mLayoutResourceId = resource;
        this.mContentDatas = new ArrayList<>(ContentDatas);
        this.mContentDatas_All = new ArrayList<>(ContentDatas);
        this.mContentDatas_Suggestion = new ArrayList<>();
    }

    public int getCount() {
        return mContentDatas.size();
    }

    public ContentData getItem(int position) {
        return mContentDatas.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(mLayoutResourceId, parent, false);
            }
            ContentData ContentData = getItem(position);
            TextView name = (TextView) convertView.findViewById(R.id.textView);
            name.setText(ContentData.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public String convertResultToString(Object resultValue) {
                return ((ContentData) resultValue).getValue();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    mContentDatas_Suggestion.clear();
                    for (ContentData contentData : mContentDatas_All) {
                        if (contentData.getValue().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            mContentDatas_Suggestion.add(contentData);
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mContentDatas_Suggestion;
                    filterResults.count = mContentDatas_Suggestion.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                mContentDatas.clear();
                if (results != null && results.count > 0) {
                    // avoids unchecked cast warning when using mContentDatas.addAll((ArrayList<ContentData>) results.values);
                    List<?> result = (List<?>) results.values;
                    for (Object object : result) {
                        if (object instanceof ContentData) {
                            mContentDatas.add((ContentData) object);
                        }
                    }
                } else if (constraint == null) {
                    // no filter, add entire original list back in
                    mContentDatas.addAll(mContentDatas_All);
                }
                notifyDataSetChanged();
            }
        };
    }
}
