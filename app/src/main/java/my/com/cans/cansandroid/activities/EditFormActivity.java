package my.com.cans.cansandroid.activities;

import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
import my.com.cans.cansandroid.objects.enums.FormField;
import my.com.cans.cansandroid.objects.enums.FormGroup;
import my.com.cans.cansandroid.objects.interfaces.Description;
import my.com.cans.cansandroid.objects.interfaces.Order;
import my.com.cans.cansandroid.objects.interfaces.SuisField;
import my.com.cans.cansandroid.objects.interfaces.SuisGroup;
import my.com.cans.cansandroid.services.BaseAPICallback;
import my.com.cans.cansandroid.services.MobileAPI;
import my.com.cans.cansandroid.services.MobileAPIResponse;
import my.com.cans.cansandroid.services.MyHTTP;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Rfeng on 06/04/2017.
 */

public class EditFormActivity extends EditPageActivity implements OnSubmitListener, BaseEditFragment.OnBuildFieldListener, BaseEditFragment.OnBuildControlListener, BaseEditFragment.LabelWidthListener, View.OnClickListener, CustomPicker.OnCustomPickerListerner, LocationListener {
    public String getKey() {
        return getIntent().getStringExtra("key");
    }

    CustomLocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationManager = new CustomLocationManager(this);

