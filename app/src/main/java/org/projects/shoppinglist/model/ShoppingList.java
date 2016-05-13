package org.projects.shoppinglist.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Julian on 05-04-2016.
 */
public class ShoppingList implements Parcelable {
    String name;
    HashMap<String, Product> products;
    HashMap<String, Object> timestampUpdated;


    public ShoppingList(){
    }

    public ShoppingList(String startName){
        name = startName;
    }
    private ShoppingList(Parcel in) {
        name = in.readString();
//        in.readTypedList(products, Product.CREATOR);
    }

    public String getName() { return name;}

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Product>getProducts() {
        return products;
    }

    public void setProducts(HashMap<String, Product> products) {
        this.products = products;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
//        out.writeTypedList(products);
    }

    public static final Parcelable.Creator<ShoppingList> CREATOR = new Parcelable.Creator<ShoppingList>() {
        public ShoppingList createFromParcel(Parcel in) {
            return new ShoppingList(in);
        }

        public ShoppingList[] newArray(int size) {
            return new ShoppingList[size];
        }
    };



}
