package my.com.cans.cansandroid.fragments;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import my.com.cans.cansandroid.activities.BaseActivity;
import my.com.cans.cansandroid.activities.EditReportActivity;
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

public class ReportsFragment extends BaseTableFragment implements OnTableInteractionListener {

    MobileAPIResponse.ReportResult[] mReports;

    @Override
    public void refresh(final SwipeRefreshLayout swipeRefreshLayout) {
        BaseActivity activity = (BaseActivity) this.getActivity();
        new MyHTTP(activity).call(MobileAPI.class).getReports().enqueue(new BaseAPICallback<MobileAPIResponse.ReportsResponse>(activity) {
            @Override
            public void onResponse(Call<MobileAPIResponse.ReportsResponse> call, Response<MobileAPIResponse.ReportsResponse> response) {
                super.onResponse(call, response);

                MobileAPIResponse.ReportsResponse resp = response.body();
                if (resp.Result != null)
                    ReportsFragment.this.mReports = resp.Result;
                ReportsFragment.super.refresh(swipeRefreshLayout);
            }
        });
    }

    @Override
    protected List<BaseTableItem> buildItems() {
        List<BaseTableItem> items = new ArrayList<>();
        for (MobileAPIResponse.ReportResult report : mReports) {
            BaseTableItem item = new BaseTableItem();
            item.itemId = report.id;
            item.title = new Convert(report.TarikhMula).to() + " - " + new Convert(report.TarikhTamat).to();
            item.details = report.Lokasi;
            items.add(item);
        }

        return items;
    }

    @Override
    public void onTableItemSelected(BaseTableItem item) {
        BaseActivity activity = (BaseActivity) this.getActivity();
        Intent intent = new Intent(activity, EditReportActivity.class);
        intent.putExtra("key", (String) item.itemId);
        startActivity(intent);
    }
}
