package com.pny.pny.grocery;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.parse.ParseUser;


public class MainActivity extends ActionBarActivity {
    ParseUser user = ParseUser.getCurrentUser();

    public void showHelp(View view) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.help);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void goToAddItems(View view) {
        final Intent intent = new Intent(MainActivity.this, additems.class);
        startActivity(intent);
    }

    public void goToViewItems(View view) {
        final Intent intent = new Intent(MainActivity.this, viewitems.class);
        startActivity(intent);
    }

    public void switchList(View view) {
        final Intent intent = new Intent(MainActivity.this, viewlists.class);
        startActivity(intent);
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
