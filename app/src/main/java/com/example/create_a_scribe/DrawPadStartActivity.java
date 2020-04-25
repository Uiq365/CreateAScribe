package com.example.create_a_scribe;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import view.DrawView;


//This class is the activity that points to the actual DrawPad canvas to allow the user to paint using their touch input
public class DrawPadStartActivity extends AppCompatActivity {
    private DrawView drawView;
    private AlertDialog.Builder currAlertDialog;
    private ImageView widthImageView;
    private AlertDialog dialogLineWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_pad);//sets the view to the drawPad once this activity is created

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);//sets the toolbar that you want to be shown
        setSupportActionBar(toolbar);//sets the toolbar to serve as the actionbar, or the bar on top of each activity with the name of the activity and the primary color

        drawView = findViewById(R.id.view);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//sets the menu that will be seen in the action bar when the drawPad is activated
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);


        return true;

    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {//This gets the value of the item id, the item we are touching
                                                                    // and compares it to the id value of the various buttons and based on the result will call certain helper funcitons.
        switch (item.getItemId()){

            case R.id.clearid: {
                break;
            }
            case R.id.saveid: {
                drawView.clear();
            }
            case R.id.eraseid: {
                break;
            }
            case R.id.colorid: {
                break;
            }
            case R.id.linewidth: {
                showLineWidthDialog();
                break;
            }
        }
        if(item.getItemId() == R.id.clearid){
            drawView.clear();
        }
        return super.onOptionsItemSelected(item);
    }


    // helper function that creates a view, the dialog that allows you to alter the stroke width of the paint line,
    // creates the widgets, the seek bar and the button,
    // and sets the button to confirm the size of the bar and then finally it sets the view to be the new view, creates the view and finally shows it
    void showLineWidthDialog(){
        currAlertDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.width_dialog, null);
        final SeekBar widthSeekBar= view.findViewById(R.id.widthDSeekBar);
        Button setLineWidthButton = view.findViewById(R.id.widthDialogButton);
        widthImageView = view.findViewById(R.id.imageViewid);

        setLineWidthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setLineWidth(widthSeekBar.getProgress());
                dialogLineWidth.dismiss();
                currAlertDialog = null;
            }
        });

        widthSeekBar.setOnSeekBarChangeListener(widthSeekBarChange);

        currAlertDialog.setView(view);
        dialogLineWidth =  currAlertDialog.create();
        dialogLineWidth.setTitle("Set Line Width");
        dialogLineWidth.show();
    }

    private  SeekBar.OnSeekBarChangeListener widthSeekBarChange = new SeekBar.OnSeekBarChangeListener() {
        Bitmap bitmap = Bitmap.createBitmap(400, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Paint p = new Paint();
            p.setColor(drawView.getDrawingColor());
            p.setStrokeCap(Paint.Cap.ROUND);
            p.setStrokeWidth(progress);

            bitmap.eraseColor(Color.WHITE);
            canvas.drawLine(30, 50, 370, 50, p);
            widthImageView.setImageBitmap(bitmap);

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

}
