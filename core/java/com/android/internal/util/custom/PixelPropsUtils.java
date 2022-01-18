/*
 * Copyright (C) 2020 The Pixel Experience Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.internal.util.custom;

import android.os.Build;
import android.util.Log;

import java.util.Arrays;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class PixelPropsUtils {

    private static final String TAG = PixelPropsUtils.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static final Map<String, Object> propsToChangePUBG;
    private static final Map<String, Object> propsToChangeCOD;

    private static final Map<String, Object> propsToChange;
    private static final Map<String, ArrayList<String>> propsToKeep;
    private static final String[] extraPackagesToChange = {
        "com.android.chrome",
        "com.android.vending",
        "com.breel.wallpapers20"
    };

    private static final String[] packagesToChangeCOD = {
        "com.activision.callofduty.shooter"
    };

    private static final String[] packagesToChangePUBG = {
        "com.tencent.ig",
        "com.pubg.krmobile",
        "com.vng.pubgmobile",
        "com.rekoo.pubgm",
        "com.pubg.imobile",
        "com.pubg.newstate",
        "com.gameloft.android.ANMP.GloftA9HM" // Asphalt 9
    };

    static {
        propsToKeep = new HashMap<>();
        propsToKeep.put("com.google.android.settings.intelligence", new ArrayList<String>(Arrays.asList("FINGERPRINT")));
        propsToChange = new HashMap<>();
        propsToChange.put("BRAND", "google");
        propsToChange.put("MANUFACTURER", "Google");
        propsToChange.put("DEVICE", "raven");
        propsToChange.put("PRODUCT", "raven");
        propsToChange.put("MODEL", "Pixel 6 Pro");
        propsToChange.put("FINGERPRINT", "google/raven/raven:12/SQ1D.220105.007/8030436:user/release-keys");
        propsToChangePUBG = new HashMap<>();
        propsToChangePUBG.put("MODEL", "GM1917");
        propsToChangeCOD = new HashMap<>();
        propsToChangeCOD.put("MODEL", "SO-52A");
    }

    public static void setProps(String packageName) {
        if (packageName == null){
            return;
        }
        if (packageName.startsWith("com.google.") || Arrays.asList(extraPackagesToChange).contains(packageName)){
            if (DEBUG) Log.d(TAG, "Defining props for: " + packageName);
            for (Map.Entry<String, Object> prop : propsToChange.entrySet()) {
                String key = prop.getKey();
                Object value = prop.getValue();
                if (propsToKeep.containsKey(packageName) && propsToKeep.get(packageName).contains(key)){
                    if (DEBUG) Log.d(TAG, "Not defining " + key + " prop for: " + packageName);
                    continue;
                }
                if (DEBUG) Log.d(TAG, "Defining " + key + " prop for: " + packageName);
                setPropValue(key, value);
            }
        }

        if (Arrays.asList(packagesToChangePUBG).contains(packageName)){
            if (DEBUG){
                Log.d(TAG, "Defining props for: " + packageName);
            }
            for (Map.Entry<String, Object> prop : propsToChangePUBG.entrySet()) {
                String key = prop.getKey();
                Object value = prop.getValue();
                setPropValue(key, value);
            }
        }
        if (Arrays.asList(packagesToChangeCOD).contains(packageName)){
            if (DEBUG){
                Log.d(TAG, "Defining props for: " + packageName);
            }
            for (Map.Entry<String, Object> prop : propsToChangeCOD.entrySet()) {
                String key = prop.getKey();
                Object value = prop.getValue();
                setPropValue(key, value);
            }
        }

       // Set proper indexing fingerprint
        if (packageName.equals("com.google.android.settings.intelligence")){
            setPropValue("FINGERPRINT", Build.DATE);
        }
    }

    private static void setPropValue(String key, Object value){
        try {
            if (DEBUG) Log.d(TAG, "Defining prop " + key + " to " + value.toString());
            Field field = Build.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to set prop " + key, e);
        }
    }
}
