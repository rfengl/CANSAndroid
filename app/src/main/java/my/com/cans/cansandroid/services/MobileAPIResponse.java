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

    public class IdResult {
        public String ID;
    }

    public class VerifyResponse extends BaseAPIResponse {
        public VerifyResult Result;
    }

    public class VerifyResult {
        public String Version;
        public String Message;
        public Date Date;
    }

    public class CoordinateResult {
        public String ID;
        public Double Latitude;
        public Double Longitude;
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

    public class FormsResponse extends BaseAPIResponse {
        public FormResult[] Result;
    }

    public class FormResult {
        public String ID;
        public String DeviceID;
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
        public Date CreatedDate;
//        public long _ts;
//        public Date getCreatedDate(){
//            return new Date(_ts * 1000);
//        }

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

    public class ReportResponse extends BaseAPIResponse {
        public ReportResult Result;
    }

    public class ReportsResponse extends BaseAPIResponse {
        public ReportResult[] Result;
    }

    public class ReportResult {
        public String ID;
        public String DeviceID;
        public String Lokasi;
        public String Kawasan;
        public Date TarikhMula;
        public Date TarikhTamat;
        public String BreakdownDetails;
        public String RootCaused;
        public String SystemBreakdownType;
        public String EquipmentName;
        public String SeverityOfAffectedProcess;
        public String SitePreventionActionTaken;
        public String ReportedBy;
        public String ActionToBeTaken;
        public Boolean KeyInDataSystem;
        public Boolean EDOApproval;
        public Boolean NotificationCPPBD;
        public Boolean MWOIssued;
        public Boolean WorkCompletion;
        public Boolean Others;
        public Boolean Selesai;
        public Date CreatedDate;
    }

    public class GetDevicesResponse extends  BaseAPIResponse {
        public GetDevicesResult[] Result;
    }

    public class GetDevicesResult {
        public String ID;
        public String DeviceID;
        public String Kawasan;
        public String Lokasi;
        public String AdditionalInfo;
    }

    public class GetDeviceValueResponse extends  BaseAPIResponse {
        public GetDeviceValueResult Result;
    }

    public class GetDeviceValueResult {
        public String ID;
        public String DeviceID;
        public Double VoltageL1;
        public Double VoltageL2;
        public Double VoltageL3;
        public Double CurrentP1;
        public Double CurrentP2;
        public Double CurrentP3;
        public Double ActivePower;
        public Double PowerFactor;
        public String PanelDoorStatus;
        public String PumpRunStatus;
    }
}
