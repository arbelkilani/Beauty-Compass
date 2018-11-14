package com.arbelkilani.beautycompass.global;

import android.location.Location;

import java.util.Calendar;
import java.util.Locale;

public class AppUtils {

    private final static String TAG = AppUtils.class.getSimpleName();

    public static String getTime() {

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);

        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
    }

    public static String convert(double latitude, double longitude) {
        StringBuilder builder = new StringBuilder();

        builder.append("Lat.  ");
        if (latitude < 0) {
            builder.append("S ");
        } else {
            builder.append("N ");
        }

        String latitudeDegrees = Location.convert(Math.abs(latitude), Location.FORMAT_SECONDS);
        String[] latitudeSplit = latitudeDegrees.split(":");
        builder.append(latitudeSplit[0]);
        builder.append("°");
        builder.append(latitudeSplit[1]);
        builder.append("'");
        builder.append(latitudeSplit[2]);
        builder.append("\"");

        builder.append("\n");

        builder.append("Lng. ");
        if (longitude < 0) {
            builder.append("W ");
        } else {
            builder.append("E ");
        }

        String longitudeDegrees = Location.convert(Math.abs(longitude), Location.FORMAT_SECONDS);
        String[] longitudeSplit = longitudeDegrees.split(":");
        builder.append(longitudeSplit[0]);
        builder.append("°");
        builder.append(longitudeSplit[1]);
        builder.append("'");
        builder.append(longitudeSplit[2]);
        builder.append("\"");

        return builder.toString();
    }

}
