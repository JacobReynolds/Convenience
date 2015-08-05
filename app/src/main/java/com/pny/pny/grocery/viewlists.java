package com.pny.pny.grocery;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class viewlists extends ActionBarActivity {
    ParseUser user = ParseUser.getCurrentUser();

    public void goBack(View view) {
        final Intent intent = new Intent(viewlists.this, MainActivity.class);
        startActivity(intent);
    }

    public void showLists() {
        ArrayList databases = new ArrayList();
        ArrayList nicknames = new ArrayList();
        databases = (ArrayList) user.get("databaseList");
        nicknames = (ArrayList) user.get("nicknameList");
        LinearLayout listView = (LinearLayout) findViewById(R.id.listLinearLayout);
        if((listView).getChildCount() > 0)
            listView.removeAllViews();
        if (databases == null) {
            createDatabaseText();
        } else {
            for (int i = 0; i < databases.size(); i++) {
                addDisplayItem(listView, databases.get(i).toString(), nicknames.get(i).toString());
            }
            createDatabaseText();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void addDisplayItem (LinearLayout view, final String hash, final String nickname) {
        Button button = new Button(this);
        button.setText(nickname);
        button.setTextColor(Color.WHITE);
        button.setBackground(getResources().getDrawable(R.drawable.smoothcorner));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayListOptions(hash, nickname);
            }
        });
        LinearLayout.LayoutParams buttonLayoutParams = new  LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        buttonLayoutParams.setMargins(0, 0, 0, 20);
        button.setLayoutParams(buttonLayoutParams);
        view.addView(button);
    }

    private void displayListOptions(final String hash, final String nickname) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(new CharSequence[]
                        {"Copy list hash", "Set current list", "Delete list"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                copyListToClipboard(hash);
                                break;
                            case 1:
                                user.put("currentList", hash);
                                user.saveInBackground();
                                final Intent intent = new Intent(viewlists.this, MainActivity.class);
                                startActivity(intent);
                                break;
                            case 2:
                                removeListFromUser(hash, nickname);
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    private void removeListFromUser(String list, String nickname) {
        ArrayList lists = (ArrayList) user.get("databaseList");
        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i).toString().equals(list)) {
                lists.remove(i);
                user.remove("currentList");
                user.put("currentList", lists);
                ArrayList nicknames = (ArrayList) user.get("nicknameList");
                nicknames.remove(nickname);
                user.remove("nicknameList");
                user.put("nicknameList", nicknames);
                user.saveInBackground();
                showLists();
                return;
            }
        }
    }

    private void copyListToClipboard(String list) {
        @SuppressWarnings("deprecation")
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboard.setText(list);
    }

    public void makeNewDatabase(final String nickname) {
        SecureRandom random = new SecureRandom();
        final String listHash = new BigInteger(130, random).toString(32);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("databaseList");
        query.selectKeys(Arrays.asList("hash"));
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> databaseNames, ParseException e) {
                if (e == null) {
                    //Since they are zero-indexed we can just take the current length
                    for (int i = 0; i < databaseNames.size(); i++) {
                        if (databaseNames.get(i).toString().equals(listHash)) {
                            makeNewDatabase(nickname);
                            return;
                        }
                    }
                    ParseObject realDatabaseList = new ParseObject("databaseList");
                    realDatabaseList.put("hash", listHash);
                    //Having issues storying key-value pairs
                    //So since they save async, they will always be in the same array positions
                    //This will work until I find a fix
                    ArrayList list = (ArrayList) user.get("databaseList") != null ? (ArrayList) user.get("databaseList") : new ArrayList();
                    ArrayList nickList = (ArrayList) user.get("nicknameList") != null ? (ArrayList) user.get("nicknameList") : new ArrayList();
                    list.add(listHash);
                    nickList.add(nickname);
                    user.put("databaseList", list);
                    user.put("nicknameList", nickList);
                    String test = list.get(list.size() - 1).toString();
                    user.put("currentList", list.get(list.size() - 1).toString());
                    user.saveInBackground();
                    realDatabaseList.saveInBackground();
                    final Intent intent = new Intent(viewlists.this, MainActivity.class);
                    startActivity(intent);
                } else {
                }
            }
        });

    }

    public void createDatabaseText() {
        LinearLayout ll = (LinearLayout)findViewById(R.id.listLinearLayout);

        // add button
        Button b = new Button(this);
        b.setText("Create new list");
        b.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Button button = new Button(this);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(viewlists.this);
                final EditText input = new EditText(viewlists.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint("List nickname");
                builder1.setView(input);
                builder1.setCancelable(true);
                builder1.setPositiveButton("Submit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                makeNewDatabase(input.getText().toString());
                                dialog.cancel();
                            }
                        });
                builder1.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
        ll.addView(b);
        b = new Button(this);
        b.setText("Add list");
        b.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        button = new Button(this);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(viewlists.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                LinearLayout layout = new LinearLayout(viewlists.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText hashBox = new EditText(viewlists.this);
                hashBox.setHint("List hash");
                layout.addView(hashBox);

                final EditText nicknameBox = new EditText(viewlists.this);
                nicknameBox.setHint("List nickname");
                layout.addView(nicknameBox);
                builder1.setView(layout);
                builder1.setCancelable(true);
                builder1.setPositiveButton("Submit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                addList(hashBox.getText().toString(), nicknameBox.getText().toString(), dialog);
                            }
                        });
                builder1.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
        ll.addView(b);
    }

    private void addList(final String listHash, final String nickname, final DialogInterface dialog) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("databaseList");
        query.selectKeys(Arrays.asList("hash"));
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> databaseNames, ParseException e) {
                if (e == null) {
                    //Since they are zero-indexed we can just take the current length
                    for (int i = 0; i < databaseNames.size(); i++) {
                        if (databaseNames.get(i).get("hash").toString().equals(listHash)) {
                            dialog.cancel();
                            ArrayList list = (ArrayList) user.get("databaseList") != null ? (ArrayList) user.get("databaseList") : new ArrayList();
                            ArrayList nicknameList = (ArrayList) user.get("nicknameList") != null ? (ArrayList) user.get("nicknameList") : new ArrayList();
                            if(list.contains(listHash))
                                return;
                            list.add(listHash);
                            nicknameList.add(nickname);
                            user.put("databaseList", list);
                            user.put("nicknameList", nicknameList);
                            user.put("currentList", list.get(list.size() - 1));
                            user.saveInBackground();
                            showLists();
                            return;
                        }
                    }
                        Context context = getApplicationContext();
                        CharSequence text = "Invalid list name";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewlists);

        showLists();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_view, menu);
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
