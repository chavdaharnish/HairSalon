package com.example.hairsalon;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneralFunctions {

    public static String hex(String pass) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance( "SHA-256" );
        md.update( pass.getBytes( StandardCharsets.UTF_8 ) );
        byte[] digest = md.digest();
        String hex = String.format( "%064x", new BigInteger( 1, digest ) );
        return hex;

    }

    public GeneralFunctions(){

    }

    public static boolean saveEmail(String email, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(Utils.KEY_EMAIL, email);
        prefsEditor.apply();
        return true;
    }

    public static String getEmail(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Utils.KEY_EMAIL, null);
    }

    public static boolean savePassword(String password, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(Utils.KEY_PASSWORD, password);
        prefsEditor.apply();
        return true;
    }

    public static String getPassword(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Utils.KEY_PASSWORD, null);
    }

    public static int compareToDay(String date2) throws ParseException {
        String date1;

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        // get current date time with Date()
        Date date = new Date();
        date1 = dateFormat.format(date);

        //Date fdate = new SimpleDateFormat("dd/MM/yyyy").parse(date2);

        if (date2 == null) {
            return 0;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date1).compareTo(sdf.format(date2));
    }

}
