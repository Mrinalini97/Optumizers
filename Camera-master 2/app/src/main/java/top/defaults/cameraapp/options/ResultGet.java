package top.defaults.cameraapp.options;

import com.google.gson.annotations.SerializedName;

public class ResultGet {

    private int fuel_consumption_percent_drop;
    private Integer Tyre_pressure_class;

    @SerializedName("body")
    private String text;
    public ResultGet(int fuel_consumption_percent_drop , Integer tyre_pressure_class) {
        this.fuel_consumption_percent_drop = fuel_consumption_percent_drop;
        this.Tyre_pressure_class = tyre_pressure_class;

    }
    public int getFuel_consumption_percent_drop() {
        return fuel_consumption_percent_drop;
    }
    public int getTyre_pressure_class() {
        return Tyre_pressure_class;
    }

}
