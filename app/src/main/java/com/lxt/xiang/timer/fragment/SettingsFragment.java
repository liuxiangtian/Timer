package com.lxt.xiang.timer.fragment;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;

import com.lxt.xiang.timer.R;
import com.lxt.xiang.timer.util.PrefsUtil;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private ListPreference themePrefs;
    private SwitchPreference switchPrefs;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        themePrefs = (ListPreference) findPreference("which_theme");
        switchPrefs = (SwitchPreference) findPreference("need_transition");
        themePrefs.setOnPreferenceChangeListener(this);
        switchPrefs.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if("which_theme".equals(key)){
            PrefsUtil.setThemeType((String) newValue);
        } else if("need_transition".equals(key)){
            PrefsUtil.setNeedTransition((Boolean) newValue);
        }
        return true;
    }

}
