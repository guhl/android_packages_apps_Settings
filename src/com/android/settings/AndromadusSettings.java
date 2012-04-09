package com.android.settings;

import android.content.ContentResolver;
import android.os.Bundle;
import android.util.Log;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;


import com.android.settings.R;
import com.android.settings.SettingsFragment;

public class AndromadusSettings extends SettingsFragment
    implements Preference.OnPreferenceChangeListener {

    private final static String TAG = AndromadusSettings.class.getSimpleName();

    private static final String TRACKBALL_WAKE_TOGGLE = "pref_trackball_wake_toggle";
    private static final String TRACKBALL_UNLOCK_TOGGLE = "pref_trackball_unlock_toggle";
    private static final String BUTTON_CATEGORY = "pref_category_button_settings";
    private static final String STATUSBAR_SIXBAR_SIGNAL = "pref_statusbar_sixbar_signal";
    private static final String VOLUME_LOCK_SCREEN = "pref_volume_lock_screen";
    private static final String STATUSBAR_IME_TOGGLE = "pref_show_statusbar_ime_switcher";
    private static final String KILL_APP_LONGPRESS_BACK_TIMEOUT = "pref_kill_app_longpress_back_timeout";

    private ContentResolver mCr;
    private PreferenceScreen mPrefSet;

    private CheckBoxPreference mTrackballWake;
    private CheckBoxPreference mTrackballUnlockScreen;
    private CheckBoxPreference mUseSixbaricons;
    private CheckBoxPreference mUseVolumeLock;
    private CheckBoxPreference mShowImeSwitcher;

    private EditTextPreference mKillAppLongpressBackTimeout;

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

        /* Trackball unlock pref */
        mTrackballUnlockScreen = (CheckBoxPreference) mPrefSet.findPreference(
                TRACKBALL_UNLOCK_TOGGLE);
        mTrackballUnlockScreen.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.TRACKBALL_UNLOCK_SCREEN, 1) == 1);

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

        /* Statusbar IME Switcher pref */
        mShowImeSwitcher = (CheckBoxPreference) mPrefSet.findPreference(
                STATUSBAR_IME_TOGGLE);
        mShowImeSwitcher.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.SHOW_STATUSBAR_IME_SWITCHER, 1) == 1);

        /* Kill App Longpress Back timeout duration pref */
        mKillAppLongpressBackTimeout = (EditTextPreference) mPrefSet.findPreference(KILL_APP_LONGPRESS_BACK_TIMEOUT);
        mKillAppLongpressBackTimeout.setOnPreferenceChangeListener(this);


        /* Remove mTrackballWake on devices without trackballs */ 
        if (!getResources().getBoolean(R.bool.has_trackball)) {
            buttonCategory.removePreference(mTrackballWake);
            buttonCategory.removePreference(mTrackballUnlockScreen);
        }
        if (Settings.Secure.getInt(mCr, Settings.Secure.KILL_APP_LONGPRESS_BACK, 0) == 0) {
            buttonCategory.removePreference(mKillAppLongpressBackTimeout);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // This kinda permanently sets the summary in english & makes the definition in strings.xml useless.. should probably fix
        mKillAppLongpressBackTimeout.setSummary("Hold down back button for " + mKillAppLongpressBackTimeout.getText() + "ms to kill a process");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        
        if (KILL_APP_LONGPRESS_BACK_TIMEOUT.equals(key)) {
            try {
                int timeout = Integer.parseInt((String) newValue);
                if (timeout < 500 || timeout > 2000) {
                    // Out of bounds, bail!
                    return false;
                }
                Settings.System.putInt(mCr, KILL_APP_LONGPRESS_BACK_TIMEOUT, timeout);
                mKillAppLongpressBackTimeout.setSummary("Hold down back button for " + timeout + "ms to kill a process");
                mKillAppLongpressBackTimeout.setText(Integer.toString(timeout));
            } finally {
                Log.d(TAG, "Exception error on preference change.");
                return false;
            }
        }
        return true;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mTrackballWake) {
            value = mTrackballWake.isChecked();
            Settings.System.putInt(mCr, Settings.System.TRACKBALL_WAKE_SCREEN,
                    value ? 1 : 0);
            return true;
        } else if (preference == mTrackballUnlockScreen) {
            value = mTrackballUnlockScreen.isChecked();
            Settings.System.putInt(mCr, Settings.System.TRACKBALL_UNLOCK_SCREEN,
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
        } else if (preference == mShowImeSwitcher) {
            value = mShowImeSwitcher.isChecked();
            Settings.System.putInt(mCr, Settings.System.SHOW_STATUSBAR_IME_SWITCHER,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }

}
