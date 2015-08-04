package com.example.pny.grocery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    ParseUser user = ParseUser.getCurrentUser();
    public void addItem(View view) {
        ParseObject testObject = new ParseObject(user.get("currentList").toString());
        EditText item = (EditText)findViewById(R.id.itemField);
        testObject.put("item", item.getText().toString());
        testObject.put("user", ParseUser.getCurrentUser().getUsername());
        item.setText("");
        testObject.saveInBackground();
    }

    public void switchList(View view) {
        final Intent intent = new Intent(MainActivity.this, listview.class);
        startActivity(intent);
    }

    public void viewItems(View view) {
        if(((LinearLayout) findViewById(R.id.displayItems)).getChildCount() > 0)
            ((LinearLayout) findViewById(R.id.displayItems)).removeAllViews();
        if (user.get("currentList") == null) {
            LinearLayout output = (LinearLayout) findViewById(R.id.displayItems);
            TextView text = new TextView(this);
            text.setText("No list selected, please select a list");
            output.addView(text);
            return;
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery(user.get("currentList").toString());
        query.selectKeys(Arrays.asList("item", "user"));
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> groceryList, ParseException e) {
                LinearLayout llview = (LinearLayout) findViewById(R.id.displayItems);
                TextView text = new TextView(MainActivity.this);
                text.setText("This list is empty, please add items.");

                if (e == null) {
                    if (groceryList.size() == 0)
                        llview.addView(text);
                    for (int i = 0; i < groceryList.size(); i++) {
                        String item = groceryList.get(i).get("item").toString();
                        final String user = groceryList.get(i).get("user").toString();
                        final String objectId = groceryList.get(i).getObjectId();
                        Button button = new Button(MainActivity.this);
                        button.setText(item);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                displayListItemOptions(user, objectId);
                            }
                        });
                        llview.addView(button);
                    }
                } else {
                    text.setText("Error retrieving data, please try again.  Most likely no list");
                    llview.addView(text);
                }
            }
        });
    }

    private void displayListItemOptions(String user, final String objectId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Added by: " + user);
        builder.setItems(new CharSequence[]
                        {"Delete item"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseUser.getCurrentUser().get("currentList").toString());
                                query.whereEqualTo("objectId", objectId);
                                query.getFirstInBackground(new GetCallback<ParseObject>() {
                                    public void done(ParseObject object, ParseException e) {
                                        if (object == null) {
                                        } else {
                                            try {
                                                object.delete();
                                                object.saveInBackground();
                                                viewItems(null);
                                            } catch (ParseException e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                    }
                                });
                        }
                    }
                });
        builder.create().show();
    }

    public void logout(View view) {
        ParseUser.logOut();
        final Intent intent = new Intent(MainActivity.this, loginchooser.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
