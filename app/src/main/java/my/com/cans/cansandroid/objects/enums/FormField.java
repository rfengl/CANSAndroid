package my.com.cans.cansandroid.objects.enums;

/**
 * Created by Rfeng on 06/04/2017.
 */

public enum FormField {
    Voltmeter("Voltmeter", 1),
    Ammeter("Ammeter", 2), PowerFactorMeter("Power Factor Meter", 3),
    LampuPenunjuk("Lampu Penunjuk", 4),
    GetahLantai("Getah Lantai", 5),
    LukisanSkematik("Lukisan Skematik", 6),
    ParasMinyak("Paras Minyak", 7),
    SilicaGel("Silica Gel", 8),
    MeterSuhu("Meter Suhu", 9),
    SistemPembumian("Sistem Pembumian", 10),
    Overcurrent("Overcurrent", 11),
    EarthFault("Earth Fault", 12),
    AirCircuitBreaker("Air Circuit Breaker", 13),
    MCCB("MCCB", 14),
    FiusSuis("Fius Suis", 15),
    SuisFuis("Suis Fuis", 16),
    MotorNo1("Motor No. 1", 17),
    MotorNo2("Motor No. 2", 18),
    MotorNo3("Motor No. 3", 19),
    MotorNo4("Motor No. 4", 20),
    SlipRing1("Slip Ring 1", 21),
    SlipRing2("Slip Ring 2", 22),
    SlipRing3("Slip Ring 3", 23),
    SlipRing4("Slip Ring 4", 24),
    CarbonBrush1("Carbon Brush 1", 25),
    CarbonBrush2("Carbon Brush 2", 26),
    CarbonBrush3("Carbon Brush 3", 27),
    CarbonBrush4("Carbon Brush 4", 28),
    Bearing1("Bearing 1", 29),
    Bearing2("Bearing 2", 30),
    Bearing3("Bearing 3", 31),
    Bearing4("Bearing 4", 32),
    Penjajaran1("Penjajaran 1", 33),
    Penjajaran2("Penjajaran 2", 34),
    Penjajaran3("Penjajaran 3", 35),
    Penjajaran4("Penjajaran 4", 36),
    SambunganKabel1("Sambungan Kabel 1", 37),
    SambunganKabel2("Sambungan Kabel 2", 38),
    SambunganKabel3("Sambungan Kabel 3", 39),
    SambunganKabel4("Sambungan Kabel 4", 40),
    StarterPanelBoard1("Starter Panel Board 1", 41),
    StarterPanelBoard2("Starter Panel Board 2", 42),
    StarterPanelBoard3("Starter Panel Board 3", 43),
    StarterPanelBoard4("Starter Panel Board 4", 44),
    AutoNo1("Auto No. 1", 45),
    AutoNo3("Auto No. 3", 46),
    ELPRO("ELPRO", 47),
    Timer("Timer", 48),
    Manual("Manual", 49),
    Elektrod("Elektrod", 50),
    CopperTapeSteelWire("Copper Tape/Steel Wire", 51),
    EarthChamber("Earth Chamber", 52),
    LampuKawasan("Lampu Kawasan", 53),
    LampuDalam("Lampu Dalam", 54),
    SuisSocketOutlet("Suis Socket Outlet", 55);

    String name;
    int code;

    public static FormField getEnum(int code) {
        for (FormField item : FormField.values()) {
            if (item.code == code)
                return item;
        }
        return null;
    }

    FormField(String name, int code) {
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
