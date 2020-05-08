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
    private AlertDialog colorDialog;

    //SeekBar objects used to hold the seekBar data from user input
    private SeekBar alphaSeekBar;
    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;

    //view holding the color gotten from the combined seekBars
    private View colorView;

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
                                                            // and compares it to the id value of the various buttons and based on the result will call certain helper functions.
        switch (item.getItemId()){

            case R.id.clearid: {
                drawView.clear();
                break;
            }
            case R.id.saveid: {
                drawView.saveImage();
            }
            case R.id.eraseid: {
                drawView.setErase();
                break;
            }
            case R.id.colorid: {
                showColorDialog();
                break;
            }
            case R.id.linewidth: {
                showLineWidthDialog();
                break;
            }
            case R.id.resetid:{
                drawView.resetColor();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }



    // this method creates and shows the color dialog pop up that allows you to change the color of the paint line
    void showColorDialog(){
        currAlertDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.color_dialog, null);
        alphaSeekBar = view.findViewById(R.id.alphaSeekBar);
        redSeekBar = view.findViewById(R.id.redSeekBar);
        greenSeekBar = view.findViewById(R.id.greenSeekBar);
        blueSeekBar = view.findViewById(R.id.blueSeekBar);
        colorView = view.findViewById(R.id.colorView);

        //register SeekBar event listeners
        alphaSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        redSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        greenSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        blueSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);

        int color = drawView.getDrawingColor();
        alphaSeekBar.setProgress(Color.alpha(color));
        redSeekBar.setProgress(Color.red(color));
        greenSeekBar.setProgress(Color.green(color));
        blueSeekBar.setProgress(Color.blue(color));

        Button setButtonColor = view.findViewById(R.id.setColorButton);
        setButtonColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setDrawingColor(Color.argb(
                        alphaSeekBar.getProgress(),
                        redSeekBar.getProgress(),
                        greenSeekBar.getProgress(),
                        blueSeekBar.getProgress()
                ));

                colorDialog.dismiss();
            }
        });
        currAlertDialog.setView(view);
        currAlertDialog.setTitle("Color Picker");
        colorDialog = currAlertDialog.create();
        colorDialog.show();

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
        widthSeekBar.setProgress(drawView.getLineWidth());

        currAlertDialog.setView(view);
        dialogLineWidth =  currAlertDialog.create();
        dialogLineWidth.setTitle("Set Line Width");
        dialogLineWidth.show();
    }

    //tracks the chane in seekBar of the RGB values combines them and shows you a preview in the colorView
    // in addition it changes the color of the drawView which then becomes the color of the paintLine
    private SeekBar.OnSeekBarChangeListener colorSeekBarChanged = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //changing the background color to the user chosen color
            drawView.setBackgroundColor(Color.argb(
                    alphaSeekBar.getProgress(),
                    redSeekBar.getProgress(),
                    greenSeekBar.getProgress(),
                    blueSeekBar.getProgress()
            ));

            //display current color
            colorView.setBackgroundColor(Color.argb(
                    alphaSeekBar.getProgress(),
                    redSeekBar.getProgress(),
                    greenSeekBar.getProgress(),
                    blueSeekBar.getProgress()
            ));

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    //changes the width of the line depending on how much the seekBar was changes
    private  SeekBar.OnSeekBarChangeListener widthSeekBarChange = new SeekBar.OnSeekBarChangeListener() {
        Bitmap bitmap = Bitmap.createBitmap(400, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // tracks the progress of the seekBar and set the width to change appropriately also shows a preview in the widthImageView
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
