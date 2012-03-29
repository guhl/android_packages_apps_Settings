package com.android.settings;

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsFragment;

public class AndromadusSettings extends SettingsFragment {

    private static final String TRACKBALL_WAKE_TOGGLE = "pref_trackball_wake_toggle";
    private static final String BUTTON_CATEGORY = "pref_category_button_settings";
    private static final String STATUSBAR_SIXBAR_SIGNAL = "pref_statusbar_sixbar_signal";
    private static final String VOLUME_LOCK_SCREEN = "pref_volume_lock_screen";

    private ContentResolver mCr;
    private PreferenceScreen mPrefSet;

    private CheckBoxPreference mTrackballWake;
    private CheckBoxPreference mUseSixbaricons;
    private CheckBoxPreference mUseVolumeLock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.andromadus_settings);

        mPrefSet = getPreferenceScreen();
        mCr = getContentResolver();

        PreferenceCategory buttonCategory = (PreferenceCategory) mPrefSet
                .findPreference(BUTTON_CATEGORY);

        /* Trackball wake pref */
        mTrackballWake = (CheckBoxPreference) mPrefSet.findPreference(
                TRACKBALL_WAKE_TOGGLE);
        mTrackballWake.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.TRACKBALL_WAKE_SCREEN, 1) == 1);

        /* Volume Lock pref */
        mUseVolumeLock = (CheckBoxPreference) mPrefSet.findPreference(
                VOLUME_LOCK_SCREEN);
        mUseVolumeLock.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.VOLUME_LOCK_SCREEN, 1) == 1);

        /* Six bar pref */
        mUseSixbaricons = (CheckBoxPreference) mPrefSet.findPreference(
                STATUSBAR_SIXBAR_SIGNAL);
        mUseSixbaricons.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.STATUSBAR_6BAR_SIGNAL, 1) == 1);

        /* Remove mTrackballWake on devices without trackballs */ 
        if (!getResources().getBoolean(R.bool.has_trackball)) {
            buttonCategory.removePreference(mTrackballWake);
        }
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mTrackballWake) {
            value = mTrackballWake.isChecked();
            Settings.System.putInt(mCr, Settings.System.TRACKBALL_WAKE_SCREEN,
                    value ? 1 : 0);
            return true;
        } else if (preference == mUseSixbaricons) {
            value = mUseSixbaricons.isChecked();
            Settings.System.putInt(mCr, Settings.System.STATUSBAR_6BAR_SIGNAL,
                    value ? 1 : 0);
            return true;
        } else if (preference == mUseVolumeLock) {
            value = mUseVolumeLock.isChecked();
            Settings.System.putInt(mCr, Settings.System.VOLUME_LOCK_SCREEN,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }

}
