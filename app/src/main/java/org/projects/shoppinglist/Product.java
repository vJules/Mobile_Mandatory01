package org.projects.shoppinglist;

/**
 * Created by Julian on 05-04-2016.
 */
public class Product {
    String name;
    int quantity;

    public Product(String startName, int startQuantity){
        name = startName;
        quantity = startQuantity;
    }

    public String toString() { return name + " " + quantity;}

}
