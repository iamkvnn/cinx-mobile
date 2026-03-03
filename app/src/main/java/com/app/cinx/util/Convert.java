package com.app.cinx.util;

import android.content.Context;
import android.util.TypedValue;

import java.text.NumberFormat;
import java.util.Locale;

public class Convert {

    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

    /**
     * Formats a VND amount as "1.200.000 đ".
     * Uses dot as the thousands separator (Vietnamese convention).
     */
    public static String formatVnd(long amount) {
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return nf.format(amount) + " đ";
    }
}
