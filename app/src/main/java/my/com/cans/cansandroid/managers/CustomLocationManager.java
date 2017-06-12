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

import my.com.cans.cansandroid.R;
import my.com.cans.cansandroid.activities.BaseActivity;

/**
 * Created by Rfeng on 05/06/2017.
 */

public class CustomLocationManager implements LocationListener {
    static Location mCurrentLocation;

    public static Location getCurrentLocation() {
        if (mCurrentLocation != null)
            return mCurrentLocation;
        Location location = new Location("Custom Location");
        location.setLatitude(3.012111);
        location.setLongitude(101.512343);
        return location;
    }

    BaseActivity mContext;
    LocationListener mLocationListener;
    LocationManager mLocationManager;

    public CustomLocationManager(BaseActivity context) {
        mContext = context;
        if (mContext instanceof LocationListener)
            mLocationListener = (LocationListener) mContext;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
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

    public void checkGPS() {
        if (!gpsEnabled() || !networkEnabled()) {
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
        if (mLocationListener != null)
            mLocationListener.onLocationChanged(location);
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