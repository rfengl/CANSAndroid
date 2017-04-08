package my.com.cans.cansandroid.controls;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.AttributeSet;

/**
 * Created by Rfeng on 05/08/16.
 */
public class RoundedImageView extends CustomImageView {
    public RoundedImageView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (width < height || height == 0)
            setMeasuredDimension(width, width);
        else
            setMeasuredDimension(height, height);
    }

    private Integer mInnerRadius;

    public void setInnerRadius(Integer innerRadius) {
        mInnerRadius = innerRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        Bitmap b;
        if (drawable.getClass() == VectorDrawable.class)
            b = getBitmap(drawable);
        else
            b = ((BitmapDrawable) drawable).getBitmap();

        if (b != null) {
            Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

            int w = getWidth(), h = getHeight();
            int innerRadius = mInnerRadius == null ? w : mInnerRadius;
            if (innerRadius < 0)
                innerRadius = w + innerRadius;

            int padding = (w - innerRadius) / 2;

            Bitmap roundBitmap = getRoundedCroppedBitmap(bitmap, innerRadius);
            canvas.drawBitmap(roundBitmap, padding, padding, null);
        }
    }

    public static Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int radius) {
        Bitmap finalBitmap;
        if (bitmap.getWidth() != radius || bitmap.getHeight() != radius)
            finalBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius, false);
        else
            finalBitmap = bitmap;
        Bitmap output = Bitmap.createBitmap(finalBitmap.getWidth(), finalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, finalBitmap.getWidth(), finalBitmap.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        float width = finalBitmap.getWidth();
        float height = finalBitmap.getHeight();
        canvas.drawCircle(width / 2 + 0.7f,
                height / 2 + 0.7f,
                width / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, rect, rect, paint);

        return output;
    }

    private Bitmap getBitmap(Drawable vectorDrawable) {
//        Drawable vectorDrawable;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            vectorDrawable = getResources().getDrawable(drawableId, null);
//        } else {
//            vectorDrawable = getResources().getDrawable(drawableId);
//        }

        if (vectorDrawable != null) {
            int h = vectorDrawable.getIntrinsicHeight();
            int w = vectorDrawable.getIntrinsicWidth();

            //Setting a default height in pixel if intrinsinc height or width is not found , eg in case of a shape drawable
            h = h > 0 ? h : 96;
            w = w > 0 ? w : 96;

            vectorDrawable.setBounds(0, 0, w, h);
            Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            vectorDrawable.draw(canvas);
            return bm;
        }
        return null;
    }

}