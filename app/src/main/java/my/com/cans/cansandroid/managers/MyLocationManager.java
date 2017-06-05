package my.com.cans.cansandroid.managers;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import my.com.cans.cansandroid.R;
import my.com.cans.cansandroid.activities.BaseActivity;

/**
 * Created by Rfeng on 05/06/2017.
 */

public class MyLocationManager {
    static Location mCurrentLocation;
    public static Location getCurrentLocation() {
        return mCurrentLocation;
    }
    public static void setCurrentLocation(Location location) {
        mCurrentLocation = location;
    }

    BaseActivity mContext;
    LocationManager mLocationManager;
    public MyLocationManager(BaseActivity context) {
        mContext = context;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public LocationManager getLocationManager() {
        return mLocationManager;
    }

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

}
