package com.wrms.spraymonitor.dataobject;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.wrms.spraymonitor.app.DBAdapter;

/**
 * Created by WRMS on 07-05-2016.
 */
public class ProductData implements Parcelable {

    private String tankFillingId;
    private String productName;
    private String productId;
    private String uomName;
    private String uomId;
    private String productQuantity;
    private String productDataId;

    public ProductData() {
    }

    ;

    public ProductData(Parcel in) {

        String[] data = new String[7];

        in.readStringArray(data);
        this.tankFillingId = data[0];
        this.productName = data[1];
        this.productId = data[2];
        this.uomName = data[3];
        this.uomId = data[4];
        this.productQuantity = data[5];
        this.productDataId = data[6];
    }

    public static final Parcelable.Creator<ProductData> CREATOR = new Parcelable.Creator<ProductData>() {

        @Override
        public ProductData createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new ProductData(source); // using parcelable constructor
        }

        @Override
        public ProductData[] newArray(int size) {
            // TODO Auto-generated method stub
            return new ProductData[size];
        }
    };

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.tankFillingId,
                this.productName,
                this.productId,
                this.uomName,
                this.uomId,
                this.productQuantity,
                this.productDataId});
    }

    public ProductData(String productDataId, DBAdapter db) {
        Cursor cursor = db.getSprayedProductById(productDataId);
        if (cursor.moveToFirst()) {
            this.tankFillingId = cursor.getString(cursor.getColumnIndex(DBAdapter.TANK_FILLING_ID));
            this.productId = cursor.getString(cursor.getColumnIndex(DBAdapter.PRODUCT_ID));
            this.uomId = cursor.getString(cursor.getColumnIndex(DBAdapter.MEASURING_UNIT_ID));
            this.productQuantity = cursor.getString(cursor.getColumnIndex(DBAdapter.PRODUCT_QTY));
            this.productDataId = cursor.getString(cursor.getColumnIndex(DBAdapter.PRODUCT_DATA_ID));

            this.productName = db.getProductNameById(this.productId);
            this.uomName = db.getUomNameById(this.uomId);
        }
        cursor.close();
    }


    public boolean save(DBAdapter db) {
        boolean isSaved = false;
        ContentValues values = new ContentValues();
        values.put(DBAdapter.TANK_FILLING_ID, getTankFillingId());
        values.put(DBAdapter.PRODUCT_ID, getProductId());
        values.put(DBAdapter.MEASURING_UNIT_ID, getUomId());
        values.put(DBAdapter.PRODUCT_QTY, getProductQuantity());
        values.put(DBAdapter.PRODUCT_DATA_ID, getProductDataId());
        long k = db.db.insert(DBAdapter.TABLE_SPRAYED_PRODUCT, null, values);
        if (k != -1) {
            isSaved = true;
        }
        return isSaved;
    }

    public boolean delete(DBAdapter db) {
        boolean isDelete = false;

        long k = db.db.delete(DBAdapter.TABLE_SPRAYED_PRODUCT, DBAdapter.PRODUCT_DATA_ID + " = '" + getProductDataId() + "'", null);
        if (k != -1) {
            isDelete = true;
        }
        return isDelete;
    }


    public String getProductDataId() {
        return productDataId;
    }

    public void setProductDataId(String productDataId) {
        this.productDataId = productDataId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUomName() {
        return uomName;
    }

    public void setUomName(String uomName) {
        this.uomName = uomName;
    }

    public String getUomId() {
        return uomId;
    }

    public void setUomId(String uomId) {
        this.uomId = uomId;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getTankFillingId() {
        return tankFillingId;
    }

    public void setTankFillingId(String tankFillingId) {
        this.tankFillingId = tankFillingId;
    }
}
