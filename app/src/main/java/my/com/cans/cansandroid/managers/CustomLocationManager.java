package my.com.cans.cansandroid.managers;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import java.util.Arrays;
import java.util.List;

import my.com.cans.cansandroid.R;
import my.com.cans.cansandroid.activities.BaseActivity;
import my.com.cans.cansandroid.services.BaseAPICallback;
import my.com.cans.cansandroid.services.MobileAPI;
import my.com.cans.cansandroid.services.MobileAPIResponse;
import my.com.cans.cansandroid.services.MyHTTP;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Rfeng on 05/06/2017.
 */

public class CustomLocationManager implements LocationListener {
    static Location mCurrentLocation;
    static Integer[] mDevices;

    public static Location getCurrentLocation() {
        if (mCurrentLocation == null) {
            mCurrentLocation = new Location("Custom Location");
            mCurrentLocation.setLatitude(3.012111);
            mCurrentLocation.setLongitude(101.512343);
        }
        return mCurrentLocation;
    }

    public static Integer[] getDevices() {
        return mDevices;
    }

    BaseActivity mContext;
    LocationListener mLocationListener;
    LocationManager mLocationManager;

    public CustomLocationManager(BaseActivity context) {
        this(context, null);
    }

    public CustomLocationManager(BaseActivity context, LocationListener locationListener) {
        mContext = context;
        if (locationListener == null) {
            if (mContext instanceof LocationListener)
                mLocationListener = (LocationListener) mContext;
        } else
            mLocationListener = locationListener;

        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
            return;
        }

        this.checkGPS();
        mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);

        if (mDevices == null && mLocationListener != null)
            onDevicesChanged();
    }

    public void destroy() {
        if (mLocationListener != null)
            mLocationManager.removeUpdates(mLocationListener);
        mContext = null;
        mLocationListener = null;
        mLocationManager = null;
    }

//    public LocationManager getLocationManager() {
//        return mLocationManager;
//    }

    private void checkGPS() {
        if (!gpsEnabled() && !networkEnabled()) {
            mContext.confirm(R.string.gps_disabled, R.string.enable_gps_setting, new BaseActivity.OnConfirmListener() {
                @Override
                public void onConfirm(DialogInterface dialog, int which) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(myIntent);
                }
            });
        }
    }

    protected boolean gpsEnabled() {
        try {
            return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        return false;
    }

    protected boolean networkEnabled() {
        try {
            return mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
//        if (mLocationListener != null) {
//
//        }
        onDevicesChanged();
    }

    private void onDevicesChanged() {
        Location location = getCurrentLocation();
        MobileAPIResponse.CoordinateRequest request = new MobileAPIResponse().new CoordinateRequest();
        request.Latitude = location.getLatitude();
        request.Longitude = location.getLongitude();

        new MyHTTP(mContext).call(MobileAPI.class).getDeviceIDs(request).enqueue(new BaseAPICallback<MobileAPIResponse.GetDeviceIDsResponse>(mContext) {
            @Override
            public void onResponse(Call<MobileAPIResponse.GetDeviceIDsResponse> call, Response<MobileAPIResponse.GetDeviceIDsResponse> response) {
                super.onResponse(call, response);

                MobileAPIResponse.GetDeviceIDsResponse resp = response.body();
                if (resp != null) {
                    Integer[] devices = response.body().Result;

                    if (devices != null) {
                        if (mDevices == null || mDevices.length != devices.length) {
                            mDevices = devices;
                            if (mLocationListener != null)
                                mLocationListener.onLocationChanged(mCurrentLocation);
                        } else {
                            List<Integer> list = Arrays.asList(mDevices);
                            for (int device : devices) {
                                if (list.contains(device)) {
                                    mDevices = devices;
                                    if (mLocationListener != null)
                                        mLocationListener.onLocationChanged(mCurrentLocation);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (mLocationListener != null)
            mLocationListener.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (mLocationListener != null)
            mLocationListener.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (mLocationListener != null)
            mLocationListener.onProviderDisabled(provider);
    }
}