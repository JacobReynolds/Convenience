package com.pny.pny.grocery;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class loginchooser extends ActionBarActivity {



    public void login (View view) {
        final Intent intent = new Intent(this, login.class);
        startActivity(intent);
    }

    public void createUser(View view) {
        final Intent intent = new Intent(this, createuser.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.rightin, R.anim.rightout);
        setContentView(R.layout.activity_loginchooser);

        ParseUser currentUser = ParseUser.getCurrentUser();
        final Intent intent = new Intent(this, MainActivity.class);
        if (currentUser != null) {
            ParseUser.becomeInBackground(currentUser.getSessionToken(), new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                    } else {
                        // The token could not be validated.
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
