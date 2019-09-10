package retrofit.response.issueApproval;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IssueApprovalListDetails {

    @SerializedName("RptManagerEmpID")
    @Expose
    private String rptManagerEmpID;
    @SerializedName("IssueTrackerID")
    @Expose
    private String issueTrackerID;
    @SerializedName("IssueCreatedEmpId")
    @Expose
    private String issueCreatedEmpId;
    @SerializedName("CreateDate")
    @Expose
    private String createDate;
    @SerializedName("Department")
    @Expose
    private String department;
    @SerializedName("StatusID")
    @Expose
    private String statusID;
    @SerializedName("EmpFullName")
    @Expose
    private String empFullName;
    @SerializedName("Issue")
    @Expose
    private String issue;
    @SerializedName("Description")
    @Expose
    private String description;

    public String getRptManagerEmpID() {
        return rptManagerEmpID;
    }

    public void setRptManagerEmpID(String rptManagerEmpID) {
        this.rptManagerEmpID = rptManagerEmpID;
    }

    public String getIssueTrackerID() {
        return issueTrackerID;
    }

    public void setIssueTrackerID(String issueTrackerID) {
        this.issueTrackerID = issueTrackerID;
    }

    public String getIssueCreatedEmpId() {
        return issueCreatedEmpId;
    }

    public void setIssueCreatedEmpId(String issueCreatedEmpId) {
        this.issueCreatedEmpId = issueCreatedEmpId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStatusID() {
        return statusID;
    }

    public void setStatusID(String statusID) {
        this.statusID = statusID;
    }

    public String getEmpFullName() {
        return empFullName;
    }

    public void setEmpFullName(String empFullName) {
        this.empFullName = empFullName;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
