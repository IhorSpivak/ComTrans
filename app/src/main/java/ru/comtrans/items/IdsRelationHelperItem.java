package ru.comtrans.items;

import java.io.Serializable;

/**
 * Created by Leol on 04.04.2017.
 */

public class IdsRelationHelperItem implements Serializable{
    public static final String CODE_GENERAL_TYPE_ID = "general_type_id";
    public static final String CODE_GENERAL_MARK = "general_marka";
    public static final String CODE_GENERAL_MODEL = "general_model";
    public static final String CODE_TEC_ENGINE_MARK = "tec_engine_mark";
    public static final String CODE_TEC_ENGINE_MODEL = "tec_engine_model";

    public static final String CODE_TEC_ENGINE_POWER = "tec_engine_power";
    public static final String CODE_TEC_ENGINE_TYPE = "tec_engine_type";
    public static final String CODE_TEC_ENGINE_VOLUME = "tec_engine_volume";
    public static final String CODE_TEC_ECO_CLASS = "tec_eco_class";
    public static final String CODE_SHAS_CAPACITY_TOTAL = "shas_capacity_total";
    public static final String CODE_SHAS_WHEEL_FORMULA = "shas_wheel_formula";
    public static final String CODE_MARK_KPP = "MARKA_KPP";
    public static final String CODE_MODEL_KPP = "MODEL_KPP";
    public static final String CODE_TEC_KPP_GEARS = "tec_kpp_gears";
    public static final String CODE_TEC_KPP_TYPE = "tec_kpp_type";
    public static final String CODE_VEHICLE_OWNER = "SOBSTVENNIK_CHASTNOE_YUR_LITSO";
    public static final String CODE_FORM_ORGANIZATION = "FORMA_ORGANIZATSII";

    public static final String TYPE_OF_INSPECTION = "VID_STANDARTA_OSMOTRA";

    private String code;
    private long mark;
    private long model;
    private long engineMark;
    private long engineModel;
    private long kppModel;
    private long vehicleOwner;
    private long inspectionType;

    public IdsRelationHelperItem() {
        this.code = "";
        this.mark = -1;
        this.model = -1;
        this.engineMark = -1;
        this.engineModel = -1;
        this.kppModel = -1;
        this.vehicleOwner = -1;
        this.inspectionType = -1;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getMark() {
        return mark;
    }

    public void setMark(long mark) {
        this.mark = mark;
    }

    public long getModel() {
        return model;
    }

    public void setModel(long model) {
        this.model = model;
    }

    public long getEngineMark() {
        return engineMark;
    }

    public void setEngineMark(long engineMark) {
        this.engineMark = engineMark;
    }

    public long getEngineModel() {
        return engineModel;
    }

    public void setEngineModel(long engineModel) {
        this.engineModel = engineModel;
    }

    public long getKppModel() {
        return kppModel;
    }

    public void setKppModel(long kppModel) {
        this.kppModel = kppModel;
    }

    public long getVehicleOwner() {
        return vehicleOwner;
    }

    public void setVehicleOwner(long vehicleOwner) {
        this.vehicleOwner = vehicleOwner;
    }
    public long getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(long inspectionType) {
        this.inspectionType = inspectionType;
    }
}
