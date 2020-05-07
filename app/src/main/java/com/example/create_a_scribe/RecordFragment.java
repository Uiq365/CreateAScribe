package com.example.create_a_scribe;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements View.OnClickListener {

    private NavController navController;
    private ImageButton listBtn;
    private ImageButton recordBtn;
    private TextView filenameText;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 25;//random integer value

    private boolean isRecording = false;


    private MediaRecorder  mediaRecorder;
    private String recordFile;

    private Chronometer timer;


    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        listBtn = view.findViewById(R.id.record_list_button);
        recordBtn = view.findViewById(R.id.record_button);
        timer = view.findViewById(R.id.record_timer);
        filenameText = view.findViewById(R.id.record_filename);

        listBtn.setOnClickListener(this);
        recordBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //check which button is pressed and do the task accordingly
        switch (v.getId()){

            case R.id.record_list_button:
                if(isRecording){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                        }
                    });
                    alertDialog.setNegativeButton("CANCEL", null);
                    alertDialog.setTitle("Audio Still Recording");
                    alertDialog.setMessage("Are you sure you want to stop recording?");
                    alertDialog.create().show();
                }else{
                    navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                }

            case R.id.record_button:
                if (isRecording){
                    //Stop Recording
                    stopRecording();

                    //change button image and set recording state to false
                    recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_started));
                    isRecording = false;
                }
                else{
                    //Start Recording
                    if(checkPermisions()){
                        startRecording();

                        recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_stopped));//if permission granted by the user allows the record button to change
                        isRecording = true;
                    }
                }
                break;
        }
    }

    private void startRecording() {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.US);
        Date now = new Date();


        recordFile = "Recording.. " + formatter.format(now) + "filename.3gp";

        filenameText.setText("Recording, File Name: " + recordFile);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isRecording = true;
        mediaRecorder.start();
    }

    // stops the recording and the timer.
    // Stops the mediaRecorder, releases it and sets it to null.
    // also sets is recording to false
    private void stopRecording() {
        timer.stop();

        filenameText.setText("Recording Stopped, File Saved: " + recordFile);

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        isRecording = false;
    }

    private boolean checkPermisions() {//this method checks to see if the user has given their permission to record audio. If not they are asked to give CreatiPad that permission.
        if(ActivityCompat.checkSelfPermission(getContext(), recordPermission) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }

    //when the activity is no longer being used, recording is stopped only after checking to see if any audio is being recorded
    @Override
    public void onStop() {
        super.onStop();
        if(isRecording){
            stopRecording();
        }
    }
}
