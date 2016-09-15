package de.theonlymarv.computermonitor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.theonlymarv.computermonitor.Activity.LoginActivity;
import de.theonlymarv.computermonitor.Remote.WebServer.Usage;

/**
 * Created by Marvin on 24.08.2016.
 */
public class Utility {

    public static final String PREFS_LOGIN_USERNAME_KEY = "__USERNAME__";
    public static final String PREFS_LOGIN_PASSWORD_KEY = "__PASSWORD__";
    public static final String PREFS_TOKEN_KEY = "__TOKEN__";
    private static final String PREFS_SAVED_CREDENTIALS_KEY = "__SAVED_CREDENTIALS__";

    public static boolean isCredentialsSaved(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(PREFS_SAVED_CREDENTIALS_KEY, false);
    }

    public static void setCredentialsSaved(Context context, boolean save) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREFS_SAVED_CREDENTIALS_KEY, save);
        editor.commit();
    }

    public static void saveToPrefs(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getFromPrefs(Context context, String key, String defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getString(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static void resetSessionSettings(Context context) {
        saveToPrefs(context, PREFS_TOKEN_KEY, "");
    }

    public static void logout(Activity context) {
        Utility.resetSessionSettings(context);
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        context.finish();
    }

    @NonNull
    public static List<Usage> getUsageWithId(int id, @NonNull List<Usage> usageList) {
        List<Usage> filteredList = new ArrayList<>();
        for (Usage usage : usageList) {
            if (usage.getDevice_id() == id) {
                filteredList.add(usage);
            }
        }
        return filteredList;
    }
}
