package my.com.cans.cansandroid.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.com.cans.cansandroid.R;
import my.com.cans.cansandroid.controls.CustomEditText;
import my.com.cans.cansandroid.controls.CustomImageView;
import my.com.cans.cansandroid.controls.CustomPicker;
import my.com.cans.cansandroid.controls.CustomTextView;
import my.com.cans.cansandroid.controls.DateTimePicker;
import my.com.cans.cansandroid.fragments.BaseEditFragment;
import my.com.cans.cansandroid.fragments.interfaces.OnSubmitListener;
import my.com.cans.cansandroid.managers.Convert;
import my.com.cans.cansandroid.managers.CustomLocationManager;
import my.com.cans.cansandroid.managers.ValidateManager;
import my.com.cans.cansandroid.objects.BaseFormField;
import my.com.cans.cansandroid.objects.interfaces.Description;
import my.com.cans.cansandroid.objects.interfaces.Order;
import my.com.cans.cansandroid.services.BaseAPICallback;
import my.com.cans.cansandroid.services.MobileAPI;
import my.com.cans.cansandroid.services.MobileAPIResponse;
import my.com.cans.cansandroid.services.MyHTTP;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Rfeng on 06/04/2017.
 */

public class EditReportActivity extends EditPageActivity implements OnSubmitListener, BaseEditFragment.OnBuildFieldListener, BaseEditFragment.OnBuildControlListener, BaseEditFragment.LabelWidthListener, View.OnClickListener, CustomPicker.OnCustomPickerListerner, LocationListener {
    public String getKey() {
        return getIntent().getStringExtra("key");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        mLocationManager = new CustomLocationManager(this);

        updateDevices();
    }

    CustomLocationManager mLocationManager;

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationManager.destroy();
    }

    @Override
    public void submitForm(Object model, final View sender) {
        if (model instanceof ReportModel) {
            ReportModel item = (ReportModel) model;

            if (item != null) {
                MobileAPIResponse.ReportResult request = new MobileAPIResponse().new ReportResult();
                request.ID = getKey();
                for (MobileAPIResponse.GetDevicesResult device : mDevices) {
                    if (device.DeviceID.equals(item.deviceID)) {
                        request.DeviceID = device.ID;
                        break;
                    }
                }
                if (ValidateManager.isEmptyOrNull(request.DeviceID))
                    request.DeviceID = item.deviceID;
                request.Lokasi = item.lokasi;
                request.Kawasan = item.kawasan;
                request.TarikhMula = item.tarikhMula;
                request.TarikhTamat = item.tarikhTamat;
                request.BreakdownDetails = item.breakdownDetails;
                request.RootCaused = item.rootCaused;
                request.EquipmentName = item.equipmentName;
                request.SystemBreakdownType = item.systemBreakdownType;
                request.SeverityOfAffectedProcess = item.severityOfAffectedProcess;
                request.SitePreventionActionTaken = item.sitePreventionActionTaken;

                request.ActionToBeTaken = item.actionToBeTaken;
                request.KeyInDataSystem = item.keyInDataSystem.selected;
                request.EDOApproval = item.edoApproval.selected;
                request.MWOIssued = item.mwoIssued.selected;
                request.NotificationCPPBD = item.notificationCPPBD.selected;
                request.WorkCompletion = item.workCompletion.selected;
                request.Others = item.others.selected;

                request.Selesai = item.selesai.selected;

                new MyHTTP(this).call(MobileAPI.class).updateReport(request).enqueue(new BaseAPICallback<MobileAPIResponse.ReportResponse>(this) {
                    @Override
                    public void onResponse(Call<MobileAPIResponse.ReportResponse> call, Response<MobileAPIResponse.ReportResponse> response) {
                        super.onResponse(call, response);

                        MobileAPIResponse.ReportResponse resp = response.body();
                        if (resp.Succeed) {
                            String key = resp.Result.ID;
                            if (key != null) {
                                EditReportActivity.this.finish();
                            }
                        }
                    }
                });
            }
        }
    }

