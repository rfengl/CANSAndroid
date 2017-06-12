package my.com.cans.cansandroid.fragments;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import my.com.cans.cansandroid.activities.BaseActivity;
import my.com.cans.cansandroid.activities.EditFormActivity;
import my.com.cans.cansandroid.fragments.interfaces.OnTableInteractionListener;
import my.com.cans.cansandroid.managers.Convert;
import my.com.cans.cansandroid.managers.CustomLocationManager;
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

public class FormsFragment extends BaseTableFragment implements OnTableInteractionListener, LocationListener {
    MobileAPIResponse.FormResult[] mForms;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BaseActivity context = (BaseActivity) this.getActivity();
        mLocationManager = new CustomLocationManager(context);
    }

    CustomLocationManager mLocationManager;

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationManager.destroy();
    }

    @Override
    public void refresh(final SwipeRefreshLayout swipeRefreshLayout) {
        Location currentLocation = CustomLocationManager.getCurrentLocation();
        if (currentLocation != null) {
            BaseActivity activity = (BaseActivity) this.getActivity();
            MobileAPIResponse.CoordinateResult request = new MobileAPIResponse().new CoordinateResult();
            request.Latitude = currentLocation.getLatitude();
            request.Longitude = currentLocation.getLongitude();

            if (activity != null) {
                new MyHTTP(activity).call(MobileAPI.class).getForms(request).enqueue(new BaseAPICallback<MobileAPIResponse.FormsResponse>(activity) {
                    @Override
                    public void onResponse(Call<MobileAPIResponse.FormsResponse> call, Response<MobileAPIResponse.FormsResponse> response) {
                        super.onResponse(call, response);

                        MobileAPIResponse.FormsResponse resp = response.body();
                        if (resp != null && resp.Succeed)
                            FormsFragment.this.mForms = resp.Result;
                        FormsFragment.super.refresh(swipeRefreshLayout);
                    }
                });
            }
        } else
            super.refresh(swipeRefreshLayout);
    }

    @Override
    protected List<BaseTableItem> buildItems() {
        List<BaseTableItem> items = new ArrayList<>();
        if (mForms != null) {
            for (MobileAPIResponse.FormResult form : mForms) {
                BaseTableItem item = new BaseTableItem();
                item.itemId = form.ID;
                item.title = new Convert(form.Tarikh).to(); //new Convert(form.getCreatedDate()).to();
                item.details = form.NamaRumahPam;
                items.add(item);
            }
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

    @Override
    public void onLocationChanged(Location location) {
        refresh(null);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
