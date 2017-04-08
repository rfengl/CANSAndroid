package my.com.cans.cansandroid.controls;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import my.com.cans.cansandroid.R;
import my.com.cans.cansandroid.managers.ValidateManager;

/**
 * Created by Rfeng on 12/08/16.
 */
public class CustomTextView extends TextView {
    public CustomTextView(Context context) {
        super(context);
        Init(null);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(attrs);
    }

    public void Init(AttributeSet attrs) {
        if (attrs == null || ValidateManager.isEmptyOrNull(attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "textSize")))
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, getPixel(R.dimen.text_size));
//        this.setTextColor(Color.WHITE);
    }

    public float getUnit(int unit, int resId) {
        return TypedValue.applyDimension(unit, getResources().getDimension(resId), getResources().getDisplayMetrics());
    }

    public int getPixel(int resId) {
        return (int) getUnit(TypedValue.COMPLEX_UNIT_PX, resId);
    }
}
