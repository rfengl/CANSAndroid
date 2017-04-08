package my.com.cans.cansandroid.objects.enums;

/**
 * Created by Rfeng on 08/04/2017.
 */

public enum FormGroup {
    PapanSuisUtama("Papan Suis Utama", 1),
    GegantiPerlindungan("Geganti Perlindungan", 2),
    SuisGear("Suis Gear", 3),
    MotorRotorBerperintang("Motor Rotor Berperintang", 4),
    MotorSangkarTupai("Motor Sangkar Tupai", 5),
    VariableSpeedSystem("Variable Speed System (VSD)", 6),
    AutoSystem("Auto System", 7),
    RumahPamDanTangkiSedut("Rumah Pam Dan Tangki Sedut", 8),
    TangkiAir("Tangki Air", 9);

    String name;
    int code;

    public static FormGroup getEnum(int code) {
        for (FormGroup item : FormGroup.values()) {
            if (item.code == code)
                return item;
        }
        return null;
    }

    FormGroup(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }
}
