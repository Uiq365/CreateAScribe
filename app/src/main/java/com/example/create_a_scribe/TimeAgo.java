package com.example.create_a_scribe;

import java.util.Date;
import java.util.concurrent.TimeUnit;

// This class is used to format the time that has passed.
// This class is used by the AudioListFragment which has the view of the audioList.
public class TimeAgo {

    public String getTimeAgo(long durr){
        Date now = new Date();

        long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - durr);
        long minutes= TimeUnit.MILLISECONDS.toMinutes(now.getTime() - durr);
        long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - durr);
        long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - durr);

        if(seconds < 60){
            return "just now";
        }else if( minutes == 1) {
            return "a minute ago";
        }else if(minutes > 1 && minutes < 60){
            return minutes + "minutes ago";
        }else if(hours == 1){
            return "an hour ago";
        }else if(hours > 1 && hours < 24){
            return hours + "hours ago";
        }else if(days == 1){
            return "a day ago";
        }else{
            return days + "days ago";
        }
    }
}
