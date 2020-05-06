package com.example.create_a_scribe;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class AudioListFragment extends Fragment implements AudioListAdapter.onItemListClick{
    private ConstraintLayout playerSheet;
    private BottomSheetBehavior bottomSheetBehavior;

    private RecyclerView audioList;
    private File[] allRecordings;
    private AudioListAdapter audioListAdapter;

    private MediaPlayer mediaPlayer= null;
    private boolean isPlaying;

    private File fileToPlay = null;

    //UI Elements
    private ImageButton playBtn;
    private ImageButton rewindBtn;
    private ImageButton fwdBtn;
    private TextView playerHeader;
    private TextView playerFilename;

    private SeekBar playerSeekBar;
    private Handler seekBarHandler;
    private Runnable updateSeekBar;

    public AudioListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // initializes the UI elements, buttons, text views, list, and the seekBar
        playerSheet = view.findViewById(R.id.player_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(playerSheet);
        audioList = view.findViewById(R.id.audio_list_view);
        playBtn = view.findViewById(R.id.player_play_btn);
        rewindBtn = view.findViewById(R.id.player_rewind_btn);
        fwdBtn= view.findViewById(R.id.player_fast_fwd_btn);
        playerHeader = view.findViewById(R.id.player_header_title);
        playerFilename = view.findViewById(R.id.player_filename);
        playerSeekBar = view.findViewById(R.id.player_seekBar);

        // sets up the file path and saves it as a string then feeds it into a file called directory
        // all the recordings were then saved to the list of files in the directory.
        // this helps for the list view when setting the recordings to be shown.
        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allRecordings = directory.listFiles();

        // adapter that sets the recordings list
        audioListAdapter = new AudioListAdapter(allRecordings, this);

        // setting up the audio list view and populating it with the audioListAdapter
        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(new LinearLayoutManager(getContext()));
        audioList.setAdapter(audioListAdapter);

        //makes sure that bottom bar never falls off screen completely
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_HIDDEN){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)//this line is used due to the api format. Allows for the use of getDrawable.
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    pauseAudio();
                }else{
                    if(fileToPlay != null){
                        resumeAudio();
                    }
                }
            }
        });

//        rewindBtn.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)//this line is used due to the api format. Allows for the use of getDrawable.
//            @Override
//            public void onClick(View v) {
//                if(isPlaying){
//                    pauseAudio();
//                    rewindAudio();
//                    resumeAudio();
//                }else{
//                    if(fileToPlay != null){
//                        resumeAudio();
//                    }
//                }
//            }
//        });

//        fwdBtn.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)//this line is used due to the api format. Allows for the use of getDrawable.
//            @Override
//            public void onClick(View v) {
//                if(isPlaying){
//                    pauseAudio();
//                }
//            }
//        });

        //this method is used to track the change of the seekBar. The purpose is to allow the user to scroll the seekBar to choose the progression of the audio
        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //if there is a file to play, the audio will be paused once the seekBar has been touched and moved
                if(fileToPlay != null){
                    pauseAudio();
                }

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Checks to see if there is a file to play and if there is when the seekBar is no longer being moved it sets the progress and resumes the audio
                if(fileToPlay != null){
                    int progress = seekBar.getProgress();
                    mediaPlayer.seekTo(progress);
                    resumeAudio();
                }

            }
        });
    }

    //this method checks to see if a recording from the list was clicked and if so the file to play is set
    // and checks whether there is audio playing, if so it stops it and plays the new audio else it just plays the original audio file
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClickListener(File file, int position) {
        fileToPlay = file;
        if(isPlaying){
            stopAudio();
            playAudio(fileToPlay);
        }else{
            playAudio(fileToPlay);

        }
    }


    //this method is used to get the progress of the media and decrease it by 5 seconds
//    private void rewindAudio(){
//        int progress = playerSeekBar.getProgress();
//        mediaPlayer.seekTo(progress - 5);
//    }

    //this method is used to get the progress of the media and increase it by 5 seconds
    private void fwdAudio(){

    }

    //this method sets the audio to be paused changes the button to a play button and sets is playing to false
    // then removes all callbacks from the seekBarHandler and passes in the runnable updateSeekBar.
    // The purpose of this is to allow a smooth change when moving the seekBar
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void pauseAudio(){
        mediaPlayer.pause();
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.sharp_play_arrow_black_36, null));
        isPlaying = false;
        seekBarHandler.removeCallbacks(updateSeekBar);
    }

    //this method sets the audio to be resumed
    // sets the button to be a pause button
    // and sets the isPlaying boolean to true.
    // calls updateRunnable file to update the seekBar and the progress
    // sets the post delay from the seekBar handler and sets the delay to 0 milliseconds.
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void resumeAudio(){
        mediaPlayer.start();
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pause, null));
        isPlaying = true;

        updateRunnable();
        seekBarHandler.postDelayed(updateSeekBar, 0);
    }


    //this method stops the audio and sets the mediaPlayer bottom sheet to collapse when the music is stopped
    // changes the button to play
    // sets is playing to false
    // then removes all callbacks from the seekBarHandler and passes in the runnable updateSeekBar.
    // The purpose of this is to allow a smooth change when moving the seekBar
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void stopAudio() {
        //stop audio
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.sharp_play_arrow_black_36, null));
        playerHeader.setText("Stopped");
        isPlaying = false;
        mediaPlayer.stop();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        seekBarHandler.removeCallbacks(updateSeekBar);
    }

    //this method plays the audio from a file parameter passed in.
    // Sets the header for the media player with all the information gotten from the file
    // sets a bottomSheetBehavior to expanded and sets the play symbol into a pause button
    // sets a on completion listener for the media player to stop the audio and set the header to finished on the completion of the song
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void playAudio(File fileToPlay) {
        mediaPlayer = new MediaPlayer();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pause, null));
        playerFilename.setText(fileToPlay.getName());
        playerHeader.setText("Playing");
        //play the audio
        isPlaying = true;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
                playerHeader.setText("Finished");
            }
        });

        playerSeekBar.setMax(mediaPlayer.getDuration());

        seekBarHandler = new Handler();
        updateRunnable();
        seekBarHandler.postDelayed(updateSeekBar, 0);
    }

    //updates the Runnable by re-running it.
    // sets the seekBar progress to whatever the current progress for the audio is
    // And sets the post delay to use the current runnable and sets the delay to 500 milliseconds
    private void updateRunnable() {
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                playerSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                seekBarHandler.postDelayed(this, 500);
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onStop() {
        super.onStop();
        if(isPlaying){
            stopAudio();
        }
    }
}
