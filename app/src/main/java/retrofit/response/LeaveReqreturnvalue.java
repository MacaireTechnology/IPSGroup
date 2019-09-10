package retrofit.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LeaveReqreturnvalue {

    @SerializedName("ReqDate")
    @Expose
    private String reqDate;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("FrmDT")
    @Expose
    private String frmDT;
    @SerializedName("ToDT")
    @Expose
    private String toDT;
    @SerializedName("Days")
    @Expose
    private String days;
    @SerializedName("Cmt")
    @Expose
    private String cmt;
    @SerializedName("Description")
    @Expose
    private String description;

    public String getReqDate() {
        return reqDate;
    }

    public void setReqDate(String reqDate) {
        this.reqDate = reqDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFrmDT() {
        return frmDT;
    }

    public void setFrmDT(String frmDT) {
        this.frmDT = frmDT;
    }

    public String getToDT() {
        return toDT;
    }

    public void setToDT(String toDT) {
        this.toDT = toDT;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getCmt() {
        return cmt;
    }

    public void setCmt(String cmt) {
        this.cmt = cmt;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
