package my.com.cans.cansandroid.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import my.com.cans.cansandroid.R;
import my.com.cans.cansandroid.controls.CustomEditText;
import my.com.cans.cansandroid.controls.CustomImageButton;
import my.com.cans.cansandroid.controls.CustomPicker;
import my.com.cans.cansandroid.controls.CustomTextView;
import my.com.cans.cansandroid.controls.DateTimePicker;
import my.com.cans.cansandroid.controls.ReadonlyEditText;
import my.com.cans.cansandroid.fragments.interfaces.OnSubmitListener;
import my.com.cans.cansandroid.managers.Convert;
import my.com.cans.cansandroid.managers.MandatoryTextWatcher;
import my.com.cans.cansandroid.managers.ValidateManager;
import my.com.cans.cansandroid.objects.BaseFormField;

public class BaseEditFragment extends BaseFragment {
    private OnBuildControlListener mBuildControl;
    private LabelWidthListener mLabelWidth;
    private OnBuildModelListener mBuildModel;
    private OnBuildFieldListener mBuildField;
    private OnSubmitListener mSubmit;
    private ScrollView mScrollView;

    public BaseEditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mScrollView = (ScrollView) view.findViewById(R.id.scrollView);

        return view;
    }

    @Override
    protected int getFragmentResourceId() {
        return R.layout.fragment_base_edit;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBuildModelListener)
            mBuildModel = (OnBuildModelListener) context;
        if (context instanceof OnBuildFieldListener)
            mBuildField = (OnBuildFieldListener) context;
        if (context instanceof OnBuildControlListener)
            mBuildControl = (OnBuildControlListener) context;
        if (context instanceof LabelWidthListener)
            mLabelWidth = (LabelWidthListener) context;
        if (context instanceof OnSubmitListener)
            mSubmit = (OnSubmitListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mBuildModel = null;
        mBuildControl = null;
        mLabelWidth = null;
        mBuildField = null;
        mSubmit = null;
    }

    protected Object mModel;

    protected Object buildModel() {
        return mBuildModel.buildModel();
    }

    @Override
    public void refresh(SwipeRefreshLayout swipeRefreshLayout) {
        super.refresh(swipeRefreshLayout);

        mModel = buildModel();
        Object model = getModel();

        if (model != null) {
            LinearLayout linearLayout = new LinearLayout(mActivity);
            int horizontalMargin = getPixel(R.dimen.activity_horizontal_margin);
            int verticalMargin = getPixel(R.dimen.activity_vertical_margin);
            linearLayout.setPadding(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            if (mScrollView == null && mView instanceof ViewGroup) {
                mScrollView = new ScrollView(mActivity);
                mScrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                ((ViewGroup) mView).addView(mScrollView);
            }

            mScrollView.removeAllViews();
            mScrollView.addView(linearLayout);

            mFormFields = buildFields();
            BaseFormField[] formFields = getFields();
            for (BaseFormField field : formFields) {
                View control = buildControl(field);
                linearLayout.addView(control);
                field.control = control;
            }

            List<View> views = customViews();
            if (views != null) {
                for (View view : views) {
                    if (view.getLayoutParams() == null) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(0, getPixel(R.dimen.activity_vertical_margin), 0, 0);
                        view.setLayoutParams(params);
                    }
                    linearLayout.addView(view);
                }
            }

//            for (BaseFormField field : getFields()) {
//                if (field.required)
//                    field.validate();
//            }

            mScrollView.setFocusableInTouchMode(true);
            mScrollView.requestFocus();
        }
    }

    protected BaseFormField[] buildFields() {
        Object model = getModel();
        List<BaseFormField> builtFields = new ArrayList<>();
        if (model != null) {
            Field[] fields = model.getClass().getFields();
            for (Field field : fields) {
                if (Modifier.isPublic(field.getModifiers()) &&
                        !Modifier.isStatic(field.getModifiers()) &&
                        !Modifier.isFinal(field.getModifiers())) {
                    builtFields.add(buildField(new BaseFormField(mActivity, field, model)));
                }
            }
        }

        BaseFormField[] fieldArray = new BaseFormField[builtFields.size()];
        builtFields.toArray(fieldArray);
        Arrays.sort(fieldArray, new Comparator<BaseFormField>() {
            @Override
            public int compare(BaseFormField field1, BaseFormField field2) {
                return field1.order.compareTo(field2.order);
            }
        });

        return fieldArray;
    }

    protected BaseFormField[] mFormFields;

    public BaseFormField[] getFields() {
        if (mFormFields == null)
            mFormFields = buildFields();
        return mFormFields;
    }

    protected BaseFormField buildField(BaseFormField field) {
        if (mBuildField != null)
            field = mBuildField.buildField(field);
        return field;
    }

    protected View buildControl(BaseFormField field) {
        LinearLayout linearLayout = new LinearLayout(mActivity);
        CustomTextView label = new CustomTextView(mActivity);
//        label.setTextColor(getResources().getColor(R.color.colorGray800));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getLabelWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = getPixel(R.dimen.label_top_margin);
        layoutParams.bottomMargin = getPixel(R.dimen.label_top_margin);
        label.setLayoutParams(layoutParams);
        label.setText(field.placeholder);
        linearLayout.addView(label);

        CustomTextView middleSymbol = new CustomTextView(mActivity);
        layoutParams = new LinearLayout.LayoutParams(getPixel(R.dimen.list_view_divider), ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = getPixel(R.dimen.label_top_margin);
        middleSymbol.setLayoutParams(layoutParams);
        middleSymbol.setText(":");
        linearLayout.addView(middleSymbol);

        TextInputLayout inputLayout = new TextInputLayout(mActivity);
        inputLayout.setTag(field.name);
        inputLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        TextInputEditText editText;

        if (field.readonly)
            editText = new ReadonlyEditText(mActivity);
        else if (field.field.getType() == Date.class)
            editText = new DateTimePicker(mActivity);
        else if (field.choices != null && field.choices.size() > 0) {
            CustomPicker picker = new CustomPicker(mActivity);
            picker.setup(getString(R.string.select), field.choices, field.icons);
            editText = picker;
        } else
            editText = new CustomEditText(mActivity);

        if (field.toUppercase)
            editText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        inputLayout.addView(editText);
        inputLayout.setHintEnabled(false);
//        inputLayout.setHint(field.placeholder);

        linearLayout.addView(inputLayout);

        Object object = field.getData();

        if (field.inputType > 0)
            editText.setInputType(field.inputType);

        if (editText instanceof DateTimePicker)
            ((DateTimePicker) editText).setDate(new Convert(object).to(Calendar.class));
        else {
            String value = new Convert(object).to();
            if (value != null)
                editText.setText(value);
        }

        if (field.required)
            editText.addTextChangedListener(new MandatoryTextWatcher(editText, inputLayout));

        if (ValidateManager.hasValue(field.helper)) {
            CustomImageButton img = new CustomImageButton(mActivity);
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
            img.setLayoutParams(layoutParams);
            img.setImageResource(android.R.drawable.ic_menu_help);
            img.setTag(field.helper);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = new Convert(v.getTag()).to(String.class);
                    mActivity.popMessage(getString(R.string.helper), tag);
                }
            });

            linearLayout.addView(img);
        }

        field.control = linearLayout;

        View control = null;
        if (mBuildControl != null)
            control = mBuildControl.buildControl(field);

        if (control == null)
            return linearLayout;
        else
            return control;
    }

    protected int getLabelWidth() {
        if (mLabelWidth != null)
            return mLabelWidth.getLabelWidth();
        return getPixel(R.dimen.label_width);
    }

    protected BaseFormField getField(String name) {
        for (BaseFormField field : getFields()) {
            if (field.name == name) {
                return field;
            }
        }
        return null;
    }

    protected Object getModel() {
        return mModel;
    }

    protected Object getResult() {
        Object model = getModel();
        Class myClass = model.getClass();

        for (BaseFormField baseField : getFields()) {
            try {
                Field field = myClass.getField(baseField.name);
                field.set(model, baseField.getData());
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return model;
    }

    protected List<View> customViews() {
        return null;
    }

    public Boolean validateForm(View button, Boolean submitNow) {
        button.setEnabled(false);
        Object model = getResult();
        Boolean isValid = isValid(model);

        if (isValid && submitNow)
            submitForm(model, button);
        else
            button.setEnabled(true);

        return isValid;
    }

    protected Boolean isValid(Object model) {
        for (BaseFormField field : getFields()) {
            if (!field.validate())
                return false;
        }
        return true;
    }

    protected void submitForm(Object model, View button) {
        if (mSubmit != null) {
            mSubmit.submitForm(model, button);
        }
    }

    public interface OnBuildModelListener {
        Object buildModel();
    }

    public interface OnBuildFieldListener {
        BaseFormField buildField(BaseFormField field);
    }

    public interface OnBuildControlListener {
        View buildControl(BaseFormField field);
    }

    public interface LabelWidthListener {
        int getLabelWidth();
    }
}
