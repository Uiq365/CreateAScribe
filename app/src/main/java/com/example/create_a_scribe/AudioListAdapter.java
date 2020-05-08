package com.example.create_a_scribe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

/**
 * This class above all else makes a clean way of showing the different recording files
 * it helps the audioList Fragment show all the recordings in a nice recycler view.
 * To do this it creates a single list item for each of the different recordings
 * the single list item has a static listImage,
 * a listTitle which is used to display the file name
 * and a listDate to keep track of when the recording was created, this information is used at the time of creating the recording
 */
public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {

    private File[] allRecordings;
    private TimeAgo timeAgo;

    private onItemListClick onItemListClick;

    public AudioListAdapter(File[] allRecordings, onItemListClick onItemListClick){
        this.allRecordings = allRecordings;
        this.onItemListClick = onItemListClick;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        timeAgo = new TimeAgo();
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        holder.list_title.setText(allRecordings[position].getName());
        holder.list_date.setText(timeAgo.getTimeAgo(allRecordings[position].lastModified()));//will correctly get the that has passed by using the TimAgo class

    }

    @Override
    public int getItemCount() {
        return allRecordings.length;
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView list_image;
        private TextView list_title;
        private TextView list_date;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            list_image = itemView.findViewById(R.id.list_image_view);
            list_title = itemView.findViewById(R.id.list_file);
            list_date = itemView.findViewById(R.id.list_date);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onItemListClick.onClickListener(allRecordings[getAdapterPosition()], getAdapterPosition());
        }
    }

    public interface onItemListClick{
        void onClickListener(File file, int position);
    }
}
