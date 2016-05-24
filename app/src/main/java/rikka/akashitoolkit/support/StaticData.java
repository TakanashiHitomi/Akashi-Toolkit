package rikka.akashitoolkit.support;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;

import rikka.akashitoolkit.R;

/**
 * Created by Rikka on 2016/4/16.
 */
public class StaticData {
    private static StaticData sInstance;

    //public int language;
    public int versionCode;
    public String versionName;
    public boolean isTablet;

    private static boolean init = false;

    private StaticData(Context context) {
        /*language = Settings
                .instance(context)
                .getIntFromString(Settings.DATA_LANGUAGE, ZH_CN);*/

        if (!init) {
            try {
                PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                versionCode = pi.versionCode;
                versionName = pi.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                versionCode = -1;
            }

            isTablet = context.getResources().getBoolean(R.bool.is_tablet);

            init = true;
        }
    }

    public static synchronized StaticData instance(Context context) {
        if (sInstance == null) {
            sInstance = new StaticData(context);
        }

        return sInstance;
    }
}
