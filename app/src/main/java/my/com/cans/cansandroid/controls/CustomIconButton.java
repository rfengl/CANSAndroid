package my.com.cans.cansandroid.controls;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageButton;

import my.com.cans.cansandroid.R;

/**
 * Created by Rfeng on 16/08/16.
 */
public class CustomIconButton extends ImageButton {
    public Uri imageUri;

    public CustomIconButton(Context context) {
        this(context, null);
    }

    public CustomIconButton(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        this.setBackgroundResource(R.drawable.button_oval);
        this.setClickable(true);
    }

//    @Override
//    public void setImageURI(Uri uri) {
//        super.setImageURI(uri);
//        imageUri = uri;
//    }
}
