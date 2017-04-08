package my.com.cans.cansandroid.objects.dbo;

import android.support.annotation.NonNull;

import my.com.cans.cansandroid.objects.interfaces.AutoIncrement;
import my.com.cans.cansandroid.objects.interfaces.PrimaryKey;

/**
 * Created by Rfeng on 05/02/16.
 */
public class DBOBase<S> {
    @PrimaryKey
    @AutoIncrement
    @NonNull
    public int id;
}
