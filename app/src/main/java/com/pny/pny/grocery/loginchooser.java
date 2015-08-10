package com.pny.pny.grocery;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;


public class loginchooser extends ActionBarActivity {
    ParseUser user = ParseUser.getCurrentUser();

    //Go to the login activity
    public void login (View view) {
        final Intent intent = new Intent(this, login.class);
        startActivity(intent);
    }

    //Go to the create user activity
    public void createUser(View view) {
        final Intent intent = new Intent(this, createuser.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.rightin, R.anim.rightout);
        setContentView(R.layout.activity_loginchooser);

        final Intent intent = new Intent(this, MainActivity.class);

        //If they are already signed in, go to their profile.
        if (user != null) {
            ParseUser.becomeInBackground(user.getSessionToken(), new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                    } else {
                        toastDisplay display = new toastDisplay();
                        display.display("Error signing in, please try again", getApplicationContext());
                    }
                }
            });
            startActivity(intent);
        } else {
            // show the signup or login screen
        }
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
