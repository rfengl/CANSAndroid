package my.com.cans.cansandroid.controls;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.io.File;

import my.com.cans.cansandroid.managers.ImageManager;

/**
 * Created by Rfeng on 26/08/16.
 */
public class CustomImageView extends ImageView {
    public Uri imageUri;
//    private ImageManager imageManager;

    public Boolean isDownload() {
        if (imageUri == null) {
            return false;
        }
        return imageUri.toString().startsWith("http");
    }

    private boolean mIsPickedPhoto;

    public Boolean isPickedPhoto() {
        return mIsPickedPhoto;
    }

    public CustomImageView(Context ctx) {
        super(ctx);
        initView(ctx);
    }

    public CustomImageView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        initView(ctx);
    }

    protected void initView(Context ctx) {
        if (this.getScaleType() != ScaleType.FIT_XY) {
//        this.setScaleType(ImageView.ScaleType.FIT_XY);
            this.setAdjustViewBounds(true);
        }
    }

    private Boolean isSquare = false;

    public void setSquare(Boolean square) {
        isSquare = square;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (isSquare) {
            if (width < height || height == 0) {
                setMeasuredDimension(width, width);
            } else {
                setMeasuredDimension(height, height);
            }
        }
    }

    public void pickPhoto(Uri uri) {
        mIsPickedPhoto = true;
        setImageURI(uri);
    }

    @Override
    public void setImageURI(Uri uri) {
        imageUri = uri;
        if (uri != null && isDownload()) {
            ImageManager imageManager = new ImageManager(this);
            imageManager.execute(uri.toString());
        } else
            super.setImageURI(uri);
    }

    public void setImagePath(String path) {
        if (path == null)
            this.setImageURI(null);
        else if (path.startsWith("/"))
            this.setImageURI(Uri.fromFile(new File(path)));
        else
            this.setImageURI(Uri.parse(path));
    }
}
