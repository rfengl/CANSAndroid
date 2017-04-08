package my.com.cans.cansandroid.controls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.List;

import my.com.cans.cansandroid.R;

/**
 * Created by Rfeng on 03/09/16.
 */
public class CustomPicker extends CustomEditText {
    String mTitle;
    List<String> mOptions;
    List<Integer> mIcons;

    public CustomPicker(Context context) {
        super(context);
        init(context);
    }

    public CustomPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    protected void init(Context context) {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDropDown();
            }
        });
        this.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    showDropDown();
            }
        });
        this.setFocusable(false);
    }

    public void setup(List<String> options) {
        setup(this.getContext().getString(R.string.select), options);
    }

    public void setup(String title, List<String> options) {
        setup(title, options, null);
    }

    public void setup(String title, List<String> options, List<Integer> icons) {
        mTitle = title;
        mOptions = options;
        mIcons = icons;
    }

    public void showDropDown() {
        if (mOptions == null || mOptions.size() == 0)
            return;

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this.getContext());
        builderSingle.setTitle(mTitle);

        final ArrayAdapter<String> arrayAdapter;

        if (mIcons != null && mIcons.size() > 0) {
            arrayAdapter = new ArrayAdapterWithIcon(this.getContext(), mOptions, mIcons);
        } else {
            arrayAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.select_dialog_item, mOptions);
        }

        builderSingle.setNegativeButton(R.string.clear, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String oldValue = CustomPicker.this.getText().toString();
                String newValue = "";
                CustomPicker.this.setText(newValue);
                onSelected(oldValue, newValue);
            }
        });
        builderSingle.setPositiveButton(R.string.close, null);

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String oldValue = CustomPicker.this.getText().toString();
                String newValue = arrayAdapter.getItem(which);
                CustomPicker.this.setText(newValue);
                onSelected(oldValue, newValue);
            }
        });

        builderSingle.show();
    }

    protected void onSelected(String oldValue, String newValue) {
        if (this.getContext() instanceof OnCustomPickerListerner)
            ((OnCustomPickerListerner) this.getContext()).onSelected(this, oldValue, newValue);
    }

    public interface OnCustomPickerListerner {
        void onSelected(CustomPicker picker, String oldValue, String newValue);
    }
}
