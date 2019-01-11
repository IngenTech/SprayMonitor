package com.wrms.spraymonitor.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wrms.spraymonitor.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by WRMS on 30-04-2016.
 */
public class ExpendableListAdapter extends BaseExpandableListAdapter {


    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    public ExpendableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childObject = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expendable_report_list_item, null);
        }

        TextView txtMaterialName = (TextView) convertView
                .findViewById(R.id.materialTypeName);
        TextView txtMaterialTypeRemark = (TextView) convertView
                .findViewById(R.id.materialTypeRemark);
        TextView txtMaterialAmount = (TextView) convertView
                .findViewById(R.id.materialAmount);

        /*if(childPosition%2==0){
            convertView.setBackgroundColor(_context.getResources().getColor(R.color.row_alternet_color_1));
        }else{
            convertView.setBackgroundColor(_context.getResources().getColor(R.color.row_alternet_color_2));
        }*/
        txtMaterialName.setText(childObject);
        txtMaterialTypeRemark.setText(childObject);
        txtMaterialAmount.setText(childObject);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expendable_group_item, null);
        }


        TextView txtInvoiceNumber = (TextView) convertView
                .findViewById(R.id.invoiceNumber);
        TextView txtInvoiceAmount = (TextView) convertView
                .findViewById(R.id.invoiceAmount);
        TextView txtInvoiceDate = (TextView) convertView
                .findViewById(R.id.invoiceDate);
        ImageView txtInvoiceView = (ImageView) convertView
                .findViewById(R.id.invoiceView);

            /*txtInvoiceNumber.setText(invoiceData.getInvoiceNumber());
            txtInvoiceAmount.setText(invoiceData.getInvoiceAmount());
            txtInvoiceDate.setText(invoiceData.getDate());*/

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

