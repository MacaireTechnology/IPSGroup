package retrofit.response.admin;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IssueTrackerDetails {

    @SerializedName("CreateDate")
    @Expose
    private String createDate;
    @SerializedName("QueryType")
    @Expose
    private String queryType;
    @SerializedName("department")
    @Expose
    private String department;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Description")
    @Expose
    private String description;

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
