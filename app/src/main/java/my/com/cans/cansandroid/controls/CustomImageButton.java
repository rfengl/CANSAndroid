package my.com.cans.cansandroid.controls;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import my.com.cans.cansandroid.R;
import my.com.cans.cansandroid.managers.ImageManager;

import static android.support.v4.app.ActivityCompat.requestPermissions;

/**
 * Created by Rfeng on 04/08/16.
 */
public class CustomImageButton extends RoundedImageView {
    public CustomImageButton(Context context) {
        this(context, null);
    }

    public CustomImageButton(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        this.setClickable(true);
    }

    public void enablePickPhoto(final String defaultImageName) {
        final Context context = this.getContext();
        final ImageView imageView = this;
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestExternalPermission())
                    new ImageManager(context).pickPhoto(imageView, defaultImageName);
            }
        });
    }

    private boolean requestExternalPermission() {
        if (ContextCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions((Activity) this.getContext(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, (short) R.string.photo);
            }
        }

        return false;
    }
}