package my.com.cans.cansandroid.controls;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

import my.com.cans.cansandroid.R;

/**
 * Created by Rfeng on 05/08/16.
 */
public class CustomButton extends Button {
    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl();
    }

    public CustomButton(Context context) {
        super(context);
        initControl();
    }

    public void initControl() {
        this.setBackgroundResource(R.drawable.button_custom);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            this.setTextColor(getResources().getColor(R.color.colorWhite, null));
        else this.setTextColor(getResources().getColor(R.color.colorWhite));
    }
}
