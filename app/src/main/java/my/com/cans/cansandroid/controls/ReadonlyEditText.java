package my.com.cans.cansandroid.controls;

import android.content.Context;
import android.view.inputmethod.EditorInfo;

/**
 * Created by Rfeng on 30/08/16.
 */
public class ReadonlyEditText extends CustomEditText {
    public ReadonlyEditText(Context context) {
        super(context);
        this.setFocusable(false);
    }
}
