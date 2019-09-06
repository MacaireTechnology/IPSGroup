package retrofit.response.team_attend;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TeamAttendDetail {

    @SerializedName("Empid")
    @Expose
    private String empid;
    @SerializedName("EmpFullName")
    @Expose
    private String empFullName;
    @SerializedName("MobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("ClockInDT")
    @Expose
    private String clockInDT;
    @SerializedName("ClockInLocaction")
    @Expose
    private String clockInLocaction;
    @SerializedName("ClockOutDT")
    @Expose
    private String clockOutDT;
    @SerializedName("ClockOutLocation")
    @Expose
    private String clockOutLocation;

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public String getEmpFullName() {
        return empFullName;
    }

    public void setEmpFullName(String empFullName) {
        this.empFullName = empFullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getClockInDT() {
        return clockInDT;
    }

    public void setClockInDT(String clockInDT) {
        this.clockInDT = clockInDT;
    }

    public String getClockInLocaction() {
        return clockInLocaction;
    }

    public void setClockInLocaction(String clockInLocaction) {
        this.clockInLocaction = clockInLocaction;
    }

    public String getClockOutDT() {
        return clockOutDT;
    }

    public void setClockOutDT(String clockOutDT) {
        this.clockOutDT = clockOutDT;
    }

    public String getClockOutLocation() {
        return clockOutLocation;
    }

    public void setClockOutLocation(String clockOutLocation) {
        this.clockOutLocation = clockOutLocation;
    }
}
