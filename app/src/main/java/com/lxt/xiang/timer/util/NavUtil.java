package com.lxt.xiang.timer.util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.view.View;

import com.lxt.xiang.timer.activity.SearchActivity;
import com.lxt.xiang.timer.activity.SettingsActivity;
import com.lxt.xiang.timer.fragment.AboutFragment;

public class NavUtil {

    public static void navToSettingsActivity(final Context context){
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public static void navToSearchActivity(final Context context){
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    public static void navToFeedBack(final Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("mailto:"));
        context.startActivity(intent);
    }

    public static void navToAbout(final FragmentManager supportFragmentManager, final String about) {
        AboutFragment.newInstance().show(supportFragmentManager, about);
    }


    public static void navToDetailsActivity(final Activity activity, final Pair<View, String> pair, final Intent intent) {
        if (activity==null) return;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP && PrefsUtil.getNeedTransition() && pair!=null){
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pair);
            activity.startActivity(intent, options.toBundle());
        } else {
            activity.startActivity(intent);
        }
    }
}
