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
    ParseUser user = new ParseUser();

    public void goBack(View view) {
        final Intent intent = new Intent(createuser.this, loginchooser.class);
        startActivity(intent);
    }

    public void createUser(View view) {
        EditText username = (EditText)findViewById(R.id.usernameText);
        EditText password = (EditText)findViewById(R.id.passwordText);
        EditText passwordVerify = (EditText)findViewById(R.id.passwordVerifyText);
        TextView errorView = (TextView) findViewById(R.id.incorrectPassword);
        if (!password.getText().toString().equals(passwordVerify.getText().toString())) {
            errorView.setText("");
            errorView.setText("Passwords do not match, please try again.");
            return;
        }
        user.setUsername(username.getText().toString());
        user.setPassword(password.getText().toString());
        final Intent intent = new Intent(this, MainActivity.class);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    ParseUser.becomeInBackground(ParseUser.getCurrentUser().getSessionToken(), new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                startActivity(intent);
                            } else {
                                // The token could not be validated.
                            }
                        }
                    });
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
