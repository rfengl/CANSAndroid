package my.com.cans.cansandroid.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import my.com.cans.cansandroid.activities.BaseActivity;
import my.com.cans.cansandroid.activities.EditReportActivity;
import my.com.cans.cansandroid.fragments.interfaces.OnTableInteractionListener;
import my.com.cans.cansandroid.managers.Convert;
import my.com.cans.cansandroid.managers.MyLocationManager;
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

public class ReportsFragment extends BaseTableFragment implements OnTableInteractionListener, LocationListener {

    MobileAPIResponse.ReportResult[] mReports;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BaseActivity context = (BaseActivity) this.getActivity();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        new MyLocationManager(context).getLocationManager().requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
    }

    @Override
    public void refresh(final SwipeRefreshLayout swipeRefreshLayout) {
        Location currentLocation = MyLocationManager.getCurrentLocation();
        if (currentLocation != null) {
            BaseActivity activity = (BaseActivity) this.getActivity();
            MobileAPIResponse.CoordinateResult request = new MobileAPIResponse().new CoordinateResult();
            request.Latitude = currentLocation.getLatitude();
            request.Longitude = currentLocation.getLongitude();

            new MyHTTP(activity).call(MobileAPI.class).getReports(request).enqueue(new BaseAPICallback<MobileAPIResponse.ReportsResponse>(activity) {
                @Override
                public void onResponse(Call<MobileAPIResponse.ReportsResponse> call, Response<MobileAPIResponse.ReportsResponse> response) {
                    super.onResponse(call, response);

                    MobileAPIResponse.ReportsResponse resp = response.body();
                    if (resp.Result != null)
                        ReportsFragment.this.mReports = resp.Result;
                    ReportsFragment.super.refresh(swipeRefreshLayout);
                }
            });
        } else
            super.refresh(swipeRefreshLayout);
    }

    @Override
    protected List<BaseTableItem> buildItems() {
        List<BaseTableItem> items = new ArrayList<>();
        if (mReports != null) {
            for (MobileAPIResponse.ReportResult report : mReports) {
                BaseTableItem item = new BaseTableItem();
                item.itemId = report.ID;
                item.title = new Convert(report.TarikhMula).to() + " - " + new Convert(report.TarikhTamat).to();
                item.details = report.Lokasi;
                items.add(item);
            }
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

//    Location mCurrentLocation;

    @Override
    public void onLocationChanged(Location location) {
        MyLocationManager.setCurrentLocation(location);
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
