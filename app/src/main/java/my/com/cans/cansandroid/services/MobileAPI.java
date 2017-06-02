package my.com.cans.cansandroid.services;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Rfeng on 04/04/2017.
 */

public interface MobileAPI {
    @POST("Verify")
    Call<BaseAPIResponse> verify();

    @GET("TestInput")
    Call<MobileAPIResponse.FormResponse> testInput();

    @GET("TestReport")
    Call<MobileAPIResponse.ReportResponse> testReport();

    @POST("UpdateForm")
    Call<MobileAPIResponse.FormResponse> updateForm(@Body MobileAPIResponse.FormResult model);

    @POST("GetForm")
    Call<MobileAPIResponse.FormResponse> getForm(@Body MobileAPIResponse.IdResult model);

    @POST("GetForms")
    Call<MobileAPIResponse.FormsResponse> getForms(@Body MobileAPIResponse.CoordinateResult model);

    @POST("UpdateReport")
    Call<MobileAPIResponse.ReportResponse> updateReport(@Body MobileAPIResponse.ReportResult model);

    @POST("GetReport")
    Call<MobileAPIResponse.ReportResponse> getReport(@Body MobileAPIResponse.IdResult model);

    @POST("GetReports")
    Call<MobileAPIResponse.ReportsResponse> getReports(@Body MobileAPIResponse.CoordinateResult model);

    @POST("GetDevices")
    Call<MobileAPIResponse.GetDevicesResponse> getDevices(@Body MobileAPIResponse.CoordinateResult model);

    @POST("GetDeviceValue")
    Call<MobileAPIResponse.GetDeviceValueResponse> getDeviceValue(@Body MobileAPIResponse.IdResult model);

    @POST("UploadImages")
    @Multipart
    Call<MobileAPIResponse.UploadResponse> uploadImages(
            @Part("key") RequestBody key,
            @Part("folder") RequestBody folder,
            @Part MultipartBody.Part images);
}