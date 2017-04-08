package my.com.cans.cansandroid.services;

import java.util.Date;

import my.com.cans.cansandroid.objects.enums.FormField;
import my.com.cans.cansandroid.objects.enums.FormGroup;

/**
 * Created by Rfeng on 04/04/2017.
 */

public class MobileAPIResponse {
    public class KeyResponse extends BaseAPIResponse {
        public KeyResult Result;
    }

    public class KeyResult {
        public String key;
    }

    public class UploadResponse extends BaseAPIResponse {
        public UploadResult Result;
    }

    public class UploadResult {
        public String downloadPath;
    }

    public class FormResponse extends BaseAPIResponse {
        public FormResult Result;
    }

    public class FormResult {
        public String id;
        public Date Tarikh;
        public String NamaRumahPam;
        public String Wilayah;
        public Double PapanSuisUtamaLV;
        public Double PapanSuisUtamaHT;
        public String PapanSuisUtamaCatitan;
        public String GegantiPerlindunganCatitan;
        public String SuisGearCatitan;
        public Double MotorRotorPerperintangNilaiWatts;
        public Double MotorRotorPerperintangNilaiVoltan;
        public Double MotorSangkarTupaiNilaiWatts;
        public Double MotorSangkarTupaiNilaiVoltan;
        public Double VariableSpeedSystemNilaiWatts;
        public Double VariableSpeedSystemNilaiVoltan;
        public FormData[] FormData;
        public String PreparedBy;
        public long _ts;

        public FormData getFormData(FormGroup group, FormField field) {
            for (int i = 0; i < FormData.length; i++) {
                FormData data = FormData[i];
                if (data.GroupID == group.getCode() && data.ColumnID == field.getCode())
                    return data;
            }
            return new FormData();
        }
    }

    public class FormData {
        public int GroupID;
        public int ColumnID;
        public Boolean Baik;
        public String Remarks;
        public Date Tarikh;
        public Double Nilai;
    }
}
