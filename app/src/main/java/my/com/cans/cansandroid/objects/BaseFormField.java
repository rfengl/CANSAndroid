package my.com.cans.cansandroid.objects;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.com.cans.cansandroid.activities.BaseActivity;
import my.com.cans.cansandroid.controls.CustomEditText;
import my.com.cans.cansandroid.managers.Convert;
import my.com.cans.cansandroid.objects.interfaces.Description;
import my.com.cans.cansandroid.objects.interfaces.Helper;
import my.com.cans.cansandroid.objects.interfaces.Order;
import my.com.cans.cansandroid.objects.interfaces.Readonly;
import my.com.cans.cansandroid.objects.interfaces.ToUppercase;

/**
 * Created by Rfeng on 03/08/16.
 */
public class BaseFormField {
    public BaseFormField(BaseActivity context, Field field, Object item) {
        this.field = field;
        this.context = context;
        this.name = field.getName();

        Description description = field.getAnnotation(Description.class);
        if (description == null)
            this.placeholder = new Convert(this.name).beautifyName();
        else
            this.placeholder = description.value();
        try {
            this.value = field.get(item);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        this.required = field.getAnnotation(NonNull.class) != null;
        this.readonly = field.getAnnotation(Readonly.class) != null;
        this.toUppercase = field.getAnnotation(ToUppercase.class) != null;

        Order order = field.getAnnotation(Order.class);
        if (order == null)
            this.order = 0;
        else
            this.order = order.value();

        Helper helper = field.getAnnotation(Helper.class);
        if (helper != null)
            this.helper = helper.value();

        Class fieldType = field.getType();
        if (fieldType == Boolean.class) {
            this.choices = new ArrayList<>();
            this.choices.add("Yes");
            this.choices.add("No");
        }
        if (fieldType.isEnum()) {
            this.choices = new ArrayList<>();
            Object[] enums = fieldType.getEnumConstants();
            for (Object enumValue : enums) {
                this.choices.add(enumValue.toString());
            }
        }

        if (fieldType == int.class ||
                fieldType == Integer.class ||
                fieldType == double.class ||
                fieldType == Double.class ||
                fieldType == float.class ||
                fieldType == Float.class ||
                fieldType == short.class ||
                fieldType == Short.class)
            this.inputType = InputType.TYPE_CLASS_NUMBER;
        else if (fieldType == Date.class)
            this.inputType = InputType.TYPE_CLASS_DATETIME;
        else if (this.name.toLowerCase().contains("phone") ||
                this.name.toLowerCase().contains("contact"))
            this.inputType = InputType.TYPE_CLASS_PHONE;
        else if (this.name.toLowerCase().contains("email"))
            this.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
    }

    public BaseActivity context;
    public Field field;
    public String name;
    public Object value;
    public Integer order;
    public List<String> choices;
    public List<Integer> icons;
    public String placeholder;
    public String helper;
    public Boolean required;
    public Boolean readonly;
    public View control;
    public int inputType;
    public Boolean toUppercase;

    public Boolean validate() {
        CustomEditText editText = null;
        View control = this.getEditControl();
        if (control.getVisibility() == View.GONE || !control.isEnabled())
            return true;

        if (control instanceof TextInputLayout) {
            control = new Convert(this.control).cast(TextInputLayout.class).getEditText();
        }
        editText = new Convert(control).cast(CustomEditText.class);
        return editText == null || editText.validate();
    }

//    public Object getData() {
//        return this.context.getData(field, this);
//    }

    public View getEditControl() {
        return getEditControl(this.control);
    }

    private View getEditControl(View control) {
        if (control instanceof CustomEditText)
            return new Convert(control).cast(CustomEditText.class);
        else if (control instanceof ViewGroup) {
            ViewGroup layout = (ViewGroup) control;
            final int childcount = layout.getChildCount();
            for (int i = 0; i < childcount; i++) {
                View v = layout.getChildAt(i);
                View editControl = getEditControl(v);
                if (editControl instanceof CustomEditText)
                    return editControl;
            }
        }

        return control;
    }

    public Object getData() {
        View control = this.getEditControl();

        if (control == null)
            return this.value;

        if (control instanceof EditText) {
            if (control.isEnabled()) {
                String result = new Convert(control).cast(EditText.class).getText().toString();
                if (result == null)
                    result = "";
                return new Convert(result).to(field.getType());
            }
        }

        return new Convert("").to(field.getType());
    }
}
