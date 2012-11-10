/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemProperties;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.settings.Utils;

import java.util.Arrays;
import java.util.List;

public class AndromadusBootReceiver extends BroadcastReceiver {

    private static final String TAG = "AndromadusBootReceiver";

    private static final String S2W_SETTINGS_PROP = "sys.s2w.restored";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (SystemProperties.getBoolean(S2W_SETTINGS_PROP, false) == false
                && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            SystemProperties.set(S2W_SETTINGS_PROP, "true");
            configureS2W(ctx);
        } else {
            SystemProperties.set(S2W_SETTINGS_PROP, "false");
        }
    }


    private void configureS2W(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

        String s2w = prefs.getString(AndromadusSettings.S2W_PREF, null);

        Utils.fileWriteOneLine(AndromadusSettings.S2W_FILE, ((String) s2w)+"\n");
        Log.d(TAG, "S2W settings restored.");
    }
}
