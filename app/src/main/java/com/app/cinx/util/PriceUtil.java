package com.app.cinx.util;

import java.text.NumberFormat;
import java.util.Locale;

public class PriceUtil {
    public static String formatPrice(long price) {
        if (price == 0) {
            return "Miễn phí";
        }
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(price) + "đ";
    }
}