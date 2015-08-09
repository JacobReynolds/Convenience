package com.pny.pny.grocery;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
        if (databases != null) {
            for (int i = 0; i < databases.size(); i++) {
                addDisplayItem(listView, databases.get(i).toString(), nicknames.get(i).toString());
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void addDisplayItem (LinearLayout view, final String hash, final String nickname) {
        Button button = new Button(this);
        button.setText(nickname);
        button.setTextColor(Color.WHITE);
        button.setBackground(getResources().getDrawable(R.drawable.buttonunclicked));
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
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.listoptions);
        Button hashButton = (Button)dialog.findViewById(R.id.copyListHash);
        Button setButton = (Button)dialog.findViewById(R.id.setCurrentList);
        Button deleteButton = (Button)dialog.findViewById(R.id.deleteList);
        hashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyListToClipboard(hash);
                dialog.dismiss();
            }
        });
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.put("currentList", hash);
                user.put("currentListNickname", nickname);
                user.saveEventually();
                final Intent intent = new Intent(viewlists.this, MainActivity.class);
                startActivity(intent);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeListFromUser(hash, nickname);
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void removeListFromUser(String list, String nickname) {
        ArrayList lists = (ArrayList) user.get("databaseList");
        if (lists.size() == 1) {
            Context context = getApplicationContext();
            CharSequence text = "Error: must have one list";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i).toString().equals(list)) {
                ArrayList nicknames = (ArrayList) user.get("nicknameList");
                nicknames.remove(nickname);
                lists.remove(i);
                String test = user.get("currentList").toString();
                if (list.equals(user.get("currentList").toString())){
                    user.put("currentList", lists.get(0));
                    user.put("currentListNickname", nicknames.get(0));
                }
                user.remove("databaseList");
                user.put("databaseList", lists);
                user.remove("nicknameList");
                user.put("nicknameList", nicknames);
                user.saveEventually();
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

    public void makeNewDatabase(final String nickname, final boolean redirect, final int count) {
        SecureRandom random = new SecureRandom();
        final String listHash = new BigInteger(130, random).toString(32);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("databaseList");
        query.selectKeys(Arrays.asList("hash"));
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> databaseNames, ParseException e) {
                if (e == null) {
                    //Since they are zero-indexed we can just take the current length
                    for (int i = 0; i < databaseNames.size(); i++) {
                        if (databaseNames.get(i).get("hash").toString().equals(listHash)) {
                            makeNewDatabase(nickname, true, 0);
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
                    user.put("currentList", list.get(list.size() - 1).toString());
                    user.put("currentListNickname", nickname);
                    user.saveEventually();
                    realDatabaseList.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                if (count > 5) {
                                    Context context = getApplicationContext();
                                    CharSequence text = "Error connecting to servers, please try again later";
                                    int duration = Toast.LENGTH_LONG;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                } else {
                                    makeNewDatabase(nickname, redirect, (count > -1 ? count + 1 : 0));
                                }
                            }
                        }
                    });
                    if (redirect) {
                        final Intent intent = new Intent(viewlists.this, MainActivity.class);
                        startActivity(intent);
                    }
                    return;
                } else {
                }
            }
        });

    }

    public void addListDialog(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.addlist);
        Button button = (Button)dialog.findViewById(R.id.addListButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nickname = (EditText) dialog.findViewById(R.id.addListNickname);
                ArrayList nicknames = (ArrayList) user.get("nicknameList");
                if (nicknames.contains(nickname.getText().toString())) {
                    Context context = getApplicationContext();
                    CharSequence text = "Nickname already used";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    return;
                } else if (nickname.getText().toString().matches("")) {
                    Context context = getApplicationContext();
                    CharSequence text = "Please enter a nickname";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    return;
                } else {
                    makeNewDatabase(nickname.getText().toString(), true, 0);
                    dialog.cancel();
                }
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void joinListDialog(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.joinlist);
        Button button = (Button)dialog.findViewById(R.id.joinListButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("databaseList");
                query.selectKeys(Arrays.asList("hash"));
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> databaseNames, ParseException e) {
                        if (e == null) {
                            EditText nickname = (EditText) dialog.findViewById(R.id.joinListNickname);
                            EditText hash = (EditText) dialog.findViewById(R.id.joinListHash);
                            ArrayList nicknames = (ArrayList) user.get("nicknameList");
                            ArrayList databases = (ArrayList) user.get("databaseList");
                            ArrayList validDatabases = new ArrayList();
                            for (int i = 0; i < databaseNames.size(); i++) {
                                validDatabases.add(databaseNames.get(i).get("hash").toString());
                            }
                            if (!validDatabases.contains(hash.getText().toString())) {
                                Context context = getApplicationContext();
                                CharSequence text = "Please enter a valid hash";
                                int duration = Toast.LENGTH_LONG;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            } else if (nicknames.contains(nickname.getText().toString())) {
                                Context context = getApplicationContext();
                                CharSequence text = "Nickname already used";
                                int duration = Toast.LENGTH_LONG;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            } else if (nickname.getText().toString().matches("")) {
                                Context context = getApplicationContext();
                                CharSequence text = "Please enter a nickname";
                                int duration = Toast.LENGTH_LONG;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            } else if (hash.getText().toString().matches("")) {
                                Context context = getApplicationContext();
                                CharSequence text = "Please enter a hash";
                                int duration = Toast.LENGTH_LONG;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            } else if (databases.contains(hash.getText().toString())) {
                                Context context = getApplicationContext();
                                CharSequence text = "List already joined";
                                int duration = Toast.LENGTH_LONG;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            } else {
                                addList(hash.getText().toString(), nickname.getText().toString());
                                dialog.cancel();
                            }
                        }
                    }
                });
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void addList(final String listHash, final String nickname) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("databaseList");
        query.selectKeys(Arrays.asList("hash"));
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> databaseNames, ParseException e) {
                if (e == null) {
                    //Since they are zero-indexed we can just take the current length
                    for (int i = 0; i < databaseNames.size(); i++) {
                        if (databaseNames.get(i).get("hash").toString().equals(listHash)) {
                            ArrayList list = (ArrayList) user.get("databaseList") != null ? (ArrayList) user.get("databaseList") : new ArrayList();
                            ArrayList nicknameList = (ArrayList) user.get("nicknameList") != null ? (ArrayList) user.get("nicknameList") : new ArrayList();
                            if(list.contains(listHash))
                                return;
                            list.add(listHash);
                            nicknameList.add(nickname);
                            user.put("databaseList", list);
                            user.put("nicknameList", nicknameList);
                            user.put("currentList", list.get(list.size() - 1));
                            user.put("currentListNickname", nickname);
                            user.saveEventually();
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
        overridePendingTransition(R.anim.leftin, R.anim.leftout);
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
