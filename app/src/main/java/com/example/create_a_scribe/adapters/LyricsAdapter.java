package com.example.create_a_scribe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.create_a_scribe.R;
import com.example.create_a_scribe.callbacks.LyricEventListener;
import com.example.create_a_scribe.model.Lyric;
import com.example.create_a_scribe.model.Lyric;
import com.example.create_a_scribe.utils.NoteUtils;

import java.util.ArrayList;
import java.util.List;

//This class writes the lyrics to the screen. What it does is makes sure the view shows the different lyrics saved in a custom format
public class LyricsAdapter extends RecyclerView.Adapter<LyricsAdapter.LyricHolder> {
    private Context context;
    private ArrayList<Lyric> lyrics;
    private LyricEventListener listener;
    private boolean multiCheckMode = false;


    public LyricsAdapter(Context context, ArrayList<Lyric> lyrics) {
        this.context = context;
        this.lyrics = lyrics;
    }


    @NonNull
    @Override
    //creates the actual view after inflating the lyric_layout file
    public LyricHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.lyric_layout, parent, false);
        return new LyricHolder(v);
    }


    /**
     * this method checks to see if the lyric exists
     * if it does it checks to see if there is a title associated with it.
     * if that is null the holder sets the text of the lyricText object to the lyricContent
     * if it isnt null, it sets the title accordingly
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(LyricHolder holder, int position) {
        final Lyric lyric = getLyric(position);
        if (lyric != null) {
            if(lyric.getLyricTitle() != null)
            {
                holder.lyricText.setText(lyric.getLyricTitle());
                holder.lyricDate.setText(NoteUtils.dateFromLong(lyric.getLyricDate()));
            }
            else{
                holder.lyricText.setText(lyric.getLyricContent());
                holder.lyricDate.setText(NoteUtils.dateFromLong(lyric.getLyricDate()));
            }
            // init lyric click event
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onLyricClick(lyric);
                }
            });

            // init lyric long click
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onLyricLongClick(lyric);
                    return false;
                }
            });

            // check checkBox if note selected
            if (multiCheckMode) {
                holder.checkBox.setVisibility(View.VISIBLE); // show checkBox if multiMode on
                holder.checkBox.setChecked(lyric.isChecked());
            } else holder.checkBox.setVisibility(View.GONE); // hide checkBox if multiMode off


        }
    }

    @Override
    public int getItemCount() {
        return lyrics.size();
    }

    private Lyric getLyric(int position) {
        return lyrics.get(position);
    }


    /**
     * get All checked lyrics
     *
     * @return Array
     */
    public List<Lyric> getCheckedLyrics() {
        List<Lyric> checkedLyrics = new ArrayList<>();
        for (Lyric s : this.lyrics) {
            if (s.isChecked())
                checkedLyrics.add(s);
        }

        return checkedLyrics;
    }


    class LyricHolder extends RecyclerView.ViewHolder {
        TextView lyricText, lyricDate;
        CheckBox checkBox;

        public LyricHolder(View itemView) {
            super(itemView);
            lyricDate = itemView.findViewById(R.id.lyric_date);
            lyricText = itemView.findViewById(R.id.lyric_text);
            checkBox = itemView.findViewById(R.id.lyric_checkBox);
        }
    }


    public void setListener(LyricEventListener listener) {
        this.listener = listener;
    }

    public void setMultiCheckMode(boolean multiCheckMode) {
        this.multiCheckMode = multiCheckMode;
        if (!multiCheckMode)
            for (Lyric lyric : this.lyrics) {
                lyric.setChecked(false);
            }
        notifyDataSetChanged();
    }
}
