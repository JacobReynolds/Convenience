package com.pny.pny.grocery;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;


public class viewitems extends ActionBarActivity {
    ParseUser user = ParseUser.getCurrentUser();

    public void goBack(View view) {
        final Intent intent = new Intent(viewitems.this, MainActivity.class);
        startActivity(intent);
    }

    public void viewItems() {
        if(((LinearLayout) findViewById(R.id.displayItems)).getChildCount() > 0)
            ((LinearLayout) findViewById(R.id.displayItems)).removeAllViews();
        if (user.get("currentList") == null) {
            LinearLayout output = (LinearLayout) findViewById(R.id.displayItems);
            TextView text = new TextView(this);
            text.setText("No list selected, please select a list");
            text.setTextColor(Color.WHITE);
            output.addView(text);
            return;
        }
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
                        String item = groceryList.get(i).get("item").toString();
                        final String description = groceryList.get(i).get("description").toString();
                        final String user = groceryList.get(i).get("user").toString();
                        final String objectId = groceryList.get(i).getObjectId();
                        Button button = new Button(viewitems.this);
                        button.setText(item);
                        button.setBackground(getResources().getDrawable(R.drawable.smoothcorner));
                        button.setTextColor(Color.WHITE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                displayListItemOptions(user, description, objectId);
                            }
                        });
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

    private void displayListItemOptions(String user, String description, final String objectId) {
        final Dialog builder = new Dialog(this);
        builder.setCanceledOnTouchOutside(true);
        builder.setTitle("Added by: " + user);
        LinearLayout layout = new LinearLayout(this);
        TextView layoutDescription = new TextView(this);
        layoutDescription.setText(description);
        layoutDescription.setTextSize(16);
        layoutDescription.setSingleLine(false);
        layoutDescription.setTypeface(Typeface.SANS_SERIF);
        Button layoutButton = new Button(this);
        layoutButton.setText("Delete item");
        layoutButton.setTypeface(Typeface.SANS_SERIF);
        layoutButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        layoutButton.setOnClickListener(new View.OnClickListener() {
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
                                object.saveInBackground();
                                builder.dismiss();
                                viewItems();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
        layout.addView(layoutDescription);
        layout.addView(layoutButton);
        builder.addContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        builder.show();
    }

    public void deleteAll(View view) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseUser.getCurrentUser().get("currentList").toString());
        query.findInBackground(new FindCallback<ParseObject>() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void done(List<ParseObject> groceryList, ParseException e) {
                if (e == null) {
                    ParseObject.deleteAllInBackground(groceryList);
                    viewItems();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
