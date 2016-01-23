package com.marcouberti.sonicboomwatchface.utils;

import android.content.Context;
import android.content.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private static final String TAG = "SharedPreferencesHelper";

    public static void save(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void save(Context context, String key, boolean value) {
        SharedPreferences sharedPref = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void save(Context context, String key, long value) {
        SharedPreferences sharedPref = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void save(Context context, String key, int value) {
        SharedPreferences sharedPref = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static String get(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }

    public static String get(Context context, String key, String defValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPref.getString(key, defValue);
    }

    public static int get(Context context, String key, int defValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPref.getInt(key, defValue);
    }

    public static long get(Context context, String key, long defValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPref.getLong(key, defValue);
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key, false);
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key, defValue);
    }

    public static int getInt(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPref.getInt(key, Integer.MIN_VALUE);
    }

    public static void clear(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.apply();
    }
}