package org.projects.shoppinglist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

String TAG = "tag";
    ArrayAdapter<String> adapter;
    ListView listView;
    ArrayList<String> bag = new ArrayList<String>();
    public ArrayAdapter getMyAdapter()
    {
        return adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (savedInstanceState!=null){
            bag = savedInstanceState.getStringArrayList("savedFood");
        }else {
            bag = new ArrayList<>();
        }

        //getting our listiew - you can check the ID in the xml to see that it
        //is indeed specified as "list"
        listView = (ListView) findViewById(R.id.list);
        //here we create a new adapter linking the bag and the
        //listview
        adapter =  new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, bag);

        adapter.notifyDataSetChanged();

        //setting the adapter on the listview
        listView.setAdapter(adapter);
        //here we set the choice mode - meaning in this case we can
        //only select one item at a time.

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        final  EditText addQnt = (EditText) findViewById(R.id.addQnt);
        final  EditText addText = (EditText) findViewById(R.id.addText);
        Button addButton = (Button) findViewById(R.id.addButton);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);



        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (addText.getText().toString().isEmpty() && addQnt.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Please write something", Toast.LENGTH_LONG).show();
                    return;
                }

                bag.add(addText.getText().toString() + " " + addQnt.getText().toString());
                //The next line is needed in order to say to the ListView
                //that the data has changed - we have added stuff now!
                Log.d("MyApp", "The listview is updated");
                adapter.notifyDataSetChanged();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkItem = listView.getCheckedItemPosition();

                if (checkItem < 0) {
                    Toast.makeText(MainActivity.this, "No item selected", Toast.LENGTH_LONG).show();
                    return;
                }

                bag.remove(checkItem);
                Toast.makeText(MainActivity.this, "Position: " + checkItem, Toast.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
                listView.setItemChecked(-1, true);
            }
        });

        getPreferences();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("savedFood", bag);
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
                    Toast.makeText(getApplicationContext(), "back from preferences", Toast.LENGTH_LONG);
            toast.setText("back from our preferences");
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

        //We read the shared preferences from the
        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String email = prefs.getString("email", "");
        String gender = prefs.getString("gender", "");
        boolean soundEnabled = prefs.getBoolean("sound", false);

        Toast.makeText(
                this,
                "Email: " + email + "\nGender: " + gender + "\nSound Enabled: "
                        + soundEnabled, Toast.LENGTH_SHORT).show();
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
            if (bag.size() == 0){
                Toast.makeText(MainActivity.this, "No items in list", Toast.LENGTH_LONG).show();
                return true;
            }
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete items")
                    .setMessage("Are you sure you want to delete all items?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            bag.clear();
                            Toast.makeText(MainActivity.this, "All items removed ", Toast.LENGTH_LONG).show();
                            adapter.notifyDataSetChanged();
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

//        return super.onOptionsItemSelected(item);
        return false;
    }

}