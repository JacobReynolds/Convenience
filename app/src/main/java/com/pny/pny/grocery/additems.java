package com.pny.pny.grocery;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.parse.ParseObject;
import com.parse.ParseUser;


public class additems extends ActionBarActivity {
    ParseUser user = ParseUser.getCurrentUser();
    public void addItem(View view) {
        ParseObject listObject = new ParseObject(user.get("currentList").toString());
        EditText item = (EditText)findViewById(R.id.itemField);
        EditText description = (EditText)findViewById(R.id.itemDescription);
        listObject.put("item", item.getText().toString());
        listObject.put("description", description.getText().toString());
        listObject.put("user", ParseUser.getCurrentUser().getUsername());
        item.setText("");
        description.setText("");
        listObject.saveInBackground();
    }

    public void goBack(View view) {
        final Intent intent = new Intent(additems.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
