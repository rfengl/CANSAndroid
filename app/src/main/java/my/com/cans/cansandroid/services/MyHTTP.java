package my.com.cans.cansandroid.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;

import my.com.cans.cansandroid.R;
import my.com.cans.cansandroid.activities.BaseActivity;
import my.com.cans.cansandroid.managers.Convert;
import my.com.cans.cansandroid.managers.MyGsonConverterFactory;
import my.com.cans.cansandroid.objects.CANSInfo;
import my.com.cans.cansandroid.objects.dbo.T_User;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

/**
 * Created by Rfeng on 23/06/16.
 */
public class MyHTTP {
    protected BaseActivity mActivity;
    private String mLoginID;
    private String mPassword;

    public MyHTTP(BaseActivity activity) {
        mActivity = activity;

        CANSInfo db = new CANSInfo(mActivity);
        T_User profile = db.getUser();
        mLoginID = profile.loginID;
        mPassword = profile.password;
    }

    protected <T> String getAPIBaseURL(Class<T> tClass) {
        return getWebBaseURL() + String.format("/%s/", tClass.getSimpleName());
    }

    public String getWebBaseURL() {
        return mActivity.getString(R.string.web_base_url_my);
    }

    public <T> T call(Class<T> tClass) {
        return call(tClass, false);
    }

    public <T> T call(Class<T> tClass, Boolean showProgress) {
        if (showProgress)
            mActivity.showProgress(true);

        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String uid = mLoginID + ";" + mPassword;
                Request request = chain.request().newBuilder().addHeader("UID", uid).build();
                return chain.proceed(request);
            }
        }).build();
        JsonSerializer<Date> ser = new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext
                    context) {
//                return src == null ? null : new JsonPrimitive(src.getTime());
                return src == null ? null : new JsonPrimitive(new Convert(src).to());
            }
        };
        JsonDeserializer<Date> deSer = new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT,
                                    JsonDeserializationContext context) throws JsonParseException {
                if (json == null)
                    return null;
                String string = json.getAsString();
                if (string.startsWith("/Date("))
                    return new Date(Long.parseLong(string.replace("/Date(", "").replace(")/", "")));
                else
                    return new Convert(string).to(Date.class);
            }
        };
        Gson gson = new GsonBuilder()
                .setDateFormat(mActivity.getString(R.string.ms_date_time_format))
                .registerTypeAdapter(Date.class, ser)
                .registerTypeAdapter(Date.class, deSer).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getAPIBaseURL(tClass))
                .addConverterFactory(MyGsonConverterFactory.create(gson))
                .client(httpClient)
                .build();

        return retrofit.create(tClass);
    }
}
