package retrofit.response.queryType;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QueryTypeDetails {

    @SerializedName("issueID")
    @Expose
    private String issueID;
    @SerializedName("IssueCode")
    @Expose
    private String issueCode;
    @SerializedName("Issue")
    @Expose
    private String issue;

    public String getIssueID() {
        return issueID;
    }

    public void setIssueID(String issueID) {
        this.issueID = issueID;
    }

    public String getIssueCode() {
        return issueCode;
    }

    public void setIssueCode(String issueCode) {
        this.issueCode = issueCode;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }
}
