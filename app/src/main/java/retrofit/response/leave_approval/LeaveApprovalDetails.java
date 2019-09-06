package retrofit.response.leave_approval;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LeaveApprovalDetails {

    @SerializedName("EmployeeID")
    @Expose
    private String employeeID;
    @SerializedName("EmpFullName")
    @Expose
    private String empFullName;
    @SerializedName("Comments")
    @Expose
    private String comments;
    @SerializedName("FromDate")
    @Expose
    private String fromDate;
    @SerializedName("ToDate")
    @Expose
    private String toDate;
    @SerializedName("IsApproved")
    @Expose
    private String isApproved;
    @SerializedName("LeaveID")
    @Expose
    private String leaveID;
    @SerializedName("LeaveReqDateTime")
    @Expose
    private String leaveReqDateTime;
    @SerializedName("LeaveType")
    @Expose
    private String leaveType;
    @SerializedName("NoOfWorkingDay")
    @Expose
    private String noOfWorkingDay;
    @SerializedName("ApprovalComments")
    @Expose
    private String approvalComments;

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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(String isApproved) {
        this.isApproved = isApproved;
    }

    public String getLeaveID() {
        return leaveID;
    }

    public void setLeaveID(String leaveID) {
        this.leaveID = leaveID;
    }

    public String getLeaveReqDateTime() {
        return leaveReqDateTime;
    }

    public void setLeaveReqDateTime(String leaveReqDateTime) {
        this.leaveReqDateTime = leaveReqDateTime;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getNoOfWorkingDay() {
        return noOfWorkingDay;
    }

    public void setNoOfWorkingDay(String noOfWorkingDay) {
        this.noOfWorkingDay = noOfWorkingDay;
    }

    public String getApprovalComments() {
        return approvalComments;
    }

    public void setApprovalComments(String approvalComments) {
        this.approvalComments = approvalComments;
    }
}
