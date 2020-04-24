package com.example.create_a_scribe;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


//This class is the activity that points to the actual DrawPad canvas to allow the user to paint using their touch input
public class DrawPadStartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_pad);//sets the view to the drawPad once this activity is created

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);//sets the toolbar that you want to be shown
        setSupportActionBar(toolbar);//sets the toolbar to serve as the actionbar, or the bar on top of each activity with the name of the activity and the primary color

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//sets the menu that will be seen in the action bar when the drawPad is activated
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);


        return true;

    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        return super.onOptionsItemSelected(item);

    }
}
