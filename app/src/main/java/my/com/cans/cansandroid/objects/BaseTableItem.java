package my.com.cans.cansandroid.objects;

import android.net.Uri;

import java.util.Dictionary;

/**
 * Created by Rfeng on 12/08/16.
 */
public class BaseTableItem {
    public Object itemId;
    public String title;
    public String details;
    public Uri contentImage;
    public Uri leftImage;
    public Dictionary<Uri, Integer> rightImages;

    public boolean hideTitle;
    public boolean hideDetails;

    public BaseTableItem() {
        init(this);
    }

    protected void init(BaseTableItem item) {
    }
}
