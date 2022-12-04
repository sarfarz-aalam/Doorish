package com.pentaware.doorish.model;
import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

public class OfflineProduct implements Serializable, Parcelable {

    public String productTitle;
    public String productQuantity;
    boolean pureOfflineOrder;

    public OfflineProduct(String title, String quantity, Boolean offlineOrder, String orderId){
        productTitle = title;
        productQuantity = quantity;
        pureOfflineOrder = offlineOrder;
    }

    public OfflineProduct(){}

    public OfflineProduct(Parcel source) {
            this.productTitle = source.readString();
            this.productQuantity = source.readString();
            this.pureOfflineOrder = (Boolean) source.readValue(getClass().getClassLoader());
        }


    public String getProductTitle() {
        return productTitle;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public boolean isPureOfflineOrder() {
        return pureOfflineOrder;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(productTitle);
        parcel.writeString(productQuantity);
        parcel.writeValue(pureOfflineOrder);
    }
    public static Creator<OfflineProduct> CREATOR = new Creator<OfflineProduct>() {

        @Override
        public OfflineProduct createFromParcel(Parcel source) {
            return new OfflineProduct(source);
        }

        @Override
        public OfflineProduct[] newArray(int size) {
            return new OfflineProduct[size];
        }
    };
}
