package my.com.cans.cansandroid.managers;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import my.com.cans.cansandroid.R;
import my.com.cans.cansandroid.objects.MyApp;

/**
 * Created by Rfeng on 02/08/16.
 */
public class Convert {
    private Object item;

    public Convert(Object item) {
        this.item = item;
    }

    public String to() {
        return to(String.class);
    }

    public <T> T to(Class<T> targetType) {
        if (this.item == null)
            return null;

        Class tClass = this.item.getClass();
        if (tClass == targetType)
            return (T) this.item;

        String value;
        if (tClass == Boolean.class) {
            if (targetType == String.class) {
                if ((Boolean) this.item == true)
                    return (T) "Yes";
                else
                    return (T) "No";
            }
            if ((Boolean) this.item == true)
                value = "1";
            else
                value = "0";
        } else if (tClass == Date.class) {
            if (targetType == String.class || targetType == Calendar.class) {
                Date date = (Date) this.item;
                value = new SimpleDateFormat(MyApp.getContext().getString(R.string.date_time_format)).format(date);
            } else
                value = new Convert(((Date) this.item).getTime()).to(String.class);

            if (value.endsWith(" 00:00"))
                value = value.replace(" 00:00", "");
        } else if (tClass == Calendar.class) {
            Calendar date = (Calendar) this.item;
            if (targetType == String.class)
                value = new SimpleDateFormat(MyApp.getContext().getString(R.string.date_time_format)).format(date.getTime());
            else
                value = new Convert(date.getTime()).to(String.class);
        } else
            value = this.item.toString();

        if (ValidateManager.isEmptyOrNull(value) &&
                (targetType == Integer.class ||
                        targetType == int.class ||
                        targetType == Double.class ||
                        targetType == double.class ||
                        targetType == Float.class ||
                        targetType == float.class ||
                        targetType == Date.class ||
                        targetType == Calendar.class)) {
            return null;
        }

        if (targetType == Integer.class || targetType == int.class) {
            Object result;
            if (value.contains("."))
                result = Math.round(Float.parseFloat(value));
            else
                result = Integer.parseInt(value);
            return (T) result;
        } else if (targetType == Double.class || targetType == double.class) {
            Object result = Double.parseDouble(value);
            return (T) result;
        } else if (targetType == Float.class || targetType == float.class) {
            Object result = Float.parseFloat(value);
            return (T) result;
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            Object result;
            value = value.toUpperCase();
            switch (value) {
                case "1":
                case "TRUE":
                case "YES":
                    result = true;
                    break;
                default:
                    result = false;
            }
            return (T) result;
        } else if (targetType == Date.class) {
            try {
                if (value.contains("T"))
                    return (T) new SimpleDateFormat(MyApp.getContext().getString(R.string.ms_date_time_format)).parse(value);
                else if (value.contains(":"))
                    return (T) new SimpleDateFormat(MyApp.getContext().getString(R.string.date_time_format)).parse(value);
                else
                    return (T) new SimpleDateFormat(MyApp.getContext().getString(R.string.date_format)).parse(value);
            } catch (ParseException ex) {
                return null;
            }
        } else if (targetType.isEnum()) {
            T result = null;
            try {
                result = (T) Enum.valueOf((Class<Enum>) targetType, value);
            } catch (IllegalArgumentException e) {
            }
            if (result == null) {
                for (T item : targetType.getEnumConstants()) {
                    if (item.toString().equals(value)) {
                        result = item;
                        break;
                    }
                }
            }
            return result;
        } else if (targetType == Calendar.class) {
            Calendar calendar = new GregorianCalendar();
            Date date;
            try {
                if (value.contains(":"))
                    date = new SimpleDateFormat(MyApp.getContext().getString(R.string.date_time_format)).parse(value);
                else
                    date = new SimpleDateFormat(MyApp.getContext().getString(R.string.date_format)).parse(value);
                calendar.setTime(date);
                return (T) calendar;
            } catch (ParseException ex) {
                return null;
            }
        } else
            return (T) value;
    }

    public <T> T cast(Class<T> targetType) {
        if (this.item == null)
            return null;
        if (targetType.isInstance(this.item))
            return (T) this.item;
        return null;
    }

    public String beautifyName() {
        if (ValidateManager.isEmptyOrNull(this.item))
            return "";

        String[] list = this.item.toString().split("(?=\\p{Lu})");
        String text = TextUtils.join(" ", list);

        if (text.length() == 1)
            return text.substring(0, 1).toUpperCase();
        else
            return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
