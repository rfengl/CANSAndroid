package my.com.cans.cansandroid.objects;

import android.app.Application;
import android.content.Context;

/**
 * Created by Rfeng on 04/04/2017.
 */

public class MyApp extends Application {
    private static MyApp instance;

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    //public static CarFixAPIResponse.GetProfileResult profile;
}
