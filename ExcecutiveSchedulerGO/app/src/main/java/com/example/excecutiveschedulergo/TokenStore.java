package com.example.excecutiveschedulergo;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenStore {

    private static final String PREF_NAME = "TOKEN";
    private static final String KEY = "token";

    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE);
        String token = prefs.getString(KEY, null);
        return token;
    }

    public static void setToken(String token, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE).edit();
        editor.putString(KEY, token);
        editor.apply();
    }

    public static void deleteToken(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE).edit();
        editor.remove(KEY);
        editor.apply();
    }

}
