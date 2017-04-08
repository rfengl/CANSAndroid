package my.com.cans.cansandroid.managers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import my.com.cans.cansandroid.R;
import my.com.cans.cansandroid.activities.BaseActivity;
import my.com.cans.cansandroid.controls.CustomImageView;
import my.com.cans.cansandroid.services.BaseAPICallback;
import my.com.cans.cansandroid.services.MobileAPI;
import my.com.cans.cansandroid.services.MobileAPIResponse;
import my.com.cans.cansandroid.services.MyHTTP;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Rfeng on 04/08/16.
 */
public class ImageManager extends AsyncTask<String, String, Bitmap> {
    private Context context;
    private ImageView loadingImage;
    private static ImageView imageView;
    private static String defaultImageName;
    public static final String BASE_PATH = Environment.getExternalStorageDirectory() + "";

    public ImageManager(Context context) {
        this.context = context;
    }

    public ImageManager(ImageView img) {
        this(img.getContext());
        this.loadingImage = img;
    }

    public void pickPhoto(String defaultImageName) {
        pickPhoto(null, defaultImageName);
    }

    public void pickPhoto(ImageView imageView, String defaultImageName) {
        ImageManager.imageView = imageView;
        ImageManager.defaultImageName = defaultImageName;
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_input_add)
                .setTitle(context.getString(R.string.add_photo))
                .setNegativeButton(context.getString(R.string.camera), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        ((Activity) context).startActivityForResult(cameraIntent, (short) R.string.camera);
                    }
                })
                .setPositiveButton(context.getString(R.string.photo), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        ((Activity) context).startActivityForResult(intent, (short) R.string.photo);
                    }
                })
                .setNeutralButton(context.getString(R.string.cancel), null)
                .show();
    }

    public Uri getPhoto(int requestCode, int resultCode, Intent data) {
        Uri mPicUri = null;
        if (requestCode == (short) R.string.camera && resultCode == Activity.RESULT_OK) {
            mPicUri = saveFile(data);
            CropImage.activity(mPicUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setMinCropResultSize(256, 256)
                    .setMaxCropResultSize(2048, 2048)
                    .start((Activity) context);
        } else if (requestCode == (short) R.string.photo && resultCode == Activity.RESULT_OK) {
//            mPicUri = saveFile(data);
            mPicUri = data.getData();
            CropImage.activity(mPicUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setMinCropResultSize(256, 256)
                    .setMaxCropResultSize(2048, 2048)
                    .start((Activity) context);
        }
//        else if (requestCode == (short) R.string.crop_pic && resultCode == Activity.RESULT_OK) {
//            mPicUri = saveFile(data);
//
//            if (ImageManager.imageView != null)
//                ImageManager.imageView.setImageURI(mPicUri);
//        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            mPicUri = result.getUri();
            if (ImageManager.imageView != null) {
                if (ImageManager.imageView instanceof CustomImageView)
                    ((CustomImageView) ImageManager.imageView).pickPhoto(mPicUri);
                else
                    ImageManager.imageView.setImageURI(mPicUri);
            }
        }

        return mPicUri;
    }

    private void performCrop(Uri mPicUri) {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(mPicUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 512);
            cropIntent.putExtra("outputY", 512);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            ((Activity) context).startActivityForResult(cropIntent, (short) R.string.crop_pic);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this.context, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private Uri saveFile(Intent data) {
        String mPic;
        String filePath;

        Uri uri = data.getData();
        Bitmap srcBmp;
        if (uri == null)
            srcBmp = (Bitmap) data.getExtras().get("data");
        else
            try {
                srcBmp = MediaStore.Images.Media.getBitmap(this.context.getContentResolver(), uri);
            } catch (IOException e) {
                if (context instanceof BaseActivity)
                    ((BaseActivity) context).message(e.getMessage());
                return uri;
            }

        if (ValidateManager.isEmptyOrNull(defaultImageName)) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            mPic = "img_" + timeStamp + "";
        } else {
            mPic = defaultImageName;
        }

        filePath = BASE_PATH + "/" + mPic + ".jpg";

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        srcBmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File f = new File(filePath);
        f.deleteOnExit();

        FileOutputStream fo;
        try {
            f.createNewFile();
            fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(new File(filePath));
    }

    public void uploadFile(String key, String folder, String fileName, Uri uri, final BaseAPICallback<MobileAPIResponse.UploadResponse> uploadResponse) {
        if (context instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) context;
            File file = FileUtils.getFile(context, uri);

            baseActivity.showProgress(R.string.uploading_image);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            fileName = fileName.replace("+", "_").replace("=", "_") + ".jpg";
            MultipartBody.Part body = MultipartBody.Part.createFormData("images", fileName, requestFile);
            RequestBody folderBody = RequestBody.create(MediaType.parse("multipart/form-data"), folder);
            RequestBody keyBody = ValidateManager.isEmptyOrNull(key)
                    ? null
                    : RequestBody.create(MediaType.parse("multipart/form-data"), key);

            new MyHTTP(baseActivity).call(MobileAPI.class).uploadImages(keyBody, folderBody, body).enqueue(new BaseAPICallback<MobileAPIResponse.UploadResponse>(baseActivity) {
                @Override
                public void onResponse(Call<MobileAPIResponse.UploadResponse> call, Response<MobileAPIResponse.UploadResponse> response) {
                    super.onResponse(call, response);
                    ImageManager.this.resetDownloadedImage(response.body().Result.downloadPath);
                    uploadResponse.onResponse(call, response);
                }

                @Override
                public void onFailure(Call<MobileAPIResponse.UploadResponse> call, Throwable t) {
                    super.onFailure(call, t);
                    uploadResponse.onFailure(call, t);
                }
            });
        }
    }

    public String resourceToPath(int resID) {
        return ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID);
    }

    public Uri resourceToUri(int resID) {
        return Uri.parse(resourceToPath(resID));
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.loadingImage.setImageResource(R.drawable.ic_loading);
//        context.showProgress(R.string.loading_image);
    }

    private static Map<String, Bitmap> mDownloadedImages = new HashMap<>();

    public void resetDownloadedImage(String path) {
        mDownloadedImages.remove(path);
    }

    protected Bitmap doInBackground(String... args) {
        Bitmap bitmap = null;
        try {
            String path = args[0];
            if (mDownloadedImages.containsKey(path))
                return mDownloadedImages.get(path);

            URL url = new URL(path);
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            Object response = connection.getContent();
            if (response instanceof Bitmap)
                bitmap = (Bitmap) response;
            else
                bitmap = BitmapFactory.decodeStream((InputStream) response);

            mDownloadedImages.put(path, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private Boolean mShowProgress = false;

    public void showProgress(int resId) {
        showProgress(context.getString(resId));
    }

    public void showProgress(String message) {
        mShowProgress = true;
        ((BaseActivity) context).showProgress(message);
    }

    protected void onPostExecute(Bitmap image) {
        if (image != null) {
            loadingImage.setImageBitmap(image);
        } else {
//            context.message(R.string.image_not_found);
        }
        if (mShowProgress)
            ((BaseActivity) context).hideProgress();
    }
}
