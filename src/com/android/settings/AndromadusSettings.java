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
    private static final String STATUSBAR_SIXBAR_SIGNAL = "pref_statusbar_sixbar_signal";
    private static final String KEY_CAMBTN_MUSIC_CTRL = "cambtn_music_controls";
    private static final String KILL_APP_LONGPRESS_BACK_TIMEOUT = "pref_kill_app_longpress_back_timeout";
    private static final String SHOW_BRIGHTNESS_TOGGLESLIDER = "pref_show_brightness_toggleslider";

    private ContentResolver mCr;
    private PreferenceScreen mPrefSet;

    private CheckBoxPreference mTrackballWake;
    private CheckBoxPreference mTrackballUnlockScreen;
    private CheckBoxPreference mUseSixbaricons;
    private CheckBoxPreference mCamBtnMusicCtrl;
    private CheckBoxPreference mShowBrightnessToggleslider;

    private EditTextPreference mKillAppLongpressBackTimeout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        /* Six bar pref */
        mUseSixbaricons = (CheckBoxPreference) mPrefSet.findPreference(
                STATUSBAR_SIXBAR_SIGNAL);
        mUseSixbaricons.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.STATUSBAR_6BAR_SIGNAL, 1) == 1);
        mUseSixbaricons.setOnPreferenceChangeListener(this);

        /* Camera button play/pause pref */
        mCamBtnMusicCtrl = (CheckBoxPreference) mPrefSet.findPreference(
                KEY_CAMBTN_MUSIC_CTRL);
        mCamBtnMusicCtrl.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.CAMBTN_MUSIC_CONTROLS, 0) == 1);
        mCamBtnMusicCtrl.setOnPreferenceChangeListener(this);

        /* Kill App Longpress Back timeout duration pref */
        mKillAppLongpressBackTimeout = (EditTextPreference) mPrefSet.findPreference(KILL_APP_LONGPRESS_BACK_TIMEOUT);
        mKillAppLongpressBackTimeout.setOnPreferenceChangeListener(this);

        /* Notification Area Brightness Toggleslider pref */
        mShowBrightnessToggleslider = (CheckBoxPreference) mPrefSet.findPreference(
                SHOW_BRIGHTNESS_TOGGLESLIDER);
        mShowBrightnessToggleslider.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.SHOW_BRIGHTNESS_TOGGLESLIDER, 0) == 1);
        mShowBrightnessToggleslider.setOnPreferenceChangeListener(this);

        /* Remove mTrackballWake on devices without trackballs */ 
        if (!getResources().getBoolean(R.bool.has_trackball)) {
            mPrefSet.removePreference(mTrackballWake);
            mPrefSet.removePreference(mTrackballUnlockScreen);
        }
        if (Settings.Secure.getInt(mCr, Settings.Secure.KILL_APP_LONGPRESS_BACK, 0) == 0) {
            mPrefSet.removePreference(mKillAppLongpressBackTimeout);
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
        } else if (TRACKBALL_WAKE_TOGGLE.equals(key)) {
            Settings.System.putInt(mCr, Settings.System.TRACKBALL_WAKE_SCREEN, (Boolean) newValue ? 1 : 0);
        } else if (TRACKBALL_UNLOCK_TOGGLE.equals(key)) {
            Settings.System.putInt(mCr, Settings.System.TRACKBALL_UNLOCK_SCREEN, (Boolean) newValue ? 1 : 0);
        } else if (STATUSBAR_SIXBAR_SIGNAL.equals(key)) {
            Settings.System.putInt(mCr, Settings.System.STATUSBAR_6BAR_SIGNAL, (Boolean) newValue ? 1 : 0);
        } else if (KEY_CAMBTN_MUSIC_CTRL.equals(key)) {
            Settings.System.putInt(mCr, Settings.System.CAMBTN_MUSIC_CONTROLS, (Boolean) newValue ? 1 : 0);
        } else if (SHOW_BRIGHTNESS_TOGGLESLIDER.equals(key)) {
            Settings.System.putInt(mCr, Settings.System.SHOW_BRIGHTNESS_TOGGLESLIDER, (Boolean) newValue ? 1 : 0);
        }
        return true;
    }

}
