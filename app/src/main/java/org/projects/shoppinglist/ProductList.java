package org.projects.shoppinglist;

import java.util.ArrayList;

/**
 * Created by Julian on 05-04-2016.
 */
public class ProductList {
    String name;
    ArrayList<Product> list;

    public ProductList (String startName){
        name = startName;
        list = new ArrayList<>();
    }
}
