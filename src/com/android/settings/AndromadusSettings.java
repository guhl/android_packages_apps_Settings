package com.android.settings;

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
    private static final String STATUSBAR_SIXBAR_SIGNAL = "pref_statusbar_sixbar_signal";
    public static final String S2W_FILE = "/sys/android_touch/sweep2wake";

    private String ms2wFormat;
    private ContentResolver mCr;
    private PreferenceScreen mPrefSet;

    private CheckBoxPreference mTrackballWake;
    private CheckBoxPreference mTrackballUnlockScreen;
    private CheckBoxPreference mUseSixbaricons;

    private ListPreference ms2wPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String temp;
        String[] ms2woptions = new String[0];
        ms2wFormat = getString(R.string.sweep2wake_summary);

        addPreferencesFromResource(R.xml.andromadus_settings);

        mPrefSet = getPreferenceScreen();
        mCr = getContentResolver();

        /* Sweep2wake pref */
        ms2wPref = (ListPreference) mPrefSet.findPreference(S2W_FILE);

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

        /* Six bar pref */
        mUseSixbaricons = (CheckBoxPreference) mPrefSet.findPreference(
                STATUSBAR_SIXBAR_SIGNAL);
        mUseSixbaricons.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.STATUSBAR_6BAR_SIGNAL, 1) == 1);
        mUseSixbaricons.setOnPreferenceChangeListener(this);

        /* Remove mTrackballWake on devices without trackballs */ 
        if (!getResources().getBoolean(R.bool.has_trackball)) {
            mPrefSet.removePreference(mTrackballWake);
            mPrefSet.removePreference(mTrackballUnlockScreen);
        }
            // Sweep to wake
        if (!Utils.fileExists(S2W_FILE) || (temp = Utils.fileReadOneLine(S2W_FILE)) == null) {
            ms2wPref.setEnabled(false);
        } else {
            ms2wPref.setEntryValues(R.array.sweep2wake_menu_values);
            ms2wPref.setEntries(R.array.sweep2wake_menu_entries);
            ms2wPref.setValue(temp);
            ms2wPref.setSummary(String.format(ms2wFormat, temp));
            ms2wPref.setOnPreferenceChangeListener(this);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        String fname = "";

        if (newValue != null) {
            if (preference == ms2wPref) {
                fname = S2W_FILE;
        }

        if (Utils.fileWriteOneLine(fname, (String) newValue)) {
                if (preference == ms2wPref) {
                    ms2wPref.setSummary(String.format(ms2wFormat, newValue));
                }
                return true;
            } else {
                return false;
            }
        }
        if (TRACKBALL_WAKE_TOGGLE.equals(key)) {
            Settings.System.putInt(mCr, Settings.System.TRACKBALL_WAKE_SCREEN, (Boolean) newValue ? 1 : 0);
        } else if (TRACKBALL_UNLOCK_TOGGLE.equals(key)) {
            Settings.System.putInt(mCr, Settings.System.TRACKBALL_UNLOCK_SCREEN, (Boolean) newValue ? 1 : 0);
        } else if (STATUSBAR_SIXBAR_SIGNAL.equals(key)) {
            Settings.System.putInt(mCr, Settings.System.STATUSBAR_6BAR_SIGNAL, (Boolean) newValue ? 1 : 0);
        }
        return true;
    }

}
