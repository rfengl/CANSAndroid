package my.com.cans.cansandroid.objects;

import android.content.Context;

import my.com.cans.cansandroid.managers.DBHelper;
import my.com.cans.cansandroid.objects.dbo.T_User;

/**
 * Created by Rfeng on 04/04/2017.
 */

public class CANSInfo extends DBHelper {
    public CANSInfo(Context context) {
        super(context);
    }

    private T_User mUser;

    public T_User getUser() {
        if (mUser == null)
            mUser = this.selectSingle(T_User.class);
        if (mUser == null) {
            mUser = new T_User();
            this.insert(mUser);
            mUser = this.selectSingle(T_User.class);
        }

        return mUser;
    }

}
