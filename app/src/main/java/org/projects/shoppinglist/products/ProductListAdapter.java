package org.projects.shoppinglist.products;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import org.projects.shoppinglist.R;
import org.projects.shoppinglist.model.ShoppingList;

import java.util.List;

/**
 * Created by Julian on 21-04-2016.
 */

public class ProductListAdapter extends ArrayAdapter<ShoppingList> {

    public ProductListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ProductListAdapter(Context context, List<ShoppingList> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LinearLayout view = (LinearLayout) convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.content_main, null);
        }

        ShoppingList p = getItem(position);

//        if (p != null) {
//            TextView tt1 = (TextView) v.findViewById(R.id.);
//
//            if (tt1 != null) {
//                tt1.setText(p.getName());
//            }
//        }


        return v;
    }

}