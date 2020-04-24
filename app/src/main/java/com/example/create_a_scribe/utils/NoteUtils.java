package com.example.create_a_scribe.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteUtils {

    public static String dateFromLong(long time){
        DateFormat format = new SimpleDateFormat("EEE, dd MM yyyy 'at' hh:mm aaa", Locale.US);
                return format.format(new Date(time));
    }
}
