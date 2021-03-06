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


public class login extends ActionBarActivity {
    //Login to a user profile
    public void login(View view) {
        //Get input fields
        EditText usernameField = (EditText)findViewById(R.id.usernameText);
        String username = usernameField.getText().toString();

        EditText passwordField = (EditText)findViewById(R.id.passwordText);
        String password = passwordField.getText().toString();

        final Intent intent = new Intent(this, MainActivity.class);
        ParseUser.logInInBackground(username.toLowerCase(), password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    startActivity(intent);
                } else {
                    //May want to change output depending on error here
                    TextView invalidLogin = (TextView)findViewById(R.id.invalidLogin);
                    invalidLogin.setText("Invalid login, please try again");
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.leftin, R.anim.leftout);
        setContentView(R.layout.activity_login);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
