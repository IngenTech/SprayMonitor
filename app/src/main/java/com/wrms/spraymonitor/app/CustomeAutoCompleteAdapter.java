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
 * Created by Admin on 29-12-2016.
 */

public class CustomeAutoCompleteAdapter extends ArrayAdapter<ContentData> {
    private final Context mContext;
    private final List<ContentData> mVillages;
    private final List<ContentData> mVillages_All;
    private final List<ContentData> mVillages_Suggestion;
    private final int mLayoutResourceId;

    public CustomeAutoCompleteAdapter(Context context, int resource, List<ContentData> locationModels) {
        super(context, resource, locationModels);
        this.mContext = context;
        this.mLayoutResourceId = resource;
        this.mVillages = new ArrayList<>(locationModels);
        this.mVillages_All = new ArrayList<>(locationModels);
        this.mVillages_Suggestion = new ArrayList<>();
    }

    public int getCount() {
        return mVillages.size();
    }

    public ContentData getItem(int position) {
        return mVillages.get(position);
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
            ContentData department = getItem(position);
            TextView name = (TextView) convertView.findViewById(R.id.textView);
            name.setText(department.getTitle());
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
                return ((ContentData) resultValue).getTitle();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    mVillages_Suggestion.clear();
                    for (ContentData department : mVillages_All) {
                        if (department.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            mVillages_Suggestion.add(department);
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mVillages_Suggestion;
                    filterResults.count = mVillages_Suggestion.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                mVillages.clear();
                if (results != null && results.count > 0) {
                    // avoids unchecked cast warning when using mVillages.addAll((ArrayList<Department>) results.values);
                    List<?> result = (List<?>) results.values;
                    for (Object object : result) {
                        if (object instanceof ContentData) {
                            mVillages.add((ContentData) object);
                        }
                    }
                } else if (constraint == null) {
                    // no filter, add entire original list back in
                    mVillages.addAll(mVillages_All);
                }
                notifyDataSetChanged();
            }
        };
    }
}