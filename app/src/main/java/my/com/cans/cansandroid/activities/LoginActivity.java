package my.com.cans.cansandroid.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;

import my.com.cans.cansandroid.R;
import my.com.cans.cansandroid.fragments.BaseEditFragment;
import my.com.cans.cansandroid.fragments.BaseFragment;
import my.com.cans.cansandroid.fragments.interfaces.OnSubmitListener;
import my.com.cans.cansandroid.managers.AsteriskPasswordTransformationMethod;
import my.com.cans.cansandroid.objects.BaseFormField;
import my.com.cans.cansandroid.objects.CANSInfo;
import my.com.cans.cansandroid.objects.dbo.T_User;
import my.com.cans.cansandroid.services.BaseAPICallback;
import my.com.cans.cansandroid.services.BaseAPIResponse;
import my.com.cans.cansandroid.services.MobileAPI;
import my.com.cans.cansandroid.services.MyHTTP;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Rfeng on 04/04/2017.
 */

public class LoginActivity extends EditPageActivity implements OnSubmitListener, BaseEditFragment.OnBuildControlListener {

    @Override
    public Object buildModel() {
        return new LoginModel();
    }

    @Override
    public void submitForm(Object model, View sender) {
        if (model instanceof LoginModel) {
            LoginModel loginModel = (LoginModel) model;
            CANSInfo db = new CANSInfo(this);
            T_User user = db.getUser();
            user.loginID = loginModel.LoginID;
            user.password = loginModel.Password;
            db.update(user);

            new MyHTTP(this).call(MobileAPI.class).verify().enqueue(new BaseAPICallback<BaseAPIResponse>(this) {
                @Override
                public void onResponse(Call<BaseAPIResponse> call, Response<BaseAPIResponse> response) {
                    super.onResponse(call, response);

                    BaseAPIResponse resp = response.body();
                    if (resp != null && resp.Succeed)
                        LoginActivity.this.finish();
                    else
                        LoginActivity.this.message(getString(R.string.access_denied));
                }
            });
        }
    }

    @Override
    public View buildControl(BaseFormField field) {
        View control = field.control;
        if (field.name == "Password") {
            TextInputLayout txtPassword = (TextInputLayout) control.findViewWithTag(field.name);
            txtPassword.getEditText().setTransformationMethod(new AsteriskPasswordTransformationMethod());
        }
        return control;
    }

    class LoginModel {
        @NonNull
        public String LoginID;
        @NonNull
        public String Password;
    }

    @Override
    public void onBackPressed() {
        closeApp();
    }
}
