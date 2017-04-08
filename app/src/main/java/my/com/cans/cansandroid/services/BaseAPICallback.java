package my.com.cans.cansandroid.services;

import my.com.cans.cansandroid.activities.BaseActivity;
import my.com.cans.cansandroid.managers.ValidateManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rfeng on 23/06/16.
 */
public class BaseAPICallback<T> implements Callback<T> {
    BaseActivity mActivity;

    public BaseAPICallback(BaseActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        T body = response.body();
        if (body instanceof BaseAPIResponse) {
            BaseAPIResponse baseAPIResponse = ((BaseAPIResponse) body);
            if (!baseAPIResponse.Succeed) {
                if (!ValidateManager.isEmptyOrNull(baseAPIResponse.Message))
                    mActivity.message(baseAPIResponse.Message);
            }
        }
        mActivity.hideProgress();
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        mActivity.hideProgress();
        String message = t.getMessage();
        if (ValidateManager.isEmptyOrNull(message))
            mActivity.message("Network failure.");
        else
            mActivity.message(message);
    }
}
