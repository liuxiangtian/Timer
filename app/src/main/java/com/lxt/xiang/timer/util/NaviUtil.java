package com.lxt.xiang.timer.util;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;

import com.lxt.xiang.timer.activity.SearchActivity;
import com.lxt.xiang.timer.activity.SettingsActivity;
import com.lxt.xiang.timer.fragment.AboutFragment;

public class NaviUtil {

    public static void naviToSettingsActivity(Context context){
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public static void naviToSearchActivity(Context context){
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    public static void naviToFeedBack(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("mailto:"));
        context.startActivity(intent);
    }

    public static void naviToAbout(FragmentManager supportFragmentManager, String about) {
        AboutFragment.newInstance().show(supportFragmentManager, about);
    }
}
