package retrofit.response.issueApproval;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IssueApprovalListRes {

    @SerializedName("ReturnValue")
    @Expose
    private List<IssueApprovalListDetails> returnValue = null;
    @SerializedName("StatusCode")
    @Expose
    private String statusCode;
    @SerializedName("StatusDescription")
    @Expose
    private String statusDescription;

    public List<IssueApprovalListDetails> getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(List<IssueApprovalListDetails> returnValue) {
        this.returnValue = returnValue;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }
}
