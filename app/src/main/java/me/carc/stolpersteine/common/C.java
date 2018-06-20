package me.carc.stolpersteine.common;

import android.os.Build;

import me.carc.stolpersteine.BuildConfig;

/**
 * Holder for various constants
 * Created by bamptonm on 19/09/2017.
 */

public class C {
    public static final boolean DEBUG_ENABLED = BuildConfig.DEBUG;

    public static final String DEBUG = "DEAD:";

    public static final boolean HAS_K = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    public static final boolean HAS_L = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    public static final boolean HAS_M = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    public static final boolean HAS_N = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    public static final boolean HAS_O = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

    // PERMISSION REQUESTS
    public static final int PERMISSION_LOCATION = 200;
    public static final int PERMISSION_CALL_PHONE = 201;
    public static final int PERMISSION_CAMERA     = 202;
    public static final int PERMISSION_STORAGE = 203;

    public static final String FACEBOOK_PERMISSIONS = "email,user_friends,publish_actions,public_profile";

    // NETWORK CHANGE STATES
    public static final String CONNECT_TO_WIFI = "WIFI";
    public static final String CONNECT_TO_MOBILE = "MOBILE";
    public static final String NOT_CONNECT = "NOT_CONNECT";
    final static String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    static final int TYPE_NC     = 0;
    static final int TYPE_WIFI   = 1;
    static final int TYPE_MOBILE = 2;


    public static String USER_LANGUAGE = "en";

    static final String DATA_DIR = "data/BTown";

    public static final String INTENT_MARKET = "market://details?id=";

    public static final String SAVED_FAVORITES_LIST = "SAVED_FAVORITES_LIST";
    public static final String SAVED_FAVORITES_ID_LIST = "SAVED_FAVORITES_ID_LIST";


    public final static String PREF_ROUTING_VEHICLE = "PREF_ROUTING_VEHICLE";


    public static final Integer THUMBNAIL_SIZE = 50;

    public static Integer IMAGE_WIDTH;
    public static Integer IMAGE_HEIGHT;
    public static Integer SCREEN_WIDTH;
    public static Integer SCREEN_HEIGHT;
    public static Float   DENSITY;


    public static final int TIME_ONE_SECOND = 1000;
    private static final int TIME_ONE_MINUTE = 60 * TIME_ONE_SECOND;
    private static final int TIME_ONE_HOUR = TIME_ONE_MINUTE * 60;
    private static final int TIME_ONE_DAY = TIME_ONE_HOUR * 24;
    public static final int TIME_ONE_WEEK = TIME_ONE_DAY * 7;


    public final static String ANSWERS_ERROR = "ANSWERS_ERROR";

}
