package my.com.cans.cansandroid.managers;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewParent;
import android.widget.EditText;

import my.com.cans.cansandroid.R;

/**
 * Created by Rfeng on 05/08/16.
 */
public class MandatoryTextWatcher implements TextWatcher {

    private EditText view;
    private TextInputLayout layout;

    public MandatoryTextWatcher(EditText view, TextInputLayout layout) {
        this.view = view;
        this.layout = layout;
    }

    public static MandatoryTextWatcher setup(EditText editText) {
        TextInputLayout textInputLayout = null;
        ViewParent view = editText.getParent();
        if (view instanceof TextInputLayout)
            textInputLayout = (TextInputLayout) view;
        MandatoryTextWatcher watcher = new MandatoryTextWatcher(editText, textInputLayout);
        editText.addTextChangedListener(watcher);
        return watcher;
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void afterTextChanged(Editable editable) {
        validate();
    }

    public boolean validate() {
        String value = view.getText().toString().trim();

        Boolean isValid = !ValidateManager.isEmptyOrNull(value);
        if (layout != null) {
            if (isValid) {
                layout.setError("");
                layout.setErrorEnabled(false);
            } else {
                layout.setErrorEnabled(true);
                layout.setErrorTextAppearance(R.style.error);
                layout.setError(view.getContext().getString(R.string.the_field_is_required));
            }
        }

        return isValid;
    }
}