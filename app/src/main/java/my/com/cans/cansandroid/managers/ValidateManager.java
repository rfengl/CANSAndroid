package my.com.cans.cansandroid.managers;

import android.text.TextUtils;

/**
 * Created by Rfeng on 03/08/16.
 */
public class ValidateManager {
    public static Boolean isEmptyOrNull(Object item) {
        return item == null || TextUtils.equals(item.toString(), "null") || TextUtils.isEmpty(item.toString());
    }

    public static Boolean hasValue(Object item) {
        return !isEmptyOrNull(item);
    }

    public static Boolean isUrl(Object item) {
        if (item == null) return false;

        return item.toString().startsWith("http://") || item.toString().startsWith("https://");
    }
}
