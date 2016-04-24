package org.projects.shoppinglist;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Julian on 05-04-2016.
 */
public class Product implements Parcelable {
    String name;
    String quantity;

    public Product(String startName, String startQuantity){
        name = startName;
        quantity = startQuantity;
    }

    public String toString() { return name + " " + quantity;}

    private Product(Parcel in) {
        name = in.readString();
        quantity = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(quantity);
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
