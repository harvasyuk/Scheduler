package com.scheduler.logic;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;


public class Utils {

    public static int dpToPx(int dp, Context context) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    public static void showToast(Context context, int textResource) {
        Toast toast = Toast.makeText(context, textResource, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, dpToPx(54, context));
        toast.show();
    }
}
