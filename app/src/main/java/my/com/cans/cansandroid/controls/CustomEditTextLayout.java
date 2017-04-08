package my.com.cans.cansandroid.controls;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;

/**
 * Created by Rfeng on 30/08/16.
 */
public class CustomEditTextLayout extends TextInputLayout {
    public CustomEditTextLayout(Context context) {
        super(context);
        initEditTextLayout(context);
    }

    public CustomEditTextLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initEditTextLayout(context);
    }

    protected void initEditTextLayout(Context context) {
        CustomEditText editText = new CustomEditText(context);
        this.addView(editText);
    }
}
