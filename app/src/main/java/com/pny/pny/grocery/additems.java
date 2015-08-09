package com.pny.pny.grocery;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class additems extends ActionBarActivity {
    //Declare the current user
    ParseUser user = ParseUser.getCurrentUser();
    final toastDisplay toast = new toastDisplay();

    //Add an item to the current list
    public void addItem(View view) {
        //Get the list from the database
        ParseObject listObject = new ParseObject(user.get("currentList").toString());

        //Get input fields
        EditText itemField = (EditText)findViewById(R.id.itemField);
        String item = itemField.getText().toString();

        EditText descriptionField = (EditText)findViewById(R.id.itemDescription);
        String description = descriptionField.getText().toString();

        //Null check
        if (item.matches("")) {
            toast.display("Please enter an item", getApplicationContext());
        } else {
            //Add item to the list
            listObject.put("item", item);
            listObject.put("description", description);
            listObject.put("user", ParseUser.getCurrentUser().getUsername());
            itemField.setText("");
            descriptionField.setText("");
            listObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        toast.display("Error adding item, please try again.", getApplicationContext());
                    }
                }
            });
        }
    }

    //Back button, go back to main activity
    public void goBack(View view) {
        final Intent intent = new Intent(additems.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.leftin, R.anim.leftout);
        setContentView(R.layout.activity_additems);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_additems, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
