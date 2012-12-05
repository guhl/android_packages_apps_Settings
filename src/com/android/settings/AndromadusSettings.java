package com.android.settings;

import android.content.res.Configuration;
import android.content.ContentResolver;
import android.os.Bundle;
import android.util.Log;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;


import com.android.settings.R;
import com.android.settings.SettingsFragment;
import com.android.settings.Utils;

public class AndromadusSettings extends SettingsFragment
    implements Preference.OnPreferenceChangeListener {

    private final static String TAG = AndromadusSettings.class.getSimpleName();

    private static final String TRACKBALL_WAKE_TOGGLE = "pref_trackball_wake_toggle";
    private static final String TRACKBALL_UNLOCK_TOGGLE = "pref_trackball_unlock_toggle";
    private static final String QUICK_LAUNCH = "pref_quick_launch";
    public static final String S2W_PREF = "sweep2wake_setting";
    public static final String S2W_FILE = "/sys/android_touch/sweep2wake";

    private String ms2wFormat;
    private String[] strs2wDesc;
    private ContentResolver mCr;
    private PreferenceScreen mPrefSet;

    private CheckBoxPreference mTrackballWake;
    private CheckBoxPreference mTrackballUnlockScreen;

    private Preference mQuickLaunch;
    private ListPreference ms2wPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String temp = "";
        ms2wFormat = getString(R.string.sweep2wake_summary);
        strs2wDesc = getResources().getStringArray(R.array.sweep2wake_menu_entries);
        addPreferencesFromResource(R.xml.andromadus_settings);

        mPrefSet = getPreferenceScreen();
        mCr = getContentResolver();

        /* Trackball wake pref */
        mTrackballWake = (CheckBoxPreference) mPrefSet.findPreference(
                TRACKBALL_WAKE_TOGGLE);
        mTrackballWake.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.TRACKBALL_WAKE_SCREEN, 1) == 1);
        mTrackballWake.setOnPreferenceChangeListener(this);

        /* Trackball unlock pref */
        mTrackballUnlockScreen = (CheckBoxPreference) mPrefSet.findPreference(
                TRACKBALL_UNLOCK_TOGGLE);
        mTrackballUnlockScreen.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.TRACKBALL_UNLOCK_SCREEN, 1) == 1);
        mTrackballUnlockScreen.setOnPreferenceChangeListener(this);

        /* Remove mTrackballWake on devices without trackballs */ 
        if (!getResources().getBoolean(R.bool.has_trackball)) {
            mPrefSet.removePreference(mTrackballWake);
            mPrefSet.removePreference(mTrackballUnlockScreen);
        }

        /* Sweep2wake pref */
        ms2wPref = (ListPreference) mPrefSet.findPreference(S2W_PREF);

        if (!Utils.fileExists(S2W_FILE) || (temp = Utils.fileReadOneLine(S2W_FILE)) == null) {
            mPrefSet.removePreference(ms2wPref);
        } else {
            int s2wEnabledValue = 1;
            try{
                    s2wEnabledValue = Integer.parseInt(temp);
            }catch(NumberFormatException nef){
            nef.printStackTrace();
        }

            ms2wPref.setEntryValues(R.array.sweep2wake_menu_values);
            ms2wPref.setEntries(R.array.sweep2wake_menu_entries);
            ms2wPref.setValue(temp);
            ms2wPref.setSummary(String.format(ms2wFormat, strs2wDesc[s2wEnabledValue]));
            ms2wPref.setOnPreferenceChangeListener(this);
        }

        /* QuickLaunch pref */

        mQuickLaunch = (Preference) mPrefSet.findPreference(QUICK_LAUNCH);
        if (getResources().getConfiguration().keyboard != Configuration.KEYBOARD_QWERTY) {
            mPrefSet.removePreference(mQuickLaunch);
        }

    }

    @Override
    public void onResume() {
        String temp;

        super.onResume();

        if (Utils.fileExists(S2W_FILE) && (temp = Utils.fileReadOneLine(S2W_FILE)) != null) {

                int s2wEnabledValue = 1;
                try{
                        s2wEnabledValue = Integer.parseInt(temp);
                }catch(NumberFormatException nef){
                        nef.printStackTrace();
                }

            ms2wPref.setSummary(String.format(ms2wFormat, strs2wDesc[s2wEnabledValue]));
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        String fname = "";
        String temp;

        if (Utils.fileExists(S2W_FILE) || (temp = Utils.fileReadOneLine(S2W_FILE)) != null) {
           fname = S2W_FILE;
             if (preference == ms2wPref) {
                  Utils.fileWriteOneLine(fname, (((String) newValue))+"\n");
                  int s2wEnabledValue = 1;
                        try{
                                s2wEnabledValue = Integer.parseInt((String)newValue);
                        }catch(NumberFormatException nef){
                                nef.printStackTrace();
                        }
                    ms2wPref.setSummary(String.format(ms2wFormat, strs2wDesc[s2wEnabledValue]));
             }
        }
        if (TRACKBALL_WAKE_TOGGLE.equals(key)) {
            Settings.System.putInt(mCr, Settings.System.TRACKBALL_WAKE_SCREEN, (Boolean) newValue ? 1 : 0);
        } else if (TRACKBALL_UNLOCK_TOGGLE.equals(key)) {
            Settings.System.putInt(mCr, Settings.System.TRACKBALL_UNLOCK_SCREEN, (Boolean) newValue ? 1 : 0);
        }
        return true;
    }

}
