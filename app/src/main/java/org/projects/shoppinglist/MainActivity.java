package org.projects.shoppinglist;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;

import org.projects.shoppinglist.model.ShoppingList;
import org.projects.shoppinglist.products.ProductListActivity;
import org.projects.shoppinglist.settings.SettingsActivity;
import org.projects.shoppinglist.utils.IntentStarter;


public class MainActivity extends AppCompatActivity {
    String TAG = "tag";
    ListView listView;
    FirebaseListAdapter<ShoppingList> fireAdapter;
    Firebase ref;

    IntentStarter intentStarter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intentStarter = new IntentStarter();

        listView = (ListView) findViewById(R.id.list);

        //Create new reference to firebase
        ref = new Firebase("https://eaashoppinglist.firebaseio.com/shoppingLists");

        //Create an adapter where used class and layout is specified
        fireAdapter = new FirebaseListAdapter<ShoppingList>(this, ShoppingList.class, android.R.layout.simple_list_item_1, ref) {
            @Override
            protected void populateView(View view, ShoppingList shoppingList, int i) {
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setText(shoppingList.getName());
            }
        };

        listView.setAdapter(fireAdapter);

        FloatingActionButton fabadd = (FloatingActionButton) findViewById(R.id.fab_add);

        final EditText addInput = new EditText(this);

        AlertDialog.Builder addAlertDialogBuild = new AlertDialog.Builder(MainActivity.this)
                .setTitle("New List")
                .setMessage("Add a new list!")
                .setView(addInput)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(addInput.getText().toString())) {
                            Toast.makeText(MainActivity.this, "Please write something", Toast.LENGTH_LONG).show();
                            return;
                        }

                        ShoppingList pl = new ShoppingList(addInput.getText().toString());
                        ref.push().setValue(pl);
                        fireAdapter.notifyDataSetChanged();
                        addInput.setText("");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        addInput.setText("");
                    }
                })
                .setIcon(android.R.drawable.edit_text);
        final AlertDialog addAlertDialog = addAlertDialogBuild.create();


        if (fabadd != null) {
            fabadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addAlertDialog.show();
                }
            });
        }


//        if (fabadd != null) {
//            fabadd.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    if (TextUtils.isEmpty(addText.getText().toString())) {
//                        Toast.makeText(MainActivity.this, "Please write something", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//
//                    ShoppingList pl = new ShoppingList(addText.getText().toString());
//                    ref.push().setValue(pl);
//                    fireAdapter.notifyDataSetChanged();
//
//                }
//            });
//        }


//        if (deleteButton != null) {
//            deleteButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    final int index = listView.getCheckedItemPosition();
//
//                    if (index < 0) {
//                        Toast.makeText(MainActivity.this, "No item selected", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                    final ShoppingList backup = fireAdapter.getItem(index); //get backup
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


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intentStarter.showShoppingList(MainActivity.this, fireAdapter.getRef(position).getKey());
            }
        });






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

        final AlertDialog.Builder optionsBuilder = new AlertDialog.Builder(MainActivity.this);


        optionsBuilder.setTitle("Product actions");
        optionsBuilder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Toast.makeText(MainActivity.this, "Editing not functional", Toast.LENGTH_LONG).show();

                        break;

                    case 1:
                        // Your code when 2nd option seletced
                        deleteItem(position);
                        break;


                }
            }
        });
        optionsBuilder.show();
    }

    public void deleteItem(int position){
        final ShoppingList backup = fireAdapter.getItem(position); //get backup
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //This will be called when other activities in our application
    //are finished.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1) //exited our preference screen
        {
            Toast toast =
                    Toast.makeText(getApplicationContext(), "Settings saved", Toast.LENGTH_LONG);
            toast.show();
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

        TextView welcomeView = (TextView) findViewById(R.id.welcome_text);

        //We read the shared preferences from the
        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String email = prefs.getString("email", "");

        welcomeView.setText("Logged in as " + email);

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



//        if (id == R.id.item_clear) {
//            if (productBag.size() == 0){
//                Toast.makeText(MainActivity.this, "No items in list", Toast.LENGTH_LONG).show();
//                return true;
//            }
//            new AlertDialog.Builder(MainActivity.this)
//                    .setTitle("Delete items")
//                    .setMessage("Are you sure you want to delete all items?")
//                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            productBag.clear();
//                            Toast.makeText(MainActivity.this, "All items removed ", Toast.LENGTH_LONG).show();
//                            productAdapter.notifyDataSetChanged();
//                            listView.setItemChecked(-1, true);
//                        }
//                    })
//                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // do nothing
//                        }
//                    })
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .show();

            return true;
        }

//        return super.onOptionsItemSelected(item);
    }


