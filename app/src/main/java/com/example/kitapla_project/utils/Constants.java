package com.example.kitapla_project.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class Constants {
    public static final String KEY_IS_LOGIN = "isLogin";
    public static final String KEY_EMAIL = "email";

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
