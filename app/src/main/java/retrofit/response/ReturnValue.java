package retrofit.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReturnValue {

    @SerializedName("EmpID")
    @Expose
    private String empID;
    @SerializedName("ClockInDateTime")
    @Expose
    private String clockInDateTime;
    @SerializedName("ClockInIMG")
    @Expose
    private String clockInIMG;
    @SerializedName("ClockOutDateTime")
    @Expose
    private String clockOutDateTime;
    @SerializedName("ClockOutIMG")
    @Expose
    private String clockOutIMG;
    @SerializedName("ClockInDT")
    @Expose
    private String clockInDT;
    @SerializedName("ClockOutDT")
    @Expose
    private String clockOutDT;
    @SerializedName("ClockInLocaction")
    @Expose
    private String clockInLocaction;
    @SerializedName("ClockOutLocation")
    @Expose
    private String clockOutLocation;
    @SerializedName("AttID")
    @Expose
    private String attID;

    public String getEmpID() {
        return empID;
    }

    public void setEmpID(String empID) {
        this.empID = empID;
    }

    public String getClockInDateTime() {
        return clockInDateTime;
    }

    public void setClockInDateTime(String clockInDateTime) {
        this.clockInDateTime = clockInDateTime;
    }

    public String getClockInIMG() {
        return clockInIMG;
    }

    public void setClockInIMG(String clockInIMG) {
        this.clockInIMG = clockInIMG;
    }

    public String getClockOutDateTime() {
        return clockOutDateTime;
    }

    public void setClockOutDateTime(String clockOutDateTime) {
        this.clockOutDateTime = clockOutDateTime;
    }

    public String getClockOutIMG() {
        return clockOutIMG;
    }

    public void setClockOutIMG(String clockOutIMG) {
        this.clockOutIMG = clockOutIMG;
    }

    public String getClockInDT() {
        return clockInDT;
    }

    public void setClockInDT(String clockInDT) {
        this.clockInDT = clockInDT;
    }

    public String getClockOutDT() {
        return clockOutDT;
    }

    public void setClockOutDT(String clockOutDT) {
        this.clockOutDT = clockOutDT;
    }

    public String getClockInLocaction() {
        return clockInLocaction;
    }

    public void setClockInLocaction(String clockInLocaction) {
        this.clockInLocaction = clockInLocaction;
    }

    public String getClockOutLocation() {
        return clockOutLocation;
    }

    public void setClockOutLocation(String clockOutLocation) {
        this.clockOutLocation = clockOutLocation;
    }

    public String getAttID() {
        return attID;
    }

    public void setAttID(String attID) {
        this.attID = attID;
    }
}