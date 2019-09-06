package retrofit.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Trackreturnvalue {
    @SerializedName("EmpId")
    @Expose
    private String empId;
    @SerializedName("EmpFullName")
    @Expose
    private String empFullName;
    @SerializedName("MobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("PrimaryEmail")
    @Expose
    private String primaryEmail;
    @SerializedName("Lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("GPSAddress")
    @Expose
    private String gPSAddress;

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
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

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getGPSAddress() {
        return gPSAddress;
    }

    public void setGPSAddress(String gPSAddress) {
        this.gPSAddress = gPSAddress;
    }
}
