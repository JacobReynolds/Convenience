package com.pny.pny.grocery;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class viewitems extends ActionBarActivity {
    ParseUser user = ParseUser.getCurrentUser();
    toastDisplay toast = new toastDisplay();

    //Back button, go back to main activity
    public void goBack(View view) {
        final Intent intent = new Intent(viewitems.this, MainActivity.class);
        startActivity(intent);
    }

    //View the items in the current list
    public void viewItems() {
        //Delete any items that might be there currently
        if(((LinearLayout) findViewById(R.id.displayItems)).getChildCount() > 0)
            ((LinearLayout) findViewById(R.id.displayItems)).removeAllViews();

        //If there is no list, tell them to choose one.
        if (user.get("currentList") == null) {
            LinearLayout output = (LinearLayout) findViewById(R.id.displayItems);
            TextView text = new TextView(this);
            text.setText("No list selected, please select a list");
            text.setTextColor(Color.WHITE);
            output.addView(text);
            return;
        }

        //Get the current list from the database
        ParseQuery<ParseObject> query = ParseQuery.getQuery(user.get("currentList").toString());
        query.selectKeys(Arrays.asList("item", "description", "user"));
        query.findInBackground(new FindCallback<ParseObject>() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void done(List<ParseObject> groceryList, ParseException e) {
                LinearLayout llview = (LinearLayout) findViewById(R.id.displayItems);
                TextView text = new TextView(viewitems.this);
                text.setText("This list is empty, please add items.");
                text.setTextColor(Color.WHITE);

                if (e == null) {
                    if (groceryList.size() == 0)
                        llview.addView(text);
                    for (int i = 0; i < groceryList.size(); i++) {
                        //Display each item, and append the data to it.
                        final String item = groceryList.get(i).get("item").toString();
                        final String description = groceryList.get(i).get("description").toString();
                        final String user = groceryList.get(i).get("user").toString();
                        final String objectId = groceryList.get(i).getObjectId();

                        //Give the button some life
                        Button button = new Button(viewitems.this);
                        button.setText(item);
                        button.setBackground(getResources().getDrawable(R.drawable.clickablebutton));
                        button.setTextColor(Color.WHITE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                displayListItemOptions(user, description, item, objectId);
                            }
                        });

                        //Give it a little spacing
                        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        buttonLayoutParams.setMargins(0, 0, 0, 20);
                        button.setLayoutParams(buttonLayoutParams);
                        llview.addView(button);
                    }
                } else {
                    text.setText("Error retrieving data, please try again.  Most likely no list");
                    llview.addView(text);
                }
            }
        });
    }

    //Display the dialog for each item
    private void displayListItemOptions(String user, String description, final String item, final String objectId) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.itemoptions);

        TextView itemTitle = (TextView)dialog.findViewById(R.id.itemTitle);
        itemTitle.append(item);

        TextView addedBy = (TextView)dialog.findViewById(R.id.itemAddedBy);
        addedBy.append(user);

        TextView itemDescription = (TextView)dialog.findViewById(R.id.itemDescription);

        if (description.length() > 0){
            itemDescription.append(description);
        } else {
            itemDescription.setText("");
        }

        //Set listeners for the delete button
        Button button = (Button)dialog.findViewById(R.id.itemViewDelete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseUser.getCurrentUser().get("currentList").toString());
                query.whereEqualTo("objectId", objectId);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (object == null) {
                        } else {
                            try {
                                object.delete();
                                object.saveEventually();
                                dialog.dismiss();
                                viewItems();
                            } catch (ParseException el) {
                            }
                        }
                    }
                });
                    dialog.cancel();
            }
        });

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    //Delete all items in the current list
    //May add confirm dialog to this soon
    public void deleteAll(View view) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseUser.getCurrentUser().get("currentList").toString());
        query.findInBackground(new FindCallback<ParseObject>() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void done(List<ParseObject> groceryList, ParseException e) {
                if (e == null) {
                    ParseObject.deleteAllInBackground(groceryList, new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                viewItems();
                            } else {
                                toast.display("Error, please try again", getApplicationContext());
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.leftin, R.anim.leftout);
        setContentView(R.layout.activity_viewitems);

        viewItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_viewitems, menu);
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
