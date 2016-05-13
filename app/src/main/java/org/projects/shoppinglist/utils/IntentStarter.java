package org.projects.shoppinglist.utils;

import android.app.Activity;
import android.content.Intent;

import org.projects.shoppinglist.MainActivity;
import org.projects.shoppinglist.products.ProductListActivity;

/**
 * Created by Julian on 11-05-2016.
 */
public class IntentStarter {

    public void showShoppingList(Activity activity, String key){
        Intent intent = new Intent(activity, ProductListActivity.class);
        intent.putExtra(ProductListActivity.KEY_PRODUCT_ID, key);
        activity.startActivity(intent);
    }

    public void showProductList(Activity activity, String key){
        Intent intent = new Intent(activity, ProductListActivity.class);
        intent.putExtra(ProductListActivity.KEY_PRODUCT_ID, key);
        activity.startActivity(intent);
    }
}
