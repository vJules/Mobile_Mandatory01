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

        final EditText addQnt = (EditText) findViewById(R.id.addQnt);
        final EditText addText = (EditText) findViewById(R.id.addText);
        Button addButton = (Button) findViewById(R.id.addButton);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);

        FloatingActionButton fabadd = (FloatingActionButton) findViewById(R.id.fab_add);
        final EditText addInputText = new EditText(this);
        final EditText addInputQnt = new EditText(this);
        final LinearLayout layout = new LinearLayout(this);

        layout.setOrientation(LinearLayout.VERTICAL);

        addInputText.setHint("Name");
        layout.addView(addInputText);

        addInputQnt.setHint("Quantity");
        layout.addView(addInputQnt);

        android.support.v7.app.AlertDialog.Builder addAlertDialogBuild = new android.support.v7.app.AlertDialog.Builder(ProductListActivity.this)
                .setTitle("New Product")
                .setMessage("Add a new product!")
                .setView(layout)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(addInputText.getText().toString()) || TextUtils.isEmpty(addInputQnt.getText().toString())) {
                            Toast.makeText(ProductListActivity.this, "Please write something", Toast.LENGTH_LONG).show();
                            return;
                        }

                        Product pl = new Product(addInputText.getText().toString(), addInputQnt.getText().toString());
                        ref.push().setValue(pl);
                        fireAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.edit_text);
        final android.support.v7.app.AlertDialog addAlertDialog = addAlertDialogBuild.create();



        if (fabadd != null) {
            fabadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addAlertDialog.show();
                }
            });
        }


//        if (addButton != null) {
//            addButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    if (TextUtils.isEmpty(addText.getText().toString()) && TextUtils.isEmpty(addQnt.getText().toString())) {
//                        Toast.makeText(ProductListActivity.this, "Please write something", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//
//                    Product p = new Product(addText.getText().toString(), addQnt.getText().toString());
//                    ref.push().setValue(p);
//                    ref.getParent();
//                    fireAdapter.notifyDataSetChanged();
//
//                }
//            });
//        }
//
//
//        if (deleteButton != null) {
//            deleteButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    final int index = listView.getCheckedItemPosition();
//
//                    if (index < 0) {
//                        Toast.makeText(ProductListActivity.this, "No item selected", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                    final Product backup = fireAdapter.getItem(index); //get backup
//                    fireAdapter.getRef(index).setValue(null);
//                    final View parent = findViewById(R.id.layout);
//
//                    //                Toast.makeText(MainActivity.this, "Position: " + checkItem, Toast.LENGTH_LONG).show();
//                    fireAdapter.notifyDataSetChanged();
//                    Snackbar snackbar = Snackbar
//                            .make(parent, backup + "Item removed", Snackbar.LENGTH_LONG)
//                            .setAction("UNDO", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    //This code will ONLY be executed in case that
//                                    //the user has hit the UNDO button
//                                    ref.push().setValue(backup);
//                                    fireAdapter.notifyDataSetChanged();
//                                    Snackbar snackbar = Snackbar.make(parent, "Item restored!", Snackbar.LENGTH_SHORT);
//                                    //Show the user we have restored the name - but here
//                                    //on this snackbar there is NO UNDO - so not SetAction method is called
//                                    snackbar.show();
//                                }
//                            });
//
//                    snackbar.show();
//
//                    listView.setItemChecked(-1, true);
//                }
//            });
//        }

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
        final CharSequence colors[] = new CharSequence[] {"Edit", "Delete"};

        final android.support.v7.app.AlertDialog.Builder optionsBuilder = new android.support.v7.app.AlertDialog.Builder(ProductListActivity.this);


        optionsBuilder.setTitle("Product actions");
        optionsBuilder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Toast.makeText(ProductListActivity.this, "EDITIONG " + position, Toast.LENGTH_LONG).show();

                        break;

                    case 1:
                        // Your code when 2nd option seletced
                        Toast.makeText(ProductListActivity.this, "DELETING " + position, Toast.LENGTH_LONG).show();
                        deleteItem(position);
                        break;


                }
            }
        });
        optionsBuilder.show();
    }

    public void deleteItem(int position) {
        final Product backup = fireAdapter.getItem(position); //get backup
        fireAdapter.getRef(position).setValue(null);
        final View parent = findViewById(R.id.layout);

        //                Toast.makeText(MainActivity.this, "Position: " + checkItem, Toast.LENGTH_LONG).show();
        fireAdapter.notifyDataSetChanged();
        Snackbar snackbar = Snackbar
                .make(parent, backup + "Item removed", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //This code will ONLY be executed in case that
                        //the user has hit the UNDO button
                        ref.push().setValue(backup);
                        fireAdapter.notifyDataSetChanged();
                        Snackbar snackbar = Snackbar.make(parent, "Item restored!", Snackbar.LENGTH_SHORT);
                        //Show the user we have restored the name - but here
                        //on this snackbar there is NO UNDO - so not SetAction method is called
                        snackbar.show();
                    }
                });

        snackbar.show();

        listView.setItemChecked(-1, true);
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
//            Toast toast =
//                    Toast.makeText(getApplicationContext(), "back from preferences", Toast.LENGTH_LONG);
//            toast.setText("back from our preferences");
//            toast.show();
            //here you could put code to do something.......
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
        String email = prefs.getString("email", "");
        String sort = prefs.getString("sort", "");

        Toast.makeText(
                this,
                "Email: " + email + "\nOrder by: " + sort, Toast.LENGTH_SHORT).show();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            setPreferences();
            return true;
        }

        //noinspection SimplifiableIfStatement


        if (id == R.id.item_clear) {
            if (fireAdapter.getCount() == 0) {
                Toast.makeText(ProductListActivity.this, "No items in list", Toast.LENGTH_LONG).show();
                return true;
            }
            new AlertDialog.Builder(ProductListActivity.this)
                    .setTitle("Delete items")
                    .setMessage("Are you sure you want to delete all items?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ref.removeValue(null);
                            Toast.makeText(ProductListActivity.this, "All items removed ", Toast.LENGTH_LONG).show();
                            fireAdapter.notifyDataSetChanged();
                            listView.setItemChecked(-1, true);
                        }
                    })
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
            if (fireAdapter.getCount() == 0) {
                Toast.makeText(ProductListActivity.this, "No items in list", Toast.LENGTH_LONG).show();
                return true;
            }
            String textToShare = "";
            for (int i = 0; i<fireAdapter.getCount();i++)
            {
                Product p = (Product) fireAdapter.getItem(i);
                String productString = "";
                if(i+1 == fireAdapter.getCount()){
                    productString =  p.toString();
                }
                else{
                    productString =  p.toString() + "\n";
                }

                textToShare = textToShare + productString;
            }

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, textToShare);
            startActivity(intent);
        }

//        return super.onOptionsItemSelected(item);
        return false;
    }
}
