package my.com.cans.cansandroid.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Field;

import my.com.cans.cansandroid.R;
import my.com.cans.cansandroid.managers.Convert;
import my.com.cans.cansandroid.objects.BaseFormField;

/**
 * Created by Rfeng on 04/04/2017.
 */
public class BaseActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    ProgressDialog mProgress;
    SwipeRefreshLayout mSwipeRefresh;

    protected int getContentViewResource() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int contentViewResource = getContentViewResource();
        if (contentViewResource > 0)
            setContentView(contentViewResource);

        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        if (mSwipeRefresh != null && this instanceof SwipeRefreshLayout.OnRefreshListener)
            mSwipeRefresh.setOnRefreshListener(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        refresh(mSwipeRefresh);
    }

    protected String getInstructionUrl() {
        return null;
    }

    @Override
    public void onRefresh() {
        refresh(mSwipeRefresh);
    }

    protected void refresh(SwipeRefreshLayout swipeRefreshLayout) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        this.refresh(null);
    }

    public void requestCallPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE}, (short) R.string.call);
            }
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_OK) {
//            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE || requestCode == (short) R.string.photo || requestCode == (short) R.string.camera || requestCode == (short) R.string.crop_pic) {
//                new ImageManager(this).getPhoto(requestCode, resultCode, data);
//            }
//        }
//    }

    public void confirm(int messageResId, final OnConfirmListener listener) {
        confirm(R.string.confirm, messageResId, listener);
    }

    public void confirm(int titleResId, int messageRedId, final OnConfirmListener listener) {
        confirm(getString(titleResId), getString(messageRedId), listener);
    }

    public void confirm(String message, final OnConfirmListener listener) {
        confirm(getString(R.string.confirm), message, listener);
    }

    public void confirm(String title, String message, final OnConfirmListener listener) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (listener != null) {
                            listener.onConfirm(dialog, whichButton);
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void popMessage(String title, String text) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(text)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();
    }

    public void message(int resId) {
        message(getString(resId));
    }

    public void message(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void showProgress() {
        showProgress(null);
    }

    public void showProgress(boolean ifNotProgressing) {
        showProgress(null, ifNotProgressing);
    }

    public void showProgress(int resId) {
        showProgress(getString(resId));
    }

    public void showProgress(int resId, boolean ifNotProgressing) {
        showProgress(getString(resId), ifNotProgressing);
    }

    public void showProgress(String message) {
        showProgress(message, false);
    }

    public void showProgress(String message, boolean ifNotProgressing) {
        if (ifNotProgressing && mProgress != null) return;

        if (mProgress == null)
            mProgress = new ProgressDialog(this);
        if (message == null)
            message = getString(R.string.processing);
        mProgress.setMessage(message);
        mProgress.setIndeterminate(false);
        mProgress.setCancelable(false);
        mProgress.show();
    }

    public void hideProgress() {
        if (mProgress != null) {
            mProgress.dismiss();
            mProgress = null;
        }
    }

    public interface OnConfirmListener {
        void onConfirm(DialogInterface dialog, int which);
    }

    public View getEditControl(BaseFormField baseField) {
        if (baseField == null)
            return null;
        View control = baseField.control;
        if (control instanceof TextInputLayout)
            return new Convert(control).cast(TextInputLayout.class).getEditText();
        return control;
    }

    public Object getData(Field field, BaseFormField baseField) {
        View control = getEditControl(baseField);
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

    public float getUnit(int unit, int resId) {
        return TypedValue.applyDimension(unit, getResources().getDimension(resId), getResources().getDisplayMetrics());
    }

    public int getPixel(int resId) {
        return (int) getUnit(TypedValue.COMPLEX_UNIT_PX, resId);
    }

    protected void closeApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        } else {
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
        }
    }
}
