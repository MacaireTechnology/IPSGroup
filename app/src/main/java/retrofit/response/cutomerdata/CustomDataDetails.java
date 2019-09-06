package retrofit.response.cutomerdata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomDataDetails {

    @SerializedName("CustID")
    @Expose
    private String custID;
    @SerializedName("CustName")
    @Expose
    private String custName;
    @SerializedName("CustMobileNo")
    @Expose
    private String custMobileNo;
    @SerializedName("CustEmaiI_ID")
    @Expose
    private String custEmaiIID;
    @SerializedName("ContactPerson")
    @Expose
    private String contactPerson;
    @SerializedName("OutletType")
    @Expose
    private String outletType;
    @SerializedName("LandMark")
    @Expose
    private String landMark;
    @SerializedName("Address")
    @Expose
    private String address;
    @SerializedName("UserDefined1")
    @Expose
    private String userDefined1;
    @SerializedName("UserDefined2")
    @Expose
    private String userDefined2;

    public String getCustID() {
        return custID;
    }

    public void setCustID(String custID) {
        this.custID = custID;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustMobileNo() {
        return custMobileNo;
    }

    public void setCustMobileNo(String custMobileNo) {
        this.custMobileNo = custMobileNo;
    }

    public String getCustEmaiIID() {
        return custEmaiIID;
    }

    public void setCustEmaiIID(String custEmaiIID) {
        this.custEmaiIID = custEmaiIID;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getOutletType() {
        return outletType;
    }

    public void setOutletType(String outletType) {
        this.outletType = outletType;
    }

    public String getLandMark() {
        return landMark;
    }

    public void setLandMark(String landMark) {
        this.landMark = landMark;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserDefined1() {
        return userDefined1;
    }

    public void setUserDefined1(String userDefined1) {
        this.userDefined1 = userDefined1;
    }

    public String getUserDefined2() {
        return userDefined2;
    }

    public void setUserDefined2(String userDefined2) {
        this.userDefined2 = userDefined2;
    }

}