//    private MobileAPIResponse.FormData buildFormData(ActionTaken model) {
//        MobileAPIResponse.FormData item = new MobileAPIResponse().new FormData();
//        item.Baik = model.selected;
////        item.Remarks = model.remarks;
//        return item;
//    }

    CustomPicker mDevicePicker;

    @Override
    public View buildControl(BaseFormField field) {
        View control = field.control;
        if (field.name.startsWith("title_")) {
            CustomTextView title = new CustomTextView(this);
            title.setTypeface(title.getTypeface(), Typeface.BOLD);
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, getPixel(R.dimen.big_text_size));
            title.setText(new Convert(field.value).to());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = getPixel(R.dimen.nav_header_vertical_spacing);
            title.setLayoutParams(layoutParams);
            control = title;
        } else if (field.name == "tarikhMula" || field.name == "tarikhTamat") {
            TextInputLayout layout = (TextInputLayout) field.control.findViewWithTag(field.name);
            DateTimePicker editText = (DateTimePicker) layout.getEditText();
            editText.setTimeEnabled(true);
        } else if (field.name.equals("deviceID")) {
            mDevicePicker = (CustomPicker) ((TextInputLayout) control.findViewWithTag(field.name)).getEditText();
        } else if (ActionTaken.class.isAssignableFrom(field.field.getType())) {
            if (field.value == null)
                field.value = new ActionTaken();

//            boolean hasRemarks = field.name != "selesai";

            TextInputLayout layout = (TextInputLayout) field.control.findViewWithTag(field.name);
            EditText editText = layout.getEditText();
            editText.setSingleLine(false);

            editText.setHint(R.string.remarks);
            editText.setVisibility(View.GONE);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setTag("Checkboxes");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = getPixel(R.dimen.label_top_margin);
            layoutParams.leftMargin = getPixel(R.dimen.list_view_divider);
            layout.addView(linearLayout, 0, layoutParams);

            LinearLayout.LayoutParams marginLeftLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            marginLeftLayoutParams.leftMargin = getPixel(R.dimen.list_view_divider);

            ActionTaken actionTaken = (ActionTaken) field.value;

            CustomImageView imgBaik = new CustomImageView(this);
            if (actionTaken.selected != null && actionTaken.selected) {
                imgBaik.setImageResource(R.drawable.ic_check);
//                if (hasRemarks)
//                    editText.setVisibility(View.VISIBLE);
            } else
                imgBaik.setImageResource(R.drawable.ic_check_box_outline);
            linearLayout.addView(imgBaik);
            CustomTextView labelBaik = new CustomTextView(this);
            if (field.name == "selesai")
                labelBaik.setText(getString(R.string.selesai));
            else
                labelBaik.setText(getString(R.string.yes));
            linearLayout.addView(labelBaik, marginLeftLayoutParams);

            linearLayout.addView(new View(this), new LinearLayout.LayoutParams(getPixel(R.dimen.short_label_width), ViewGroup.LayoutParams.WRAP_CONTENT));

            CustomImageView imgRosak = new CustomImageView(this);
            if (actionTaken.selected != null && actionTaken.selected)
                imgRosak.setImageResource(R.drawable.ic_check_box_outline);
            else
                imgRosak.setImageResource(R.drawable.ic_check);
            linearLayout.addView(imgRosak);
            CustomTextView labelRosak = new CustomTextView(this);
            if (field.name == "selesai")
                labelRosak.setText(getString(R.string.tidak));
            else
                labelRosak.setText(getString(R.string.no));
            linearLayout.addView(labelRosak, marginLeftLayoutParams);

            labelBaik.setTag(field);
            labelBaik.setOnClickListener(this);
            imgBaik.setTag(field);
            imgBaik.setOnClickListener(this);
            labelRosak.setTag(field);
            labelRosak.setOnClickListener(this);
            imgRosak.setTag(field);
            imgRosak.setOnClickListener(this);
        } else {
            TextInputLayout layout = (TextInputLayout) field.control.findViewWithTag(field.name);
            EditText editText = layout.getEditText();
            editText.setSingleLine(false);
        }
        return control;
    }

    @Override
    public void onClick(View v) {
        BaseFormField field = (BaseFormField) v.getTag();
        ActionTaken actionTaken = (ActionTaken) field.value;
        LinearLayout linearLayout = (LinearLayout) field.control.findViewWithTag("Checkboxes");
        actionTaken.selected = v == linearLayout.getChildAt(0) || v == linearLayout.getChildAt(1);

        if (actionTaken.selected) {
            ((ImageView) linearLayout.getChildAt(0)).setImageResource(R.drawable.ic_check);
            ((ImageView) linearLayout.getChildAt(3)).setImageResource(R.drawable.ic_check_box_outline);
        } else {
            ((ImageView) linearLayout.getChildAt(0)).setImageResource(R.drawable.ic_check_box_outline);
            ((ImageView) linearLayout.getChildAt(3)).setImageResource(R.drawable.ic_check);
        }

//        boolean hasRemarks = field.name != "selesai";
//        if (hasRemarks) {
//            TextInputLayout layout = (TextInputLayout) field.control.findViewWithTag(field.name);
//            EditText editText = layout.getEditText();
//            if (actionTaken.selected) {
//                editText.setVisibility(View.VISIBLE);
//                editText.requestFocus();
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
//            } else {
//                editText.setVisibility(View.GONE);
//            }
//        }
    }

    private MobileAPIResponse.ReportResult mResult;

    private MobileAPIResponse.GetDevicesResult[] mDevices;

    @Override
    protected void refresh(final SwipeRefreshLayout swipeRefreshLayout) {
        MobileAPIResponse.IdResult request = new MobileAPIResponse().new IdResult();
        request.ID = getKey();

        if (request.ID == null)
            EditReportActivity.super.refresh(swipeRefreshLayout);
        else
            new MyHTTP(this).call(MobileAPI.class).getReport(request).enqueue(new BaseAPICallback<MobileAPIResponse.ReportResponse>(this) {
                @Override
                public void onResponse(Call<MobileAPIResponse.ReportResponse> call, Response<MobileAPIResponse.ReportResponse> response) {
                    super.onResponse(call, response);

                    MobileAPIResponse.ReportResponse resp = response.body();
                    if (resp != null && resp.Succeed) {
                        mResult = resp.Result;
                        EditReportActivity.super.refresh(swipeRefreshLayout);
                        updateDevices();

//                        if (mResult.DeviceID != null) {
//                            MobileAPIResponse.CoordinateResult deviceIdModel = new MobileAPIResponse().new CoordinateResult();
//                            deviceIdModel.ID = mResult.DeviceID;
//                            new MyHTTP(EditReportActivity.this).call(MobileAPI.class).getDevices(deviceIdModel).enqueue(new BaseAPICallback<MobileAPIResponse.GetDevicesResponse>(EditReportActivity.this) {
//                                @Override
//                                public void onResponse(Call<MobileAPIResponse.GetDevicesResponse> call, Response<MobileAPIResponse.GetDevicesResponse> response) {
//                                    super.onResponse(call, response);
//
//                                    MobileAPIResponse.GetDevicesResponse resp = response.body();
//                                    if (resp != null && resp.Succeed) {
//                                        mDevices = resp.Result;
//                                        EditReportActivity.super.refresh(null);
//                                    }
//                                }
//                            });
//                        }
                    }
                }
            });
    }

    private void updateDevices() {
        if (mDevices == null && mDevicePicker != null) {
            Location location = CustomLocationManager.getCurrentLocation();
            MobileAPIResponse.CoordinateResult request = new MobileAPIResponse().new CoordinateResult();
            if (mResult != null && mResult.DeviceID != null)
                request.ID = mResult.DeviceID;
            if (location != null) {
                request.Latitude = location.getLatitude();
                request.Longitude = location.getLongitude();
            }
            new MyHTTP(this).call(MobileAPI.class).getDevices(request).enqueue(new BaseAPICallback<MobileAPIResponse.GetDevicesResponse>(this) {
                @Override
                public void onResponse(Call<MobileAPIResponse.GetDevicesResponse> call, Response<MobileAPIResponse.GetDevicesResponse> response) {
                    super.onResponse(call, response);

                    MobileAPIResponse.GetDevicesResponse resp = response.body();
                    if (resp != null && resp.Succeed) {
                        mDevices = resp.Result;

                        List<String> choices = new ArrayList<>();
                        MobileAPIResponse.GetDevicesResult selectedDevice = null;
                        for (MobileAPIResponse.GetDevicesResult item : mDevices) {
                            choices.add(item.DeviceID);
                            if (selectedDevice == null)
                                selectedDevice = item;
                            if (item.ID.equals(mModel.deviceID))
                                selectedDevice = item;
                        }
                        mDevicePicker.setup(getString(R.string.select), choices);
                        if (selectedDevice != null) {
                            if (ValidateManager.isEmptyOrNull(mModel.deviceID)) {
                                mModel.deviceID = selectedDevice.DeviceID;
                                ((CustomEditText) getEditFragment().getField("kawasan").getEditControl()).setText(selectedDevice.Kawasan);
                                ((CustomEditText) getEditFragment().getField("lokasi").getEditControl()).setText(selectedDevice.Lokasi);
                            }
                            mDevicePicker.setText(selectedDevice.DeviceID);
                        }
                    }
                }
            });
        }
    }

    private ReportModel mModel;

    @Override
    public Object buildModel() {
        if (mModel != null)
            return mModel;

        mModel = new ReportModel();
        if (mResult == null)
            return mModel;

        mModel.deviceID = mResult.DeviceID;
        mModel.lokasi = mResult.Lokasi;
        mModel.kawasan = mResult.Kawasan;
        mModel.tarikhMula = mResult.TarikhMula;
        mModel.tarikhTamat = mResult.TarikhTamat;
        mModel.breakdownDetails = mResult.BreakdownDetails;
        mModel.rootCaused = mResult.RootCaused;
        mModel.equipmentName = mResult.EquipmentName;
        mModel.systemBreakdownType = mResult.SystemBreakdownType;
        mModel.severityOfAffectedProcess = mResult.SeverityOfAffectedProcess;
        mModel.sitePreventionActionTaken = mResult.SitePreventionActionTaken;

        mModel.actionToBeTaken = mResult.ActionToBeTaken;
        mModel.keyInDataSystem = buildActionTaken(mResult.KeyInDataSystem);
        mModel.edoApproval = buildActionTaken(mResult.EDOApproval);
        mModel.mwoIssued = buildActionTaken(mResult.MWOIssued);
        mModel.notificationCPPBD = buildActionTaken(mResult.NotificationCPPBD);
        mModel.workCompletion = buildActionTaken(mResult.WorkCompletion);
        mModel.others = buildActionTaken(mResult.Others);

        mModel.selesai = buildActionTaken(mResult.Selesai);

        return mModel;
    }

    private ActionTaken buildActionTaken(Boolean formData) {
        ActionTaken item = new ActionTaken();
        item.selected = formData;
//        item.remarks = formData.Remarks;
        return item;
    }

    @Override
    public int getLabelWidth() {
        return getPixel(R.dimen.long_label_width);
    }

    @Override
    public BaseFormField buildField(BaseFormField field) {
        if (ActionTaken.class.isAssignableFrom(field.field.getType())) {
            return new EditReportField(this, field.field, mModel);
        } else if (field.name.equals("deviceID")) {
            field.choices = new ArrayList<>();
            field.choices.add(new Convert(field.value).to());
        }
        return field;
    }

    @Override
    public void onLocationChanged(Location location) {
        updateDevices();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onSelected(CustomPicker picker, String oldValue, String newValue) {
        if (!ValidateManager.isEmptyOrNull(newValue) && !newValue.equals(oldValue)) {
            for (MobileAPIResponse.GetDevicesResult item : mDevices) {
                if (item.DeviceID.equals(newValue)) {
                    mModel.kawasan = item.Kawasan;
                    mModel.lokasi = item.Lokasi;
                    super.refresh(null);
                    break;
                }
            }
        }
    }

    class EditReportField extends BaseFormField {
        public EditReportField(BaseActivity context, Field field, Object item) {
            super(context, field, item);
        }

        @Override
        public Object getData() {
            if (ActionTaken.class.isAssignableFrom(this.field.getType())) {
                ActionTaken item = (ActionTaken) this.value;

//                if (this.control == null)
//                    return item;

//                TextInputLayout inputLayout = (TextInputLayout) this.control.findViewWithTag(this.name);
//                if (inputLayout != null) {
//                    EditText editText = inputLayout.getEditText();
//                    if (editText.getVisibility() != View.GONE)
//                        item.remarks = editText.getText().toString();
//                    else
//                        item.remarks = null;
//                }

                return item;
            }
            return super.getData();
        }
    }

    class ReportModel {
        @NonNull
        @Order(1)
        @Description("Device ID")
        public String deviceID;
        @NonNull
        @Order(1)
        public String lokasi;
        @NonNull
        @Order(2)
        public String kawasan;
        @NonNull
        @Order(3)
        public Date tarikhMula;
        @Order(4)
        public Date tarikhTamat;

        @Order(10)
        public String title_breakdownDetails = "INSIDEN / DETAILS OF BREAKDOWN";
        @Order(11)
        @Description("Permasalahan / Background")
        public String breakdownDetails;
        @Order(12)
        @Description("Punca Kerosakan / Root Caused")
        public String rootCaused;
        @Order(13)
        @Description("Jenis Kegagalan Sistem / Type of System Breakdown")
        public String systemBreakdownType;
        @Order(14)
        @Description("Name Kelengkapan / Equipment Name")
        public String equipmentName;
        @Order(15)
        @Description("Keterukan / Severity of Affected Process")
        public String severityOfAffectedProcess;
        @Order(16)
        public String title_sitePreventionActionTaken = "TINDAKAN PEMBAIKAN DI TAPAK / SITE PREVENTION ACTION TAKEN";
        @Order(17)
        @Description("To be Filled by Technician / Chargeman")
        public String sitePreventionActionTaken;

        @Order(20)
        public String title_officeUse = "UNTUK KEGUNAAN PEJABAT / OFFICE USE";
        @Order(21)
        @Description("Tindakan yang perlu diambil / Action to be taken")
        public String actionToBeTaken;
        @Order(22)
        @Description("Simpan dalam data / Key in data system")
        public ActionTaken keyInDataSystem;
        @Order(23)
        @Description("Kelulusan EDO / EDO Approval")
        public ActionTaken edoApproval;
        @Order(24)
        @Description("Majukan memo kepada CPBD / Notification to CPBD")
        public ActionTaken notificationCPPBD;
        @Order(25)
        @Description("Pengisuan MWO / MWO Issued")
        public ActionTaken mwoIssued;
        @Order(26)
        @Description("Status kerja / Work completion")
        public ActionTaken workCompletion;
        @Order(27)
        @Description("Lain-lain / Others")
        public ActionTaken others;

        @Order(30)
        @Description("Selesai?")
        public ActionTaken selesai;
    }

    class ActionTaken {
        public Boolean selected;
//        public String remarks;

//        @Override
//        public String toString() {
//            return remarks;
//        }
    }
}
