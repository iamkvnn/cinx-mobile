package com.app.cinx.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class PriceUtil {
    public static String formatPrice(long price) {
        if (price == 0) return "Miễn phí";
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        return format.format(price) + "đ";
    }
}
