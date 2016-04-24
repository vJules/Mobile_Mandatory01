package org.projects.shoppinglist;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Julian on 05-04-2016.
 */
public class ProductList implements Parcelable {
    String name;
    ArrayList<Product> list;

    public ProductList (String startName){
        name = startName;
        list = new ArrayList<>();
    }
    private ProductList(Parcel in) {
        name = in.readString();
        list = new ArrayList<Product>();
        in.readTypedList(list, Product.CREATOR);
    }

    public String getName() { return name;}

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeTypedList(list);
    }

    public static final Parcelable.Creator<ProductList> CREATOR = new Parcelable.Creator<ProductList>() {
        public ProductList createFromParcel(Parcel in) {
            return new ProductList(in);
        }

        public ProductList[] newArray(int size) {
            return new ProductList[size];
        }
    };



}
