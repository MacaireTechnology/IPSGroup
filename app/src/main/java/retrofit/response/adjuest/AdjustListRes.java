package retrofit.response.adjuest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdjustListRes {

    @SerializedName("EmployeeID")
    @Expose
    private String employeeID;
    @SerializedName("EmpFullName")
    @Expose
    private String empFullName;
    @SerializedName("AdjAttClockIn")
    @Expose
    private String adjAttClockIn;
    @SerializedName("AdjAttClockOut")
    @Expose
    private String adjAttClockOut;
    @SerializedName("Comment")
    @Expose
    private String comment;
    @SerializedName("ReqDate")
    @Expose
    private String reqDate;
    @SerializedName("RecID")
    @Expose
    private String recID;
    @SerializedName("ApprovalRemark")
    @Expose
    private String approvalRemark;
    @SerializedName("AttStatus")
    @Expose
    private String attStatus;
    @SerializedName("InTime")
    @Expose
    private String inTime;
    @SerializedName("OutTime")
    @Expose
    private String outTime;

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getEmpFullName() {
        return empFullName;
    }

    public void setEmpFullName(String empFullName) {
        this.empFullName = empFullName;
    }

    public String getAdjAttClockIn() {
        return adjAttClockIn;
    }

    public void setAdjAttClockIn(String adjAttClockIn) {
        this.adjAttClockIn = adjAttClockIn;
    }

    public String getAdjAttClockOut() {
        return adjAttClockOut;
    }

    public void setAdjAttClockOut(String adjAttClockOut) {
        this.adjAttClockOut = adjAttClockOut;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReqDate() {
        return reqDate;
    }

    public void setReqDate(String reqDate) {
        this.reqDate = reqDate;
    }

    public String getRecID() {
        return recID;
    }

    public void setRecID(String recID) {
        this.recID = recID;
    }

    public String getApprovalRemark() {
        return approvalRemark;
    }

    public void setApprovalRemark(String approvalRemark) {
        this.approvalRemark = approvalRemark;
    }

    public String getAttStatus() {
        return attStatus;
    }

    public void setAttStatus(String attStatus) {
        this.attStatus = attStatus;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }
    
}
