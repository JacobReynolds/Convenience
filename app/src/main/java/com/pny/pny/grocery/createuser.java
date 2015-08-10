package com.pny.pny.grocery;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class createuser extends ActionBarActivity {
    //Declare the current user
    ParseUser user = new ParseUser();

    //Create a new user
    public void createUser(View view) {
        //Get input fields
        EditText usernameField = (EditText)findViewById(R.id.usernameText);
        String username = usernameField.getText().toString();

        EditText passwordField = (EditText)findViewById(R.id.passwordText);
        String password = passwordField.getText().toString();

        EditText passwordVerify = (EditText)findViewById(R.id.passwordVerifyText);
        final TextView errorView = (TextView) findViewById(R.id.incorrectPassword);

        //Null checking, and comparing passwords
        if (!password.equals(passwordVerify.getText().toString())) {
            errorView.setText("");
            errorView.setText("Passwords do not match, please try again.");
            return;
        } else if(password.trim().length() == 0) {
            errorView.setText("");
            errorView.setText("Password must contain letters.");
            return;
        } else if (username.matches("")) {
            errorView.setText("");
            errorView.setText("Please enter a username.");
        } else if (password.matches("")){
            errorView.setText("");
            errorView.setText("Please enter a password.");
        }

        //Assign to the user
        user.setUsername(username.toLowerCase());
        user.setPassword(password);
        final Intent intent = new Intent(this, MainActivity.class);

        //Sign up, once finished go to main activity.
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    ParseUser.becomeInBackground(user.getSessionToken(), new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                viewlists newDatabase = new viewlists();
                                newDatabase.makeNewDatabase("My First List", false, 0);
                                //This way MainActivity's onCreatem method can grab this
                                user.put("currentListNickname", "My First List");
                                startActivity(intent);
                            } else {
                                errorView.setText("");
                                errorView.setText("Error, please try again.");
                            }
                        }
                    });
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    switch(e.getCode()) {
                        case 202:
                            errorView.setText("");
                            errorView.setText("Username already taken.");
                    }
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.leftin, R.anim.leftout);

        setContentView(R.layout.activity_create_user);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_user, menu);
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
