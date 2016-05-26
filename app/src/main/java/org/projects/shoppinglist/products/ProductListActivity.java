package org.projects.shoppinglist.products;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseListAdapter;

import org.projects.shoppinglist.R;
import org.projects.shoppinglist.model.ShoppingList;
import org.projects.shoppinglist.settings.SettingsActivity;
import org.projects.shoppinglist.model.Product;
import org.projects.shoppinglist.utils.IntentStarter;

import java.util.Date;

/**
 * Created by Julian on 09-05-2016.
 */
public class ProductListActivity extends AppCompatActivity {
    String TAG = "tag";
    ListView listView;
    FirebaseListAdapter<Product> fireAdapter;
    Firebase ref;

    public static final String KEY_PRODUCT_ID = "org.projects.shoppinglist.products.ProductListActivity.KEY_PRODUCT_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productlist);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        toolbar.setNavigationIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.abc_ic_ab_back_mtrl_am_alpha, null));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                finish();
            }
        });

        listView = (ListView) findViewById(R.id.list);

        final String key = getIntent().getStringExtra(KEY_PRODUCT_ID );

        Firebase shoppingRef = new Firebase("https://eaashoppinglist.firebaseio.com/shoppingLists").child(key);
        shoppingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = (String) dataSnapshot.child("name").getValue();
                toolbar.setTitle(name);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) { }
        });

        ref = shoppingRef.child("products");



        fireAdapter = new FirebaseListAdapter<Product>(this, Product.class, android.R.layout.simple_list_item_2, ref) {
            @Override
            protected void populateView(View view, Product product, int i) {
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setText(product.getName());

                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text2.setText(product.getQuantity());
            }
        };

        listView.setAdapter(fireAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);




        //Get the FAB
        FloatingActionButton fabadd = (FloatingActionButton) findViewById(R.id.fab_add);

        //Initialize a layout
        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //Initialize EditTexts
        final EditText addInputText = new EditText(this);
        final EditText addInputQnt = new EditText(this);

        //Set input type so number show on keyboard
        addInputQnt.setInputType(InputType.TYPE_CLASS_NUMBER);

        //Set hints for EditTexts and add them to the layout
        addInputText.setHint("Name");
        layout.addView(addInputText);

        addInputQnt.setHint("Quantity");
        layout.addView(addInputQnt);

        //Initialize AlertBulder
        android.support.v7.app.AlertDialog.Builder addAlertDialogBuild = new android.support.v7.app.AlertDialog.Builder(ProductListActivity.this)
                .setTitle("New Product")
                .setMessage("Add a new product!")

                //Set the view to use the previous defined layout
                .setView(layout)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //Make sure the input is not empty
                        if (TextUtils.isEmpty(addInputText.getText().toString()) || TextUtils.isEmpty(addInputQnt.getText().toString())) {
                            Toast.makeText(ProductListActivity.this, "Please write something", Toast.LENGTH_LONG).show();
                            return;
                        }

                        //Initialize a new product and push it to firebase
                        Product pl = new Product(addInputText.getText().toString(), addInputQnt.getText().toString());
                        ref.push().setValue(pl);
                        fireAdapter.notifyDataSetChanged();

                        //Remove the text for next time a item is being added
                        addInputQnt.setText("");
                        addInputText.setText("");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        addInputQnt.setText("");
                        addInputText.setText("");
                    }
                })
                .setIcon(android.R.drawable.edit_text);
        final android.support.v7.app.AlertDialog addAlertDialog = addAlertDialogBuild.create();



        //Show AlertDialog when FAB is Clicked
        if (fabadd != null) {
            fabadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addAlertDialog.show();
                }
            });
        }

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                optionsShow(position);
                return true;
            }
        });

        getPreferences();
    }

    public void optionsShow(final int position) {
        //CharSequence which holds the options
        final CharSequence options[] = new CharSequence[] {"Edit", "Delete"};

        //AlertBuilder for item options
        final android.support.v7.app.AlertDialog.Builder optionsBuilder = new android.support.v7.app.AlertDialog.Builder(ProductListActivity.this);
        optionsBuilder.setTitle("Product actions");

        //Set the items in the AlertDialog to be the items in CharSequence
        optionsBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Switch which holds the options
                switch (which) {
                    case 0:
                        //Code for edit
                        break;

                    case 1:
                        //Code for delete button calls delete method
                        deleteItem(position);
                        break;
                }
            }
        });
        optionsBuilder.show();
    }

    public void deleteItem(int position) {
        //Save the deleted product, if user wants to restore
        final Product backup = fireAdapter.getItem(position);
        //Delete the product on firebase
        fireAdapter.getRef(position).setValue(null);

        final View parent = findViewById(R.id.layout);
        fireAdapter.notifyDataSetChanged();

        //Make SnackBar, with an Undo Action
        Snackbar snackbar = Snackbar
                .make(parent, backup + "Item removed", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Send the backup product to firebase if user hits undo, and show restored SnackBar
                        ref.push().setValue(backup);
                        fireAdapter.notifyDataSetChanged();
                        Snackbar snackbar = Snackbar.make(parent, "Item restored!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });
        snackbar.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putParcelableArrayList("savedFood", productBag);
    }



    //This will be called when other activities in our application
    //are finished.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) //exited our preference screen
        {
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setPreferences() {
        //Here we create a new activity and we instruct the
        //Android system to start it
        Intent intent = new Intent(this, SettingsActivity.class);
        //startActivity(intent); //this we can use if we DONT CARE ABOUT RESULT

        //we can use this, if we need to know when the user exists our preference screens
        startActivityForResult(intent, 1);
    }

    public void getPreferences() {

        //We read the shared preferences from the
        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sub_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            setPreferences();
            return true;
        }

        //Run if item_clear is the item clicked
        if (id == R.id.item_clear) {
            //Check if there are any items in the list. If there isn't show a snackbar and dont continue with rest of code.
            if (fireAdapter.getCount() == 0) {
                Toast.makeText(ProductListActivity.this, "No items in list", Toast.LENGTH_LONG).show();
                return true;
            }
            //If there are items, make a AlertDialog
            new AlertDialog.Builder(ProductListActivity.this)
                    .setTitle("Delete items")
                    .setMessage("Are you sure you want to delete all items?")
                    //If yes pressed
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Remove all by setting value to null and show snackbar
                            ref.removeValue();
                            Toast.makeText(ProductListActivity.this, "All items removed ", Toast.LENGTH_LONG).show();
                            fireAdapter.notifyDataSetChanged();
                        }
                    })
                    //If no pressed
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            return true;
        }

        if (id == R.id.item_share) {
            //Check if there are any items in the list. If there isn't show a snackbar and dont continue with rest of code.
            if (fireAdapter.getCount() == 0) {
                Toast.makeText(ProductListActivity.this, "No items in list", Toast.LENGTH_LONG).show();
                return true;
            }

            //Nake string where items are added
            String textToShare = "";

            //Loop through all items
            for (int i = 0; i<fireAdapter.getCount();i++)
            {
                //Get item and make name and quantity one string
                Product p = (Product) fireAdapter.getItem(i);
                String productString = "";
                if(i+1 == fireAdapter.getCount()){
                    productString =  p.toString();
                }
                else{
                    productString =  p.toString() + "\n";
                }

                //Add the product string to the entire list of products
                textToShare = textToShare + productString;
            }

            //A new Intent, which opens options for which app to send with. Add the string using putExtra.
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, textToShare);
            startActivity(intent);
        }

//        return super.onOptionsItemSelected(item);
        return false;
    }
}
