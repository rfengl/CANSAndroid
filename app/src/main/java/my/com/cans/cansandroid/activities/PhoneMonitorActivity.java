package my.com.cans.cansandroid.activities;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.Toast;

import my.com.cans.cansandroid.R;
import my.com.cans.cansandroid.managers.CustomLocationManager;
import my.com.cans.cansandroid.managers.ValidateManager;
import my.com.cans.cansandroid.services.BaseAPICallback;
import my.com.cans.cansandroid.services.MobileAPI;
import my.com.cans.cansandroid.services.MobileAPIResponse;
import my.com.cans.cansandroid.services.MyHTTP;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Rfeng on 12/06/2017.
 */

public class PhoneMonitorActivity extends WebActivity implements LocationListener {
    @Override
    protected String title() {
        return this.getString(R.string.monitoring);
    }

    private String deviceID;

    @Override
    protected String url() {
        if (ValidateManager.isEmptyOrNull(deviceID))
            return "";
        else
            return "http://cansiotapp.azurewebsites.net/PhoneMonitor?DeviceID=" + deviceID;
    }

    CustomLocationManager mLocationManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationManager = new CustomLocationManager(this);

        updateDevices();
        reload();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationManager.destroy();
        mLocationManager = null;
    }

    private void reload() {
        if (mLocationManager == null)
            return;

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadContent();
                reload();
            }
        }, 2 * 60 * 1000);
    }

    private void loadContent() {
        String url = url();
        if (!ValidateManager.isEmptyOrNull(url)) {
//            Toast.makeText(PhoneMonitorActivity.this, getString(R.string.refresh), Toast.LENGTH_SHORT).show();
            this.showProgress(true);

            webView.loadUrl(url);
            setContentView(webView);
        }
    }

    private void updateDevices() {
        Location location = CustomLocationManager.getCurrentLocation();
        if (location != null) {
            MobileAPIResponse.CoordinateRequest request = new MobileAPIResponse().new CoordinateRequest();
            request.Latitude = location.getLatitude();
            request.Longitude = location.getLongitude();
            new MyHTTP(this).call(MobileAPI.class).getDevices(request).enqueue(new BaseAPICallback<MobileAPIResponse.GetDevicesResponse>(this) {
                @Override
                public void onResponse(Call<MobileAPIResponse.GetDevicesResponse> call, Response<MobileAPIResponse.GetDevicesResponse> response) {
                    super.onResponse(call, response);

                    MobileAPIResponse.GetDevicesResponse resp = response.body();
                    if (resp != null && resp.Succeed) {
                        if (resp.Result.length > 0) {
                            MobileAPIResponse.GetDevicesResult result = resp.Result[0];
                            if (!result.DeviceID.equals(deviceID)) {
                                deviceID = result.DeviceID;
                                loadContent();
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        updateDevices();
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
