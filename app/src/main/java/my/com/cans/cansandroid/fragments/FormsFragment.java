package my.com.cans.cansandroid.fragments;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.com.cans.cansandroid.activities.BaseActivity;
import my.com.cans.cansandroid.activities.EditFormActivity;
import my.com.cans.cansandroid.fragments.interfaces.OnTableInteractionListener;
import my.com.cans.cansandroid.managers.Convert;
import my.com.cans.cansandroid.objects.BaseTableItem;
import my.com.cans.cansandroid.services.BaseAPICallback;
import my.com.cans.cansandroid.services.MobileAPI;
import my.com.cans.cansandroid.services.MobileAPIResponse;
import my.com.cans.cansandroid.services.MyHTTP;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Rfeng on 11/04/2017.
 */

public class FormsFragment extends BaseTableFragment implements OnTableInteractionListener {
    MobileAPIResponse.FormResult[] mForms;

    @Override
    public void refresh(final SwipeRefreshLayout swipeRefreshLayout) {
        BaseActivity activity = (BaseActivity) this.getActivity();
        new MyHTTP(activity).call(MobileAPI.class).getForms().enqueue(new BaseAPICallback<MobileAPIResponse.FormsResponse>(activity) {
            @Override
            public void onResponse(Call<MobileAPIResponse.FormsResponse> call, Response<MobileAPIResponse.FormsResponse> response) {
                super.onResponse(call, response);

                MobileAPIResponse.FormsResponse resp = response.body();
                if (resp.Result != null)
                    FormsFragment.this.mForms = resp.Result;
                FormsFragment.super.refresh(swipeRefreshLayout);
            }
        });
    }

    @Override
    protected List<BaseTableItem> buildItems() {
        List<BaseTableItem> items = new ArrayList<>();
        for (MobileAPIResponse.FormResult form : mForms) {
            BaseTableItem item = new BaseTableItem();
            item.itemId = form.ID;
            item.title = new Convert(form.Tarikh).to(); //new Convert(form.getCreatedDate()).to();
            item.details = form.NamaRumahPam;
            items.add(item);
        }

        return items;
    }

    @Override
    public void onTableItemSelected(BaseTableItem item) {
        BaseActivity activity = (BaseActivity) this.getActivity();
        Intent intent = new Intent(activity, EditFormActivity.class);
        intent.putExtra("key", (String) item.itemId);
        startActivity(intent);
    }
}