        updateDevices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationManager.destroy();
    }

    @Override
    public void submitForm(Object model, final View sender) {
        if (model instanceof FormModel) {
            FormModel item = (FormModel) model;

            if (item != null) {
                MobileAPIResponse.FormResult request = new MobileAPIResponse().new FormResult();
                List<MobileAPIResponse.FormData> formDatas = new ArrayList<>();
                request.ID = getKey();
                for (MobileAPIResponse.GetDevicesResult device : mDevices) {
                    if (device.DeviceID.equals(item.deviceID)) {
                        request.DeviceID = device.ID;
                        break;
                    }
                }
                if (ValidateManager.isEmptyOrNull(request.DeviceID))
                    request.DeviceID = item.deviceID;
                request.Tarikh = item.tarikh;
                request.NamaRumahPam = item.namaRumahPam;
                request.Wilayah = item.wilayah;
                request.PapanSuisUtamaLV = item.papanSuisUtamaLV;
                request.PapanSuisUtamaHT = item.papanSuisUtamaHT;
                request.PapanSuisUtamaCatitan = item.papanSuisUtamaCatitan;
                request.GegantiPerlindunganCatitan = item.gegantiPerlindunganCatitan;
                request.SuisGearCatitan = item.suisGearCatitan;
                request.MotorRotorPerperintangNilaiVoltan = item.motorRotorBerperintangNilaiVoltan;
                request.MotorRotorPerperintangNilaiWatts = item.motorRotorBerperintangNilaiWatts;
                request.MotorSangkarTupaiNilaiVoltan = item.motorSangkarTupaiNilaiVoltan;
                request.MotorSangkarTupaiNilaiWatts = item.motorSangkarTupaiNilaiWatts;
                request.VariableSpeedSystemNilaiVoltan = item.variableSpeedSystemNilaiVoltan;
                request.VariableSpeedSystemNilaiWatts = item.variableSpeedSystemNilaiWatts;

                BaseFormField[] fields = getEditFragment().getFields();
                for (BaseFormField field : fields) {
                    if (field instanceof EditFormField) {
                        EditFormField editFormField = (EditFormField) field;
                        if (editFormField != null && editFormField.getGroup() != null) {
                            Suis obj = null;
                            try {
                                obj = (Suis) field.field.get(item);
                                MobileAPIResponse.FormData formData = buildFormData(
                                        editFormField.getGroup(),
                                        editFormField.getField(),
                                        obj);
                                formDatas.add(formData);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                request.FormData = formDatas.toArray(new MobileAPIResponse.FormData[0]);

                new MyHTTP(this).call(MobileAPI.class).updateForm(request).enqueue(new BaseAPICallback<MobileAPIResponse.FormResponse>(this) {
                    @Override
                    public void onResponse(Call<MobileAPIResponse.FormResponse> call, Response<MobileAPIResponse.FormResponse> response) {
                        super.onResponse(call, response);

                        MobileAPIResponse.FormResponse resp = response.body();
                        if (resp.Succeed) {
                            String key = resp.Result.ID;
                            if (key != null) {
                                EditFormActivity.this.finish();
                            }
                        }
                    }
                });
            }
        }
    }

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
        } else if (field.name.startsWith("subtitle_")) {
            CustomTextView title = new CustomTextView(this);
            title.setTypeface(title.getTypeface(), Typeface.BOLD);
            title.setText(new Convert(field.value).to());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = getPixel(R.dimen.nav_header_vertical_spacing);
            title.setLayoutParams(layoutParams);
            control = title;
        } else if (field.name.equals("deviceID")) {
            mDevicePicker = (CustomPicker) ((TextInputLayout) control.findViewWithTag(field.name)).getEditText();
        } else if (Suis.class.isAssignableFrom(field.field.getType())) {
            Class<?> type = field.field.getType();

            if (field.value == null) {
                if (type == SuisRemarks.class)
                    field.value = new SuisRemarks();
                else if (type == SuisAmpere.class)
                    field.value = new SuisAmpere();
                else if (type == SuisTarikh.class)
                    field.value = new SuisTarikh();
                else if (type == Suis.class)
                    field.value = new Suis();
            }

            TextInputLayout layout = (TextInputLayout) field.control.findViewWithTag(field.name);
            EditText editText = layout.getEditText();

            if (SuisRemarks.class.isAssignableFrom(type)) {
                editText.setHint(R.string.sila_nyatakan_kerosakan);
                editText.setVisibility(View.GONE);
            } else if (SuisTarikh.class.isAssignableFrom(type)) {
                ViewGroup parent = ((ViewGroup) layout.getParent());
                parent.removeView(layout);
                layout = new TextInputLayout(this);
                layout.setTag(field.name);
                editText = new DateTimePicker(this);
                editText.setHint(R.string.tarikh_kablikasi);
                editText.setText(new Convert(field.getData()).to());
                layout.addView(editText);
                parent.addView(layout);
                layout.setHintEnabled(false);
            } else if (SuisAmpere.class.isAssignableFrom(type)) {
                editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.setHint(R.string.nilai_ampere);
            } else {
                editText.setVisibility(View.GONE);
            }

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setTag("Checkboxes");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = getPixel(R.dimen.label_top_margin);
            layoutParams.leftMargin = getPixel(R.dimen.list_view_divider);
            layout.addView(linearLayout, 0, layoutParams);

            LinearLayout.LayoutParams marginLeftLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            marginLeftLayoutParams.leftMargin = getPixel(R.dimen.list_view_divider);

            Suis suis = (Suis) field.value;

            CustomImageView imgBaik = new CustomImageView(this);
            if (suis.baik != null && suis.baik == true)
                imgBaik.setImageResource(R.drawable.ic_check);
            else
                imgBaik.setImageResource(R.drawable.ic_check_box_outline);
            linearLayout.addView(imgBaik);
            CustomTextView labelBaik = new CustomTextView(this);
            labelBaik.setText(getString(R.string.baik));
            linearLayout.addView(labelBaik, marginLeftLayoutParams);

            linearLayout.addView(new View(this), new LinearLayout.LayoutParams(getPixel(R.dimen.short_label_width), ViewGroup.LayoutParams.WRAP_CONTENT));

            CustomImageView imgRosak = new CustomImageView(this);
            if (suis.baik != null && suis.baik == false) {
                imgRosak.setImageResource(R.drawable.ic_check);
                if (suis instanceof SuisRemarks)
                    editText.setVisibility(View.VISIBLE);
            } else
                imgRosak.setImageResource(R.drawable.ic_check_box_outline);
            linearLayout.addView(imgRosak);
            CustomTextView labelRosak = new CustomTextView(this);
            labelRosak.setText(getString(R.string.rosak));
            linearLayout.addView(labelRosak, marginLeftLayoutParams);

            labelBaik.setTag(field);
            labelBaik.setOnClickListener(this);
            imgBaik.setTag(field);
            imgBaik.setOnClickListener(this);
            labelRosak.setTag(field);
            labelRosak.setOnClickListener(this);
            imgRosak.setTag(field);
            imgRosak.setOnClickListener(this);
        }

        return control;
    }

    @Override
    public void onClick(View v) {
        BaseFormField field = (BaseFormField) v.getTag();
        Suis suis = (Suis) field.value;
        LinearLayout linearLayout = (LinearLayout) field.control.findViewWithTag("Checkboxes");
        suis.baik = v == linearLayout.getChildAt(0) || v == linearLayout.getChildAt(1);

        if (suis.baik) {
            ((ImageView) linearLayout.getChildAt(0)).setImageResource(R.drawable.ic_check);
            ((ImageView) linearLayout.getChildAt(3)).setImageResource(R.drawable.ic_check_box_outline);
        } else {
            ((ImageView) linearLayout.getChildAt(0)).setImageResource(R.drawable.ic_check_box_outline);
            ((ImageView) linearLayout.getChildAt(3)).setImageResource(R.drawable.ic_check);
        }

        if (suis instanceof SuisRemarks) {
            TextInputLayout layout = (TextInputLayout) field.control.findViewWithTag(field.name);
            EditText editText = layout.getEditText();
            if (suis.baik)
                editText.setVisibility(View.GONE);
            else {
                editText.setVisibility(View.VISIBLE);
                editText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }
    }

    private MobileAPIResponse.FormResult mResult;

    private MobileAPIResponse.GetDevicesResult[] mDevices;

    @Override
    protected void refresh(final SwipeRefreshLayout swipeRefreshLayout) {
        MobileAPIResponse.IdResult request = new MobileAPIResponse().new IdResult();
        request.ID = getKey();

        if (request.ID == null) {
            EditFormActivity.super.refresh(swipeRefreshLayout);
            updateDevices();
        }
        else
            new MyHTTP(this).call(MobileAPI.class).getForm(request).enqueue(new BaseAPICallback<MobileAPIResponse.FormResponse>(this) {
                @Override
                public void onResponse(Call<MobileAPIResponse.FormResponse> call, Response<MobileAPIResponse.FormResponse> response) {
                    super.onResponse(call, response);

                    MobileAPIResponse.FormResponse resp = response.body();
                    if (resp != null && resp.Succeed) {
                        mResult = resp.Result;
                        EditFormActivity.super.refresh(swipeRefreshLayout);
                        updateDevices();
                    }
                }
            });
    }

    private void updateDevices() {
        if (mDevices == null && mDevicePicker != null) {
            Location location = CustomLocationManager.getCurrentLocation();
            MobileAPIResponse.CoordinateRequest request = new MobileAPIResponse().new CoordinateRequest();
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
                            if (item.ID.equals(mFormModel.deviceID))
                                selectedDevice = item;
                        }
                        mDevicePicker.setup(getString(R.string.select), choices);
                        if (selectedDevice != null) {
                            if (ValidateManager.isEmptyOrNull(mFormModel.deviceID)) {
                                mFormModel.deviceID = selectedDevice.DeviceID;
                                ((CustomEditText) getEditFragment().getField("namaRumahPam").getEditControl()).setText(selectedDevice.Kawasan);
                                ((CustomEditText) getEditFragment().getField("wilayah").getEditControl()).setText(selectedDevice.Lokasi);
                            }
                            mDevicePicker.setText(selectedDevice.DeviceID);
                        }
                    }
                }
            });
        }
    }

    private FormModel mFormModel;

    @Override
    public Object buildModel() {
        if (mFormModel != null)
            return mFormModel;

        mFormModel = new FormModel();
        if (mResult == null) {
            mFormModel.tarikh = new Date();
            return mFormModel;
        }

        mFormModel.tarikh = mResult.Tarikh;
        mFormModel.deviceID = mResult.DeviceID;
        mFormModel.namaRumahPam = mResult.NamaRumahPam;
        mFormModel.wilayah = mResult.Wilayah;

        mFormModel.papanSuisUtamaLV = mResult.PapanSuisUtamaLV;
        mFormModel.papanSuisUtamaHT = mResult.PapanSuisUtamaHT;
        mFormModel.papanSuisUtamaCatitan = mResult.PapanSuisUtamaCatitan;
        mFormModel.gegantiPerlindunganCatitan = mResult.GegantiPerlindunganCatitan;
        mFormModel.suisGearCatitan = mResult.SuisGearCatitan;

        mFormModel.motorRotorBerperintangNilaiVoltan = mResult.MotorRotorPerperintangNilaiVoltan;
        mFormModel.motorRotorBerperintangNilaiWatts = mResult.MotorRotorPerperintangNilaiWatts;
        mFormModel.motorSangkarTupaiNilaiVoltan = mResult.MotorSangkarTupaiNilaiVoltan;
        mFormModel.motorSangkarTupaiNilaiWatts = mResult.MotorSangkarTupaiNilaiWatts;
        mFormModel.variableSpeedSystemNilaiVoltan = mResult.VariableSpeedSystemNilaiVoltan;
        mFormModel.variableSpeedSystemNilaiWatts = mResult.VariableSpeedSystemNilaiWatts;

        List<EditFormField> editFormFields = new ArrayList<>();
        Field[] fields = mFormModel.getClass().getFields();
        for (Field field : fields) {
            if (Modifier.isPublic(field.getModifiers()) &&
                    !Modifier.isStatic(field.getModifiers()) &&
                    !Modifier.isFinal(field.getModifiers())) {
                if (Suis.class.isAssignableFrom(field.getType()))
                    editFormFields.add((EditFormField) buildField(new BaseFormField(this, field, mFormModel)));
            }
        }

        for (EditFormField field : editFormFields) {
            if (field.getGroup() != null) {
                MobileAPIResponse.FormData formData = mResult.getFormData(field.getGroup(), field.getField());
                Suis suis;
                if (field.field.getType() == SuisRemarks.class)
                    suis = buildSuisRemarks(formData);
                else if (field.field.getType() == SuisTarikh.class)
                    suis = buildSuisTarikh(formData);
                else if (field.field.getType() == SuisAmpere.class)
                    suis = buildSuisAmpere(formData);
                else
                    suis = buildSuis(formData);

                try {
                    field.field.set(mFormModel, suis);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return mFormModel;
    }

    private MobileAPIResponse.FormData buildFormData(FormGroup group, FormField field, Suis suis) {
        MobileAPIResponse.FormData item = new MobileAPIResponse().new FormData();
        item.GroupID = group.getCode();
        item.ColumnID = field.getCode();
        item.Baik = suis.baik;

        if (suis instanceof SuisRemarks)
            item.Remarks = ((SuisRemarks) suis).remarks;
        if (suis instanceof SuisTarikh)
            item.Tarikh = ((SuisTarikh) suis).tarikhKalibrasi;
        if (suis instanceof SuisAmpere)
            item.Nilai = ((SuisAmpere) suis).nilaiAmpere;

        return item;
    }

    private Suis buildSuis(MobileAPIResponse.FormData formData) {
        Suis suis = new Suis();
        suis.baik = formData.Baik;
        return suis;
    }

    private SuisRemarks buildSuisRemarks(MobileAPIResponse.FormData formData) {
        SuisRemarks suis = new SuisRemarks();
        suis.baik = formData.Baik;
        suis.remarks = formData.Remarks;
        return suis;
    }

    private SuisTarikh buildSuisTarikh(MobileAPIResponse.FormData formData) {
        SuisTarikh suis = new SuisTarikh();
        suis.baik = formData.Baik;
        suis.tarikhKalibrasi = formData.Tarikh;
        return suis;
    }

    private SuisAmpere buildSuisAmpere(MobileAPIResponse.FormData formData) {
        SuisAmpere suis = new SuisAmpere();
        suis.baik = formData.Baik;
        suis.nilaiAmpere = formData.Nilai;
        return suis;
    }

    @Override
    public int getLabelWidth() {
        return getPixel(R.dimen.long_label_width);
    }

    @Override
    public BaseFormField buildField(BaseFormField field) {
        if (Suis.class.isAssignableFrom(field.field.getType())) {
            return new EditFormField(this, field.field, mFormModel);
        } else if (field.name.equals("deviceID")) {
            field.choices = new ArrayList<>();
            field.choices.add(new Convert(field.value).to());
        } else if (field.name.equals("tarikh")) {
            field.readonly = true;
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
                    mFormModel.deviceID = newValue;
                    mFormModel.namaRumahPam = item.Kawasan;
                    mFormModel.wilayah = item.Lokasi;
                    super.refresh(null);
                    loadVoltage(item.ID);
                    break;
                }
            }
        }
    }

    private void loadVoltage(String deviceID) {
        MobileAPIResponse.IdResult idResult = new MobileAPIResponse().new IdResult();
        idResult.ID = deviceID;
        new MyHTTP(this).call(MobileAPI.class).getDeviceValue(idResult).enqueue(new BaseAPICallback<MobileAPIResponse.GetDeviceValueResponse>(this) {
            @Override
            public void onResponse(Call<MobileAPIResponse.GetDeviceValueResponse> call, Response<MobileAPIResponse.GetDeviceValueResponse> response) {
                super.onResponse(call, response);
                MobileAPIResponse.GetDeviceValueResponse resp = response.body();
                if (resp != null && resp.Succeed) {
                    MobileAPIResponse.GetDeviceValueResult result = resp.Result;
                    if (result != null) {
                        mFormModel.papanSuisUtamaLV = result.VoltageL1;
                    }
                }

                EditFormActivity.super.refresh(null);
            }
        });
    }

    class EditFormField extends BaseFormField {
        private FormGroup mGroup;
        private FormField mField;

        public EditFormField(BaseActivity context, Field field, Object item) {
            super(context, field, item);

            SuisGroup suisGroup = field.getAnnotation(SuisGroup.class);
            if (suisGroup != null)
                mGroup = suisGroup.value();
            SuisField suisField = field.getAnnotation(SuisField.class);
            if (suisField != null)
                mField = suisField.value();
        }

        public FormGroup getGroup() {
            return mGroup;
        }

        public FormField getField() {
            return mField;
        }

        @Override
        public Object getData() {
            if (Suis.class.isAssignableFrom(this.field.getType())) {
                Suis suis = (Suis) this.value;

                if (this.control == null)
                    return suis;

                TextInputLayout inputLayout = (TextInputLayout) this.control.findViewWithTag(this.name);
                if (inputLayout != null) {
                    EditText editText = inputLayout.getEditText();
                    if (suis instanceof SuisRemarks) {
                        if (editText.getVisibility() != View.GONE)
                            ((SuisRemarks) suis).remarks = editText.getText().toString();
                        else
                            ((SuisRemarks) suis).remarks = null;
                    } else if (suis instanceof SuisTarikh)
                        ((SuisTarikh) suis).tarikhKalibrasi = ((DateTimePicker) editText).getDate();
                    else if (suis instanceof SuisAmpere)
                        ((SuisAmpere) suis).nilaiAmpere = new Convert(editText.getText().toString()).to(Double.class);
                }

                return suis;
            }
            return super.getData();
        }
    }

    class FormModel {
        @NonNull
        @Order(1)
        public Date tarikh;
        @NonNull
        @Order(2)
        @Description("Device ID")
        public String deviceID;
        @NonNull
        @Order(2)
        @Description("Nama Rumah Pam & Kolam")
        public String namaRumahPam;
        @NonNull
        @Order(3)
        public String wilayah;

        @Order(4)
        public String title_rumahPam = "1. RUMAH PAM";

        @Order(5)
        public String title_papanSuisUtama = "a. PAPAN SUIS UTAMA";
        @Order(6)
        @Description("LV")
        public Double papanSuisUtamaLV;
        @Order(7)
        @Description("HT")
        public Double papanSuisUtamaHT;

        @Order(8)
        @SuisGroup(FormGroup.PapanSuisUtama)
        @SuisField(FormField.Voltmeter)
        public SuisRemarks voltmeter;
        @Order(9)
        @SuisGroup(FormGroup.PapanSuisUtama)
        @SuisField(FormField.Ammeter)
        public SuisRemarks ammeter;
        @Order(10)
        @SuisGroup(FormGroup.PapanSuisUtama)
        @SuisField(FormField.PowerFactorMeter)
        public SuisRemarks powerFactorMeter;
        @Order(11)
        @SuisGroup(FormGroup.PapanSuisUtama)
        @SuisField(FormField.LampuPenunjuk)
        public SuisRemarks lampuPenunjuk;
        @Order(12)
        @SuisGroup(FormGroup.PapanSuisUtama)
        @SuisField(FormField.GetahLantai)
        public SuisRemarks getahLantai;
        @Order(13)
        @SuisGroup(FormGroup.PapanSuisUtama)
        @SuisField(FormField.LukisanSkematik)
        public SuisRemarks lukisanSkematik;

        @Order(14)
        @Description("Catitan")
        public String papanSuisUtamaCatitan;

        @Order(15)
        public String subtitle_htTransformer = "HT Transformer";
        @Order(16)
        @SuisGroup(FormGroup.PapanSuisUtama)
        @SuisField(FormField.ParasMinyak)
        public Suis parasMinyak;
        @Order(17)
        @SuisGroup(FormGroup.PapanSuisUtama)
        @SuisField(FormField.SilicaGel)
        public Suis silicaGel;
        @Order(18)
        @SuisGroup(FormGroup.PapanSuisUtama)
        @SuisField(FormField.MeterSuhu)
        public Suis meterSuhu;
        @Order(19)
        @SuisGroup(FormGroup.PapanSuisUtama)
        @SuisField(FormField.SistemPembumian)
        public Suis sistemPembumian;

        @Order(100)
        public String title_gegantiPerlindungan = "b. GEGANTI PERLINDUNGAN";
        @Order(101)
        @SuisGroup(FormGroup.GegantiPerlindungan)
        @SuisField(FormField.Overcurrent)
        public SuisTarikh overcurrent;
        @Order(102)
        @SuisGroup(FormGroup.GegantiPerlindungan)
        @SuisField(FormField.EarthFault)
        public SuisTarikh earthFault;
        @Order(103)
        @Description("Catitan")
        public String gegantiPerlindunganCatitan;

        @Order(200)
        public String title_suisGear = "c. SUIS GEAR";
        @Order(201)
        @Description("Air Circuit Breaker (ACB)")
        @SuisGroup(FormGroup.SuisGear)
        @SuisField(FormField.EarthFault)
        public SuisAmpere airCircuitBreaker;
        @Order(202)
        @SuisGroup(FormGroup.SuisGear)
        @SuisField(FormField.MCCB)
        @Description("MCCB")
        public SuisAmpere mccb;
        @Order(203)
        @SuisGroup(FormGroup.SuisGear)
        @SuisField(FormField.FiusSuis)
        @Description("Fius Suis")
        public SuisAmpere fiusSuis;
        @Order(204)
        @SuisGroup(FormGroup.SuisGear)
        @SuisField(FormField.SuisFuis)
        @Description("Suis Fius")
        public SuisAmpere suisFius;
        @Order(205)
        @Description("Catitan")
        public String suisGearCatitan;

        @Order(300)
        public String title_motorRotorBerperintang = "d. MOTOR ROTOR BERPERINTANG";
        @Order(301)
        @Description("Nilai Watts/HP (kW)")
        public Double motorRotorBerperintangNilaiWatts;
        @Order(302)
        @Description("Nilai Voltan (V)")
        public Double motorRotorBerperintangNilaiVoltan;
        @Order(303)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.MotorNo1)
        @Description("Motor No. 1")
        public Suis motorRotorBerperintangMotorNo1;
        @Order(304)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.MotorNo2)
        @Description("Motor No. 2")
        public Suis motorRotorBerperintangMotorNo2;
        @Order(305)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.MotorNo3)
        @Description("Motor No. 3")
        public Suis motorRotorBerperintangMotorNo3;
        @Order(306)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.MotorNo4)
        @Description("Motor No. 4")
        public Suis motorRotorBerperintangMotorNo4;

        @Order(310)
        public String subtitle_motorRotorBerperintangSlipRing = "Slip Ring";
        @Order(311)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.SlipRing1)
        @Description("Motor No. 1")
        public Suis motorRotorBerperintangSlipRing1;
        @Order(312)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.SlipRing2)
        @Description("Motor No. 2")
        public Suis motorRotorBerperintangSlipRing2;
        @Order(313)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.SlipRing3)
        @Description("Motor No. 3")
        public Suis motorRotorBerperintangSlipRing3;
        @Order(314)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.SlipRing4)
        @Description("Motor No. 4")
        public Suis motorRotorBerperintangSlipRing4;

        @Order(320)
        public String subtitle_motorRotorBerperintangCarbonBrush = "Carbon Brush";
        @Order(321)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.CarbonBrush1)
        @Description("Motor No. 1")
        public Suis motorRotorBerperintangCarbonBrush1;
        @Order(322)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.CarbonBrush2)
        @Description("Motor No. 2")
        public Suis motorRotorBerperintangCarbonBrush2;
        @Order(323)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.CarbonBrush3)
        @Description("Motor No. 3")
        public Suis motorRotorBerperintangCarbonBrush3;
        @Order(324)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.CarbonBrush4)
        @Description("Motor No. 4")
        public Suis motorRotorBerperintangCarbonBrush4;

        @Order(330)
        public String subtitle_motorRotorBerperintangBearing = "Bearing";
        @Order(331)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.Bearing1)
        @Description("Motor No. 1")
        public Suis motorRotorBerperintangBearing1;
        @Order(332)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.Bearing2)
        @Description("Motor No. 2")
        public Suis motorRotorBerperintangBearing2;
        @Order(333)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.Bearing3)
        @Description("Motor No. 3")
        public Suis motorRotorBerperintangBearing3;
        @Order(334)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.Bearing4)
        @Description("Motor No. 4")
        public Suis motorRotorBerperintangBearing4;

        @Order(340)
        public String subtitle_motorRotorBerperintangPenjajaran = "Penjajaran";
        @Order(341)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.Penjajaran1)
        @Description("Motor No. 1")
        public Suis motorRotorBerperintangPenjajaran1;
        @Order(342)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.Penjajaran2)
        @Description("Motor No. 2")
        public Suis motorRotorBerperintangPenjajaran2;
        @Order(343)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.Penjajaran3)
        @Description("Motor No. 3")
        public Suis motorRotorBerperintangPenjajaran3;
        @Order(344)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.Penjajaran4)
        @Description("Motor No. 4")
        public Suis motorRotorBerperintangPenjajaran4;

        @Order(350)
        public String subtitle_motorRotorBerperintangSambunganKabel = "Sambungan Kabel di Suis Utama";
        @Order(351)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.SambunganKabel1)
        @Description("Motor No. 1")
        public Suis motorRotorBerperintangSambunganKabel1;
        @Order(352)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.SambunganKabel2)
        @Description("Motor No. 2")
        public Suis motorRotorBerperintangSambunganKabel2;
        @Order(353)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.SambunganKabel3)
        @Description("Motor No. 3")
        public Suis motorRotorBerperintangSambunganKabel3;
        @Order(354)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.SambunganKabel4)
        @Description("Motor No. 4")
        public Suis motorRotorBerperintangSambunganKabel4;

        @Order(360)
        public String subtitle_motorRotorBerperintangStarterPanelBoard = "Starter Panel Board";
        @Order(361)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.StarterPanelBoard1)
        @Description("Motor No. 1")
        public Suis motorRotorBerperintangStarterPanelBoard1;
        @Order(362)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.StarterPanelBoard2)
        @Description("Motor No. 2")
        public Suis motorRotorBerperintangStarterPanelBoard2;
        @Order(363)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.StarterPanelBoard3)
        @Description("Motor No. 3")
        public Suis motorRotorBerperintangStarterPanelBoard3;
        @Order(364)
        @SuisGroup(FormGroup.MotorRotorBerperintang)
        @SuisField(FormField.StarterPanelBoard4)
        @Description("Motor No. 4")
        public Suis motorRotorBerperintangStarterPanelBoard4;

        @Order(400)
        public String title_motorSangkarTupai = "e. MOTOR SANGKAR TUPAI";
        @Order(401)
        @Description("Nilai Watts/HP (kW)")
        public Double motorSangkarTupaiNilaiWatts;
        @Order(402)
        @Description("Nilai Voltan (V)")
        public Double motorSangkarTupaiNilaiVoltan;
        @Order(403)
        @SuisGroup(FormGroup.MotorSangkarTupai)
        @SuisField(FormField.MotorNo1)
        @Description("Motor No. 1")
        public Suis motorSangkarTupaiMotorNo1;
        @Order(404)
        @SuisGroup(FormGroup.MotorSangkarTupai)
        @SuisField(FormField.MotorNo2)
        @Description("Motor No. 2")
        public Suis motorSangkarTupaiMotorNo2;
        @Order(405)
        @SuisGroup(FormGroup.MotorSangkarTupai)
        @SuisField(FormField.MotorNo3)
        @Description("Motor No. 3")
        public Suis motorSangkarTupaiMotorNo3;
        @Order(406)
        @SuisGroup(FormGroup.MotorSangkarTupai)
        @SuisField(FormField.MotorNo4)
        @Description("Motor No. 4")
        public Suis motorSangkarTupaiMotorNo4;

        @Order(430)
        public String subtitle_motorSangkarTupaiBearing = "Bearing";
        @Order(431)
        @SuisGroup(FormGroup.MotorSangkarTupai)
        @SuisField(FormField.Bearing1)
        @Description("Motor No. 1")
        public Suis motorSangkarTupaiBearing1;
        @Order(432)
        @SuisGroup(FormGroup.MotorSangkarTupai)
        @SuisField(FormField.Bearing2)
        @Description("Motor No. 2")
        public Suis motorSangkarTupaiBearing2;
        @Order(433)
        @SuisGroup(FormGroup.MotorSangkarTupai)
        @SuisField(FormField.Bearing3)
        @Description("Motor No. 3")
        public Suis motorSangkarTupaiBearing3;
        @Order(434)
        @SuisGroup(FormGroup.MotorSangkarTupai)
        @SuisField(FormField.Bearing4)
        @Description("Motor No. 4")
        public Suis motorSangkarTupaiBearing4;

        @Order(440)
        public String subtitle_motorSangkarTupaiPenjajaran = "Penjajaran";
        @Order(441)
        @SuisGroup(FormGroup.MotorSangkarTupai)
        @SuisField(FormField.Penjajaran1)
        @Description("Motor No. 1")
        public Suis motorSangkarTupaiPenjajaran1;
        @Order(442)
        @SuisGroup(FormGroup.MotorSangkarTupai)
        @SuisField(FormField.Penjajaran2)
        @Description("Motor No. 2")
        public Suis motorSangkarTupaiPenjajaran2;
        @Order(443)
        @SuisGroup(FormGroup.MotorSangkarTupai)
        @SuisField(FormField.Penjajaran3)
        @Description("Motor No. 3")
        public Suis motorSangkarTupaiPenjajaran3;
        @Order(444)
        @SuisGroup(FormGroup.MotorSangkarTupai)
        @SuisField(FormField.Penjajaran4)
        @Description("Motor No. 4")
        public Suis motorSangkarTupaiPenjajaran4;

        @Order(450)
        public String subtitle_motorSangkarTupaiSambunganKabel = "Sambungan Kabel di Suis Utama";
        @Order(451)
        @SuisGroup(FormGroup.MotorSangkarTupai)
        @SuisField(FormField.SambunganKabel1)
        @Description("Motor No. 1")
        public Suis motorSangkarTupaiSambunganKabel1;
        @Order(452)
        @SuisGroup(FormGroup.MotorSangkarTupai)
        @SuisField(FormField.SambunganKabel2)
        @Description("Motor No. 2")
        public Suis motorSangkarTupaiSambunganKabel2;
        @Order(453)
        @SuisGroup(FormGroup.MotorSangkarTupai)
        @SuisField(FormField.SambunganKabel3)
        @Description("Motor No. 3")
        public Suis motorSangkarTupaiSambunganKabel3;
        @Order(454)
        @SuisGroup(FormGroup.MotorSangkarTupai)
        @SuisField(FormField.SambunganKabel4)
        @Description("Motor No. 4")
        public Suis motorSangkarTupaiSambunganKabel4;

        @Order(460)
        public String subtitle_motorSangkarTupaiStarterPanelBoard = "Starter Panel Board";
        @Order(461)
        @SuisGroup(FormGroup.MotorSangkarTupai)
        @SuisField(FormField.StarterPanelBoard1)
        @Description("Motor No. 1")
        public Suis motorSangkarTupaiStarterPanelBoard1;
        @Order(462)
        @SuisGroup(FormGroup.MotorSangkarTupai)
        @SuisField(FormField.StarterPanelBoard2)
        @Description("Motor No. 2")
        public Suis motorSangkarTupaiStarterPanelBoard2;
        @Order(463)
        @SuisGroup(FormGroup.MotorSangkarTupai)
        @SuisField(FormField.StarterPanelBoard3)
        @Description("Motor No. 3")
        public Suis motorSangkarTupaiStarterPanelBoard3;
        @Order(464)
        @SuisGroup(FormGroup.MotorSangkarTupai)
        @SuisField(FormField.StarterPanelBoard4)
        @Description("Motor No. 4")
        public Suis motorSangkarTupaiStarterPanelBoard4;

        @Order(500)
        public String title_variableSpeedSystem = "f. VARIABLE SPEED SYSTEM (VSD)";
        @Order(501)
        @Description("Nilai Watts/HP (kW)")
        public Double variableSpeedSystemNilaiWatts;
        @Order(502)
        @Description("Nilai Voltan (V)")
        public Double variableSpeedSystemNilaiVoltan;
        @Order(503)
        @SuisGroup(FormGroup.VariableSpeedSystem)
        @SuisField(FormField.MotorNo1)
        @Description("Motor No. 1")
        public Suis variableSpeedSystemMotorNo1;
        @Order(504)
        @SuisGroup(FormGroup.VariableSpeedSystem)
        @SuisField(FormField.MotorNo2)
        @Description("Motor No. 2")
        public Suis variableSpeedSystemMotorNo2;
        @Order(505)
        @SuisGroup(FormGroup.VariableSpeedSystem)
        @SuisField(FormField.MotorNo3)
        @Description("Motor No. 3")
        public Suis variableSpeedSystemMotorNo3;
        @Order(506)
        @SuisGroup(FormGroup.VariableSpeedSystem)
        @SuisField(FormField.MotorNo4)
        @Description("Motor No. 4")
        public Suis variableSpeedSystemMotorNo4;

        @Order(530)
        public String subtitle_variableSpeedSystemBearing = "Bearing";
        @Order(531)
        @SuisGroup(FormGroup.VariableSpeedSystem)
        @SuisField(FormField.Bearing1)
        @Description("Motor No. 1")
        public Suis variableSpeedSystemBearing1;
        @Order(532)
        @SuisGroup(FormGroup.VariableSpeedSystem)
        @SuisField(FormField.Bearing2)
        @Description("Motor No. 2")
        public Suis variableSpeedSystemBearing2;
        @Order(533)
        @SuisGroup(FormGroup.VariableSpeedSystem)
        @SuisField(FormField.Bearing3)
        @Description("Motor No. 3")
        public Suis variableSpeedSystemBearing3;
        @Order(534)
        @SuisGroup(FormGroup.VariableSpeedSystem)
        @SuisField(FormField.Bearing4)
        @Description("Motor No. 4")
        public Suis variableSpeedSystemBearing4;

        @Order(540)
        public String subtitle_variableSpeedSystemPenjajaran = "Penjajaran";
        @Order(541)
        @SuisGroup(FormGroup.VariableSpeedSystem)
        @SuisField(FormField.Penjajaran1)
        @Description("Motor No. 1")
        public Suis variableSpeedSystemPenjajaran1;
        @Order(542)
        @SuisGroup(FormGroup.VariableSpeedSystem)
        @SuisField(FormField.Penjajaran2)
        @Description("Motor No. 2")
        public Suis variableSpeedSystemPenjajaran2;
        @Order(543)
        @SuisGroup(FormGroup.VariableSpeedSystem)
        @SuisField(FormField.Penjajaran3)
        @Description("Motor No. 3")
        public Suis variableSpeedSystemPenjajaran3;
        @Order(544)
        @SuisGroup(FormGroup.VariableSpeedSystem)
        @SuisField(FormField.Penjajaran4)
        @Description("Motor No. 4")
        public Suis variableSpeedSystemPenjajaran4;

        @Order(550)
        public String subtitle_variableSpeedSystemSambunganKabel = "Sambungan Kabel di Suis Utama";
        @Order(551)
        @SuisGroup(FormGroup.VariableSpeedSystem)
        @SuisField(FormField.SambunganKabel1)
        @Description("Motor No. 1")
        public Suis variableSpeedSystemSambunganKabel1;
        @Order(552)
        @SuisGroup(FormGroup.VariableSpeedSystem)
        @SuisField(FormField.SambunganKabel2)
        @Description("Motor No. 2")
        public Suis variableSpeedSystemSambunganKabel2;
        @Order(553)
        @SuisGroup(FormGroup.VariableSpeedSystem)
        @SuisField(FormField.SambunganKabel3)
        @Description("Motor No. 3")
        public Suis variableSpeedSystemSambunganKabel3;
        @Order(554)
        @SuisGroup(FormGroup.VariableSpeedSystem)
        @SuisField(FormField.SambunganKabel4)
        @Description("Motor No. 4")
        public Suis variableSpeedSystemSambunganKabel4;

        @Order(560)
        public String subtitle_variableSpeedSystemStarterPanelBoard = "Starter Panel Board";
        @Order(561)
        @SuisGroup(FormGroup.VariableSpeedSystem)
        @SuisField(FormField.StarterPanelBoard1)
        @Description("Motor No. 1")
        public Suis variableSpeedSystemStarterPanelBoard1;
        @Order(562)
        @SuisGroup(FormGroup.VariableSpeedSystem)
        @SuisField(FormField.StarterPanelBoard2)
        @Description("Motor No. 2")
        public Suis variableSpeedSystemStarterPanelBoard2;
        @Order(563)
        @SuisGroup(FormGroup.VariableSpeedSystem)
        @SuisField(FormField.StarterPanelBoard3)
        @Description("Motor No. 3")
        public Suis variableSpeedSystemStarterPanelBoard3;
        @Order(564)
        @SuisGroup(FormGroup.VariableSpeedSystem)
        @SuisField(FormField.StarterPanelBoard4)
        @Description("Motor No. 4")
        public Suis variableSpeedSystemStarterPanelBoard4;

        @Order(600)
        public String title_autoSystem = "g. AUTO SYSTEM";
        @Order(601)
        @Description("Auto No. 1")
        @SuisGroup(FormGroup.AutoSystem)
        @SuisField(FormField.AutoNo1)
        public Suis autoNo1;
        @Order(602)
        @SuisGroup(FormGroup.AutoSystem)
        @SuisField(FormField.AutoNo3)
        @Description("Auto No. 3")
        public Suis autoNo3;
        @Order(603)
        @SuisGroup(FormGroup.AutoSystem)
        @SuisField(FormField.ELPRO)
        @Description("ELPRO")
        public Suis elpro;
        @Order(604)
        @SuisGroup(FormGroup.AutoSystem)
        @SuisField(FormField.Timer)
        @Description("Timer")
        public Suis timer;
        @Order(605)
        @SuisGroup(FormGroup.AutoSystem)
        @SuisField(FormField.Manual)
        @Description("Manual")
        public Suis manual;

        @Order(700)
        public String title_rumahPamDanTangkiSedut = "2. RUMAH PAM DAN TANGKI SEDUT";
        @Order(701)
        @SuisGroup(FormGroup.RumahPamDanTangkiSedut)
        @SuisField(FormField.Elektrod)
        @Description("Elektrod")
        public Suis rumahPamDanTangkiSedutElektrod;
        @Order(702)
        @SuisGroup(FormGroup.RumahPamDanTangkiSedut)
        @SuisField(FormField.CopperTapeSteelWire)
        @Description("Copper Tape/Steel Wire")
        public Suis rumahPamDanTangkiSedutCopperTape;
        @Order(703)
        @SuisGroup(FormGroup.RumahPamDanTangkiSedut)
        @SuisField(FormField.EarthChamber)
        @Description("Earth Chamber")
        public Suis rumahPamDanTangkiSedutEarthChamber;
        @Order(704)
        @SuisGroup(FormGroup.RumahPamDanTangkiSedut)
        @SuisField(FormField.LampuKawasan)
        @Description("Lampu Kawasan")
        public Suis rumahPamDanTangkiSedutLampuKawasan;
        @Order(705)
        @SuisGroup(FormGroup.RumahPamDanTangkiSedut)
        @SuisField(FormField.LampuDalam)
        @Description("Lampu Dalam")
        public Suis rumahPamDanTangkiSedutLampuDalam;
        @Order(706)
        @SuisGroup(FormGroup.RumahPamDanTangkiSedut)
        @SuisField(FormField.SuisSocketOutlet)
        @Description("Suis Socker Outlet")
        public Suis rumahPamDanTangkiSedutSuisSockerOutlet;

        @Order(800)
        public String title_tangkiAir = "3. TANGKI AIR";
        @Order(801)
        @SuisGroup(FormGroup.TangkiAir)
        @SuisField(FormField.Elektrod)
        @Description("Elektrod")
        public Suis tangkiAirElektrod;
        @Order(802)
        @SuisGroup(FormGroup.TangkiAir)
        @SuisField(FormField.CopperTapeSteelWire)
        @Description("Copper Tape/Steel Wire")
        public Suis tangkiAirCopperTape;
        @Order(803)
        @SuisGroup(FormGroup.TangkiAir)
        @SuisField(FormField.EarthChamber)
        @Description("Earth Chamber")
        public Suis tangkiAirEarthChamber;
        @Order(804)
        @SuisGroup(FormGroup.TangkiAir)
        @SuisField(FormField.LampuKawasan)
        @Description("Lampu Kawasan")
        public Suis tangkiAirLampuKawasan;
        @Order(805)
        @SuisGroup(FormGroup.TangkiAir)
        @SuisField(FormField.LampuDalam)
        @Description("Lampu Dalam")
        public Suis tangkiAirLampuDalam;
        @Order(806)
        @SuisGroup(FormGroup.TangkiAir)
        @SuisField(FormField.SuisSocketOutlet)
        @Description("Suis Socket Outlet")
        public Suis tangkiAirSuisSockerOutlet;

    }

    class Suis {
        public Boolean baik;
    }

    class SuisRemarks extends Suis {
        public String remarks;

        @Override
        public String toString() {
            return this.remarks;
        }
    }

    class SuisTarikh extends Suis {
        public Date tarikhKalibrasi;

        @Override
        public String toString() {
            if (tarikhKalibrasi == null)
                return null;
            return new Convert(tarikhKalibrasi).to();
        }
    }

    class SuisAmpere extends Suis {
        public Double nilaiAmpere;

        @Override
        public String toString() {
            if (nilaiAmpere == null)
                return null;
            return new Convert(nilaiAmpere).to();
        }
    }
}
