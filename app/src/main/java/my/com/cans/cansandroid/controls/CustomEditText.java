package my.com.cans.cansandroid.controls;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

import my.com.cans.cansandroid.managers.MandatoryTextWatcher;

/**
 * Created by Rfeng on 02/08/16.
 */
public class CustomEditText extends TextInputEditText {
    public CustomEditText(Context context) {
        super(context);
        initEditText(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initEditText(context);
    }

    protected void initEditText(Context context) {
        this.setSingleLine(true);
        this.setImeOptions(EditorInfo.IME_ACTION_NEXT);
    }

    private MandatoryTextWatcher textWatcher;

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        super.addTextChangedListener(watcher);

        if (watcher instanceof MandatoryTextWatcher)
            textWatcher = (MandatoryTextWatcher) watcher;
    }

    public Boolean validate() {
        if (textWatcher != null)
            return textWatcher.validate();
        return true;
    }
}
